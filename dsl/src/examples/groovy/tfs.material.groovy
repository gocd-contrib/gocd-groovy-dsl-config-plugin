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

import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines

new Pipelines().with {
  pipeline('docs-website') {
    materials {
      tfs {
        url = 'http://10.21.3.210:8080/tfs'
        project = 'MyProject'
        domain = 'MyDomain'
        username = 'bob'
        encryptedPassword = 'aSdiFgRRZ6A='
      }
    }
  }
}
