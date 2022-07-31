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

import cd.go.contrib.plugins.configrepo.groovy.dsl.Jobs

new Jobs().with {
  job('build') {
    artifacts {
      external {
        id = 'docker-release-candidate'
        storeId = 'dockerhub'
        configuration = [
          image: 'gocd/gocd-server',
          tag  : 'latest'
        ]
        secureConfiguration = [
          DB_PASSWORD: 'AES:yBk975e1rJiuRnAx9AZ6Og==:ee6yi/2sEh5/TUlbmNWelg=='
        ]
      }
    }
  }
}

