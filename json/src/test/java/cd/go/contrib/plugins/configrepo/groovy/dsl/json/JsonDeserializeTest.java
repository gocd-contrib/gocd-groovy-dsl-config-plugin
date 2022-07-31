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

package cd.go.contrib.plugins.configrepo.groovy.dsl.json;

import cd.go.contrib.plugins.configrepo.groovy.dsl.Node;
import cd.go.contrib.plugins.configrepo.groovy.dsl.TestBase;
import cd.go.contrib.plugins.configrepo.groovy.util.GroovyScriptRunner;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import jakarta.validation.ConstraintViolation;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNullElse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonDeserializeTest extends TestBase {

    @ParameterizedTest
    @MethodSource("values")
    void testGroovyToJSON(String path) throws IOException {
        GroovyScriptRunner engine = getRunner();
        Object result = engine.runScript(path + ".groovy");
        assertTrue(result instanceof Node);

        String actualJson = GoCDJsonSerializer.toJsonString(result);
        String expectedJSON = ResourceGroovyMethods.getText(new File(path + ".json"), "utf-8");
        JsonFluentAssert.assertThatJson(actualJson).isEqualTo(expectedJSON);
    }

    @ParameterizedTest
    @MethodSource("values")
    void shouldValidate(String path) throws IOException {
        GroovyScriptRunner engine = getRunner();
        Object result = engine.runScript(path + ".groovy");
        AtomicReference<Set<ConstraintViolation<Object>>> constraintViolations = new AtomicReference<>();
        Consumer<Set<ConstraintViolation<Object>>> consumer = errors -> constraintViolations.set(errors);
        validate(result, consumer);

        assertTrue(requireNonNullElse(constraintViolations.get(), Collections.emptySet()).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("values")
    void testJSONToGroovy(String path) throws IOException {
        GroovyScriptRunner engine = getRunner();
        Object result = engine.runScript(path + ".groovy");

        String actualJson = ResourceGroovyMethods.getText(new File(path + ".json"), "utf-8");
        Object node = GoCDJsonSerializer.fromJson(actualJson, result.getClass());
        assertEquals(result.getClass(), node.getClass());
        assertEquals(result, node);
    }

}
