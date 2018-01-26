/*
 * Copyright 2018 ThoughtWorks, Inc.
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

package cd.go.contrib.plugins.configrepo.groovy.dsl

import net.javacrumbs.jsonunit.fluent.JsonFluentAssert
import org.junit.jupiter.api.Test

class ShellTaskTest {
  @Test
  void 'to json with script argument'() {
    def task = new ShellTask(shell: "foosh", commandString: 'scp file.tar.gz ${SSH_USER}@${SSH_HOST}:file.tar.gz')
    JsonFluentAssert.assertThatJson(task.toJson()).isEqualTo([
      type     : 'exec',
      command  : 'foosh',
      arguments: ['-c', 'scp file.tar.gz ${SSH_USER}@${SSH_HOST}:file.tar.gz']
    ])
  }

  @Test
  void 'to json with script argument and login'() {
    def task = new ShellTask(shell: "foosh", commandString: "nvm exec 4.2 yarn install", login: true)
    JsonFluentAssert.assertThatJson(task.toJson()).isEqualTo([
      type     : 'exec',
      command  : 'foosh',
      arguments: ['-l', '-c', 'nvm exec 4.2 yarn install']

    ])
  }

  @Test
  void 'to json with file argument'() {
    def task = new ShellTask(shell: "foosh", file: "build.sh")
    JsonFluentAssert.assertThatJson(task.toJson()).isEqualTo([
      type     : 'exec',
      command  : 'foosh',
      arguments: ['build.sh']
    ])
  }

  @Test
  void 'to json with file argument and login shell'() {
    def task = new ShellTask(shell: "foosh", file: "build.sh", login: true)
    JsonFluentAssert.assertThatJson(task.toJson()).isEqualTo([
      type     : 'exec',
      command  : 'foosh',
      arguments: ['-l', 'build.sh']
    ])
  }
}
