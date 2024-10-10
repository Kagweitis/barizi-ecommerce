pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'georgekagwe'
        DOCKER_IMAGE = "${DOCKER_REGISTRY}/barizi"
        DOCKER_TAG = "latest"
        DOCKER_FILE = 'Dockerfile'
        APP_NAME = 'barizi-app'
        PORT = '8082'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Kagweitis/barizi-ecommerce.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests' // Skipping tests for faster builds
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def image = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", 'dockerhub_credentials') {
                        sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
               script {
               sh "docker stop ${APP_NAME} || true"
               sh "docker rm ${APP_NAME} || true"
               sh "docker run -d --name ${APP_NAME} -p ${PORT}:${PORT} --restart unless-stopped ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
               }
            }
        }
    }

    post {
        success {
            echo 'Build and deployment successful!'
        }
        failure {
            echo 'Build or deployment failed!'
        }
    }
}
