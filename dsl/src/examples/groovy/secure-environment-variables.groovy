import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines

new Pipelines().with {
  pipeline('build') {
    secureVariables = [
      SSH_PASSWORD: 'AES:yBk975e1rJiuRnAx9AZ6Og==:ee6yi/2sEh5/TUlbmNWelg==',
      API_TOKEN   : 'AES:mYCdWAx/pR0YKLpSJHF9ZQ==:zbSkQLjsL34pzXDtyTj0iw=='
    ]
  }
}
