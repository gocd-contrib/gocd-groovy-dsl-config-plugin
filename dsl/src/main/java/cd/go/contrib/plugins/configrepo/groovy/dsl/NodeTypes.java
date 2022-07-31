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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NodeTypes {

    public static Set<Class<? extends Node>> ALL_KNOWN_NODE_TYPES = Collections.unmodifiableSet(
            new HashSet<>(
                    Arrays.asList(
                            cd.go.contrib.plugins.configrepo.groovy.dsl.AbstractArtifact.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.AbstractBuiltInArtifact.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.AbstractFetchArtifactTask.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Approval.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Artifacts.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.BuildArtifact.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.CollectionNode.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.ConfigRepoMaterial.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Configuration.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.DependencyMaterial.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Environment.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Environments.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.ExecTask.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.FetchArtifactTask.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.FetchExternalArtifactTask.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.GitHubPRMaterial.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.GitMaterial.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.HasEnvironmentVariables.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.HgMaterial.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Job.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Jobs.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Material.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Materials.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.NamedNode.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.P4Material.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.PackageMaterial.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Pipeline.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Pipelines.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.PluggableMaterial.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.PluginArtifact.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.PluginTask.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Properties.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Property.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.ScmMaterial.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.ShellTask.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Stage.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Stages.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.SvnMaterial.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Tab.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Tabs.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Task.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Tasks.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.TestArtifact.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.TfsMaterial.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.Timer.class,
                            cd.go.contrib.plugins.configrepo.groovy.dsl.TrackingTool.class
                    ))
    );
}
