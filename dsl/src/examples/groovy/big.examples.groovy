import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD

GoCD.script {
  environments {
    environment('production') {
      environmentVariables = [SSH_HOST: '192.168.1.100']
      secureVariables = [SSH_PASSWORD: 's^Du#@$xsSa']
      pipelines = ['website']
      agents = ['agent1-uuid', 'agent2-uuid']
    }
  }
  pipelines {
    pipeline('website') {
      trackingTool {
        link = 'https://github.com/gocd/gocd/issues/${ID}'
        regex = ~/##(\\d+)/
      }

      materials {
        git {
          url = 'https://github.com/gocd/www.go.cd'
        }
      }
      stages {
        stage('build-website') {
          jobs {
            job('build') {
              tasks {
                exec {
                  commandLine = ['bundle', 'install']
                }
                exec {
                  commandLine = ['bundle', 'exec', 'rake', 'build']
                }
              }

              artifacts {
                build {
                  source = 'target/jcoverage'
                  destination = 'Jcoverage'
                }
              }

              tabs {
                tab('JCoverage') {
                  path = 'JCoverage/index.html'
                }
              }
            }
          }
        }
      }
    }
  }
}
