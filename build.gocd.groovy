/*
 * Copyright 2020 ThoughtWorks, Inc.
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

import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD

GoCD.script {
  pipelines {
    pipeline('gocd-groovy-dsl-plugin') {
      group = "go-cd-contrib"

      trackingTool {
        link = 'https://github.com/gocd/gocd/issues/${ID}'
        regex = ~/##(\\d+)/
      }

      materials {
        git {
          url = 'https://github.com/gocd-contrib/gocd-groovy-dsl-config-plugin'
        }
      }
      stages {
        stage('build') {
          jobs {
            job('build') {
              tasks {
                bash {
                  commandString = 'git clean -dffx'
                }
                bash {
                  commandString = './gradlew clean test assemble'
                }
              }

              artifacts {
                build {
                  source = 'dsl/libs/*'
                  destination = 'dsl'
                }
                build {
                  source = 'groovy-dsl-plugin/build/libs/*'
                  destination = 'plugin'
                }
              }
            }
          }
        }
      }
    }
  }
}
