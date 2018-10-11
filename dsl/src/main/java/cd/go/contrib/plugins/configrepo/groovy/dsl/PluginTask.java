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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static groovy.lang.Closure.DELEGATE_ONLY;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class PluginTask extends Task<PluginTask> {

    private Map<String, String> options;

    private Map<String, String> secureOptions;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Expose
    @SerializedName("plugin_configuration")
    @NotNull
    @Valid
    private Configuration configuration;



    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @Expose
    @SerializedName("artifact_origin")
    private final String artifactOrigin = "external";

    public PluginTask() {
        this(null);
    }

    public PluginTask(@DelegatesTo(value = PluginTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.PluginTask") Closure cl) {
        super("plugin");
        configure(cl);
    }

    public Configuration configuration(@DelegatesTo(value = Configuration.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.Configuration") Closure cl) {
        configuration = new Configuration();
        configuration.configure(cl);
        return configuration;
    }

    @Override
    public JsonElement toJson() {
        JsonObject jsonObject = (JsonObject) super.toJson();
        JsonArray configuration = new JsonArray();
        if (this.options != null && !this.options.isEmpty()) {
            options.forEach((k, v) -> {
                JsonObject property = new JsonObject();
                property.addProperty("key", k);
                property.addProperty("value", String.valueOf((Object) v));
                configuration.add(property);
            });
        }

        if (this.secureOptions != null && !this.secureOptions.isEmpty()) {
            secureOptions.forEach((k, v) -> {
                JsonObject property = new JsonObject();
                property.addProperty("key", k);
                property.addProperty("encrypted_value", String.valueOf((Object) v));
                configuration.add(property);
            });
        }

        if (configuration.size() > 0) {
            jsonObject.add("configuration", configuration);
        }
        return jsonObject;
    }
}
