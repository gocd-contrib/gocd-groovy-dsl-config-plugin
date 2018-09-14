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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.Map;

import static groovy.lang.Closure.DELEGATE_ONLY;

/**
 * Represents a stage.
 * <p>
 * {@includeCode stage-with-jobs.groovy}
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Stage extends NamedNode<Stage> {

    /**
     * If set, performs material updates or checkouts on the agent. Defaults to {@code true}.
     */
    @Expose
    @SerializedName("fetch_materials")
    private Boolean fetchMaterials;

    /**
     * If set, this flag will remove all files/directories in the working directory on the agent, before the job starts.
     * Defaults to {@code false}.
     */
    @Expose
    @SerializedName("clean_working_directory")
    private Boolean cleanWorkingDir;

    /**
     * If set, never cleanup artifacts for this stage, if purging artifacts is configured at the server level.
     * Defaults to {@code false}.
     */
    @Expose
    @SerializedName("never_cleanup_artifacts")
    private Boolean artifactCleanupProhibited;

    /**
     * Specifies how the stage is approved.
     */
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    @Expose
    @SerializedName("approval")
    @Valid
    private Approval approval;

    /**
     * The list of environment variables associated with this stage.
     * <p>
     * {@includeCode plain-environment-variables.groovy }
     *
     * @see #secureEnvironmentVariables
     */
    private Map<String, String> environmentVariables;

    /**
     * The list of secure(encrypted) environment variables associated with this stage.
     * <p>
     * {@includeCode secure-environment-variables.groovy }
     *
     * @see #environmentVariables
     * @see <a href='https://api.gocd.org/current/#encrypt-a-plain-text-value'>Encryption API</a>
     */
    private Map<String, String> secureEnvironmentVariables;

    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    @Expose
    @SerializedName("jobs")
    @Valid
    private Jobs jobs = new Jobs();

    public Stage(String name, @DelegatesTo(value = Stage.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.Stage") Closure cl) {
        super(name);
        configure(cl);
    }

    public Stage(String name) {
        super(name);
    }

    public Stage() {
    }

    public Jobs jobs(@DelegatesTo(value = Jobs.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.Jobs") Closure cl) {
        jobs.configure(cl);
        return jobs;
    }

    public Approval approval(@DelegatesTo(value = Approval.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.Approval") Closure cl) {
        approval = new Approval();
        approval.configure(cl);
        return approval;
    }

    @Override
    public JsonElement toJson() {
        return KeyValuePairSerializer.serializeVariablesInto((JsonObject) super.toJson(), getEnvironmentVariables(), getSecureEnvironmentVariables());
    }

}
