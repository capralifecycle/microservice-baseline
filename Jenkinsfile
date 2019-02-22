#!/usr/bin/env groovy

// See https://github.com/capralifecycle/jenkins-pipeline-library
@Library('cals') _

buildConfig([
  slack: [
    channel: '#cals-dev-info',
    teamDomain: 'cals-capra',
  ],
]) {
  dockerNode {
    checkout scm

    insideMaven {
      stage('Build and test Java project') {
        sh "mvn -s $MAVEN_SETTINGS -B package"
      }
    }
  }
}
