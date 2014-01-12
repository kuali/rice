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
package org.kuali.rice.krad.data.jpa.eclipselink;

import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;

import java.util.List;

/**
 * Implementation that takes the query customizer generator and executes the changes on the class descriptor
 * for a field
 */
public class QueryCustomizer {

    public static void customizeField(List<QueryCustomizerGenerator> queryCustomizerGenerators,
            ClassDescriptor descriptor, String propertyName) {

        Expression exp = null;
        ForeignReferenceMapping mapping = null;

        if (OneToOneMapping.class.isAssignableFrom(descriptor.getMappingForAttributeName(propertyName).getClass())) {
            OneToOneMapping databaseMapping = ((OneToOneMapping) descriptor.getMappingForAttributeName(propertyName));
            exp = databaseMapping.buildSelectionCriteria();
            mapping = (ForeignReferenceMapping) databaseMapping;
        } else if (OneToManyMapping.class.isAssignableFrom(descriptor.getMappingForAttributeName(propertyName)
                .getClass())) {
            OneToManyMapping databaseMapping = ((OneToManyMapping) descriptor.getMappingForAttributeName(propertyName));
            exp = databaseMapping.buildSelectionCriteria();
            mapping = (ForeignReferenceMapping) databaseMapping;
        } else {
            throw new RuntimeException("Mapping type not implemented for query customizer for property "+propertyName);
        }

        for (QueryCustomizerGenerator queryCustomizerGenerator : queryCustomizerGenerators) {
            QueryCustomizerOperators operator = queryCustomizerGenerator.operator();
            if(!operator.equals(QueryCustomizerOperators.EQUAL)){
                throw new UnsupportedOperationException("Operator "+operator.getValue()
                        +" not supported in QueryCustomizer");
            }
            String attributeName = queryCustomizerGenerator.attributeName();
            Object attributeValue = queryCustomizerGenerator.attributeValue();
            Class<?> attributeValueClass = queryCustomizerGenerator.attributeResolverClass();

            if (exp != null && mapping != null) {
                ExpressionBuilder builder = exp.getBuilder();
                if (!attributeValueClass.equals(Void.class)) {
                    try {
                        QueryCustomizerValue queryCustomizerValue =
                                (QueryCustomizerValue) attributeValueClass.newInstance();
                        attributeValue = queryCustomizerValue.getValue();
                    } catch (Exception e) {
                        throw new RuntimeException(
                                "Cannot find query customizer attribute class" + attributeValueClass);
                    }
                }

                if (attributeValue != null) {
                    Expression addedExpression = builder.get(attributeName).equal(attributeValue);
                    exp = exp.and(addedExpression);
                    mapping.setSelectionCriteria(exp);
                }
            }
        }
    }
}
