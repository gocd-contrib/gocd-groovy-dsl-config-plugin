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

package cd.go.contrib.plugins.configrepo.groovy.dsl.json;

import cd.go.contrib.plugins.configrepo.groovy.dsl.Filter;
import cd.go.contrib.plugins.configrepo.groovy.dsl.GitHubPRMaterial;
import cd.go.contrib.plugins.configrepo.groovy.dsl.ShellTask;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

public abstract class GoCDJsonSerializer {

    public static String toJsonString(Object node) throws JsonProcessingException {
        return mapper().writerWithDefaultPrettyPrinter().writeValueAsString(toJson(node));
    }

    public static <T extends JsonNode> T toJson(Object node) {
        return mapper().valueToTree(node);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) throws IOException {
        return mapper().readerFor(classOfT).readValue(json);
    }

    public static ObjectMapper mapper() {
        return mapper(null);
    }

    protected static ObjectMapper mapper(JsonFactory jf) {
        return new ObjectMapper(jf)
                .registerModule(module())
                .disable(MapperFeature.AUTO_DETECT_CREATORS)
                .disable(MapperFeature.AUTO_DETECT_SETTERS)
                .disable(MapperFeature.AUTO_DETECT_FIELDS)
                .disable(MapperFeature.AUTO_DETECT_GETTERS)
                .disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                ;
    }

    public static SimpleModule module() {
        return new SimpleModule()
                .addSerializer(ShellTask.class, new ShellTaskSerializer())
                .addSerializer(GitHubPRMaterial.class, new GithubPRMaterialSerializer())
                .addSerializer(Filter.class, new FilterSerializer())
                .addDeserializer(Filter.class, new FilterDeserializer())
                ;
    }

}
