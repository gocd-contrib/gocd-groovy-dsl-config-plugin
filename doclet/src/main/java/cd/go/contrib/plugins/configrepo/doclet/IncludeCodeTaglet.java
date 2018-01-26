/*
 * Copyright 2018 ThoughtWorks, Inc.
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

package cd.go.contrib.plugins.configrepo.doclet;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import com.uwyn.jhighlight.renderer.GroovyXhtmlRenderer;
import com.uwyn.jhighlight.renderer.XhtmlRenderer;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.codehaus.groovy.runtime.StringGroovyMethods;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class IncludeCodeTaglet implements Taglet {

    @Override
    public boolean inField() {
        return true;
    }

    @Override
    public boolean inConstructor() {
        return true;
    }

    @Override
    public boolean inMethod() {
        return true;
    }

    @Override
    public boolean inOverview() {
        return true;
    }

    @Override
    public boolean inPackage() {
        return true;
    }

    @Override
    public boolean inType() {
        return true;
    }

    @Override
    public boolean isInlineTag() {
        return true;
    }

    @Override
    public String getName() {
        return "includeCode";
    }

    @Override
    public String toString(Tag tag) {
        return "<code>" + readLines("/" + tag.text().trim().replaceAll("^\"|\"$", "").replaceAll("^'|'$", "")) + "</code>";
    }

    private String readLines(String resourceFile) {
        URL resource = IncludeCodeTaglet.class.getResource(resourceFile);
        try {
            List<String> lines = ResourceGroovyMethods.readLines(resource, "utf-8");

            AtomicBoolean hasWithBlock = new AtomicBoolean(false);
            lines.removeIf(line -> {
                if (line.matches("^\\s*import\\s+.*")) {
                    return true;
                }

                if (line.matches("^\\s*new\\s+.*.\\.with\\s*\\{\\s*$")) {
                    hasWithBlock.set(true);
                    return true;
                }
                return false;
            });
            CharSequence joinedLines = String.join("\n", lines);
            if (hasWithBlock.get()) {
                joinedLines = StringGroovyMethods.reverse((CharSequence) StringGroovyMethods.reverse(joinedLines).replaceFirst("}", ""));
            }
            String code = StringGroovyMethods.stripIndent(joinedLines).trim();

            GroovyXhtmlRenderer groovyXhtmlRenderer = new GroovyXhtmlRenderer();
            Method getCssClassDefinitions = XhtmlRenderer.class.getDeclaredMethod("getCssClassDefinitions");
            getCssClassDefinitions.setAccessible(true);
            String css = "<style type='text/css'>" +
                    getCssClassDefinitions.invoke(groovyXhtmlRenderer) +
                    "</style>";
            String highlightedCode = new GroovyXhtmlRenderer().highlight(null, code, "utf-8", true);
            return css + highlightedCode;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not find resource " + resourceFile, e);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not invoke method getCssClassDefinitions", e);
        }
    }

    @Override
    public String toString(Tag[] tags) {
        return null;
    }

    public static void register(Map<String, Taglet> tagletMap) {
        IncludeCodeTaglet tag = new IncludeCodeTaglet();
        Taglet t = tagletMap.get(tag.getName());
        if (t != null) {
            tagletMap.remove(tag.getName());
        }
        tagletMap.put(tag.getName(), tag);
    }
}
