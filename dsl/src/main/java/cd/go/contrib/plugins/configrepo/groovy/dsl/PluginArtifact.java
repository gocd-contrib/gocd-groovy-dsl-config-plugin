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

import cd.go.contrib.plugins.configrepo.groovy.dsl.util.KeyValuePairSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

import static groovy.lang.Closure.DELEGATE_ONLY;

/**
 * Represents an artifact managed by a plugin.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class PluginArtifact extends AbstractArtifact<PluginArtifact> {

    /**
     * The identifier of the plugin artifact.
     */
    @Expose
    @SerializedName("id")
    @NotEmpty
    private String id;

    /**
     * The identifier of the artifact store
     */
    @Expose
    @SerializedName("store_id")
    @NotEmpty
    private String storeId;

    /**
     * The configuration properties of this plugin artifact.
     *
     * @see #secureConfiguration
     */
    private Map<String, String> configuration;

    /**
     * The secure configuration properties of this plugin artifact.
     *
     * @see #configuration
     */
    private Map<String, String> secureConfiguration;

    PluginArtifact(@DelegatesTo(value = PluginArtifact.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.PluginArtifact") Closure cl) {
        super("external");
        configure(cl);
    }

    @Override
    public JsonElement toJson() {
        JsonObject jsonObject = (JsonObject) super.toJson();
        return KeyValuePairSerializer.serializePluginArtifactConfigurationInto(jsonObject, getConfiguration(), getSecureConfiguration());
    }

}
