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

package cd.go.contrib.plugins.configrepo.groovy.dsl;

import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.Configurable;
import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.KeyVal;
import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.UtilsMixin;
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Attributes.*;
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.BranchStrategy;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.util.regex.Pattern;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.Configurable.redelegate;
import static cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.Configurable.redelegateAndCall;
import static groovy.lang.Closure.DELEGATE_ONLY;
import static lombok.AccessLevel.NONE;

@Getter
@Setter
public class BranchMatcher implements Configurable, KeyVal.Mixin, UtilsMixin {

    @NotNull(message = "`matching {}` block requires `pattern` (regexp), set with `pattern = ~/(your|regexp|here)?.+/`")
    private Pattern pattern = Pattern.compile(".+");

    @NotNull(message = "`matching {}` block requires `from = <branching provider>` to be set before the `onMatch {}` " +
            "block. Available providers: [git {}, github {}, gitlab {}, bitbucket {}, bitbucketSelfHosted {}]")
    private BranchStrategy from;

    @Getter(value = NONE)
    @Setter(value = NONE)
    @NotNull
    private Pipelines pipelines;

    public BranchMatcher(@NotNull Pipelines pipelines) {
        this.pipelines = pipelines;
    }

    public BranchStrategy git(@DelegatesTo(value = GitBranch.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Attributes.GitBranch") Closure<?> cl) {
        return new BranchStrategy(redelegateAndCall(cl, new GitBranch()));
    }

    public BranchStrategy github(@DelegatesTo(value = GitHubPR.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Attributes.GithubPR") Closure<?> cl) {
        return new BranchStrategy(redelegateAndCall(cl, new GitHubPR()));
    }

    public BranchStrategy gitlab(@DelegatesTo(value = GitLabMR.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Attributes.GitLabMR") Closure<?> cl) {
        return new BranchStrategy(redelegateAndCall(cl, new GitLabMR()));
    }

    public BranchStrategy bitbucket(@DelegatesTo(value = BitbucketPR.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Attributes.BitbucketPR") Closure<?> cl) {
        return new BranchStrategy(redelegateAndCall(cl, new BitbucketPR()));
    }

    public BranchStrategy bitbucketSelfHosted(@DelegatesTo(value = BitbucketSelfHostedPR.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Attributes.BitbucketSelfHostedPR") Closure<?> cl) {
        return new BranchStrategy(redelegateAndCall(cl, new BitbucketSelfHostedPR()));
    }

    public void onMatch(@DelegatesTo(value = Pipelines.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.BranchContext") Closure<?> cl) {
        final Closure<?> lambda = redelegate(cl, pipelines);
        from.fetch(pattern).forEach(lambda::call);
    }
}
