import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines

new Pipelines().with {
  pipeline('docs-website') {
    materials {
      p4 {
        url = 'https://p4.example.com/gocd/gocd'
        branch = 'develop'
      }
    }
  }
}
