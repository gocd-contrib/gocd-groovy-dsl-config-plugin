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

package cd.go.contrib.plugins.configrepo.groovy.api.helpers

import cd.go.contrib.plugins.configrepo.groovy.api.Client
import cd.go.contrib.plugins.configrepo.groovy.api.mocks.MockedCall
import cd.go.contrib.plugins.configrepo.groovy.api.mocks.MockedInterceptorChain
import cd.go.contrib.plugins.configrepo.groovy.api.mocks.ResponseStub
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.jupiter.api.Test

import java.util.function.Consumer

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.*

class AuthHeaderInterceptorTest {
  @Test
  void 'basic() injects a basic auth header'() {
    def basic = AuthHeaderInterceptor.basic("user", "pass")

    def http = interceptorInvokingClient(basic, { req ->
      assertTrue(req.headers().names().contains("Authorization"), "no header entry ${req.headers().names()}")

      String expected = "Basic ${Base64.encoder.encodeToString("user:pass".bytes)}"
      assertEquals(expected, req.header("Authorization"), "value not match")
    })

    Client.bitbucketcloud(http).pullRequests("a", "b").execute()
    verify(http, times(1)).newCall(any(Request))
  }

  @Test
  void 'accepts arbitrary token and value'() {
    def auth = new AuthHeaderInterceptor("anything", "hello")

    def http = interceptorInvokingClient(auth, { req ->
      assertTrue(req.headers().names().contains("Authorization"), "no header entry ${req.headers().names()}")

      assertEquals("anything hello", req.header("Authorization"), "value not match")
    })

    Client.bitbucketcloud(http).pullRequests("a", "b").execute()
    verify(http, times(1)).newCall(any(Request))
  }

  private static Call.Factory interceptorInvokingClient(Interceptor i, Consumer<Request> assertions) {
    Call.Factory client = mock(Call.Factory)
    Consumer<ResponseStub> emptyResponder = { ResponseStub rs -> rs.body = '{ "page": 1, "values": [] }' }

    when(client.newCall(any(Request))).thenAnswer({ invoke ->
      Response res = i.intercept(
        new MockedInterceptorChain(invoke.getArgument(0) as Request, emptyResponder, i)
      )
      Request req = res.request()
      assertions.accept(req)
      return new MockedCall(req, { _ -> res })
    })

    return client
  }
}