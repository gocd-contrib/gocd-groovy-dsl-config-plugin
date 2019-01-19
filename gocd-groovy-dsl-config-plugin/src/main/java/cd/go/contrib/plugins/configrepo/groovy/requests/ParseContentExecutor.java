/*
 * Copyright 2019 ThoughtWorks, Inc.
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

import cd.go.contrib.plugins.configrepo.groovy.GroovyDslPlugin;
import cd.go.contrib.plugins.configrepo.groovy.JsonConfigCollection;
import cd.go.contrib.plugins.configrepo.groovy.RequestExecutor;
import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD;
import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipeline;
import cd.go.contrib.plugins.configrepo.groovy.dsl.json.GoCDJsonSerializer;
import cd.go.contrib.plugins.configrepo.groovy.sandbox.GroovyScriptRunner;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Map;

public class ParseContentExecutor implements RequestExecutor {

    private final Map<String, String> contents;

    public ParseContentExecutor(Map<String, String> contents) {
        this.contents = contents;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        GroovyScriptRunner engine = new GroovyScriptRunner(null, Pipeline.class.getPackage().getName());
        JsonConfigCollection result = new JsonConfigCollection();

        for (Map.Entry<String, String> entry : contents.entrySet()) {
            String filename = entry.getKey();
            String content = entry.getValue();

            Object maybeConfig = engine.runScriptWithText(content);

            if (maybeConfig instanceof GoCD) {
                GoCD configFromFile = (GoCD) maybeConfig;
                result.addConfig(filename, configFromFile);
                GroovyDslPlugin.LOG.debug("Found pipeline configs at {}", filename);
            } else {
                String type = null;
                if (maybeConfig != null) {
                    type = maybeConfig.getClass().getName();
                }
                result.addError("The object returned by the script is of unexpected type " + type, filename);
                GroovyDslPlugin.LOG.warn("Skipping file {} the object returned by the script is of type {}", filename, type);
            }
        }
        result.updateTargetVersionFromFiles();
        return DefaultGoPluginApiResponse.success(GoCDJsonSerializer.toJsonString(result.getJsonObject()));
    }

}
