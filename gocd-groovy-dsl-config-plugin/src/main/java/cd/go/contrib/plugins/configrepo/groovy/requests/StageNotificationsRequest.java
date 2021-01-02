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

package cd.go.contrib.plugins.configrepo.groovy.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StageNotificationsRequest {

    private static final ObjectMapper JSON = new ObjectMapper();

    private List<BuildCause> buildCauses;

    private String stageCounter;

    private String stageName;

    private String pipelineCounter;

    private String pipelineName;

    private String state;

    private String result;

    public StageNotificationsRequest() {
    }

    public static StageNotificationsRequest fromJSON(String json) throws IOException {
        return JSON.readValue(json, StageNotificationsRequest.class);
    }

    private static String stringAt(Map<String, Object> thing, String key, String error) {
        return retrieve(thing, key, error);
    }

    private static Map<String, Object> objAt(Map<String, Object> thing, String key, String error) {
        return retrieve(thing, key, error);
    }

    private static <T> List<T> listAt(Map<String, Object> thing, String key, String error) {
        return retrieve(thing, key, error);
    }

    @SuppressWarnings("unchecked")
    private static <T> T retrieve(Map<String, Object> thing, String key, String error) {
        return (T) requireNonNull(thing.get(key), "Missing stage-status request " + error);
    }

    public String pipelineName() {
        return pipelineName;
    }

    public String pipelineCounter() {
        return pipelineCounter;
    }

    public String stageName() {
        return stageName;
    }

    public String stageCounter() {
        return stageCounter;
    }

    public String state() {
        return state;
    }

    public String result() {
        return result;
    }

    public List<BuildCause> buildCausesOfType(final String type) {
        return buildCauses.stream().
                filter(cause -> type.equals(cause.type())).
                collect(Collectors.toList());
    }

    public String stageUrl(String baseUrl) throws URISyntaxException {
        return new URI(baseUrl + "/" + format("pipelines/%s/%s/%s/%s", pipelineName(), pipelineCounter(), stageName(), stageCounter())).
                normalize().toString();
    }

    @JsonProperty("pipeline")
    @SuppressWarnings("unused")
    private void unpackPipeline(Map<String, Object> pipeline) {
        pipelineName = stringAt(pipeline, "name", "pipeline name");
        pipelineCounter = stringAt(pipeline, "counter", "pipeline counter");
        final Map<String, Object> stage = objAt(pipeline, "stage", "stage");
        stageName = stringAt(stage, "name", "stage name");
        stageCounter = stringAt(stage, "counter", "stage counter");
        state = stringAt(stage, "state", "stage state");
        result = stringAt(stage, "result", "stage result");
        final List<Map<String, Object>> causes = listAt(pipeline, "build-cause", "build causes");
        buildCauses = causes.stream().map(BuildCause::new).collect(Collectors.toList());
    }

    public static class BuildCause {

        private final String type;

        private final Map<String, Object> config;

        private final String revision;

        public BuildCause(Map<String, Object> json) {
            final Map<String, Object> material = objAt(json, "material", "build cause material");
            final List<Map<String, Object>> mods = listAt(json, "modifications", "build cause modifications");

            type = stringAt(material, "type", "build cause material type");
            config = objAt(material, type + "-configuration", "build cause material " + type + "-configuration");
            revision = requireNonNull(mods.stream().
                    map(m -> stringAt(m, "revision", "build cause material revision")).
                    filter(StringUtils::isNotBlank).
                    findFirst().
                    orElse(null), "Failed to find stage-status build cause revision");
        }

        public String type() {
            return type;
        }

        public String revision() {
            return revision;
        }

        public String key() {
            if (!"git".equals(type)) {
                throw new RuntimeException("build-cause key() currently only supports `git` materials");
            }
            return String.join("|", type,
                    stringAt(config, "url", "build cause material url"),
                    stringAt(config, "branch", "build cause material url")
            );
        }
    }
}
