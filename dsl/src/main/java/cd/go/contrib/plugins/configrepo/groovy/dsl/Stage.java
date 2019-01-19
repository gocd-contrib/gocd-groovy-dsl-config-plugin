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

import com.fasterxml.jackson.annotation.JsonProperty;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;

import static groovy.lang.Closure.DELEGATE_ONLY;
import static lombok.AccessLevel.NONE;

/**
 * Represents a stage.
 * <p>
 * {@includeCode stage-with-jobs.groovy}
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Stage extends HasEnvironmentVariables<Stage> {

    /**
     * If set, performs material updates or checkouts on the agent. Defaults to {@code true}.
     */
    @JsonProperty("fetch_materials")
    private Boolean fetchMaterials;

    /**
     * If set, this flag will remove all files/directories in the working directory on the agent, before the job starts.
     * Defaults to {@code false}.
     */
    @JsonProperty("clean_working_directory")
    private Boolean cleanWorkingDir;

    /**
     * If set, never cleanup artifacts for this stage, if purging artifacts is configured at the server level.
     * Defaults to {@code false}.
     */
    @JsonProperty("never_cleanup_artifacts")
    private Boolean artifactCleanupProhibited;

    /**
     * Specifies how the stage is approved.
     */
    @Getter(value = NONE)
    @Setter(value = NONE)
    @JsonProperty("approval")
    @Valid
    private Approval approval;

    @Getter(value = NONE)
    @Setter(value = NONE)
    @JsonProperty("jobs")
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
        super();
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

}
