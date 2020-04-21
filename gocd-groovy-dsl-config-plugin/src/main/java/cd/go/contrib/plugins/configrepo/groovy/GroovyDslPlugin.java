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

import cd.go.contrib.plugins.configrepo.groovy.executors.*;
import cd.go.contrib.plugins.configrepo.groovy.requests.ParseContentRequest;
import cd.go.contrib.plugins.configrepo.groovy.requests.PipelineExportRequest;
import cd.go.contrib.plugins.configrepo.groovy.requests.ValidatePluginSettingsRequest;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

@Extension
public class GroovyDslPlugin implements GoPlugin {

    public static final Logger LOG = Logger.getLoggerFor(GroovyDslPlugin.class);

    private PluginRequest pluginRequest;

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor accessor) {
        pluginRequest = new PluginRequest(accessor);
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        try {
            switch (Request.fromString(request.requestName())) {
                case PARSE_DIRECTORY:
                    return new ParseDirectoryExecutor(pluginRequest, request).execute();
                case PLUGIN_SETTINGS_GET_CONFIGURATION:
                    return new GetPluginConfigurationExecutor().execute();
                case PLUGIN_SETTINGS_VALIDATE_CONFIGURATION:
                    return ValidatePluginSettingsRequest.fromJSON(request.requestBody()).executor().execute();
                case GET_CAPABILITIES:
                    return new CapabilitiesExcutor().execute();
                case PIPELINE_EXPORT:
                    return PipelineExportRequest.fromJSON(request.requestBody()).executor().execute();
                case PARSE_CONTENT:
                    return ParseContentRequest.fromJSON(request.requestBody()).executor().execute();
                case PLUGIN_SETTINGS_GET_VIEW:
                    return new GetViewRequestExecutor().execute();
                case GET_ICON:
                    return new GetPluginSettingsIconExecutor().execute();
                default:
                    throw new UnhandledRequestTypeException(request.requestName());
            }
        } catch (Exception e) {
            LOG.error("Failed to handle request " + request.requestName() + " due to:", e);
            return DefaultGoPluginApiResponse.error("Failed to handle request " + request.requestName() + " due to:" + e.getMessage());
        }
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return Constants.PLUGIN_IDENTIFIER;
    }
}
