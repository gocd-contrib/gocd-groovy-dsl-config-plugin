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

package cd.go.contrib.plugins.configrepo.groovy.dsl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link Filter} element specifies files in changesets that should not trigger a pipeline automatically. When a
 * pipeline is triggered by files that are not ignored the filtered files will still be updated with other files. You
 * can only define one filter under each SCM material. When you trigger a pipeline manually, it will update to most
 * recent revision, including filtered files.
 * <p>
 * {@includeCode scm.filter.groovy }
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString(callSuper = true)
public class Filter {

    private final boolean isWhitelist;

    @NotEmpty
    private final List<String> items;

    @SuppressWarnings("unused" /*method here for deserialization only*/)
    protected Filter() {
        this(null);
    }

    public Filter(List<String> items) {
        this(false, items);
    }

    public Filter(boolean isWhitelist, List<String> items) {
        this.isWhitelist = isWhitelist;
        this.items = items;
    }

    public Filter deepClone() {
        return new Filter(isWhitelist, new ArrayList<>(items));
    }
}
