import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipeline

new Pipeline().with {
  materials {
    git {
      url = 'https://github.com/gocd/gocd'
      blacklist = ['docs/**/*.*', 'README.md']
    }
    git {
      url = 'https://github.com/gocd/gocd-plugins'
      whitelist = ['src']
    }
  }
}
