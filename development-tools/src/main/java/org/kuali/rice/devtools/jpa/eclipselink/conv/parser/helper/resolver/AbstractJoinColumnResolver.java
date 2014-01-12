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

import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.CollectionDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.metadata.ObjectReferenceDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractJoinColumnResolver extends AbstractMappedFieldResolver {
    private static final Log LOG = LogFactory.getLog(AbstractJoinColumnResolver.class);

    public AbstractJoinColumnResolver(Collection<DescriptorRepository> descriptorRepositories) {
        super(descriptorRepositories);
    }

    protected final List<Expression> getJoinColumns(String enclosingClass, String fieldName, String mappedClass) {
        final ObjectReferenceDescriptor ord = OjbUtil.findObjectReferenceDescriptor(mappedClass, fieldName,
                descriptorRepositories);

        final CollectionDescriptor cld = OjbUtil.findCollectionDescriptor(mappedClass, fieldName,
                descriptorRepositories);

        if (ord != null) {
            return processReferenceField(enclosingClass, fieldName, mappedClass, ord);
        } else if (cld != null) {
            return processCollectionField(enclosingClass, fieldName, mappedClass, cld);
        }

        return Collections.emptyList();
    }

    private List<Expression> processReferenceField(String enclosingClass, String fieldName, String mappedClass, ObjectReferenceDescriptor ord) {
        final List<Expression> joinColumns = new ArrayList<Expression>();
        final Collection<String> fks = ord.getForeignKeyFields();
        if (fks == null || fks.isEmpty()) {
            LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a reference descriptor for " + fieldName
                    + " but does not have any foreign keys configured");
            return joinColumns;
        }

        final Collection<String> pks = OjbUtil.getPrimaryKeyNames(mappedClass, descriptorRepositories);

        //make sure it isn't a one to one
        if (!(pks.containsAll(fks) && fks.containsAll(pks)) && !pks.isEmpty()) {

            final ClassDescriptor cd = OjbUtil.findClassDescriptor(mappedClass, descriptorRepositories);
            final ClassDescriptor icd = getItemClassDescriptor(enclosingClass, fieldName, mappedClass, ord);
            final FieldDescriptor[] fkDescs = ord.getForeignKeyFieldDescriptors(cd);
            final FieldDescriptor[] pkDescs = icd.getPkFields();

            if (fkDescs.length != pkDescs.length) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a collection descriptor for " + fieldName
                        + " with an foreign key that is not joined to all of the primary key fields. This is not supported in JPA.");
            }

            for (int i = 0; i < fkDescs.length; i ++) {
                joinColumns.add(createJoinColumn(fkDescs[i], pkDescs[i]));
            }
        }
        return joinColumns;
    }

    private List<Expression> processCollectionField(String enclosingClass, String fieldName, String mappedClass, CollectionDescriptor cld) {
        final List<Expression> joinColumns = new ArrayList<Expression>();

        if (!cld.isMtoNRelation()) {
            final Collection<String> fks = cld.getForeignKeyFields();
            if (fks == null || fks.isEmpty()) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a collection descriptor for " + fieldName
                        + " but does not have any inverse foreign keys configured");
                return joinColumns;
            }

            final ClassDescriptor cd = OjbUtil.findClassDescriptor(mappedClass, descriptorRepositories);
            final ClassDescriptor icd = getItemClassDescriptor(enclosingClass, fieldName, mappedClass, cld);
            final FieldDescriptor[] fkDescs =  cld.getForeignKeyFieldDescriptors(icd);
            final FieldDescriptor[] pkDescs = cd.getPkFields();

            if (fkDescs.length != pkDescs.length) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a collection descriptor for " + fieldName
                + " with an inverse foreign key that is not joined to all of the primary key fields.  This is not supported in JPA.");
            }

            for (int i = 0; i < fkDescs.length; i ++) {
                joinColumns.add(createJoinColumn(pkDescs[i], fkDescs[i]));
            }
        }
        return joinColumns;
    }

    private AnnotationExpr createJoinColumn(FieldDescriptor thisField, FieldDescriptor itemField) {
        final List<MemberValuePair> pairs = new ArrayList<MemberValuePair>();

        pairs.add(new MemberValuePair("name", new StringLiteralExpr(thisField.getColumnName())));
        pairs.add(new MemberValuePair("referencedColumnName", new StringLiteralExpr(itemField.getColumnName())));
        if (!isAnonymousFk(thisField)) {
            pairs.add(new MemberValuePair("insertable", new BooleanLiteralExpr(false)));
            pairs.add(new MemberValuePair("updatable", new BooleanLiteralExpr(false)));
        }

        // Per this page: https://forums.oracle.com/message/3923913
        // the nullable attribute is a hint to the DDL generation, especially on fields like this.
        // Commenting this flag out for now as it's just "noise" in the annotation definitions
//        if (!isNullableFk(thisField)) {
//            pairs.add(new MemberValuePair("nullable", new BooleanLiteralExpr(false)));
//        }
        return new NormalAnnotationExpr(new NameExpr("JoinColumn"), pairs);
    }

    private ClassDescriptor getItemClassDescriptor(String enclosingClass, String fieldName, String mappedClass, CollectionDescriptor cld) {
        return getItemClassDescriptor(enclosingClass, fieldName, mappedClass, cld.getItemClassName());
    }

    private ClassDescriptor getItemClassDescriptor(String enclosingClass, String fieldName, String mappedClass, ObjectReferenceDescriptor ord) {
        return getItemClassDescriptor(enclosingClass, fieldName, mappedClass, ord.getItemClassName());
    }

    private ClassDescriptor getItemClassDescriptor(String enclosingClass, String fieldName, String mappedClass, String itemClassName) {
        if (StringUtils.isBlank(itemClassName)) {
            LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a reference descriptor for " + fieldName
                    + " but does not class name attribute");
            return null;
        } else {
            return OjbUtil.findClassDescriptor(itemClassName, descriptorRepositories);
        }
    }

    private boolean isAnonymousFk(FieldDescriptor fd) {
        if (fd != null) {
            return "anonymous".equals(fd.getAccess());
        }
        return false;
    }

    private boolean isNullableFk(FieldDescriptor fd) {
        if (fd != null) {
            return fd.isRequired();
        }
        return false;
    }
}