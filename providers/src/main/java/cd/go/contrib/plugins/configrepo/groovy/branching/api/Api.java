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

package cd.go.contrib.plugins.configrepo.groovy.branching.api;

import cd.go.contrib.plugins.configrepo.groovy.branching.api.bitbucketcloud.BitBucketCloudService;
import cd.go.contrib.plugins.configrepo.groovy.branching.api.bitbucketserver.BitBucketServerService;
import cd.go.contrib.plugins.configrepo.groovy.branching.api.github.GithubService;
import cd.go.contrib.plugins.configrepo.groovy.branching.api.gitlab.GitlabService;
import cd.go.contrib.plugins.configrepo.groovy.branching.api.helpers.AuthHeaderInterceptor;
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Gitlab;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.validation.constraints.NotBlank;
import java.util.function.Consumer;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Api {

    public static GithubService github() {
        return github(InternalClient.get(null));
    }

    public static GithubService github(final String oauth2) {
        return github(InternalClient.get(c -> c.addInterceptor(new AuthHeaderInterceptor("token", oauth2))));
    }

    public static GithubService github(final Call.Factory client) {
        return new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .callFactory(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(GithubService.class);
    }

    public static GitlabService gitlab(final String baseUrl) {
        return gitlab(InternalClient.get(null), baseUrl);
    }

    public static GitlabService gitlab(final String oauth2, final String baseUrl) {
        return gitlab(InternalClient.get(c -> c.addInterceptor(new AuthHeaderInterceptor("Bearer", oauth2))), baseUrl);
    }

    public static GitlabService gitlab(final Call.Factory client, final String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(isBlank(baseUrl) ? Gitlab.GITLAB_SAAS : baseUrl)
                .callFactory(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(GitlabService.class);
    }

    public static BitBucketCloudService bitbucketcloud() {
        return bitbucketcloud(InternalClient.get(null));
    }

    public static BitBucketCloudService bitbucketcloud(final String user, final String secret) {
        return bitbucketcloud(InternalClient.get(c -> c.addInterceptor(AuthHeaderInterceptor.basic(user, secret))));
    }

    public static BitBucketCloudService bitbucketcloud(final Call.Factory client) {
        return new Retrofit.Builder()
                .baseUrl("https://api.bitbucket.org")
                .callFactory(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(BitBucketCloudService.class);
    }


    public static BitBucketServerService bitbucketserver(@NotBlank final String baseUrl) {
        return bitbucketserver(InternalClient.get(null), baseUrl);
    }

    public static BitBucketServerService bitbucketserver(@NotBlank String oauth2, @NotBlank final String baseUrl) {
        return bitbucketserver(InternalClient.get(
                c -> c.addInterceptor(new AuthHeaderInterceptor("Bearer", oauth2))
                ), baseUrl
        );
    }

    public static BitBucketServerService bitbucketserver(final Call.Factory client, @NotBlank final String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .callFactory(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(BitBucketServerService.class);
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
