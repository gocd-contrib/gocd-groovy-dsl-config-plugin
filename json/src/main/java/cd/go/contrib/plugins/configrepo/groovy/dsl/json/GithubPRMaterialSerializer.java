/*
 * Copyright 2020 ThoughtWorks, Inc.
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

import cd.go.contrib.plugins.configrepo.groovy.dsl.GitHubPRMaterial;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class GithubPRMaterialSerializer extends StdSerializer<GitHubPRMaterial> {

    protected GithubPRMaterialSerializer() {
        super(GitHubPRMaterial.class);
    }

    @Override
    public void serializeWithType(GitHubPRMaterial value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        serialize(value, gen, serializers);
    }

    @Override
    public void serialize(GitHubPRMaterial value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeObject(value.toPluggableMaterial());
    }

}
