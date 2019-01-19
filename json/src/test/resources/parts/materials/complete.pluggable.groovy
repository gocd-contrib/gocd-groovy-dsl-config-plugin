package parts.materials

import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().pluggable('myPluggableGit') {
  blacklist = ['dir1', 'dir2']
  destination = 'destinationDir'
  scm = 'someScmGitRepositoryId'
}
