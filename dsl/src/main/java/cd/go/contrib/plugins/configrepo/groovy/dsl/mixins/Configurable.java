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

package cd.go.contrib.plugins.configrepo.groovy.dsl.mixins;

import groovy.lang.Closure;

public interface Configurable {

    static <U> U redelegateAndCall(Closure<?> cl, U delegate, Object... args) {
        redelegate(cl, delegate).call(args);
        return delegate;
    }

    static Closure<?> redelegate(Closure<?> cl, Object delegate) {
        if (cl != null) {
            cl = cl.rehydrate(delegate, delegate, delegate);
            cl.setResolveStrategy(Closure.DELEGATE_ONLY);
        }
        return cl;
    }

    static <T> T applyTo(Closure<?> cl, T delegate) {
        if (cl != null) {
            redelegateAndCall(cl, delegate, delegate);
        }
        return delegate;
    }

    default Configurable configure(Closure<?> cl) {
        return applyTo(cl, this);
    }
}
