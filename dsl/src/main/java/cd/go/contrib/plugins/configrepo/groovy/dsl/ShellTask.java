/*
 * Copyright 2018 ThoughtWorks, Inc.
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

import com.google.gson.JsonObject;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import static groovy.lang.Closure.DELEGATE_ONLY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Executes a shell script.
 * <p>
 * {@includeCode shell-large-examples.groovy}
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ShellTask extends Task<ShellTask> {

    /**
     * The shell command to be executed {@code bash}, {@code zsh}. Must be on PATH.
     */
    private String shell;

    /**
     * The directory in which the script or command is to be executed.
     * <p>
     * Note that this directory is relative to the directory where the agent checks out the materials.
     */
    private String workingDir;

    /**
     * The command string to be executed using the {@link #shell}'s {@code -c} argument
     *
     * <p>
     * <strong>Note: </strong> Must either specify {@link #commandString} or {@link #file}
     * <p>
     * This is the same as running:
     * <p>
     * {@includeCode shell-with-command-string.groovy}
     */
    private String commandString;

    /**
     * The file to be passed to the {@link #shell}.
     *
     * <p>
     * <strong>Note: </strong> Must either specify {@link #commandString} or {@link #file}
     * <p>
     * This is the same as running {@code "bash FILE"}
     */
    private String file;

    /**
     * Sets the {@code -l} or {@code --login} argument to your shell. This will usually load up the {@code .profile}
     * (or equivalent) of the shell.
     *
     * @see #loadProfile()
     */
    private Boolean login;

    ShellTask() {
        this(null, null);
    }

    public ShellTask(String shell, @DelegatesTo(value = ShellTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ShellTask") Closure cl) {
        super("exec");
        this.shell = shell;
        configure(cl);
    }

    public void loadProfile() {
        login = true;
    }

    @Override
    public JsonObject toJson() {
        ExecTask execTask = new ExecTask();
        execTask.setWorkingDir(workingDir);

        execTask.getCommandLine().add(shell);

        if (Boolean.TRUE.equals(login)) {
            execTask.getCommandLine().add("-l");
        }

        if (isNotEmpty(commandString)) {
            execTask.getCommandLine().add("-c");
            execTask.getCommandLine().add(commandString);
        } else {
            execTask.getCommandLine().add(file);
        }

        return execTask.toJson();
    }

}
