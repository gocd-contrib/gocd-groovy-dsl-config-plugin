import cd.go.contrib.plugins.configrepo.groovy.dsl.Tasks

new Tasks().with {
  exec {
    commandLine = ["curl", "https://gocd.example.com/go/api/version", "-H'Accept: application/vnd.go.cd.v1+json'"]
    workingDir = "foo"
  }
}
