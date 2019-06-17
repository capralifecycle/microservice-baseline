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
        sh "mvn -s \$MAVEN_SETTINGS -B package"
      }

      stage('Generate report for available updates') {
        sh '''
          mvn -s $MAVEN_SETTINGS -B -U \\
            -Dversions.outputFile=dependency-updates.txt \\
            versions:display-dependency-updates
          mvn -s $MAVEN_SETTINGS -B -U \\
            -Dversions.outputFile=plugin-updates.txt \\
            versions:display-plugin-updates
        '''

        archiveArtifacts artifacts: 'dependency-updates.txt,plugin-updates.txt', fingerprint: true
      }

      stage('Show available updates') {
        echo 'Available dependency updates'
        sh 'cat dependency-updates.txt'

        echo 'Available plugin updates:'
        sh 'cat plugin-updates.txt'
      }
    }
  }
}
