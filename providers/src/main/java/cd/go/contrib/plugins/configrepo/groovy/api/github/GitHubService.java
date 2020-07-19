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

package cd.go.contrib.plugins.configrepo.groovy.api.github;

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

public interface GitHubService {

    String PAGINATION_HEADER = "Link";

    @Headers({"Accept: application/vnd.github.v3+json"})
    @GET("repos/{owner}/{repo}/pulls")
    Call<List<PullRequest>> pullRequests(@Path("owner") String owner, @Path("repo") String repo);

    @Headers({"Accept: application/vnd.github.v3+json"})
    @GET("repos/{owner}/{repo}/pulls")
    Call<List<PullRequest>> pullRequests(@Path("owner") String owner, @Path("repo") String repo, @Query("page") int page);

    default List<PullRequest> allPullRequests(String fullRepoName) throws IOException {
        final Map.Entry<String, String> parsed = BranchHelper.parse(fullRepoName);

        final String owner = parsed.getKey();
        final String repo = parsed.getValue();

        final Response<List<PullRequest>> initial = pullRequests(owner, repo).execute();
        ResponseFailure.throwOnFailure(initial);

        Pagination pg = readPagination(initial);
        final List<PullRequest> result = new ArrayList<>(Objects.requireNonNull(initial.body()));

        while (pg != null && pg.hasNext()) {
            final Response<List<PullRequest>> nextPage = pullRequests(owner, repo, pg.next()).execute();
            ResponseFailure.throwOnFailure(nextPage);

            pg = readPagination(nextPage);
            result.addAll(Objects.requireNonNull(nextPage.body()));
        }

        return result;
    }

    @Headers({"Accept: application/vnd.github.v3+json"})
    @POST("repos/{owner}/{repo}/statuses/{sha}")
    Call<Void> createCommitStatus(@Path("owner") String owner, @Path("repo") String repo, @Path("sha") String sha, @Body CommitStatusGitHub payload);

    default void publishCommitStatus(String fullRepoName, String sha, CommitStatusGitHub payload) throws IOException {
        final Map.Entry<String, String> parsed = BranchHelper.parse(fullRepoName);

        final String owner = parsed.getKey();
        final String repo = parsed.getValue();

        final Response<Void> resp = createCommitStatus(owner, repo, sha, payload).execute();
        ResponseFailure.throwOnFailure(resp);
    }

    private Pagination readPagination(Response<?> res) {
        if (res.headers().names().contains(PAGINATION_HEADER)) {
            return new Pagination(res.headers().get(PAGINATION_HEADER));
        }
        return null;
    }
}
