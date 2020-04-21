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

import static groovy.lang.Closure.DELEGATE_ONLY;

/**
 * Represents a timer specification to perform cron-like trigggers of pipelines.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Timer extends Node<Timer> {

    /**
     * Specify a cron-like schedule to build the pipeline.
     * <p>
     * A cron expression is a string comprised of 6 or 7 fields separated by white space.
     * Fields can contain any of the allowed values, along with various combinations of the allowed special
     * characters for that field.
     * </p>
     * Examples:
     * <table>
     * <caption>Cron expression examples</caption>
     * <thead>
     * <tr><th>Example</th><th>Description</th></tr>
     * </thead>
     * <tbody>
     * <tr><th>0 0 0/4 * * ?</th><th>Run every 4th hour</th></tr>
     * <tr><th>0 0 1 * * ?</th><th>Run at 1:00 AM everyday</th></tr>
     * <tr><th>0 43 2 * * sat</th><th>Run at 2:43 AM every saturday</th></tr>
     * <tr><th>0 15 10 3 * ?</th><th>Run at 10:15 AM on the 3rd day of every month</th></tr>
     * </tbody>
     * </table>
     * <p>
     * The fields are as follows:
     * </p>
     * <p>
     * <code>seconds minutes hours day_of_month month day_of_week [year(optional)]</code>
     * </p>
     * <table>
     * <caption>Cron expression fields</caption>
     * <thead>
     * <tr>
     * <th>Field</th>
     * <th>Allowed Values</th>
     * <th>Allowed Special Characters</th>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     * <td>seconds</td>
     * <td>
     * <code>0-59</code>
     * </td>
     * <td>
     * <code>, - * /</code>
     * </td>
     * </tr>
     * <tr>
     * <td>minutes</td>
     * <td>
     * <code>0-59</code>
     * </td>
     * <td>
     * <code>, - * /</code>
     * </td>
     * </tr>
     * <tr>
     * <td>hours</td>
     * <td>
     * <code>0-23</code>
     * </td>
     * <td>
     * <code>, - * /</code>
     * </td>
     * </tr>
     * <tr>
     * <td>day of month</td>
     * <td>
     * <code>0-31</code>
     * </td>
     * <td>
     * <code>, - * ? / L W</code>
     * </td>
     * </tr>
     * <tr>
     * <td>month</td>
     * <td>
     * <code>1-12</code>{' '}OR{' '}<code>jan-dec</code>
     * </td>
     * <td>
     * <code>, - * /</code>
     * </td>
     * </tr>
     * <tr>
     * <td>day of week</td>
     * <td>
     * <code>1-7</code>{' '}OR{' '}<code>sun-sat</code>
     * </td>
     * <td>
     * <code>, - * ? / L #</code>
     * </td>
     * </tr>
     * <tr>
     * <td>year</td>
     * <td>
     * <code>1970-2099</code>
     * </td>
     * <td>
     * <code>, - * /</code>
     * </td>
     * </tr>
     * </tbody>
     * </table>
     * <p>
     * A field may be an asterix (<code>*</code>), which stands for "all possible values".
     * </p>
     * <p>
     * A field may be a question mark (<code>?</code>), which stands for "no specific value". This is useful if
     * I want my trigger to fire on a particular day of the month (say, the 10th), but don't care what day of
     * the week that happens to be, I would put <code>10</code> in the day-of-month field, and <code>?</code>
     * in the day-of-week field.
     * </p>
     * <p>
     * A field may contain a slash (<code>/</code>) to signify increments. For example, <code>0/15</code> in
     * the seconds field means "the seconds 0, 15, 30, and 45".
     * </p>
     * <p>
     * Ranges of numbers are allowed. Ranges are two numbers separated with a hyphen. For example,
     * <code>8-11</code> for an "hours" entry specifies execution at hours 8, 9, 10 and 11.
     * </p>
     * <p>
     * Lists are allowed. A list is a set of numbers (or ranges) separated by commas. Examples:
     * <code>1,2,5,9</code>, <code>0-4,8-12</code>
     * </p>
     * <p>
     * Step values can be used together with ranges. For example, <code>10-22/4</code> can be used in the
     * hours field to specify command execution every at the hours 10, 14, 18, 22.
     * </p>
     *
     * @see <a href="http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger">Quartz Cron
     * documentation</a>
     */
    @JsonProperty("spec")
    @NotEmpty
    private String spec;

    /**
     * Skips scheduling if the previous run of the pipeline was with the latest material(s). This option is typically
     * useful when automatic pipeline scheduling is turned off.
     */
    @JsonProperty("only_on_changes")
    private Boolean onlyOnChanges;

    @SuppressWarnings("unused" /*method here for deserialization only*/)
    public Timer() {
    }

    public Timer(@DelegatesTo(value = Timer.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.Timer") Closure cl) {
        configure(cl);
    }

}
