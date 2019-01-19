/*
 * Copyright 2019 ThoughtWorks, Inc.
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

package cd.go.contrib.plugins.configrepo.groovy.requests


import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines
import org.junit.jupiter.api.Test

import static org.assertj.core.api.Assertions.assertThat

class PipelineExportRequestTest {

  @Test
  void "should deserialize"() {
    def json = '{"pipeline":{"group":"first","name":"up42","label_template":"${COUNT}","lock_behavior":"none","environment_variables":[],"parameters":[],"materials":[{"url":"test-repo","branch":"master","shallow_clone":false,"filter":{"ignore":[],"whitelist":[]},"auto_update":true,"type":"git"}],"stages":[{"name":"up42_stage","fetch_materials":true,"never_cleanup_artifacts":false,"clean_working_directory":false,"approval":{"type":"success","users":[],"roles":[]},"environment_variables":[],"jobs":[{"name":"up42_job","environment_variables":[],"tabs":[],"resources":[],"artifacts":[],"properties":[],"run_instance_count":"0","timeout":0,"tasks":[{"command":"ls","timeout":-1,"arguments":[],"run_if":"passed","type":"exec"}]}]}]}}'
    assertThat(PipelineExportRequest.fromJSON(json).pipeline).isEqualTo(new Pipelines().pipeline("up42", {
      labelTemplate = '${COUNT}'
      lockBehavior = 'none'
      group = 'first'
      materials {
        git {
          url = 'test-repo'
        }
      }
      stages {
        stage('up42_stage') {

        }
      }
    }))
  }
}
