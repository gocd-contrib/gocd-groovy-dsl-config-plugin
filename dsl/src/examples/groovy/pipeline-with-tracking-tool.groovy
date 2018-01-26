import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines

new Pipelines().with {
  pipeline('build') {
    trackingTool {
      link = 'https://github.com/gocd/gocd/issues/${ID}'
      regex = ~/##(\\d+)/
    }
  }
}
