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

import japa.parser.ast.BlockComment;
import japa.parser.ast.Comment;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.Collection;
import java.util.Collections;

public class EnumeratedResolver extends AbstractMappedFieldResolver {
    private static final Log LOG = LogFactory.getLog(EnumeratedResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "Enumerated";

    public EnumeratedResolver(Collection<DescriptorRepository> descriptorRepositories) {
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

            if (isEnum(fc)) {
                final Comment fixme = new BlockComment("\nFIXME:\n" +
                        "Enums must be annotated with the @Enumerated annotation.\n " +
                        "The @Enumerated annotation should set the EnumType.\n" +
                        "If the EnumType is not set, then the Enumerated annotation is defaulted to EnumType.ORDINAL.\n" +
                        "This conversion program cannot tell whether EnumType.ORDINAL is the appropriate EnumType.");
                AnnotationExpr enumerated = new NormalAnnotationExpr(new NameExpr(SIMPLE_NAME), Collections.singletonList(new MemberValuePair("value", new NameExpr("EnumType."))));
                enumerated.setComment(fixme);
                return new NodeData(enumerated, new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false),
                    Collections.singletonList(new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), "TemporalType"), false, false)));
            }
        }
        return null;
    }

    private boolean isEnum(Class<?> fc) {
        if (fc != null) {
            return fc.isEnum();
        }
        return false;
    }
}
