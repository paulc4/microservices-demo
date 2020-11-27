node{
  stage('SCM Checkout'){
  git 'https://github.com/bittersweetmemories/microservices-demo/new/QA'
  }
  stage('Compile-Package'){
  sh 'mvn package'
  }
}
