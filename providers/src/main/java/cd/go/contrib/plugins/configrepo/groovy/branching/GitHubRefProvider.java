/*
 * Copyright 2021 ThoughtWorks, Inc.
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
import cd.go.contrib.plugins.configrepo.groovy.api.github.GitHubService;
import cd.go.contrib.plugins.configrepo.groovy.api.github.PullRequest;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.GitHub;
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Attributes;

import java.io.IOException;
import java.util.List;

import static java.lang.String.format;

public class GitHubRefProvider implements RefProvider {

    private final GitHubService github;

    private final GitHub config;

    public static GitHubRefProvider create(Attributes.GitHubPR attrs) {
        final GitHub config = attrs.asConnectionConfig();
        return new GitHubRefProvider(Client.get(config), config);
    }

    public GitHubRefProvider(GitHubService github, GitHub config) {
        this.github = github;
        this.config = config;
    }

    @Override
    public List<PullRequest> fetch() {
        try {
            return github.allPullRequests(config.fullRepoName);
        } catch (IOException e) {
            throw new RuntimeException(format("Failed to fetch pull request information from GitHub [%s]", config.fullRepoName), e);
        }
    }
}
