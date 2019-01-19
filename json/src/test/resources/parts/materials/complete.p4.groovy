package parts.materials

import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().p4('p4Material1') {
  autoUpdate = false
  blacklist = ['externals', 'tools']
  encryptedPassword = 'encrypted-password'
  port = 'host.domain.com:12345'
  useTickets = false
  username = 'johndoe'
  view = '//depot/external... //ws/external...\n//depot/tools... //ws/external...'
}
