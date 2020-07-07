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

import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Basic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.util.UriUtils.embedAuth;
import static cd.go.contrib.plugins.configrepo.groovy.dsl.util.UriUtils.maskAuth;
import static cd.go.contrib.plugins.configrepo.groovy.utils.CommandUtils.*;
import static java.lang.String.format;

public class BasicGitRefProvider implements RefProvider {

    private static final Pattern REF_SPLIT = Pattern.compile("^([0-9a-fA-F]+)\\s+(refs/.+)$");

    private final String authedUrl;

    public BasicGitRefProvider(String authedUrl) {
        this.authedUrl = authedUrl;
    }

    public static BasicGitRefProvider create(Basic.Git s) {
        String fetchUrl = embedAuth(s.url, s.materialUsername, s.materialPassword);
        return new BasicGitRefProvider(fetchUrl);
    }

    @Override
    public List<GitRef> fetch() {
        final Process git;
        final String safeUrl = maskAuth(authedUrl);

        try {
            git = exec("git", "ls-remote", authedUrl);
        } catch (IOException e) {
            throw failed(format("Failed to run `git ls-remote %s`", safeUrl), e);
        }

        final OutputStream err = new ByteArrayOutputStream();
        final List<GitRef> refs = new ArrayList<>();

        runReadingLines(git, line -> {
            final Matcher matcher = REF_SPLIT.matcher(line);
            if (matcher.find()) {
                refs.add(new GitRef(matcher.group(2), authedUrl));
            }
        }, err);

        final int code = git.exitValue();

        if (code != 0) {
            throw failed(
                    format("Non-zero exit [%d] from `git ls-remote %s`; STDERR: %s", code, safeUrl, err)
            );
        }

        return refs;
    }

    public static class GitRef implements MergeParent {

        private final String ref;

        private final String url;

        public GitRef(String ref, String url) {
            this.ref = ref;
            this.url = url;
        }

        @Override
        public String ref() {
            return ref;
        }

        @Override
        public String url() {
            return url;
        }
    }
}
