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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Field {
    protected final String key;

    @JsonProperty("display-name")
    protected final String displayName;

    @JsonProperty("default-value")
    protected final String defaultValue;

    @JsonProperty("required")
    protected final Boolean required;

    @JsonProperty("secure")
    protected final Boolean secure;

    @JsonProperty("display-order")
    protected final String displayOrder;

    public Field(String key, String displayName, String defaultValue, Boolean required, Boolean secure, String displayOrder) {
        this.key = key;
        this.displayName = displayName;
        this.defaultValue = defaultValue;
        this.required = required;
        this.secure = secure;
        this.displayOrder = displayOrder;
    }

    public Map<String, String> validate(String input) {
        HashMap<String, String> result = new HashMap<>();
        String validationError = doValidate(input);
        if (StringUtils.isNotBlank(validationError)) {
            result.put("key", key);
            result.put("message", validationError);
        }
        return result;
    }

    protected String doValidate(String input) {
        return null;
    }

    public String key() {
        return key;
    }

    public String displayName() {
        return displayName;
    }
}
