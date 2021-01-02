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

import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines

new Pipelines().with {
  pipeline('build') {
    secureEnvironmentVariables = [
      SSH_PASSWORD: 'AES:yBk975e1rJiuRnAx9AZ6Og==:ee6yi/2sEh5/TUlbmNWelg==',
      API_TOKEN   : 'AES:mYCdWAx/pR0YKLpSJHF9ZQ==:zbSkQLjsL34pzXDtyTj0iw=='
    ]
  }
}
