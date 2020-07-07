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

import cd.go.contrib.plugins.configrepo.groovy.branching.api.Api;
import cd.go.contrib.plugins.configrepo.groovy.branching.api.github.GithubService;
import cd.go.contrib.plugins.configrepo.groovy.branching.api.github.PullRequest;
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Github;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class GithubRefProvider implements RefProvider {

    public static GithubRefProvider create(Github attrs) {
        return new GithubRefProvider(attrs.fullRepoName, attrs.apiAuthToken);
    }

    private final String namespace;

    private final String repo;

    private final String authToken;

    public GithubRefProvider(@NotBlank String fullRepoName, String authToken) {
        final Map.Entry<String, String> parsed = BranchHelper.parse(fullRepoName);

        this.namespace = parsed.getKey();
        this.repo = parsed.getValue();
        this.authToken = authToken;
    }

    @Override
    public List<PullRequest> fetch() {
        final GithubService github = null != authToken ? Api.github(authToken) : Api.github();
        try {
            return github.allPullRequests(namespace, repo);
        } catch (IOException e) {
            throw new RuntimeException(format("Failed to fetch pull request information from GitHub [%s/%s]", namespace, repo), e);
        }
    }
}
