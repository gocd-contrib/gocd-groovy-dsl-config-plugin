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

import cd.go.contrib.plugins.configrepo.groovy.dsl.GitMaterial;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.*;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;

import java.util.function.BiConsumer;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.Configurable.redelegateAndCall;
import static groovy.lang.Closure.DELEGATE_ONLY;

public class Notifies {

    private static final BiConsumer<GitMaterial, ConnectionConfig> STUBBED = (m, c) -> System.out.printf("configure material %s to notify: %s%n", m.getUrl(), c.identifier());

    // During a request, the plugin will swap in an appropriate implementation for notification configuration
    private static final ThreadLocal<BiConsumer<GitMaterial, ConnectionConfig>> configurer = ThreadLocal.withInitial(() -> STUBBED);

    public static void with(BiConsumer<GitMaterial, ConnectionConfig> fn, ThrowingRunnable body) throws Throwable {
        configurer.set(fn);
        try {
            body.run();
        } finally {
            configurer.remove();
        }
    }

    public interface MaterialMixin {

        default void notifiesBy(ConnectionConfig provider) {
            if (!(this instanceof GitMaterial)) {
                throw new IllegalArgumentException("notifications are currently only supported on `git` materials");
            }
            configurer.get().accept((GitMaterial) this, provider);

        }

        default void notifiesGitHubAt(
                @DelegatesTo(value = GitHub.class, strategy = DELEGATE_ONLY)
                @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.connection.GitHub")
                        Closure<?> cfg) {
            final GitHub provider = new GitHub();
            redelegateAndCall(cfg, provider);
            notifiesBy(provider);
        }

        default void notifiesGitLabAt(
                @DelegatesTo(value = GitLab.class, strategy = DELEGATE_ONLY)
                @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.connection.GitLab")
                        Closure<?> cfg) {
            final GitLab provider = new GitLab();
            redelegateAndCall(cfg, provider);
            notifiesBy(provider);
        }

        default void notifiesBitbucketAt(
                @DelegatesTo(value = Bitbucket.class, strategy = DELEGATE_ONLY)
                @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.connection.Bitbucket")
                        Closure<?> cfg) {
            final Bitbucket provider = new Bitbucket();
            redelegateAndCall(cfg, provider);
            notifiesBy(provider);
        }

        default void notifiesBitbucketSelfHostedAt(
                @DelegatesTo(value = BitbucketSelfHosted.class, strategy = DELEGATE_ONLY)
                @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.connection.BitbucketSelfHosted")
                        Closure<?> cfg) {
            final BitbucketSelfHosted provider = new BitbucketSelfHosted();
            redelegateAndCall(cfg, provider);
            notifiesBy(provider);
        }
    }
}
