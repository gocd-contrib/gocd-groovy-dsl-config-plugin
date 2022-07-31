/*
 * Copyright 2022 Thoughtworks, Inc.
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

package cd.go.contrib.plugins.configrepo.groovy.dsl.connection;

import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.KeyVal;
import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.UtilsMixin;

public interface ConnectionConfig extends KeyVal.Mixin, UtilsMixin {

    Type type();

    /**
     * An identifier representing this {@link ConnectionConfig}. This should be deterministically calculated from
     * the configured fields <strong>with the exception of authentication-related fields</strong>. Another way to put
     * this is that two {@link ConnectionConfig} instances should have equal identifiers if all non-authentication
     * connection details are the same. Identifiers should <strong>not be equal</strong> if any non-authentication
     * connection details are different.
     *
     * @return a {@link String} identifier.
     */
    String identifier();
}
