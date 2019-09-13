package parts.materials

import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().hg('hgMaterial1') {
  autoUpdate = false
  blacklist = ['externals', 'tools']
  destination = 'dir1'
  encryptedPassword = 'some encrypted password'
  url = 'repos/myhg'
  username = 'username'
}
