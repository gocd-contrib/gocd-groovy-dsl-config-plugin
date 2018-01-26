/*
 * The MIT License
 *
 * Copyright 2014 CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package cd.go.contrib.plugins.configrepo.groovy.sandbox.whitelists;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Determines which methods and similar members which scripts may call.
 */
public abstract class Whitelist {

    public abstract boolean permitsMethod(Method method, Object receiver, Object[] args);

    public abstract boolean permitsConstructor(Constructor<?> constructor, Object[] args);

    public abstract boolean permitsStaticMethod(Method method, Object[] args);

    public abstract boolean permitsFieldGet(Field field, Object receiver);

    public abstract boolean permitsFieldSet(Field field, Object receiver, Object value);

    public abstract boolean permitsStaticFieldGet(Field field);

    public abstract boolean permitsStaticFieldSet(Field field, Object value);

}
