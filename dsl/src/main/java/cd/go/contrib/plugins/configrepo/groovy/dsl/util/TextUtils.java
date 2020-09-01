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

package cd.go.contrib.plugins.configrepo.groovy.dsl.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    private static final Pattern GIT_EXTRACT_SHORT_REF_NAME = Pattern.compile("^refs/([^/]+)/(.+)$");

    private static final String SANITIZE = "[^a-zA-Z0-9\\-_]";

    private TextUtils() {
    }

    public static String gitShortRef(String fullRef) {
        final Matcher matcher = GIT_EXTRACT_SHORT_REF_NAME.matcher(fullRef);

        if (!matcher.find()) { // likely already a short ref
            return fullRef;
        }

        switch (matcher.group(1)) {
            case "tags":
            case "heads":
                return matcher.group(2);
            default:
                return fullRef.substring("refs/".length());
        }
    }

    public static String sanitizeName(String ref) {
        return ref.replaceAll(SANITIZE, "_");
    }
}
