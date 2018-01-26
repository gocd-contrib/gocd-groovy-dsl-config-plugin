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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import cd.go.contrib.plugins.configrepo.groovy.executors.GetPluginConfigurationExecutor;
import org.apache.commons.lang3.StringUtils;

public class PluginSettings {
    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Expose
    @SerializedName("include_file_pattern")
    private String includeFilePattern;

    @Expose
    @SerializedName("exclude_file_pattern")
    private String excludeFilePattern;


    public static PluginSettings fromJSON(String json) {
        return GSON.fromJson(json, PluginSettings.class);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginSettings that = (PluginSettings) o;

        return includeFilePattern != null ? includeFilePattern.equals(that.includeFilePattern) : that.includeFilePattern == null;
    }

    @Override
    public int hashCode() {
        return includeFilePattern != null ? includeFilePattern.hashCode() : 0;
    }
}
