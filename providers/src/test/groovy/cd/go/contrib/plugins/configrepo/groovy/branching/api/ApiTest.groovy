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

package cd.go.contrib.plugins.configrepo.groovy.branching.api

import cd.go.contrib.plugins.configrepo.groovy.branching.api.bitbucketcloud.BitBucketCloudService
import cd.go.contrib.plugins.configrepo.groovy.branching.api.bitbucketserver.BitBucketServerService
import cd.go.contrib.plugins.configrepo.groovy.branching.api.exceptions.ClientError
import cd.go.contrib.plugins.configrepo.groovy.branching.api.github.GithubService
import cd.go.contrib.plugins.configrepo.groovy.branching.api.gitlab.GitlabService
import cd.go.contrib.plugins.configrepo.groovy.branching.api.mocks.MockedHttp
import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

import static groovy.json.JsonOutput.toJson
import static java.lang.String.format
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertThrows

class ApiTest {
  @Nested
  class Github {
    public static final String PAGING_HEADER = GithubService.PAGINATION_HEADER

    @Test
    void 'throws error on failure response'() {
      GithubService api = Api.github(new MockedHttp().
        GET('/repos/gocd/gocd/pulls', { r ->
          r.code = 422
          r.body = "dude, I'm dead."
        })
      )

      assertThrows(ClientError.class, { -> api.allPullRequests('gocd', 'gocd') })
    }

    @Test
    void 'fetches pull requests'() {
      GithubService api = Api.github(
        new MockedHttp().GET('/repos/gocd/gocd/pulls', { r ->
          r.body = '[ { "number": 1, "state": "open", "base": { "repo": { "clone_url": "git.repo" } } } ]'
        })
      )
      def prs = api.allPullRequests('gocd', 'gocd')

      assertEquals(1, prs.size())
      assertEquals('refs/pull/1/head', prs.get(0).ref())
      assertEquals('git.repo', prs.get(0).url())
    }

    @Test
    void 'fetches pull requests over multiple pages'() {
      GithubService api = Api.github(new MockedHttp().
        GET('/repos/gocd/gocd/pulls', { r ->
          r.headers.add(PAGING_HEADER, '<https://api.github.com/repositories/1234/pulls?page=2>; rel="next"')
          r.body = '[ { "number": 1, "state": "open", "base": { "repo": { "clone_url": "git.repo" } } } ]'
        }).
        GET('/repos/gocd/gocd/pulls?page=2', { r ->
          r.headers.add(PAGING_HEADER, '<https://api.github.com/repositories/1234/pulls?page=3>; rel="next"')
          r.body = '[ { "number": 2, "state": "open", "base": { "repo": { "clone_url": "git.repo" } } } ]'
        }).
        GET('/repos/gocd/gocd/pulls?page=3', { r ->
          r.headers.add(PAGING_HEADER, '<https://api.github.com/repositories/1234/pulls?page=2>; rel="prev"')
          r.body = '[ { "number": 3, "state": "open", "base": { "repo": { "clone_url": "git.repo" } } } ]'
        })
      )
      def prs = api.allPullRequests('gocd', 'gocd')

      assertEquals(3, prs.size())

      assertEquals('refs/pull/1/head', prs.get(0).ref())
      assertEquals('git.repo', prs.get(0).url())

      assertEquals('refs/pull/2/head', prs.get(1).ref())
      assertEquals('git.repo', prs.get(1).url())

      assertEquals('refs/pull/3/head', prs.get(2).ref())
      assertEquals('git.repo', prs.get(2).url())
    }

    @Test
    void 'extracts other metadata from pull request'() {
      GithubService api = Api.github(
        new MockedHttp().GET('/repos/gocd/gocd/pulls', { r ->
          r.body = toJson([[
            number: 1, state: 'open', base: [repo: [clone_url: 'git.repo']],
            labels: [[name: 'red'], [name: 'green']],
            title : 'Such a fancy PR',
            user  : [login: 'the-boss'],
            _links: [html: [href: 'https://greathub.com/pull/my/finger']]
          ]])
        })
      )
      def prs = api.allPullRequests('gocd', 'gocd')

      assertEquals(1, prs.size())

      def pr = prs.get(0)
      assertEquals('refs/pull/1/head', pr.ref())
      assertEquals('git.repo', pr.url())
      assertEquals('Such a fancy PR', pr.title())
      assertEquals('the-boss', pr.author())
      assertEquals('https://greathub.com/pull/my/finger', pr.showUrl())
      assertEquals(['red', 'green'], pr.labels())
    }
  }

