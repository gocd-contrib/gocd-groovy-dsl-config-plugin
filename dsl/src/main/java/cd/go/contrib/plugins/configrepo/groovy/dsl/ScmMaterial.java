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

import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.Notifies;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Consumer;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class ScmMaterial<T extends ScmMaterial<?>> extends Material<T> implements Notifies.MaterialMixin {

    @JsonProperty("username")
    protected String username;

    /**
     * The encrypted password
     *
     * @see <a href='https://api.gocd.org/current/#encrypt-a-plain-text-value'>Encryption API</a>
     */
    @JsonProperty("encrypted_password")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String encryptedPassword;

    /**
     * The plain text password in the form of secret param*
     * Use encrypted password instead
     *
     * @see <a href="https://docs.gocd.org/current/configuration/secrets_management.html">secrets</a>
     */
    @JsonProperty("password")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String password;

    /**
     * The directory under the sandbox of Go Agent. GoCD will check out the source code into this directory.
     */
    @JsonProperty("destination")
    private String destination;

    /**
     * By default GoCD polls the repository for changes automatically. If {@code autoUpdate} is set to {@code false}
     * then GoCD will not poll the repository for changes. Instead it will check for changes only when you trigger a
     * pipeline that contains this material. If the same material is specified more than once in the configuration file,
     * all of them must have the same value for {@code autoUpdate}.
     */
    @JsonProperty("auto_update")
    private Boolean autoUpdate = true;

    /**
     * The {@link Filter} element specifies files in changesets that should not trigger a pipeline automatically. When a
     * pipeline is triggered by files that are not ignored the filtered files will still be updated with other files.
     * You can only define one filter under each SCM material. When you trigger a pipeline manually, it will update to
     * most recent revision, including filtered files.
     * <p>
     * {@includeCode scm.filter.groovy }
     */
    @Getter(value = NONE)
    @Setter(value = NONE)
    @JsonProperty("filter")
    @Valid
    private Filter filter;

    ScmMaterial() {
        super();
    }

    ScmMaterial(String name) {
        super(name);
    }

    ScmMaterial(String name, Consumer<T> configure) {
        super(name);
        //noinspection unchecked
        configure.accept((T) this);
    }

    @JsonIgnore
    public List<String> getBlacklist() {
        if (this.filter != null && !this.filter.isWhitelist()) {
            return this.filter.getItems();
        }
        return null;
    }

    /**
     * {@includeCode scm.blacklist.groovy }
     */
    public void setBlacklist(List<String> blacklist) {
        filter = new Filter(blacklist);
    }

    @JsonIgnore
    public List<String> getWhitelist() {
        if (this.filter != null && this.filter.isWhitelist()) {
            return this.filter.getItems();
        }
        return null;
    }

    /**
     * {@includeCode scm.whitelist.groovy }
     */
    public void setWhitelist(List<String> whitelist) {
        filter = new Filter(true, whitelist);
    }

    protected abstract T deepClone();

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    protected T injectSettings(ScmMaterial<T> other) {
        other.username = username;
        other.password = password;
        other.encryptedPassword = encryptedPassword;
        other.autoUpdate = autoUpdate;
        other.destination = destination;
        if (null != filter) {
            other.filter = filter.deepClone();
        }

        return (T) other;
    }
}
