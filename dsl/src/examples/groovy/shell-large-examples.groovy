import cd.go.contrib.plugins.configrepo.groovy.dsl.Job

new Job().with {
  tasks {
    // for simple evaluation of environment variables
    // equivalent of `bash -c 'rsync file user@${REMOTE_HOST}:'`
    bash {
      commandString = 'rsync file user@${REMOTE_HOST}:'
    }

    // to explicitly call a shell script using `bash deploy.sh`
    bash {
      file = "./deploy.sh"
    }

    // to load up `.profile` or equivalent. This is particularly useful if you're using shell shims like `rvm` or `nvm`
    bash {
      login = true
      // you can also invoke the following method:
      loadProfile()
    }

    // if you want to use a custom shell
    shell {
      shell = '/bin/fish'
      file = './deploy.sh'
    }
  }
}
