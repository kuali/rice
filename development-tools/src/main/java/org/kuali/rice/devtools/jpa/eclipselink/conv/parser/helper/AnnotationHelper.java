/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AnnotationExpr;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is a sort of visitor "helper" class that looks to see if a particular annotation is on a class and if not
 * places it on the class.
 */
public class AnnotationHelper extends VoidVisitorHelperBase<String> {

    private static final Log LOG = LogFactory.getLog(AnnotationHelper.class);

    private final Collection<AnnotationResolver> resolvers;
    private final boolean removeExisting;

    public AnnotationHelper(Collection<AnnotationResolver> resolvers, boolean removeExisting) {
        this.resolvers = resolvers;
        this.removeExisting = removeExisting;
    }

    @Override
    public void visitPre(final ClassOrInterfaceDeclaration n, final String mappedClass) {
        addAnnotation(n, mappedClass, Level.CLASS);
    }

    @Override
    public void visitPre(final FieldDeclaration n, final String mappedClass) {
        addAnnotation(n, mappedClass, Level.FIELD);
    }

    /** walks up the tree until reaching the CompilationUnit. */
    private CompilationUnit getCompilationUnit(Node n) {
        Node unit = n;
        while (!(unit instanceof CompilationUnit) && unit != null) {
            unit = unit.getParentNode();
        }
        return (CompilationUnit) unit;
    }

    private void addAnnotation(final BodyDeclaration n, final String mappedClass, Level level) {
        for (AnnotationResolver resolver : resolvers) {
            if (resolver.getLevel() == level) {
                LOG.debug("Evaluating resolver " + ClassUtils.getShortClassName(resolver.getClass()) + " for " + getTypeOrFieldNameForMsg(n) + ".");

                final String fullyQualifiedName = resolver.getFullyQualifiedName();

                //1 figure out if annotation is already imported either via star import or single import.
                final CompilationUnit unit = getCompilationUnit(n);
                final List<ImportDeclaration> imports = unit.getImports() != null ? unit.getImports() : new ArrayList<ImportDeclaration>();
                final boolean foundAnnImport = imported(imports, fullyQualifiedName);

                //2 check if annotation already exists...
                final AnnotationExpr existingAnnotation = findAnnotation(n, fullyQualifiedName, foundAnnImport);

                //3 if removeExisting is set and the annotation exists, then remove the annotation prior to calling the resolver
                //Note: cannot remove the import without much more complex logic because the annotation may exist on other nodes in the CompilationUnit
                //Could traverse the entire CompilationUnit searching for the annotation if we wanted to determine whether we can safely remove an import
                if (removeExisting && existingAnnotation != null) {
                    LOG.info("removing existing " + existingAnnotation + " from " + getTypeOrFieldNameForMsg(n) + ".");
                    final List<AnnotationExpr> annotations = n.getAnnotations() != null ? n.getAnnotations() : new ArrayList<AnnotationExpr>();
                    annotations.remove(existingAnnotation);
                    n.setAnnotations(annotations);
                }

                //4 add annotation if it doesn't already exist or if replaceExisting is set
                // and the annotation resolves (meaning the resolver determines if should be added by returning a non-null value)
                if (existingAnnotation == null || (existingAnnotation != null && removeExisting)) {
                    NodeData nodes = resolver.resolve(n, mappedClass);
                    if (nodes != null && nodes.annotation != null) {
                        LOG.info("adding " + nodes.annotation + " to " + getTypeOrFieldNameForMsg(n) + ".");
                        final List<AnnotationExpr> annotations = n.getAnnotations() != null ? n.getAnnotations() : new ArrayList<AnnotationExpr>();
                        annotations.add(nodes.annotation);
                        n.setAnnotations(annotations);

                        //5 add import for annotation
                        if (!foundAnnImport) {
                            LOG.info("adding import " + fullyQualifiedName + " to " + getTypeNameForMsg(n) + ".");
                            imports.add(nodes.annotationImport);
                        }

                        //6 add additional imports if they are needed
                        if (nodes.additionalImports != null) {
                            for (ImportDeclaration aImport : nodes.additionalImports) {
                                if (aImport.isStatic() || aImport.isAsterisk()) {
                                    throw new IllegalStateException("The additional imports should not be static or star imports");
                                }
                                final boolean imported = imported(imports, aImport.getName().toString());
                                if (!imported) {
                                    LOG.info("adding import " + aImport.getName().toString() + " to " + getTypeNameForMsg(n) + ".");
                                    imports.add(aImport);
                                }
                            }
                        }

                        unit.setImports(imports);

                        if (nodes.nestedDeclaration != null) {
                            final TypeDeclaration parent = unit.getTypes().get(0);

                            final List<BodyDeclaration> members = parent.getMembers() != null ? parent.getMembers() : new ArrayList<BodyDeclaration>();
                            final TypeDeclaration existingNestedDeclaration = findTypeDeclaration(members, nodes.nestedDeclaration.getName());

                            //7 if removeExisting is set and the nested declaration exists, then remove the nested declaration
                            if (removeExisting) {
                                if (existingNestedDeclaration != null) {
                                    LOG.info("removing existing nested declaration " + existingNestedDeclaration.getName() + " from " + getTypeOrFieldNameForMsg(n) + ".");
                                    members.remove(existingNestedDeclaration);
                                }
                            }

                            //8 add nested class
                            if (existingNestedDeclaration == null || (existingNestedDeclaration != null && removeExisting)) {
                                nodes.nestedDeclaration.setParentNode(parent);
                                LOG.info("adding nested declaration " + nodes.nestedDeclaration.getName() + " to " + getTypeOrFieldNameForMsg(n) + ".");
                                members.add(nodes.nestedDeclaration);
                            }
                            parent.setMembers(members);
                        }
                    }
                }
            }
        }
    }

