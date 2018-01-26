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

package cd.go.contrib.plugins.configrepo.groovy;

import com.google.gson.*;

import java.util.HashSet;
import java.util.Set;

public class JsonConfigCollection {

    private static final int DEFAULT_VERSION = 2;

    private static final Gson GSON = new Gson();

    private Set<Integer> uniqueVersions = new HashSet<>();

    private JsonObject mainObject = new JsonObject();

    private JsonArray environments = new JsonArray();

    private JsonArray pipelines = new JsonArray();

    private JsonArray errors = new JsonArray();

    public JsonConfigCollection() {
        updateTargetVersionTo(DEFAULT_VERSION);
        mainObject.add("environments", environments);
        mainObject.add("pipelines", pipelines);
        mainObject.add("errors", errors);
    }

    protected JsonArray getEnvironments() {
        return environments;
    }

    public void addEnvironment(JsonElement environment, String location) {
        environments.add(environment);
        environment.getAsJsonObject().add("location", new JsonPrimitive(location));
    }

    public JsonObject getJsonObject() {
        return mainObject;
    }

    public void addPipeline(JsonElement pipeline, String location) {
        pipelines.add(pipeline);
        pipeline.getAsJsonObject().add("location", new JsonPrimitive(location));
    }

    public JsonArray getPipelines() {
        return pipelines;
    }

    public JsonArray getErrors() {
        return errors;
    }

    public void addError(String message, String location) {
        JsonObject error = new JsonObject();
        error.addProperty("message", message);
        error.addProperty("location", location);
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
        mainObject.add("target_version", new JsonPrimitive(targetVersion));
    }
}
