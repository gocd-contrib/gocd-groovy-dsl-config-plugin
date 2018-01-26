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

package cd.go.contrib.plugins.configrepo.groovy.sandbox;

import cd.go.contrib.plugins.configrepo.groovy.sandbox.whitelists.EnumeratingWhitelist;
import cd.go.contrib.plugins.configrepo.groovy.sandbox.whitelists.EnumeratingWhitelistTest;
import groovy.lang.GString;
import groovy.lang.Script;
import org.codehaus.groovy.runtime.GStringImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class GroovyCallSiteSelectorTest {

    @Test
    public void arrays() throws Exception {
        Method m = EnumeratingWhitelistTest.C.class.getDeclaredMethod("m", Object[].class);
        Assert.assertEquals("literal call", m, GroovyCallSiteSelector.method(new EnumeratingWhitelistTest.C(), "m", new Object[]{new Object[]{"a", "b"}}));
        assertEquals("we assume the interceptor has dealt with varargs", null, GroovyCallSiteSelector.method(new EnumeratingWhitelistTest.C(), "m", new Object[]{"a", "b"}));
        assertEquals("array cast", m, GroovyCallSiteSelector.method(new EnumeratingWhitelistTest.C(), "m", new Object[]{new String[]{"a", "b"}}));
    }

    @Test
    public void overloads() throws Exception {
        PrintWriter receiver = new PrintWriter(new ByteArrayOutputStream());
        assertEquals(PrintWriter.class.getMethod("print", Object.class), GroovyCallSiteSelector.method(receiver, "print", new Object[]{new Object()}));
        assertEquals(PrintWriter.class.getMethod("print", String.class), GroovyCallSiteSelector.method(receiver, "print", new Object[]{"message"}));
        assertEquals(PrintWriter.class.getMethod("print", int.class), GroovyCallSiteSelector.method(receiver, "print", new Object[]{42}));
    }

    @Test
    public void methodsOnGString() throws Exception {
        GStringImpl gString = new GStringImpl(new Object[0], new String[]{"x"});
        assertEquals(String.class.getMethod("substring", int.class), GroovyCallSiteSelector.method(gString, "substring", new Object[]{99}));
        assertEquals(GString.class.getMethod("getValues"), GroovyCallSiteSelector.method(gString, "getValues", new Object[0]));
        assertEquals(GString.class.getMethod("getStrings"), GroovyCallSiteSelector.method(gString, "getStrings", new Object[0]));
    }

    @Test
    public void primitives() throws Exception {
        assertEquals(Primitives.class.getMethod("m1", long.class), GroovyCallSiteSelector.staticMethod(Primitives.class, "m1", new Object[]{Long.MAX_VALUE}));
        assertEquals(Primitives.class.getMethod("m1", long.class), GroovyCallSiteSelector.staticMethod(Primitives.class, "m1", new Object[]{99}));
        assertEquals(Primitives.class.getMethod("m2", long.class), GroovyCallSiteSelector.staticMethod(Primitives.class, "m2", new Object[]{Long.MAX_VALUE}));
        assertEquals(Primitives.class.getMethod("m2", int.class), GroovyCallSiteSelector.staticMethod(Primitives.class, "m2", new Object[]{99}));
    }

    public static class Primitives {

        public static void m1(long x) {
        }

        public static void m2(int x) {
        }

        public static void m2(long x) {
        }
    }

    @Test
    public void staticMethodsCannotBeOverridden() throws Exception {
        assertEquals(SandboxInterceptorTest.StaticTestExample.class.getMethod("getInstance"), GroovyCallSiteSelector.staticMethod(SandboxInterceptorTest.StaticTestExample.class, "getInstance", new Object[0]));
    }

    @Test
    public void main() throws Exception {
        Script receiver = (Script) new cd.go.contrib.plugins.configrepo.groovy.sandbox.GroovyScriptRunner(null).runScriptWithText("def main() {}; this");
        assertEquals(receiver.getClass().getMethod("main"), GroovyCallSiteSelector.method(receiver, "main", new Object[0]));
        assertEquals(receiver.getClass().getMethod("main", String[].class), GroovyCallSiteSelector.method(receiver, "main", new Object[]{"somearg"}));
    }

    static class EnvVars {

        public EnvVars() {

        }

        public EnvVars(String... objects) {
        }

        public EnvVars(List<String> objects) {

        }
    }

    class VarargsConstructor {

        public VarargsConstructor(String... objects) {

        }

        public VarargsConstructor(List<String> objects) {

        }
    }

    @Test
    public void constructorVarargs() throws Exception {
        assertEquals(EnvVars.class.getConstructor(), GroovyCallSiteSelector.constructor(EnvVars.class, new Object[0]));
        assertEquals(EnvVars.class.getConstructor(String[].class), GroovyCallSiteSelector.constructor(EnvVars.class, new Object[]{"x"}));
        List<String> params = new ArrayList<>();
        params.add("foo");
        params.add("bar");
        params.add("baz");
        assertEquals(EnvVars.class.getConstructor(List.class),
                GroovyCallSiteSelector.constructor(EnvVars.class, new Object[]{params}));
        assertEquals(EnvVars.class.getConstructor(String[].class),
                GroovyCallSiteSelector.constructor(EnvVars.class, new Object[]{params.get(0)}));
        assertEquals(EnvVars.class.getConstructor(String[].class),
                GroovyCallSiteSelector.constructor(EnvVars.class, params.toArray()));
        assertEquals(EnumeratingWhitelist.MethodSignature.class.getConstructor(Class.class, String.class, Class[].class),
                GroovyCallSiteSelector.constructor(EnumeratingWhitelist.MethodSignature.class,
                        new Object[]{String.class, "foo", Integer.class, Float.class}));
    }

    @Test
    public void varargsFailureCases() throws Exception {
        // If there's a partial match, we should get a ClassCastException
        try {
            assertNull(GroovyCallSiteSelector.constructor(VarargsConstructor.class,
                    new Object[]{"x", new Object()}));
        } catch (Exception e) {
            assertTrue(e instanceof ClassCastException);
            assertEquals("Cannot cast object 'x' with class 'java.lang.String' to class 'hudson.model.ParameterValue'",
                    e.getMessage());
        }
        // If it's a complete non-match, we just shouldn't get a constructor.
        assertNull(GroovyCallSiteSelector.constructor(VarargsConstructor.class, new Object[]{"a", "b"}));
    }

    @Test
    public void varargsArrayElementTypeMismatch() throws Exception {
        List<String> l = Arrays.asList("a", "b", "c");
        assertEquals(String.class.getMethod("join", CharSequence.class, Iterable.class),
                GroovyCallSiteSelector.staticMethod(String.class, "join", new Object[]{",", l}));
    }
}
