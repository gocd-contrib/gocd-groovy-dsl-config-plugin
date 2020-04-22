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
                tab('JCoverage') { path = 'Jcoverage/index.html' }
              }
            }
          }
        }
      }
    }
  }

}
