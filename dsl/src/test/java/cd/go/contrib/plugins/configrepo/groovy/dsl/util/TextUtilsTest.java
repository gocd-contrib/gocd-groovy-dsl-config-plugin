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

package cd.go.contrib.plugins.configrepo.groovy.dsl.util;

import org.junit.jupiter.api.Test;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.util.TextUtils.gitShortRef;
import static cd.go.contrib.plugins.configrepo.groovy.dsl.util.TextUtils.sanitizeName;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TextUtilsTest {

    @Test
    void gitShortRefComputesShorthandFromFullRef() {
        assertEquals("foo", gitShortRef("refs/heads/foo"));
        assertEquals("v1.2.3.4-5", gitShortRef("refs/tags/v1.2.3.4-5"));
        assertEquals("a/very/deep/path", gitShortRef("refs/heads/a/very/deep/path"));
        assertEquals("pull/1234/yay", gitShortRef("refs/pull/1234/yay"));
        assertEquals("already/shortened", gitShortRef("already/shortened"));
    }

    @Test
    void sanitizeNamesMakesStringSafeForIdentifiers() {
        assertEquals("h_ppy_n0t___d", sanitizeName("h@ppy.n0t.$@d"));
        assertEquals("-but-dashes--are-ok", sanitizeName("-but-dashes--are-ok"));
    }
}