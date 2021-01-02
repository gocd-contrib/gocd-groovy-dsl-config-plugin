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

package cd.go.contrib.plugins.configrepo.groovy.dsl.strategies;

import cd.go.contrib.plugins.configrepo.groovy.dsl.BranchContext;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.ConnectionConfig;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.Type;
import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.ThrowingRunnable;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class BranchStrategy {

    private static final ThreadLocal<RefResolver> resolver = ThreadLocal.withInitial(() -> (s, f) -> Collections.emptyList());

    public static void with(RefResolver fn, ThrowingRunnable body) throws Throwable {
        resolver.set(fn);
        try {
            body.run();
        } finally {
            resolver.remove();
        }
    }

    private final Attributes<? extends ConnectionConfig> attrs;

    public BranchStrategy(Attributes<? extends ConnectionConfig> attrs) {
        this.attrs = attrs;
    }

    @JsonProperty("type")
    public Type type() {
        return attrs.type();
    }

    @JsonProperty("attributes")
    public Attributes<? extends ConnectionConfig> attrs() {
        return attrs;
    }

    public List<BranchContext> fetch(Pattern filter) {
        return resolver.get().apply(this, filter);
    }
}
