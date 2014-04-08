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
package org.kuali.rice.krad.data.jpa;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.List;

/**
 * Takes a filter generator and executes the changes on the class descriptor for a field.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Filter {

    private static final Logger LOG = LoggerFactory.getLogger(Filter.class);

    /**
     * Takes a list of filter generators and executes the changes on the class descriptor for a field.
     *
     * @param filterGenerators a list of filter generators.
     * @param descriptor the class descriptor to execute the changes on.
     * @param propertyName the property name of the field to change.
     */
    public static void customizeField(List<FilterGenerator> filterGenerators,
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

        for (FilterGenerator filterGenerator : filterGenerators) {
            FilterOperators operator = filterGenerator.operator();
            if(!operator.equals(FilterOperators.EQUAL)){
                throw new UnsupportedOperationException("Operator "+operator.getValue()
                        +" not supported in Filter");
            }
            String attributeName = filterGenerator.attributeName();
            Object attributeValue = coerce(mapping.getReferenceClass(), attributeName, filterGenerator.attributeValue());
            Class<?> attributeValueClass = filterGenerator.attributeResolverClass();

            if (exp != null && mapping != null) {
                ExpressionBuilder builder = exp.getBuilder();
                if (!attributeValueClass.equals(Void.class)) {
                    try {
                        FilterValue filterValue =
                                (FilterValue) attributeValueClass.newInstance();
                        attributeValue = filterValue.getValue();
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

    /**
     * Coerces the {@code attributeValue} for the {@code attributeName} based on the field type in the
     * {@code referenceClass}.
     *
     * <p>
     * If the {@code attributeValue} is strictly null or empty (that is, has zero length) then this will just pass it
     * back since it cannot coerce the value further.  This will then search for the field, and if it finds a matching
     * field, it will attempt to first convert using the {@link Convert} converter if available.  If the converter is
     * not available, it will attempt to coerce from the {@code attributeValue} to one of {@link Character},
     * {@link Boolean}, or any of the wrapped number types.  Otherwise, it will just pass the {@code attributeValue}
     * back.
     * </p>
     *
     * @param referenceClass the class to execute the changes on.
     * @param attributeName the attribute name of the field to coerce.
     * @param attributeValue the value to coerce.
     * @return the coerced value.
     */
    private static Object coerce(Class<?> referenceClass, String attributeName, String attributeValue) {
        if (StringUtils.isEmpty(attributeValue)) {
            return attributeValue;
        }

        Field field = null;
        try {
            field = referenceClass.getDeclaredField(attributeName);
        } catch (NoSuchFieldException nsfe) {
            LOG.error("Could not locate the field " + attributeName + " in " + referenceClass.getName(), nsfe);
        }

        if (field != null) {
            Convert convert = field.getAnnotation(Convert.class);

            if (convert != null) {
                return coerceConverterValue(convert, attributeName, attributeValue);
            }

            return coerceValue(field.getType(), attributeName, attributeValue);
        }

        return attributeValue;
    }

    /**
     * Uses the {@link Convert} converter to first translate the {@code attributeValue} to the desired type and then
     * convert it to the database format for searching.
     *
     * @param convert the conversion annotation.
     * @param attributeName the attribute name of the field to coerce.
     * @param attributeValue the value to coerce.
     * @return the coerced value.
     */
    private static Object coerceConverterValue(Convert convert, String attributeName, String attributeValue) {
        try {
            AttributeConverter<Object, String> converter
                    = (AttributeConverter<Object, String>) convert.converter().newInstance();
            Object entityAttribute = converter.convertToEntityAttribute(attributeValue);
            return converter.convertToDatabaseColumn(entityAttribute);
        } catch (Exception e) {
            LOG.error("Could not create the converter for " + attributeName, e);
        }

        return attributeValue;
    }

    /**
     * Coerces the {@code attributeValue} using the given {@code type}.
     *
     * @param type the type to use to coerce the value.
     * @param attributeName the attribute name of the field to coerce.
     * @param attributeValue the value to coerce.
     * @return the coerced value.
     */
    private static Object coerceValue(Class<?> type, String attributeName, String attributeValue) {
        try {
            if (Character.TYPE.equals(type) || Character.class.isAssignableFrom(type)) {
                return Character.valueOf(attributeValue.charAt(0));
            } else if (Boolean.TYPE.equals(type) || Boolean.class.isAssignableFrom(type)) {
                return Boolean.valueOf(attributeValue);
            } else if (Short.TYPE.equals(type) || Short.class.isAssignableFrom(type)) {
                return Short.valueOf(attributeValue);
            } else if (Integer.TYPE.equals(type) || Integer.class.isAssignableFrom(type)) {
                return Integer.valueOf(attributeValue);
            } else if (Long.TYPE.equals(type) || Long.class.isAssignableFrom(type)) {
                return Long.valueOf(attributeValue);
            } else if (Double.TYPE.equals(type) || Double.class.isAssignableFrom(type)) {
                return Double.valueOf(attributeValue);
            } else if (Float.TYPE.equals(type) || Float.class.isAssignableFrom(type)) {
                return Float.valueOf(attributeValue);
            }
        } catch (NumberFormatException nfe) {
            LOG.error("Could not coerce the value " + attributeValue + " for the field " + attributeName, nfe);
        }

        return attributeValue;
    }

}
