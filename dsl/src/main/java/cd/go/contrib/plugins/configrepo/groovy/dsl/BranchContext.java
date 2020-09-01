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

import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.ConnectionConfig;
import cd.go.contrib.plugins.configrepo.groovy.dsl.util.TextUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class BranchContext {

    /**
     * Required for JSON deserialization
     */
    @SuppressWarnings("unused")
    public BranchContext() {
    }

    @SuppressWarnings("rawtypes")
    public BranchContext(@NotEmpty String fullRefName, @NotEmpty String branch, @NotNull ScmMaterial repo) {
        this.fullRefName = fullRefName;
        this.branch = branch;
        this.branchSanitized = TextUtils.sanitizeName(branch);
        this.repo = repo;
    }

    @NotNull
    private ConnectionConfig provider;

    @JsonProperty
    @NotEmpty
    private String identifier;

    @JsonProperty
    @NotEmpty
    private String title;

    @JsonProperty
    @NotNull
    private String author;

    @JsonProperty("reference_url")
    @NotNull
    private String referenceUrl;

    @JsonProperty
    @NotNull
    private List<String> labels;

    @JsonProperty("full_ref_name")
    @NotEmpty
    private String fullRefName;

    @JsonProperty("branch_name")
    @NotEmpty
    private String branch;

    @JsonProperty("sanitized_branch_name")
    @NotEmpty
    private String branchSanitized;

    @JsonProperty
    @NotNull
    @SuppressWarnings("rawtypes")
    private ScmMaterial repo;
}
