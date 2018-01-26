import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines

return new Pipelines().pipeline('pipe2'){
  group = 'group1'
  labelTemplate = 'foo-1.0-${COUNT}'
  lockBehavior = 'lockOnFailure'
  trackingTool {
    link = 'http://your-trackingtool/yourproject/${ID}'
    regex = ~/evo-(\d+)/
  }
  timer {
    spec = '0 15 10 * * ? *'
    onlyOnChanges = true
  }
  template = 'template'
}
