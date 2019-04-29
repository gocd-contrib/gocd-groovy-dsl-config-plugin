import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines

new Pipelines().with {
  pipeline('docs-website') {
    materials {
      git {
        url = 'https://github.com/gocd/gocd'
        branch = 'develop'
        blacklist = ['README.md', 'docs/**']
        username = 'bob'
        // see https://api.gocd.org/current/#encrypt-a-plain-text-value
        encryptedPassword = 'aSdiFgRRZ6A='
      }
    }
  }
}
