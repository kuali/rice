package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.ParserUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.Collection;

public abstract class AbstractGeneratedResolver implements AnnotationResolver {
    protected final Collection<DescriptorRepository> descriptorRepositories;

    public AbstractGeneratedResolver(Collection<DescriptorRepository> descriptorRepositories) {
        this.descriptorRepositories = descriptorRepositories;
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

    protected abstract NodeData getAnnotationNodes(String clazz, String fieldName);
}
