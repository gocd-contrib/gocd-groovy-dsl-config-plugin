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

package parts.jobs

import cd.go.contrib.plugins.configrepo.groovy.dsl.Jobs

return new Jobs().job('test') {
  environmentVariables = [
    '1varWhichIsNotJavaValidIdentifier': 'unknown',
    LD_LIBRARY_PATH                    : '.',
  ]
  resources = ['linux']
  runInstanceCount = 7
  timeout = 5
  artifacts {
    test {
      destination = 'dest'
      source = 'src'
    }
    build {
      source = 'bin'
    }
  }
  properties {
    property('perf') {
      source = 'test.xml'
      xpath = 'substring-before(//report/data/all/coverage[starts-with(@type,\'class\')]/@value, \'%\')'
    }
  }
  tabs {
    tab('test') {
      path = 'results.xml'
    }
  }
  tasks {
    exec {
      commandLine = ['make', '-j3', 'docs', 'install']
      runIf = 'any'
      workingDir = 'some-dir'
    }
    fetchArtifact {
      destination = 'test'
      file = true
      job = 'upstream_job'
      pipeline = 'upstream'
      runIf = 'any'
      source = 'result'
      stage = 'upstream_stage'
    }
    fetchExternalArtifact {
      artifactId = 'artifact_id'
      configuration = [
        abc: 'def',
        xyz: 'rst',
      ]
      job = 'upstream_job'
      pipeline = 'upstream'
      runIf = 'passed'
      stage = 'upstream_stage'
    }
    plugin {
      options = [
        ConverterType: 'jsunit',
      ]
      runIf = 'failed'
      secureOptions = [
        password: 'ssd#%fFS*!Esx',
      ]
      configuration {
        id = 'xunit.converter.task.plugin'
      }
    }
  }
}
