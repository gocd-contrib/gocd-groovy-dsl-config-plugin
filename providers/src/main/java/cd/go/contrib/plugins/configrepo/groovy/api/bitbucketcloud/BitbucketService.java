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

package cd.go.contrib.plugins.configrepo.groovy.api.bitbucketcloud;

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

public interface BitbucketService {

    @GET("2.0/repositories/{workspace}/{repo}/pullrequests?state=OPEN")
    Call<Envelope<PullRequest>> pullRequests(@Path("workspace") String workspace, @Path("repo") String repo);

    @GET("2.0/repositories/{workspace}/{repo}/pullrequests?state=OPEN")
    Call<Envelope<PullRequest>> pullRequests(@Path("workspace") String workspace, @Path("repo") String repo, @Query("page") int page);

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

    @POST("2.0/repositories/{workspace}/{repo}/commit/{sha}/statuses/build")
    Call<Void> createCommitStatus(@Path("workspace") String workspace, @Path("repo") String repo, @Path("sha") String sha, @Body CommitStatusBitbucket payload);

    default void publishCommitStatus(String fullRepoName, String sha, CommitStatusBitbucket payload) throws IOException {
        final Map.Entry<String, String> parsed = BranchHelper.parse(fullRepoName);

        final String owner = parsed.getKey();
        final String repo = parsed.getValue();

        final Response<Void> resp = createCommitStatus(owner, repo, sha, payload).execute();
        ResponseFailure.throwOnFailure(resp);
    }
}
