package parts.tasks

import cd.go.contrib.plugins.configrepo.groovy.dsl.Tasks

return new Tasks().fetchArtifact {
  destination = 'test'
  job = 'upstream_job'
  pipeline = 'upstream'
  runIf = 'any'
  source = 'result'
  stage = 'upstream_stage'
}
