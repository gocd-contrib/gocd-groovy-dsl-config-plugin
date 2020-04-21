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

package cd.go.contrib.plugins.configrepo.groovy.sandbox;

import cd.go.contrib.plugins.configrepo.groovy.sandbox.whitelists.ClassLoaderWhitelist;
import cd.go.contrib.plugins.configrepo.groovy.sandbox.whitelists.GenericWhitelist;
import cd.go.contrib.plugins.configrepo.groovy.sandbox.whitelists.ProxyWhitelist;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

import java.io.File;
import java.io.IOException;

public class GroovyScriptRunner {

    private final GroovyShell groovyShell;

    private final String url;

    public GroovyScriptRunner(String url, String... imports) {
        this.url = url;
        CompilerConfiguration compilerConfiguration = GroovySandbox.createSecureCompilerConfiguration();
        ImportCustomizer importCustomizer = new ImportCustomizer();
        importCustomizer.addStarImports(imports);
        compilerConfiguration.addCompilationCustomizers(importCustomizer);

        ClassLoader secureClassLoader = GroovySandbox.createSecureClassLoader(GroovyScriptRunner.class.getClassLoader());
        this.groovyShell = new GroovyShell(secureClassLoader, new Binding(), compilerConfiguration);
    }

    public Object runScript(String fileName) throws IOException {
        return runScriptWithText(ResourceGroovyMethods.getText(new File(url + "/" + fileName), "utf-8"));
    }

    public Object runScriptWithText(String scriptText) throws IOException {
        Script script = groovyShell.parse(scriptText);
        return GroovySandbox.run(script, new ProxyWhitelist(new ClassLoaderWhitelist(GroovyScriptRunner.class.getClassLoader()), new GenericWhitelist()));
    }

}
