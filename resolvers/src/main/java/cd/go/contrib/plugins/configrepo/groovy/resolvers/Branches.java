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

import cd.go.contrib.plugins.configrepo.groovy.branching.MergeCandidate;
import cd.go.contrib.plugins.configrepo.groovy.dsl.BranchContext;
import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.ConnectionConfig;
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.Attributes;
import cd.go.contrib.plugins.configrepo.groovy.dsl.strategies.BranchStrategy;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cd.go.contrib.plugins.configrepo.groovy.branching.BranchHelper.createContext;
import static cd.go.contrib.plugins.configrepo.groovy.branching.BranchHelper.createProvider;
import static cd.go.contrib.plugins.configrepo.groovy.dsl.validate.Validator.validate;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class Branches {

    private static final SecureRandom RAND = new SecureRandom();

    private static final int START = 0x1000000;

    private static final int END = 0xFFFFFFF;

    /**
     * Generates {@link BranchContext} instances from the results of fetching branch references from an SCM
     * repo or provider. The results are filtered via regex against the branch reference names.
     *
     * @param strategy
     *         the configured {@link BranchStrategy} (i.e., the `from =` value under the `matching {}` block in the
     *         config script)
     * @param pattern
     *         the {@link Pattern} to match fetched refs
     *
     * @return a {@link List} of {@link BranchContext} instances
     */
    public static List<BranchContext> real(final BranchStrategy strategy, final Pattern pattern) {
        validate(strategy.attrs(), invalidBranchStrategy(strategy));

        return createProvider(strategy).fetch().stream().
                filter(pr -> pattern.matcher(pr.ref()).matches()).
                map(pr -> createContext(strategy.attrs(), pr)).
                collect(Collectors.toList());
    }

    /**
     * Returns dummy values for {@link BranchContext}
     *
     * @param s
     *         the configured {@link BranchStrategy}
     * @param p
     *         the {@link Pattern} to match fetched refs; this is not used in this implementation
     *
     * @return a {@link List} of {@link BranchContext} instances
     */
    public static List<BranchContext> stubbed(final BranchStrategy s, @SuppressWarnings("unused") final Pattern p) {
        validate(s.attrs(), invalidBranchStrategy(s));

        final String identifier = randomHex();
        final String ref = "refs/heads/stubbed-ref-" + identifier;

        return Collections.singletonList(createContext(s.attrs(), new MergeCandidate() {
            @Override
            public String identifier() {
                return "stubbed-pr-number-" + identifier;
            }

            @Override
            public String title() {
                return "stubbed-title-for-" + identifier;
            }

            @Override
            public String author() {
                return "stubbed-author";
            }

            @Override
            public String showUrl() {
                return "https://stubbed.repository.url/pulls/" + ref;
            }

            @Override
            public List<String> labels() {
                return Collections.emptyList();
            }

            @Override
            public String ref() {
                return ref;
            }

            @Override
            public String url() {
                return "https://stubbed.repository.url";
            }
        }));
    }

    private static Consumer<Set<ConstraintViolation<Attributes<? extends ConnectionConfig>>>> invalidBranchStrategy(final BranchStrategy strategy) {
        return (errors) -> {
            throw new ValidationException(format(
                    "Invalid branch matching config block `%s {}`; please address the following:\n%s",
                    strategy.type(),
                    errors.stream().map(ConstraintViolation::getMessage).collect(joining(";\n"))));
        };
    }

    /** @return a random hexadecimal string */
    private static String randomHex() {
        return Integer.toHexString(RAND.nextInt(END - START) + START);
    }
}
