import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().p4('p4Material1') {
  port = 'host.domain.com:12345'
  username = 'johndoe'
  encryptedPassword = 'encrypted-password'
  useTickets = false
  view = '''
          //depot/external... //ws/external...
          //depot/tools... //ws/external...
         '''.stripIndent().trim()
  blacklist = ['externals', 'tools']
  autoUpdate = false
}
