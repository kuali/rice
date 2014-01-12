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
package org.kuali.rice.krad.util;

import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;

/**
 * A utility class for determining the data type of classes and their attributes.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataTypeUtil {

    private DataTypeUtil() {}

    public static String determineFieldDataType(Class<?> type, String attributeName) {
		final Class<?> attributeType = thieveAttributeType(type, attributeName);
		return determineDataType(attributeType);
	}
	 /**
     * Determines the datatype of the given class.
     *
     * @param attributeType the class whose datatype is to be determined.
     * @return String representation of the datatype. Defaults to string.
     */
	public static String determineDataType(Class<?> attributeType) {
        if (isStringy(attributeType)) {
            return KRADConstants.DATA_TYPE_STRING; // our most common case should go first
        }
        if (isDecimaltastic(attributeType)) {
            return KRADConstants.DATA_TYPE_FLOAT;
        }
        if (isDateLike(attributeType)) {
            return KRADConstants.DATA_TYPE_DATE;
        }
        if (isIntsy(attributeType)) {
            return KRADConstants.DATA_TYPE_LONG;
        }
        if (isBooleanable(attributeType)) {
            return KRADConstants.DATA_TYPE_BOOLEAN;
        }
        return KRADConstants.DATA_TYPE_STRING; // default to String
    }

    /**
     * Determines if the given Class is a String
     * @param type the class to check for Stringiness
     * @return true if the Class is a String, false otherwise
     */
	public static boolean isStringy(Class<?> type) {
        return java.lang.String.class.isAssignableFrom(type);
    }

    /**
     * Determines if the given class is enough like a date to store values of it as a SearchableAttributeDateTimeValue
     * @param type the class to determine the type of
     * @return true if it is like a date, false otherwise
     */
	public static boolean isDateLike(Class<?> type) {
        return java.util.Date.class.isAssignableFrom(type);
    }

    /**
     * Determines if the given class is enough like a Float to store values of it as a SearchableAttributeFloatValue
     * @param type the class to determine of the type of
     * @return true if it is like a "float", false otherwise
     */
	public static boolean isDecimaltastic(Class<?> type) {
        return java.lang.Double.class.isAssignableFrom(type) || java.lang.Float.class.isAssignableFrom(type) || type.equals(Double.TYPE) || type.equals(Float.TYPE) || java.math.BigDecimal.class.isAssignableFrom(type) || org.kuali.rice.core.api.util.type.KualiDecimal.class.isAssignableFrom(type);
    }

    /**
     * Determines if the given class is enough like a "long" to store values of it as a SearchableAttributeLongValue
     * @param type the class to determine the type of
     * @return true if it is like a "long", false otherwise
     */
	public static boolean isIntsy(Class<?> type) {
        return java.lang.Integer.class.isAssignableFrom(type) || java.lang.Long.class.isAssignableFrom(type) || java.lang.Short.class.isAssignableFrom(type) || java.lang.Byte.class.isAssignableFrom(type) || java.math.BigInteger.class.isAssignableFrom(type) || type.equals(Integer.TYPE) || type.equals(Long.TYPE) || type.equals(Short.TYPE) || type.equals(Byte.TYPE);
    }

    /**
     * Determines if the given class is enough like a boolean, to index it as a String "Y" or "N"
     * @param type the class to determine the type of
     * @return true if it is like a boolean, false otherwise
     */
	public static boolean isBooleanable(Class<?> type) {
        return java.lang.Boolean.class.isAssignableFrom(type) || type.equals(Boolean.TYPE);
    }

    /**
     * Given a BusinessObject class and an attribute name, determines the class of that attribute on the BusinessObject class
     * @param boClass a class extending BusinessObject
     * @param attributeKey the name of a field on that class
     * @return the Class of the given attribute
     */
    private static Class<?> thieveAttributeType(Class<?> boClass, String attributeKey) {
        for (PropertyDescriptor prop : PropertyUtils.getPropertyDescriptors(boClass)) {
            if (prop.getName().equals(attributeKey)) {
                return prop.getPropertyType();
            }
        }
        return null;
    }

}
