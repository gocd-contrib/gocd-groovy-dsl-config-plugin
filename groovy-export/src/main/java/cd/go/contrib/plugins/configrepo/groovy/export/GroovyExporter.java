/*
 * Copyright 2019 ThoughtWorks, Inc.
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

import cd.go.contrib.plugins.configrepo.groovy.dsl.CollectionNode;
import cd.go.contrib.plugins.configrepo.groovy.dsl.NamedNode;
import cd.go.contrib.plugins.configrepo.groovy.dsl.Node;
import cd.go.contrib.plugins.configrepo.groovy.dsl.NodeTypes;
import groovy.lang.Closure;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

public class GroovyExporter {

    private final IndentedStringWriter writer;

    public GroovyExporter(Writer writer) {
        this.writer = new IndentedStringWriter(writer);
    }

    public void fullExport(Node node) throws Throwable {
        this.writer.newEntity("GoCD.script", () -> {
            this.writer.newEntity("pipelines", () -> {
                export(node);
            });
        });
    }

    public void export(Node node) throws Throwable {
        List<Method> methodThatReturnsNode = getAllMethodsReturning(node.getClass());

        if (methodThatReturnsNode.isEmpty()) {
            throw new UnsupportedOperationException("Unable to understand how to construct a node of type " + node.getClass().getSimpleName());
        }

        if (node instanceof NamedNode) {
            // assume it's a method with signature `(String, Closure)`
            String setterMethodNames = methodThatReturnsNode.get(0).getName();
            writer.newEntity(setterMethodNames, ((NamedNode) node).getName(), () -> {
                exportPropertiesOf(node);
            });
        } else {
            String setterMethodNames = methodThatReturnsNode.get(0).getName();
            writer.newEntity(setterMethodNames, () -> {
                exportPropertiesOf(node);
            });
        }
    }

    private void exportPropertiesOf(Node node) throws Throwable {
        exportSimplePropertiesOf(node);
        exportNonCollectionNodeFields(node);
        writeCollectionFields(node);
    }

    // assigns simple properties:
    // String, Boolean, Number, List<String>, Map<String, String>
    private void exportSimplePropertiesOf(Node node) throws Throwable {
        List<Method> publicSetterMethods = getPublicSetterMethods(node);
        for (Method setterMethod : publicSetterMethods) {
            String fieldName = uncapitalize(setterMethod.getName().replaceFirst("^set", ""));
            if (fieldName.equals("name")) {
                continue;
            }

            Method getterMethod = getPublicGetterMethodFor(node, fieldName);
            getterMethod.setAccessible(true);
            Object propertyValue = getterMethod.invoke(node);
            if (propertyValue == null) {
                continue;
            }
            Object defaultValueOfField = getterMethod.invoke(node.getClass().newInstance());
            if (propertyValue.equals(defaultValueOfField)) {
                continue;
            }
            if (propertyValue instanceof String) {
                writer.property(fieldName, (String) propertyValue);
                continue;
            }
            if (propertyValue instanceof Pattern) {
                writer.property(fieldName, (Pattern) propertyValue);
                continue;
            }
            if (propertyValue instanceof Number) {
                writer.property(fieldName, (Number) propertyValue);
                continue;
            }
            if (propertyValue instanceof Boolean) {
                writer.property(fieldName, (Boolean) propertyValue);
                continue;
            }
            if (propertyValue instanceof Collection && !(propertyValue instanceof CollectionNode)) {
                if (((Collection) propertyValue).isEmpty()) {
                    continue;
                }

                ParameterizedType genericParameterType = (ParameterizedType) setterMethod.getGenericParameterTypes()[0];

                // List<String>
                if (genericParameterType.getRawType().equals(List.class) && genericParameterType.getActualTypeArguments()[0].equals(String.class)) {
                    writer.property(fieldName, (List<String>) propertyValue);
                    continue;
                }
                throw new UnsupportedOperationException("Unable to understand how set field " + fieldName + " on type " + node.getClass().getSimpleName());
            }
            if (propertyValue instanceof Map) {
                if (((Map) propertyValue).isEmpty()) {
                    continue;
                }

                ParameterizedType genericParameterType = (ParameterizedType) setterMethod.getGenericParameterTypes()[0];

                // Map<String, String>
                if (genericParameterType.getRawType().equals(Map.class) &&
                        genericParameterType.getActualTypeArguments()[0].equals(String.class) &&
                        genericParameterType.getActualTypeArguments()[1].equals(String.class)) {
                    writer.property(fieldName, (Map<String, String>) propertyValue);
                    continue;
                }
                throw new UnsupportedOperationException("Unable to understand how set field " + fieldName + " on type " + node.getClass().getSimpleName());
            }
            throw new UnsupportedOperationException("Unable to understand how set field " + fieldName + " on type " + node.getClass().getSimpleName());
        }
    }

    private void exportNonCollectionNodeFields(Node node) throws Throwable {
        List<Field> nonCollectionFields = FieldUtils.getAllFieldsList(node.getClass())
                .stream()
                .filter(field -> Node.class.isAssignableFrom(field.getType()))
                .filter(field -> !CollectionNode.class.isAssignableFrom(field.getType()))
                .sorted(Comparator.comparing(Field::getName))
                .collect(Collectors.toList());


        for (Field nonCollectionField : nonCollectionFields) {
            exportChildNonCollectionField(node, nonCollectionField);
        }
    }

    private void exportChildNonCollectionField(Node node, Field nonCollectionField) throws Throwable {
        Node childNode = (Node) FieldUtils.readField(nonCollectionField, node, true);

        if (childNode == null) {
            return;
        }

        export(childNode);
    }

    private void writeCollectionFields(Node node) throws Throwable {
        List<Field> childCollectionFields = FieldUtils.getAllFieldsList(node.getClass())
                .stream()
                .filter(field -> CollectionNode.class.isAssignableFrom(field.getType()))
                .sorted(Comparator.comparing(Field::getName))
                .collect(Collectors.toList());

        for (Field childCollectionField : childCollectionFields) {
            exportChildCollectionField(node, childCollectionField);
        }
    }

    private void exportChildCollectionField(Node node, Field childCollectionField) throws Throwable {
        CollectionNode<?> childCollection = (CollectionNode<?>) FieldUtils.readField(childCollectionField, node, true);
        if (childCollection.isEmpty()) {
            return;
        }

        writer.simpleField(childCollectionField.getName(), () -> {
            for (Node child : childCollection) {
                export(child);
            }
        });
    }

    private List<Method> getAllMethodsReturning(Class<? extends Node> type) {
        return NodeTypes.ALL_KNOWN_NODE_TYPES.stream()
                .flatMap(aClass -> getAllMethods(aClass))
                .filter(method -> type.isAssignableFrom(method.getReturnType()))
                .filter(method -> !method.isSynthetic())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> ArrayUtils.contains(method.getParameterTypes(), Closure.class))
                .collect(Collectors.toList());
    }

    private List<Method> getPublicSetterMethods(Node node) {
        return getAllMethods(node.getClass())
                .filter(method -> !method.isSynthetic())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> method.getName().startsWith("set"))
                .filter(distinctByKey(Method::getName))
                .sorted(Comparator.comparing(Method::getName)).collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private Method getPublicGetterMethodFor(Node node, String fieldName) {
        return getAllMethods(node.getClass())
                .filter(method -> !method.isSynthetic())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> method.getName().equals("get" + capitalize(fieldName)) || method.getName().equals("is" + capitalize(fieldName)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unable to find getter method for " + fieldName + " on type " + node.getClass().getSimpleName()));
    }

    private static Stream<Method> getAllMethods(Class<? extends Node> aClass) {
        Method[] methodArray = aClass.getDeclaredMethods();
        final List<Class<?>> superclassList = ClassUtils.getAllSuperclasses(aClass);
        for (final Class<?> klass : superclassList) {
            methodArray = ArrayUtils.addAll(methodArray, klass.getDeclaredMethods());
        }
        return Arrays.stream(methodArray).sorted(Comparator.comparing(Method::getName));
    }

}
