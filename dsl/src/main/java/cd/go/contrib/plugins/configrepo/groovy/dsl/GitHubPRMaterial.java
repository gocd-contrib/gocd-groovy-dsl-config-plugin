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
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

import static groovy.lang.Closure.DELEGATE_ONLY;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GitHubPRMaterial extends ScmMaterial<GitHubPRMaterial> {

    private String url;

    private String branch;

    public GitHubPRMaterial(String name, Consumer<GitHubPRMaterial> configure) {
        super(name, configure);
    }

    public GitHubPRMaterial(@DelegatesTo(value = GitHubPRMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.GitHubPRMaterial") Closure cl) {
        this(null, cl);
    }

    public GitHubPRMaterial(String name, @DelegatesTo(value = GitHubPRMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.GitHubPRMaterial") Closure cl) {
        super(name);
        configure(cl);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public GitHubPRMaterial dup(
            @DelegatesTo(value = GitHubPRMaterial.class, strategy = DELEGATE_ONLY)
            @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.GitHubPRMaterial")
                    Closure config) {
        return Configurable.applyTo(config, deepClone());
    }

    public Object toPluggableMaterial() {
        LinkedHashMap<Object, Object> result = new LinkedHashMap<>();
        result.put("name", name);
        result.put("type", "plugin");
        result.put("plugin_configuration", pluginConfig());
        result.put("configuration", configuration());
        return result;
    }

    @Override
    protected GitHubPRMaterial deepClone() {
        return new GitHubPRMaterial(name, g -> {
            injectSettings(g);
            g.url = url;
            g.branch = branch;
        });
    }

    private Object configuration() {
        ArrayList<Object> result = new ArrayList<>();
        result.add(kvp("url", url, false));
        result.add(kvp("username", username, false));
        result.add(kvp("password", encryptedPassword, true));
        result.add(kvp("defaultBranch", branch, false));
        return result;
    }

    private Object kvp(String key, String value, boolean encrypted) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("key", key);
        if (encrypted) {
            result.put("encrypted_value", value);
        } else {
            result.put("value", value);
        }
        return result;
    }

    private Object pluginConfig() {
        LinkedHashMap<Object, Object> result = new LinkedHashMap<>();
        result.put("id", "github.pr");
        result.put("version", "1");
        return result;
    }
}
