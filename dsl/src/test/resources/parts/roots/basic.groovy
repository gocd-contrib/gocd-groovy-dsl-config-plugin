import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD

GoCD.script {
  environments {
    environment('first') {}
    environment('second') {}
  }

  pipelines {
    pipeline('pipe1') {}
    pipeline('pipe2') {}
  }
}
