package parts.materials

import cd.go.contrib.plugins.configrepo.groovy.dsl.Materials

return new Materials().dependency('upstream') {
  pipeline = 'upstream-pipeline-1'
  stage = 'test'
}
