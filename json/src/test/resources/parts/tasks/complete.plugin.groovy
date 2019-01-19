package parts.tasks

import cd.go.contrib.plugins.configrepo.groovy.dsl.Tasks

return new Tasks().plugin {
  options = [
    ConverterType: 'jsunit',
  ]
  runIf = 'failed'
  secureOptions = [
    password: 'ssd#%fFS*!Esx',
  ]
  configuration {
    id = 'xunit.converter.task.plugin'
  }
}
