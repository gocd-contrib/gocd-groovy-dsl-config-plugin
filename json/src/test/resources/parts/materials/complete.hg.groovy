package parts.materials

import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().hg('hgMaterial1') {
  autoUpdate = false
  blacklist = ['externals', 'tools']
  destination = 'dir1'
  url = 'repos/myhg'
}
