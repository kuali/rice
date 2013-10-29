package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.DescriptorRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is a sort of visitor "helper" class that looks to see if a particular annotation is on a class and if not
 * places it on the class.
 */
public class AnnotationHelper extends OjbDescriptorRepositoryAwareVisitorHelper {

    private static final Log LOG = LogFactory.getLog(AnnotationHelper.class);

    private final Collection<AnnotationResolver> resolvers;

    public AnnotationHelper(Collection<DescriptorRepository> descriptorRepositories, Collection<AnnotationResolver> resolvers) {
        super(descriptorRepositories);
        this.resolvers = resolvers;
    }

    @Override
    public void visitPre(final ClassOrInterfaceDeclaration n, final Object arg) {
        addAnnotation(n ,arg, Level.CLASS);
    }

    @Override
    public void visitPre(final FieldDeclaration n, final Object arg) {
        addAnnotation(n ,arg, Level.FIELD);
    }

    /** walks up the tree until reaching the CompilationUnit. */
    private CompilationUnit getCompilationUnit(Node n) {
        Node unit = n;
        while (!(unit instanceof CompilationUnit) && unit != null) {
            unit = unit.getParentNode();
        }
        return (CompilationUnit) unit;
    }

    private void addAnnotation(final BodyDeclaration n, final Object arg, Level level) {
        for (AnnotationResolver resolver : resolvers) {
            if (resolver.getLevel() == level) {
                LOG.info("Evaluating resolver " + ClassUtils.getShortClassName(resolver.getClass()));

                final String fullyQualifiedName = resolver.getFullyQualifiedName();
                final String simpleName = ClassUtils.getShortClassName(fullyQualifiedName);

                //1 figure out if annotation is already imported either via star import or single import.
                final CompilationUnit unit = getCompilationUnit(n);
                final List<ImportDeclaration> imports = unit.getImports() != null ? unit.getImports() : new ArrayList<ImportDeclaration>();
                final boolean foundAnnImport = imported(imports, fullyQualifiedName);


                //2 check if annotation already exists...if so don't add it.
                boolean foundfullyQualifiedAnn = false;
                boolean foundSimpleAnn = false;
                final List<AnnotationExpr> annotations = n.getAnnotations() != null ? n.getAnnotations() : new ArrayList<AnnotationExpr>();

                for (AnnotationExpr ae : annotations) {
                    final String name = ae.getName().toString();
                    if ((simpleName.equals(name) && foundAnnImport)) {
                        LOG.info("found " + ae + " on " + getNameFormMessage(n) + " ignoring.");
                        foundSimpleAnn = true;
                        break;
                    }

                    if (fullyQualifiedName.equals(name)) {
                        foundfullyQualifiedAnn = true;
                        LOG.info("found " + ae + " on " + getNameFormMessage(n) + " ignoring.");
                        break;
                    }
                }

                //3 add annotation if it doesn't already exist and the annotation resolves (meaning the resolver
                // determines if should be added by returning a non-null value)
                if (!foundfullyQualifiedAnn && !foundSimpleAnn) {
                    NodeData nodes = resolver.resolve(getDescriptorRepositories(), n, arg);
                    if (nodes != null && nodes.annotation != null) {
                        LOG.info("adding " + nodes.annotation + " to " + getNameFormMessage(n) + ".");
                        annotations.add(nodes.annotation);
                        n.setAnnotations(annotations);

                        //4 add import for annotation
                        if (!foundAnnImport) {
                            LOG.info("adding import " + fullyQualifiedName + " to " + unit.getTypes().get(0).getName() + ".");
                            imports.add(nodes.annotationImport);
                        }

                        //5 add additional imports if they are needed
                        if (nodes.additionalImports != null) {
                            for (ImportDeclaration aImport : nodes.additionalImports) {
                                if (aImport.isStatic() || aImport.isAsterisk()) {
                                    throw new IllegalStateException("The additional imports should not be static or star imports");
                                }
                                final boolean imported = imported(imports, aImport.getName().toString());
                                if (!imported) {
                                    imports.add(aImport);
                                }
                            }
                        }

                        unit.setImports(imports);

                        //6 add nested class if does not exist
                        if (nodes.nestedDeclaration != null) {
                            nodes.nestedDeclaration.setParentNode(unit.getTypes().get(0));
                            unit.getTypes().get(0).getMembers().add(nodes.nestedDeclaration);
                        }
                    }
                }
            }
        }
    }

    private boolean imported(List<ImportDeclaration> imports, String fullyQualifiedName) {
        final String packageName = ClassUtils.getPackageName(fullyQualifiedName);

        for (final ImportDeclaration i : imports) {
            if (!i.isStatic()) {
                final String importName = i.getName().toString();
                if (i.isAsterisk()) {
                    if (packageName.equals(importName)) {
                        final CompilationUnit unit = getCompilationUnit(i);
                        LOG.info("found import " + packageName + ".* on " + unit.getTypes().get(0).getName() + " ignoring.");
                        return true;
                    }
                } else {
                    if (fullyQualifiedName.equals(importName)) {
                        final CompilationUnit unit = getCompilationUnit(i);
                        LOG.info("found import " + fullyQualifiedName + " on " + unit.getTypes().get(0).getName() + " ignoring.");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String getNameFormMessage(final BodyDeclaration n) {
        if (n instanceof TypeDeclaration) {
            return ((TypeDeclaration) n).getName();
        } else if (n instanceof FieldDeclaration) {
            return ((FieldDeclaration) n).getVariables().toString();
        }
        return null;
    }


}
