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

package cd.go.contrib.plugins.configrepo.groovy;

import cd.go.contrib.plugins.configrepo.groovy.utils.Util;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;

import java.util.Collections;

public interface Constants {

    String PLUGIN_ID = Util.pluginId();

    // The type of this extension
    String CONFIGREPO_EXTENSION_TYPE = "configrepo";
    String NOTIFICATION_EXTENSION_TYPE = "notification";

    // The extension point API version that this plugin understands
    String CONFIGREPO_API_VERSION = "3.0";
    String NOTIFICATION_API_VERSION = "4.0";

    // the identifier of this plugin
    GoPluginIdentifier CONFIGREPO_PLUGIN_IDENTIFIER = new GoPluginIdentifier(CONFIGREPO_EXTENSION_TYPE, Collections.singletonList(CONFIGREPO_API_VERSION));
    GoPluginIdentifier NOTIFICATION_PLUGIN_IDENTIFIER = new GoPluginIdentifier(NOTIFICATION_EXTENSION_TYPE, Collections.singletonList(NOTIFICATION_API_VERSION));

    // requests that the plugin makes to the server
    String REQUEST_SERVER_PREFIX = "go.processor";
    String REQUEST_SERVER_GET_PLUGIN_SETTINGS = REQUEST_SERVER_PREFIX + ".plugin-settings.get";
}
