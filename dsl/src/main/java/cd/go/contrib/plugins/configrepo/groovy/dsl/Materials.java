/*
 * Copyright 2018 ThoughtWorks, Inc.
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

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import static groovy.lang.Closure.DELEGATE_ONLY;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Materials extends CollectionNode<Material> {

    /**
     * Creates an svn material which this pipeline polls on.
     * <p>
     * {@includeCode svn.material.groovy}
     */
    public SvnMaterial svn(@DelegatesTo(value = SvnMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.SvnMaterial") Closure cl) {
        return svn(null, cl);
    }

    /**
     * Creates an svn material which this pipeline polls on.
     * <p>
     * {@includeCode svn.material.groovy}
     */
    public SvnMaterial svn(String name, @DelegatesTo(value = SvnMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.SvnMaterial") Closure cl) {
        return (SvnMaterial) create(() -> new SvnMaterial(name, cl));
    }

    /**
     * Creates a mercurial/hg material which this pipeline polls on.
     * <p>
     * {@includeCode hg.material.groovy}
     */
    public HgMaterial hg(@DelegatesTo(value = HgMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.HgMaterial") Closure cl) {
        return hg(null, cl);
    }

    /**
     * Creates a mercurial/hg material which this pipeline polls on.
     * <p>
     * {@includeCode hg.material.groovy}
     */
    public HgMaterial hg(String name, @DelegatesTo(value = HgMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.HgMaterial") Closure cl) {
        return (HgMaterial) create(() -> new HgMaterial(name, cl));
    }

    /**
     * Creates a perforce/hg material which this pipeline polls on.
     * <p>
     * {@includeCode p4.material.groovy}
     */
    public P4Material p4(@DelegatesTo(value = P4Material.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.P4Material") Closure cl) {
        return p4(null, cl);
    }

    /**
     * Creates a perforce/hg material which this pipeline polls on.
     * <p>
     * {@includeCode p4.material.groovy}
     */
    public P4Material p4(String name, @DelegatesTo(value = P4Material.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.P4Material") Closure cl) {
        return (P4Material) create(() -> new P4Material(name, cl));
    }

    /**
     * Creates a git material which this pipeline polls on.
     * {@includeCode git.material.groovy}
     */
    public GitMaterial git(@DelegatesTo(value = GitMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.GitMaterial") Closure cl) {
        return git(null, cl);
    }

    /**
     * Creates a git material which this pipeline polls on.
     * {@includeCode git.material.groovy}
     */
    public GitMaterial git(String name, @DelegatesTo(value = GitMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.GitMaterial") Closure cl) {
        return (GitMaterial) create(() -> new GitMaterial(name, cl));
    }

    /**
     * Creates a tfs material which this pipeline polls on.
     * {@includeCode tfs.material.groovy}
     */
    public TfsMaterial tfs(@DelegatesTo(value = TfsMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.TfsMaterial") Closure cl) {
        return tfs(null, cl);
    }

    /**
     * Creates a tfs material which this pipeline polls on.
     * {@includeCode tfs.material.groovy}
     */
    public TfsMaterial tfs(String name, @DelegatesTo(value = TfsMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.TfsMaterial") Closure cl) {
        return (TfsMaterial) create(() -> new TfsMaterial(name, cl));
    }

    public DependencyMaterial dependency(String name, @DelegatesTo(value = DependencyMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.DependencyMaterial") Closure cl) {
        return (DependencyMaterial) create(() -> new DependencyMaterial(name, cl));
    }

    public ConfigRepoMaterial configRepo(@DelegatesTo(value = ConfigRepoMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ConfigRepoMaterial") Closure cl) {
        return configRepo(null, cl);
    }

    public ConfigRepoMaterial configRepo(String name, @DelegatesTo(value = ConfigRepoMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ConfigRepoMaterial") Closure cl) {
        return (ConfigRepoMaterial) create(() -> new ConfigRepoMaterial(name, cl));
    }

    public PluggableMaterial pluggable(@DelegatesTo(value = PluggableMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.PluggableMaterial") Closure cl) {
        return pluggable(null, cl);
    }

    public PluggableMaterial pluggable(String name, @DelegatesTo(value = PluggableMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.PluggableMaterial") Closure cl) {
        return (PluggableMaterial) create(() -> new PluggableMaterial(name, cl));
    }

    public PackageMaterial pkg(@DelegatesTo(value = PackageMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.PackageMaterial") Closure cl) {
        return pkg(null, cl);
    }

    public PackageMaterial pkg(String name, @DelegatesTo(value = PackageMaterial.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.PackageMaterial") Closure cl) {
        return (PackageMaterial) create(() -> new PackageMaterial(name, cl));
    }
}
