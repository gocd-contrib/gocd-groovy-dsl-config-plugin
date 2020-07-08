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

import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.Configurable;
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
import java.util.function.Consumer;

import static groovy.lang.Closure.DELEGATE_ONLY;

/**
 * Represents a
 * <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#svn">svn material config</a>.
 * <p>
 * {@includeCode svn.material.groovy}
 *
 * @see <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#svn">svn material config</a>.
 * for detailed description of svn config.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SvnMaterial extends ScmMaterial<SvnMaterial> {

    @JsonProperty("url")
    @Valid
    private String url;

    @JsonProperty("check_externals")
    private Boolean checkExternals;

    public SvnMaterial() {
        this(null);
    }

    public SvnMaterial(@DelegatesTo(value = SvnMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.SvnMaterial") Closure cl) {
        this(null, cl);
    }

    public SvnMaterial(String name, @DelegatesTo(value = SvnMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.SvnMaterial") Closure cl) {
        super(name);
        configure(cl);
    }

    public SvnMaterial(String name, Consumer<SvnMaterial> configure) {
        super(name, configure);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public SvnMaterial dup(
            @DelegatesTo(value = SvnMaterial.class, strategy = DELEGATE_ONLY)
            @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.SvnMaterial")
                    Closure config) {
        return Configurable.applyTo(config, deepClone());
    }

    @Override
    protected SvnMaterial deepClone() {
        return new SvnMaterial(name, s -> {
            injectSettings(s);
            s.url = url;
            s.checkExternals = checkExternals;
        });
    }
}
