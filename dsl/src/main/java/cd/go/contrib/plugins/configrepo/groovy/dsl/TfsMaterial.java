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

import javax.validation.constraints.NotBlank;

import static groovy.lang.Closure.DELEGATE_ONLY;

/**
 * Represents a
 * <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#tfs">tfs material config</a>.
 *
 * {@includeCode tfs.material.groovy}
 *
 * @see <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#tfs">tfs material config</a>.
 * for detailed description of tfs config.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class TfsMaterial extends ScmMaterial<TfsMaterial> {

    /**
     * The URL of the TFS server
     */
    @NotBlank
    private String url;

    /**
     * Domain name for TFS authentication credentials.
     */
    private String domain;

    /**
     * Username of the account to access the TFS collection.
     */
    private String username;

    /**
     * The encrypted password of the account to access the TFS collection.
     *
     * @see <a href='https://api.gocd.org/current/#encrypt-a-plain-text-value'>Encryption API</a>
     */
    private String encryptedPassword;

    /**
     * The project path within the TFS collection.
     */
    private String projectPath;

    TfsMaterial() {
        this(null);
    }

    TfsMaterial(@DelegatesTo(value = TfsMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.TfsMaterial") Closure cl) {
        this(null, cl);
    }

    TfsMaterial(String name, @DelegatesTo(value = TfsMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.TfsMaterial") Closure cl) {
        super(name, "tfs");
        configure(cl);
    }

}
