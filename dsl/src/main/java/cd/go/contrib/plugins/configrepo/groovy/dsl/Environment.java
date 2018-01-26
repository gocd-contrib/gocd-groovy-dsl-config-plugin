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

import java.util.List;
import java.util.Map;

import static groovy.lang.Closure.DELEGATE_ONLY;

/**
 * Represents an environment in GoCD.
 * <p>
 * {@includeCode environments-full.groovy}
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Environment extends NamedNode<Environment> {

    /**
     * The list of environment variables associated with this environment.
     * <p>
     * {@includeCode plain-environment-variables.groovy }
     *
     * @see #secureVariables
     */
    private Map<String, String> environmentVariables;

    /**
     * The list of secure(encrypted) environment variables associated with this environment.
     * <p>
     * {@includeCode secure-environment-variables.groovy }
     *
     * @see #environmentVariables
     * @see <a href='https://api.gocd.org/current/#encrypt-a-plain-text-value'>Encryption API</a>
     */
    private Map<String, String> secureVariables;

    /**
     * The list of pipelines that should be added into this environment.
     */
    @Expose
    @SerializedName("pipelines")
    private List<String> pipelines;

    /**
     * The list of agents that should be added into this environment.
     */
    @Expose
    @SerializedName("agents")
    private List<String> agents;

    Environment() {
        this(null, null);
    }

    Environment(String name) {
        this(name, null);
    }

    Environment(String name, @DelegatesTo(value = Environment.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.Environment") Closure cl) {
        super(name);
        configure(cl);
    }

    @Override
    public JsonElement toJson() {
        return KeyValuePairSerializer.serializeVariablesInto((JsonObject) super.toJson(), getEnvironmentVariables(), getSecureVariables());
    }
}
