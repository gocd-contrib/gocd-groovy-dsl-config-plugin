import cd.go.contrib.plugins.configrepo.groovy.dsl.Jobs

new Jobs().with {
  job('build') {
    tasks {
      exec {
        commandLine = ['make', '-j3', 'doc', 'install']
      }
    }
  }
}
