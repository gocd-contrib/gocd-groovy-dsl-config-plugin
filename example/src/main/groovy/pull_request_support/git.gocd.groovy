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

import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD

GoCD.script {
  branches {
    matching {
      // OPTIONAL: defaults to match any string. When present,
      // this will filter/restrict git refs to those that match
      // the provided pattern.
      //
      // With vanilla git, you most likely will need this.
      pattern = ~/^refs\/heads\/feature-.+/

      from = git {
        // This can be any URL supported by the `git clone` command, including
        // local files, ssh, etc.
        //
        // You can interpolate lookup() in the URL to handle basic authentication
        // or through some other means on your GoCD agent (ssh keys, .netrc, etc.)
        url = "https://my.private.server/my-repo.git"

        // OPTIONAL, depending on your git server
        materialUsername = "readonly-ci-user"
        materialPassword = lookup("github.readonly.password")
      }

      onMatch { ctx ->
        // Build your entire workflow; you can have many pipeline blocks here.
        pipeline("build-branch-${ctx.branchSanitized}") {
          group = sanitizeName("feature-branch-${ctx.branch}")

          // As a convenience, a preconfigured material pointing to the pull request
          // is available in the template binding context. Of course, one may modify
          // the material or choose to not use it and manually configure a material.
          materials { add(ctx.repo) }
          stages { stage('tests') {
            jobs { job('units') { tasks {
              bash { commandString = 'npm run tests' }
            } } }
          } }
        }

        pipeline("deploy-experimental-branch-${ctx.branchSanitized}") {
          group = sanitizeName("feature-branch-${ctx.branch}")
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
      pattern = ~/^refs\/tags\/stable-.+/

      from = git {
        url = "/local/file/my-repo.git"
      }

      onMatch { ctx ->
        pipeline("yet-another-release-${ctx.branchSanitized}") {
          group = sanitizeName("release-${ctx.branch}")
          materials { add(ctx.repo) }
          stages { stage("publish") {
            jobs { job("publish") { tasks {
              bash { commandString = 'yarn run deploy' }
            } } }
          } }
        }
      }
    }
  }
}
