package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.CollectionDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldHelper;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrderByResolver extends AbstractMappedFieldResolver {
    private static final Log LOG = LogFactory.getLog(OrderByResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "OrderBy";

    public OrderByResolver(Collection<DescriptorRepository> descriptorRepositories) {
        super(descriptorRepositories);
    }
    @Override
    public String getFullyQualifiedName() {
        return PACKAGE + "." + SIMPLE_NAME;
    }

    /** gets the annotation but also adds an import in the process if a Convert annotation is required. */
    @Override
    protected NodeData getAnnotationNodes(String enclosingClass, String fieldName, String mappedClass) {
        final CollectionDescriptor cld = OjbUtil.findCollectionDescriptor(mappedClass, fieldName, descriptorRepositories);
        if (cld != null) {
            Collection<FieldHelper> orderBy = cld.getOrderBy();
            if (orderBy != null && !orderBy.isEmpty()) {
                String orderByStr = "";
                for (FieldHelper fh : orderBy) {
                    orderByStr += fh.name + (fh.isAscending ? "" : " DESC") + ", ";
                }
                orderByStr = orderByStr.replaceAll(", $", "");
                return new NodeData(new SingleMemberAnnotationExpr(new NameExpr(SIMPLE_NAME), new StringLiteralExpr(orderByStr)),
                        new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false));
            }
        }
        return null;
    }
}
