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
import cd.go.contrib.plugins.configrepo.groovy.api.bitbucketserver.BitbucketSelfHostedService;
import cd.go.contrib.plugins.configrepo.groovy.api.bitbucketserver.PullRequest;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.BitbucketSelfHosted;
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Attributes;

import java.io.IOException;
import java.util.List;

import static java.lang.String.format;

public class BitbucketSelfHostedRefProvider implements RefProvider {

    private final BitbucketSelfHostedService bitbucket;

    private final BitbucketSelfHosted config;

    public static RefProvider create(Attributes.BitbucketSelfHostedPR attrs) {
        final BitbucketSelfHosted config = attrs.asConnectionConfig();
        return new BitbucketSelfHostedRefProvider(Client.get(config), config);
    }

    public BitbucketSelfHostedRefProvider(BitbucketSelfHostedService bitbucket, BitbucketSelfHosted config) {
        this.bitbucket = bitbucket;
        this.config = config;
    }

    @Override
    public List<PullRequest> fetch() {
        try {
            return bitbucket.allPullRequests(config.fullRepoName);
        } catch (IOException e) {
            throw new RuntimeException(format("Failed to fetch pull request information from BitBucket [%s/%s]", config.serverBaseUrl, config.fullRepoName), e);
        }
    }
}