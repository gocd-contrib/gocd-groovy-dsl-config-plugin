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
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

import static groovy.lang.Closure.DELEGATE_ONLY;

/**
 * Represents an issue tracker.
 * <p>
 * {@includeCode pipeline-with-tracking-tool.groovy}
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class TrackingTool extends Node<TrackingTool> {

    /**
     * The URL with a string '${ID}'. GoCD will replace the string '${ID}' with the first matched group value at
     * run-time.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code https://github.com/gocd/gocd/issues/${ID}} — GitHub issues
     * <li>{@code https://bugzilla.example.com/bugs/bug.php?id=${ID}} — Bugzilla issue
     * <li>{@code https://jira.example.com/jira/browse/${ID}} — Jira Issue
     * </ul>
     */
    @Expose
    @SerializedName("link")
    private String link;

    /**
     * A regex to identify the IDs. GoCD will find the first matched group in your commit messages and use it to
     * construct the hyper-link.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code ~/##(\d+)/} — Will extract "{@code 1748}" from the message "{@code Improve message on login failure
     * (fixes #1748).}"
     * <li>{@code ~/(JIRA-\d+)/} — Will extract "{@code JIRA-1748}" from the message "{@code Improve message on login
     * failure (fixes JIRA-1748).}"
     * </ul>
     */
    @Expose
    @SerializedName("regex")
    private Pattern regex;

    public TrackingTool(@DelegatesTo(value = TrackingTool.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.TrackingTool") Closure cl) {
        configure(cl);
    }

}
