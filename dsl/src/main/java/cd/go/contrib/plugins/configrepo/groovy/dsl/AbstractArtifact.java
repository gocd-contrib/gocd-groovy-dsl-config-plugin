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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        visible = true,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BuildArtifact.class, name = "build"),
        @JsonSubTypes.Type(value = TestArtifact.class, name = "test"),
        @JsonSubTypes.Type(value = PluginArtifact.class, name = "external"),
})
@ToString(callSuper = true)
public abstract class AbstractArtifact<T extends AbstractArtifact> extends Node<T> {

    @JsonProperty("type")
    @Getter(value = NONE)
    @Setter(value = NONE)
    @NotEmpty
    protected final String type;

    AbstractArtifact(String type) {
        this.type = type;
    }
}
