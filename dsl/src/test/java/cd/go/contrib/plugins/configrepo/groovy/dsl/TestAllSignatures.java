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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.NodeTypes.ALL_KNOWN_NODE_TYPES;
import static groovy.lang.Closure.DELEGATE_ONLY;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

class TestAllSignatures {

    @Test
    void testJSONIndex() {
        Set<Class<? extends Node>> actualNodeTypes = allNodeTypes().collect(Collectors.toSet());
        assertThat(actualNodeTypes).isEqualTo(ALL_KNOWN_NODE_TYPES);
    }

    private static ScanResult scanResult = new ClassGraph()
//            .verbose()
            .enableClassInfo()
            .enableMethodInfo()
            .ignoreClassVisibility()
            .scan();

    @ParameterizedTest
    @MethodSource("allNonAbstractNodeTypes")
    void nonAbstractClassesShouldBePublic(Class<? extends Node> type) {
        assertThat(Modifier.isPublic(type.getModifiers())).isTrue();
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
        assertThat(parameterAnnotation)
                .hasSize(2)
                .anySatisfy(annotation -> {
                    assertThat(annotation.annotationType()).isSameAs(DelegatesTo.class);
                    DelegatesTo delegatesTo = (DelegatesTo) annotation;
                    assertThat(delegatesTo.value())
                            .isSameAs(type);
                    assertThat(delegatesTo.strategy())
                            .isSameAs(DELEGATE_ONLY);
                }).anySatisfy(annotation -> {
                    assertThat(annotation.annotationType()).isSameAs(ClosureParams.class);
                    ClosureParams closureParams = (ClosureParams) annotation;
                    assertThat(closureParams.value())
                            .isSameAs(SimpleType.class);
                    assertThat(closureParams.options())
                            .hasSize(1)
                            .containsExactly(type.getName());
                }
        );
    }

    @ParameterizedTest
    @MethodSource("allNonAbstractNodeTypes")
    void publicClassConstructorsMustBePublic(Class<? extends Node> type) {
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            assertThat(Modifier.isPublic(constructor.getModifiers()))
                    .describedAs("Constructor %s was not public", constructor)
                    .isTrue();
        }
    }

    @ParameterizedTest
    @MethodSource("methodsReturningNodeType")
    void verifyFactoryMethodSignatures(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        int closureParamIndex = Arrays.asList(method.getParameterTypes()).indexOf(Closure.class);
        assertParameterAnnotations(method.getReturnType(), parameterAnnotations[closureParamIndex]);
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
