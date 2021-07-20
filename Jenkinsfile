pipeline {
agent any

stages{
 stage('SCM')
 {
  stage('SCM')
  {
   steps{ 
    checkout scm
   }
  }
  stage('Build')
  parallel {
   stage('Compile') {
    agent {
     docker {
      image 'maven:3.6.0-jdk-8-alpine'
      args '-v /root/.m2/repository:/root/.m2/repository'
      reuseNode true
     }
    }
    steps {
     sh 'mv clean compile'
      sh 'mvn package -DskipTests=true'
    }
  }
 }
}
}
}
