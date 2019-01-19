package parts.materials

import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().git('mygit') {
  autoUpdate = false
  url = 'http://example.com/mygit.git'
}
