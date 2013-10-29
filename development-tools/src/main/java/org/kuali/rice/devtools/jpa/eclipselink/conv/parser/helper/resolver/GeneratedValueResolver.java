package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.Collection;

public class GeneratedValueResolver extends AbstractMappedFieldResolver {
    private static final Log LOG = LogFactory.getLog(GeneratedValueResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "GeneratedValue";

    public GeneratedValueResolver(Collection<DescriptorRepository> descriptorRepositories) {
        super(descriptorRepositories);
    }

    @Override
    public String getFullyQualifiedName() {
        return PACKAGE + "." + SIMPLE_NAME;
    }

    @Override
    protected NodeData getAnnotationNodes(String clazz, String fieldName) {
        final FieldDescriptor fd = OjbUtil.findFieldDescriptor(clazz, fieldName, descriptorRepositories);

        if (fd != null) {
            final boolean autoInc = fd.isAutoIncrement();
            final String seqName = fd.getSequenceName();
            if (autoInc && StringUtils.isBlank(seqName)) {
                LOG.error(clazz + "." + fieldName + " field has autoincrement set to true but sequenceName is blank.");
            }

            if (!autoInc && StringUtils.isNotBlank(seqName)) {
                LOG.error(clazz + "." + fieldName + " field has autoincrement set to false but sequenceName is " + seqName + ".");
            }
            if (autoInc || StringUtils.isNotBlank(seqName)) {
                return new NodeData(new MarkerAnnotationExpr(new NameExpr(SIMPLE_NAME)),
                        new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false));
            }
        }
        return null;
    }
}
