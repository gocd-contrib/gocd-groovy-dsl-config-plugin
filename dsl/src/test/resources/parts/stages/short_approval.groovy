import cd.go.contrib.plugins.configrepo.groovy.dsl.Stages

return new Stages().stage('test') {
  approval {
    type = 'manual'
  }
  jobs {
    job('one') {}
  }
}
