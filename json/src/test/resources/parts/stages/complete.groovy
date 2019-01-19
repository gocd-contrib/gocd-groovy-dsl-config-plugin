package parts.stages

import cd.go.contrib.plugins.configrepo.groovy.dsl.Stages

return new Stages().stage('test') {
  artifactCleanupProhibited = true
  cleanWorkingDir = true
  environmentVariables = [
    TEST_NUM: '1',
  ]
  fetchMaterials = true
  secureEnvironmentVariables = [
    PASSWORD: 'AES:rzIrGQcbIX9e/J5Ic0WHcA==:nam+Ne5mUtS0TILGu4zHlw==',
  ]
  approval {
    roles = ['manager']
    users = ['john']
  }
  jobs {
    job('one') {
    }
    job('two') {
    }
  }
}
