/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.ImportDeclaration;
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
import org.apache.ojb.broker.metadata.CollectionDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Join Table annotations can be used by many associations besides M:N however we are only using it for M:N.
 */
public class JoinTableResolver extends AbstractMappedFieldResolver {
    private static final Log LOG = LogFactory.getLog(JoinTableResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "JoinTable";

    public JoinTableResolver(Collection<DescriptorRepository> descriptorRepositories) {
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
            final List<MemberValuePair> pairs = new ArrayList<MemberValuePair>();
            final Collection<ImportDeclaration> additionalImports = new ArrayList<ImportDeclaration>();

            if (!cld.isMtoNRelation()) {
                return null;
            }

            boolean error = false;
            final String joinTable = cld.getIndirectionTable();
            if (StringUtils.isBlank(joinTable)) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a collection descriptor for " + fieldName
                        + " for a M:N relationship but does not have an indirection table configured");
                error = true;
            } else {
                pairs.add(new MemberValuePair("name", new StringLiteralExpr(joinTable)));
            }

            final String[] fkToItemClass = getFksToItemClass(cld);
            if (fkToItemClass == null || fkToItemClass.length == 0) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a collection descriptor for " + fieldName
                        + " for a M:N relationship but does not have any fk-pointing-to-element-class configured");
                error = true;
            }

            final String itemClassName = cld.getItemClassName();
            if (StringUtils.isBlank(itemClassName)) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a reference descriptor for " + fieldName
                        + " but does not class name attribute");
                error = true;
            }

            if (error) {
                return null;
            }

            final List<Expression> joinColumns = new ArrayList<Expression>();
            for (String fk : fkToItemClass) {
                final List<MemberValuePair> joinColumnsPairs = new ArrayList<MemberValuePair>();
                joinColumnsPairs.add(new MemberValuePair("name", new StringLiteralExpr(fk)));
                final Collection<String> pks = OjbUtil.getPrimaryKeyNames(itemClassName, descriptorRepositories);
                joinColumnsPairs.add(new MemberValuePair("referencedColumnName", new StringLiteralExpr(getPksAsString(pks))));
                joinColumns.add(new NormalAnnotationExpr(new NameExpr("JoinColumn"), joinColumnsPairs));
            }
            pairs.add(new MemberValuePair("joinColumns", new ArrayInitializerExpr(joinColumns)));
            additionalImports.add(new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), "JoinColumn"), false, false));

            final String[] fkToThisClass = getFksToThisClass(cld);
            if (fkToThisClass == null || fkToThisClass.length == 0) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a collection descriptor for " + fieldName
                        + " for a M:N relationship but does not have any fk-pointing-to-this-class configured");
                return null;
            } else {
                final List<Expression> invJoinColumns = new ArrayList<Expression>();
                for (String fk : fkToItemClass) {
                    final List<MemberValuePair> invJoinColumnsPairs = new ArrayList<MemberValuePair>();
                    invJoinColumnsPairs.add(new MemberValuePair("name", new StringLiteralExpr(fk)));
                    final Collection<String> pks = OjbUtil.getPrimaryKeyNames(mappedClass, descriptorRepositories);
                    invJoinColumnsPairs.add(new MemberValuePair("referencedColumnName", new StringLiteralExpr(
                            getPksAsString(pks))));
                    invJoinColumns.add(new NormalAnnotationExpr(new NameExpr("JoinColumn"), invJoinColumnsPairs));
                }
                pairs.add(new MemberValuePair("inverseJoinColumns", new ArrayInitializerExpr(invJoinColumns)));
            }


            final Collection<String> fks = cld.getForeignKeyFields();
            if (fks != null || !fks.isEmpty()) {
                LOG.warn(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a collection descriptor for " + fieldName
                        + " for a M:N relationship but has the inverse-foreignkey configured as opposed to "
                        + "fk-pointing-to-this-class and fk-pointing-to-element-class");
            }

            return new NodeData(new NormalAnnotationExpr(new NameExpr(SIMPLE_NAME), pairs),
                    new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false),
                    additionalImports);
        }
        return null;
    }

    private String getPksAsString(Collection<String> descriptors) {
        if (descriptors.size() == 1) {
            return descriptors.iterator().next();
        }
        String pks = "";
        for (String d : descriptors) {
            pks += d + "|";
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

    private String[] getFksToThisClass(CollectionDescriptor cld) {
        try {
            return cld.getFksToItemClass();
        } catch (NullPointerException e) {
            return new String[] {};
        }
    }
}
