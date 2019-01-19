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

package cd.go.contrib.plugins.configrepo.groovy.dsl.json;

import cd.go.contrib.plugins.configrepo.groovy.dsl.Node;
import cd.go.contrib.plugins.configrepo.groovy.dsl.TestBase;
import cd.go.contrib.plugins.configrepo.groovy.sandbox.GroovyScriptRunner;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class JsonDeserializeTest extends TestBase {

    @ParameterizedTest
    @MethodSource("values")
    void testTransformRoundJson(String path) throws IOException, ResourceException, ScriptException {
        GroovyScriptRunner engine = getRunner();
        Object result = engine.runScript(path + ".groovy");
        AtomicReference<Set<ConstraintViolation<Object>>> constraintViolations = new AtomicReference<>();
        Consumer<Set<ConstraintViolation<Object>>> consumer = errors -> constraintViolations.set(errors);
        validate(result, consumer);

        assertThat(constraintViolations.get()).isNullOrEmpty();
        assertThat(result).isInstanceOf(Node.class);

        String actualJson = GoCDJsonSerializer.toJsonString(result);
        Object object = GoCDJsonSerializer.fromJson(actualJson, result.getClass());
        assertThat(object).isExactlyInstanceOf(result.getClass());
        assertThat(object).isEqualTo(result);
    }

}
