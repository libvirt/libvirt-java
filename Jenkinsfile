pipeline {
    agent {
        label 'executor'
    }
    tools {
        maven "M3"
    }
    options {
        timestamps()
        ansiColor("xterm")
    }

    parameters {
        string(name: 'releaseVersion',
            defaultValue: '',
            description: 'Release version'
        )
    }

    environment {
        def VERSION = "${params.releaseVersion}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh "mvn -DskipTests clean package"
            }
        }

        stage('Test') {
            steps {
                sh "mvn test"
            }

            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    archiveArtifacts 'target/*.jar'
                }
            }
        }

        stage('Build & Deploy SNAPSHOT') {
            when {
                branch 'master'
            }
            steps {
                sh "mvn -B deploy"
            }
        }

        stage('Release and Publish artifact') {
            when {
                expression { env.releaseVersion }
            }
            steps {
                sh "mvn release:prepare release:perform -DreleaseVersion=${params.releaseVersion}"
            }
        }
    }
}
