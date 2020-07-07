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

package cd.go.contrib.plugins.configrepo.groovy.dsl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BranchContextSerializationTest {

    @Test
    void deserialization() throws JsonProcessingException {
        final String payload = "{" +
                "\"branch_name\": \"foo\"," +
                "\"sanitized_branch_name\": \"foo\"," +
                "\"full_ref_name\": \"refs/heads/foo\"," +
                "\"repo\": {" +
                "    \"type\": \"git\"," +
                "    \"url\": \"https://gitbud.com/repo.git\"," +
                "    \"branch\": \"foo\"," +
                "    \"username\": \"git\"," +
                "    \"encrypted_password\": \"abcd1234\"," +
                "    \"shallow_clone\": true" +
                "}" +
                "}";
        final BranchContext c = new ObjectMapper().readValue(payload, BranchContext.class);
        assertNotNull(c);
        assertNotNull(c.getRepo());

        assertEquals("foo", c.getBranch());
        assertEquals("https://gitbud.com/repo.git", ((GitMaterial) c.getRepo()).getUrl());
    }

    @Test
    void deserializationList() throws JsonProcessingException {
        final String payload = "[{" +
                "\"branch_name\": \"foo\"," +
                "\"sanitized_branch_name\": \"foo\"," +
                "\"full_ref_name\": \"refs/heads/foo\"," +
                "\"repo\": {" +
                "    \"type\": \"git\"," +
                "    \"url\": \"https://gitbud.com/repo.git\"," +
                "    \"branch\": \"foo\"," +
                "    \"username\": \"git\"," +
                "    \"encrypted_password\": \"abcd1234\"," +
                "    \"shallow_clone\": true" +
                "}" +
                "}]";
        final List<BranchContext> l = new ObjectMapper().readValue(payload, new TypeReference<>() {
        });
        assertNotNull(l);
        assertEquals(1, l.size());

        final BranchContext c = l.get(0);
        assertNotNull(c);
        assertNotNull(c.getRepo());

        assertEquals("foo", c.getBranch());
        assertEquals("https://gitbud.com/repo.git", ((GitMaterial) c.getRepo()).getUrl());
    }

}
