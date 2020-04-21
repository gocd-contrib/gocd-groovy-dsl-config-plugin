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

package cd.go.contrib.plugins.configrepo.groovy.sandbox.whitelists;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * {@link Whitelist} that allows everything defined from a specific classloader.
 *
 * @author Jesse Glick
 */
public final class ClassLoaderWhitelist extends Whitelist {
    private final ClassLoader scriptLoader;

    public ClassLoaderWhitelist(ClassLoader scriptLoader) {
        this.scriptLoader = scriptLoader;
    }

    private boolean permits(Class<?> declaringClass) {
        return declaringClass.getClassLoader() == scriptLoader;
    }

    @Override public boolean permitsMethod(Method method, Object receiver, Object[] args) {
        return permits(method.getDeclaringClass());
    }

    @Override public boolean permitsConstructor(Constructor<?> constructor, Object[] args) {
        return permits(constructor.getDeclaringClass());
    }

    @Override public boolean permitsStaticMethod(Method method, Object[] args) {
        return permits(method.getDeclaringClass());
    }

    @Override public boolean permitsFieldGet(Field field, Object receiver) {
        return permits(field.getDeclaringClass());
    }

    @Override public boolean permitsFieldSet(Field field, Object receiver, Object value) {
        return permits(field.getDeclaringClass());
    }

    @Override public boolean permitsStaticFieldGet(Field field) {
        return permits(field.getDeclaringClass());
    }

    @Override public boolean permitsStaticFieldSet(Field field, Object value) {
        return permits(field.getDeclaringClass());
    }
}

