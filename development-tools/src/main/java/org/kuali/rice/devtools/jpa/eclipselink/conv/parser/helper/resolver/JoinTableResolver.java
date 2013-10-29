package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.CollectionDescriptor;
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

/**
 * Join Table annotations can be used by many associations besides M:N however we are only using it for M:N.
 */
public class JoinTableResolver implements AnnotationResolver {
    private static final Log LOG = LogFactory.getLog(JoinTableResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "JoinTable";

    private final Collection<DescriptorRepository> descriptorRepositories;

    public JoinTableResolver(Collection<DescriptorRepository> descriptorRepositories) {
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
                return getAnnotationNodes(fullyQualifiedClass, ParserUtil.getFieldName(field), dclr);
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
            return cd.getCollectionDescriptorByName(fieldName) != null;
        }
        return false;
    }

    /** gets the annotation but also adds an import in the process if a Convert annotation is required. */
    private NodeData getAnnotationNodes(String clazz, String fieldName, TypeDeclaration dclr) {
        final ClassDescriptor cd = OjbUtil.findClassDescriptor(clazz, descriptorRepositories);
        if (cd != null) {
            List<MemberValuePair> pairs = new ArrayList<MemberValuePair>();
            Collection<ImportDeclaration> additionalImports = new ArrayList<ImportDeclaration>();

            final CollectionDescriptor cld = cd.getCollectionDescriptorByName(fieldName);
            if (cld != null) {
                boolean error = false;
                if (!cld.isMtoNRelation()) {
                    error = true;
                }

                final String joinTable = cld.getIndirectionTable();
                if (StringUtils.isBlank(joinTable)) {
                    LOG.error(clazz + "." + fieldName + " field has a collection descriptor for " + fieldName
                            + " for a M:N relationship but does not have an indirection table configured");
                    error = true;
                } else {
                    pairs.add(new MemberValuePair("name", new StringLiteralExpr(joinTable)));
                }

                final String[] fkToItemClass = getFksToItemClass(cld);
                if (fkToItemClass == null || fkToItemClass.length == 0) {
                    LOG.error(clazz + "." + fieldName + " field has a collection descriptor for " + fieldName
                            + " for a M:N relationship but does not have any fk-pointing-to-element-class configured");
                    error = true;
                }

                final String itemClassName = cld.getItemClassName();
                if (StringUtils.isBlank(itemClassName)) {
                    LOG.error(clazz + "." + fieldName + " field has a reference descriptor for " + fieldName
                            + " but does not class name attribute");
                    error = true;
                }

                if (error) {
                    return null;
                }


                final ClassDescriptor itemCd = OjbUtil.findClassDescriptor(itemClassName, descriptorRepositories);

                final List<Expression> joinColumns = new ArrayList<Expression>();
                for (String fk : fkToItemClass) {
                    final List<MemberValuePair> joinColumnsPairs = new ArrayList<MemberValuePair>();
                    joinColumnsPairs.add(new MemberValuePair("name", new StringLiteralExpr(fk)));
                    joinColumnsPairs.add(new MemberValuePair("referencedColumnName", new StringLiteralExpr(getPksAsString(itemCd.getPkFields()))));
                    joinColumns.add(new NormalAnnotationExpr(new NameExpr("JoinColumn"), joinColumnsPairs));
                }
                pairs.add(new MemberValuePair("joinColumns", new ArrayInitializerExpr(joinColumns)));
                additionalImports.add(new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), "JoinColumn"), false, false));

                final String[] fkToThisClass = getFksToThisClass(cld);
                if (fkToThisClass == null || fkToThisClass.length == 0) {
                    LOG.error(clazz + "." + fieldName + " field has a collection descriptor for " + fieldName
                            + " for a M:N relationship but does not have any fk-pointing-to-this-class configured");
                    return null;
                } else {
                    final List<Expression> invJoinColumns = new ArrayList<Expression>();
                    for (String fk : fkToItemClass) {
                        final List<MemberValuePair> invJoinColumnsPairs = new ArrayList<MemberValuePair>();
                        invJoinColumnsPairs.add(new MemberValuePair("name", new StringLiteralExpr(fk)));
                        invJoinColumnsPairs.add(new MemberValuePair("referencedColumnName", new StringLiteralExpr(getPksAsString(cd.getPkFields()))));
                        invJoinColumns.add(new NormalAnnotationExpr(new NameExpr("JoinColumn"), invJoinColumnsPairs));
                    }
                    pairs.add(new MemberValuePair("inverseJoinColumns", new ArrayInitializerExpr(invJoinColumns)));
                }


                final Collection<String> fks = cld.getForeignKeyFields();
                if (fks != null || !fks.isEmpty()) {
                    LOG.warn(clazz + "." + fieldName + " field has a collection descriptor for " + fieldName
                            + " for a M:N relationship but has the inverse-foreignkey configured as opposed to "
                            + "fk-pointing-to-this-class and fk-pointing-to-element-class");
                }

                return new NodeData(new NormalAnnotationExpr(new NameExpr(SIMPLE_NAME), pairs),
                        new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false),
                        additionalImports);
            }
        }
        return null;
    }

    private String getPksAsString(FieldDescriptor[] descriptors) {
        if (descriptors.length == 1) {
            return descriptors[0].getColumnName();
        }
        String pks = "";
        for (FieldDescriptor d : descriptors) {
            pks += d.getColumnName() + "|";
        }
        return pks;
    }

    private String[] getFksToItemClass(CollectionDescriptor cld) {
        try {
            return cld.getFksToItemClass();
        } catch (NullPointerException e) {
            return new String[] {};
        }
    }

    private String [] getFksToThisClass(CollectionDescriptor cld) {
        try {
            return cld.getFksToItemClass();
        } catch (NullPointerException e) {
            return new String[] {};
        }
    }
}
