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

package cd.go.contrib.plugins.configrepo.groovy.dsl;

import cd.go.contrib.plugins.configrepo.groovy.sandbox.GroovyScriptRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class TestBase {

    GroovyScriptRunner runner;

    protected static void validate(Object object, Consumer<Set<ConstraintViolation<Object>>> errorHandler) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            errorHandler.accept(violations);
        }
    }

    protected GroovyScriptRunner getRunner() throws IOException {
        if (runner == null) {
            runner = new GroovyScriptRunner(".", Pipeline.class.getPackage().getName());
        }
        return runner;
    }

    protected static Stream<String> values() throws IOException {
        return Files
                .walk(Paths.get("..", "json", "src", "test", "resources"))
                .filter((it) -> Files.isRegularFile(it) && it.toFile().getName().endsWith(".groovy"))
                .map(it -> it.toString().replaceAll("\\.groovy$", ""));
    }

}
