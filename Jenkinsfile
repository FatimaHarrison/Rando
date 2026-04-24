pipeline {
    agent any

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    parameters {
        string(name: 'ROLLBACK_VERSION', defaultValue: '', description: 'Docker image tag to roll back to (e.g. 42)')
        booleanParam(name: 'ROLLBACK', defaultValue: false, description: 'Check to perform a rollback instead of a new deployment')
    }

    environment {
        APP_NAME   = 'myapp'
        REGISTRY   = 'registry.example.com'
        STAGING    = 'ubuntu@staging-server'
        PROD       = 'ubuntu@prod-server'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                git branch: 'main', url: 'https://github.com/your-org/your-repo.git'
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

        stage('Build & Push Docker Image') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Building Docker image..."
                sh """
          docker build -t ${APP_NAME}:${DOCKER_TAG} .
          docker tag ${APP_NAME}:${DOCKER_TAG} ${REGISTRY}/${APP_NAME}:${DOCKER_TAG}
          docker push ${REGISTRY}/${APP_NAME}:${DOCKER_TAG}
        """
            }
        }

        stage('Deploy to Staging') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Deploying to STAGING..."
                sh """
          ssh ${STAGING} 'docker pull ${REGISTRY}/${APP_NAME}:${DOCKER_TAG}'
          ssh ${STAGING} 'docker stop ${APP_NAME} || true'
          ssh ${STAGING} 'docker rm ${APP_NAME} || true'
          ssh ${STAGING} 'docker run -d --name ${APP_NAME} -p 9180:9180 ${REGISTRY}/${APP_NAME}:${DOCKER_TAG}'
        """
            }
        }

        stage('Staging Smoke Tests') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Running smoke tests against STAGING..."
                sh """
          curl -f http://staging-server:9180/health
        """
            }
        }

        stage('Approve Production Deployment') {
            when { expression { !params.ROLLBACK } }
            steps {
                timeout(time: 30, unit: 'MINUTES') {
                    input message: "Promote build ${DOCKER_TAG} to PRODUCTION?"
                }
            }
        }

        stage('Deploy to Production') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Deploying to PRODUCTION..."
                sh """
          ssh ${PROD} 'docker pull ${REGISTRY}/${APP_NAME}:${DOCKER_TAG}'
          ssh ${PROD} 'docker stop ${APP_NAME} || true'
          ssh ${PROD} 'docker rm ${APP_NAME} || true'
          ssh ${PROD} 'docker run -d --name ${APP_NAME} -p 9180:9180 ${REGISTRY}/${APP_NAME}:${DOCKER_TAG}'
        """
            }
        }

        stage('Production Health Check') {
            when { expression { !params.ROLLBACK } }
            steps {
                echo "Checking PRODUCTION health..."
                sh """
          curl -f http://prod-server:9180/health
        """
            }
        }

        // --- ROLLBACK PATH ---

        stage('Rollback to Previous Version') {
            when { expression { params.ROLLBACK && params.ROLLBACK_VERSION?.trim() } }
            steps {
                echo "Rolling back PRODUCTION to version: ${params.ROLLBACK_VERSION}"
                sh """
          ssh ${PROD} 'docker pull ${REGISTRY}/${APP_NAME}:${ROLLBACK_VERSION}'
          ssh ${PROD} 'docker stop ${APP_NAME} || true'
          ssh ${PROD} 'docker rm ${APP_NAME} || true'
          ssh ${PROD} 'docker run -d --name ${APP_NAME} -p 9180:9180 ${REGISTRY}/${APP_NAME}:${ROLLBACK_VERSION}'
        """
            }
        }

        stage('Post-Rollback Health Check') {
            when { expression { params.ROLLBACK && params.ROLLBACK_VERSION?.trim() } }
            steps {
                echo "Checking PRODUCTION health after rollback..."
                sh """
          curl -f http://prod-server:9180/health
        """
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully."
        }
        failure {
            echo "Pipeline failed. Check logs and consider triggering a rollback with a known good version."
        }
        always {
            echo "Build finished. See console output and test reports for details."
        }
    }
}
