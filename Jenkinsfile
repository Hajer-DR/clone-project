pipeline {

 agent any
 
 options {
 
  skipDefaultCheckout()
  
 }
 stages {
 
  stage('SCM') {
   steps {
    checkout scm
   }
  }
  
    
  stage('Build') {
   parallel {
   
    stage('Compile') {
    
     agent {
      docker {
       image 'maven:3.6.0-jdk-8-alpine'
       args '-v /root/.m2/repository:/root/.m2/repository'
       // to use the same node and workdir defined on top-level pipeline for all docker agents
       reuseNode true
      }
      
     }
     
     steps {
      sh 'mvn clean compile'
      sh 'mvn package -DskipTests=true'
     }
     
    }
    
    
  stage('Unit Tests') {


   agent {
    docker {
     image 'maven:3.6.0-jdk-8-alpine'
     args '-v /root/.m2/repository:/root/.m2/repository'
     reuseNode true
    }
   }
   steps {
    sh 'mvn test'
   }
 //  post {
 //   always {
 //    junit 'target/surefire-reports/**/*.xml'
 //   }
 //  }
  }
  stage('Integration Tests') {
   agent {
    docker {
     image 'maven:3.6.0-jdk-8-alpine'
     args '-v /root/.m2/repository:/root/.m2/repository'
     reuseNode true
    }
   }
   steps {
    sh 'mvn verify -Dsurefire.skip=true'
   }
   post {
   
    always {
     junit 'target/failsafe-reports/**/*.xml'
    }
    success {
     stash(name: 'artifact', includes: 'target/*.jar')
     stash(name: 'pom', includes: 'pom.xml')

     // to add artifacts in jenkins pipeline tab (UI)
     archiveArtifacts 'target/*.jar'
    }
   }   
  }  
 } 
}   }  }
