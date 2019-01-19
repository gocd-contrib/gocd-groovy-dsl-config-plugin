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

package cd.go.contrib.plugins.configrepo.groovy.export

import cd.go.contrib.plugins.configrepo.groovy.dsl.Node
import cd.go.contrib.plugins.configrepo.groovy.dsl.TestBase
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import static org.assertj.core.api.Assertions.assertThat

class GroovyExporterTest extends TestBase {
  @ParameterizedTest
  @MethodSource("values")
  void name(String path) {
    def engine = getRunner()
    Node result = (Node) engine.runScript(path + '.groovy')
    assertThat(result).isInstanceOf(Node)

    def writer = new StringWriter()
    def exporter = new GroovyExporter(writer, true)
    exporter.export(result)

    assertThat(sourceFile(path).trim())
      .endsWith(writer.toString().trim())
  }

  private String sourceFile(String path) {
    def writer = new StringWriter()
    writer << new File(path + '.groovy').filterLine({ String line ->
      !(line.startsWith("package ") || line.startsWith("import "))
    })
    writer.toString()
  }
}
