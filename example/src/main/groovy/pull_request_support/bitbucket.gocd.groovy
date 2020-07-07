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
          group = "main"

          // As a convenience, a preconfigured material pointing to the pull request
          // is available in the template binding context. Of course, one may modify
          // the material or choose to not use it and manually configure a material.
          materials { add(ctx.repo) }
          stages { stage('tests') {
            jobs {job('units') { tasks {
              bash { commandString = 'npm run tests' }
            } } }
          } }
        }

        pipeline("deploy-experimental-pr-${ctx.branchSanitized}") {
          group = "main"
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
      from = bitbucketServer {
        fullRepoName = "something/internally-hosted"
        serverBaseUrl = "https://my-hosted-server:8443/bucket"
        apiAuthToken = lookup("my.oauth.secret")
      }

      onMatch { ctx ->
        pipeline("yet-another-pr-${ctx.branchSanitized}") {
          group = "main"
          materials { add(ctx.repo) }
          stages { stage("tests") {
            jobs { job("units") { tasks {
              bash { commandString = 'yarn run tests' }
            } } }
          } }
        }
      }
    }
  }
}
