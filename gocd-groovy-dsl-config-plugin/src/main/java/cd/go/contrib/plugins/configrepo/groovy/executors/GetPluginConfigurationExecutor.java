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

import cd.go.contrib.plugins.configrepo.groovy.Field;
import cd.go.contrib.plugins.configrepo.groovy.RequestExecutor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.LinkedHashMap;
import java.util.Map;


public class GetPluginConfigurationExecutor implements RequestExecutor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final Field INCLUDE_FILE_PATTERN = new Field("include_file_pattern", "Include File Pattern", "**/*.gocd.groovy,**/GoCDFile", false, false, "1");

    public static final Field EXCLUDE_FILE_PATTERN = new Field("exclude_file_pattern", "Exclude File Pattern", "", false, false, "2");

    public static final Map<String, Field> FIELDS = new LinkedHashMap<>();

    static {
        FIELDS.put(INCLUDE_FILE_PATTERN.key(), INCLUDE_FILE_PATTERN);
        FIELDS.put(EXCLUDE_FILE_PATTERN.key(), EXCLUDE_FILE_PATTERN);
    }

    public GoPluginApiResponse execute() throws JsonProcessingException {
        return new DefaultGoPluginApiResponse(200, OBJECT_MAPPER.writer().writeValueAsString(FIELDS));
    }

}
