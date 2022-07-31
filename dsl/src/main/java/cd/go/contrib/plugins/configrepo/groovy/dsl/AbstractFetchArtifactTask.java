/*
 * Copyright 2022 Thoughtworks, Inc.
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AbstractFetchArtifactTask extends Task<AbstractFetchArtifactTask> {

    @JsonProperty("artifact_origin")
    protected String artifactOrigin = "gocd";

    @JsonProperty("pipeline")
    protected String pipeline;

    @JsonProperty("stage")
    @NotEmpty
    protected String stage;

    @JsonProperty("job")
    @NotEmpty
    protected String job;

    protected AbstractFetchArtifactTask() {
        super("fetch");
    }
}
