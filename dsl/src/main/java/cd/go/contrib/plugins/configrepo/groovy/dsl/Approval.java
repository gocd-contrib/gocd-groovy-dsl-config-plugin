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
public class Approval extends Node<Approval> {

    private static final List<String> APPROVAL_TYPES = Arrays.asList("success", "manual");

    /**
     * The type of the approval, either {@code 'manual'} or {@code 'success'}
     */
    @Setter(AccessLevel.NONE)
    @Expose
    @SerializedName("type")
    private String type = "success";

    /**
     * The list of roles that are authorized to trigger
     */
    @Expose
    @SerializedName("roles")
    private List<String> roles;

    /**
     * The list of users that are authorized to trigger
     */
    @Expose
    @SerializedName("users")
    private List<String> users;

    public void setType(String newValue) {
        if (APPROVAL_TYPES.contains(newValue.toLowerCase())) {
            this.type = newValue.toLowerCase();
        } else {
            throw new IllegalArgumentException("Illegal value for approval type: " + newValue);
        }
    }

}
