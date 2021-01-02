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

package cd.go.contrib.plugins.configrepo.groovy;

import cd.go.contrib.plugins.configrepo.groovy.executors.StageStatusExecutor;
import cd.go.contrib.plugins.configrepo.groovy.requests.StageNotificationsRequest;
import cd.go.contrib.plugins.configrepo.groovy.resolvers.Notifications;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

@Extension
public class BuildStatusNotificationPlugin implements GoPlugin {

    public static final Logger LOG = Logger.getLoggerFor(BuildStatusNotificationPlugin.class);

    public static final String NOTIFICATIONS_INTERESTED_IN = "notifications-interested-in";

    public static final String STAGE_STATUS = "stage-status";

    private static final ObjectMapper JSON = new ObjectMapper();

    private PluginRequest pluginRequest;

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor accessor) {
        pluginRequest = new PluginRequest(accessor);
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) {
        try {
            switch (request.requestName()) {
                case NOTIFICATIONS_INTERESTED_IN:
                    return DefaultGoPluginApiResponse.success(JSON.writeValueAsString(
                            singletonMap("notifications", singletonList(STAGE_STATUS))
                    ));
                case STAGE_STATUS:
                    final StageNotificationsRequest stageRequest = StageNotificationsRequest.fromJSON(request.requestBody());
                    return new StageStatusExecutor(pluginRequest, stageRequest, Notifications::realEmit).execute();
                default:
                    throw new UnhandledRequestTypeException(request.requestName());
            }
        } catch (Throwable e) {
            return DefaultGoPluginApiResponse.error("Failed to handle request " + request.requestName() + " due to:" + e.getMessage());
        }
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return Constants.NOTIFICATION_PLUGIN_IDENTIFIER;
    }
}