  @Nested
  class Gitlab {
    public static final String PAGING_HEADER = GitlabService.PAGINATION_HEADER

    @Test
    void 'throws error on failure response'() {
      GitlabService api = Api.gitlab(new MockedHttp().
        GET('/api/v4/projects/gocd%2Fgocd/merge_requests?state=opened', { r ->
          r.code = 422
          r.body = "dude, I'm dead."
        }), null
      )

      assertThrows(ClientError.class, { -> api.allPullRequests('gocd', 'gocd') })
    }

    @Test
    void 'fetches pull requests'() {
      GitlabService api = Api.gitlab(
        new MockedHttp().GET('/api/v4/projects/gocd%2Fgocd/merge_requests?state=opened', { r ->
          r.body = '[ {"iid": 5, "web_url": "https://repo.com/gocd/gocd/-/merge_requests/5"} ]'
        }), null
      )
      def prs = api.allPullRequests('gocd', 'gocd')

      assertEquals(1, prs.size())
      assertEquals('refs/merge-requests/5/head', prs.get(0).ref())
      assertEquals('https://repo.com/gocd/gocd', prs.get(0).url())
    }

    @Test
    void 'fetches pull requests over multiple pages'() {
      GitlabService api = Api.gitlab(new MockedHttp().
        GET('/api/v4/projects/gocd%2Fgocd/merge_requests?state=opened', { r ->
          r.headers.add(PAGING_HEADER, '2')
          r.body = '[ {"iid": 5, "web_url": "https://repo.com/gocd/gocd/-/merge_requests/5"} ]'
        }).
        GET('/api/v4/projects/gocd%2Fgocd/merge_requests?state=opened&page=2', { r ->
          r.headers.add(PAGING_HEADER, '3')
          r.body = '[ {"iid": 6, "web_url": "https://repo.com/gocd/gocd/-/merge_requests/6"} ]'
        }).
        GET('/api/v4/projects/gocd%2Fgocd/merge_requests?state=opened&page=3', { r ->
          r.body = '[ {"iid": 7, "web_url": "https://repo.com/gocd/gocd/-/merge_requests/7"} ]'
        }), null
      )
      def prs = api.allPullRequests('gocd', 'gocd')

      assertEquals(3, prs.size())

      assertEquals('refs/merge-requests/5/head', prs.get(0).ref())
      assertEquals('https://repo.com/gocd/gocd', prs.get(0).url())

      assertEquals('refs/merge-requests/6/head', prs.get(1).ref())
      assertEquals('https://repo.com/gocd/gocd', prs.get(1).url())

      assertEquals('refs/merge-requests/7/head', prs.get(2).ref())
      assertEquals('https://repo.com/gocd/gocd', prs.get(2).url())
    }

    @Test
    void 'extracts other metadata from pull request'() {
      GitlabService api = Api.gitlab(
        new MockedHttp().GET('/api/v4/projects/gocd%2Fgocd/merge_requests?state=opened', { r ->
          r.body = toJson([[
            iid   : 5, web_url: 'https://repo.com/gocd/gocd/-/merge_requests/5',
            labels: ['red', 'green'],
            title : 'Such a fancy PR',
            author: [username: 'the-boss'],
          ]])
        }), null
      )
      def prs = api.allPullRequests('gocd', 'gocd')

      assertEquals(1, prs.size())

      def pr = prs.get(0)
      assertEquals('refs/merge-requests/5/head', pr.ref())
      assertEquals('https://repo.com/gocd/gocd', pr.url())
      assertEquals('Such a fancy PR', pr.title())
      assertEquals('the-boss', pr.author())
      assertEquals('https://repo.com/gocd/gocd/-/merge_requests/5', pr.showUrl())
      assertEquals(['red', 'green'], pr.labels())
    }
  }

