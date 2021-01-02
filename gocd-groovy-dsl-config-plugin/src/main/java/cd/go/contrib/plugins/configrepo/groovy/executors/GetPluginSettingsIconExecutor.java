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

package cd.go.contrib.plugins.configrepo.groovy.executors;

import cd.go.contrib.plugins.configrepo.groovy.RequestExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Base64;

import static cd.go.contrib.plugins.configrepo.groovy.utils.Util.readResourceBytes;

public class GetPluginSettingsIconExecutor implements RequestExecutor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public GoPluginApiResponse execute() throws Exception {
        ImmutableMap<String, String> jsonObject = ImmutableMap.<String, String>builder()
                .put("content_type", "image/svg+xml")
                .put("data", Base64.getEncoder().encodeToString(readResourceBytes("/groovy.svg")))
                .build();

        return new DefaultGoPluginApiResponse(200, OBJECT_MAPPER.writeValueAsString(jsonObject));
    }
}
