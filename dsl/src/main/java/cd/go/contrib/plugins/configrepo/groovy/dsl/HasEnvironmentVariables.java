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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class HasEnvironmentVariables<T extends HasEnvironmentVariables> extends NamedNode<T> {

    public HasEnvironmentVariables(String name) {
        super(name);
    }

    public HasEnvironmentVariables() {
        super();
    }

    /**
     * The list of environment variables associated with this environment.
     * <p>
     * {@includeCode plain-environment-variables.groovy }
     *
     * @see #secureEnvironmentVariables
     */
    @JsonIgnore
    private Map<String, String> environmentVariables = new LinkedHashMap<>();

    /**
     * The list of secure(encrypted) environment variables associated with this environment.
     * <p>
     * {@includeCode secure-environment-variables.groovy }
     *
     * @see #environmentVariables
     * @see <a href='https://api.gocd.org/current/#encrypt-a-plain-text-value'>Encryption API</a>
     */
    @JsonIgnore
    private Map<String, String> secureEnvironmentVariables = new LinkedHashMap<>();

    @JsonGetter("environment_variables")
    // by default we ignore "empty" values. However, it skips rendering of empty environment vars. So we override
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.ALWAYS)
    @SuppressWarnings("unused" /*method here for deserialization only*/)
    private List<Map<String, String>> getAllVariables() {
        ArrayList<Map<String, String>> allVariables = new ArrayList<>();

        if (environmentVariables != null) {
            environmentVariables.forEach((k, v) -> {
                LinkedHashMap<String, String> var = new LinkedHashMap<>();
                var.put("name", k);
                var.put("value", v);
                allVariables.add(var);
            });
        }

        if (secureEnvironmentVariables != null) {
            secureEnvironmentVariables.forEach((k, v) -> {
                LinkedHashMap<String, String> var = new LinkedHashMap<>();
                var.put("name", k);
                var.put("encrypted_value", v);
                allVariables.add(var);
            });
        }

        return allVariables;
    }

    @JsonSetter("environment_variables")
    @SuppressWarnings("unused" /*method here for serialization only*/)
    private void setAllVariables(List<Map<String, String>> allVariables) {
        if (allVariables == null) {
            return;
        }

        allVariables.forEach(var -> {
            if (var.containsKey("encrypted_value")) {
                secureEnvironmentVariables.put(var.get("name"), var.get("encrypted_value"));
            } else {
                environmentVariables.put(var.get("name"), var.get("value"));
            }
        });
    }
}

