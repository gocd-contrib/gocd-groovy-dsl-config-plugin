import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD

GoCD.script {
  environments {
    environment('production') {
      environmentVariables = [SSH_HOST: '192.168.1.100']
      secureEnvironmentVariables = [SSH_PASSWORD: 's^Du#@$xsSa']
      pipelines = ['website']
      agents = ['agent1-uuid', 'agent2-uuid']
    }
  }
}
