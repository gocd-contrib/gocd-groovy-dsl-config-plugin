package parts.tasks

import cd.go.contrib.plugins.configrepo.groovy.dsl.Tasks

return new Tasks().fetchExternalArtifact {
  artifactId = 'artifact_id'
  configuration = [
    abc: 'def',
    xyz: 'rst',
  ]
  job = 'upstream_job'
  pipeline = 'upstream'
  runIf = 'passed'
  stage = 'upstream_stage'
}
