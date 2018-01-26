import cd.go.contrib.plugins.configrepo.groovy.dsl.Jobs

new Jobs().with {
  job('build') {
    tabs {
      tab('JCoverage') {
        path = 'Jcoverage/index.html'
      }
      tab('JUnit') {
        path = 'junit/index.html'
      }
    }
  }
}
