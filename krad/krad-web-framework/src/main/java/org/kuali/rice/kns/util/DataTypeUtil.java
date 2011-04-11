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
package org.kuali.rice.kns.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.kuali.rice.kns.bo.BusinessObject;

/**
 * Pulled this logic out of the org.kuali.rice.kns.workflow.service.impl.WorkflowAttributePropertyResolutionServiceImpl
 * since it wasn't really service logic at all, just util logic.
 * 
 * @author James Renfro
 *
 */
public class DataTypeUtil {

	public static String determineFieldDataType(Class<? extends BusinessObject> businessObjectClass, String attributeName) {
		final Class<?> attributeClass = thieveAttributeClassFromBusinessObjectClass(businessObjectClass, attributeName);
		return determineDataType(attributeClass);
	}
	
	public static String determineDataType(Class<?> attributeClass) {
        if (isStringy(attributeClass)) return KNSConstants.DATA_TYPE_STRING; // our most common case should go first
        if (isDecimaltastic(attributeClass)) return KNSConstants.DATA_TYPE_FLOAT;
        if (isDateLike(attributeClass)) return KNSConstants.DATA_TYPE_DATE;
        if (isIntsy(attributeClass)) return KNSConstants.DATA_TYPE_LONG;
        if (isBooleanable(attributeClass)) return KNSConstants.DATA_TYPE_BOOLEAN;
        return KNSConstants.DATA_TYPE_STRING; // default to String
    }
	
    /**
     * Determines if the given Class is a String
     * @param clazz the class to check for Stringiness
     * @return true if the Class is a String, false otherwise
     */
	public static boolean isStringy(Class clazz) {
        return java.lang.String.class.isAssignableFrom(clazz);
    }

    /**
     * Determines if the given class is enough like a date to store values of it as a SearchableAttributeDateTimeValue
     * @param class the class to determine the type of
     * @return true if it is like a date, false otherwise
     */
	public static boolean isDateLike(Class clazz) {
        return java.util.Date.class.isAssignableFrom(clazz);
    }
    
    /**
     * Determines if the given class is enough like a Float to store values of it as a SearchableAttributeFloatValue
     * @param value the class to determine of the type of
     * @return true if it is like a "float", false otherwise
     */
	public static boolean isDecimaltastic(Class clazz) {
        return java.lang.Double.class.isAssignableFrom(clazz) || java.lang.Float.class.isAssignableFrom(clazz) || clazz.equals(Double.TYPE) || clazz.equals(Float.TYPE) || java.math.BigDecimal.class.isAssignableFrom(clazz) || org.kuali.rice.core.util.type.KualiDecimal.class.isAssignableFrom(clazz);
    }
    
    /**
     * Determines if the given class is enough like a "long" to store values of it as a SearchableAttributeLongValue
     * @param value the class to determine the type of
     * @return true if it is like a "long", false otherwise
     */
	public static boolean isIntsy(Class clazz) {
        return java.lang.Integer.class.isAssignableFrom(clazz) || java.lang.Long.class.isAssignableFrom(clazz) || java.lang.Short.class.isAssignableFrom(clazz) || java.lang.Byte.class.isAssignableFrom(clazz) || java.math.BigInteger.class.isAssignableFrom(clazz) || clazz.equals(Integer.TYPE) || clazz.equals(Long.TYPE) || clazz.equals(Short.TYPE) || clazz.equals(Byte.TYPE);
    }

    /**
     * Determines if the given class is enough like a boolean, to index it as a String "Y" or "N"
     * @param value the class to determine the type of
     * @return true if it is like a boolean, false otherwise
     */
	public static boolean isBooleanable(Class clazz) {
        return java.lang.Boolean.class.isAssignableFrom(clazz) || clazz.equals(Boolean.TYPE);
    }
    
    /**
     * Given a BusinessObject class and an attribute name, determines the class of that attribute on the BusinessObject class
     * @param boClass a class extending BusinessObject
     * @param attributeKey the name of a field on that class
     * @return the Class of the given attribute
     */
    private static Class thieveAttributeClassFromBusinessObjectClass(Class<? extends BusinessObject> boClass, String attributeKey) {
        Class attributeFieldClass = null;
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(boClass);
            int i = 0;
            while (attributeFieldClass == null && i < beanInfo.getPropertyDescriptors().length) {
                final PropertyDescriptor prop = beanInfo.getPropertyDescriptors()[i];
                if (prop.getName().equals(attributeKey)) {
                    attributeFieldClass = prop.getPropertyType();
                }
                i += 1;
            }
        }
        catch (SecurityException se) {
            throw new RuntimeException("Could not determine type of attribute "+attributeKey+" of BusinessObject class "+boClass.getName(), se);
        }
        catch (IntrospectionException ie) {
            throw new RuntimeException("Could not determine type of attribute "+attributeKey+" of BusinessObject class "+boClass.getName(), ie);
        }
        return attributeFieldClass;
    }
	
}
