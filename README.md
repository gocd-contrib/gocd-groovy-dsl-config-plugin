[![Google Groups](https://img.shields.io/badge/Google_Groups-user_help-purple)](https://groups.google.com/g/go-cd)
[![GitHub Discussions](https://img.shields.io/badge/GitHub_discussions-user_&amp;_dev_chat-green)](https://github.com/gocd/gocd/discussions)
[![javadoc.io](https://javadoc.io/badge2/cd.go.groovydsl/dsl/javadoc.io.svg)](https://javadoc.io/doc/cd.go.groovydsl/dsl)
[![Maven Central](https://img.shields.io/maven-central/v/cd.go.groovydsl/dsl.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22cd.go.groovydsl%22%20AND%20a:%22dsl%22)

## GoCD Groovy DSL Configuration Plugin

A GoCD plugin that allows maintaining pipelines and environments in source control using a DSL written in the groovy language.

Table of Contents
=================

* [A note about security](#a-note-about-security)
* [Install](#install)
* [Linting or verifying your DSL locally](#linting-or-verifying-your-dsl-locally)
   * [Lint usage](#lint-usage)
* [Support for Branches and PRs](#support-for-branches-and-prs)
* [Example](#example)
* [License](#license)
      
## A note about security

This plugin evaluates untrusted code on the GoCD server. A malicious script can do significant damage (steal keys and secrets, remove files and directories, install malware, etc). As such, **please** use this plugin only with GoCD servers and repositories where you completely trust the users who will have permissions to commit to the groovy-based config repo.

## Install

- Download the plugin jar from the [downloads page](https://github.com/gocd-contrib/gocd-groovy-dsl-config-plugin/releases) and place the jar file on the GoCD server in `plugins/external` [directory](https://docs.gocd.org/current/extension_points/plugin_user_guide.html).

- Add `config-repos` element right above first `<pipelines />`. Then you can add any number of repository configuration repositories as such:

    ```xml
    <config-repos>
      <config-repo pluginId="cd.go.contrib.plugins.configrepo.groovy" id="my-project">
        <git url="https://github.com/gocd-contrib/gocd-groovy-dsl-config-plugin.git" />
      </config-repo>
    </config-repos>
    ```

- The plugin will now scan and read files ending with the extension `.gocd.groovy` and setup pipelines defined in those files.

## Linting or verifying your DSL locally

There are some basic standalone linting abilities provided by the plugin:

```shell
java -jar groovy-dsl-plugin.jar syntax file1
```

### Lint and validation of Groovy DSL

```shell
$ java -jar groovy-dsl-plugin.jar
Usage: java -jar groovy-dsl-plugin.jar [options] [command] [command options]
  Options:
    --help, -h
      print this help
  Commands:
    syntax      perform syntax checking of specified file
      Usage: syntax [options] [file|-]
        Options:
          --help, -h
            print this help
          --json, -j
            show generated json for the config
            Default: false
```

## How do I know which parts of the DSL to use?

Generally speaking, the philosophy of this plugin is to allow for IDE and Groovy language-driven support rather than providing static documentation of the structure as with other non-language-based GoCD "pipelines as code" plugins. Alongside getting clues from examples, we'd generally suggest following the below steps to set up an IDE with the DSL dependency and being able to benefit from auto-completion/suggests as well as viewing the API documentation inline with editing.

### Auto complete in your IDE

If you're using an IDE like IntelliJ or Eclipse, you can import the `dsl.jar` file from the [downloads page](https://github.com/gocd-contrib/gocd-groovy-dsl-config-plugin/releases), or using the following maven co-ordinates in your build script:

```xml
<!-- maven -->
<dependency>
  <groupId>cd.go.groovydsl</groupId>
  <artifactId>dsl</artifactId>
  <!-- get version from https://mvnrepository.com/artifact/cd.go.groovydsl/dsl -->
  <version>XXX</version>
</dependency>
```

```groovy
// groovy build.gradle
dependencies {
  // get version from https://mvnrepository.com/artifact/cd.go.groovydsl/dsl
  compileOnly group: 'cd.go.groovydsl', name: 'dsl', version: 'XXX'
}
```

### Static DSL reference documentation

If you don't use an IDE, an alternative is to use the API Javadocs. Since the intent is for this to be downloaded and viewing in an IDE it is not published somewhere statically, but you can download/view localy:
1. Find the relevant version of the plugin from https://mvnrepository.com/artifact/cd.go.groovydsl/dsl
2. Navigate to Files < View All
3. Download the `dsl-${version}-javadoc.jar`
4. Unzip it and open `index.html` in a browser

## Support for Branches and PRs

Often you would want to run your builds against multiple branches and PRs. Manually configuring pipelines/workflow for each branch/PR could be cumbersome.
The plugin provides an ability to templatize a pipeline or an entire workflow to run your builds against each branch and PR. Once a template is defined,
the plugin scans the configured repository at a regular interval and for each available branch/PR it builds the corresponding pipelines.

The plugin also provides notification capability, you can configure the plugin to notify your pull requests with the build status.

For a working example, refer to the [Groovy Script](https://github.com/gocd/pr-pipelines-workflow/blob/master/.gocd/build.gocd.groovy) which generates the PR workflow
under the pipeline group `gocd-pull_8567-Groovy_plugin_branch_PR_feature_example` on https://build.gocd.org. You will have to login as Guest user to view this pipeline group.

You can refer to [PR support examples](example/src/main/groovy/pull_request_support) for comprehensive examples for each provider.

## Example

Here is a simple example of the DSL, there are more examples in the [examples directory](example/src/main/groovy).

```groovy
import cd.go.contrib.plugins.configrepo.groovy.dsl.*

GoCD.script {
  pipelines {
    pipeline('build') {
      group = 'go-cd'
      lockBehavior = 'lockOnFailure'
      trackingTool {
        link = 'https://github.com/gocd-contrib/gocd-groovy-dsl-config-plugin/issues/${ID}'
        regex = ~/##(\d+)/
      }

      environmentVariables = [
        'pipeline-var': 'pipeline-value'
      ]

      materials {
        git('my-repo') {
          url = 'https://github.com/gocd-contrib/gocd-groovy-dsl-config-plugin'
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
Copyright 2022 Thoughtworks, Inc.

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
