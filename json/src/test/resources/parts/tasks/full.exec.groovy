package parts.tasks

import cd.go.contrib.plugins.configrepo.groovy.dsl.Tasks

return new Tasks().exec {
  commandLine = ['make', '-j3', 'docs', 'install']
  runIf = 'any'
  workingDir = 'dir'
}
