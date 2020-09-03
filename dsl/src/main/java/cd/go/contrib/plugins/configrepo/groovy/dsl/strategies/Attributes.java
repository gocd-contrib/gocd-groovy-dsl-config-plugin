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
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

import static org.apache.commons.lang3.StringUtils.isAllBlank;

public interface Attributes<T extends ConnectionConfig> {

    Type type();

    String getMaterialUrl();

    String getMaterialUsername();

    String getMaterialPassword();

    default boolean materialCredentialsGiven() {
        return !isAllBlank(getMaterialUsername(), getMaterialPassword());
    }

    /**
     * @return a "pure" {@link ConnectionConfig} (i.e., sans material config fields). Ideally, this is
     * a completely separate instance from this {@link Attributes} instance so as to avoid any side
     * effects from sharing data among the mechanisms manipulating it.
     */
    T asConnectionConfig();

    @Getter
    @Setter
    class MaterialFields {

        /**
         * OPTIONAL: User can override the URL, with say, ssh or a proxy
         * <p>
         * Assumes material accepts URL (since we are only supporting Git at the moment, this
         * is fine; if we later expand to other SCMs, P4 is the only oddball.)
         */
        private String materialUrl = null;

        /**
         * OPTIONAL: Username for material auth
         */
        private String materialUsername = null;

        /**
         * OPTIONAL: Password for material auth
         */
        private String materialPassword = null;
    }

    class GitBranch extends Basic.Git implements Attributes<Basic.Git> {

        @Delegate
        private final MaterialFields materialConfig = new MaterialFields();

        @Override
        public Basic.Git asConnectionConfig() {
            return new Basic.Git(self -> {
                self.url = url;
            });
        }
    }

    class GitHubPR extends GitHub implements Attributes<GitHub> {

        @Delegate
        private final MaterialFields materialConfig = new MaterialFields();

        @Override
        public GitHub asConnectionConfig() {
            return new GitHub(self -> {
                self.apiAuthToken = apiAuthToken;
                self.fullRepoName = fullRepoName;
            });
        }
    }

    class GitLabMR extends GitLab implements Attributes<GitLab> {

        @Delegate
        private final MaterialFields materialConfig = new MaterialFields();

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

        @Delegate
        private final MaterialFields materialConfig = new MaterialFields();

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

        @Delegate
        private final MaterialFields materialConfig = new MaterialFields();

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
