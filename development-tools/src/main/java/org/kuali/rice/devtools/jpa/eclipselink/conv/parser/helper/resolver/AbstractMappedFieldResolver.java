package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.ParserUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.Level;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.Collection;

public abstract class AbstractMappedFieldResolver implements AnnotationResolver {
    protected final Collection<DescriptorRepository> descriptorRepositories;

    public AbstractMappedFieldResolver(Collection<DescriptorRepository> descriptorRepositories) {
        this.descriptorRepositories = descriptorRepositories;
    }

    @Override
    public final Level getLevel() {
        return Level.FIELD;
    }

    @Override
    public final NodeData resolve(Node node, Object arg) {
        if (!(node instanceof FieldDeclaration)) {
            throw new IllegalArgumentException("this annotation belongs only on FieldDeclaration");
        }

        final FieldDeclaration field = (FieldDeclaration) node;

        if (ResolverUtil.canFieldBeAnnotated(field)) {
            final TypeDeclaration dclr = (TypeDeclaration) node.getParentNode();

            final String name = dclr.getName();
            final String pckg = ((CompilationUnit) dclr.getParentNode()).getPackage().getName().toString();
            final String fullyQualifiedClass = pckg + "." + name;
            final boolean mappedColumn = OjbUtil.isMappedColumn(fullyQualifiedClass, ParserUtil.getFieldName(field),
                    descriptorRepositories);
            if (mappedColumn) {
                return getAnnotationNodes(fullyQualifiedClass, ParserUtil.getFieldName(field));
            }
        }
        return null;
    }

    /**
     * Override this method to resolve the annotation data by executing annotation specific rules.
     *
     * @param clazz the parent class name
     * @param fieldName the field name
     * @return annotation node data or null if the annotation should not be created.
     */
    protected abstract NodeData getAnnotationNodes(String clazz, String fieldName);
}
