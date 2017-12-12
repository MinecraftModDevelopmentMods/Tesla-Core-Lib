#!/bin/groovy
pipeline {
    agent any

    stages() {
        stage("Checkout") {
            steps {
                checkout scm
            }
        }

        stage("Clean & Setup") {
            steps {
                sh """set -x
                      chmod 755 gradlew
                      ./gradlew clean setupCIWorkspace
                   """
            }
        }

        stage("Build") {
            steps {
                sh """set -x
                      ./gradlew build
                   """

                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true, onlyIfSuccessful: true
            }
        }

        stage("Maven") {
            steps {
                sh """set -x
                      ./gradlew uploadArchives
                   """
            }
        }
    }
}