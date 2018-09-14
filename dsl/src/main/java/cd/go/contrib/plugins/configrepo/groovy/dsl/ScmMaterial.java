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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
abstract class ScmMaterial<T extends ScmMaterial> extends Material<T> {

    /**
     * The directory under the sandbox of Go Agent. GoCD will check out the source code into this directory.
     */
    @Expose
    @SerializedName("destination")
    private String destination;

    /**
     * By default GoCD polls the repository for changes automatically. If {@code autoUpdate} is set to {@code false}
     * then GoCD will not poll the repository for changes. Instead it will check for changes only when you trigger a
     * pipeline that contains this material. If the same material is specified more than once in the configuration file,
     * all of them must have the same value for {@code autoUpdate}.
     */
    @Expose
    @SerializedName("auto_update")
    private Boolean autoUpdate;


    /**
     * The {@link Filter} element specifies files in changesets that should not trigger a pipeline automatically. When a
     * pipeline is triggered by files that are not ignored the filtered files will still be updated with other files.
     * You can only define one filter under each SCM material. When you trigger a pipeline manually, it will update to
     * most recent revision, including filtered files.
     * <p>
     * {@includeCode scm.filter.groovy }
     */
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    @Expose
    @SerializedName("filter")
    @Valid
    private Filter filter;

    ScmMaterial(String type) {
        super(type);
    }

    ScmMaterial(String name, String type) {
        super(name, type);
    }


    /**
     * {@includeCode scm.blacklist.groovy }
     */
    public void setBlacklist(List<String> blacklist) {
        filter = new Filter(blacklist);
    }

    /**
     * {@includeCode scm.whitelist.groovy }
     */
    public void setWhitelist(List<String> whitelist) {
        filter = new Filter(true, whitelist);
    }
}
