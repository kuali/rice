/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.api.criteria;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class which includes various utilities and constants for use within the criteria API.
 * This class is intended to be for internal use only within the criteria API.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
final class CriteriaSupportUtils {
	
	private CriteriaSupportUtils () {}
	
    /**
     * Defines various property constants for internal use within the criteria package.
     */
    static class PropertyConstants {
    	
    	/**
    	 * A constant representing the property name for {@link PropertyPathExpression#getPropertyPath()}
    	 */
        final static String PROPERTY_PATH = "propertyPath";
        
    	/**
    	 * A constant representing the property name for {@link ValuedExpression#getValue()}
    	 */
        final static String VALUE = "value";
        
        /**
         * A constant representing the method name for {@link ValuedExpression#getValue()}
         */
        final static String GET_VALUE_METHOD_NAME = "getValue";

        /**
    	 * A constant representing the property name for {@link MultiValuedExpression#getValues()}
    	 */
        final static String VALUES = "values";

        /**
         * A constant representing the method name for {@link MultiValuedExpression#getValues()}
         */
        final static String GET_VALUES_METHOD_NAME = "getValues";
    }

    /**
     * Validates the various properties of a {@link ValuedExpression}. 
     * 
     * @param valuedExpressionClass the type of the expression
     * @param propertyPath the propertyPath which is being configured on the expression
     * @param value the value which is being configured on the expression
     * 
     * @throws IllegalArgumentException if the propertPath is null or blank
     * @throws IllegalArgumentException if the value is null
     * @throws IllegalArgumentException if the given {@link ValuedExpression} class does not support the {@link CriteriaValue}
     */
    static void validateValuedExpressionConstruction(Class<? extends ValuedExpression> valuedExpressionClass, String propertyPath, CriteriaValue<?> value) {
    	if (StringUtils.isBlank(propertyPath)) {
			throw new IllegalArgumentException("Property path cannot be null or blank.");
		}
		if (value == null) {
		    throw new IllegalArgumentException("CriteriaValue cannot be null.");
		}
		if (!CriteriaSupportUtils.supportsCriteriaValue(valuedExpressionClass, value)) {
		    throw new IllegalArgumentException(valuedExpressionClass.getSimpleName() + " does not support the given CriteriaValue");
		}
    }

	static boolean supportsCriteriaValue(Class<? extends ValuedExpression> simpleExpressionClass, CriteriaValue<?> value) {
	    if (simpleExpressionClass == null) {
	        throw new IllegalArgumentException("simpleExpressionClass was null");
	    }
	    if (value == null) {
	        throw new IllegalArgumentException("valueClass was null");
	    }
	    XmlElements elementsAnnotation = CriteriaSupportUtils.findXmlElementsAnnotation(simpleExpressionClass);
	    if (elementsAnnotation != null) {
	        XmlElement[] elements = elementsAnnotation.value();
	        for (XmlElement element : elements) {
	            if (value.getClass().equals(element.type())) {
	                return true;
	            }
	        }
	    }
	    return false;
	}
	
	private static XmlElements findXmlElementsAnnotation(Class<?> simpleExpressionClass) {
		if (simpleExpressionClass != null) {
			try{
				Field valueField = simpleExpressionClass.getDeclaredField(PropertyConstants.VALUE);
				XmlElements elementsAnnotation = valueField.getAnnotation(XmlElements.class);
				if (elementsAnnotation != null) {
					return elementsAnnotation;
				}
			} catch (NoSuchFieldException e) {
				// ignore, try the method
			}
			try {
				Method valueMethod = simpleExpressionClass.getDeclaredMethod(PropertyConstants.GET_VALUE_METHOD_NAME, (Class<?>[])null);
				XmlElements elementsAnnotation = valueMethod.getAnnotation(XmlElements.class);
				if (elementsAnnotation == null) {
					return CriteriaSupportUtils.findXmlElementsAnnotation(simpleExpressionClass.getSuperclass());
				}
				return elementsAnnotation;
			} catch (NoSuchMethodException e) {
				return CriteriaSupportUtils.findXmlElementsAnnotation(simpleExpressionClass.getSuperclass());
			}
		}
		return null;
	}
	
	static CriteriaValue<?> determineCriteriaValue(Object object) {
		if (object == null) {
			throw new IllegalArgumentException("Given criteria value cannot be null.");
		} else if (object instanceof CharSequence) {
			return new CriteriaStringValue((CharSequence)object);
		} else if (object instanceof Calendar) {
			return new CriteriaDateTimeValue((Calendar)object);
		} else if (object instanceof Date) {
			return new CriteriaDateTimeValue((Date)object);
		} else if (object instanceof BigInteger) {
			return new CriteriaIntegerValue((BigInteger)object);
		} else if (object instanceof Short) {
			return new CriteriaIntegerValue((Short)object);
		} else if (object instanceof Integer) {
			return new CriteriaIntegerValue((Integer)object);
		} else if (object instanceof AtomicInteger) {
			return new CriteriaIntegerValue((AtomicInteger)object);
		} else if (object instanceof Long) {
			return new CriteriaIntegerValue((Long)object);
		} else if (object instanceof AtomicLong) {
			return new CriteriaIntegerValue((AtomicLong)object);
		} else if (object instanceof BigDecimal) {
			return new CriteriaDecimalValue((BigDecimal)object);
		} else if (object instanceof Float) {
			return new CriteriaDecimalValue((Float)object);
		} else if (object instanceof Double) {
			return new CriteriaDecimalValue((Double)object);
		}
		throw new IllegalArgumentException("Failed to translate the given object to a CriteriaValue: " + object);
	}
	
	static List<CriteriaValue<?>> determineCriteriaValueList(List<? extends Object> values) {
		if (values == null) {
			return null;
		} else if (values.isEmpty()) {
			return Collections.emptyList();
		}
		List<CriteriaValue<?>> criteriaValues = new ArrayList<CriteriaValue<?>>();
		for (Object value : values) {
			criteriaValues.add(determineCriteriaValue(value));
		}
		return criteriaValues;
	}
	
	/**
     * Validates the incoming list of CriteriaValue to ensure they are valid for a
     * {@link MultiValuedExpression}.  To be valid, the following must be true:
     * 
     * <ol>
     *   <li>The list of values must not be null.</li>
     *   <li>The list of values must not be empty.</li>
     *   <li>The list of values must all be of the same parameterized {@link CriteriaValue} type.</li>
     * </ol>
     */
    static void validateValuesForMultiValuedExpression(List<? extends CriteriaValue<?>> values) {
    	if (values == null) {
    		throw new IllegalArgumentException("Criteria values cannot be null.");
    	} else if (values.isEmpty()) {
    		throw new IllegalArgumentException("Criteria values cannot be empty.");
    	}
    	Class<?> previousType = null;
    	for (CriteriaValue<?> value : values) {
    		Class<?> currentType = value.getClass();
    		if (previousType != null) {
    			if (!currentType.equals(previousType)) {
    				throw new IllegalArgumentException("Encountered criteria values which do not match.  One was: " + previousType + " the other was: " + currentType);
    			}
    		}
    		previousType = currentType;
    	}
    }
	
}
