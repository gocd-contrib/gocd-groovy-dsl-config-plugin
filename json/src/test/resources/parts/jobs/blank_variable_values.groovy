package parts.jobs

import cd.go.contrib.plugins.configrepo.groovy.dsl.Jobs

return new Jobs().job('test') {
  environmentVariables = [
    THIS_IS_A_BLANK_VAR: '',
  ]
}
