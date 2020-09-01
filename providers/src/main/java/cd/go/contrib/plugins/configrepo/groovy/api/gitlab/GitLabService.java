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

package cd.go.contrib.plugins.configrepo.groovy.api.gitlab;

import cd.go.contrib.plugins.configrepo.groovy.api.exceptions.ResponseFailure;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface GitLabService {

    String PAGINATION_HEADER = "x-next-page";

    @GET("api/v4/projects/{id}/merge_requests?state=opened")
    Call<List<PullRequest>> pullRequests(@Path("id") String id);

    @GET("api/v4/projects/{id}/merge_requests?state=opened")
    Call<List<PullRequest>> pullRequests(@Path("id") String id, @Query("page") int page);

    default List<PullRequest> allPullRequests(String fullRepoName) throws IOException {
        final Response<List<PullRequest>> initial = pullRequests(fullRepoName).execute();
        ResponseFailure.throwOnFailure(initial);

        Pagination pg = readPagination(initial);
        final List<PullRequest> result = new ArrayList<>(Objects.requireNonNull(initial.body()));

        while (pg != null && pg.hasNext()) {
            final Response<List<PullRequest>> nextPage = pullRequests(fullRepoName, pg.next()).execute();
            ResponseFailure.throwOnFailure(nextPage);

            pg = readPagination(nextPage);
            result.addAll(Objects.requireNonNull(nextPage.body()));
        }

        return result;
    }

    @POST("api/v4/projects/{id}/statuses/{sha}")
    Call<Void> createCommitStatus(@Path("id") String id, @Path("sha") String sha, @Query("state") CommitStatusGitLab.States state, @Query("context") String context, @Query("target_url") String url);

    default void publishCommitStatus(String fullRepoName, String sha, CommitStatusGitLab result) throws IOException {
        final Response<Void> resp = createCommitStatus(fullRepoName, sha, result.state, result.context, result.targetUrl).execute();
        ResponseFailure.throwOnFailure(resp);
    }

    private Pagination readPagination(Response<?> res) {
        if (res.headers().names().contains(PAGINATION_HEADER)) {
            return new Pagination(res.headers().get(PAGINATION_HEADER));
        }
        return null;
    }
}
