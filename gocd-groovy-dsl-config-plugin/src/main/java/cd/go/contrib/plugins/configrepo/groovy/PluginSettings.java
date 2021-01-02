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

package cd.go.contrib.plugins.configrepo.groovy;

import cd.go.contrib.plugins.configrepo.groovy.executors.GetPluginConfigurationExecutor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

public class PluginSettings {

    private static final ObjectReader READER = new ObjectMapper().readerFor(PluginSettings.class);

    @JsonProperty("include_file_pattern")
    private String includeFilePattern;

    @JsonProperty("exclude_file_pattern")
    private String excludeFilePattern;

    @SuppressWarnings("unused")
    @JsonProperty("server_base_url")
    private String serverBaseUrl;


    public static PluginSettings fromJSON(String json) {
        if (json == null) {
            return null;
        }
        try {
            return READER.readValue(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String includeFilePattern() {
        if (StringUtils.isBlank(includeFilePattern)) {
            includeFilePattern = GetPluginConfigurationExecutor.INCLUDE_FILE_PATTERN.defaultValue;
        }
        return includeFilePattern;
    }

    public String excludeFilePattern() {
        if (StringUtils.isBlank(excludeFilePattern)) {
            excludeFilePattern = GetPluginConfigurationExecutor.EXCLUDE_FILE_PATTERN.defaultValue;
        }
        return excludeFilePattern;
    }

    public String serverBaseUrl() {
        if (StringUtils.isBlank(serverBaseUrl)) {
            throw new RuntimeException("You must configure the GoCD Server Base URL; it must not be null or blank");
        }
        return serverBaseUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginSettings that = (PluginSettings) o;

        return Objects.equals(includeFilePattern, that.includeFilePattern);
    }

    @Override
    public int hashCode() {
        return includeFilePattern != null ? includeFilePattern.hashCode() : 0;
    }
}