  @Nested
  class BitBucketCloud {
    @Test
    void 'throws error on failure response'() {
      BitBucketCloudService api = Api.bitbucketcloud(new MockedHttp().
        GET('/2.0/repositories/gocd/gocd/pullrequests?state=OPEN', { r ->
          r.code = 422
          r.body = "dude, I'm dead."
        })
      )

      assertThrows(ClientError.class, { -> api.allPullRequests('gocd', 'gocd') })
    }

    @Test
    void 'fetches pull requests'() {
      BitBucketCloudService api = Api.bitbucketcloud(
        new MockedHttp().GET('/2.0/repositories/gocd/gocd/pullrequests?state=OPEN', { r ->
          r.body = toJson([
            page  : 1,
            values: [
              [source: internal("change"), destination: master("gocd/gocd")]
            ]
          ])
        })
      )
      def prs = api.allPullRequests('gocd', 'gocd')

      assertEquals(1, prs.size())
      assertEquals('refs/heads/change', prs.get(0).ref())
      assertEquals('https://bb.org/gocd/gocd', prs.get(0).url())
    }

    @Test
    void 'fetches pull requests over multiple pages'() {
      BitBucketCloudService api = Api.bitbucketcloud(
        new MockedHttp().
          GET('/2.0/repositories/gocd/gocd/pullrequests?state=OPEN', { r ->
            r.body = toJson([
              page  : 1,
              next  : "https://blah/blah?page=2",
              values: [
                [source: internal("change"), destination: master("gocd/gocd")]
              ]
            ])
          }).
          GET('/2.0/repositories/gocd/gocd/pullrequests?state=OPEN&page=2', { r ->
            r.body = toJson([
              page  : 2,
              next  : "https://blah/blah?page=3",
              values: [
                [source: forked("from", "fork/gocd"), destination: master("gocd/gocd")]
              ]
            ])
          }).
          GET('/2.0/repositories/gocd/gocd/pullrequests?state=OPEN&page=3', { r ->
            r.body = toJson([
              page  : 3,
              values: [
                [source: internal("another"), destination: master("gocd/gocd")]
              ]
            ])
          })
      )

      def prs = api.allPullRequests('gocd', 'gocd')

      assertEquals(3, prs.size())

      assertEquals('refs/heads/change', prs.get(0).ref())
      assertEquals('https://bb.org/gocd/gocd', prs.get(0).url())

      assertEquals('refs/heads/from', prs.get(1).ref())
      assertEquals('https://bb.org/fork/gocd', prs.get(1).url())

      assertEquals('refs/heads/another', prs.get(2).ref())
      assertEquals('https://bb.org/gocd/gocd', prs.get(2).url())
    }

    @Test
    void 'extracts other metadata from pull request'() {
      BitBucketCloudService api = Api.bitbucketcloud(
        new MockedHttp().GET('/2.0/repositories/gocd/gocd/pullrequests?state=OPEN', { r ->
          r.body = toJson([
            page  : 1,
            values: [[
              source: internal("change"), destination: master("gocd/gocd"),
              title : 'Such a fancy PR',
              author: [nickname: 'the-boss'],
              links : [html: [href: 'https://greathub.com/pull/my/finger']],
            ]]
          ])
        })
      )

      def prs = api.allPullRequests('gocd', 'gocd')

      assertEquals(1, prs.size())

      def pr = prs.get(0)
      assertEquals('refs/heads/change', pr.ref())
      assertEquals('https://bb.org/gocd/gocd', pr.url())
      assertEquals('Such a fancy PR', pr.title())
      assertEquals('the-boss', pr.author())
      assertEquals('https://greathub.com/pull/my/finger', pr.showUrl())
    }

    def internal(String branch) {
      return mergePoint(branch, null)
    }

    def forked(String branch, String reponame) {
      return mergePoint(branch, reponame)
    }

    def master(String reponame) {
      return mergePoint("master", reponame)
    }

    private def mergePoint(String branch, String reponame) {
      if (StringUtils.isBlank(reponame)) {
        return [branch: [name: branch]]
      }
      return [
        branch    : [name: branch],
        repository: [links: [html: [href: format("https://bb.org/%s", reponame)]]]
      ]
    }
  }

  @Nested
  class BitBucketServer {
    private static final BASE_URL = "http://my-bucket.com"

