package parts.materials

import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().git('gitMaterial1') {
  autoUpdate = false
  blacklist = ['externals', 'tools']
  branch = 'feature12'
  destination = 'dir1'
  encryptedPassword = 'some encrypted password'
  shallowClone = true
  url = 'http://my.git.repository.com'
  username = 'username'
}
