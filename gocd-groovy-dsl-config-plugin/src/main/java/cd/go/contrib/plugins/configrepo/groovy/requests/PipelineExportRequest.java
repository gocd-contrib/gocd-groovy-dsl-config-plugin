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

import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipeline;
import cd.go.contrib.plugins.configrepo.groovy.dsl.json.GoCDJsonSerializer;
import cd.go.contrib.plugins.configrepo.groovy.executors.PipelineExportExecutor;

import java.io.IOException;
import java.util.Map;

public class PipelineExportRequest {

    private final Pipeline pipeline;

    public PipelineExportRequest(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public static PipelineExportRequest fromJSON(String requestBody) throws IOException {
        Map map = GoCDJsonSerializer.fromJson(requestBody, Map.class);
        Object pipelineMap = map.get("pipeline");
        String json = GoCDJsonSerializer.toJsonString(pipelineMap);
        Pipeline pipeline = GoCDJsonSerializer.fromJson(json, Pipeline.class);
        return new PipelineExportRequest(pipeline);
    }

    public PipelineExportExecutor executor() {
        return new PipelineExportExecutor(pipeline);
    }
}
