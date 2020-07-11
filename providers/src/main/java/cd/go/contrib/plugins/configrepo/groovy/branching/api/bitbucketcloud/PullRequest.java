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

package cd.go.contrib.plugins.configrepo.groovy.branching.api.bitbucketcloud;

import cd.go.contrib.plugins.configrepo.groovy.branching.MergeCandidate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.firstNonBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest implements MergeCandidate {

    @JsonProperty
    @SuppressWarnings("unused")
    private MergeEndpoint source;

    @JsonProperty
    @SuppressWarnings("unused")
    private MergeEndpoint destination;

    @JsonProperty
    @SuppressWarnings("unused")
    private String title;

    private String author;

    private String showUrl;

    @Override
    public String ref() {
        return source.ref();
    }

    @Override
    public String url() {
        return firstNonBlank(source.url(), destination.url());
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

    @JsonProperty("author")
    @SuppressWarnings({"unused"})
    private void unpackAuthor(Map<String, Object> node) {
        author = (String) requireNonNull(node.get("nickname"));
    }

    @JsonProperty("links")
    @SuppressWarnings({"unused", "unchecked"})
    private void unpackLinks(Map<String, Object> node) {
        showUrl = requireNonNull((String) requireNonNull(
                (Map<String, Object>) node.get("html"),
                "Missing PR html link entry"
        ).get("href"), "Missing PR html link href");
    }
}
