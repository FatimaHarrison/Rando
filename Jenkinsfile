pipeline {

    agent any   // Run on any available Jenkins agent (Windows host)

    options {
        timestamps()   // Add timestamps to console logs
        buildDiscarder(logRotator(numToKeepStr: '20'))   // Keep last 20 builds
    }
    parameters {
        // Commit hash used when performing a rollback
        string(name: 'ROLLBACK_VERSION', defaultValue: '', description: 'Git commit hash to roll back to')

        // Boolean flag to switch pipeline into rollback mode
        booleanParam(name: 'ROLLBACK', defaultValue: false, description: 'Perform rollback instead of new deployment')
    }
    environment {
        // Local WSL deployment paths (NOT remote SSH)
        STAGING_PATH = '/var/www/staging'
        PROD_PATH    = '/var/www/production'
    }
    stages {
        //CHECKOUT Source CODE

        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                git branch: 'main', url: 'https://github.com/FatimaHarrison/Rando.git'
            }
        }
        // BUILD APPLICATION (SKIPPED DURING ROLLBACK)
        stage('Build') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Building application with Maven..."
                sh 'mvn clean package -DskipTests=false'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'   // Publish test results
                }
            }
        }
        //STATIC ANALYSIS SKIPPED DURING ROLLBACK
        stage('Static Analysis') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Running static analysis..."
                sh 'mvn -q -DskipTests=true verify'
            }
        }
        //DEPLOY STAGING
        stage('Deploy to Staging') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Deploying build to STAGING environment..."
                // Copy build artifacts into WSL staging folder
                sh """
                    cp -r target/* /mnt/c/Users/tutor/ubuntu${STAGING_PATH}/
                """
            }
        }
        //STAGING SMOKE TESTS

        stage('Staging Smoke Tests') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Running smoke tests against STAGING..."

                // Hit local staging endpoint
                sh "curl -f http://localhost:8081/health"
            }
        }
        //MANUAL APPROVAL BEFORE PRODUCTION
        stage('Approve Production Deployment') {
            when { expression { !params.ROLLBACK } }
            steps {
                timeout(time: 30, unit: 'MINUTES') {
                    input message: "Promote build to PRODUCTION?"
                }
            }
        }
        //DEPLOY TO PRODUCTION (LOCAL WSL COPY)
        stage('Deploy to Production') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Deploying build to PRODUCTION environment..."

                // Copy build artifacts into WSL production folder
                sh """
                    cp -r target/* /mnt/c/Users/tutor/ubuntu${PROD_PATH}/
                """
            }
        }
        //PRODUCTION HEALTH CHECK
        stage('Production Health Check') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Checking PRODUCTION health..."
                sh "curl -f http://localhost:8080/health"
            }
        }
        //ROLLBACK EXECUTION PATH
        stage('Rollback') {
            when { expression { params.ROLLBACK && params.ROLLBACK_VERSION?.trim() } }
            steps {
                echo "Rolling back to commit: ${params.ROLLBACK_VERSION}"
                // Checkout specific commit and rebuild artifact
                sh """
                    git fetch
                    git checkout ${params.ROLLBACK_VERSION}
                    mvn clean package -DskipTests=true
                """
                //Copy rolled-back artifact into production
                sh """
                    cp -r target/* /mnt/c/Users/tutor/ubuntu${PROD_PATH}/
                """
            }
        }
        //POST-ROLLBACK HEALTH CHECK
        stage('Post-Rollback Health Check') {
            when { expression { params.ROLLBACK && params.ROLLBACK_VERSION?.trim() } }
            steps {
                echo "Checking PRODUCTION health after rollback..."
                sh "curl -f http://localhost:8080/health"
            }
        }
    }
    //POST-BUILD ACTIONS
    post {
        success {
            echo "Pipeline completed successfully."
        }
        failure {
            echo "Pipeline failed. Check logs and consider triggering a rollback."
        }
        always {
            echo "Build finished."
        }
    }
}
