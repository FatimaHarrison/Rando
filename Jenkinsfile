pipeline {
    agent any

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    parameters {
        string(name: 'ROLLBACK_VERSION', defaultValue: '', description: 'Git commit hash to roll back to')
        booleanParam(name: 'ROLLBACK', defaultValue: false, description: 'Perform rollback instead of new deployment')
    }

    environment {
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
                git branch: 'main', url: 'https://github.com/FatimaHarrison/Rando.git'
            }
        }

        stage('Test SSH') {
            steps {
                echo "Testing SSH connection to WSL..."
                sshagent(['wsl-ssh']) {
                    sh 'ssh -p 6786 misst-1@172.17.0.1 "echo connected"'
                }
            }
        }

        stage('Build') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Building application..."
                sh 'mvn clean package -DskipTests=false'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Static Analysis') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Running static analysis..."
                sh 'mvn -q -DskipTests=true verify'
            }
        }

        stage('Deploy to Staging') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Deploying to STAGING..."
                sshagent(['wsl-ssh']) {
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
                sh "curl -f http://${WSL_HOST}:8081/health"
            }
        }

        stage('Approve Production Deployment') {
            when { expression { !params.ROLLBACK } }
            steps {
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
                sh "curl -f http://${WSL_HOST}:8080/health"
            }
        }

        // --- ROLLBACK PATH ---
        stage('Rollback') {
            when { expression { params.ROLLBACK && params.ROLLBACK_VERSION?.trim() } }
            steps {
                echo "Rolling back to commit: ${params.ROLLBACK_VERSION}"
                sshagent(['wsl-ssh']) {
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
                sh "curl -f http://${WSL_HOST}:8080/health"
            }
        }
    }

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
