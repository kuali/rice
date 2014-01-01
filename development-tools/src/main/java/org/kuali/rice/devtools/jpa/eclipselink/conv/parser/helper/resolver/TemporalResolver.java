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
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

public class TemporalResolver extends AbstractMappedFieldResolver {
    private static final Log LOG = LogFactory.getLog(TemporalResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "Temporal";
    public static final String DATE = "DATE";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String TIME = "TIME";

    public TemporalResolver(Collection<DescriptorRepository> descriptorRepositories) {
        super(descriptorRepositories);
    }

    @Override
    public String getFullyQualifiedName() {
        return PACKAGE + "." + SIMPLE_NAME;
    }

    @Override
    protected NodeData getAnnotationNodes(String enclosingClass, String fieldName, String mappedClass) {
        final FieldDescriptor fd = OjbUtil.findFieldDescriptor(mappedClass, fieldName, descriptorRepositories);

        if (fd != null) {
            final Class<?> fc = ResolverUtil.getType(enclosingClass, fieldName);
            final String columnType = fd.getColumnType();
            if (isJavaSqlDate(fc)) {
                LOG.warn(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " is a java.sql.Date. " + getWarnMessageFragment(columnType));
            } else if (isJavaSqlTimestamp(fc)) {
                LOG.warn(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " is a java.sql.Timestamp. " + getWarnMessageFragment(columnType));
            } else if (isJavaSqlTime(fc)) {
                LOG.warn(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " is a java.sql.Time. " + getWarnMessageFragment(columnType));
            } else if (isJavaUtilDate(fc) || isJavaUtilCalendar(fc)) {
                if (DATE.equals(columnType)) {
                    return new NodeData(new SingleMemberAnnotationExpr(new NameExpr(SIMPLE_NAME), new NameExpr("TemporalType.DATE")),
                            new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false),
                            Collections.singletonList(new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), "TemporalType"), false, false)));
                } else if (TIMESTAMP.equals(columnType)) {
                    return new NodeData(new SingleMemberAnnotationExpr(new NameExpr(SIMPLE_NAME), new NameExpr("TemporalType.TIMESTAMP")),
                            new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false),
                            Collections.singletonList(new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), "TemporalType"), false, false)));
                } else if (TIME.equals(columnType)) {
                    return new NodeData(new SingleMemberAnnotationExpr(new NameExpr(SIMPLE_NAME), new NameExpr("TemporalType.TIME")),
                            new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false),
                            Collections.singletonList(new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), "TemporalType"), false, false)));
                }

                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " is a java.sql.Date or java.util.Calendar but the column type " + columnType + " is unknown.  Unable to add @Temporal annotation");
            }

            return null;
        }
        return null;
    }

    private boolean isJavaUtilDate(Class<?> fc) {
        if (fc != null) {
            return !java.sql.Date.class.isAssignableFrom(fc) && java.util.Date.class.isAssignableFrom(fc);
        }
        return false;
    }

    private boolean isJavaUtilCalendar(Class<?> fc) {
        if (fc != null) {
            return Calendar.class.isAssignableFrom(fc);
        }
        return false;
    }

    private boolean isJavaSqlDate(Class<?> fc) {
        if (fc != null) {
            return java.sql.Date.class.isAssignableFrom(fc);
        }
        return false;
    }

    private boolean isJavaSqlTimestamp(Class<?> fc) {
        if (fc != null) {
            return Timestamp.class.isAssignableFrom(fc);
        }
        return false;
    }

    private boolean isJavaSqlTime(Class<?> fc) {
        if (fc != null) {
            return Time.class.isAssignableFrom(fc);
        }
        return false;
    }

    private String getWarnMessageFragment(String columnType) {
        if (DATE.equals(columnType)) {
            return "Consider converting to java.util.Calendar or java.util.Date with a @Temporal(TemporalType.DATE) annotation";
        } else if (TIMESTAMP.equals(columnType)) {
            return "Consider converting to java.util.Calendar or java.util.Date with a @Temporal(TemporalType.TIMESTAMP) annotation";
        } else if (TIME.equals(columnType)) {
            return "Consider converting to java.util.Calendar or java.util.Date with a @Temporal(TemporalType.TIME) annotation";
        } else {
            return "Consider converting to java.util.Calendar or java.util.Date with a @Temporal annotation";
        }
    }
}
