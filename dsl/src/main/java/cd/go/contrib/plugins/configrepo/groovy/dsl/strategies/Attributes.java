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

package cd.go.contrib.plugins.configrepo.groovy.dsl.strategies;

import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.KeyVal;

import static org.apache.commons.lang3.StringUtils.isAllBlank;

public abstract class Attributes implements KeyVal.Mixin {

    public enum Type {
        git, github, gitlab, bitbucketcloud, bitbucketserver
    }

    public abstract Type type();

    /**
     * OPTIONAL: User can override the URL, with say, ssh or a proxy
     * <p>
     * Assumes material accepts URL (since we are only supporting Git at the moment, this
     * is fine; if we later expand to other SCMs, P4 is the only oddball.)
     */
    public String materialUrl;

    /**
     * OPTIONAL: Username for material auth
     */
    public String materialUsername;

    /**
     * OPTIONAL: Username for material password
     */
    public String materialPassword;

    public boolean credentialsGiven() {
        return !isAllBlank(materialUsername, materialPassword);
    }
}
