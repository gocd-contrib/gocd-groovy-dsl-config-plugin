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

package cd.go.contrib.plugins.configrepo.groovy.executors;

import cd.go.contrib.plugins.configrepo.groovy.PluginRequest;
import cd.go.contrib.plugins.configrepo.groovy.RequestExecutor;
import cd.go.contrib.plugins.configrepo.groovy.dsl.json.GoCDJsonSerializer;
import cd.go.contrib.plugins.configrepo.groovy.meta.NotifyPayload;
import cd.go.contrib.plugins.configrepo.groovy.requests.StageNotificationsRequest;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Collections;
import java.util.function.Consumer;

import static java.lang.String.format;

public class StageStatusExecutor implements RequestExecutor {

    private static final Logger LOG = Logger.getLoggerFor(StageStatusExecutor.class);

    private final PluginRequest pluginRequest;

    private final StageNotificationsRequest stageRequest;

    private final Consumer<NotifyPayload> notifier;

    public StageStatusExecutor(PluginRequest pluginRequest, StageNotificationsRequest stageRequest, Consumer<NotifyPayload> notifier) {
        this.pluginRequest = pluginRequest;
        this.stageRequest = stageRequest;
        this.notifier = notifier;
    }

    @Override
    public GoPluginApiResponse execute() throws Throwable {
        final String label = format("%s/%s", stageRequest.pipelineName(), stageRequest.stageName());
        final String status = stageRequest.state();
        final String url = stageRequest.stageUrl(pluginRequest.getPluginSettings().serverBaseUrl());

        LOG.debug("Received stage status event for {}", label);

        stageRequest.buildCausesOfType("git").forEach(c -> {
            LOG.debug("  > Looking at {} build cause [key: {}] @ revision {}", c.type(), c.key(), c.revision());

            try {
                this.notifier.accept(new NotifyPayload(c.key(), c.revision(), label, status, url));
            } catch (Throwable e) {
                LOG.error(format("Failed to notify event [%s] on commit [%s] with label [%s]", status, c.revision(), label), e);
                sneakyThrow(e);
            }
        });

        return DefaultGoPluginApiResponse.success(GoCDJsonSerializer.mapper().writeValueAsString(Collections.singletonMap("status", "success")));
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
        throw (E) e;
    }
}
