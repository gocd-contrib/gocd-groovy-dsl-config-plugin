/*
 * Copyright 2025 ThoughtWorks, Inc.
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

def releaseCredentials = {
  [
    GITHUB_TOKEN: 'AES:9Z9Lv85kry1oWWlOaCUF/w==:fWti8kD99VN7f++r7PfgLmXulS8GPmyb8bWm7yl1DYoDh1QihWEumO1mCfwiJ/O0',
  ]
}

def secretParam = { String param ->
  return "{{SECRET:[build-pipelines][$param]}}".toString()
}

GoCD.script {
  pipelines {
    pipeline("gocd-contrib-gocd-groovy-dsl-config-plugin") {
      materials {
        git {
          url = "https://git.gocd.io/git/gocd-contrib/gocd-groovy-dsl-config-plugin"
        }
      }
      group = "plugins"
      stages {
        stage("test") {
          jobs {
            job("test") {
              elasticProfileId = 'ecs-gocd-dev-build'
              tasks {
                bash {
                  commandString = './gradlew assemble check'
                }
              }
            }
          }
        }

        stage("github-preview-release") {
          environmentVariables       = [GITHUB_USER : 'gocd-ci-user']
          secureEnvironmentVariables = releaseCredentials()
          jobs {
            job("create-preview-release") {
              elasticProfileId = 'ecs-gocd-dev-build'
              tasks {
                bash {
                  commandString = './gradlew githubRelease'
                }
              }
            }
          }
        }

        stage("github-release") {
          approval { type = 'manual' }
          environmentVariables       = [GITHUB_USER : 'gocd-ci-user', PRERELEASE: "NO"]
          secureEnvironmentVariables = releaseCredentials()
          jobs {
            job("create-release") {
              elasticProfileId = 'ecs-gocd-dev-build'
              tasks {
                bash {
                  commandString = './gradlew githubRelease'
                }
              }
            }
          }
        }
      }
    }

    pipeline('upload-groovy-plugin-to-maven') {
      group = 'plugins'
      labelTemplate = '${COUNT}'
      lockBehavior = 'none'
      materials {
        git('signing-keys') {
          url = "https://git.gocd.io/git/gocd/signing-keys"
          destination = "signing-keys"
          blacklist = ['**/*']
        }
        git('groovy') {
          url = "https://git.gocd.io/git/gocd-contrib/gocd-groovy-dsl-config-plugin"
          destination = 'groovy'
        }
        dependency('GroovyPipeline') {
          pipeline = "gocd-contrib-gocd-groovy-dsl-config-plugin"
          stage = 'github-release'
        }
      }
      stages {
        stage('upload') {
          artifactCleanupProhibited = false
          cleanWorkingDir = true
          fetchMaterials = true
          environmentVariables = [
            GNUPGHOME                   : '.signing',
            GOCD_GPG_KEYRING_FILE       : 'signing-key.gpg',
            GOCD_GPG_PASSPHRASE         : secretParam("GOCD_GPG_PASSPHRASE"),
            MAVEN_CENTRAL_TOKEN_USERNAME: secretParam("MAVEN_CENTRAL_TOKEN_USERNAME"),
            MAVEN_CENTRAL_TOKEN_PASSWORD: secretParam("MAVEN_CENTRAL_TOKEN_PASSWORD"),
          ]
          secureEnvironmentVariables = [
            GOCD_GPG_KEY_ID: 'AES:+ORNmqROtoiLtfp+q4FlfQ==:PxQcI6mOtG4J/WQHS9jakg=='
          ]
          jobs {
            job('upload-to-maven') {
              elasticProfileId = 'ecs-gocd-dev-build'
              tasks {
                bash {
                  commandString = 'mkdir -p ${GNUPGHOME}'
                  workingDir = "groovy"
                }
                bash {
                  commandString = 'echo ${GOCD_GPG_PASSPHRASE} > gpg-passphrase'
                  workingDir = "groovy"
                }
                bash {
                  commandString = 'gpg --quiet --batch --passphrase-file gpg-passphrase --output - ../signing-keys/gpg-keys.pem.gpg | gpg --import --batch --quiet'
                  workingDir = "groovy"
                }
                bash {
                  commandString = 'gpg --export-secret-keys ${GOCD_GPG_KEY_ID} > dsl/${GOCD_GPG_KEYRING_FILE}'
                  workingDir = "groovy"
                }
                bash {
                  commandString = './gradlew clean dsl:publishToSonatype closeAndReleaseSonatypeStagingRepository'
                  workingDir = "groovy"
                }
              }
            }
          }
        }
      }
    }

    pipeline("gocd-contrib-gocd-groovy-dsl-config-plugin-pr") {
      materials {
        githubPR("gocd-groovy-dsl-config-plugin-material") {
          url = "https://git.gocd.io/git/gocd-contrib/gocd-groovy-dsl-config-plugin"
        }
      }
      group = "plugins-pr"
      stages {
        stage("test") {
          jobs {
            job("test") {
              elasticProfileId = 'ecs-gocd-dev-build'
              tasks {
                bash {
                  commandString = './gradlew assemble check'
                }
              }
            }
          }
        }
      }
    }
  }
}
