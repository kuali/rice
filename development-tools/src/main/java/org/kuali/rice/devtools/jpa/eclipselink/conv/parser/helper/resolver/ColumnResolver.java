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
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ColumnResolver extends AbstractMappedFieldResolver {
    private static final Log LOG = LogFactory.getLog(ColumnResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "Column";

    private final boolean upperCaseTableName;

    public ColumnResolver(Collection<DescriptorRepository> descriptorRepositories, boolean upperCaseTableName) {
        super(descriptorRepositories);
        this.upperCaseTableName = upperCaseTableName;
    }

    @Override
    public String getFullyQualifiedName() {
        return PACKAGE + "." + SIMPLE_NAME;
    }

    @Override
    protected NodeData getAnnotationNodes(String enclosingClass, String fieldName, String mappedClass) {
        final FieldDescriptor fd = OjbUtil.findFieldDescriptor(mappedClass, fieldName, descriptorRepositories);

        if (fd != null) {
            List<MemberValuePair> pairs = new ArrayList<MemberValuePair>();
            final String access = fd.getAccess();
            if ("readonly".equals(access)) {
                pairs.add(new MemberValuePair("insertable", new BooleanLiteralExpr(false)));
                pairs.add(new MemberValuePair("updatable", new BooleanLiteralExpr(false)));
            } else if ("readwrite".equals(access)) {
                LOG.debug(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field access is readwrite keeping @Column attributes (insertable, updatable) at defaults");
            } else if ("anonymous".equals(access)) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field access is anonymous, the field should not exist in the java class as is the meaning anonymous access");
            } else if (access == null) {
                LOG.debug(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field access is null keeping @Column attributes (insertable, updatable) at defaults");
            } else {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field access is " + access + ", unsupported conversion to @Column attributes");
            }

            final String columnName = fd.getColumnName();
            if (StringUtils.isNotBlank(columnName)) {
                pairs.add(new MemberValuePair("name", new StringLiteralExpr(upperCaseTableName ? columnName.toUpperCase() : columnName)));
            } else {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field column is blank");
            }
            /*  don't bother with column type attribute...this is mostly taken care of automatically by JPA
            final String columnType = fd.getColumnType();
            if (StringUtils.isNotBlank(columnType)) {
                LOG.error(enclosingClass + "." + fieldName + " for the mapped class " + mappedClass + " field column type is " + columnType + ", unsupported conversion to @Column attributes");
            }
            */
            final boolean required = fd.isRequired();
            if (required) {
                pairs.add(new MemberValuePair("nullable", new BooleanLiteralExpr(false)));
            } else {
                LOG.debug(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field is nullable keeping @Column attribute (nullable) at default");
            }

            final int length = fd.getLength();
            if (length > 0) {
                pairs.add(new MemberValuePair("length", new IntegerLiteralExpr(String.valueOf(length))));
            } else {
                LOG.debug(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field length is not set keeping @Column attribute (length) at default");
            }

            final int precision = fd.getPrecision();
            if (precision > 0) {
                pairs.add(new MemberValuePair("precision", new IntegerLiteralExpr(String.valueOf(precision))));
            } else {
                LOG.debug(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field precision is not set keeping @Column attribute (precision) at default");
            }

            final int scale = fd.getScale();
            if (scale > 0) {
                pairs.add(new MemberValuePair("scale", new IntegerLiteralExpr(String.valueOf(scale))));
            } else {
                LOG.debug(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field scale is not set keeping @Column attribute (scale) at default");
            }
            return new NodeData(new NormalAnnotationExpr(new NameExpr(SIMPLE_NAME), pairs),
                    new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false));
        }
        return null;
    }
}
