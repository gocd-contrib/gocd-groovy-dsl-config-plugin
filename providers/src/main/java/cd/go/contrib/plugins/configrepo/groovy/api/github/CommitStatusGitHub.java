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

import com.fasterxml.jackson.annotation.JsonProperty;

import static java.lang.String.format;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class CommitStatusGitHub {

    public enum States {
        success, pending, failure, error;

        public static States getFrom(String value) {
            switch (value.toLowerCase()) {
                case "building":
                    return pending;
                case "passed":
                    return success;
                case "failing":
                case "failed":
                case "cancelled":
                    return failure;
                default:
                    throw new IllegalArgumentException(format("Don't know how to map [%s] to a GitHub commit status state", value));
            }
        }
    }

    public CommitStatusGitHub(String state, String context, String targetUrl) {
        this(States.getFrom(state), context, targetUrl);
    }

    public CommitStatusGitHub(States state, String context, String targetUrl) {
        this.state = state;
        this.context = context;
        this.targetUrl = targetUrl;
    }

    @JsonProperty
    private final States state;

    @JsonProperty
    private final String context;

    @JsonProperty("target_url")
    private final String targetUrl;
}
