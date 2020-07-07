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

package cd.go.contrib.plugins.configrepo.groovy.branching.api.bitbucketserver;

import cd.go.contrib.plugins.configrepo.groovy.branching.MergeParent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest implements MergeParent {

    private String branchName;

    private String repoUrl;

    @Override
    public String ref() {
        return branchName;
    }

    @Override
    public String url() {
        return repoUrl;
    }

    @JsonProperty("fromRef")
    @SuppressWarnings("unused")
    private void unpackSourceRepo(Map<String, Object> refNode) {
        if (null != refNode) {
            this.branchName = requireNonNull((String) refNode.get("id"), "Missing PR branch ref");
            this.repoUrl = requireNonNull(
                    unpackCloneUrls(unpackLinks(unpackRepo(refNode))).
                            stream().
                            filter(u -> "http".equals(u.get("name"))).
                            map(u -> u.get("href")).
                            findFirst().
                            orElse(null)
                    , "Missing PR source repo HTTP clone URL"
            );
        }
    }

    private List<Map<String, String>> unpackCloneUrls(Map<String, List<Map<String, String>>> links) {
        return requireNonNull(links.get("clone"), "Missing PR source repo clone URLs");
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<Map<String, String>>> unpackLinks(Map<String, Object> repoNode) {
        return requireNonNull(
                (Map<String, List<Map<String, String>>>) repoNode.get("links"),
                "Missing PR source repo links"
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> unpackRepo(Map<String, Object> refNode) {
        return requireNonNull((Map<String, Object>) refNode.get("repository"), "Missing PR source repo information");
    }
}
