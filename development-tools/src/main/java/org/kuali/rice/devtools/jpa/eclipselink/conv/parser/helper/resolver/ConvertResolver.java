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
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversionDefaultImpl;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class ConvertResolver extends AbstractMappedFieldResolver {
    private static final Log LOG = LogFactory.getLog(ConvertResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "Convert";

    private Map<String,String> converterMappings;

    public ConvertResolver(Collection<DescriptorRepository> descriptorRepositories, Map<String,String> converterMappings) {
        super(descriptorRepositories);
        this.converterMappings = converterMappings;
    }

    @Override
    public String getFullyQualifiedName() {
        return PACKAGE + "." + SIMPLE_NAME;
    }

    private String getJpaConverterForOjbClass( String ojbConverter ) {
        for ( String key : converterMappings.keySet() ) {
            // Substring match
            if ( ojbConverter.contains(key) ) {
                return converterMappings.get(key);
            }
        }
        return null;
    }
    
    /** gets the annotation but also adds an import in the process if a Convert annotation is required. */
    @Override
    protected NodeData getAnnotationNodes(String enclosingClass, String fieldName, String mappedClass) {
        final FieldDescriptor fd = OjbUtil.findFieldDescriptor(mappedClass, fieldName, descriptorRepositories);

        if (fd != null) {
            final FieldConversion fc = fd.getFieldConversion();
            //in ojb all columns have at least the default field conversion
            if (fc != null && FieldConversionDefaultImpl.class != fc.getClass()) {
                LOG.info(enclosingClass + "." + fieldName + " for the mapped class " + mappedClass + " field has a converter " + fc.getClass().getName());

                final String jpaConverter = getJpaConverterForOjbClass(fc.getClass().getName());
                if (jpaConverter == null) {
                    LOG.error(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a converter " + fc.getClass().getName()
                        + " but a replacement converter was not configured, unable to set Convert class");
                    return new NodeData(new NormalAnnotationExpr(new NameExpr(SIMPLE_NAME), Collections.singletonList(new MemberValuePair("converter", new NameExpr(null)))),
                            new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false));
                } else if ( StringUtils.isBlank(jpaConverter) ) {
                    LOG.info(ResolverUtil.logMsgForField(enclosingClass, fieldName, mappedClass) + " field has a converter " + fc.getClass().getName()
                            + " But no converter definition is needed due to default converter configuration." );
                } else {
                    final String shortClassName = ClassUtils.getShortClassName(jpaConverter);
                    final String packageName = ClassUtils.getPackageName(jpaConverter);
                    return new NodeData(new NormalAnnotationExpr(new NameExpr(SIMPLE_NAME),  Collections.singletonList(new MemberValuePair("converter", new NameExpr(shortClassName + ".class")))),
                            new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false),
                            Collections.singletonList(new ImportDeclaration(new QualifiedNameExpr(new NameExpr(packageName), shortClassName), false, false)));
                }
            }
        }
        return null;
    }
}
