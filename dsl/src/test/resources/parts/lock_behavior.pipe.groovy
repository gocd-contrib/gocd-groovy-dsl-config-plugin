import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines

return new Pipelines().pipeline('pipe2') {
  group = 'group1'
  lockBehavior = 'lockOnFailure'
}
