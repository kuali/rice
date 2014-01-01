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
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class LobResolver extends AbstractMappedFieldResolver {
    private static final Log LOG = LogFactory.getLog(LobResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "Lob";
    private static final Collection<Class<?>> VALID_TYPES;
    private static final Collection<String> VALID_TYPES_STR;
    static {
        Collection<Class<?>> tempClass = new ArrayList<Class<?>>();
        tempClass.add(String.class);
        tempClass.add(byte[].class);
        tempClass.add(Byte[].class);
        tempClass.add(char[].class);
        tempClass.add(Character[].class);

        Collection<String> tempClassStr = new ArrayList<String>();
        for (Class<?> c : tempClass) {
            tempClassStr.add(c.getName());
        }

        VALID_TYPES = Collections.unmodifiableCollection(tempClass);
        VALID_TYPES_STR = Collections.unmodifiableCollection(tempClassStr);
    }

    public LobResolver(Collection<DescriptorRepository> descriptorRepositories) {
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
            if (isLob(columnType)) {
                if (isValidFieldType(fc)) {
                    return new NodeData(new MarkerAnnotationExpr(new NameExpr(SIMPLE_NAME)),
                            new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false));
                } else {
                    LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " is not a valid field type for the @Lob annotation, must be one of " + VALID_TYPES_STR);
                }
            }

            return null;
        }
        return null;
    }

    private boolean isLob(String columnType) {
        return "BLOB".equals(columnType) || "CLOB".equals(columnType);
    }

    private boolean isValidFieldType(Class<?> type) {
        for (Class<?> c : VALID_TYPES) {
            if (c.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }
}
