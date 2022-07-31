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

package cd.go.contrib.plugins.configrepo.groovy.api;

import cd.go.contrib.plugins.configrepo.groovy.api.bitbucketcloud.BitbucketService;
import cd.go.contrib.plugins.configrepo.groovy.api.bitbucketserver.BitbucketSelfHostedService;
import cd.go.contrib.plugins.configrepo.groovy.api.github.GitHubService;
import cd.go.contrib.plugins.configrepo.groovy.api.gitlab.GitLabService;
import cd.go.contrib.plugins.configrepo.groovy.api.helpers.AuthHeaderInterceptor;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.Bitbucket;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.BitbucketSelfHosted;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.GitHub;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.GitLab;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import jakarta.validation.constraints.NotBlank;
import java.util.function.Consumer;

import static org.apache.commons.lang3.StringUtils.*;

public class Client {

    public static GitHubService get(GitHub config) {
        return null != config.apiAuthToken ? Client.github(config.apiAuthToken) : Client.github();
    }

    public static GitLabService get(GitLab config) {
        return isNotBlank(config.apiAuthToken) ?
                Client.gitlab(config.apiAuthToken, config.serverBaseUrl) :
                Client.gitlab(config.serverBaseUrl);
    }

    public static BitbucketService get(Bitbucket config) {
        return isNoneBlank(config.apiUser, config.apiPass) ?
                Client.bitbucketcloud(config.apiUser, config.apiPass) :
                Client.bitbucketcloud();
    }

    public static BitbucketSelfHostedService get(BitbucketSelfHosted config) {
        return isNotBlank(config.apiAuthToken) ?
                Client.bitbucketserver(config.apiAuthToken, config.serverBaseUrl) :
                Client.bitbucketserver(config.serverBaseUrl);
    }

    public static GitHubService github() {
        return github(InternalClient.get(null));
    }

    public static GitHubService github(final String oauth2) {
        return github(InternalClient.get(c -> c.addInterceptor(new AuthHeaderInterceptor("token", oauth2))));
    }

    public static GitHubService github(final Call.Factory client) {
        return new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .callFactory(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(GitHubService.class);
    }

    public static GitLabService gitlab(final String baseUrl) {
        return gitlab(InternalClient.get(null), baseUrl);
    }

    public static GitLabService gitlab(final String oauth2, final String baseUrl) {
        return gitlab(InternalClient.get(c -> c.addInterceptor(new AuthHeaderInterceptor("Bearer", oauth2))), baseUrl);
    }

    public static GitLabService gitlab(final Call.Factory client, final String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(isBlank(baseUrl) ? GitLab.GITLAB_SAAS : baseUrl)
                .callFactory(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(GitLabService.class);
    }

    public static BitbucketService bitbucketcloud() {
        return bitbucketcloud(InternalClient.get(null));
    }

    public static BitbucketService bitbucketcloud(final String user, final String secret) {
        return bitbucketcloud(InternalClient.get(c -> c.addInterceptor(AuthHeaderInterceptor.basic(user, secret))));
    }

    public static BitbucketService bitbucketcloud(final Call.Factory client) {
        return new Retrofit.Builder()
                .baseUrl("https://api.bitbucket.org")
                .callFactory(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(BitbucketService.class);
    }


    public static BitbucketSelfHostedService bitbucketserver(@NotBlank final String baseUrl) {
        return bitbucketserver(InternalClient.get(null), baseUrl);
    }

    public static BitbucketSelfHostedService bitbucketserver(@NotBlank String oauth2, @NotBlank final String baseUrl) {
        return bitbucketserver(InternalClient.get(
                c -> c.addInterceptor(new AuthHeaderInterceptor("Bearer", oauth2))
                ), baseUrl
        );
    }

    public static BitbucketSelfHostedService bitbucketserver(final Call.Factory client, @NotBlank final String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .callFactory(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(BitbucketSelfHostedService.class);
    }

    private static class InternalClient {

        private InternalClient() {
        }

        private static class RootClient {

            // Bill Pugh Singleton for thread-safe lazy-init
            private static final OkHttpClient INSTANCE = new OkHttpClient();
        }

        private static OkHttpClient instance() {
            return RootClient.INSTANCE;
        }

        private static Call.Factory get(final Consumer<OkHttpClient.Builder> configure) {
            final OkHttpClient.Builder builder = instance().newBuilder();
            if (null != configure) {
                configure.accept(builder);
            }
            return builder.build();
        }
    }
}
