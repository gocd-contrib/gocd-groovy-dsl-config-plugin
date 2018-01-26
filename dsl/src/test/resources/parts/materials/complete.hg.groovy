import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().hg('hgMaterial1') {
  url = 'repos/myhg'
  blacklist = ['externals', 'tools']
  destination = 'dir1'
  autoUpdate = false
}
