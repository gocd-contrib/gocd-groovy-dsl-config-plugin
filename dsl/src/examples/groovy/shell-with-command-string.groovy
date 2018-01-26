import cd.go.contrib.plugins.configrepo.groovy.dsl.Tasks

new Tasks().with {
  // equivalent of `bash -c "YOUR_COMMAND_STRING"`
  shell {
    commandString = ""
  }
}
