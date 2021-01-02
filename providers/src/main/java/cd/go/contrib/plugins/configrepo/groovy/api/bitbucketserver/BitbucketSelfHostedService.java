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

package cd.go.contrib.plugins.configrepo.groovy.api.bitbucketserver;

import cd.go.contrib.plugins.configrepo.groovy.api.exceptions.ResponseFailure;
import cd.go.contrib.plugins.configrepo.groovy.branching.BranchHelper;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface BitbucketSelfHostedService {

    @GET("rest/api/1.0/projects/{workspace}/repos/{repo}/pull-requests?state=OPEN")
    Call<Envelope<PullRequest>> pullRequests(@Path("workspace") String workspace, @Path("repo") String repo);

    @GET("rest/api/1.0/projects/{workspace}/repos/{repo}/pull-requests?state=OPEN")
    Call<Envelope<PullRequest>> pullRequests(@Path("workspace") String workspace, @Path("repo") String repo, @Query("start") int page);

    default List<PullRequest> allPullRequests(String fullRepoName) throws IOException {
        final Map.Entry<String, String> parts = BranchHelper.parse(fullRepoName);
        final String workspace = parts.getKey();
        final String repo = parts.getValue();

        final Response<Envelope<PullRequest>> initial = pullRequests(workspace, repo).execute();
        ResponseFailure.throwOnFailure(initial);

        Envelope<PullRequest> body = Objects.requireNonNull(initial.body());
        final List<PullRequest> result = new ArrayList<>(body.values);

        while (body.hasNext()) {
            final Response<Envelope<PullRequest>> nextPage = pullRequests(workspace, repo, body.nextPage()).execute();
            ResponseFailure.throwOnFailure(nextPage);

            body = Objects.requireNonNull(nextPage.body());
            result.addAll(body.values);
        }

        return result;
    }

    @POST("rest/build-status/1.0/commits/{sha}")
    Call<Void> createCommitStatus(@Path("sha") String sha, @Body CommitStatusBitbucketSelfHosted payload);

    default void publishCommitStatus(String sha, CommitStatusBitbucketSelfHosted payload) throws IOException {
        final Response<Void> resp = createCommitStatus(sha, payload).execute();
        ResponseFailure.throwOnFailure(resp);
    }
}
