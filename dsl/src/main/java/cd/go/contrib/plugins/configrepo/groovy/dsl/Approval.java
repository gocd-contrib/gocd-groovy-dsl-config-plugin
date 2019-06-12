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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static lombok.AccessLevel.NONE;

/**
 * Specifies how a stage should be triggered. Approval of type 'manual' or 'success' can be used to stop a pipeline
 * execution at the start of a stage and can only be resumed when it is manually approved on the pipeline activity page,
 * stage details page or through RESTful url.
 * <p>
 * {@includeCode approval.groovy}
 *
 * @see <a href="https://docs.gocd.org/current/configuration/configuration_reference.html#approval">approval config</a>
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Approval extends Node<Approval> {

    private static final List<String> APPROVAL_TYPES = Arrays.asList("success", "manual");

    /**
     * The type of the approval, either {@code 'manual'} or {@code 'success'}
     */
    @JsonProperty("type")
    @NotEmpty
    @OneOfStrings(value = {"success", "manual"})
    private String type = "success";

    /**
     * Configuration to only allow stage to be scheduled if the previous stage run is successful.
     */
    @Getter(value = NONE)
    @Setter(value = NONE)
    @JsonProperty("allow_only_on_success")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean allowOnlyOnSuccess = false;

    /**
     * The list of roles that are authorized to trigger
     */
    @JsonProperty("roles")
    private List<String> roles = new ArrayList<>();

    /**
     * The list of users that are authorized to trigger
     */
    @JsonProperty("users")
    private List<String> users = new ArrayList<>();

}
