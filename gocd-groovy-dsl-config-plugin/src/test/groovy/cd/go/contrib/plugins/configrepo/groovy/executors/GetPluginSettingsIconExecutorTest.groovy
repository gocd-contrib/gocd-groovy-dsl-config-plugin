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

package cd.go.contrib.plugins.configrepo.groovy.executors

import cd.go.contrib.plugins.configrepo.groovy.utils.Util
import com.fasterxml.jackson.databind.ObjectMapper
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class GetPluginSettingsIconExecutorTest {

  @Test
  void rendersIconInBase64() throws Exception {
    GoPluginApiResponse response = new GetPluginSettingsIconExecutor().execute()

    assertEquals(200, response.responseCode())
    ObjectMapper objectMapper = new ObjectMapper()
    Map<String, String> hashMap = objectMapper.readValue(response.responseBody(), objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class))

    assertEquals(2, hashMap.size())
    assertEquals("image/svg+xml", hashMap.get("content_type"))
    assertEquals(Base64.getEncoder().encodeToString(Util.readResourceBytes("/groovy.svg")), hashMap.get("data"))
  }
}
