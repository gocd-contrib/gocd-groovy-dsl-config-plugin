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

/**
 * Represents a artifact that should be uploaded from the agent to the server.
 * <p>
 * {@includeCode 'artifacts.big.groovy' }
 *
 * @see <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#artifacts">Artifact
 * Configuration</a>
 * @see <a href="https://ant.apache.org/manual/dirtasks.html#patterns">Source pattern</a>
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
abstract public class AbstractBuiltInArtifact<T extends AbstractBuiltInArtifact> extends AbstractArtifact<T> {

    /**
     * The file or folders to publish to the server. GoCD will only upload files that are in the working directory of
     * the job. You can specify an ant style glob.
     *
     * @see <a href="https://ant.apache.org/manual/dirtasks.html#patterns">Source pattern</a>
     * Example: <code>target/**{@literal /}*.xml</code>
     */
    @JsonProperty("source")
    @NotEmpty
    private String source;

    /**
     * The destination is relative to the artifacts folder of the current instance on the server side. If it is not
     * specified, the artifact will be stored in the root of the artifacts directory.
     */
    @JsonProperty("destination")
    private String destination;

    @SuppressWarnings("unused" /*method here for deserialization only*/)
    protected AbstractBuiltInArtifact() {
        this(null, null);
    }

    protected AbstractBuiltInArtifact(String type, @DelegatesTo(value = AbstractBuiltInArtifact.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.AbstractBuiltInArtifact") Closure cl) {
        super(type);
        configure(cl);
    }

}
