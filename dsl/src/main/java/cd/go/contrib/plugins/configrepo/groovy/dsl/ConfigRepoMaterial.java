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
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.List;
import java.util.function.Consumer;

import static groovy.lang.Closure.DELEGATE_ONLY;
import static lombok.AccessLevel.NONE;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConfigRepoMaterial extends Material<ConfigRepoMaterial> {

    /**
     * The directory under the sandbox of Go Agent. GoCD will check out the source code into this directory.
     */
    @JsonProperty("destination")
    private String destination;


    /**
     * The {@link Filter} element specifies files in changesets that should not trigger a pipeline automatically. When a
     * pipeline is triggered by files that are not ignored the filtered files will still be updated with other files.
     * You can only define one filter under each SCM material. When you trigger a pipeline manually, it will update to
     * most recent revision, including filtered files.
     * <p>
     * {@includeCode scm.filter.groovy }
     */
    @Getter(value = NONE)
    @Setter(value = NONE)
    @JsonProperty("filter")
    @Valid
    private Filter filter;

    public ConfigRepoMaterial() {
        this(null);
    }

    public ConfigRepoMaterial(@DelegatesTo(value = ConfigRepoMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ConfigRepoMaterial") Closure<?> cl) {
        this(null, cl);
    }

    public ConfigRepoMaterial(String name, @DelegatesTo(value = ConfigRepoMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ConfigRepoMaterial") Closure<?> cl) {
        super(name);
        configure(cl);
    }

    public ConfigRepoMaterial(String name, Consumer<ConfigRepoMaterial> config) {
        super(name);
        config.accept(this);
    }

    @Override
    public ConfigRepoMaterial dup(
            @DelegatesTo(value = ConfigRepoMaterial.class, strategy = DELEGATE_ONLY)
            @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ConfigRepoMaterial")
                    Closure<?> config) {
        return Configurable.applyTo(config, deepClone());
    }

    @JsonIgnore
    public List<String> getBlacklist() {
        if (this.filter != null && !this.filter.isWhitelist()) {
            return this.filter.getItems();
        }
        return null;
    }

    /**
     * {@includeCode scm.blacklist.groovy }
     */
    public void setBlacklist(List<String> blacklist) {
        filter = new Filter(blacklist);
    }

    @JsonIgnore
    public List<String> getWhitelist() {
        if (this.filter != null && this.filter.isWhitelist()) {
            return this.filter.getItems();
        }
        return null;
    }

    /**
     * {@includeCode scm.whitelist.groovy }
     */
    public void setWhitelist(List<String> whitelist) {
        filter = new Filter(true, whitelist);
    }

    private ConfigRepoMaterial deepClone() {
        return new ConfigRepoMaterial(name, c -> {
            c.destination = destination;
            c.filter = filter.deepClone();
        });
    }
}
