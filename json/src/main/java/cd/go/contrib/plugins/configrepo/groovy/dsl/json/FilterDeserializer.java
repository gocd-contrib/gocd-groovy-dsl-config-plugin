/*
 * Copyright 2022 Thoughtworks, Inc.
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

package cd.go.contrib.plugins.configrepo.groovy.dsl.json;

import cd.go.contrib.plugins.configrepo.groovy.dsl.Filter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FilterDeserializer extends StdDeserializer<Filter> {

    protected FilterDeserializer() {
        super(Filter.class);
    }

    @Override
    public Filter deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        TreeNode node = p.getCodec().readTree(p);
        if (node instanceof ObjectNode objectNode) {
            boolean isWhitelist;
            ArrayNode itemsAsArray;

            if (objectNode.has("ignore")) {
                isWhitelist = false;
                itemsAsArray = (ArrayNode) objectNode.get("ignore");
            } else if (objectNode.has("includes")) {
                isWhitelist = true;
                itemsAsArray = (ArrayNode) objectNode.get("includes");
            } else {
                throw new UnrecognizedPropertyException(p, "Filter contains neither a whitelist nor an ignore", p.currentLocation(), getClass(), "filter", null);
            }

            if (itemsAsArray == null || itemsAsArray.isEmpty()) {
                return null;
            }
            List<String> items = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(itemsAsArray.elements(), Spliterator.ORDERED), false)
                    .map(JsonNode::asText)
                    .collect(Collectors.toList());

            return new Filter(isWhitelist, items);
        }
        return null;
    }
}
