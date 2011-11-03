/**
 * Copyright 2005-2011 The Kuali Foundation
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

    /**
     * Returns the name for the given class
     *
     * @param clazz - class object to return name for
     * @return String class name or empty string if class is null
     */
    public static String getName(Class<?> clazz) {
        if (clazz == null) {
            return "";
        } else {
            return clazz.getName();
        }
    }
}
