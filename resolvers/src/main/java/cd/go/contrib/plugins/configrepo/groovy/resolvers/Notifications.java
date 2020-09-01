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

package cd.go.contrib.plugins.configrepo.groovy.resolvers;

import cd.go.contrib.plugins.configrepo.groovy.api.Client;
import cd.go.contrib.plugins.configrepo.groovy.api.bitbucketcloud.CommitStatusBitbucket;
import cd.go.contrib.plugins.configrepo.groovy.api.bitbucketserver.CommitStatusBitbucketSelfHosted;
import cd.go.contrib.plugins.configrepo.groovy.api.github.CommitStatusGitHub;
import cd.go.contrib.plugins.configrepo.groovy.api.gitlab.CommitStatusGitLab;
import cd.go.contrib.plugins.configrepo.groovy.dsl.GitMaterial;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.*;
import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.ThrowingRunnable;
import cd.go.contrib.plugins.configrepo.groovy.exceptions.NotificationFailure;
import cd.go.contrib.plugins.configrepo.groovy.meta.NotifyPayload;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static java.lang.String.format;

public class Notifications {

    /**
     * While unlikely, it may be possible for more than one thread to concurrently read/write to this if 2 (or more)
     * distinct config repos configure notifications on the same material.
     */
    private static final Map<String, Set<ConnectionConfig>> registrar = new ConcurrentHashMap<>();

    private static final ThreadLocal<Consumer<NotifyPayload>> emitter = ThreadLocal.withInitial(() -> n -> {
    });

    public static void realConfig(GitMaterial git, ConnectionConfig spec) {
        final String key = keyFor(git);

        if (!registrar.containsKey(key)) {
            registrar.put(key, new ConnectionConfigSet());
        }
        registrar.get(key).add(spec);
    }

    public static void with(Consumer<NotifyPayload> fn, ThrowingRunnable body) throws Throwable {
        emitter.set(fn);
        try {
            body.run();
        } finally {
            emitter.remove();
        }
    }

    public static void realEmit(final NotifyPayload p) {
        if (registrar.containsKey(p.key())) {
            registrar.get(p.key()).forEach(cfg -> {
                try {
                    publish(cfg, p.revision(), p.label(), p.status(), p.url());
                } catch (IOException e) {
                    final String message = format("Failed to publish to endpoint [%s] with payload: %s", cfg.identifier(), p.toString());
                    throw new NotificationFailure(message, e);
                }
            });
        }
    }

    /**
     * Generates a key that is comparable to a BuildCause#key() (and thus must mimic the format). This value will be
     * used to determine if a build cause matches a registered notifier.
     *
     * @param git
     *         a {@link GitMaterial}
     *
     * @return a {@link String} key representing a potential BuildCause match.
     */
    private static String keyFor(GitMaterial git) {
        return format("git|%s|%s", git.getUrl(), git.getBranch());
    }

    private static void publish(ConnectionConfig config, String commit, String build, String status, String url) throws IOException {
        switch (config.type()) {
            case GitHub:
                final GitHub github = (GitHub) config;
                Client.get(github).publishCommitStatus(github.fullRepoName, commit, github(status, build, url));
                break;
            case GitLab:
                final GitLab gitlab = (GitLab) config;
                Client.get(gitlab).publishCommitStatus(gitlab.fullRepoName, commit, gitlab(status, build, url));
                break;
            case Bitbucket:
                final Bitbucket bitbucket = (Bitbucket) config;
                Client.get(bitbucket).publishCommitStatus(bitbucket.fullRepoName, commit, bitbucket(status, build, url));
                break;
            case BitbucketSelfHosted:
                final BitbucketSelfHosted bitbucketSelfHosted = (BitbucketSelfHosted) config;
                Client.get(bitbucketSelfHosted).publishCommitStatus(commit, bitbucketSelfHosted(status, build, url));
                break;
            default:
                // should never get here
                throw new IllegalArgumentException(format("Don't know how to handle notification type %s", config.type()));
        }
    }

    private static CommitStatusGitHub github(String status, String label, String url) {
        return new CommitStatusGitHub(status, label, url);
    }

    private static CommitStatusGitLab gitlab(String status, String label, String url) {
        return new CommitStatusGitLab(status, label, url);
    }

    private static CommitStatusBitbucket bitbucket(String status, String label, String url) {
        return new CommitStatusBitbucket(status, label, url);
    }

    private static CommitStatusBitbucketSelfHosted bitbucketSelfHosted(String status, String label, String url) {
        return new CommitStatusBitbucketSelfHosted(status, label, url);
    }

    private Notifications() {
    }
}
