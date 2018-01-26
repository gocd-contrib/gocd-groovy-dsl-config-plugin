import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines

new Pipelines().with {
  pipeline('docs-website') {
    materials {
      svn {
        url = 'https://svn.example.com/my-project/trunk'
        username = 'bob'
        // see https://api.gocd.org/current/#encrypt-a-plain-text-value
        encryptedPassword = 'aSdiFgRRZ6A='
      }
    }
  }
}
