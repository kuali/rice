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
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.Collection;

public class IdResolver extends AbstractMappedFieldResolver {

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "Id";

    public IdResolver(Collection<DescriptorRepository> descriptorRepositories) {
        super(descriptorRepositories);
    }

    @Override
    public String getFullyQualifiedName() {
        return PACKAGE + "." + SIMPLE_NAME;
    }

    @Override
    protected NodeData getAnnotationNodes(String enclosingClass, String fieldName, String mappedClass) {
        final boolean pk = isPrimaryKeyColumn(mappedClass, fieldName);
        if (pk) {
            return new NodeData(new MarkerAnnotationExpr(new NameExpr(SIMPLE_NAME)),
                    new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false));
        }
        return null;
    }

    private boolean isPrimaryKeyColumn(String clazz, String fieldName) {
        final FieldDescriptor fd = OjbUtil.findFieldDescriptor(clazz, fieldName, descriptorRepositories);

        if (fd != null) {
            return fd.isPrimaryKey();
        }
        return false;
    }
}
