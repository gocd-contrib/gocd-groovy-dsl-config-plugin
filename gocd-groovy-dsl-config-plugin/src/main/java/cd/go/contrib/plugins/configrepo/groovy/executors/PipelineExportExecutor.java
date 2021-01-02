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

package cd.go.contrib.plugins.configrepo.groovy.executors;

import cd.go.contrib.plugins.configrepo.groovy.RequestExecutor;
import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD;
import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipeline;
import cd.go.contrib.plugins.configrepo.groovy.dsl.json.GoCDJsonSerializer;
import cd.go.contrib.plugins.configrepo.groovy.export.GroovyExporter;
import cd.go.contrib.plugins.configrepo.groovy.util.GroovyScriptRunner;
import com.google.common.collect.ImmutableMap;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

public class PipelineExportExecutor implements RequestExecutor {

    private final Pipeline pipeline;

    public PipelineExportExecutor(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        String exportedSource = exportedSource();

        StringWriter out = new StringWriter();

        Pipeline maybePipeline = getPipelineAfterParsing(exportedSource);

        PrintWriter writer = new PrintWriter(out);
        writer.println("/*");
        writer.println(" * This file was automatically exported by the GoCD Groovy DSL Plugin.");
        writer.println(" */");
        writer.println();

        if (!pipeline.equals(maybePipeline)) {
            writer.println("// WARNING: It appears that there are be some discrepancies with the pipeline export.");
            writer.println("// WARNING: Please report a bug at https://github.com/gocd-contrib/gocd-groovy-dsl-config-plugin/issues.");
            writer.println();
        }

        writer.println(exportedSource());
        writer.flush();

        String responseBody = GoCDJsonSerializer.toJsonString(ImmutableMap.of("pipeline", out.toString()));
        return new DefaultGoPluginApiResponse(200, responseBody, ImmutableMap.<String, String>builder()
                .put("Content-Type", "text/plain; charset=utf-8")
                .put("X-Export-Filename", this.pipeline.getName() + ".gocd.groovy")
                .build());
    }

    private Pipeline getPipelineAfterParsing(String exportedSource) {
        final Object maybeConfig = new GroovyScriptRunner(Pipeline.class.getPackage().getName()).runScriptWithText(exportedSource);
        if (maybeConfig instanceof GoCD) {
            return ((GoCD) maybeConfig).pipelines(null).get(0);
        }
        return null;
    }

    private String exportedSource() {
        StringWriter exportedSource = new StringWriter();

        try {
            GroovyExporter groovyExporter = new GroovyExporter(exportedSource);
            groovyExporter.fullExport(pipeline);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        return exportedSource.toString();
    }
}
