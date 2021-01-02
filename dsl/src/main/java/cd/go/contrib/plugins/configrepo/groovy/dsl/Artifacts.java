/*
 * Copyright 2021 ThoughtWorks, Inc.
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
 * Represents a list of artifacts.
 * <p>
 * {@includeCode artifacts.big.groovy}
 *
 * @see AbstractBuiltInArtifact
 * @see PluginArtifact
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Artifacts extends CollectionNode<AbstractArtifact> {

    /**
     * Create a build artifact
     * <p>
     * {@includeCode build-artifact.groovy}
     *
     * @see <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#artifacts">Artifact
     * Configuration</a>
     * @see <a href="https://ant.apache.org/manual/dirtasks.html#patterns">Source pattern</a>
     */
    public BuildArtifact build(@DelegatesTo(value = BuildArtifact.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.BuildArtifact") Closure cl) {
        return (BuildArtifact) create(() -> new BuildArtifact("build", cl));
    }

    /**
     * Create a test artifact
     * <p>
     * {@includeCode test-artifact.groovy}
     *
     * @see <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#artifacts">Artifact
     * Configuration</a>
     * @see <a href="https://ant.apache.org/manual/dirtasks.html#patterns">Source pattern</a>
     */
    public TestArtifact test(@DelegatesTo(value = TestArtifact.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.TestArtifact") Closure cl) {
        return (TestArtifact) create(() -> new TestArtifact("test", cl));
    }

    /**
     * Create a plugin artifact
     *
     * <p>
     * {@includeCode external-artifact.groovy}
     *
     * @see <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#artifacts">Artifact
     * Configuration</a>
     * @see <a href="https://ant.apache.org/manual/dirtasks.html#patterns">Source pattern</a>
     */
    public PluginArtifact external(@DelegatesTo(value = PluginArtifact.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.PluginArtifact") Closure cl) {
        return (PluginArtifact) create(() -> new PluginArtifact(cl));
    }
}
