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
package org.kuali.rice.krad.data;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ObjectRetrievalFailureException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Some simple utility methods for use with data objects.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class DataObjectUtils {
    private static final Logger LOG = Logger.getLogger(DataObjectUtils.class);

    private DataObjectUtils() {}

    /**
     * This method is a proxy-safe null check. Some data access technologies might implement proxy for things such as
     * lazy loading of a relationship. In these cases, it may not be possible to know that the underlying proxy will
     * actually resolve to a null object. This method checks if the given object is actually null or will ultimately
     * resolve to null.
     *
     * <p>Note that this might have side-affects, such as materializing an underlying lazy relationship.</p>
     *
     * @param dataObject the object to check if it is null or will resolve to null
     * @return true if the object is null or will resolve to null, false otherwise
     */
    public static boolean isNull(Object dataObject) {
        // regardless, if its null, then its null
        if (dataObject == null) {
            return true;
        }

        // TODO - this is kind of JPA specific right now, need to delegate this down to the provider layer, probably
        // need to add something to the persistence or metadata provider for this...
        try {
            dataObject.equals(null);
        } catch (ObjectRetrievalFailureException e) {
            return true;
        }

        return false;
    }

    /**
     * This method is a proxy-safe check to determine if an object is null or will resolve to null. Some data access
     * technologies might implement proxy for things such as lazy loading of a relationship. In these cases, it may not
     * be possible to know that the underlying proxy will actually resolve to a null object. This method checks if the
     * given object is actually non-null or will ultimately resolve to a non-null value.
     *
     * <p>Note that this might have side-affects, such as materializing an underlying lazy relationship.</p>
     *
     * @param dataObject the object to check if it is not null or will resolve to a non-null value
     * @return true if the object is not null or will resolve to a non-null value, false otherwise
     */
    public static boolean isNotNull(Object dataObject) {
        return !isNull(dataObject);
    }

    /**
     * Returns the value of the property in the object.
     *
     * @param dataObject
     * @param propertyName
     * @return Object will be null if any parent property for the given property is null.
     */
    public static Object getPropertyValue(Object dataObject, String propertyName) {
        if (dataObject == null || propertyName == null) {
            throw new RuntimeException("Business object and property name can not be null");
        }

        DataObjectWrapper<Object> wrapper = KradDataServiceLocator.getDataObjectService().wrap(dataObject);

        return wrapper.getPropertyValueNullSafe(propertyName);
    }

    /**
     *   Get property type for the given object and property name.
     *
     * @param dataObject
     * @param propertyName
     * @return
     */
     public static Class<?> getPropertyType(Object dataObject, String propertyName) {
         if (dataObject == null || propertyName == null) {
             throw new RuntimeException("Business object and property name can not be null");
         }
         DataObjectWrapper<Object> wrapper = KradDataServiceLocator.getDataObjectService().wrap(dataObject);
         return wrapper.getPropertyType(propertyName);
     }

    public static void setObjectValue(Object dataObject, String propertyName, Object propertyValue) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        // set property in the object
        PropertyUtils.setNestedProperty(dataObject, propertyName, propertyValue);
    }

    /**
     * Determines if a given string could represent a nested attribute of an object.
     *
     * @param attributeName
     * @return true if the attribute is nested
     */
    public static boolean isNestedAttribute(String attributeName) {
        boolean isNested = false;

        if (StringUtils.contains(attributeName, ".")) {
            isNested = true;
        }

        return isNested;
    }



    /**
     * Returns the prefix of a nested attribute name, or the empty string if the attribute name is not nested.
     *
     * @param attributeName
     * @return everything BEFORE the last "." character in attributeName
     */
    public static String getNestedAttributePrefix(String attributeName) {
        String prefix = "";

        if (StringUtils.contains(attributeName, ".")) {
            prefix = StringUtils.substringBeforeLast(attributeName, ".");
        }

        return prefix;
    }


    /**
     * Returns the primitive part of an attribute name string.
     *
     * @param attributeName
     * @return everything AFTER the last "." character in attributeName
     */
    public static String getNestedAttributePrimitive(String attributeName) {
        String primitive = attributeName;

        if (StringUtils.contains(attributeName, ".")) {
            primitive = StringUtils.substringAfterLast(attributeName, ".");
        }

        return primitive;
    }

    /**
     * This method safely extracts either simple values OR nested values. For example, if the bo is SubAccount, and the
     * fieldName is
     * a21SubAccount.subAccountTypeCode, this thing makes sure it gets the value off the very end attribute, no matter
     * how deeply
     * nested it is. The code would be slightly simpler if this was done recursively, but this is safer, and consumes a
     * constant
     * amount of memory, no matter how deeply nested it goes.
     *
     * @param dataObject
     * @param fieldName
     * @return The field value if it exists. If it doesnt, and the name is invalid, and
     */
    public static Object getNestedValue(Object dataObject, String fieldName) {

        if (dataObject == null) {
            throw new IllegalArgumentException("The bo passed in was null.");
        }
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("The fieldName passed in was blank.");
        }

        // okay, this section of code is to handle sub-object values, like
        // SubAccount.a21SubAccount.subAccountTypeCode. it basically walks
        // through the period-delimited list of names, and ends up with the
        // final value.
        String[] fieldNameParts = fieldName.split("\\.");
        Object currentObject = null;
        Object priorObject = dataObject;
        for (int i = 0; i < fieldNameParts.length; i++) {
            String fieldNamePart = fieldNameParts[i];

            try {
                if (fieldNamePart.indexOf("]") > 0) {
                    currentObject = PropertyUtils.getIndexedProperty(priorObject, fieldNamePart);
                } else {
                    currentObject = PropertyUtils.getSimpleProperty(priorObject, fieldNamePart);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Caller does not have access to the property accessor method.", e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Property accessor method threw an exception.", e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("The accessor method requested for this property cannot be found.", e);
            }

            // if a node or the leaf is null, then we're done, there's no need to
            // continue accessing null things
            if (currentObject == null) {
                return currentObject;
            }

            priorObject = currentObject;
        }
        return currentObject;
    }

    /**
     * This method safely creates a object from a class
     *
     * @param type
     * @return a newInstance() of type
     */
    public static Object createNewObjectFromClass(Class<?> type) {
        if (type == null) {
            throw new RuntimeException("Class was passed in as null");
        }
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error occured while trying to create a new instance for class " + type, e);
        }
    }



    /**
     * Retrieves all fields including the inherited fields for a given class. The recursion stops if either  Object class is reached
     * or if stopAt is reached first.
     *
     * @param fields  List of fields (public, private and protected)
     * @param type   Class from which fields retrieval has to start
     * @param stopAt Parent class where the recursion should stop
     * @return
     */
    public static List<Field> getAllFields(List<Field> fields, Class<?> type, Class<?> stopAt) {
        for (Field field : type.getDeclaredFields()) {
            fields.add(field);
        }

        if (type.getSuperclass() != null && !type.getName().equals(stopAt.getName())) {
            fields = getAllFields(fields, type.getSuperclass(), stopAt);
        }

        return fields;
    }


    /**
     * Helper method for creating a new instance of the given class
     *
     * @param type - class of object to create
     * @return T object of type given by the clazz parameter
     */
    public static <T> T newInstance(Class<T> type) {
        T object = null;
        try {
            object = type.newInstance();
        } catch (InstantiationException e) {
            LOG.error("Unable to create new instance of class: " + type.getName());
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            LOG.error("Unable to create new instance of class: " + type.getName());
            throw new RuntimeException(e);
        }

        return object;
    }

}
