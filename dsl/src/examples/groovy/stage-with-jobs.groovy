import cd.go.contrib.plugins.configrepo.groovy.dsl.Stages

new Stages().with {
  stage('build') {
    jobs {
      job('backend') {
        // job definition
      }
      job('frontend') {
        // job definiton
      }
    }
  }
}
