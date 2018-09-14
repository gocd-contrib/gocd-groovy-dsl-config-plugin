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

import javax.validation.constraints.NotEmpty;

import static groovy.lang.Closure.DELEGATE_ONLY;

/**
 * Represents a
 * <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#hg">hg material config</a>.
 * <p>
 * {@includeCode hg.material.groovy}
 *
 * @see <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#hg">hg material config</a>.
 * for detailed description of hg config.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class HgMaterial extends ScmMaterial<HgMaterial> {

    @Expose
    @SerializedName("url")
    @NotEmpty
    private String url;

    HgMaterial() {
        this(null);
    }

    HgMaterial(@DelegatesTo(value = HgMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.HgMaterial") Closure cl) {
        this(null, cl);
    }

    HgMaterial(String name, @DelegatesTo(value = HgMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.HgMaterial") Closure cl) {
        super(name, "hg");
        configure(cl);
    }

}
