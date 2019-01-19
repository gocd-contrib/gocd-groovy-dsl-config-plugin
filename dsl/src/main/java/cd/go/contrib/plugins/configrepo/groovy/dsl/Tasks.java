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

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import static groovy.lang.Closure.DELEGATE_ONLY;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Tasks extends CollectionNode<Task> {

    public ExecTask exec(@DelegatesTo(value = ExecTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ExecTask") Closure cl) {
        return (ExecTask) create(() -> new ExecTask(cl));
    }

    public ShellTask shell(@DelegatesTo(value = ShellTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ShellTask") Closure cl) {
        return (ShellTask) create(() -> new ShellTask(null, cl));
    }

    public ShellTask bash(@DelegatesTo(value = ShellTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ShellTask") Closure cl) {
        return (ShellTask) create(() -> new ShellTask("bash", cl));
    }

    public ShellTask zsh(@DelegatesTo(value = ShellTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ShellTask") Closure cl) {
        return new ShellTask("zsh", cl);
    }

    public FetchArtifactTask fetchArtifact(@DelegatesTo(value = FetchArtifactTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.FetchArtifactTask") Closure cl) {
        return fetchDirectory(cl);
    }

    public FetchArtifactTask fetchFile(@DelegatesTo(value = FetchArtifactTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.FetchArtifactTask") Closure cl) {
        return (FetchArtifactTask) create(() -> new FetchArtifactTask(true, cl));
    }

    public FetchArtifactTask fetchDirectory(@DelegatesTo(value = FetchArtifactTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.FetchArtifactTask") Closure cl) {
        return (FetchArtifactTask) create(() -> new FetchArtifactTask(false, cl));
    }

    public PluginTask plugin(@DelegatesTo(value = PluginTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.PluginTask") Closure cl) {
        return new PluginTask(cl);
    }
}
