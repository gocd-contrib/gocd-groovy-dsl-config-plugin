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

import javax.validation.constraints.NotBlank;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Attributes.Type.bitbucketcloud;

public class BitBucketCloud extends Attributes {

    /** This is the `{workspace_slug}/{repo_slug}` representing the git repository hosted on BitBucket */
    @NotBlank(message = "`bitbucket {}` block requires `fullRepoName` (string), set with `fullRepoName = 'workspace/repo'`")
    public String fullRepoName;

    public String apiUser;

    public String apiPass;

    @Override
    public Type type() {
        return bitbucketcloud;
    }
}
