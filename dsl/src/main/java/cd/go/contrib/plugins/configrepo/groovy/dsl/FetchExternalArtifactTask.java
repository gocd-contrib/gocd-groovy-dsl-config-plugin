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

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FetchExternalArtifactTask extends AbstractFetchArtifactTask {

    @JsonProperty("artifact_id")
    @NotEmpty
    private String artifactId;

    @JsonIgnore
    private Map<String, String> configuration = new LinkedHashMap<>();

    public FetchExternalArtifactTask() {
        this(null);
    }

    public FetchExternalArtifactTask(@DelegatesTo(value = FetchExternalArtifactTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.FetchExternalArtifactTask") Closure cl) {
        super();
        this.artifactOrigin = "external";
        configure(cl);
    }

    @JsonGetter("configuration")
    // by default we ignore "empty" values. However, it skips rendering of empty configuration. So we override
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.ALWAYS)
    @SuppressWarnings("unused" /*method here for deserialization only*/)
    List<Map<String, String>> getAllConfigurations() {
        ArrayList<Map<String, String>> allConfigs = new ArrayList<>();

        if (configuration != null) {
            configuration.forEach((k, v) -> {
                LinkedHashMap<String, String> var = new LinkedHashMap<>();
                var.put("key", k);
                var.put("value", v);
                allConfigs.add(var);
            });
        }

        return allConfigs;
    }

    @JsonSetter("configuration")
    @SuppressWarnings("unused" /*method here for serialization only*/)
    void setAllConfigurations(List<Map<String, String>> allConfigs) {
        if (allConfigs == null) {
            return;
        }

        allConfigs.forEach(var -> configuration.put(var.get("key"), var.get("value")));
    }

    public void configurationValues(List<Map<String, String>> allConfigs) {
        setAllConfigurations(allConfigs);
    }

}
