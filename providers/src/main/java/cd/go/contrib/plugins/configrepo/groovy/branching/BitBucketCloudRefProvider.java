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
import cd.go.contrib.plugins.configrepo.groovy.branching.api.bitbucketcloud.BitBucketCloudService;
import cd.go.contrib.plugins.configrepo.groovy.branching.api.bitbucketcloud.PullRequest;
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.BitBucketCloud;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

public class BitBucketCloudRefProvider implements RefProvider {

    private final String namespace;

    private final String repo;

    private final String apiUser;

    private final String apiPass;

    public BitBucketCloudRefProvider(String fullRepoName, String apiUser, String apiPass) {
        final Entry<String, String> parsed = BranchHelper.parse(fullRepoName);

        this.namespace = parsed.getKey();
        this.repo = parsed.getValue();
        this.apiUser = apiUser;
        this.apiPass = apiPass;
    }

    public static RefProvider create(BitBucketCloud attrs) {
        return new BitBucketCloudRefProvider(attrs.fullRepoName, attrs.apiUser, attrs.apiPass);
    }

    @Override
    public List<PullRequest> fetch() {
        final BitBucketCloudService bitbucket = apiCredentialsGiven() ? Api.bitbucketcloud(apiUser, apiPass) : Api.bitbucketcloud();
        try {
            return bitbucket.allPullRequests(namespace, repo);
        } catch (IOException e) {
            throw new RuntimeException(format("Failed to fetch pull request information from BitBucket Cloud [%s/%s]", namespace, repo), e);
        }
    }

    private boolean apiCredentialsGiven() {
        return isNoneBlank(apiUser, apiPass);
    }
}
