import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().svn('svnMaterial1') {
  url = 'http://svn'
  username = 'user1'
  encryptedPassword = 'encrypted-password'
  checkExternals = true
  blacklist = ['tools', 'lib']
  destination = 'destDir1'
  autoUpdate = false
}
