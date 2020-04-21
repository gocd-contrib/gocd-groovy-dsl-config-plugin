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

package build_matrix

import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD


def operatingSystems = ['darwin', 'linux', 'windows']
def architectures = ['amd64', '386']

def matrix = [operatingSystems, architectures].combinations()

GoCD.script { script ->
  pipelines {
    pipeline("build-matrix") {
      group = "example-group"

      trackingTool {
        link = 'https://github.com/gocd-contrib/gocd-golang-bootstrapper/issues/${ID}'
        regex = ~/##(\\d+)/
      }

      materials {
        git {
          url = 'https://github.com/gocd-contrib/gocd-golang-bootstrapper'
        }
      }
      stages {
        stage('build') {
          jobs {
            // deconstruct each combination in the build matrix
            matrix.each { os, arch ->
              job("build-${os}-${arch}") {
                tasks {
                  exec {
                    commandLine = ['make', '-j4', "go-bootstrapper.${os}.${arch}".toString()]
                  }
                }

                artifacts {
                  build {
                    source = "out/go-bootstrapper-*.${os}.${arch}*"
                    destination = 'dist'
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
