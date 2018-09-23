[![Join the chat at https://gitter.im/gocd/configrepo-plugins](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/gocd/configrepo-plugins?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## GoCD Groovy DSL Configuration Plugin 

A GoCD plugin that allows maintaining pipelines and environments in source control using a DSL written in the groovy language.

Table of Contents
=================

* [A note about security](#a-note-about-security)
* [Install](#install)
* [Linting or verifying your DSL locally](#linting-or-verifying-your-dsl-locally)
   * [Lint usage](#lint-usage)
* [Example](#example)
* [License](#license)
      
## A note about security

This plugin evaluates untrusted code, on the GoCD server. A malicious script can do significant damage (steal keys and secrets, remove files and directories, install malware, etc). Since evaluating the groovy code in a sandbox is currently a work in progress, **please** use this plugin only with GoCD servers and repositories where you completely trust your users.

## Install

- Download the plugin jar from the [downloads page](https://github.com/gocd-contrib/gocd-groovy-dsl-config-plugin/releases) and place the jar file on the GoCD server in `plugins/external` [directory](https://docs.gocd.org/current/extension_points/plugin_user_guide.html).

- Add `config-repos` element right above first `<pipelines />`. Then you can add any number of repository configuration repositories as such:

    ```xml
    <config-repos>
      <config-repo pluginId="cd.go.contrib.plugins.configrepo.groovy" id="my-project">
        <git url="https://github.com/ketan/gocd-groovy-dsl-config-plugin.git" />
      </config-repo>
    </config-repos>
    ```

- The plugin will now scan and read files ending with the extension `.gocd.groovy` and setup pipelines defined in those files.

## Linting or verifying your DSL locally

There is some basic standalone linting abilities provided by the plugin:

```shell
java -jar groovy-dsl-plugin.jar --files file1,file2
```

### Lint and validation of Groovy DSL

```shell
$ java -jar groovy-dsl-plugin.jar
The following option is required: [--files | -f]
Usage: java -jar groovy-dsl-plugin.jar [options]
  Options:
  * --files, -f
      comma-separated list of files to verify
    --help, -h
      print this help
    --json, -j
      show generated json for the config
      Default: false
```

## Auto complete in your IDE

If you're using an IDE like IntelliJ or Eclipse, you can import the `dsl.jar` file from the [downloads page](https://github.com/gocd-contrib/gocd-groovy-dsl-config-plugin/releases), or using the following maven co-ordinates in your build script:

```xml
<!-- maven -->
<dependency>
  <groupId>com.github.ketan</groupId>
  <artifactId>dsl</artifactId>
  <version>0.1.0-1</version>
</dependency>
```

```groovy
// gradle
compileOnly group: 'com.github.ketan', name: 'dsl', version: '0.1.0-1'
```

## Example

Here is a simple example of the DSL, there are mode examples in the [examples directory](example/src/main/groovy).

```groovy
import cd.go.contrib.plugins.configrepo.groovy.dsl.*

GoCD.script {
  pipelines {
    pipeline('build') {
      group = 'go-cd'
      lockBehavior = 'lockOnFailure'
      trackingTool {
        link = 'https://github.com/ketan/gocd-groovy-dsl-config-plugin/issues/${ID}'
        regex = ~/##(\d+)/
      }

      environmentVariables = [
        'pipeline-var': 'pipeline-value'
      ]

      materials {
        git('my-repo') {
          url = 'https://github.com/ketan/gocd-groovy-dsl-config-plugin'
          branch = 'master'
          blacklist = ['README.md', "examples/**/*"]
        }
      }

      stages {
        stage('test') {
          environmentVariables = [
            'stage-var': 'stage-value'
          ]
          jobs {
            job('build') {
              tasks {
                bash {
                  commandString = "git clean -fdx"
                }

                bash {
                  commandString = './gradlew clean assemble test'
                }
              }
            }
          }
        }
      }
    }
  }
}
```

## License

```plain
Copyright 2018, ThoughtWorks, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
