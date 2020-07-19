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

package cd.go.contrib.plugins.configrepo.groovy.dsl.strategies;

import cd.go.contrib.plugins.configrepo.groovy.dsl.BranchContext;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.ConnectionConfig;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.Type;
import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.ThrowingRunnable;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class BranchStrategy {

    private static final ThreadLocal<RefResolver> resolver = ThreadLocal.withInitial(() -> (s, f) -> Collections.emptyList());

    public static void with(RefResolver fn, ThrowingRunnable body) throws Throwable {
        resolver.set(fn);
        try {
            body.run();
        } finally {
            resolver.remove();
        }
    }

    private final Attributes<? extends ConnectionConfig> attrs;

    public BranchStrategy(Attributes<? extends ConnectionConfig> attrs) {
        this.attrs = attrs;
    }

    @JsonProperty("type")
    public Type type() {
        return attrs.type();
    }

    @JsonProperty("attributes")
    public Attributes<? extends ConnectionConfig> attrs() {
        return attrs;
    }

    public void validate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Set<ConstraintViolation<Attributes<? extends ConnectionConfig>>> errors = factory.getValidator().validate(attrs());

        if (errors.size() != 0) {
            throw invalidConfig(errors);
        }
    }

    private ValidationException invalidConfig(Set<ConstraintViolation<Attributes<? extends ConnectionConfig>>> errors) {
        return new ValidationException(format(
                "Invalid branch matching config block `%s {}`; please address the following:\n%s",
                type(),
                errors.stream().map(ConstraintViolation::getMessage).collect(joining(";\n"))
        ));
    }

    public List<BranchContext> fetch(Pattern filter) {
        return resolver.get().apply(this, filter);
    }
}
