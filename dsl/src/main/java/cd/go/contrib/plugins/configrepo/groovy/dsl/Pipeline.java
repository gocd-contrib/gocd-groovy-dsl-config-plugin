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
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.LinkedHashMap;
import java.util.Map;

import static groovy.lang.Closure.DELEGATE_ONLY;
import static lombok.AccessLevel.NONE;

/**
 * Represents a
 * <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#pipeline">pipeline config</a>
 * object.
 * <p>
 * {@includeCode pipeline-with-simple-artifacts.groovy}
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Pipeline extends HasEnvironmentVariables<Pipeline> {

    /**
     * The name of the pipeline group that this pipeline belongs to.
     */
    @JsonProperty("group")
    @NotEmpty
    private String group;

    /**
     * Pipeline label templates provide a means to label a pipeline or artefacts using a counter, or material
     * revision (or both).
     * <p>
     * Valid substitutions are:
     * <table summary="substitutions">
     * <tr>
     * <td>${COUNT}</td><td>The pipeline counter (starts at 1).</td>
     * </tr>
     * <tr>
     * <td>${&lt;material-name&gt;}</td><td>The revision of the material named "material-name". The "material-name" can
     * be the name of an SCM material, or a pipeline material.</td>
     * </tr>
     * <tr>
     * <td>${&lt;material-name&gt;[:&lt;length&gt;]}</td><td>The first "length" characters of revision of the material
     * named "material-name".</td>
     * </tr>
     * <tr>
     * <td>#{&lt;parameter-name&gt;}</td><td>Substitute the value of the parameter named "parameter-name".</td>
     * </tr>
     * </table>
     * <p>
     * An example of a label template is:
     * <ul>
     * <li><code>15.1-${COUNT}</code> or
     * <li><code>15.1-${COUNT}-${svn}</code> or
     * <li><code>15.1-${COUNT}-${git[:7]}</code>
     * </ul>
     */
    @JsonProperty("label_template")
    private String labelTemplate;

    /**
     * The possible values are {@code none}, {@code lockOnFailure} or {@code unlockWhenFinished}.
     * <p>
     * When set to {@code lockOnFailure}, GoCD ensures that only a single instance of a
     * pipeline can be run at a time and the pipeline will be locked if it fails,
     * unless it is the last stage which fails.
     * <p>
     * When set to {@code unlockWhenFinished}, GoCD ensures that only a single instance
     * of a pipeline can be run at a time, and the pipeline will be unlocked as soon as it
     * finishes (success or failure), or reaches a manual stage.
     * <p>
     * The default value is {@code none}.
     */
    @JsonProperty("lock_behavior")
    @OneOfStrings(value = {"none", "lockOnFailure", "unlockWhenFinished"})
    private String lockBehavior;

    /**
     * The issue tracker associated with this pipeline.
     * <p>
     * {@includeCode pipeline-with-tracking-tool.groovy}
     */
    @Getter(value = NONE)
    @Setter(value = NONE)
    @JsonProperty("tracking_tool")
    @Valid
    private TrackingTool trackingTool;

    /**
     * The name of the template that this pipeline references. If set, no stages may be defined in this pipeline.
     */
    @JsonProperty("template")
    private String template;

    /**
     * Specify a cron-like schedule to build the pipeline.
     *
     * @see Timer
     */
    @Getter(value = NONE)
    @Setter(value = NONE)
    @JsonProperty("timer")
    @Valid
    private Timer timer;

    /**
     * The list of parameter substitutions to be used in a pipeline or a template.
     * <p>
     * {@includeCode pipeline-with-params.groovy}
     *
     * @see <a href='https://docs.gocd.org/current/configuration/configuration_reference.html#param'>Parameter reference</a>
     */
    private Map<String, String> params = new LinkedHashMap<>();

    @Getter(value = NONE)
    @Setter(value = NONE)
    @JsonProperty("materials")
    @Valid
    private Materials materials = new Materials();

    @JsonProperty("stages")
    @Valid
    @Getter(value = NONE)
    @Setter(value = NONE)
    private Stages stages = new Stages();

    public Pipeline() {
        this(null, null);
    }

    public Pipeline(String name) {
        this(name, null);
    }

    public Pipeline(String name, @DelegatesTo(value = Pipeline.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.Pipeline") Closure cl) {
        super(name);
        configure(cl);
    }

    /**
     * @see TrackingTool
     */
    public TrackingTool trackingTool(@DelegatesTo(value = TrackingTool.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.TrackingTool") Closure cl) {
        trackingTool = new TrackingTool(cl);
        return trackingTool;
    }

    /**
     * @see Timer
     */
    public Timer timer(@DelegatesTo(value = Timer.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.Timer") Closure cl) {
        timer = new Timer(cl);
        return timer;
    }

    /**
     * @see Material
     */
    public Materials materials(@DelegatesTo(value = Materials.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.Materials") Closure cl) {
        materials.configure(cl);
        return materials;
    }

    /**
     * @see Stage
     */
    public Stages stages(@DelegatesTo(value = Stages.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.Stages") Closure cl) {
        stages.configure(cl);
        return stages;
    }

}
