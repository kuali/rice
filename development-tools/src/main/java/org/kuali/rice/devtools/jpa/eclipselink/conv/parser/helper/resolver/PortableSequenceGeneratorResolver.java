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

import java.util.Collection;
import java.util.Collections;

public class PortableSequenceGeneratorResolver extends AbstractMappedFieldResolver {
    private static final Log LOG = LogFactory.getLog(PortableSequenceGeneratorResolver.class);

    public static final String PACKAGE = "org.kuali.rice.krad.data.jpa.eclipselink";
    public static final String SIMPLE_NAME = "PortableSequenceGenerator";

    private final boolean upperCaseTableName;

    public PortableSequenceGeneratorResolver(Collection<DescriptorRepository> descriptorRepositories, boolean upperCaseTableName) {
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
            final boolean autoInc = fd.isAutoIncrement();
            final String seqName = fd.getSequenceName();
            if (autoInc && StringUtils.isBlank(seqName)) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has autoincrement set to true but sequenceName is blank.");
            }

            if (!autoInc && StringUtils.isNotBlank(seqName)) {
                LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has autoincrement set to false but sequenceName is " + seqName + ".");
            }
            if (autoInc || StringUtils.isNotBlank(seqName)) {
                return new NodeData(new NormalAnnotationExpr(new NameExpr(SIMPLE_NAME), Collections.singletonList(new MemberValuePair("name", new StringLiteralExpr(upperCaseTableName ? seqName.toUpperCase() : seqName)))),
                        new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false));
            }
        }
        return null;
    }
}
