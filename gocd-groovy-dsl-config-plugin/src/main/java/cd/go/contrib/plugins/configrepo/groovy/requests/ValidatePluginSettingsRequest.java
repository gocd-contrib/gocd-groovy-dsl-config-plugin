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

import cd.go.contrib.plugins.configrepo.groovy.RequestExecutor;
import cd.go.contrib.plugins.configrepo.groovy.executors.ValidateConfigurationExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ValidatePluginSettingsRequest extends HashMap<String, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    @SuppressWarnings("unchecked")
    public static ValidatePluginSettingsRequest fromJSON(String json) throws IOException {
        ValidatePluginSettingsRequest result = new ValidatePluginSettingsRequest();

        Map<String, Map<String, String>> settings = (Map<String, Map<String, String>>) MAPPER.readValue(json, HashMap.class).get("plugin-settings");

        for (Map.Entry<String, Map<String, String>> entry : settings.entrySet()) {
            result.put(entry.getKey(), entry.getValue().get("value"));
        }

        return result;
    }

    public RequestExecutor executor() {
        return new ValidateConfigurationExecutor(this);
    }
}
