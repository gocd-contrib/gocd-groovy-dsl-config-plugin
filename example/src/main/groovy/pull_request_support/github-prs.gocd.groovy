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
  branches {
    matching {
      // OPTIONAL: defaults to match any string. When present,
      // this will filter/restrict git refs to those that match
      // the provided pattern.
      pattern = ~/.*/

      from = github {
        // lookup() allows one to provide secret/secure values to the script.
        // These arbitrary key+values are configurable from the UI on the GoCD server.
        //
        // lookup() is resolvable/usable anywhere in config script block.
        apiAuthToken = lookup("my.oauth.token") // OPTIONAL for public repos, but recommended.
        fullRepoName = "gocd-contrib/gocd-cli"

        // OPTIONAL for public repos
        materialUsername = "readonly-ci-user"
        materialPassword = lookup("ci.readonly.password")
      }

      onMatch { ctx ->
        // Build your entire workflow; you can have many pipeline blocks here.
        pipeline("build-PR-${ctx.branchSanitized}") {
          group = "tools"

          // As a convenience, a preconfigured material pointing to the pull request
          // is available in the template binding context. Of course, one may modify
          // the material or choose to not use it and manually configure a material.
          materials { add(ctx.repo) }
          stages { stage('tests') {
            jobs {job('units') { tasks {
              bash { commandString = 'build.sh' }
            } } }
          } }
        }

        pipeline("deploy-experimental-pr-${ctx.branchSanitized}") {
          group = "tools"
          materials { add(ctx.repo) }
          stages { stage("publish") {
            jobs { job("publish") { tasks {
              bash { commandString = './publish.sh' }
            } } }
          } }
        }
      }
    }

    // Multiple matching blocks are allowed so you can track many repositories.
    matching {
      from = github {
        fullRepoName = "gocd-contrib/gocd-groovy-dsl-config-plugin.git"
      }

      onMatch { ctx ->
        pipeline("groovy-plugin-pr-${ctx.branchSanitized}") {
          group = "plugins"
          materials { add(ctx.repo) }
          stages { stage("tests") {
            jobs { job("units") { tasks {
              bash { commandString = './gradlew clean test assemble' }
            } } }
          } }
        }
      }
    }
  }
}
