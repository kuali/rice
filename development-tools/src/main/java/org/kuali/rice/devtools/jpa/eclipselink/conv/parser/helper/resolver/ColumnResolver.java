package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import org.apache.commons.lang.StringUtils;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ColumnResolver implements AnnotationResolver {
    private static final Log LOG = LogFactory.getLog(ColumnResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "Column";

    private final Collection<DescriptorRepository> descriptorRepositories;

    public ColumnResolver(Collection<DescriptorRepository> descriptorRepositories) {
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
    public NodeData resolve(Node node, Object arg) {
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
            List<MemberValuePair> pairs = new ArrayList<MemberValuePair>();

            FieldDescriptor fd = cd.getFieldDescriptorByName(fieldName);
            final String access = fd.getAccess();
            if ("readonly".equals(access)) {
                pairs.add(new MemberValuePair("insertable", new BooleanLiteralExpr(false)));
                pairs.add(new MemberValuePair("updatable", new BooleanLiteralExpr(false)));
            } else if ("readwrite".equals(access)) {
                LOG.info(clazz + "." + fieldName + " field access is readwrite keeping @Column attributes (insertable, updatable) at defaults");
            } else if (access == null) {
                LOG.info(clazz + "." +  fieldName + " field access is null keeping @Column attributes (insertable, updatable) at defaults");
            } else {
                LOG.error(clazz + "." + fieldName + " field access is " + access + ", unsupported conversion to @Column attributes");
            }

            final String columnName = fd.getColumnName();
            if (StringUtils.isNotBlank(columnName)) {
                pairs.add(new MemberValuePair("name", new StringLiteralExpr(columnName)));
            } else {
                LOG.error(clazz + "." + fieldName + " field column is blank");
            }

            final String columnType = fd.getColumnType();
            if (StringUtils.isNotBlank(columnType)) {
                LOG.error(clazz + "." + fieldName + " field column type is " + columnType + ", unsupported conversion to @Column attributes");
            }

            final boolean required = fd.isRequired();
            if (required) {
                pairs.add(new MemberValuePair("nullable", new BooleanLiteralExpr(false)));
            } else {
                LOG.info(clazz + "." + fieldName + " field is nullable keeping @Column attribute (nullable) at default");
            }

            final int length = fd.getLength();
            if (length > 0) {
                pairs.add(new MemberValuePair("length", new IntegerLiteralExpr(String.valueOf(length))));
            } else {
                LOG.info(clazz + "." + fieldName + " field length is not set keeping @Column attribute (length) at default");
            }

            final int precision = fd.getPrecision();
            if (precision > 0) {
                pairs.add(new MemberValuePair("precision", new IntegerLiteralExpr(String.valueOf(precision))));
            } else {
                LOG.info(clazz + "." + fieldName + " field precision is not set keeping @Column attribute (precision) at default");
            }

            final int scale = fd.getScale();
            if (scale > 0) {
                pairs.add(new MemberValuePair("scale", new IntegerLiteralExpr(String.valueOf(scale))));
            } else {
                LOG.info(clazz + "." + fieldName + " field scale is not set keeping @Column attribute (scale) at default");
            }
            return new NodeData(new NormalAnnotationExpr(new NameExpr(SIMPLE_NAME), pairs),
                    new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false));
        }
        return null;
    }
}
