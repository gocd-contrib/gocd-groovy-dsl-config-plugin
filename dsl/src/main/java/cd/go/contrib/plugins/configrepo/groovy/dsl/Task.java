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

import cd.go.contrib.plugins.configrepo.groovy.dsl.util.OneOfStrings;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class Task<T extends Node> extends Node<T> {

    /**
     * Specifies when a task should be allowed to run. Multiple conditions may be defined for each task.
     * <p>
     * A running job on an agent has two possible states: passed or failed. A job starts in the state {@code passed}. If
     * any task fails, it transitions to the state {@code failed}.
     * <p>
     * A task can specify any of three possible runif filters: {@code passed}, {@code failed} or {@code any}. Defaults
     * to {@code passed}.
     */
    @JsonProperty("run_if")
    @OneOfStrings(value = {"passed", "failed", "any"})
    private String runIf;

    // this is only used for serialization, so no getters/setters and is marked transient
    @Getter(NONE)
    @Setter(NONE)
    @JsonProperty("type")
    private transient String type;

    @SuppressWarnings("unused" /*method here for serialization only*/)
    protected Task(String type) {
        this.type = type;
    }

}
