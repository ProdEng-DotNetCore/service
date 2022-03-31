pipeline {
    agent any
    environment {
        DOCKER_PASSWORD = credentials("docker_password")
        DOCKER_TAG = 'hello-img'
        DOCKER_USERNAME = 'vikcocfmi'
    }

    stages {
        stage('Build & Test') {
            steps {
                sh './gradlew clean build'
            }
        }

        stage('Tag image') {
              steps {
                script {
                    GIT_TAG = sh([script: 'git fetch --tag && git tag', returnStdout: true]).trim()
                    MAJOR_VERSION = sh([script: 'git tag | cut -d . -f 1', returnStdout: true]).trim()
                    MINOR_VERSION = sh([script: 'git tag | cut -d . -f 2', returnStdout: true]).trim()
                    PATCH_VERSION = sh([script: 'git tag | cut -d . -f 3', returnStdout: true]).trim()
                    IMAGE_VERSION = sh([script: '$MAJOR_VERSION.$NEW_MINOR_VERSION.$PATCH_VERSION', returnStdout: true]).trim()
                }
                sh "docker build -t ${DOCKER_USERNAME}/hello-img:${MAJOR_VERSION}.\$((${MINOR_VERSION} + 1)).${PATCH_VERSION} ."

                sh "docker build -t ${DOCKER_USERNAME}/${DOCKER_TAG}:${IMAGE_VERSION} ."
                sh "docker login docker.io -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
                sh "docker push ${DOCKER_USERNAME}/${DOCKER_TAG}:${IMAGE_VERSION}"
              }
        }

    }
}