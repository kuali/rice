package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.ParserUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.Level;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.Collection;

public class VersionResolver implements AnnotationResolver {
    private static final Log LOG = LogFactory.getLog(VersionResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "Version";

    private final Collection<DescriptorRepository> descriptorRepositories;

    public VersionResolver(Collection<DescriptorRepository> descriptorRepositories) {
        this.descriptorRepositories = descriptorRepositories;
    }

    @Override
    public String getFullyQualifiedName() {
        return PACKAGE + "." + SIMPLE_NAME;
    }

    @Override
    public Level getLevel() {
        return Level.FIELD;
    }

    @Override
    public NodeData resolve(Collection<DescriptorRepository> drs, Node node, Object arg) {
        if (!(node instanceof FieldDeclaration)) {
            throw new IllegalArgumentException("this annotation belongs only on FieldDeclaration");
        }

        final FieldDeclaration field = (FieldDeclaration) node;

        if (canBeAnnotated(field)) {
            final TypeDeclaration dclr = (TypeDeclaration) node.getParentNode();
            if (!(dclr.getParentNode() instanceof CompilationUnit)) {
                //handling nested classes
                return null;
            }
            final String name = dclr.getName();
            final String pckg = ((CompilationUnit) dclr.getParentNode()).getPackage().getName().toString();
            final String fullyQualifiedClass = pckg + "." + name;
            final boolean mappedColumn = isMappedColumn(fullyQualifiedClass, ParserUtil.getFieldName(field));
            if (mappedColumn) {
                return getAnnotationNodes(fullyQualifiedClass, ParserUtil.getFieldName(field));
            }
        }
        return null;
    }

    private boolean canBeAnnotated(FieldDeclaration node) {
        return !ModifierSet.isStatic(node.getModifiers());
    }

    private boolean isMappedColumn(String clazz, String fieldName) {
        final ClassDescriptor cd = OjbUtil.findClassDescriptor(clazz, descriptorRepositories);
        if (cd != null) {
            return cd.getFieldDescriptorByName(fieldName) != null;
        }
        return false;
    }

    private NodeData getAnnotationNodes(String clazz, String fieldName) {
        final ClassDescriptor cd = OjbUtil.findClassDescriptor(clazz, descriptorRepositories);
        if (cd != null) {
            final FieldDescriptor fd = cd.getFieldDescriptorByName(fieldName);
            if (fd.isLocking()) {
                return new NodeData(new MarkerAnnotationExpr(new NameExpr(SIMPLE_NAME)),
                        new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false));
            }
        }
        return null;
    }
}
