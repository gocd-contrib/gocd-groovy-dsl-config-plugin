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

package cd.go.contrib.plugins.configrepo.groovy.export;

import cd.go.contrib.plugins.configrepo.groovy.dsl.Node;
import cd.go.contrib.plugins.configrepo.groovy.dsl.TestBase;
import cd.go.contrib.plugins.configrepo.groovy.dsl.json.GoCDJsonSerializer;
import cd.go.contrib.plugins.configrepo.groovy.sandbox.GroovyScriptRunner;
import com.google.common.io.Files;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class GroovyExporterTest extends TestBase {

    @ParameterizedTest
    @MethodSource("values")
    void shouldExportNodeAsGroovyCode(String path) throws Throwable {
        GroovyScriptRunner engine = getRunner();
        Node result = (Node) engine.runScript(path + ".groovy");
        assertThat(result).isInstanceOf(Node.class);

        StringWriter writer = new StringWriter();
        GroovyExporter exporter = new GroovyExporter(writer);
        exporter.export(result);

        assertThat(sourceGroovyFile(path).trim())
                .endsWith(writer.toString().trim());
    }

    @ParameterizedTest
    @MethodSource("values")
    void shouldExportJsonStringAsGroovyCode(String path) throws Throwable {
        // load the node, so we know what type it is
        GroovyScriptRunner engine = getRunner();
        Node result = (Node) engine.runScript(path + ".groovy");
        assertThat(result).isInstanceOf(Node.class);

        // load the node from actual json
        String actualJSON = ResourceGroovyMethods.getText(new File(path + ".json"), "utf-8");
        Node node = GoCDJsonSerializer.fromJson(actualJSON, result.getClass());

        // serialize to groovy
        StringWriter writer = new StringWriter();
        GroovyExporter exporter = new GroovyExporter(writer);
        exporter.export(node);

        // should be same as groovy code
        assertThat(sourceGroovyFile(path).trim())
                .endsWith(writer.toString().trim());
    }

    private String sourceGroovyFile(String path) throws IOException {
        StringWriter writer = new StringWriter();
        List<String> strings = Files.readLines(new File(path + ".groovy"), UTF_8);
        String collect = strings.stream()
                .filter(line -> !(line.startsWith("package ") || line.startsWith("import ")))
                .collect(Collectors.joining("\n"));

        writer.write(collect);
        return writer.toString();
    }
}
