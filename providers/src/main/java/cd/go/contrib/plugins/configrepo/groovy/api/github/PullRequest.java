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

package cd.go.contrib.plugins.configrepo.groovy.api.github;

import cd.go.contrib.plugins.configrepo.groovy.branching.MergeCandidate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest implements MergeCandidate {

    @JsonProperty
    @SuppressWarnings("unused")
    private int number;

    private String fullName;

    @JsonProperty
    @SuppressWarnings("unused")
    private String title;

    private String author;

    private String repoUrl;

    private String showUrl;

    private List<String> labels;

    @Override
    public String ref() {
        return format("refs/pull/%d/head", number);
    }

    @Override
    public String url() {
        return repoUrl;
    }

    @Override
    public String identifier() {
        return format("%s#%d", fullName, number);
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
        return labels;
    }

    @JsonProperty("base")
    @SuppressWarnings({"unchecked", "unused"})
    private void unpackRepo(Map<String, Object> node) {
        if (null != node) {
            final Map<String, Object> repo = requireNonNull(
                    (Map<String, Object>) node.get("repo"),
                    "Missing PR repo"
            );
            repoUrl = requireNonNull((String) repo.get("clone_url"), "Missing PR repo clone URL");
            fullName = requireNonNull((String) repo.get("full_name"), "Missig PR repo full name");
        }
    }

    @JsonProperty("user")
    @SuppressWarnings({"unused"})
    private void unpackAuthor(Map<String, Object> node) {
        author = (String) requireNonNull(node.get("login"));
    }

    @JsonProperty("labels")
    @SuppressWarnings({"unchecked", "unused"})
    private void unpackLabels(List<Object> node) {
        labels = node.stream().map(l -> ((Map<String, String>) l).get("name")).collect(Collectors.toList());
    }

    @JsonProperty("_links")
    @SuppressWarnings({"unchecked", "unused"})
    private void unpackShowUrl(Map<String, Object> node) {
        showUrl = requireNonNull((String) requireNonNull(
                (Map<String, Object>) node.get("html"),
                "Missing PR html link entry"
        ).get("href"), "Missing PR html link href");
    }
}
