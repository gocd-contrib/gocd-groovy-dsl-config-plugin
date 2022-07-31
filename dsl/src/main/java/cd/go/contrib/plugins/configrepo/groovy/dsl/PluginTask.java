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

package cd.go.contrib.plugins.configrepo.groovy.dsl;

import com.fasterxml.jackson.annotation.*;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static groovy.lang.Closure.DELEGATE_ONLY;
import static lombok.AccessLevel.NONE;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PluginTask extends Task<PluginTask> {

    @JsonIgnore
    private Map<String, String> options = new LinkedHashMap<>();

    @JsonIgnore
    private Map<String, String> secureOptions = new LinkedHashMap<>();

    @Getter(NONE)
    @Setter(NONE)
    @JsonProperty("plugin_configuration")
    @NotNull
    @Valid
    private Configuration configuration = new Configuration();

    public PluginTask() {
        this(null);
    }

    public PluginTask(@DelegatesTo(value = PluginTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.PluginTask") Closure cl) {
        super("plugin");
        configure(cl);
    }

    public Configuration configuration(@DelegatesTo(value = Configuration.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.Configuration") Closure cl) {
        configuration.configure(cl);
        return configuration;
    }

    @JsonGetter("configuration")
    // by default we ignore "empty" values. However, it skips rendering of empty environment vars. So we override
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.ALWAYS)
    @SuppressWarnings("unused" /*method here for deserialization only*/)
    private List<Map<String, String>> getAllConfiguration() {
        ArrayList<Map<String, String>> allVariables = new ArrayList<>();

        if (options != null) {
            options.forEach((k, v) -> {
                LinkedHashMap<String, String> var = new LinkedHashMap<>();
                var.put("key", k);
                var.put("value", v);
                allVariables.add(var);
            });
        }

        if (secureOptions != null) {
            secureOptions.forEach((k, v) -> {
                LinkedHashMap<String, String> var = new LinkedHashMap<>();
                var.put("key", k);
                var.put("encrypted_value", v);
                allVariables.add(var);
            });
        }

        return allVariables;
    }

    @JsonSetter("configuration")
    @SuppressWarnings("unused" /*method here for serialization only*/)
    private void setAllConfiguration(List<Map<String, String>> allVariables) {
        if (allVariables == null) {
            return;
        }

        allVariables.forEach(var -> {
            if (var.containsKey("encrypted_value")) {
                secureOptions.put(var.get("key"), var.get("encrypted_value"));
            } else {
                options.put(var.get("key"), var.get("value"));
            }
        });
    }

    public void configurations(List<Map<String, String>> allConfigs) {
        setAllConfiguration(allConfigs);
    }

    public void configurationValue(Configuration config) {
        this.configuration = config;
    }
}
