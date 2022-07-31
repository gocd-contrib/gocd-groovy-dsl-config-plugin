/*
 * Copyright 2022 Thoughtworks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
