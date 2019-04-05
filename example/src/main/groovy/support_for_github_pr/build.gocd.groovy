/*
 * Copyright 2019 ThoughtWorks, Inc.
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

package build_multiple_branches

import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD

GoCD.script {
  pipelines {
    pipeline("my-project-pr") {
      materials {
        githubPR {
          url = "https://github.com/bob/my-project"
        }
      }
      group = "gocd" == org ? "supported-plugins-pr" : "plugins-pr"
      stages {
        stage("test") {
          jobs {
            job('build') {
              tasks {
                bash {
                  commandString = 'bundle install --path .bundle -j4'
                }
                bash {
                  commandString = 'bundle exec rake build'
                }
              }
            }
          }
        }
      }
    }
  }
}
