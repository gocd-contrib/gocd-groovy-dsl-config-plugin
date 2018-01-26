import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines

new Pipelines().with {
  pipeline('build') {
    environmentVariables = [
      SSH_HOST: 'deploy.example.com',
      SSH_USER: 'deploy-user'
    ]
  }
}
