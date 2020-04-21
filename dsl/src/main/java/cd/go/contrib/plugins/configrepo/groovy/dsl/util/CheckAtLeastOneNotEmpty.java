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
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = CheckAtLeastOneNotEmpty.CheckAtLeastOneNotNullValidator.class)
@Documented
public @interface CheckAtLeastOneNotEmpty {

    String message() default "{javax.validation.constraints.NotEmpty.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fieldNames();

    public static class CheckAtLeastOneNotNullValidator implements ConstraintValidator<CheckAtLeastOneNotEmpty, Object> {

        private String[] fieldNames;

        public void initialize(CheckAtLeastOneNotEmpty constraintAnnotation) {
            this.fieldNames = constraintAnnotation.fieldNames();
        }

        public boolean isValid(Object object, ConstraintValidatorContext constraintContext) {
            if (object == null) {
                return true;
            }

            try {
                for (String fieldName : fieldNames) {
                    Field field = object.getClass().getDeclaredField(fieldName);
                    boolean accessible = field.isAccessible();
                    try {
                        field.setAccessible(true);
                        String property = (String) field.get(object);
                        if (isNotBlank(property)) {
                            return true;
                        }
                    } finally {
                        field.setAccessible(accessible);
                    }
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }

        private boolean isNotBlank(String string) {
            return !isBlank(string);
        }

        private boolean isBlank(String string) {
            return string == null || string.trim().length() == 0;
        }
    }

}
