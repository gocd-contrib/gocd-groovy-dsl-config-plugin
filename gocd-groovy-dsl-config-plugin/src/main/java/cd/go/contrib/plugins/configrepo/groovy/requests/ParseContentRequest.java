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

package cd.go.contrib.plugins.configrepo.groovy.requests;

import cd.go.contrib.plugins.configrepo.groovy.dsl.json.GoCDJsonSerializer;
import cd.go.contrib.plugins.configrepo.groovy.executors.ParseContentExecutor;

import java.io.IOException;
import java.util.Map;

public class ParseContentRequest {

    private final Map<String, String> contents;

    public ParseContentRequest(Map<String, String> contents) {
        this.contents = contents;
    }

    public static ParseContentRequest fromJSON(String requestBody) throws IOException {
        Map<String, Map<String, String>> request = GoCDJsonSerializer.mapper().readerFor(Object.class).readValue(requestBody);
        Map<String, String> contents = request.get("contents");

        return new ParseContentRequest(contents);
    }

    public ParseContentExecutor executor() {
        return new ParseContentExecutor(contents);
    }
}
