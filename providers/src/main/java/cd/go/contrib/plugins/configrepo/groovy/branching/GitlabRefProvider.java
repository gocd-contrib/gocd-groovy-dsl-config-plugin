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
import cd.go.contrib.plugins.configrepo.groovy.branching.api.gitlab.GitlabService;
import cd.go.contrib.plugins.configrepo.groovy.branching.api.gitlab.PullRequest;
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Gitlab;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class GitlabRefProvider implements RefProvider {

    private final String namespace;

    private final String repo;

    private final String baseUrl;

    private final String apiAuthToken;

    public GitlabRefProvider(String baseUrl, String fullRepoName, String apiAuthToken) {
        final Map.Entry<String, String> parsed = BranchHelper.parse(fullRepoName);

        this.namespace = parsed.getKey();
        this.repo = parsed.getValue();
        this.baseUrl = baseUrl;
        this.apiAuthToken = apiAuthToken;
    }

    public static GitlabRefProvider create(Gitlab attrs) {
        return new GitlabRefProvider(attrs.serverBaseUrl, attrs.fullRepoName, attrs.apiAuthToken);
    }

    @Override
    public List<PullRequest> fetch() {
        final GitlabService api = apiCredentialsGiven() ?
                Api.gitlab(apiAuthToken, baseUrl) :
                Api.gitlab(baseUrl);

        try {
            return api.allPullRequests(namespace, repo);
        } catch (IOException e) {
            throw new RuntimeException(format("Failed to fetch pull request information from GitLab [%s/%s/%s]", baseUrl, namespace, repo), e);
        }
    }

    private boolean apiCredentialsGiven() {
        return isNotBlank(apiAuthToken);
    }

}
