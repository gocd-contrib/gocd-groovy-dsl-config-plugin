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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

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
@EqualsAndHashCode(callSuper = true)
public class ExecTask extends Task<ExecTask> {

    /**
     * The directory in which the script or command is to be executed.
     * <p>
     * Note that this directory is relative to the directory where the agent checks out the materials.
     */
    @Expose
    @SerializedName("working_directory")
    private String workingDir;

    /**
     * The command line to be executed.
     */
    private List<String> commandLine = new LinkedList<>();

    public ExecTask() {
        this(null);
    }

    public ExecTask(@DelegatesTo(value = ExecTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ExecTask") Closure cl) {
        super("exec");
        configure(cl);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = (JsonObject) super.toJson();
        jsonObject.addProperty("command", commandLine.get(0));

        if (commandLine.size() > 1) {
            JsonArray arguments = new JsonArray();
            commandLine.subList(1, commandLine.size()).forEach(arguments::add);
            jsonObject.add("arguments", arguments);
        }

        return jsonObject;
    }

}
