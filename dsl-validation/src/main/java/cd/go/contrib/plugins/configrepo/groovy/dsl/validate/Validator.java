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

package cd.go.contrib.plugins.configrepo.groovy.dsl.validate;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.function.Consumer;

public class Validator {

    public static <T> void validate(T node, Consumer<Set<ConstraintViolation<T>>> errorHandler) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Set<ConstraintViolation<T>> errors = factory.getValidator().validate(node);

        if (!errors.isEmpty()) {
            errorHandler.accept(errors);
        }
    }
}
