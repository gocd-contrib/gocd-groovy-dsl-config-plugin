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
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Attributes;
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.BranchStrategy;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.util.TextUtils.gitShortRef;
import static cd.go.contrib.plugins.configrepo.groovy.dsl.util.UriUtils.stripAuth;
import static cd.go.contrib.plugins.configrepo.groovy.dsl.validate.Validator.validate;
import static java.lang.String.format;
import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.StringUtils.firstNonBlank;

public class BranchHelper {

    private BranchHelper() {
    }

    public static RefProvider createProvider(BranchStrategy s) {
        switch (s.type()) {
            case git:
                return BasicGitRefProvider.create((Attributes.GitBranch) s.attrs());
            case GitHub:
                return GitHubRefProvider.create((Attributes.GitHubPR) s.attrs());
            case GitLab:
                return GitLabRefProvider.create((Attributes.GitLabMR) s.attrs());
            case Bitbucket:
                return BitbucketRefProvider.create((Attributes.BitbucketPR) s.attrs());
            case BitbucketSelfHosted:
                return BitbucketSelfHostedRefProvider.create((Attributes.BitbucketSelfHostedPR) s.attrs());
            default:
                throw new IllegalArgumentException("Unsupported branch matching type: " + s.type());
        }
    }

    public static BranchContext createContext(Attributes<?> attrs, MergeCandidate merge) {
        final String ref = merge.ref();
        final String branch = prettifyRef(gitShortRef(ref), attrs);
        final BranchContext bc = new BranchContext(ref, branch, createMaterial(attrs, merge));

        bc.setProvider(attrs.asConnectionConfig());
        bc.setIdentifier(requireNonNullElse(merge.identifier(), ""));
        bc.setTitle(requireNonNullElse(merge.title(), ""));
        bc.setAuthor(requireNonNullElse(merge.author(), ""));
        bc.setReferenceUrl(requireNonNullElse(merge.showUrl(), ""));
        bc.setLabels(new ArrayList<>(requireNonNullElse(merge.labels(), Collections.emptyList())));

        validate(bc, (errors) -> {
            throw new ValidationException("Branch context binding is missing data! " +
                    "Check the provider's API response. Error(s):\n" +
                    errors.stream().
                            map(ConstraintViolation::getMessage).
                            collect(Collectors.joining("\n", "  ", ""))
            );
        });
        return bc;
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
    @SuppressWarnings("rawtypes")
    public static ScmMaterial createMaterial(final Attributes<?> attrs, final MergeParent merge) {
        // TODO: expand support for other SCMs beyond git
        return new GitMaterial("repo", git -> {
            String url = firstNonBlank(attrs.getMaterialUrl(), merge.url());

            if (attrs.materialCredentialsGiven()) {
                url = stripAuth(url);
                git.setUsername(attrs.getMaterialUsername());
                git.setPassword(attrs.getMaterialPassword());
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

    private static String prettifyRef(String shortRef, Attributes<?> attrs) {
        switch (attrs.type()) {
            case GitHub:
            case GitLab:
                if (shortRef.endsWith("/head")) {
                    return shortRef.substring(0, shortRef.length() - "/head".length());
                }
        }
        return shortRef;
    }
}
