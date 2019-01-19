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

import com.fasterxml.jackson.annotation.JsonProperty;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

import static groovy.lang.Closure.DELEGATE_ONLY;
import static lombok.AccessLevel.NONE;

/**
 * Represents an issue tracker.
 * <p>
 * {@includeCode pipeline-with-tracking-tool.groovy}
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true,
        doNotUseGetters = true // because Pattern#equals always returns false.
)
@ToString(callSuper = true,
        doNotUseGetters = true // because Pattern#equals always returns false
)
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
    @JsonProperty("link")
    @NotEmpty
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
    @JsonProperty("regex")
    @NotNull
    @Getter(value = NONE)
    @Setter(value = NONE)
    private String regex;

    @SuppressWarnings("unused" /*method here for deserialization only*/)
    public TrackingTool() {
    }

    public TrackingTool(@DelegatesTo(value = TrackingTool.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.TrackingTool") Closure cl) {
        configure(cl);
    }

    public Pattern getRegex() {
        return regex != null ? Pattern.compile(regex) : null;
    }

    public void setRegex(Pattern regex) {
        this.regex = regex == null ? null : regex.toString();
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
