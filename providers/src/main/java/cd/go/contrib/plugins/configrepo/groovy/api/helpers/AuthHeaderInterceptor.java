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

package cd.go.contrib.plugins.configrepo.groovy.api.helpers;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Base64;

import static java.lang.String.format;

public class AuthHeaderInterceptor implements Interceptor {

    private static final String AUTHORIZATION = "Authorization";

    private static final String BASIC_AUTH_TYPE = "Basic";

    @NotBlank
    private final String scheme;

    @NotNull
    private final String value;

    public static AuthHeaderInterceptor basic(@NotBlank String user, @NotNull String pass) {
        return new AuthHeaderInterceptor(BASIC_AUTH_TYPE, base64(format("%s:%s", user, pass)));
    }

    private static String base64(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    public AuthHeaderInterceptor(@NotBlank String scheme, @NotNull String value) {
        this.scheme = scheme;
        this.value = value;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request().
                newBuilder().addHeader(AUTHORIZATION, headerValue()).build();
        return chain.proceed(request);
    }

    protected String headerValue() {
        return String.join(" ", scheme, value).trim();
    }
}
