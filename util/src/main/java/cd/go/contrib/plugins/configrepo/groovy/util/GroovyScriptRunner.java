/*
 * Copyright 2021 ThoughtWorks, Inc.
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

package cd.go.contrib.plugins.configrepo.groovy.util;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

import java.io.File;
import java.io.IOException;

public class GroovyScriptRunner {

    private final GroovyShell groovyShell;

    public GroovyScriptRunner(String... imports) {
        final CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        final ImportCustomizer importCustomizer = new ImportCustomizer();
        importCustomizer.addStarImports(imports);
        compilerConfiguration.addCompilationCustomizers(importCustomizer);

        this.groovyShell = new GroovyShell(this.getClass().getClassLoader(), new Binding(), compilerConfiguration);
    }

    public Object runScript(final String url) throws IOException {
        return runScriptWithText(ResourceGroovyMethods.getText(new File(url), "utf-8"));
    }

    public Object runScriptWithText(final String text) {
        return groovyShell.parse(text).run();
    }
}
