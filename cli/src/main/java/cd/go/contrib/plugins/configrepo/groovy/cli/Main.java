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

package cd.go.contrib.plugins.configrepo.groovy.cli;

import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD;
import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipeline;
import cd.go.contrib.plugins.configrepo.groovy.dsl.json.GoCDJsonSerializer;
import cd.go.contrib.plugins.configrepo.groovy.sandbox.GroovyScriptRunner;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.codehaus.groovy.runtime.IOGroovyMethods;

import javax.validation.ConstraintViolation;
import java.io.*;

import static cd.go.contrib.plugins.configrepo.groovy.dsl.validate.Validator.validate;

public class Main {

    private final Syntax args;

    private GroovyScriptRunner engine;

    public Main(Syntax args) {
        this.args = args;
    }

    public static void main(String[] argv) {
        Syntax args = parseArgsAndBlowUpIfRequired(argv);

        new Main(args).run();
    }

    private void run() {
        try {
            System.out.print("Parsing file " + getLocation(args.file) + ".");
            String contents = IOGroovyMethods.getText(getFileAsStream(args.file), "utf-8");
            java.lang.Object maybeConfig = getRunner().runScriptWithText(contents);
            if (maybeConfig instanceof GoCD) {
                System.out.print(" Ok!");
                GoCD configFromFile = (GoCD) maybeConfig;

                validate(configFromFile, violations -> {
                    System.out.println("Found " + violations.size() + " validation errors!");
                    for (ConstraintViolation<Object> violation : violations) {
                        System.out.println("  - " + violation.getPropertyPath() + " " + violation.getMessage());
                    }
                    System.exit(1);
                });

                System.out.println(" Found environments: " + configFromFile.environments(null).getNames() + ".");
                System.out.println(" Found pipelines: " + configFromFile.pipelines(null).getNames() + ".");
                System.out.println();
                String jsonString = GoCDJsonSerializer.toJsonString(configFromFile);
                if (args.showJson) {
                    System.out.println();
                    System.out.println("Showing JSON from file " + getLocation(args.file) + ":");
                    System.out.println(jsonString);
                }
            }
        } catch (Exception e) {
            System.out.println(" Bad!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private GroovyScriptRunner getRunner() throws IOException {
        if (engine == null) {
            engine = new GroovyScriptRunner(".", Pipeline.class.getPackage().getName());
        }
        return engine;
    }

    private static InputStream getFileAsStream(String file) {
        InputStream s = null;
        try {
            s = "-".equals(file) ? System.in : new FileInputStream(new File(file));
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return s;
    }

    private static Syntax parseArgsAndBlowUpIfRequired(String... argv) {
        HelpCommand root = new HelpCommand();
        Syntax syntax = new Syntax();
        JCommander cmd = JCommander.newBuilder()
                .programName("java -jar groovy-dsl-plugin.jar")
                .addObject(root)
                .addCommand("syntax", syntax)
                .build();

        String parsedCommand = cmd.getParsedCommand();

        try {
            cmd.parse(argv);

            if (syntax.help) {
                printUsageAndExit(1, cmd, parsedCommand);
            }

            if (null == syntax.file) {
                printUsageAndExit(1, cmd, parsedCommand);
            }

        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            printUsageAndExit(1, cmd, parsedCommand);
        }
        return syntax;
    }

    private static String getLocation(String file) {
        return "-".equals(file) ? "<STDIN>" : file;
    }

    private static void printUsageAndExit(int exitCode, JCommander cmd, String command) {
        StringBuilder out = new StringBuilder();
        if (null == command) {
            cmd.getUsageFormatter().usage(out);
        } else {
            cmd.getUsageFormatter().usage(command, out);
        }
        String message = out.toString();
        System.err.println(message);
        System.exit(exitCode);
    }
}
