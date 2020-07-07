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

package cd.go.contrib.plugins.configrepo.groovy.branching.api.exceptions;

import retrofit2.Response;

import java.io.IOException;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public abstract class ResponseFailure extends RuntimeException {

    public ResponseFailure(Response<?> response, String detail) {
        super(message(response, detail));
    }

    public static void throwOnFailure(Response<?> response) {
        throwOnFailure(response, null);
    }

    public static void throwOnFailure(Response<?> response, String detail) {
        if (isFailed(response)) {
            throw from(response, detail);
        }
    }

    public static ResponseFailure from(Response<?> response, String detail) {
        if (response.isSuccessful() || response.raw().isRedirect()) {
            throw new IllegalArgumentException(format("Refusing to create a ResponseFailure from a successful response, status: %d", response.code()));
        }

        final int code = response.code();
        if (code >= 500) {
            return new ServerError(response, detail);
        }

        return new ClientError(response, detail);
    }

    public static boolean isFailed(Response<?> response) {
        return !response.isSuccessful() && !response.raw().isRedirect();
    }

    private static String message(Response<?> response, String detail) {
        String baseMsg;
        try {
            baseMsg = format("Failed [%s] with %d. Body: %s", response.raw().request().url(), response.code(), requireNonNull(response.errorBody()).string());
        } catch (IOException | NullPointerException e) {
            baseMsg = format("Failed [%s] with %d", response.raw().request().url(), response.code());
        }
        return isNotBlank(detail) ? format("%s; %s", detail, baseMsg) : baseMsg;
    }
}
