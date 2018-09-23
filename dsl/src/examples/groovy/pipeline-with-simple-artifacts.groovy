import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD

GoCD.script {
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
                tab('JCoverage') { path = 'Jcoverage/index.html'}
              }
            }
          }
        }
      }
    }
  }

}
