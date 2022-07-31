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

import cd.go.contrib.plugins.configrepo.groovy.dsl.GitMaterial
import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD

GoCD.script {
  branches {
    matching {
      // OPTIONAL: defaults to match any string. When present,
      // this will filter/restrict git refs to those that match
      // the provided pattern.
      pattern = ~/.*/

      // This block is for gitlab.com (cloud); for self-hosted GitLab,
      // see the section below.
      from = gitlab {
        // lookup() allows one to provide secret/secure values to the script.
        // These arbitrary key+values are configurable from the UI on the GoCD server.
        //
        // lookup() is resolvable/usable anywhere in config script block.
        apiAuthToken = lookup("my.oauth.token") // OPTIONAL for public repos, but recommended.
        fullRepoName = "the-best/repo-ever"

        // OPTIONAL for public repos.
        materialUsername = "readonly-ci-user"
        materialPassword = lookup("ci.readonly.password")
      }

      onMatch { ctx ->
        // Build your entire workflow; you can have many pipeline blocks here.
        pipeline("build-PR-${ctx.branchSanitized}") {
          group = sanitizeName("${ctx.branch}-${ctx.title}")

          // As a convenience, a preconfigured material pointing to the merge request
          // is available in the template binding context. Of course, one may modify
          // the material or choose to not use it and manually configure a material.
          materials { add(ctx.repo) }
          stages { stage('tests') {
            jobs { job('units') { tasks {
              bash { commandString = 'npm run tests' }
            } } }
          } }

          // When a build stage runs or completes, git materials can be configured to
          // notify a git provider of the build status of a particular commit SHA.
          //
          // Here, we notify GitLab of the build status; `ctx.provider` is preconfigured
          // to direct notifications back to the same provider and repository used to
          // enumerate merge requests.
          //
          // For further customization, see the example at the bottom of this file.
          ctx.repo.notifiesBy(ctx.provider)
        }

        pipeline("deploy-experimental-pr-${ctx.branchSanitized}") {
          group = sanitizeName("${ctx.branch}-${ctx.title}")
          materials { add(ctx.repo) }
          stages { stage("publish") {
            jobs { job("publish") { tasks {
              bash { commandString = 'npm run deploy-unstable' }
            } } }
          } }
        }
      }
    }

    // Multiple matching blocks are allowed so you can track many repositories.
    matching {
      from = gitlab {
        fullRepoName = "something/internally-hosted"
        // self-hosted requires your server base URL
        serverBaseUrl = "https://my-hosted-server:8443/gitlab"
        apiAuthToken = lookup("my.oauth.secret") // technically optional, but commonly required in most setups
      }

      onMatch { ctx ->
        pipeline("yet-another-pr-${ctx.branchSanitized}") {
          // `sanitizeName()` will sanitize strings for use as GoCD identifiers for group, pipeline,
          // stage, job, etc.
          //
          // NOTE: The `ctx` context binding object has various useful metadata about the
          // matched git ref. Download this plugin's related dsl.jar and configure it as a dependency
          // when writing your configs so your IDE can provide hints and inspections for the
          // other fields on this object.
          group = sanitizeName("internal-${ctx.branch}-${ctx.title}")
          materials {
            add((ctx.repo as GitMaterial).dup { // dup() modifies a deep-copy; the explicit cast helps the IDE with auto-complete
              // When configuring multiple materials, each must specify a distinct destination dir
              // to clone into.
              destination = "main-repo"

              // Notify the upstream GitLab repo of the build status
              notifiesBy(ctx.provider)
            })

            // We can also notify on additional materials to any supported provider. Here,
            // we are simply defining a second git material and notifying the corresponding
            // GitLab repo.
            git("security") {
              url = "https://gitlab.com/gocd/not-a-real-repo"
              destination = "security"

              notifiesGitLabAt { // Configuring notifications to GitLab is simple
                fullRepoName = "gocd/not-a-real-repo"
                apiAuthToken = lookup("my.oauth.token")
              }
            }
          }
          stages { stage("tests") {
            jobs { job("units") { tasks {
              bash { commandString = 'yarn run tests'; workingDir = "main-repo" }
            } } }
          } }
        }
      }
    }
  }
}
