import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().pluggable('myPluggableGit') {
  scm = 'someScmGitRepositoryId'
  destination = 'destinationDir'
  blacklist = ['dir1', 'dir2']
}
