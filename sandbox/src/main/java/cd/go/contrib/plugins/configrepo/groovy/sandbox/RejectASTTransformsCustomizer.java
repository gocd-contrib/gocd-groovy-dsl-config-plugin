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

import com.google.common.collect.ImmutableList;
import groovy.lang.*;
import groovy.transform.ASTTest;
import groovy.transform.AnnotationCollector;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

import java.util.ArrayList;
import java.util.List;

public class RejectASTTransformsCustomizer extends CompilationCustomizer {
    private static final List<String> BLOCKED_TRANSFORMS = ImmutableList.of(ASTTest.class.getCanonicalName(), Grab.class.getCanonicalName(),
            GrabConfig.class.getCanonicalName(), GrabExclude.class.getCanonicalName(), GrabResolver.class.getCanonicalName(),
            Grapes.class.getCanonicalName(), AnnotationCollector.class.getCanonicalName());

    public RejectASTTransformsCustomizer() {
        super(CompilePhase.CONVERSION);
    }

    @Override
    public void call(final SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
        new RejectASTTransformsVisitor(source).visitClass(classNode);
    }

    private static class RejectASTTransformsVisitor extends ClassCodeVisitorSupport {
        private SourceUnit source;

        public RejectASTTransformsVisitor(SourceUnit source) {
            this.source = source;
        }

        @Override
        protected SourceUnit getSourceUnit() {
            return source;
        }

        @Override
        public void visitImports(ModuleNode node) {
            if (node != null) {
                for (ImportNode importNode : node.getImports()) {
                    checkImportForBlockedAnnotation(importNode);
                }
                for (ImportNode importStaticNode : node.getStaticImports().values()) {
                    checkImportForBlockedAnnotation(importStaticNode);
                }
            }
        }

        private void checkImportForBlockedAnnotation(ImportNode node) {
            if (node != null && node.getType() != null) {
                for (String blockedAnnotation : getBlockedTransforms()) {
                    if (blockedAnnotation.equals(node.getType().getName()) || blockedAnnotation.endsWith("." + node.getType().getName())) {
                        throw new SecurityException("Annotation " + node.getType().getName() + " cannot be used in the sandbox.");
                    }
                }
            }
        }

        /**
         * If the node is annotated with one of the blocked transform annotations, throw a security exception.
         *
         * @param node the node to process
         */
        @Override
        public void visitAnnotations(AnnotatedNode node) {
            for (AnnotationNode an : node.getAnnotations()) {
                for (String blockedAnnotation : getBlockedTransforms()) {
                    if (blockedAnnotation.equals(an.getClassNode().getName()) || blockedAnnotation.endsWith("." + an.getClassNode().getName())) {
                        throw new SecurityException("Annotation " + an.getClassNode().getName() + " cannot be used in the sandbox.");
                    }
                }
            }
        }
    }

    private static List<String> getBlockedTransforms() {
        List<String> blocked = new ArrayList<>(BLOCKED_TRANSFORMS);

        String additionalBlocked = System.getProperty(RejectASTTransformsCustomizer.class.getName() + ".ADDITIONAL_BLOCKED_TRANSFORMS");

        if (additionalBlocked != null) {
            for (String b : additionalBlocked.split(",")) {
                blocked.add(b.trim());
            }
        }

        return blocked;
    }
}

