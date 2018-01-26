import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines

new Pipelines().with {
  pipeline('docs-website') {
    materials {
      tfs {
        url = 'http://10.21.3.210:8080/tfs'
        project = 'MyProject'
        domain = 'MyDomain'
        username = 'bob'
        encryptedPassword = 'aSdiFgRRZ6A='
      }
    }
  }
}
