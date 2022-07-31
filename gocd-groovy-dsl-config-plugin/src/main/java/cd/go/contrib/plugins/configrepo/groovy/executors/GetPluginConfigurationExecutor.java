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

package cd.go.contrib.plugins.configrepo.groovy.executors;

import cd.go.contrib.plugins.configrepo.groovy.Field;
import cd.go.contrib.plugins.configrepo.groovy.RequestExecutor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.format;


public class GetPluginConfigurationExecutor implements RequestExecutor {

    public static final Field INCLUDE_FILE_PATTERN = new Field("include_file_pattern", "Include File Pattern", "**/*.gocd.groovy,**/GoCDFile", false, false, "1");

    public static final Field EXCLUDE_FILE_PATTERN = new Field("exclude_file_pattern", "Exclude File Pattern", "", false, false, "2");

    public static final Field SERVER_BASE_URL = new Field("server_base_url", "GoCD Server Base URL", "", true, false, "3") {
        @Override
        protected String doValidate(String input) {
            if (StringUtils.isBlank(input)) {
                return "The Server Base URL must be set";
            }

            try {
                final URI uri = new URI(input).normalize();

                if (StringUtils.isBlank(uri.getScheme())) {
                    return "The Server Base URL must include a scheme";
                }

                if (!StringUtils.isAllBlank(uri.getRawQuery(), uri.getRawFragment())) {
                    return "The Server Base URL must not contain a query or fragment";
                }

                if (!uri.getPath().endsWith("/go") || !uri.toString().endsWith("/go") /* this last case covers the edge case `http://host:port/go?#` */) {
                    return "The Server Base URL must end with `/go`";
                }
            } catch (URISyntaxException e) {
                return format("Invalid URL; failed to parse [%s]", input);
            }
            return null;
        }
    };

    public static final Map<String, Field> FIELDS = new LinkedHashMap<>();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        add(
                INCLUDE_FILE_PATTERN,
                EXCLUDE_FILE_PATTERN,
                SERVER_BASE_URL
        );
    }

    private static void add(Field... fields) {
        for (Field f : fields) {
            FIELDS.put(f.key(), f);
        }
    }

    public GoPluginApiResponse execute() throws JsonProcessingException {
        return new DefaultGoPluginApiResponse(200, OBJECT_MAPPER.writer().writeValueAsString(FIELDS));
    }
}
