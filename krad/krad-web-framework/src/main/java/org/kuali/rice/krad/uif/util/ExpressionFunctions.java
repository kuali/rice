package org.kuali.rice.krad.uif.util;

import org.apache.commons.lang.StringUtils;

/**
 * Defines functions that can be used in el expressions within
 * the UIF dictionary files
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ExpressionFunctions {

    /**
     * Checks whether the given class parameter is assignable from the given object class
     * parameter
     *
     * @param assignableClass - class to use for assignable to
     * @param objectClass - class to use for assignable from
     * @return boolean true if the object class is of type assignable class, false if not
     */
    public static boolean isAssignableFrom(Class<?> assignableClass, Class<?> objectClass) {
        return assignableClass.isAssignableFrom(objectClass);
    }
    
    /**
     * Checks whether the given value is null or blank string
     *
     * @param value - property value to check
     * @return boolean true if value is null or blank, false if not
     */
    public static boolean empty(Object value) {
        return (value == null) || (StringUtils.isBlank(value.toString()));
    }
}
