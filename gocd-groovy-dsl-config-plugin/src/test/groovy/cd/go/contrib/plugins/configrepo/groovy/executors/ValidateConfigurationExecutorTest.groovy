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

import cd.go.contrib.plugins.configrepo.groovy.requests.ValidatePluginSettingsRequest
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse
import groovy.json.JsonSlurper
import org.junit.jupiter.api.Test

import static groovy.json.JsonOutput.toJson
import static org.junit.jupiter.api.Assertions.assertEquals

class ValidateConfigurationExecutorTest {
  private static final JsonSlurper JSON = new JsonSlurper()

  @Test
  void 'validates required fields'() {
    def resp = validate(toJson(['plugin-settings': [:]]))
    assertEquals(200, resp.responseCode())
    assertEquals([
      [
        key    : 'server_base_url',
        message: 'The Server Base URL must be set'
      ]
    ], JSON.parseText(resp.responseBody()))
  }

  @Test
  void 'validates server url format'() {
    def resp = validate(toJson(['plugin-settings': [
      server_base_url: [value: '/a/b/c'],
    ]]))
    assertEquals(200, resp.responseCode())
    assertEquals([
      [
        key    : 'server_base_url',
        message: 'The Server Base URL must include a scheme'
      ]
    ], JSON.parseText(resp.responseBody()))

    resp = validate(toJson(['plugin-settings': [
      server_base_url: [value: '1:2:3'],
    ]]))
    assertEquals(200, resp.responseCode())
    assertEquals([
      [
        key    : 'server_base_url',
        message: 'Invalid URL; failed to parse [1:2:3]'
      ]
    ], JSON.parseText(resp.responseBody()))

    resp = validate(toJson(['plugin-settings': [
      server_base_url: [value: 'http://host.tld:123/go#foo'],
    ]]))
    assertEquals(200, resp.responseCode())
    assertEquals([
      [
        key    : 'server_base_url',
        message: 'The Server Base URL must not contain a query or fragment'
      ]
    ], JSON.parseText(resp.responseBody()))

    resp = validate(toJson(['plugin-settings': [
      server_base_url: [value: 'https://host.tld/go?q=1'],
    ]]))
    assertEquals(200, resp.responseCode())
    assertEquals([
      [
        key    : 'server_base_url',
        message: 'The Server Base URL must not contain a query or fragment'
      ]
    ], JSON.parseText(resp.responseBody()))

    resp = validate(toJson(['plugin-settings': [
      server_base_url: [value: 'http://host.tld'],
    ]]))
    assertEquals(200, resp.responseCode())
    assertEquals([
      [
        key    : 'server_base_url',
        message: 'The Server Base URL must end with `/go`'
      ]
    ], JSON.parseText(resp.responseBody()))

    resp = validate(toJson(['plugin-settings': [
      server_base_url: [value: 'http://host.tld/go?'],
    ]]))
    assertEquals(200, resp.responseCode())
    assertEquals([
      [
        key    : 'server_base_url',
        message: 'The Server Base URL must end with `/go`'
      ]
    ], JSON.parseText(resp.responseBody()))

    resp = validate(toJson(['plugin-settings': [
      server_base_url: [value: 'http://host.tld/go#'],
    ]]))
    assertEquals(200, resp.responseCode())
    assertEquals([
      [
        key    : 'server_base_url',
        message: 'The Server Base URL must end with `/go`'
      ]
    ], JSON.parseText(resp.responseBody()))

    resp = validate(toJson(['plugin-settings': [
      server_base_url: [value: 'http://host.tld/go?#'],
    ]]))
    assertEquals(200, resp.responseCode())
    assertEquals([
      [
        key    : 'server_base_url',
        message: 'The Server Base URL must end with `/go`'
      ]
    ], JSON.parseText(resp.responseBody()))
  }

  private static GoPluginApiResponse validate(String json) {
    return new ValidateConfigurationExecutor(ValidatePluginSettingsRequest.fromJSON(json)).execute()
  }
}
