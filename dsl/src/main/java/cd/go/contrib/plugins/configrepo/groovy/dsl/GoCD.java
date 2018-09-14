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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static groovy.lang.Closure.DELEGATE_ONLY;

/**
 * Represents the top level GoCD configuration consisting of pipelines and environments.
 * <p>
 * {@includeCode big.examples.groovy}
 *
 * @see Pipeline
 * @see Environment
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class GoCD extends Node<GoCD> {

    @Expose
    @SerializedName("target_version")
    @Min(3)
    private Integer targetVersion = 3;

    /**
     * Container to define one or more pipelines in GoCD
     */
    @Expose
    @SerializedName("pipelines")
    @Valid
    private Pipelines pipelines = new Pipelines();

    /**
     * Container to define one or more environments in GoCD
     */
    @Expose
    @SerializedName("environments")
    @Valid
    private Environments environments = new Environments();

    public static GoCD script(@DelegatesTo(value = GoCD.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD") Closure cl) {
        GoCD gocd = new GoCD();
        gocd.configure(cl);
        return gocd;
    }

    public Pipelines pipelines(@DelegatesTo(value = Pipelines.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines") Closure cl) {
        pipelines.configure(cl);
        return pipelines;
    }

    public Environments environments(@DelegatesTo(value = Environments.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.Environments") Closure cl) {
        environments.configure(cl);
        return environments;
    }
}
