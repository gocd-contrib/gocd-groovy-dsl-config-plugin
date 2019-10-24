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

import javax.validation.constraints.NotEmpty;

import static groovy.lang.Closure.DELEGATE_ONLY;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DependencyMaterial extends Material<DependencyMaterial> {

    @JsonProperty("pipeline")
    @NotEmpty
    private String pipeline;

    @JsonProperty("stage")
    @NotEmpty
    private String stage;

    /**
     * When set to true, the pipeline will not be automatically scheduled for changes to this material
     * <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#pipeline-1">More info</a>.
     */
    @JsonProperty("ignore_for_scheduling")
    private Boolean ignoreForScheduling;

    public DependencyMaterial() {
        this(null);
    }

    public DependencyMaterial(@DelegatesTo(value = DependencyMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.DependencyMaterial") Closure cl) {
        this(null, cl);
    }

    public DependencyMaterial(String name, @DelegatesTo(value = DependencyMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.DependencyMaterial") Closure cl) {
        super(name);
        configure(cl);
    }

}
