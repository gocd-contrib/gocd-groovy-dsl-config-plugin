import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().configRepo('crMaterial') {
  destination = 'dir1'
  blacklist = ['externals', 'tools']
}
