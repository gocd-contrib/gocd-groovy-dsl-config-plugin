import cd.go.contrib.plugins.configrepo.groovy.dsl.Tasks

return new Tasks().exec {
  commandLine = ['make', '-j3', 'docs', 'install']
  workingDir = 'dir'
  runIf = 'any'
}
