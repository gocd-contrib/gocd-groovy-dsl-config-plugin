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

import cd.go.contrib.plugins.configrepo.groovy.dsl.connection.ConnectionConfig;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link java.util.Set} of {@link ConnectionConfig} instances where membership identity is based on the
 * {@link ConnectionConfig#identifier()} method, which does not consider authentication-related fields. This lets us
 * retain sensible {@link Object#equals(Object)} and {@link Object#hashCode()} implementations that may consider all
 * fields (useful for debugging, among other things) without worry of breaking Set membership.
 */
public class ConnectionConfigSet extends AbstractSet<ConnectionConfig> {

    /**
     * While unlikely, it may be possible for more than one thread to concurrently read/write to this if 2 (or more)
     * distinct config repos configure notifications on the same material.
     */
    private final transient Map<String, ConnectionConfig> contents = new ConcurrentHashMap<>();

    @Override
    public boolean contains(Object o) {
        if (o instanceof ConnectionConfig) {
            return contents.containsKey(((ConnectionConfig) o).identifier());
        }
        return false;
    }

    @Override
    public boolean add(ConnectionConfig connectionConfig) {
        if (!contains(connectionConfig)) {
            contents.put(connectionConfig.identifier(), connectionConfig);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (!contains(o)) {
            return false;
        }
        contents.remove(((ConnectionConfig) o).identifier());
        return true;
    }

    @Override
    public void clear() {
        contents.clear();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<ConnectionConfig> iterator() {
        return contents.values().iterator();
    }

    @Override
    public int size() {
        return contents.size();
    }
}
