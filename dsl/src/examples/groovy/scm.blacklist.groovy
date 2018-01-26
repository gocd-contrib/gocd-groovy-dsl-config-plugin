import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

new Materials().with {
  git {
    url = 'https://github.com/gocd/gocd'
    blacklist = ['README.md', 'docs/**/*.*']
  }
}
