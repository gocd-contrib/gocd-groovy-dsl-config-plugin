/*
 * Copyright 2018 ThoughtWorks, Inc.
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

package cd.go.contrib.plugins.configrepo.groovy.dsl.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Map;

public abstract class KeyValuePairSerializer {

    public static JsonObject serializeVariablesInto(JsonObject jsonObject, Map<String, String> environmentVariables, Map<String, String> secureVariables) {
        return serialize(jsonObject, environmentVariables, secureVariables, "name", "value", "encrypted_value", "environment_variables");
    }

    public static JsonObject serializePluginArtifactConfigurationInto(JsonObject jsonObject, Map<String, String> configuration, Map<String, String> secureConfiguration) {
        return serialize(jsonObject, configuration, secureConfiguration, "key", "value", "encrypted_value", "configuration");
    }

    private static JsonObject serialize(JsonObject jsonObject, Map<String, String> values, Map<String, String> secureValues, String nameKey, String valueKey, String encryptedValueKey, String collectionKey) {
        JsonArray vars = new JsonArray();

        addPlainVariables(values, vars, nameKey, valueKey);
        addSecureVariables(secureValues, vars, nameKey, encryptedValueKey);

        if (vars.size() > 0) {
            jsonObject.add(collectionKey, vars);
        }

        return jsonObject;
    }

    private static void addSecureVariables(Map<String, String> secureVariables, JsonArray vars, String nameKey, String encryptedValueKey) {
        if (secureVariables == null) {
            return;
        }
        secureVariables.forEach((k, v) -> {
            JsonObject variable = new JsonObject();
            variable.addProperty(nameKey, k);
            variable.addProperty(encryptedValueKey, v);
            vars.add(variable);
        });
    }

    private static void addPlainVariables(Map<String, String> environmentVariables, JsonArray vars, String nameKey, String valueKey) {
        if (environmentVariables == null) {
            return;
        }
        for (Map.Entry<String, String> entry : environmentVariables.entrySet()) {
            JsonObject variable = new JsonObject();
            variable.addProperty(nameKey, entry.getKey());
            variable.addProperty(valueKey, String.valueOf((Object) entry.getValue()));
            vars.add(variable);
        }
    }
}
