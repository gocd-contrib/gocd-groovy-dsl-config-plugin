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

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
abstract class CollectionNode<T extends Node> extends Node<T> {

    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private final List<T> elements = new ArrayList<>();

    public T create(Supplier<T> callable) {
        T newObj = callable.get();
        add(newObj);
        return newObj;
    }

    public void add(T newObj) {
        elements.add(newObj);
    }

    public void leftShift(T newObj) {
        add(newObj);
    }

    public void forEach(Consumer<T> tConsumer) {
        elements.forEach(tConsumer);
    }

    public List<String> getNames() {
        return elements.stream().filter(t -> t instanceof NamedNode).map(t -> ((NamedNode) t).getName()).collect(Collectors.toList());
    }

    int size() {
        return elements.size();
    }
}
