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

package cd.go.contrib.plugins.configrepo.groovy.branching;

import cd.go.contrib.plugins.configrepo.groovy.api.Client;
import cd.go.contrib.plugins.configrepo.groovy.api.bitbucketcloud.BitbucketService;
import cd.go.contrib.plugins.configrepo.groovy.api.bitbucketcloud.PullRequest;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.Bitbucket;
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Attributes;

import java.io.IOException;
import java.util.List;

import static java.lang.String.format;

public class BitbucketRefProvider implements RefProvider {

    private final BitbucketService bitbucket;

    private final Bitbucket config;

    public static RefProvider create(Attributes.BitbucketPR attrs) {
        final Bitbucket config = attrs.asConnectionConfig();
        return new BitbucketRefProvider(Client.get(config), config);
    }

    public BitbucketRefProvider(BitbucketService bitbucket, Bitbucket config) {
        this.bitbucket = bitbucket;
        this.config = config;
    }

    @Override
    public List<PullRequest> fetch() {
        try {
            return bitbucket.allPullRequests(config.fullRepoName);
        } catch (IOException e) {
            throw new RuntimeException(format("Failed to fetch pull request information from BitBucket Cloud [%s]", config.fullRepoName), e);
        }
    }
}
