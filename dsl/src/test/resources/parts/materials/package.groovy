import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().pkg('myapt') {
  ref = 'apt-repo-id'
}
