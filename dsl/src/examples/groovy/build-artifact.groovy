import cd.go.contrib.plugins.configrepo.groovy.dsl.Jobs

new Jobs().with {
  job('build') {
    artifacts {
      build {
        source = 'target/jcoverage'
        destination = 'Jcoverage'
      }
      build {
        source = 'target/dist/**/*.exe'
        destination = 'windows-installers'
      }
    }
  }
}

