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

package cd.go.contrib.plugins.configrepo.groovy.requests

import groovy.json.JsonOutput
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.ThrowingSupplier

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow
import static org.junit.jupiter.api.Assertions.assertEquals

class StageNotificationsRequestTest {
  @Test
  void deserialization() {
    StageNotificationsRequest req = assertDoesNotThrow({ ->
      StageNotificationsRequest.fromJSON JsonOutput.toJson([
        pipeline: [
          name         : 'pipe', counter: '2',
          stage        : [name: 'stage', counter: '1', state: 'Building', result: 'Unknown'],
          'build-cause': [
            [
              material     : [
                type               : 'git',
                'git-configuration': [url: 'https://gitsnub.com/foo/bar', branch: 'baz']
              ],
              modifications: [[revision: '1234']]
            ]
          ]
        ]
      ])
    } as ThrowingSupplier<StageNotificationsRequest>)

    assertEquals('pipe', req.pipelineName())
    assertEquals('2', req.pipelineCounter())
    assertEquals('stage', req.stageName())
    assertEquals('1', req.stageCounter())
    assertEquals('Building', req.state())
    assertEquals('Unknown', req.result())
    assertEquals(0, req.buildCausesOfType('tfs').size())
    assertEquals(['1234'], req.buildCausesOfType('git').collect { c -> c.revision() })
  }
}
