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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

import static groovy.lang.Closure.DELEGATE_ONLY;

/**
 * Represents a task that represents the semantics of the {@link Runtime#exec(String[])} system call.
 * <p>
 * {@includeCode exec-task.groovy}
 *
 * @see ShellTask
 * @see Runtime#exec(String[])
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ExecTask extends Task<ExecTask> {

    /**
     * The directory in which the script or command is to be executed.
     * <p>
     * Note that this directory is relative to the directory where the agent checks out the materials.
     */
    @JsonProperty("working_directory")
    private String workingDir;

    /**
     * The command line to be executed.
     */
    @JsonIgnore
    private List<String> commandLine = new LinkedList<>();

    public ExecTask() {
        this(null);
    }

    public ExecTask(@DelegatesTo(value = ExecTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ExecTask") Closure cl) {
        super("exec");
        configure(cl);
    }

    @JsonAnyGetter
    @SuppressWarnings("unused" /*method here for serialization only*/)
    private Map<String, Object> partialSeserialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        List<String> commandLine = getCommandLine();

        result.put("command", commandLine.get(0));

        if (commandLine.size() > 1) {
            List<String> arguments = new ArrayList<>();
            result.put("arguments", new ArrayList<>(commandLine.subList(1, commandLine.size())));
        }

        return result;
    }

    @JsonAnySetter
    @SuppressWarnings("unused" /*method here for deserialization only*/)
    private void partialDeserialize(String key, Object value) {
        if (key.equals("command")) {
            commandLine.add(0, (String) value);
        } else if (key.equals("arguments")) {
            commandLine.addAll(1, (List<String>) value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) {
            return false;
        }

        if (o.getClass() == ShellTask.class) {
            return ((ShellTask) o).toExecTask().equals(this);
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) return false;

        ExecTask execTask = (ExecTask) o;

        if (workingDir != null ? !workingDir.equals(execTask.workingDir) : execTask.workingDir != null) return false;
        return commandLine != null ? commandLine.equals(execTask.commandLine) : execTask.commandLine == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (workingDir != null ? workingDir.hashCode() : 0);
        result = 31 * result + (commandLine != null ? commandLine.hashCode() : 0);
        return result;
    }
}
