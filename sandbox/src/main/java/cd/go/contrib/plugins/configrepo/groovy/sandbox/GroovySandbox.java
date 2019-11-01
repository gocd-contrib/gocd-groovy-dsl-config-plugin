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

import cd.go.contrib.plugins.configrepo.groovy.sandbox.whitelists.ClassLoaderWhitelist;
import cd.go.contrib.plugins.configrepo.groovy.sandbox.whitelists.ProxyWhitelist;
import cd.go.contrib.plugins.configrepo.groovy.sandbox.whitelists.Whitelist;
import groovy.grape.GrabAnnotationTransformation;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;
import org.kohsuke.groovy.sandbox.SandboxTransformer;

import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.Callable;

public class GroovySandbox {

    public static CompilerConfiguration createSecureCompilerConfiguration() {
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.addCompilationCustomizers(new RejectASTTransformsCustomizer());
        cc.setDisabledGlobalASTTransformations(new HashSet<>(Collections.singletonList(GrabAnnotationTransformation.class.getName())));
        cc.addCompilationCustomizers(new SandboxTransformer());
        return cc;
    }

    public static ClassLoader createSecureClassLoader(ClassLoader base) {
        return new SandboxResolvingClassLoader(base);
    }

    public static <V> V runInSandbox(Callable<V> c, Whitelist whitelist) throws Exception {
        GroovyInterceptor sandbox = new SandboxInterceptor(whitelist);
        sandbox.register();
        try {
            return c.call();
        } finally {
            sandbox.unregister();
        }
    }

    public static Object run(Script script, final Whitelist whitelist) throws RejectedAccessException {
        Whitelist wrapperWhitelist = new ProxyWhitelist(new ClassLoaderWhitelist(script.getClass().getClassLoader()), whitelist);
        GroovyInterceptor sandbox = new SandboxInterceptor(wrapperWhitelist);
        sandbox.register();
        try {
            return script.run();
        } finally {
            sandbox.unregister();
        }
    }

    private GroovySandbox() {
    }

}
