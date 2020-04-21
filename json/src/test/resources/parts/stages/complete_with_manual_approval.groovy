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

package parts.stages

import cd.go.contrib.plugins.configrepo.groovy.dsl.Stages

return new Stages().stage('test') {
  artifactCleanupProhibited = true
  cleanWorkingDir = true
  environmentVariables = [
    TEST_NUM: '1',
  ]
  fetchMaterials = true
  secureEnvironmentVariables = [
    PASSWORD: 'AES:rzIrGQcbIX9e/J5Ic0WHcA==:nam+Ne5mUtS0TILGu4zHlw==',
  ]
  approval {
    allowOnlyOnSuccess = true
    roles = ['manager']
    type = 'manual'
    users = ['john']
  }
  jobs {
    job('one') {
    }
    job('two') {
    }
  }
}
