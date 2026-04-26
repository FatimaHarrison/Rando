pipeline {

    agent any //Any available Jenkins agent to run the pipeline
    options {
        timestamps() // Adding timestamps to console output for easier debugging
        buildDiscarder(logRotator(numToKeepStr: '20'))   // Keep only the last 20 builds
    }
    parameters {
        // Commit hash used when performing a rollback
        string(name: 'ROLLBACK_VERSION', defaultValue: '', description: 'Git commit hash to roll back to')

        // Boolean flag to switch pipeline into rollback mode
        booleanParam(name: 'ROLLBACK', defaultValue: false, description: 'Perform rollback instead of new deployment')
    }
    environment {
        // SSH credentials and deployment paths for WSL environment
        WSL_USER = 'misst-1'
        WSL_HOST = '172.17.0.1'
        SSH_PORT = '6786'
        STAGING_PATH = '/var/www/staging'
        PROD_PATH = '/var/www/production'
    }
    stages {
        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                // Pull latest code from GitHub main branch
                git branch: 'main', url: 'https://github.com/FatimaHarrison/Rando.git'
            }
        }
        stage('Test SSH') {
            steps {
                echo "Testing SSH connection to WSL..."
                // Use Jenkins credential 'wsl-ssh' to test remote connectivity
                sshagent(['wsl-ssh']) {
                    sh 'ssh -p 6786 misst-1@172.17.0.1 "echo connected"'
                }
            }
        }
        stage('Build') {
            when { expression { !params.ROLLBACK } }   // Skip build if rollback mode is enabled
            steps {
                echo "Building application..."
                // Run Maven clean + package, including tests
                sh 'mvn clean package -DskipTests=false'
            }
            post {
                always {
                    // Publish JUnit test results even if build fails
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Static Analysis') {
            when { expression { !params.ROLLBACK } }   // Skip static analysis during rollback
            steps {
                echo "Running static analysis..."
                // Run Maven verify phase (includes static code checks)
                sh 'mvn -q -DskipTests=true verify'
            }
        }
        stage('Deploy to Staging') {
            when { expression { !params.ROLLBACK } }   // Only deploy if not rolling back
            steps {
                echo "Deploying to STAGING..."
                sshagent(['wsl-ssh']) {
                    // SSH into WSL, pull latest code, rebuild and restart Docker containers
                    sh """
                        ssh -p ${SSH_PORT} ${WSL_USER}@${WSL_HOST} \\
                        "cd ${STAGING_PATH} && git pull && docker compose up -d --build"
                    """
                }
            }
        }
        stage('Staging Smoke Tests') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Running smoke tests against STAGING..."
                // Hit staging health endpoint to verify deployment
                sh "https://${WSL_HOST}:8081/health"
            }
        }
        stage('Approve Production Deployment') {
            when { expression { !params.ROLLBACK } }
            steps {
                // Manual approval gate before production deployment
                timeout(time: 30, unit: 'MINUTES') {
                    input message: "Promote build to PRODUCTION?"
                }
            }
        }
        stage('Deploy to Production') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Deploying to PRODUCTION..."
                sshagent(['wsl-ssh']) {
                    // Pull latest code and rebuild production Docker containers
                    sh """
                        ssh -p ${SSH_PORT} ${WSL_USER}@${WSL_HOST} \\
                        "cd ${PROD_PATH} && git pull && docker compose up -d --build"
                    """
                }
            }
        }
        stage('Production Health Check') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Checking PRODUCTION health..."
                // Hit production health endpoint
                sh "https://${WSL_HOST}:8080/health"
            }
        }
        // ROLLBACK EXECUTION PATH
        stage('Rollback') {
            when { expression { params.ROLLBACK && params.ROLLBACK_VERSION?.trim() } }
            steps {
                echo "Rolling back to commit: ${params.ROLLBACK_VERSION}"
                sshagent(['wsl-ssh']) {
                    // Checkout specific commit and rebuild production environment
                    sh """
                        ssh -p ${SSH_PORT} ${WSL_USER}@${WSL_HOST} \\
                        "cd ${PROD_PATH} && git fetch && git checkout ${params.ROLLBACK_VERSION} && docker compose up -d --build"
                    """
                }
            }
        }
        stage('Post-Rollback Health Check') {
            when { expression { params.ROLLBACK && params.ROLLBACK_VERSION?.trim() } }
            steps {
                echo "Checking PRODUCTION health after rollback..."
                // Verify production is healthy after rollback
                sh "https://${WSL_HOST}:8080/health"
            }
        }
    }
    post {
        success {
            echo "Pipeline completed successfully."// Final success message
        }
        failure {
            echo "Pipeline failed. Check logs and consider triggering a rollback."// Failure message
        }
        always {
            echo "Build finished."// Always runs regardless of outcome
        }
    }
}
