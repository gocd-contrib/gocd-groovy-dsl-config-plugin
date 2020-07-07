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

package cd.go.contrib.plugins.configrepo.groovy.dsl.mixins;

import java.util.NoSuchElementException;
import java.util.function.Function;

import static java.lang.String.format;

public class KeyVal {

    // During a request, the plugin will swap in an appropriate implementation for key retrieval
    private static final ThreadLocal<Function<String, String>> resolver = ThreadLocal.withInitial(() -> s -> format("value.for.%s", s));

    public static void with(Function<String, String> fn, ThrowingRunnable body) throws Throwable {
        resolver.set(fn);
        try {
            body.run();
        } finally {
            resolver.remove();
        }
    }

    private static String lookup(String key) {
        return resolver.get().apply(key);
    }

    private KeyVal() {
    }

    public interface Mixin {

        /**
         * Allows ConfigRepo Configuration Property lookup by key from within a Groovy config definition script
         *
         * @param key
         *         the Configuration Property name
         *
         * @return the value of the Configuration Property
         *
         * @throws java.util.NoSuchElementException
         *         if property is not found
         */
        default String lookup(String key) {
            return KeyVal.lookup(key);
        }

        /**
         * Allows ConfigRepo Configuration Property lookup by key from within a Groovy config definition script
         *
         * @param key
         *         the Configuration Property name
         * @param defaultValue
         *         the fallback value
         *
         * @return the value of the Configuration Property or the provided default when the property is not found
         */
        default String lookup(String key, String defaultValue) {
            try {
                return lookup(key);
            } catch (NoSuchElementException e) {
                return defaultValue;
            }
        }
    }
}
