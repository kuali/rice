package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.Level;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.Collection;

public class MappedSuperClassResolver implements AnnotationResolver {
    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "MappedSuperclass";

    private final Collection<DescriptorRepository> descriptorRepositories;

    public MappedSuperClassResolver(Collection<DescriptorRepository> descriptorRepositories) {
        this.descriptorRepositories = descriptorRepositories;
    }

    @Override
    public String getFullyQualifiedName() {
        return PACKAGE + "." + SIMPLE_NAME;
    }

    @Override
    public Level getLevel() {
        return Level.CLASS;
    }

    @Override
    public NodeData resolve(Node node, String mappedClass) {
        if (!(node instanceof ClassOrInterfaceDeclaration)) {
            throw new IllegalArgumentException("this annotation belongs only on ClassOrInterfaceDeclaration");
        }

        final TypeDeclaration dclr = (TypeDeclaration) node;
        if (!(dclr.getParentNode() instanceof CompilationUnit)) {
            //handling nested classes
            return null;
        }

        final String name = dclr.getName();
        final String pckg = ((CompilationUnit) dclr.getParentNode()).getPackage().getName().toString();
        final String enclosingClass = pckg + "." + name;
        if (!enclosingClass.equals(mappedClass)) {
            return new NodeData(new MarkerAnnotationExpr(new NameExpr(SIMPLE_NAME)),
                new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false));
        }
        return null;
    }
}
