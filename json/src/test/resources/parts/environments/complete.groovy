package parts.environments

import cd.go.contrib.plugins.configrepo.groovy.dsl.Environments

return new Environments().environment('testing') {
  agents = ['123']
  environmentVariables = [
    DEPLOYMENT: 'testing',
  ]
  pipelines = ['example-deploy-testing', 'build-testing']
  secureEnvironmentVariables = [
    ENV_PASSWORD: 'AES:yBk975e1rJiuRnAx9AZ6Og==:ee6yi/2sEh5/TUlbmNWelg==',
  ]
}
