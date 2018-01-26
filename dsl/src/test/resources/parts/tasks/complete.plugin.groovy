import cd.go.contrib.plugins.configrepo.groovy.dsl.Tasks

return new Tasks().plugin {
  options = [
    ConverterType: 'jsunit'
  ]
  secureOptions = [
    password: 'ssd#%fFS*!Esx'
  ]
  runIf = 'failed'
  configuration {
    id = 'xunit.converter.task.plugin'
  }
}
//plugin:
//  options:
//    ConverterType: jsunit
//  secure_options:
//    password: "ssd#%fFS*!Esx"
//  run_if: failed
//  configuration:
//    id: xunit.converter.task.plugin
//    version: 1
