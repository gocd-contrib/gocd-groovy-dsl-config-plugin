/*
 * Copyright 2021 ThoughtWorks, Inc.
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

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static groovy.lang.Closure.DELEGATE_ONLY;

/**
 * Represents an artifact managed by a plugin.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PluginArtifact extends AbstractArtifact<PluginArtifact> {

    /**
     * The identifier of the plugin artifact.
     */
    @JsonProperty("id")
    @NotEmpty
    private String id;

    /**
     * The identifier of the artifact store
     */
    @JsonProperty("store_id")
    @NotEmpty
    private String storeId;

    /**
     * The configuration properties of this plugin artifact.
     *
     * @see #secureConfiguration
     */
    @JsonIgnore
    private Map<String, String> configuration = new LinkedHashMap<>();

    /**
     * The secure configuration properties of this plugin artifact.
     *
     * @see #configuration
     */
    @JsonIgnore
    private Map<String, String> secureConfiguration = new LinkedHashMap<>();

    @SuppressWarnings("unused" /*method here for serialization only*/)
    public PluginArtifact() {
        this(null);
    }

    public PluginArtifact(@DelegatesTo(value = PluginArtifact.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.PluginArtifact") Closure cl) {
        super("external");
        configure(cl);
    }

    @JsonGetter("configuration")
    // by default we ignore "empty" values. However, it skips rendering of empty environment vars. So we override
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    @SuppressWarnings("unused" /*method here for deserialization only*/)
    private List<Map<String, String>> getPluginConfiguration() {
        ArrayList<Map<String, String>> allVariables = new ArrayList<>();

        if (configuration != null) {
            configuration.forEach((k, v) -> {
                LinkedHashMap<String, String> var = new LinkedHashMap<>();
                var.put("key", k);
                var.put("value", v);
                allVariables.add(var);
            });
        }

        if (secureConfiguration != null) {
            secureConfiguration.forEach((k, v) -> {
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
    private void setPluginConfiguration(List<Map<String, String>> allVariables) {
        if (allVariables == null) {
            return;
        }

        allVariables.forEach(var -> {
            if (var.containsKey("encrypted_value")) {
                secureConfiguration.put(var.get("key"), var.get("encrypted_value"));
            } else {
                configuration.put(var.get("key"), var.get("value"));
            }
        });
    }

}
