import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().git('gitMaterial1') {
  url = 'http://my.git.repository.com'
  branch = 'feature12'
  blacklist = ['externals', 'tools']
  destination = 'dir1'
  autoUpdate = false
  shallowClone = true
}
