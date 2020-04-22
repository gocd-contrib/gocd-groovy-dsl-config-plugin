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

package cd.go.contrib.plugins.configrepo.groovy;

import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.request.DefaultGoApiRequest;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;

import static cd.go.contrib.plugins.configrepo.groovy.Constants.PLUGIN_IDENTIFIER;
import static cd.go.contrib.plugins.configrepo.groovy.Constants.REQUEST_SERVER_GET_PLUGIN_SETTINGS;

/**
 * Instances of this class know how to send messages to the GoCD Server.
 */
public class PluginRequest {

    private final GoApplicationAccessor accessor;

    public PluginRequest(GoApplicationAccessor accessor) {
        this.accessor = accessor;
    }

    public PluginSettings getPluginSettings() throws ServerRequestFailedException {
        DefaultGoApiRequest request = new DefaultGoApiRequest(REQUEST_SERVER_GET_PLUGIN_SETTINGS, "1.0", PLUGIN_IDENTIFIER);
        GoApiResponse response = accessor.submit(request);

        if (response.responseCode() != 200) {
            throw ServerRequestFailedException.getPluginSettings(response);
        }

        PluginSettings pluginSettings = PluginSettings.fromJSON(response.responseBody());
        if (pluginSettings == null) {
            pluginSettings = new PluginSettings();
        }
        return pluginSettings;
    }
}
