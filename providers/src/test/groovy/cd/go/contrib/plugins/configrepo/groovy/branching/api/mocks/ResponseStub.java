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

package cd.go.contrib.plugins.configrepo.groovy.branching.api.mocks;

import okhttp3.*;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;

public class ResponseStub {

    public int code = 200;

    public String body;

    public final Headers.Builder headers = new Headers.Builder();

    public ResponseStub() {
    }

    public Response response(Request req) {
        return new Response.Builder().request(req).protocol(Protocol.HTTP_1_1).message("").code(code).body(responseBody()).headers(headers.build()).build();
    }

    private ResponseBody responseBody() {
        Buffer buffer = new Buffer();
        buffer.write(body.getBytes());
        return new RealResponseBody("application/json", body.length(), buffer);
    }
}
