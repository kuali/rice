/**
 * Copyright 2005-2013 The Kuali Foundation
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
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.metadata.ObjectReferenceDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * this only does mappings for OneToOne at the field level for compound fks.
 */
public abstract class AbstractPrimaryKeyJoinColumnResolver extends AbstractMappedFieldResolver {
    private static final Log LOG = LogFactory.getLog(AbstractPrimaryKeyJoinColumnResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "PrimaryKeyJoinColumns";

    public AbstractPrimaryKeyJoinColumnResolver(Collection<DescriptorRepository> descriptorRepositories) {
        super(descriptorRepositories);
    }
    @Override
    public String getFullyQualifiedName() {
        return PACKAGE + "." + SIMPLE_NAME;
    }

    protected final List<Expression> getJoinColumns(String enclosingClass, String fieldName, String mappedClass) {
        final ObjectReferenceDescriptor ord = OjbUtil.findObjectReferenceDescriptor(mappedClass, fieldName,
                descriptorRepositories);

        final List<Expression> joinColumns = new ArrayList<Expression>();

        if (ord != null) {

            final Collection<ImportDeclaration> additionalImports = new ArrayList<ImportDeclaration>();

            final Collection<String> fks = ord.getForeignKeyFields();
            if (fks == null || fks.isEmpty()) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a reference descriptor for " + fieldName
                        + " but does not have any foreign keys configured");
                return null;
            }

            final Collection<String> pks = OjbUtil.getPrimaryKeyNames(mappedClass, descriptorRepositories);

            if (pks.size() == fks.size() && pks.containsAll(fks) && !pks.isEmpty()) {

                final ClassDescriptor cd = OjbUtil.findClassDescriptor(mappedClass, descriptorRepositories);
                final ClassDescriptor icd;

                final String itemClassName = ord.getItemClassName();
                if (StringUtils.isBlank(itemClassName)) {
                    LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a reference descriptor for " + fieldName
                            + " but does not class name attribute");
                    return null;
                } else {
                    icd = OjbUtil.findClassDescriptor(itemClassName, descriptorRepositories);
                }

                final FieldDescriptor[] pfds = cd.getPkFields();
                final FieldDescriptor[] ipfds = icd.getPkFields();
                for (int i = 0; i < pfds.length; i++) {
                    final List<MemberValuePair> pairs = new ArrayList<MemberValuePair>();
                    pairs.add(new MemberValuePair("name", new StringLiteralExpr(pfds[i].getColumnName())));
                    pairs.add(new MemberValuePair("referencedColumnName", new StringLiteralExpr(ipfds[i].getColumnName())));
                    joinColumns.add(new NormalAnnotationExpr(new NameExpr("PrimaryKeyJoinColumn"), pairs));
                }
            }
        }
        return joinColumns;
    }
}