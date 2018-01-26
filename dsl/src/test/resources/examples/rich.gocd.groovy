import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD

GoCD.script {
  pipelines {
    pipeline('pipe2') {
      group = 'rich'
      labelTemplate = '${mygit[:8]}'
      lockBehavior = 'lockOnFailure'
      trackingTool {
        link = 'http://your-trackingtool/yourproject/${ID}'
        regex = ~/evo-(\d+)/
      }
      timer {
        spec = '0 0 22 ? * MON-FRI'
        onlyOnChanges = true
      }

      materials {
        git('mygit') {
          url = 'http://my.example.org/mygit.git'
          branch = 'ci'
        }

        dependency('upstream') {
          pipeline = 'pipe2'
          stage = 'test'
        }
      }
      stages {
        stage('build') {
          cleanWorkingDir = true
          approval {
            type = 'manual'
            roles = ['manager']
          }

          jobs {
            job('csharp') {
              runInstanceCount = 3
              environmentVariables = [MONO_PATH: '/usr/bin/local/mono']
              secureVariables = [PASSWORD: 'AES:yBk975e1rJiuRnAx9AZ6Og==:ee6yi/2sEh5/TUlbmNWelg==']
              resources = ['net45']
              tasks {
                fetchDirectory {
                  pipeline = 'pipe2'
                  stage = 'build'
                  job = 'test'
                  source = 'test-bin/'
                  destination = 'bin/'
                }
                exec {
                  commandLine = ['make', 'VERBOSE=true']
                }
                bash {
                  commandString = './build.sh ci'
                }
              }
              artifacts {
                build {
                  source = 'bin/'
                  destination = 'build'
                }
                test {
                  source = 'tests/'
                  destination = 'test-reports/'
                }
                external {
                  id = 'docker-release-candidate'
                  storeId = 'dockerhub'
                  configuration = [
                    image: 'gocd/gocd-server',
                    tag  : 'latest'
                  ]
                  secureConfiguration = [
                    DB_PASSWORD: 'AES:yBk975e1rJiuRnAx9AZ6Og==:ee6yi/2sEh5/TUlbmNWelg=='
                  ]
                }
              }
              tabs {
                tab('report') { path = 'test-reports/index.html' }
              }
            }
          }
        }
      }
    }
  }
}
