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

package cd.go.contrib.plugins.configrepo.groovy.api.gitlab;

import static java.lang.String.format;

public class CommitStatusGitLab {

    @SuppressWarnings("unused")
    public enum States {
        pending, running, success, failed, canceled;

        public static States getFrom(String value) {
            switch (value.toLowerCase()) {
                case "building":
                    return running;
                case "passed":
                    return success;
                case "failing":
                case "failed":
                    return failed;
                case "cancelled":
                    return canceled;
                default:
                    throw new IllegalArgumentException(format("Don't know how to map [%s] to a GitLab commit status state", value));
            }
        }
    }

    public CommitStatusGitLab(String state, String context, String targetUrl) {
        this(CommitStatusGitLab.States.getFrom(state), context, targetUrl);
    }

    public CommitStatusGitLab(CommitStatusGitLab.States state, String context, String targetUrl) {
        this.state = state;
        this.context = context;
        this.targetUrl = targetUrl;
    }

    public final States state;

    public final String context;

    public final String targetUrl;
}
