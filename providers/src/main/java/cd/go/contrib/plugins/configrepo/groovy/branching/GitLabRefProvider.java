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

package cd.go.contrib.plugins.configrepo.groovy.branching;

import cd.go.contrib.plugins.configrepo.groovy.api.Client;
import cd.go.contrib.plugins.configrepo.groovy.api.gitlab.GitLabService;
import cd.go.contrib.plugins.configrepo.groovy.api.gitlab.PullRequest;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.GitLab;
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Attributes;

import java.io.IOException;
import java.util.List;

import static java.lang.String.format;

public class GitLabRefProvider implements RefProvider {

    private final GitLabService gitlab;

    private final GitLab config;

    public static GitLabRefProvider create(Attributes.GitLabMR attrs) {
        final GitLab config = attrs.asConnectionConfig();
        return new GitLabRefProvider(Client.get(config), config);
    }

    public GitLabRefProvider(GitLabService gitlab, GitLab config) {
        this.gitlab = gitlab;
        this.config = config;
    }

    @Override
    public List<PullRequest> fetch() {
        try {
            return gitlab.allPullRequests(config.fullRepoName);
        } catch (IOException e) {
            throw new RuntimeException(format("Failed to fetch pull request information from GitLab [%s/%s]", config.serverBaseUrl, config.fullRepoName), e);
        }
    }
}
