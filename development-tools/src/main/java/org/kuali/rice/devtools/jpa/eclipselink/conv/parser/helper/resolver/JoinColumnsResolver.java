package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.BlockComment;
import japa.parser.ast.Comment;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JoinColumnsResolver extends AbstractJoinColumnResolver {
    private static final Log LOG = LogFactory.getLog(JoinColumnsResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "JoinColumns";

    public JoinColumnsResolver(Collection<DescriptorRepository> descriptorRepositories) {
        super(descriptorRepositories);
    }
    @Override
    public String getFullyQualifiedName() {
        return PACKAGE + "." + SIMPLE_NAME;
    }

    @Override
    protected NodeData getAnnotationNodes(String enclosingClass, String fieldName, String mappedClass) {
        final List<Expression> joinColumns = getJoinColumns(enclosingClass, fieldName, mappedClass);
        if (joinColumns != null && joinColumns.size() > 1) {
            final Comment fixme = new BlockComment("\nFIXME:\n"
                    + "For compound primary keys, make sure the join columns are in the correct order.\n");
            AnnotationExpr
                    annotation = new SingleMemberAnnotationExpr(new NameExpr(SIMPLE_NAME), new ArrayInitializerExpr(joinColumns));
            annotation.setComment(fixme);
            return new NodeData(annotation,
                    new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false),
                    Collections.singletonList(new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE),
                            "PrimaryKeyJoinColumn"), false, false)));
        }

        return null;
    }
}
