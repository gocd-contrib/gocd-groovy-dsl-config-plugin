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

import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.Configurable;
import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.KeyVal;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.function.Supplier;

import static groovy.lang.Closure.DELEGATE_ONLY;
import static lombok.AccessLevel.NONE;

public class BranchedWorkflows extends ArrayList<BranchMatcher> implements Configurable, KeyVal.Mixin {

    @Getter(value = NONE)
    @Setter(value = NONE)
    @NotNull
    private final Pipelines pipelines;

    public BranchedWorkflows(@NotNull Pipelines pipelines) {
        this.pipelines = pipelines;
    }

    public BranchMatcher matching(@DelegatesTo(value = BranchMatcher.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.BranchMatcher") Closure cl) {
        BranchMatcher matcher = create(() -> new BranchMatcher(pipelines));
        matcher.configure(cl);
        return matcher;
    }

    private BranchMatcher create(Supplier<BranchMatcher> s) {
        final BranchMatcher el = s.get();
        add(el);
        return el;
    }
}
