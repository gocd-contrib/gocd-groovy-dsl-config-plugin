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

package cd.go.contrib.plugins.configrepo.groovy.api.github;

import javax.validation.constraints.NotBlank;
import java.util.regex.Pattern;

public class Pagination {

    // <https://api.github.com/repositories/:id/pulls?page=:number>; rel="next", <https://api.github.com/repositories/:id/pulls?page=:number>; rel="last"
    private static final Pattern PAGINATION = Pattern.compile("<https:[^>]+\\?page=(\\d+)>; rel=\"(first|last|next|prev)\"");

    private static final int BASE_10 = 10;

    @NotBlank
    private final String content;

    public Pagination(@NotBlank String content) {
        this.content = content;
    }

    public boolean hasNext() {
        return content.contains("; rel=\"next\"");
    }

    public boolean hasLast() {
        return content.contains("; rel=\"last\"");
    }

    public int next() {
        return get("next");
    }

    public int last() {
        return get("last");
    }

    /**
     * Retrieves the page value by the "rel" label
     *
     * @param which
     *         the desired label
     *
     * @return the matching page value
     */
    private int get(String which) {
        return PAGINATION.matcher(content).results().            // look at all matched segments
                filter(m -> which.equals(m.group(2))).           // match against value of "rel"
                map(m -> Integer.valueOf(m.group(1), BASE_10)).  // parse numeric value of "page"
                findFirst().orElse(-1);                    // return or default to -1
    }
}
