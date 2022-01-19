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

package cd.go.contrib.plugins.configrepo.groovy.dsl;

import cd.go.contrib.plugins.configrepo.groovy.dsl.util.CheckAtLeastOneNotEmpty;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotEmpty;

import static groovy.lang.Closure.DELEGATE_ONLY;

/**
 * Executes a shell script.
 * <p>
 * {@includeCode shell-large-examples.groovy}
 */
@Getter
@Setter
@CheckAtLeastOneNotEmpty(fieldNames = {"commandString", "file"})
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ShellTask extends Task<ShellTask> {

    /**
     * The shell command to be executed {@code bash}, {@code zsh}. Must be on PATH.
     */
    @NotEmpty
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

    public ShellTask() {
        this(null, null);
    }

    public ShellTask(String shell, @DelegatesTo(value = ShellTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ShellTask") Closure cl) {
        super(null);
        this.shell = shell;
        configure(cl);
    }

    public void loadProfile() {
        login = true;
    }

    public ExecTask toExecTask() {
        ExecTask execTask = new ExecTask();
        execTask.setWorkingDir(getWorkingDir());

        execTask.getCommandLine().add(getShell());

        if (Boolean.TRUE.equals(getLogin())) {
            execTask.getCommandLine().add("-l");
        }

        if (!(getCommandString() == null || ((CharSequence) getCommandString()).length() == 0)) {
            execTask.getCommandLine().add("-c");
            execTask.getCommandLine().add(getCommandString());
        } else {
            execTask.getCommandLine().add(getFile());
        }

        return execTask;
    }

}
