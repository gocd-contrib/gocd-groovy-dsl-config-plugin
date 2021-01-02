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

package cd.go.contrib.plugins.configrepo.groovy.utils;

import cd.go.contrib.plugins.configrepo.groovy.exceptions.CommandFailedException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;

import static org.codehaus.groovy.runtime.ProcessGroovyMethods.execute;
import static org.codehaus.groovy.runtime.ProcessGroovyMethods.waitForProcessOutput;

public class CommandUtils {

    public static Process exec(List<String> cmd, List<String> envs, File cwd) throws IOException {
        return execute(cmd, envs, cwd);
    }

    public static Process exec(List<String> cmd) throws IOException {
        return execute(cmd);
    }

    public static Process exec(String... cmd) throws IOException {
        return execute(cmd);
    }

    public static void runReadingLines(Process p, Consumer<String> eachLine, OutputStream err) {
        waitForProcessOutput(p, new LineProcessingOutputStream(eachLine), err);
    }

    public static CommandFailedException failed(String message) {
        return new CommandFailedException(message);
    }

    public static CommandFailedException failed(String message, Throwable cause) {
        return new CommandFailedException(message, cause);
    }
}
