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

package cd.go.contrib.plugins.configrepo.groovy.branching.api.bitbucketserver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Envelope<T> {

    @JsonProperty
    @SuppressWarnings("unused")
    private boolean isLastPage;

    @JsonProperty
    @SuppressWarnings("unused")
    private int nextPageStart;

    /** the main payload/result returned by the response */
    @JsonProperty
    public List<T> values;

    /** @return whether or not there is a remaining page of results */
    public boolean hasNext() {
        return !isLastPage;
    }

    /**
     * Yeah, this is kind of cheap as we aren't inspecting the `page` query param from the
     * `next` link, which would be authoritative. Nevertheless, we are instead assuming
     * `current page + 1` because in any real-life situation, it ought to be reliable, and
     * much easier to compute vs parsing a query param value + parseInt() + error handling.
     *
     * @return the page number for the next page of results.
     */
    public int nextPage() {
        return nextPageStart;
    }
}
