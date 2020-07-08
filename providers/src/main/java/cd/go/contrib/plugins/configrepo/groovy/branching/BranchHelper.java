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

package cd.go.contrib.plugins.configrepo.groovy.branching;

import cd.go.contrib.plugins.configrepo.groovy.dsl.BranchContext;
import cd.go.contrib.plugins.configrepo.groovy.dsl.GitMaterial;
import cd.go.contrib.plugins.configrepo.groovy.dsl.ScmMaterial;
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.*;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.util.RefUtils.gitShortRef;
import static cd.go.contrib.plugins.configrepo.groovy.dsl.util.UriUtils.stripAuth;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.firstNonBlank;

public class BranchHelper {

    private BranchHelper() {
    }

    public static RefProvider createProvider(BranchStrategy s) {
        switch (s.type()) {
            case git:
                return BasicGitRefProvider.create((Basic.Git) s.attrs());
            case github:
                return GithubRefProvider.create((Github) s.attrs());
            case gitlab:
                return GitlabRefProvider.create((Gitlab) s.attrs());
            case bitbucketcloud:
                return BitBucketCloudRefProvider.create((BitBucketCloud) s.attrs());
            case bitbucketserver:
                return BitBucketServerRefProvider.create((BitBucketServer) s.attrs());
            default:
                throw new IllegalArgumentException("Unsupported branch matching type: " + s.type());
        }
    }

    public static BranchContext createContext(Attributes attrs, MergeParent merge) {
        final String ref = merge.ref();
        final String branch = prettifyRef(gitShortRef(ref), attrs);
        return new BranchContext(ref, branch, createMaterial(attrs, merge));
    }

    /**
     * Creates a usable material from a {@link MergeParent} (e.g., a pull request) and configured {@link Attributes}
     * block.
     * <p>
     * TODO: Support other SCMs besides git once we add basic providers for SVN, Hg, P4, and TFS
     *
     * @param attrs
     *         the configured attributes block
     * @param merge
     *         the merge parent object which provides the clone URL and the full ref name
     *
     * @return an {@link ScmMaterial} instance representing the given {@link MergeParent}
     */
    public static ScmMaterial createMaterial(final Attributes attrs, final MergeParent merge) {
        // TODO: expand support for other SCMs beyond git
        return new GitMaterial("repo", git -> {
            String url = firstNonBlank(attrs.materialUrl, merge.url());

            if (attrs.credentialsGiven()) {
                url = stripAuth(url);
                git.setUsername(attrs.materialUsername);
                git.setPassword(attrs.materialPassword);
            }

            final String ref = merge.ref();
            git.setUrl(url);
            git.setBranch(format("%s:refs/remotes/origin/%s", ref, gitShortRef(ref)));
            git.setShallowClone(true);
            git.setAutoUpdate(true);
        });
    }

    public static Entry<String, String> parse(String fullName) {
        final int slash = fullName.indexOf("/");

        if (slash <= 0 || slash == fullName.length() - 1) {
            throw new IllegalArgumentException(format("Full repo name must be in \"{namespace}/{repo}\" format! Actual: `%s`", fullName));
        }

        return new SimpleImmutableEntry<>(fullName.substring(0, slash), fullName.substring(slash + 1));
    }

    private static String prettifyRef(String shortRef, Attributes attrs) {
        switch (attrs.type()) {
            case github:
            case gitlab:
                if (shortRef.endsWith("/head")) {
                    return shortRef.substring(0, shortRef.length() - "/head".length());
                }
        }
        return shortRef;
    }
}
