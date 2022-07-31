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

import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD;
import cd.go.contrib.plugins.configrepo.groovy.dsl.json.GoCDJsonSerializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.validate.Validator.validate;

public class JsonConfigCollection {

    private static final int DEFAULT_VERSION = 2;

    private final Set<Integer> uniqueVersions = new HashSet<>();

    private final ObjectNode mainObject = JsonNodeFactory.instance.objectNode();

    private final ArrayNode environments = JsonNodeFactory.instance.arrayNode();

    private final ArrayNode pipelines = JsonNodeFactory.instance.arrayNode();

    private final ArrayNode errors = JsonNodeFactory.instance.arrayNode();

    public JsonConfigCollection() {
        updateTargetVersionTo(DEFAULT_VERSION);
        mainObject.set("environments", environments);
        mainObject.set("pipelines", pipelines);
        mainObject.set("errors", errors);
    }

    protected ArrayNode getEnvironments() {
        return environments;
    }

    public void addEnvironment(ObjectNode environment, String location) {
        environments.add(environment);
        environment.put("location", location);
    }

    public ObjectNode getJsonObject() {
        return mainObject;
    }

    public void addPipeline(ObjectNode pipeline, String location) {
        pipelines.add(pipeline);
        pipeline.put("location", location);
    }

    public ArrayNode getPipelines() {
        return pipelines;
    }

    public ArrayNode getErrors() {
        return errors;
    }

    public void addError(String message, String location) {
        ObjectNode error = JsonNodeFactory.instance.objectNode();
        error.put("message", message);
        error.put("location", location);
        this.errors.add(error);
    }

    public void append(JsonConfigCollection other) {
        this.environments.addAll(other.environments);
        this.pipelines.addAll(other.pipelines);
        this.errors.addAll(other.errors);
        this.uniqueVersions.addAll(other.uniqueVersions);
    }

    public void updateFormatVersionFound(int version) {
        uniqueVersions.add(version);
        updateTargetVersionTo(version);
    }

    public void updateTargetVersionFromFiles() {
        if (uniqueVersions.size() > 1) {
            throw new RuntimeException("Versions across files are not unique. Found versions: " + uniqueVersions + ". There can only be one version across the whole repository.");
        }
        updateTargetVersionTo(uniqueVersions.iterator().hasNext() ? uniqueVersions.iterator().next() : DEFAULT_VERSION);
    }

    private void updateTargetVersionTo(int targetVersion) {
        mainObject.remove("target_version");
        mainObject.put("target_version", targetVersion);
    }

    public void addConfig(String sourceLocation, GoCD config) {
        if (config.getTargetVersion() != null) {
            updateFormatVersionFound((config).getTargetVersion());
        }

        validate(config, constraintViolations -> {
            StringBuilder buf = new StringBuilder();

            for (ConstraintViolation<GoCD> violation : constraintViolations) {
                buf.append("  - ").append(violation.getPropertyPath()).append(" ").append(violation.getMessage());
                buf.append("\n");
            }

            throw new RuntimeException(buf.toString());
        });
        config.environments(null).forEach(environment -> addEnvironment(GoCDJsonSerializer.toJson(environment), sourceLocation));
        config.pipelines(null).forEach(pipeline -> addPipeline(GoCDJsonSerializer.toJson(pipeline), sourceLocation));
    }
}
