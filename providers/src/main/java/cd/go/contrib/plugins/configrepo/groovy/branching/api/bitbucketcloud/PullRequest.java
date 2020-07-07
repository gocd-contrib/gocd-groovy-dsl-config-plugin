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

import cd.go.contrib.plugins.configrepo.groovy.branching.MergeParent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import static org.apache.commons.lang3.StringUtils.firstNonBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest implements MergeParent {

    @JsonProperty
    @SuppressWarnings("unused")
    private MergeEndpoint source;

    @JsonProperty
    @SuppressWarnings("unused")
    private MergeEndpoint destination;

    public String url() {
        return firstNonBlank(source.url(), destination.url());
    }

    public String ref() {
        return source.ref();
    }
}
