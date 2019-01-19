package parts.materials

import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().configRepo('crMaterial') {
  blacklist = ['externals', 'tools']
  destination = 'dir1'
}
