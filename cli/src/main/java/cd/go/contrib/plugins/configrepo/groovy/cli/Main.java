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

package cd.go.contrib.plugins.configrepo.groovy.cli;

import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD;
import cd.go.contrib.plugins.configrepo.groovy.dsl.Pipeline;
import cd.go.contrib.plugins.configrepo.groovy.sandbox.GroovyScriptRunner;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;

public class Main {

    private final Args args;

    private GroovyScriptRunner engine;

    public Main(Args args) {
        this.args = args;
    }

    public static void main(String[] argv) {
        Args args = parseArgsAndBlowUpIfRequired(argv);

        new Main(args).run();
    }

    private void run() {
        for (File file : args.files) {
            try {
                System.out.print("Parsing file " + file + ".");
                java.lang.Object maybeConfig = getRunner().runScript(file.getPath());
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

                    System.out.print(" Found environments: " + configFromFile.getEnvironments().getNames() + ".");
                    System.out.print(" Found pipelines: " + configFromFile.getPipelines().getNames() + ".");
                    System.out.println();
                    String jsonString = configFromFile.toJsonString();
                    if (args.showJson) {
                        System.out.println();
                        System.out.println("Showing JSON from file " + file + ":");
                        System.out.println(jsonString);
                    }
                }
            } catch (Exception e) {
                System.out.println(" Bad!");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void validate(Object configFromFile, Consumer<Set<ConstraintViolation<Object>>> errorHandler) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(configFromFile);
        if (!violations.isEmpty()) {
            errorHandler.accept(violations);
        }
    }

    private GroovyScriptRunner getRunner() throws IOException {
        if (engine == null) {
            engine = new GroovyScriptRunner(".", Pipeline.class.getPackage().getName());
        }
        return engine;
    }

    private static Args parseArgsAndBlowUpIfRequired(String[] argv) {
        Args args = new Args();

        try {
            new JCommander(args)
                    .parse(argv);
            if (args.help) {
                printUsageAndExit(0);
            }


        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            printUsageAndExit(1);
        }
        return args;
    }

    private static void printUsageAndExit(int exitCode) {
        StringBuilder out = new StringBuilder();
        JCommander jCommander = new JCommander(new Args());
        jCommander.setProgramName("java -jar groovy-dsl-plugin.jar");
        jCommander.usage(out);
        System.err.print(out);
        System.exit(exitCode);
    }
}
