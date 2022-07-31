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

      // This block is for Bitbucket (cloud); for Self-hosted Bitbucket,
      // see the section below.
      from = bitbucket {
        fullRepoName = "the-best/repo-ever"

        // OPTIONAL for public repos, but recommended.
        apiUser = "readonly-api-user"

        // lookup() allows one to provide secret/secure values to the script.
        // These arbitrary key+values are configurable from the UI on the GoCD server.
        //
        // lookup() is resolvable/usable anywhere in config script block.
        apiPass = lookup("my.oauth.token")

        // OPTIONAL for public repos
        materialUsername = "readonly-ci-user"
        materialPassword = lookup("ci.readonly.password")
      }

      onMatch { ctx ->
        // Build your entire workflow; you can have many pipeline blocks here.
        pipeline("build-PR-${ctx.branchSanitized}") {
          // `sanitizeName()` will sanitize strings for use as GoCD identifiers for group, pipeline,
          // stage, job, etc.
          //
          // NOTE: The `ctx` context binding object has various useful metadata about the
          // matched git ref. Download this plugin's related dsl.jar and configure it as a dependency
          // when writing your configs so your IDE can provide hints and inspections for the
          // other fields on this object.
          group = sanitizeName("app-${ctx.branch}-${ctx.title}")

          // As a convenience, a preconfigured material pointing to the pull request
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
          // Here, we notify Bitbucket of the build status; `ctx.provider` is preconfigured
          // to direct notifications back to the same provider and repository used to
          // enumerate pull requests.
          //
          // For further customization, see the example at the bottom of this file.
          ctx.repo.notifiesBy(ctx.provider)
        }

        pipeline("deploy-experimental-pr-${ctx.branchSanitized}") {
          group = sanitizeName("app-${ctx.branch}-${ctx.title}")
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
      // NOTE: Self-hosted Bitbucket has a differently named configuration block with
      // different options. All of these are required.
      from = bitbucketSelfHosted {
        fullRepoName = "something/internally-hosted"
        serverBaseUrl = "https://my-hosted-server:8443/bucket"
        apiAuthToken = lookup("my.oauth.secret")
      }

      onMatch { ctx ->
        pipeline("yet-another-pr-${ctx.branchSanitized}") {
          group = sanitizeName("internal-${ctx.branch}-${ctx.title}")
          materials {
            add((ctx.repo as GitMaterial).dup { // dup() modifies a deep-copy; the explicit cast helps the IDE with auto-complete
              // When configuring multiple materials, each must specify a distinct destination dir
              // to clone into.
              destination = "main-repo"

              // Notify the upstream Bitbucket repo of the build status
              notifiesBy(ctx.provider)
            })

            // We can also notify on additional materials to any supported provider. Here,
            // we are simply defining a second git material and notifying the corresponding
            // Bitbucket repo.
            git("security") {
              url = "https://bitbucket.org/gocd/not-a-real-repo"
              destination = "security"

              notifiesBitbucketAt { // Configuring notifications to Bitbucket is simple
                fullRepoName = "gocd/not-a-real-repo"
                apiUser = "readonly-api-user"
                apiPass = lookup("my.oauth.token")
              }
            }

            // Self-hosted Bitbucket repos can be notified via the `notifiesBitbucketSelfHostedAt()`
            // method on the git material.
            git("other") {
              url = "https://my-hosted-server:8443/bucket/something/else-entirely"
              destination = "other"

              notifiesBitbucketSelfHostedAt {
                fullRepoName = "something/internally-hosted"
                serverBaseUrl = "https://my-hosted-server:8443/bucket"
                apiAuthToken = lookup("my.oauth.secret")
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
