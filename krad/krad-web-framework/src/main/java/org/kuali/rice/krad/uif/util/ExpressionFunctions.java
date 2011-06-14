package org.kuali.rice.krad.uif.util;

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
}
