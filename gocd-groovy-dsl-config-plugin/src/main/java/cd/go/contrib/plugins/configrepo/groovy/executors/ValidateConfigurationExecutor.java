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

import cd.go.contrib.plugins.configrepo.groovy.Field;
import cd.go.contrib.plugins.configrepo.groovy.RequestExecutor;
import cd.go.contrib.plugins.configrepo.groovy.requests.ValidatePluginSettingsRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.ArrayList;
import java.util.Map;

public class ValidateConfigurationExecutor implements RequestExecutor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ValidatePluginSettingsRequest request;

    public ValidateConfigurationExecutor(ValidatePluginSettingsRequest request) {
        this.request = request;
    }

    public GoPluginApiResponse execute() throws JsonProcessingException {
        ArrayList<Map<String, String>> result = new ArrayList<>();

        for (Map.Entry<String, Field> entry : GetPluginConfigurationExecutor.FIELDS.entrySet()) {
            Field field = entry.getValue();
            Map<String, String> validationError = field.validate(request.get(entry.getKey()));

            if (!validationError.isEmpty()) {
                result.add(validationError);
            }
        }

        return DefaultGoPluginApiResponse.success(OBJECT_MAPPER.writer().writeValueAsString(result));
    }
}
