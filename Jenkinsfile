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

    insideMaven([ version: '3-jdk-11-alpine' ]) {
      stage('Build and test Java project') {
        sh "mvn -s \$MAVEN_SETTINGS -B verify"
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
    }

    insideSonarScanner {
      analyzeSonarCloudForMaven([
        'sonar.organization': 'capraconsulting',
        'sonar.projectKey': 'capraconsulting_microservice-baseline',
      ])
    }
  }
}
