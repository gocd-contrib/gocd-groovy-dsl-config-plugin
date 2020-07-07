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

package cd.go.contrib.plugins.configrepo.groovy.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import static java.lang.String.format;

public class Configurations {

    private static final String KEY = "key";

    private static final String VALUE = "value";

    private static final String ENCRYPTED_VALUE = "encrypted_value";

    private static final String USER_DEF_NAMESPACE = "userdef.";

    private final List<ConfigProperty> properties = new ArrayList<>();

    public Configurations(List<Map<String, String>> properties) {
        properties.forEach(map -> {
            if (map.containsKey(ENCRYPTED_VALUE)) {
                this.properties.add(new Encrypted(map.get(KEY), map.get(ENCRYPTED_VALUE)));
            } else {
                this.properties.add(new PlainText(map.get(KEY), map.get(VALUE)));
            }
        });
    }

    public String userDefinedValueOf(final String key) {
        return valueOf(USER_DEF_NAMESPACE + key);
    }

    public String valueOf(final String key) {
        final ConfigProperty value = findFirst(v -> v.is(key));
        if (null == value) {
            throw new NoSuchElementException(format("No configuration value found for key `%s`", key));
        }
        return value.value();
    }

    @SuppressWarnings("unchecked")
    private <T extends ConfigProperty> T findFirst(Predicate<ConfigProperty> pred) {
        return (T) properties.stream().filter(pred).findFirst().orElse(null);
    }
}
