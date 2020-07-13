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

import cd.go.contrib.plugins.configrepo.groovy.branching.MergeCandidate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest implements MergeCandidate {

    @JsonProperty
    @SuppressWarnings("unused")
    private int id;

    private String fullName;

    private String branchName;

    private String repoUrl;

    @JsonProperty
    @SuppressWarnings("unused")
    private String title;

    private String author;

    private String showUrl;

    @Override
    public String ref() {
        return branchName;
    }

    @Override
    public String url() {
        return repoUrl;
    }

    @Override
    public String identifier() {
        return format("%s#%d", fullName, id);
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String author() {
        return author;
    }

    @Override
    public String showUrl() {
        return showUrl;
    }

    @Override
    public List<String> labels() {
        return null;
    }

    @JsonProperty("fromRef")
    @SuppressWarnings("unused")
    private void unpackSourceRepo(Map<String, Object> node) {
        if (null != node) {
            this.branchName = requireNonNull((String) node.get("id"), "Missing PR branch ref");
            this.repoUrl = requireNonNull(
                    unpackCloneUrls(unpackLinks(unpackRepo(node))).
                            stream().
                            filter(u -> "http".equals(u.get("name"))).
                            map(u -> u.get("href")).
                            findFirst().
                            orElse(null)
                    , "Missing PR source repo HTTP clone URL"
            );
        }
    }

    @JsonProperty("toRef")
    @SuppressWarnings({"unused", "unchecked"})
    private void unpackDestRepo(Map<String, Object> node) {
        final Map<String, Object> repo = unpackRepo(node);
        final String slug = (String) requireNonNull(repo.get("slug"), "Missing PR repo slug");
        final String project = (String) requireNonNull(
                requireNonNull((Map<String, Object>) repo.get("project"),
                        "Missing PR repo project"
                ).get("key"), "Missing PR repo project key"
        );
        fullName = project + "/" + slug;
    }

    @JsonProperty("author")
    @SuppressWarnings({"unused", "unchecked"})
    private void unpackAuthor(Map<String, Object> node) {
        author = (String) requireNonNull(
                requireNonNull((Map<String, Object>) node.get("user"),
                        "Missing PR author.user object"
                ).get("name"),
                "Missing PR author.user.name"
        );
    }

    @JsonProperty("links")
    @SuppressWarnings({"unused", "unchecked"})
    private void unpackShowUrl(Map<String, Object> node) {
        showUrl = requireNonNull(
                requireNonNull(
                        (List<Map<String, String>>) node.get("self"),
                        "Missing PR self link"
                ).
                        stream().
                        map(u -> u.get("href")).
                        findFirst().
                        orElse(null),
                "Missing PR self link href"
        );
    }

    private List<Map<String, String>> unpackCloneUrls(Map<String, List<Map<String, String>>> links) {
        return requireNonNull(links.get("clone"), "Missing PR repo clone URLs");
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<Map<String, String>>> unpackLinks(Map<String, Object> node) {
        return requireNonNull(
                (Map<String, List<Map<String, String>>>) node.get("links"),
                "Missing PR repo links"
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> unpackRepo(Map<String, Object> node) {
        return requireNonNull((Map<String, Object>) node.get("repository"), "Missing PR repo information");
    }
}
