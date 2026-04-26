def call(Map config = [:]) {

    pipeline {
        agent any

        environment {
            APP_NAME = config.appName
            REGISTRY = config.registry
        }

        stages {

            stage('Checkout') {
                steps {
                    checkout scm
                }
            }

            stage('Build') {
                steps {
                    sh "echo Building ${APP_NAME}"
                    sh "mvn clean package || true"
                }
            }

            stage('Docker Build') {
                steps {
                    sh """
                    docker build -t ${REGISTRY}/${APP_NAME}:${BUILD_NUMBER} .
                    """
                }
            }

            stage('Push Image') {
                steps {
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'USER',
                        passwordVariable: 'PASS'
                    )]) {
                        sh """
                        echo $PASS | docker login -u $USER --password-stdin
                        docker push ${REGISTRY}/${APP_NAME}:${BUILD_NUMBER}
                        """
                    }
                }
            }
        }
    }
}