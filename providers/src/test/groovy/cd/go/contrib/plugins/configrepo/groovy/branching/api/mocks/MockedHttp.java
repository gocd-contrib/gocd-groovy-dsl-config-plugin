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

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * An OkHttp {@link Call.Factory} for mocking out HTTP responses for tests
 */
@SuppressWarnings("NullableProblems")
public class MockedHttp implements Call.Factory {

    private final Map<RequestMatch, Supplier<ResponseStub>> reqs = new HashMap<>();

    public MockedHttp GET(String uri, Consumer<ResponseStub> config) {
        reqs.put(new RequestMatch("GET", uri), () -> {
            final ResponseStub r = new ResponseStub();
            config.accept(r);
            return r;
        });
        return this;
    }

    private static RequestMatch asMatch(Request request) {
        final String path = Optional.of(request.url().uri()).map(
                uri -> uri.getRawPath() +
                        Optional.ofNullable(uri.getQuery()).map(s -> "?" + s).orElse("") +
                        Optional.ofNullable(uri.getFragment()).map(s -> "#" + s).orElse("")
        ).get();

        return new RequestMatch(request.method(), path);
    }

    public Response respondTo(Request req) {
        final RequestMatch match = asMatch(req);

        if (reqs.containsKey(match)) {
            return reqs.get(match).get().response(req);
        }

        final List<String> knownEntries = reqs.keySet().stream().map(RequestMatch::describe).collect(Collectors.toList());
        throw new RuntimeException(format(
                "Could not match request `%s` against expected requests:\n%s",
                match.describe(),
                join(knownEntries, ",\n")
        ));
    }

    @Override
    public Call newCall(Request request) {
        return new MockedCall(request, this::respondTo);
    }

}
