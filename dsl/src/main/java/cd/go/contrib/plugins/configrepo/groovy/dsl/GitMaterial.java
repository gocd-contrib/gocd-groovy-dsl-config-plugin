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

import static groovy.lang.Closure.DELEGATE_ONLY;

/**
 * Represents a
 * <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#git">git material config</a>.
 * <p>
 * {@includeCode git.material.groovy}
 *
 * @see <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#git">git material config</a>
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class GitMaterial extends ScmMaterial<GitMaterial> {

    /**
     * The url of the git repository.
     */
    @Expose
    @SerializedName("url")
    private String url;

    /**
     * The git branch.
     */
    @Expose
    @SerializedName("branch")
    private String branch;

    /**
     * Add a {@code --depth=N} option to git cloning command on GoCD agent. Shallow clone truncates history to latest
     * revisions, thus helps accelerating clone operation for repositories with long history.
     * <p>
     * Clone depth is dynamically calculated to ensure revisions from GO_FROM_REVISION to GO_TO_REVISION are included
     * in the cloned repository.
     */
    @Expose
    @SerializedName("shallow_clone")
    private Boolean shallowClone;

    GitMaterial() {
        this(null);
    }

    GitMaterial(@DelegatesTo(value = GitMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.GitMaterial") Closure cl) {
        this(null, cl);
    }

    GitMaterial(String name, @DelegatesTo(value = GitMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.GitMaterial") Closure cl) {
        super(name, "git");
        configure(cl);
    }

}
