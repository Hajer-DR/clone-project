pipeline {
 agent any
 environment {
  // This can be nexus3 or nexus2
  NEXUS_VERSION = "nexus3"
  // This can be http or https
  NEXUS_PROTOCOL = "http"
  // Where your Nexus is running. In my case:
  //NEXUS_URL = "192.168.0.170:1081"

  // Repository where we will upload the artifact
  NEXUS_REPOSITORY = "Devops-repo"
  // Jenkins credential id to authenticate to Nexus OSS
  NEXUS_CREDENTIAL_ID = "nexus-user-credentials"
  /*
    Windows: set the ip address of docker host. In my case 192.168.99.100.
    to obtains this address : $ docker-machine ip
    Linux: set localhost to SONARQUBE_URL
  */
  SONARQUBE_URL = "http://192.168.0.170"
  SONARQUBE_PORT = "9000"
 }
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
      sh ' mvn clean compile'
      sh 'mvn package -DskipTests=true'
     }
    }
    stage('CheckStyle') {
           when {
    branch 'master'
   }
     agent {
      docker {
       image 'maven:3.6.0-jdk-8-alpine'
       args '-v /root/.m2/repository:/root/.m2/repository'
       reuseNode true
      }
     }
     steps {
      sh ' mvn checkstyle:checkstyle'
//plugin deprecated - causes error
 //   step([$class: 'CheckStylePublisher',
 //     canRunOnFailed: true,
 //      defaultEncoding: '',
 //    healthy: '100',

 //      pattern: '**/target/checkstyle-result.xml',
//       unHealthy: '90',
 //      useStableBuildAsReference: true
   //  ])
     }
    }
   }
  }
  stage('Unit Tests') {
 //check why it makes always jenkins skip the step
 //  when {
 //   anyOf { branch 'master'; branch 'develop' }
//   }
       when {
    branch 'master'
   }

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
   post {
    always {
     junit 'target/surefire-reports/**/*.xml'
    }
   }
  }
  stage('Integration Tests') {

       when {
    branch 'master'
   }


   //check why it makes always jenkins skip the step
 //  when {
//    anyOf { branch 'master'; branch 'develop' }
//   }
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





    stage('Code Quality Analysis') {
           when {
    branch 'master'
   }

   parallel {
    stage('PMD') {

     agent {
      docker {
       image 'maven:3.6.0-jdk-8-alpine'
       args '-v /root/.m2/repository:/root/.m2/repository'
       reuseNode true
      }
     }

     steps {
      sh ' mvn pmd:pmd'
     }

       post {
    always {
     recordIssues enabledForFailure: true, tool: pmdParser(pattern: '**/target/pmd.xml')
    }
    }

   }
    stage('Findbugs') {



     agent {
      docker {
       image 'maven:3.6.0-jdk-8-alpine'
       args '-v /root/.m2/repository:/root/.m2/repository'
       reuseNode true
      }
     }
     steps {
      sh ' mvn findbugs:findbugs'

     }

       post {
    always {
     recordIssues enabledForFailure: true, tool: findBugs(pattern: '**/target/findbugsXml.xml')
    }
    }
    }
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
//  added manual network devopsman, added jenkins and sonar containers to this network, so the agent of this stage can reach sonarqube container

     args "-v /root/.m2/repository:/root/.m2/repository --net=devops_devops "

       reuseNode true
      }
     }
     steps {
      sh " mvn sonar:sonar -Dsonar.host.url=$SONARQUBE_URL:$SONARQUBE_PORT"
     }
   }
   }
   post {
    always {
     // using warning next gen plugin
     recordIssues aggregatingResults: true, tools: [javaDoc(), checkStyle(pattern: '**/target/checkstyle-result.xml')]
    }
   }
  }
    stage('Deploy Artifact To Nexus') {
     when {
      branch 'master'
     }
     steps {
      script {
          // récupérer l’artéfact qui est déjà généré dans l’étape « Integration Tests » pour éviter la réexécution de mvn package de nouveau, il suffit d’utiliser la fonction unstash pour récupérer l’artéfact
       unstash 'pom'
       unstash 'artifact'
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
        nexusArtifactUploader(
         nexusVersion: NEXUS_VERSION,
         protocol: NEXUS_PROTOCOL,
         nexusUrl: NEXUS_URL,
         groupId: pom.groupId,
         version: pom.version,
         repository: NEXUS_REPOSITORY,
         credentialsId: NEXUS_CREDENTIAL_ID,
         artifacts: [
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
       } else {
        error "*** File: ${artifactPath}, could not be found";
       }
      }
     }
    }
  }
  }