    @Test
    void 'throws error on failure response'() {
      BitBucketServerService api = Api.bitbucketserver(new MockedHttp().
        GET('/rest/api/1.0/projects/gocd/repos/gocd/pull-requests?state=OPEN', { r ->
          r.code = 422
          r.body = "dude, I'm dead."
        }), BASE_URL
      )

      assertThrows(ClientError.class, { -> api.allPullRequests('gocd', 'gocd') })
    }

    @Test
    void 'fetches pull requests'() {
      BitBucketServerService api = Api.bitbucketserver(
        new MockedHttp().GET('/rest/api/1.0/projects/gocd/repos/gocd/pull-requests?state=OPEN', { r ->
          r.body = toJson([
            isLastPage: true,
            start     : 0,
            values    : [[fromRef: from("change", "gocd/gocd")]]
          ])
        }), BASE_URL
      )
      def prs = api.allPullRequests('gocd', 'gocd')

      assertEquals(1, prs.size())
      assertEquals('refs/heads/change', prs.get(0).ref())
      assertEquals(repoUrl('gocd/gocd'), prs.get(0).url())
    }

    @Test
    void 'fetches pull requests over multiple pages'() {
      BitBucketServerService api = Api.bitbucketserver(new MockedHttp().
        GET('/rest/api/1.0/projects/gocd/repos/gocd/pull-requests?state=OPEN', { r ->
          r.body = toJson([
            isLastPage   : false,
            nextPageStart: 1,
            values       : [
              [fromRef: from("change", "gocd/gocd")]
            ]
          ])
        }).
        GET('/rest/api/1.0/projects/gocd/repos/gocd/pull-requests?state=OPEN&start=1', { r ->
          r.body = toJson([
            isLastPage   : false,
            nextPageStart: 2,
            values       : [
              [fromRef: from("from", "fork/gocd")]
            ]
          ])
        }).
        GET('/rest/api/1.0/projects/gocd/repos/gocd/pull-requests?state=OPEN&start=2', { r ->
          r.body = toJson([
            isLastPage: true,
            values    : [
              [fromRef: from("another", "gocd/gocd")]
            ]
          ])
        }), BASE_URL
      )

      def prs = api.allPullRequests('gocd', 'gocd')

      assertEquals(3, prs.size())

      assertEquals('refs/heads/change', prs.get(0).ref())
      assertEquals(repoUrl('gocd/gocd'), prs.get(0).url())

      assertEquals('refs/heads/from', prs.get(1).ref())
      assertEquals(repoUrl('fork/gocd'), prs.get(1).url())

      assertEquals('refs/heads/another', prs.get(2).ref())
      assertEquals(repoUrl('gocd/gocd'), prs.get(2).url())
    }

    @Test
    void 'extracts other metadata from pull request'() {
      BitBucketServerService api = Api.bitbucketserver(
        new MockedHttp().GET('/rest/api/1.0/projects/gocd/repos/gocd/pull-requests?state=OPEN', { r ->
          r.body = toJson([
            isLastPage: true,
            start     : 0,
            values    : [[
              fromRef: from("change", "gocd/gocd"),
              title  : 'Such a fancy PR',
              author : [user: [name: 'the-boss']],
              links  : [self: [[href: 'https://greathub.com/pull/my/finger']]],
            ]]
          ])
        }), BASE_URL
      )

      def prs = api.allPullRequests('gocd', 'gocd')

      assertEquals(1, prs.size())

      def pr = prs.get(0)
      assertEquals('refs/heads/change', pr.ref())
      assertEquals(repoUrl('gocd/gocd'), pr.url())
      assertEquals('Such a fancy PR', pr.title())
      assertEquals('the-boss', pr.author())
      assertEquals('https://greathub.com/pull/my/finger', pr.showUrl())
    }

    def from(String branch, String reponame) {
      return mergePoint(branch, reponame)
    }

    def repoUrl(String name) {
      return [BASE_URL, name].join("/")
    }

    private def mergePoint(String branch, String reponame) {
      return [
        id        : format("refs/heads/%s", branch),
        repository: [links: [clone: [[href: repoUrl(reponame), name: "http"]]]]
      ]
    }
  }
}