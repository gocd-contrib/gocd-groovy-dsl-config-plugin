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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
abstract class Task<T extends Node> extends Node<T> {

    private static final List<String> VALID_RUN_IFS = Arrays.asList("passed", "failed", "any");

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Expose
    @SerializedName("type")
    private final String type;

    /**
     * Specifies when a task should be allowed to run. Multiple conditions may be defined for each task.
     * <p>
     * A running job on an agent has two possible states: passed or failed. A job starts in the state {@code passed}. If
     * any task fails, it transitions to the state {@code failed}.
     * <p>
     * A task can specify any of three possible runif filters: {@code passed}, {@code failed} or {@code any}. Defaults
     * to {@code passed}.
     */
    @Expose
    @SerializedName("run_if")
    private String runIf;

    protected Task(String type) {
        this.type = type;
    }

    public void setRunIf(String newValue) {
        if (VALID_RUN_IFS.contains(newValue.toLowerCase())) {
            this.runIf = newValue.toLowerCase();
        } else {
            throw new IllegalArgumentException("Illegal value for run if: " + newValue);
        }
    }
}
