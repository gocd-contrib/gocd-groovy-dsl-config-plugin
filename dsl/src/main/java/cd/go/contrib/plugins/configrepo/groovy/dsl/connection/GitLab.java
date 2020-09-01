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

package cd.go.contrib.plugins.configrepo.groovy.dsl.connection;

import javax.validation.constraints.NotBlank;
import java.util.function.Consumer;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.connection.Type.GitLab;
import static java.lang.String.format;

public class GitLab implements ConnectionConfig {

    public GitLab() {
    }

    public GitLab(Consumer<GitLab> config) {
        this();
        config.accept(this);
    }

    public static final String GITLAB_SAAS = "https://gitlab.com";

    /** This is the `{group}/{repo_slug}` representing the git repository hosted on GitLab */
    @NotBlank(message = "`gitlab {}` block requires `fullRepoName` (string), set with `fullRepoName = 'group/repo'`")
    public String fullRepoName;

    /** This is base URL to the GitLab server */
    public String serverBaseUrl = GITLAB_SAAS;

    /**
     * RECOMMENDED: While optional, providing an Personal Access Token (oauth) here will provide higher limits on GitLab
     * and is required for private repositories. Be sure to assign the correct scopes for your token in GitLab.
     */
    public String apiAuthToken;

    @Override
    public Type type() {
        return GitLab;
    }

    @Override
    public String identifier() {
        return format("%s[%s][%s]", type(), fullRepoName, serverBaseUrl);
    }
}
