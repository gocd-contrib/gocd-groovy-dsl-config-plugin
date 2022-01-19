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

import jakarta.validation.constraints.NotEmpty;
import java.util.function.Consumer;

import static groovy.lang.Closure.DELEGATE_ONLY;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PackageMaterial extends Material<PackageMaterial> {

    @JsonProperty("package_id")
    @NotEmpty
    private String ref;

    public PackageMaterial() {
        this(null);
    }

    public PackageMaterial(@DelegatesTo(value = PackageMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.PackageMaterial") Closure<?> cl) {
        this(null, cl);
    }

    public PackageMaterial(String name, @DelegatesTo(value = PackageMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.PackageMaterial") Closure<?> cl) {
        super(name);
        configure(cl);
    }

    public PackageMaterial(String name, Consumer<PackageMaterial> config) {
        super(name);
        config.accept(this);
    }

    @Override
    public PackageMaterial dup(
            @DelegatesTo(value = PackageMaterial.class, strategy = DELEGATE_ONLY)
            @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.PackageMaterial")
                    Closure<?> config) {
        return Configurable.applyTo(config, deepClone());
    }

    private PackageMaterial deepClone() {
        return new PackageMaterial(name, p -> p.ref = ref);
    }
}
