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

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import java.util.function.Consumer;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.connection.Type.Bitbucket;
import static java.lang.String.format;
import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.StringUtils.isAllEmpty;
import static org.apache.commons.lang3.StringUtils.isNoneEmpty;

public class Bitbucket implements ConnectionConfig {

    public Bitbucket() {
    }

    public Bitbucket(Consumer<Bitbucket> config) {
        this();
        config.accept(this);
    }

    /** This is the `{workspace_slug}/{repo_slug}` representing the git repository hosted on BitBucket */
    @NotBlank(message = "`bitbucket {}` block requires `fullRepoName` (string), set with `fullRepoName = 'workspace/repo'`")
    public String fullRepoName;

    public String apiUser;

    public String apiPass;

    @Override
    public Type type() {
        return Bitbucket;
    }

    @Override
    public String identifier() {
        return format("%s[%s]", type(), fullRepoName);
    }

    @SuppressWarnings("unused")
    @AssertTrue(message = "When configuring authentication in a `bitbucket {}` block, you must set both `apiUser` and `apiPass` (this can be either an auth token or user password)")
    public boolean isValidAuth() {
        final String normalizedUsername = requireNonNullElse(apiUser, "").trim();
        return isAllEmpty(normalizedUsername, apiPass) || isNoneEmpty(normalizedUsername, apiPass);
    }
}
