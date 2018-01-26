import cd.go.contrib.plugins.configrepo.groovy.dsl.Tasks

return new Tasks().fetchFile {
  runIf = 'any'
  pipeline = 'upstream'
  stage = 'upstream_stage'
  job = 'upstream_job'
  source = 'result'
  destination = 'test'
}
