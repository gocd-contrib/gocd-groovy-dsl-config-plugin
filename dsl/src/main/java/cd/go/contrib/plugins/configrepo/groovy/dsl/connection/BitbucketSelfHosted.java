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

package cd.go.contrib.plugins.configrepo.groovy.dsl.connection;

import jakarta.validation.constraints.NotBlank;
import java.util.function.Consumer;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.connection.Type.BitbucketSelfHosted;
import static java.lang.String.format;

public class BitbucketSelfHosted implements ConnectionConfig {

    public BitbucketSelfHosted() {
    }

    public BitbucketSelfHosted(Consumer<BitbucketSelfHosted> config) {
        this();
        config.accept(this);
    }

    /** This is the `{workspace_slug}/{repo_slug}` representing the git repository hosted on BitBucket */
    @NotBlank(message = "`bitbucketSelfHosted {}` block requires `fullRepoName` (string), set with `fullRepoName = 'workspace/repo'`")
    public String fullRepoName;

    /** This is base URL to the BitBucket server */
    @NotBlank(message = "`bitbucketSelfHosted {}` block requires `serverBaseUrl` (string), set with `serverBaseUrl = 'https://your.bitbucket.server'`")
    public String serverBaseUrl;

    @NotBlank(message = "`bitbucketSelfHosted {}` block requires `apiAuthToken` (string), set with `apiAuthToken = lookup('my.auth.secret')`")
    public String apiAuthToken;

    @Override
    public Type type() {
        return BitbucketSelfHosted;
    }

    @Override
    public String identifier() {
        return format("%s[%s][%s]", type(), fullRepoName, serverBaseUrl);
    }
}
