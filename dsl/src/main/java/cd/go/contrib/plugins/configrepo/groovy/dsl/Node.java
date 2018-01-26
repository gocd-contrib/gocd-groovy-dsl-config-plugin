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

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import groovy.lang.Closure;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

@Getter
@Setter
@EqualsAndHashCode
abstract class Node<T extends Node> {

    protected static Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(new TypeToken<CollectionNode<? extends Node>>(){}.getRawType(), (JsonSerializer<CollectionNode<? extends Node>>) (src, typeOfSrc, context) -> {
                if (src.size() > 0) {
                    JsonArray jsonArray = new JsonArray(src.size());
                    src.forEach(o -> jsonArray.add(o.toJson()));
                    return jsonArray;
                }
                return JsonNull.INSTANCE;
            })
            .registerTypeAdapter(Filter.class, (JsonSerializer<Filter>) (src, typeOfSrc, context) -> {
                JsonObject jsonObject = new JsonObject();
                JsonElement itemsInFilter = context.serialize(src.getItems());
                if (src.isWhitelist()) {
                    jsonObject.add("whitelist", itemsInFilter);
                } else {
                    jsonObject.add("ignore", itemsInFilter);
                }
                return jsonObject;
            })
            .registerTypeAdapter(Pattern.class, (JsonSerializer<Pattern>) (src, typeOfSrc, context) -> {
                if (src == null) {
                    return context.serialize(null);
                } else {
                    return context.serialize(src.pattern());
                }
            })
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public JsonElement toJson() {
        return GSON.toJsonTree(this);
    }

    public String toJsonString() {
        return GSON.toJson(toJson());
    }

    protected void configure(Closure cl) {
        if (cl != null) {
            Closure rehydrate = cl.rehydrate(this, this, this);
            rehydrate.setResolveStrategy(Closure.DELEGATE_ONLY);
            rehydrate.call(this);
        }
    }
}
