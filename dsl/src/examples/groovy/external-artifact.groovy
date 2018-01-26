import cd.go.contrib.plugins.configrepo.groovy.dsl.Jobs

new Jobs().with {
  job('build') {
    artifacts {
      external {
        id = 'docker-release-candidate'
        storeId = 'dockerhub'
        configuration = [
          image: 'gocd/gocd-server',
          tag: 'latest'
        ]
        secureConfiguration = [
          DB_PASSWORD: 'AES:yBk975e1rJiuRnAx9AZ6Og==:ee6yi/2sEh5/TUlbmNWelg=='
        ]
      }
    }
  }
}

