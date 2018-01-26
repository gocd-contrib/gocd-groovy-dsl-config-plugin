import cd.go.contrib.plugins.configrepo.groovy.dsl.Jobs

new Jobs().with {
  job('build') {
    tasks {
      exec {
        commandLine = ['./gradlew', 'clean', 'assemble', 'check']
      }
      bash {
        commandString = "scp foo.jar deploy@remote.host:"
      }
    }
  }
}
