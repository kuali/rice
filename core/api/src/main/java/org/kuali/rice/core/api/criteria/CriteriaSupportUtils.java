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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.apache.commons.lang.StringUtils;

/**
 * A class which includes various utilities and constants for use within the criteria API.
 * This class is intended to be for internal use only within the criteria API.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
final class CriteriaSupportUtils {
	
	private CriteriaSupportUtils () {}
	
    /**
     * A private class which exposes constants which define the XML element names to use
     * when instances of {@link SimpleExpression} are marshaled to XML.
     */
    static class SimpleExpressionProperties {
        final static String PROPERTY_PATH = "propertyPath";
        final static String VALUE = "value";
        final static String GET_VALUE_METHOD_NAME = "getValue";
    }

    static void validateSimpleExpressionConstruction(Class<? extends SimpleExpression> simpleExpressionClass, String propertyPath, CriteriaValue<?> value) {
    	if (StringUtils.isBlank(propertyPath)) {
			throw new IllegalArgumentException("Property path cannot be null or blank.");
		}
		if (value == null) {
		    throw new IllegalArgumentException("CriteriaValue cannot be null.");
		}
		if (!CriteriaSupportUtils.supportsCriteriaValue(simpleExpressionClass, value)) {
		    throw new IllegalArgumentException(simpleExpressionClass.getSimpleName() + " does not support the given CriteriaValue");
		}
    }

	static boolean supportsCriteriaValue(Class<? extends SimpleExpression> simpleExpressionClass, CriteriaValue<?> value) {
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
				Field valueField = simpleExpressionClass.getDeclaredField(SimpleExpressionProperties.VALUE);
				XmlElements elementsAnnotation = valueField.getAnnotation(XmlElements.class);
				if (elementsAnnotation != null) {
					return elementsAnnotation;
				}
			} catch (NoSuchFieldException e) {
				// ignore, try the method
			}
			try {
				Method valueMethod = simpleExpressionClass.getDeclaredMethod(SimpleExpressionProperties.GET_VALUE_METHOD_NAME, (Class<?>[])null);
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
	
}
