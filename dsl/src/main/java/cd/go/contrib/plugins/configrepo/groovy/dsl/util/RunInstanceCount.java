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

package cd.go.contrib.plugins.configrepo.groovy.dsl.util;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = RunInstanceCount.CheckRunInstanceCount.class)
@Documented
public @interface RunInstanceCount {

    String message() default "{jakarta.validation.constraints.Positive.message} or must be the string 'all'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class CheckRunInstanceCount implements ConstraintValidator<RunInstanceCount, Object> {

        public void initialize(RunInstanceCount constraint) {
        }

        public boolean isValid(Object obj, ConstraintValidatorContext context) {
            if (obj == null) {
                return true;
            }
            if ("all".equals(obj)) {
                return true;
            }
            if (obj instanceof String) {
                try {
                    return Integer.valueOf((String) obj) > 0;
                } catch (NumberFormatException ignore) {
                }
            }

            if (obj instanceof Integer) {
                return ((Integer) obj) > 0;
            }
            return false;
        }

    }
}
