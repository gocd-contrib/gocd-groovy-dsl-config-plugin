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

package cd.go.contrib.plugins.configrepo.groovy.api.bitbucketcloud;

import cd.go.contrib.plugins.configrepo.groovy.branching.MergeParent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Represents a BitBucket git repository and ref that are the endpoints to be
 * merged (i.e., source and destination) in a pull request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MergeEndpoint implements MergeParent {

    private String branchName;

    private String repoUrl;

    private String fullName;

    @Override
    public String ref() {
        return format("refs/heads/%s", branchName);
    }

    @Override
    public String url() {
        return repoUrl;
    }

    public String fullName() {
        return fullName;
    }

    @JsonProperty("branch")
    @SuppressWarnings("unused")
    private void unpackBranchName(Map<String, Object> branchNode) {
        this.branchName = requireNonNull((String) branchNode.get("name"), "Missing PR branch ref");
    }

    @JsonProperty("repository")
    @SuppressWarnings("unused")
    private void unpackRepository(Map<String, Object> repoNode) {
        if (null != repoNode) {
            this.repoUrl = requireNonNull(
                    requireNonNull(
                            unpackLinks(repoNode).get("html"), "Missing PR repo clone link"
                    ).get("href"),
                    "Missing PR repo HTTP clone URL"
            );

            this.fullName = (String) requireNonNull(repoNode.get("full_name"), "Missing PR repo full name");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<String, String>> unpackLinks(Map<String, Object> repoNode) {
        return requireNonNull((Map<String, Map<String, String>>) repoNode.get("links"),
                "Missing PR source repo links");
    }
}
