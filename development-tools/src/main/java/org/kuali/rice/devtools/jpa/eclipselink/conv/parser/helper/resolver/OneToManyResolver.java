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
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.CollectionDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.ObjectReferenceDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OneToManyResolver extends AbstractMappedFieldResolver {
    private static final Log LOG = LogFactory.getLog(OneToManyResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "OneToMany";

    public OneToManyResolver(Collection<DescriptorRepository> descriptorRepositories) {
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

            if (cld.isMtoNRelation()) {
                return null;
            }

            final String[] fkToItemClass = getFksToItemClass(cld);
            if (fkToItemClass != null || fkToItemClass.length != 0) {
                LOG.warn(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a collection descriptor for " + fieldName
                        + " for a 1:M relationship but has fk-pointing-to-element-class configured");
            }

            final String[] fkToThisClass = getFksToThisClass(cld);
            if (fkToThisClass != null || fkToThisClass.length != 0) {
                LOG.warn(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a collection descriptor for " + fieldName
                        + " for a 1:N relationship but does not have any fk-pointing-to-this-class configured");
            }

            final Collection<String> fks = cld.getForeignKeyFields();
            if (fks == null || fks.isEmpty()) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a collection descriptor for " + fieldName
                        + " but does not have any foreign keys configured");
                return null;
            }

            final String className = cld.getItemClassName();
            if (StringUtils.isBlank(className)) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a reference descriptor for " + fieldName
                        + " but does not class name attribute");
            } else {
                final String shortClassName = ClassUtils.getShortClassName(className);
                final String packageName = ClassUtils.getPackageName(className);
                pairs.add(new MemberValuePair("targetEntity", new NameExpr(shortClassName + ".class")));
                additionalImports.add(new ImportDeclaration(new QualifiedNameExpr(new NameExpr(packageName), shortClassName), false, false));
            }

            final boolean proxy = cld.isLazy();
            if (proxy) {
                pairs.add(new MemberValuePair("fetch", new StringLiteralExpr("Fetch.LAZY")));
                additionalImports.add(new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), "FetchType"), false, false));
            }

            final int proxyPfl = cld.getProxyPrefetchingLimit();
            if (proxyPfl > 0) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a proxy prefetch limit of " + proxyPfl + ", unsupported conversion to @OneToOne attributes");
            }

            final boolean refresh = cld.isRefresh();
            if (refresh) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has refresh set to " + refresh + ", unsupported conversion to @OneToOne attributes");
            }

            final List<Expression> cascadeTypes = new ArrayList<Expression>();
            final boolean autoRetrieve = cld.getCascadeRetrieve();
            if (autoRetrieve) {
                cascadeTypes.add(new NameExpr("CascadeType.REFRESH"));
            } else {
                LOG.warn(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has auto-retrieve set to " + autoRetrieve + ", unsupported conversion to CascadeType");
            }

            final int autoDelete = cld.getCascadingDelete();
            if (autoDelete == ObjectReferenceDescriptor.CASCADE_NONE) {
                LOG.warn(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has auto-delete set to none, unsupported conversion to CascadeType");
            } else if (autoDelete == ObjectReferenceDescriptor.CASCADE_LINK) {
                LOG.warn(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has auto-delete set to link, unsupported conversion to CascadeType");
            } else if (autoDelete == ObjectReferenceDescriptor.CASCADE_OBJECT) {
                cascadeTypes.add(new NameExpr("CascadeType.REMOVE"));
            } else {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has auto-delete set to an invalid value");
            }

            final int autoUpdate = cld.getCascadingStore();
            if (autoUpdate == ObjectReferenceDescriptor.CASCADE_NONE) {
                LOG.warn(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has auto-update set to none, unsupported conversion to CascadeType");
            } else if (autoUpdate == ObjectReferenceDescriptor.CASCADE_LINK) {
                LOG.warn(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has auto-update set to link, unsupported conversion to CascadeType");
            } else if (autoUpdate == ObjectReferenceDescriptor.CASCADE_OBJECT) {
                cascadeTypes.add(new NameExpr("CascadeType.PERSIST"));
            } else {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has auto-update set to an invalid value");
            }

            if (!cascadeTypes.isEmpty()) {
                pairs.add(new MemberValuePair("cascade", new ArrayInitializerExpr(cascadeTypes)));
                additionalImports.add(new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), "CascadeType"), false, false));
            }

            return new NodeData(new NormalAnnotationExpr(new NameExpr(SIMPLE_NAME), pairs),
                    new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false),
                    additionalImports);
        }
        return null;
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
