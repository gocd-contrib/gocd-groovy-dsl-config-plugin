import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().git('mygit') {
  url = 'http://example.com/mygit.git'
  autoUpdate = false
}
