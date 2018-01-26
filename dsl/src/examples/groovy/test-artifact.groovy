import cd.go.contrib.plugins.configrepo.groovy.dsl.Jobs

new Jobs().with {
  job('build') {
    artifacts {
      test {
        source = 'target/reports/junit/**/*.xml'
        destination = 'junit-xml'
      }
    }
  }
}

