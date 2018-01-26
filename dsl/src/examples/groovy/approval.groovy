import cd.go.contrib.plugins.configrepo.groovy.dsl.Stages

new Stages().with {
  stage('deploy') {
    approval {
      type = 'manual'
      users = ['bob', 'alice']
      roles = ['admins', 'deployers']
    }
  }
}
