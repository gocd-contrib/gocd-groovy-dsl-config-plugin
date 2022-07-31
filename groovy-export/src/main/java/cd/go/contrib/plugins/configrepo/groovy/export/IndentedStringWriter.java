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

package cd.go.contrib.plugins.configrepo.groovy.export;

import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.ThrowingRunnable;
import org.codehaus.groovy.tools.Utilities;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;

public class IndentedStringWriter {

    private final Writer writer;

    private int indent = 0;

    public IndentedStringWriter(Writer writer) {
        this.writer = writer;
    }

    public void newEntity(String methodName, ThrowingRunnable consumer) throws Throwable {
        newEntity(methodName, null, consumer);
    }

    public void newEntity(String methodName, String entityName, ThrowingRunnable consumer) throws Throwable {
        String invocationWithMethodName = isBlank(entityName) ? "" : "('" + escapeJava(entityName) + "')";
        println(methodName + invocationWithMethodName + " {");
        withIndent(consumer);
        println("}");
    }

    public void simpleField(String methodName, ThrowingRunnable consumer) throws Throwable {
        println(methodName + " {");
        withIndent(consumer);
        println("}");
    }

    public void property(String name, Pattern propertyValue) throws IOException {
        if (propertyValue != null) {
            println(name + " = " + "~/" + propertyValue + "/");
        }
    }

    public void property(String name, String value) throws IOException {
        if (isNotBlank(value)) {
            println(name + " = " + quoteValue(value));
        }
    }

    public void property(String name, Boolean propertyValue) throws IOException {
        if (propertyValue != null) {
            println(name + " = " + propertyValue);
        }
    }

    public void property(String name, Number value) throws IOException {
        if (value != null) {
            println(name + " = " + value);
        }
    }

    public void property(String name, List<String> propertyValue) throws IOException {
        if (propertyValue != null && propertyValue.size() > 0) {
            String commaSeparatedValues = propertyValue.stream().map(this::quoteValue).collect(Collectors.joining(", "));
            println(MessageFormat.format("{0} = [{1}]", name, commaSeparatedValues));
        }
    }

    public void property(String name, Map<String, String> propertyValue) throws Throwable {
        if (propertyValue != null && propertyValue.size() > 0) {
            println(name + " = " + "[");
            withIndent(() -> {
                TreeMap<String, String> formattedProperties = new TreeMap<>();

                propertyValue.forEach((k, v) -> {
                    if (Utilities.isJavaIdentifier(k)) {
                        formattedProperties.put(k, quoteValue(v));
                    } else {
                        formattedProperties.put(quoteValue(k), quoteValue(v));
                    }
                });

                String longestVariableName = formattedProperties.keySet().stream().max(Comparator.comparingInt(String::length)).get();

                formattedProperties.forEach((k, v) -> {
                    try {
                        println(rightPad(k, longestVariableName.length()) + ": " + v + ",");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
            println("]");
        }
    }

    private String quoteValue(String value) {
        if (value == null) {
            return "''";
        }
        return "'" + escapeJava(value).replace("'", "\\'") + "'";
    }

    private void withIndent(ThrowingRunnable consumer) throws Throwable {
        indent++;
        try {
            consumer.run();
        } finally {
            indent--;
        }
    }

    private void println(String line) throws IOException {
        writer.append(repeat("  ", indent));
        writer.append(line);
        writer.append("\n");
    }

}
