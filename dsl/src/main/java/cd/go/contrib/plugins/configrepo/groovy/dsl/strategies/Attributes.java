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

package cd.go.contrib.plugins.configrepo.groovy.dsl.strategies;

import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.*;

import static org.apache.commons.lang3.StringUtils.isAllBlank;

public interface Attributes<T extends ConnectionConfig> {

    Type type();

    /**
     * OPTIONAL: User can override the URL, with say, ssh or a proxy
     * <p>
     * Assumes material accepts URL (since we are only supporting Git at the moment, this
     * is fine; if we later expand to other SCMs, P4 is the only oddball.)
     */
    String materialUrl = null;

    /**
     * OPTIONAL: Username for material auth
     */
    String materialUsername = null;

    /**
     * OPTIONAL: Username for material password
     */
    String materialPassword = null;

    default boolean credentialsGiven() {
        return !isAllBlank(materialUsername, materialPassword);
    }

    /**
     * @return a "pure" {@link ConnectionConfig} (i.e., sans material config fields). Ideally, this is
     * a completely separate instance from this {@link Attributes} instance so as to avoid any side
     * effects from sharing data among the mechanisms manipulating it.
     */
    T asConnectionConfig();

    class GitBranch extends Basic.Git implements Attributes<Basic.Git> {

        @Override
        public Basic.Git asConnectionConfig() {
            throw new IllegalArgumentException("asConnectionConfig() is not supported by plain `git`");
        }
    }

    class GitHubPR extends GitHub implements Attributes<GitHub> {

        @Override
        public GitHub asConnectionConfig() {
            return new GitHub(self -> {
                self.apiAuthToken = apiAuthToken;
                self.fullRepoName = fullRepoName;
            });
        }
    }

    class GitLabMR extends GitLab implements Attributes<GitLab> {

        @Override
        public GitLab asConnectionConfig() {
            return new GitLab(self -> {
                self.apiAuthToken = apiAuthToken;
                self.fullRepoName = fullRepoName;
                self.serverBaseUrl = serverBaseUrl;
            });
        }
    }

    class BitbucketPR extends Bitbucket implements Attributes<Bitbucket> {

        @Override
        public Bitbucket asConnectionConfig() {
            return new Bitbucket(self -> {
                self.apiUser = apiUser;
                self.apiPass = apiPass;
                self.fullRepoName = fullRepoName;
            });
        }
    }

    class BitbucketSelfHostedPR extends BitbucketSelfHosted implements Attributes<BitbucketSelfHosted> {

        @Override
        public BitbucketSelfHosted asConnectionConfig() {
            return new BitbucketSelfHosted(self -> {
                self.apiAuthToken = apiAuthToken;
                self.serverBaseUrl = serverBaseUrl;
                self.fullRepoName = fullRepoName;
            });
        }
    }
}