    private AnnotationExpr findAnnotation(final BodyDeclaration n, String fullyQualifiedName, boolean foundAnnImport) {
        final String simpleName = ClassUtils.getShortClassName(fullyQualifiedName);
        final List<AnnotationExpr> annotations = n.getAnnotations() != null ? n.getAnnotations() : new ArrayList<AnnotationExpr>();

        for (AnnotationExpr ae : annotations) {
            final String name = ae.getName().toString();
            if ((simpleName.equals(name) && foundAnnImport)) {
                LOG.info("found " + ae + " on " + getTypeOrFieldNameForMsg(n) + ".");
                return ae;
            }

            if (fullyQualifiedName.equals(name)) {
                LOG.info("found " + ae + " on " + getTypeOrFieldNameForMsg(n) + ".");
                return ae;
            }
        }
        return null;
    }

    private TypeDeclaration findTypeDeclaration(List<BodyDeclaration> members, String name) {
        if (members != null) {
            for (BodyDeclaration bd : members) {
                if (bd instanceof TypeDeclaration) {
                    if (((TypeDeclaration) bd).getName().equals(name)) {
                        return (TypeDeclaration) bd;
                    }
                }
            }
        }
        return null;
    }

    private boolean imported(List<ImportDeclaration> imports, String fullyQualifiedName) {
        final String packageName = ClassUtils.getPackageName(fullyQualifiedName);

        for (final ImportDeclaration i : imports) {
            if (!i.isStatic()) {
                final String importName = i.getName().toString();
                if (i.isAsterisk()) {
                    if (packageName.equals(importName)) {
                        if ( LOG.isDebugEnabled() ) {
                            LOG.debug("found import " + packageName + ".* on " + getTypeNameForMsg(i) + ".");
                        }
                        return true;
                    }
                } else {
                    if (fullyQualifiedName.equals(importName)) {
                        if ( LOG.isDebugEnabled() ) {
                            LOG.debug("found import " + fullyQualifiedName + " on " + getTypeNameForMsg(i) + ".");
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String getTypeOrFieldNameForMsg(final BodyDeclaration n) {
        if (n instanceof TypeDeclaration) {
            return ((TypeDeclaration) n).getName();
        } else if (n instanceof FieldDeclaration) {
            final FieldDeclaration fd = (FieldDeclaration) n;
            //this wont work for nested classes but we should be in nexted classes at this point
            final CompilationUnit unit = getCompilationUnit(n);
            final TypeDeclaration parent = unit.getTypes().get(0);
            Collection<String> variableNames = new ArrayList<String>();
            if (fd.getVariables() != null) {
                for (VariableDeclarator vd : fd.getVariables()) {
                    variableNames.add(vd.getId().getName());
                }
            }
            return variableNames.size() == 1 ?
                    parent.getName() + "." + variableNames.iterator().next() :
                    parent.getName() + "." + variableNames.toString();

        }
        return null;
    }

    private String getTypeNameForMsg(final Node n) {
        final CompilationUnit unit = getCompilationUnit(n);
        final TypeDeclaration parent = unit.getTypes().get(0);
        return parent.getName();
    }
}
