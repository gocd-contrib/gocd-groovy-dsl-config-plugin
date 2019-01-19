package parts.materials

import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().svn('svnMaterial1') {
  autoUpdate = false
  blacklist = ['tools', 'lib']
  checkExternals = true
  destination = 'destDir1'
  encryptedPassword = 'encrypted-password'
  url = 'http://svn'
  username = 'user1'
}
