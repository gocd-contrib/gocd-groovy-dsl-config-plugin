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

package cd.go.contrib.plugins.configrepo.groovy.dsl.json;

import cd.go.contrib.plugins.configrepo.groovy.dsl.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.json.GoCDJsonSerializer.mapper;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;

public class TaskDeserializer extends StdDeserializer<Task> {

    public TaskDeserializer() {
        super(Task.class);
    }

    @Override
    public Task deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
        TreeNode node = jsonParser.getCodec().readTree(jsonParser);
        if (node instanceof ObjectNode) {
            ObjectNode objectNode = (ObjectNode) node;
            if (objectNode.has("type")) {
                String type = objectNode.get("type").asText();
                String runIf = objectNode.get("run_if").asText();
                switch (type) {
                    case "exec":
                        ExecTask execTask = new ExecTask();
                        execTask.setRunIf(runIf);
                        if (objectNode.has("working_directory")) {
                            execTask.setWorkingDir(objectNode.get("working_directory").asText(""));
                        }
                        String command = objectNode.findValue("command").asText();
                        execTask.getCommandLine().add(0, command);

                        ArrayNode arguments = (ArrayNode) objectNode.get("arguments");
                        if (arguments != null) {
                            List<String> args = StreamSupport
                                    .stream(spliteratorUnknownSize(arguments.elements(), Spliterator.ORDERED), false)
                                    .map(JsonNode::asText)
                                    .collect(toList());
                            execTask.getCommandLine().addAll(args);
                        }
                        return execTask;
                    case "fetch":
                        String artifactOrigin = objectNode.get("artifact_origin").asText();
                        String pipeline = "";
                        if (objectNode.has("pipeline")) {
                            pipeline = objectNode.get("pipeline").asText();
                        }
                        String stage = objectNode.get("stage").asText();
                        String job = objectNode.get("job").asText();
                        switch (artifactOrigin) {
                            case "gocd":
                                FetchArtifactTask fetchArtifactTask = new FetchArtifactTask();
                                fetchArtifactTask.setPipeline(pipeline);
                                fetchArtifactTask.setStage(stage);
                                fetchArtifactTask.setJob(job);
                                fetchArtifactTask.setRunIf(runIf);

                                JsonNode isSourceAFile = objectNode.get("is_source_a_file");
                                fetchArtifactTask.setFile(isSourceAFile != null && isSourceAFile.asBoolean());
                                fetchArtifactTask.setSource(objectNode.get("source").asText());
                                if (objectNode.has("destination")) {
                                    fetchArtifactTask.setDestination(objectNode.get("destination").asText());
                                }
                                return fetchArtifactTask;
                            case "external":
                                FetchExternalArtifactTask fetchExternalArtifactTask = new FetchExternalArtifactTask();
                                fetchExternalArtifactTask.setPipeline(pipeline);
                                fetchExternalArtifactTask.setStage(stage);
                                fetchExternalArtifactTask.setJob(job);
                                fetchExternalArtifactTask.setRunIf(runIf);

                                fetchExternalArtifactTask.setArtifactId(objectNode.get("artifact_id").asText());
                                fetchExternalArtifactTask.configurationValues(getConfiguration((ArrayNode) objectNode.get("configuration")));
                                return fetchExternalArtifactTask;
                            default:
                                throw new UnsupportedOperationException("Unknow artifact origin: " + artifactOrigin);
                        }
                    case "plugin":
                        PluginTask pluginTask = new PluginTask();
                        pluginTask.setRunIf(runIf);
                        pluginTask.configurations(getConfiguration((ArrayNode) objectNode.get("configuration")));
                        JsonNode pluginConfig = objectNode.get("plugin_configuration");
                        Configuration configuration = new Configuration();
                        configuration.setId(pluginConfig.get("id").asText());
                        configuration.setVersion(pluginConfig.get("version").asInt());
                        pluginTask.configurationValue(configuration);
                        return pluginTask;
                    default:
                        throw new UnsupportedOperationException("Unknown task 'type': " + type);
                }
            }
        }
        return null;
    }

    private List<Map<String, String>> getConfiguration(ArrayNode config) {
        return StreamSupport
                .stream(spliteratorUnknownSize(config.elements(), Spliterator.ORDERED), false)
                .map(jsonNode -> (Map<String, String>) mapper().convertValue(jsonNode, Map.class))
                .collect(toList());
    }
}
