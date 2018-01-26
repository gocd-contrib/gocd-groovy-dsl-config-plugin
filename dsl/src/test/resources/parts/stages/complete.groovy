import cd.go.contrib.plugins.configrepo.groovy.dsl.Stages

return new Stages().stage("test") {
  fetchMaterials = true
  cleanWorkingDir = true
  artifactCleanupProhibited = true
  approval {
    type = 'success'
    roles = ['manager']
    users = ['john']
  }
  environmentVariables = ['TEST_NUM': 1]
  secureVariables = ['PASSWORD': "AES:rzIrGQcbIX9e/J5Ic0WHcA==:nam+Ne5mUtS0TILGu4zHlw=="]
  jobs {
    job('one') {}
    job('two') {}
  }
}
