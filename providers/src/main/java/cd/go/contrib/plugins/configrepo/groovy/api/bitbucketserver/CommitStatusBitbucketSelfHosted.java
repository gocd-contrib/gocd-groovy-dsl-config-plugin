/*
 * Copyright 2022 Thoughtworks, Inc.
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

package cd.go.contrib.plugins.configrepo.groovy.api.bitbucketserver;

import com.fasterxml.jackson.annotation.JsonProperty;

import static java.lang.String.format;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class CommitStatusBitbucketSelfHosted {

    public enum States {
        SUCCESSFUL, INPROGRESS, FAILED;

        public static States getFrom(String value) {
            switch (value.toLowerCase()) {
                case "building":
                    return INPROGRESS;
                case "passed":
                    return SUCCESSFUL;
                case "failing":
                case "failed":
                case "cancelled":
                    return FAILED;
                default:
                    throw new IllegalArgumentException(format("Don't know how to map [%s] to a Bitbucket Server commit status state", value));
            }
        }

    }

    public CommitStatusBitbucketSelfHosted(String state, String key, String url) {
        this(States.getFrom(state), key, url);
    }

    public CommitStatusBitbucketSelfHosted(States state, String key, String url) {
        this.state = state;
        this.key = key;
        this.url = url;
    }

    @JsonProperty
    private final States state;

    @JsonProperty
    private final String key;

    @JsonProperty
    private final String url;
}
