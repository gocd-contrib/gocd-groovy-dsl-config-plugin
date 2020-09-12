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

package cd.go.contrib.plugins.configrepo.groovy.dsl;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.reflections.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.NodeTypes.ALL_KNOWN_NODE_TYPES;
import static groovy.lang.Closure.DELEGATE_ONLY;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestAllSignatures {

    @Test
    void testJSONIndex() {
        Set<Class<? extends Node>> actualNodeTypes = allNodeTypes().collect(Collectors.toSet());
        assertEquals(ALL_KNOWN_NODE_TYPES, actualNodeTypes);
    }

    private static final ScanResult scanResult = new ClassGraph()
            .enableClassInfo()
            .enableMethodInfo()
            .ignoreClassVisibility()
            .scan();

    @ParameterizedTest
    @MethodSource("allNonAbstractNodeTypes")
    void nonAbstractClassesShouldBePublic(Class<? extends Node> type) {
        assertTrue(Modifier.isPublic(type.getModifiers()));
    }

    @ParameterizedTest
    @MethodSource("allNodeTypes")
    void verifyConstructorClosureParameterAnnotation(Class<? extends Node> type) {
        Constructor<?>[] constructors = type.getDeclaredConstructors();

        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() >= 1) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                int closureParamIndex = Arrays.asList(parameterTypes).indexOf(Closure.class);
                if (closureParamIndex != -1) {
                    Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
                    assertParameterAnnotations(type, parameterAnnotations[closureParamIndex]);
                }
            }
        }
    }

    private void assertParameterAnnotations(Class<?> type, Annotation[] parameterAnnotation) {
        assertEquals(2, parameterAnnotation.length);
        assertTrue(Arrays.stream(parameterAnnotation).anyMatch(annotation -> DelegatesTo.class == annotation.annotationType() &&
                type == ((DelegatesTo) annotation).value() &&
                DELEGATE_ONLY == ((DelegatesTo) annotation).strategy()));
        assertTrue(Arrays.stream(parameterAnnotation).anyMatch(annotation -> ClosureParams.class == annotation.annotationType() &&
                SimpleType.class == ((ClosureParams) annotation).value() &&
                Collections.singletonList(type.getName()).equals(Arrays.asList(((ClosureParams) annotation).options()))));
    }

    @ParameterizedTest
    @MethodSource("allNonAbstractNodeTypes")
    void publicClassConstructorsMustBePublic(Class<? extends Node> type) {
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            assertTrue(Modifier.isPublic(constructor.getModifiers()), format("Constructor %s was not public", constructor));
        }
    }

    @ParameterizedTest
    @MethodSource("methodsReturningNodeType")
    void verifyFactoryMethodSignatures(Method method) {
        if (!Modifier.isAbstract(method.getModifiers())) {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            int closureParamIndex = Arrays.asList(method.getParameterTypes()).indexOf(Closure.class);
            assertParameterAnnotations(method.getReturnType(), parameterAnnotations[closureParamIndex]);
        }
    }

    private static Stream<Class<Node>> allNodeTypes() {
        return scanResult.getSubclasses(Node.class.getName()).loadClasses(Node.class).stream();
    }

    private static Stream<Class<Node>> allNonAbstractNodeTypes() {
        return allNodeTypes().filter(aClass -> !Modifier.isAbstract(aClass.getModifiers()));
    }

    @SuppressWarnings("unchecked")
    private static Stream<Arguments> methodsReturningNodeType() {
        return allNodeTypes()
                .flatMap(aClass -> ReflectionUtils.getAllMethods(aClass, input -> Node.class.isAssignableFrom(requireNonNull(input).getReturnType())).stream())
                .filter(method -> method.getDeclaringClass() != CollectionNode.class)
                .filter(method -> !method.isSynthetic())
                .filter(method -> !method.getDeclaringClass().isAnonymousClass())
                .filter(method -> method.getParameterCount() > 0)
                .filter(method -> Arrays.asList(method.getParameterTypes()).contains(Closure.class))
                .map(Arguments::of);
    }
}
