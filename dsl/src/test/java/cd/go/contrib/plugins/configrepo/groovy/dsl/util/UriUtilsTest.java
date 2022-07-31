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

package cd.go.contrib.plugins.configrepo.groovy.dsl.util;

import org.junit.jupiter.api.Test;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.util.UriUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UriUtilsTest {

    @Test
    void maskAuthHidesPasswordInUrl() {
        assertEquals("https://user:********@example.com/boom", maskAuth("https://user:pass@example.com/boom"));
        assertEquals("not a url?!?", maskAuth("not a url?!?"));
    }

    @Test
    void embedAuthInsertsCredentialsInUrl() {
        assertEquals("https://user:pass@example.com/boom?ka-pow=yes", embedAuth("https://example.com/boom?ka-pow=yes", "user", "pass"));
        assertEquals("https://user:p%2540ss%2521%257E%2525@example.com/boom?ka-pow=yes", embedAuth("https://example.com/boom?ka-pow=yes", "user", "p@ss!~%"));
        assertEquals("https://another:pass2@example.com/boom?ka-pow=yes", embedAuth("https://user:pass@example.com/boom?ka-pow=yes", "another", "pass2"));
        assertEquals("https://example.com/boom?ka-pow=yes", embedAuth("https://example.com/boom?ka-pow=yes", null, ""));
        assertEquals("https://example.com/boom?ka-pow=yes", embedAuth("https://example.com/boom?ka-pow=yes", "user", ""));
        assertEquals("https://example.com/boom?ka-pow=yes", embedAuth("https://example.com/boom?ka-pow=yes", "", "pass"));
        assertEquals("not a url?!?", embedAuth("not a url?!?", "a", "b"));
    }

    @Test
    void stripAuthRemovesCredentials() {
        assertEquals("https://example.com/boom?ka-pow=yes", stripAuth("https://user:pass@example.com/boom?ka-pow=yes"));
        assertEquals("not a url?!?", stripAuth("not a url?!?"));
    }

    @Test
    void encodesForUri() {
        assertEquals("abc+123", encode("abc 123"));
        assertEquals("abc%2C123%21%3F", encode("abc,123!?"));
        assertEquals("abc", encode("abc"));
        assertEquals("", encode(""));
    }
}