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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import groovy.lang.Closure;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a material that a pipeline polls on.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GitMaterial.class, name = "git"),
        @JsonSubTypes.Type(value = HgMaterial.class, name = "hg"),

        @JsonSubTypes.Type(value = SvnMaterial.class, name = "svn"),
        @JsonSubTypes.Type(value = P4Material.class, name = "p4"),
        @JsonSubTypes.Type(value = TfsMaterial.class, name = "tfs"),

        @JsonSubTypes.Type(value = PluggableMaterial.class, name = "plugin"),
        @JsonSubTypes.Type(value = DependencyMaterial.class, name = "dependency"),
        @JsonSubTypes.Type(value = ConfigRepoMaterial.class, name = "configrepo"),
        @JsonSubTypes.Type(value = PackageMaterial.class, name = "package"),
})
public abstract class Material<T extends Material> extends NamedNode<T> {

    Material() {
        this(null);
    }

    Material(String name) {
        super(name);
    }

    public abstract T dup(Closure<?> config);
}
