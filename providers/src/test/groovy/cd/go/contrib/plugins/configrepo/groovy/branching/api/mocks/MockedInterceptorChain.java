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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MockedInterceptorChain implements Interceptor.Chain {

    private final Request req;

    private final Consumer<ResponseStub> responder;

    private final List<Interceptor> interceptors;

    public MockedInterceptorChain(Request req, Consumer<ResponseStub> rs, Interceptor... interceptors) {
        this(req, rs, Arrays.asList(interceptors));
    }

    public MockedInterceptorChain(Request req, Consumer<ResponseStub> rs, List<Interceptor> interceptors) {
        this.req = req;
        this.responder = rs;
        this.interceptors = interceptors;
    }

    @Override
    public Request request() {
        return req;
    }

    @Override
    public Response proceed(Request request) throws IOException {
        if (interceptors.isEmpty()) {
            final ResponseStub rs = new ResponseStub();
            this.responder.accept(rs);
            return rs.response(request);
        } else {
            return interceptors.get(0).intercept(
                    new MockedInterceptorChain(
                            request, this.responder, this.interceptors.subList(1, this.interceptors.size())
                    )
            );
        }
    }

    @Override
    public Connection connection() {
        return null;
    }

    @Override
    public Call call() {
        return null;
    }

    @Override
    public int connectTimeoutMillis() {
        return 0;
    }

    @Override
    public Interceptor.Chain withConnectTimeout(int timeout, TimeUnit unit) {
        return null;
    }

    @Override
    public int readTimeoutMillis() {
        return 0;
    }

    @Override
    public Interceptor.Chain withReadTimeout(int timeout, TimeUnit unit) {
        return null;
    }

    @Override
    public int writeTimeoutMillis() {
        return 0;
    }

    @Override
    public Interceptor.Chain withWriteTimeout(int timeout, TimeUnit unit) {
        return null;
    }
}