import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines

new Pipelines().with {
  pipeline('docs-website') {
    materials {
      hg {
        url = 'https://hg.example.com/myproject##myBranch'
        blacklist = ['README.md', 'docs/**']
      }
    }
  }
}
