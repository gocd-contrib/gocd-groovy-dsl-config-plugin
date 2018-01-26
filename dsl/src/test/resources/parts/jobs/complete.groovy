import cd.go.contrib.plugins.configrepo.groovy.dsl.Jobs

return new Jobs().job("test") {
  timeout = 5
  runInstanceCount = 7
  environmentVariables = [LD_LIBRARY_PATH: '.']
  tabs {
    tab('test') { path = 'results.xml' }
  }
  resources = ['linux']
  artifacts {
    test {
      source = 'src'
      destination = 'dest'
    }

    build {
      source = 'bin'
    }
  }

  tasks {
    exec {
      runIf = 'any'
      commandLine = ['make', '-j3', 'docs', 'install']
      workingDir = "some-dir"
    }
  }
}
