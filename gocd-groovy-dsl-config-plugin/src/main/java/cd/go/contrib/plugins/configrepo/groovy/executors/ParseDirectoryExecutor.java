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

package cd.go.contrib.plugins.configrepo.groovy.executors;

import cd.go.contrib.plugins.configrepo.groovy.GroovyDslPlugin;
import cd.go.contrib.plugins.configrepo.groovy.JsonConfigCollection;
import cd.go.contrib.plugins.configrepo.groovy.PluginRequest;
import cd.go.contrib.plugins.configrepo.groovy.ServerRequestFailedException;
import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD;
import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipeline;
import cd.go.contrib.plugins.configrepo.groovy.dsl.json.GoCDJsonSerializer;
import cd.go.contrib.plugins.configrepo.groovy.sandbox.GroovyScriptRunner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.PatternSet;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ParseDirectoryExecutor {

    private final PluginRequest pluginRequest;

    private final String directory;

    private GroovyScriptRunner engine;

    public ParseDirectoryExecutor(PluginRequest pluginRequest, GoPluginApiRequest request) throws IOException {
        this.pluginRequest = pluginRequest;

        Map<String, java.lang.Object> map = GoCDJsonSerializer.fromJson(request.requestBody(), Map.class);

        this.directory = (String) map.get("directory");
    }

    public GoPluginApiResponse execute() throws JsonProcessingException {
        try {
            return doParseFiles();
        } catch (ServerRequestFailedException | IOException e) {
            GroovyDslPlugin.LOG.error("Unexpected error occurred in Groovy DSL configuration plugin.", e);
            JsonConfigCollection config = new JsonConfigCollection();
            config.addError(e.toString(), "Groovy DSL config plugin");
            return DefaultGoPluginApiResponse.error(GoCDJsonSerializer.toJsonString(config.getJsonObject()));
        }
    }

    private GoPluginApiResponse doParseFiles() throws ServerRequestFailedException, IOException {
        String[] files = getFilesMatchingPattern();

        GroovyScriptRunner engine = getEngine();
        JsonConfigCollection result = new JsonConfigCollection();
        for (String file : files) {
            try {
                Object maybeConfig = engine.runScript(file);

                if (maybeConfig instanceof GoCD) {
                    GoCD configFromFile = (GoCD) maybeConfig;
                    result.addConfig(file, configFromFile);
                    GroovyDslPlugin.LOG.debug("Found pipeline configs at " + new File(directory, file));
                } else {
                    String type = null;
                    if (maybeConfig != null) {
                        type = maybeConfig.getClass().getName();
                    }
                    result.addError("The object returned by the script is of unexpected type " + type, file);
                    GroovyDslPlugin.LOG.warn("Skipping file " + new File(directory, file) + ", the object returned by the script is of type " + type);
                }
            } catch (Exception e) {
                result.addError("Unable to parse file " + file + ". " + e.getMessage(), file);
                GroovyDslPlugin.LOG.warn("Skipping file " + file + " in directory " + directory, e);
            }
        }
        result.updateTargetVersionFromFiles();
        return DefaultGoPluginApiResponse.success(GoCDJsonSerializer.toJsonString(result.getJsonObject()));
    }

    private GroovyScriptRunner getEngine() throws IOException {
        if (engine == null) {
            engine = new GroovyScriptRunner(directory, Pipeline.class.getPackage().getName());
        }
        return engine;
    }

    private String[] getFilesMatchingPattern() throws ServerRequestFailedException, IOException {

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(directory);

        PatternSet patternSet = new PatternSet();
        Project project = new Project();
        project.setBaseDir(new File(directory));

        patternSet.setIncludes(pluginRequest.getPluginSettings().includeFilePattern());
        scanner.setIncludes(patternSet.getIncludePatterns(project));

        if (StringUtils.isNotBlank(pluginRequest.getPluginSettings().excludeFilePattern())) {
            patternSet.setExcludes(pluginRequest.getPluginSettings().excludeFilePattern());
            scanner.setExcludes(patternSet.getExcludePatterns(project));
        }

        scanner.scan();
        return scanner.getIncludedFiles();
    }

}
