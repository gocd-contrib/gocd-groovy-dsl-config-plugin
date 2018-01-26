import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines

new Pipelines().with {
  pipeline('build') {
    params = [
      OS     : 'linux',
      BROWSER: 'firefox'
    ]

    stages {
      stage('foo') {
        jobs {
          job('foo') {
            tasks {
              exec {
                commandLine = ['./gradle', 'test', '-Pos=#{OS}', '-Pbrowser=#{BROWSER}']
              }
            }
          }
        }
      }
    }
  }
}
