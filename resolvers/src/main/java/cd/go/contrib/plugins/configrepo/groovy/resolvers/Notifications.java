/*
 * Copyright 2021 ThoughtWorks, Inc.
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
import com.thoughtworks.go.plugin.api.logging.Logger;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.validate.Validator.validate;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class Notifications {

    private static final Logger LOG = Logger.getLoggerFor(Notifications.class);

    /** Maps a namespace to a Map of materials to notification configurations. */
    private static final Map<String, Map<String, Set<ConnectionConfig>>> registrar = new ConcurrentHashMap<>();

    private static final ThreadLocal<Consumer<NotifyPayload>> emitter = ThreadLocal.withInitial(() -> n -> {
    });

    public static BiConsumer<GitMaterial, ConnectionConfig> realConfig(final String namespace) {
        // Because we do not have a means to detect the removal of notification configurations during parseDirectory(),
        // we clear and reinitialize the namespaced storage that holds the notification configs under the given
        // namespace each time we parse. The namespace limits this purge to notifiers created through the  parsing
        // of this config repo material.
        registrar.put(namespace, new ConcurrentHashMap<>()); // clear and reinitialize namespace storage
        final Map<String, Set<ConnectionConfig>> registered = registrar.get(namespace);

        return (git, spec) -> {
            validate(spec, invalidNotifyConfig(git, spec));
            final String key = keyFor(git);

            LOG.debug("Registering material [{}] to notify {}", key, spec.identifier());

            if (!registered.containsKey(key)) {
                registered.put(key, new ConnectionConfigSet());
            }
            registered.get(key).add(spec);
        };
    }

    public static void validatingNoOpConfig(GitMaterial git, ConnectionConfig spec) {
        validate(spec, invalidNotifyConfig(git, spec));
    }

    public static void with(Consumer<NotifyPayload> fn, ThrowingRunnable body) throws Throwable {
        emitter.set(fn);
        try {
            body.run();
        } finally {
            emitter.remove();
        }
    }

    /**
     * Performs the actual notification to git repository providers.
     *
     * @param p
     *         the {@link NotifyPayload} data object containing the relevant information about the stage build event
     */
    public static void realEmit(final NotifyPayload p) {
        notifiersMatchingKey(p.key()).forEach(cfg -> {
            try {
                LOG.debug("Notifying {} on {}", cfg.identifier(), p);
                publish(cfg, p.revision(), p.label(), p.status(), p.url());
            } catch (IOException e) {
                final String message = format("Failed to publish to endpoint [%s] with payload: %s", cfg.identifier(), p.toString());
                throw new NotificationFailure(message, e);
            }
        });
    }

    /**
     * Collects all notifiers from all namespaces that match the material build cause key.
     * <p>
     * Yes, not the most efficient as we end up looping again over the result in {@link #realEmit(NotifyPayload)},
     * but this is easier to read and reason about as compared to handling everything in a single
     * iteration.
     * <p>
     * I can't see this trade-off becoming the major bottleneck anywhere, but I hope I don't eat my words.
     *
     * @param key
     *         the build cause key from a stage notification event; generally represents a material
     *
     * @return a {@link ConnectionConfigSet} of matching notifiers
     */
    private static ConnectionConfigSet notifiersMatchingKey(final String key) {
        LOG.debug("Finding notifiers matching build cause [{}] in all registration partitions", key);

        return registrar.keySet().stream().reduce(new ConnectionConfigSet(), (memo, ns) -> {
            Map<String, Set<ConnectionConfig>> map = registrar.get(ns);
            if (map.containsKey(key)) {
                LOG.debug("  > Located match in namespace partition: {}", ns);
                memo.addAll(map.get(key));
            } else {
                LOG.debug("  > No match found in namespace: {}", ns);
            }
            return memo;
        }, (a, b) -> {
            a.addAll(b);
            return a;
        });
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

    private static Consumer<Set<ConstraintViolation<ConnectionConfig>>> invalidNotifyConfig(final GitMaterial git, final ConnectionConfig spec) {
        final String material = format("GitMaterial{url=%s; branch=%s}", git.getUrl(), git.getBranch());
        return (errors) -> {
            throw new ValidationException(
                    format(
                            "Invalid notification config block `%s {}` on material %s; please address the following:\n%s",
                            spec.type(),
                            material,
                            errors.stream().map(ConstraintViolation::getMessage).collect(joining(";\n"))
                    )
            );
        };
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
