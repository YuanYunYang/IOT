pipeline {
  agent any

  environment {
    GIT_URL = 'http://192.168.1.37:8088/ibms/iot-platform.git'
    HARBOR_REGISTRY = '192.168.1.250'
    HARBOR_PROJECT = 'micro-ibms'
    TAG = "${env.BUILD_NUMBER}"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout([$class: 'GitSCM',
          branches: [[name: '*/main']],
          userRemoteConfigs: [[url: env.GIT_URL, credentialsId: 'gitlab-cred']]
        ])
      }
    }

    stage('Build JARs') {
      steps {
        sh 'mvn -DskipTests clean package'
      }
    }

    stage('Build & Push Images') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'harbor-cred', usernameVariable: 'H_USER', passwordVariable: 'H_PASS')]) {
          sh '''
            echo "$H_PASS" | docker login -u "$H_USER" --password-stdin $HARBOR_REGISTRY

            for m in iot-gateway iot-service-user iot-service-device iot-service-alarm iot-service-core; do
              docker build -t $HARBOR_REGISTRY/$HARBOR_PROJECT/$m:$TAG ./$m
              docker push $HARBOR_REGISTRY/$HARBOR_PROJECT/$m:$TAG
            done
          '''
        }
      }
    }

  }
}
