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

package cd.go.contrib.plugins.configrepo.groovy.executors;

import cd.go.contrib.plugins.configrepo.groovy.RequestExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public class CapabilitiesExecutor implements RequestExecutor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public GoPluginApiResponse execute() throws Exception {
        return new DefaultGoPluginApiResponse(200, MAPPER.writer().writeValueAsString(ImmutableMap.builder()
                .put("supports_pipeline_export", true)
                .put("supports_parse_content", true)
                .put("supports_list_config_files", false)
                .put("supports_user_defined_properties", true)
                .build()));
    }
}
