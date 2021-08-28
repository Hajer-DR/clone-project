pipeline {
 agent any
 environment {
  
  // This can be nexus3 or nexus2
  NEXUS_VERSION = "nexus3"
  // This can be http or https
  NEXUS_PROTOCOL = "http"
  // Where your Nexus is running. In my case:
  NEXUS_URL = "192.168.0.170:1081"
  // Repository where we will upload the artifact
  NEXUS_REPOSITORY = "Devops-repo"
  // Jenkins credential id – configured in previous step
  NEXUS_CREDENTIAL_ID = "nexus-user-credentials"
  SONARQUBE_URL = "http://192.168.0.170"
  SONARQUBE_PORT = "9000"
 }

 options {
 
  skipDefaultCheckout()
  
 }

 stages {
 //////////////////////////////////////////////////////////////
  stage('SCM') {
   steps {
    checkout scm
   }
  }

//////////////////////////////////////////////////////
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
  }
}
//////////////////////////////////////////////////////////
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
//////////////////////////////////////////////////////

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

////////////////////////////////////////////////////////

stage('Code Quality Analysis') {

   parallel {
   
    stage('JavaDoc') {

     agent {
      docker {
       image 'maven:3.6.0-jdk-8-alpine'
       args '-v /root/.m2/repository:/root/.m2/repository'
       reuseNode true
      }
     }
     steps {
      sh ' mvn javadoc:javadoc'
      step([$class: 'JavadocArchiver', javadocDir: './target/site/apidocs', keepAll: 'true'])
     }
    }

stage('SonarQube') {
       agent {
      docker {
       image 'maven:3.6.0-jdk-8-alpine'    
   args "-v /root/.m2/repository:/root/.m2/repository --net=devopsnet "  
       reuseNode true
      } 
     }
     steps {
      sh " mvn sonar:sonar -Dsonar.host.url=$SONARQUBE_URL:$SONARQUBE_PORT"
     }
      
   
   post {
    always {
     // using warning next gen plugin
     recordIssues aggregatingResults: true, tools: [javaDoc(), checkStyle(pattern: '**/target/checkstyle-result.xml')]
    }
   }
  }
}
}

/////////////////////////////////////////
stage('Deploy Artifact To Nexus') {

   steps {
    script {
     // Read POM xml file using 'readMavenPom' step , this step 'readMavenPom' is included in: https://plugins.jenkins.io/pipeline-utility-steps
     pom = readMavenPom file: "pom.xml";
   
     // Find built artifact under target folder
     filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
   
     // Print some info from the artifact found
     echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
   
     // Extract the path from the File found
     artifactPath = filesByGlob[0].path;
   
     // Assign to a boolean response verifying If the artifact name exists
     artifactExists = fileExists artifactPath;
     if (artifactExists) {
   
   
   //le upload se fait ici
   
      nexusArtifactUploader(
       nexusVersion: NEXUS_VERSION,
       protocol: NEXUS_PROTOCOL,
       nexusUrl: NEXUS_URL,
       groupId: pom.groupId,
       version: pom.version,
       repository: NEXUS_REPOSITORY,
       credentialsId: NEXUS_CREDENTIAL_ID,
      
    artifacts:

    [
        // Artifact generated such as .jar, .ear and .war files.
        [artifactId: pom.artifactId,
         classifier: '',
         file: artifactPath,
         type: pom.packaging
        ],
    
        // Lets upload the pom.xml file for additional information for Transitive dependencies
        [artifactId: pom.artifactId,
         classifier: '',
         file: "pom.xml",
         type: "pom"
        ]   
       ]     
      )
     } 
else {
      error "*** File: ${artifactPath}, could not be found";
     }

     }  } } 


////////////////////////////////////


 }
}
