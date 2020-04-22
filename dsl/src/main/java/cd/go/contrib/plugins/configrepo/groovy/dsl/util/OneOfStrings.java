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

package cd.go.contrib.plugins.configrepo.groovy.dsl.util;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = OneOfStrings.OneOfStringsValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
public @interface OneOfStrings {

    String message() default "must be one of '{value}'";

    boolean nullable() default true;

    String[] value() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class OneOfStringsValidator implements ConstraintValidator<OneOfStrings, String> {

        private String[] allowedValues;

        private boolean nullable;

        @Override
        public void initialize(OneOfStrings annotation) {
            this.allowedValues = annotation.value();
            this.nullable = annotation.nullable();
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null & nullable) {
                return true;
            }
            for (String allowedValue : allowedValues) {
                if (allowedValue.equals(value)) {
                    return true;
                }
            }

            return false;
        }
    }

}
