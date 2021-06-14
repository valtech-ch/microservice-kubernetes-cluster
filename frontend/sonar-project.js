/* eslint-disable */

const sonarqubeScanner = require('sonarqube-scanner');
sonarqubeScanner(
    {
      serverUrl:  process.argv[2],
      token : process.argv[3],
      options : {
        'sonar.projectKey': 'microservice-kubernetes-cluster-frontend',
        'sonar.projectName': 'Microservice Kubernetes Cluster Frontend',
        'sonar.sourceEncoding': 'UTF-8',
        'sonar.sources':  'src',
        'sonar.tests':  'src',
        'sonar.inclusions'  :  '**', // Entry point of your code
        'sonar.test.inclusions':  'src/**/*.spec.js,src/**/*.spec.jsx,src/**/*.test.js,src/**/*.test.jsx',
        'sonar.javascript.lcov.reportPaths':  'coverage/lcov.info',
        'sonar.testExecutionReportPaths':  'coverage/test-reporter.xml'
      }
    }, () => {});