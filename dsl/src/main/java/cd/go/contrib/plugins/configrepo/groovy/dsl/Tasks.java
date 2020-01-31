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

/**
 * Represents a collection of tasks.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Tasks extends CollectionNode<Task> {

    /**
     * Creates an `exec` task.
     */
    public ExecTask exec(@DelegatesTo(value = ExecTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ExecTask") Closure cl) {
        return (ExecTask) create(() -> new ExecTask(cl));
    }

    /**
     * Convenience for {@link #exec(Closure)} task that wraps a command in a shell.
     * <p>
     * {@includeCode shell-large-examples.groovy}
     */
    public ShellTask shell(@DelegatesTo(value = ShellTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ShellTask") Closure cl) {
        return (ShellTask) create(() -> new ShellTask(null, cl));
    }

    /**
     * Convenience for {@link #exec(Closure)} task that wraps a command in a {@code bash} shell.
     * <p>
     * {@includeCode shell-large-examples.groovy}
     */
    public ShellTask bash(@DelegatesTo(value = ShellTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ShellTask") Closure cl) {
        return (ShellTask) create(() -> new ShellTask("bash", cl));
    }

    /**
     * Convenience for {@link #exec(Closure)} task that wraps a command in a {@code zsh} shell.
     * <p>
     * {@includeCode shell-large-examples.groovy}
     */
    public ShellTask zsh(@DelegatesTo(value = ShellTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ShellTask") Closure cl) {
        return new ShellTask("zsh", cl);
    }

    public FetchArtifactTask fetchArtifact(@DelegatesTo(value = FetchArtifactTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.FetchArtifactTask") Closure cl) {
        return fetchDirectory(cl);
    }

    public FetchExternalArtifactTask fetchExternalArtifact(@DelegatesTo(value = FetchExternalArtifactTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.FetchExternalArtifactTask") Closure cl) {
        return (FetchExternalArtifactTask) create(() -> new FetchExternalArtifactTask(cl));
    }

    public FetchArtifactTask fetchFile(@DelegatesTo(value = FetchArtifactTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.FetchArtifactTask") Closure cl) {
        return (FetchArtifactTask) create(() -> new FetchArtifactTask(true, cl));
    }

    public FetchArtifactTask fetchDirectory(@DelegatesTo(value = FetchArtifactTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.FetchArtifactTask") Closure cl) {
        return (FetchArtifactTask) create(() -> new FetchArtifactTask(false, cl));
    }

    public PluginTask plugin(@DelegatesTo(value = PluginTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.PluginTask") Closure cl) {
        return (PluginTask) create(() -> new PluginTask(cl));
    }
}
