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
package org.kuali.rice.krad.uif.util;

import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.datadictionary.Copyable;
import org.kuali.rice.krad.uif.util.ObjectPathExpressionParser.PathEntry;
import org.kuali.rice.krad.util.KRADUtils;

/**
 * Represents a property reference in a path expression, for use in implementing
 * {@link ObjectPathExpressionParser.PathEntry}.
 * 
 * <p>
 * This class defers the actual resolution of property references nodes in a path expression until
 * the transition between parse nodes. This facilitates traversal to the final node in the path.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @version 2.4
 * @see ObjectPathExpressionParser#parsePathExpression(Object, String, PathEntry)
 */
public class ObjectPropertyReference {

    /**
     * Primitive default values
     */
    private static final boolean DEFAULT_BOOLEAN = false;
    private static final byte DEFAULT_BYTE = 0;
    private static final short DEFAULT_SHORT = 0;
    private static final int DEFAULT_INT = 0;
    private static final long DEFAULT_LONG = 0L;
    private static final float DEFAULT_FLOAT = 0.0f;
    private static final double DEFAULT_DOUBLE = 0.0d;
    private static final char DEFAULT_CHAR = '\u0000';

    /**
     * Log4j logger.
     */
    private static final Logger LOG = Logger.getLogger(ObjectPropertyReference.class);

    /**
     * Reference for single use.
     */
    private static final ThreadLocal<ObjectPropertyReference> TL_BUILDER_REF = new ThreadLocal<ObjectPropertyReference>();

    /**
     * Reference for single use.
     */
    private static final ThreadLocal<Boolean> TL_WARN = new ThreadLocal<Boolean>();

    /**
     * Singleton reference path entry, to be used when parsing for looking up a bean property
     * without modifying.
     */
    private static final ReferencePathEntry LOOKUP_REF_PATH_ENTRY = new ReferencePathEntry(false);

    /**
     * Singleton reference path entry, to be used when parsing for modifying the bean property.
     */
    private static final ReferencePathEntry MUTATE_REF_PATH_ENTRY = new ReferencePathEntry(true);

    /**
     * Internal path entry implementation.
     */
    private static final class ReferencePathEntry implements PathEntry {

        /**
         * Determines whether or not {@link ObjectPropertyReference#initialize(Object, Class)}
         * should be used to create an object when a property reference resolves to null.
         */
        private final boolean grow;

        /**
         * Internal private constructor.
         */
        private ReferencePathEntry(boolean grow) {
            this.grow = grow;
        }

        /**
         * Transition from one path entry to the next while parsing a bean property expression.
         * 
         * {@inheritDoc}
         */
        @Override
        public Object parse(String parentPath, Object node, String next) {
            ObjectPropertyReference current = (ObjectPropertyReference) node;

            // At the initial parse node, copy to a new property reference.
            // Otherwise, we will modify the existing reference to reduce object construction
            // due to object reference parsing.
            if (next == null) {
                ObjectPropertyReference resolved = new ObjectPropertyReference();
                resolved.rootBean = current.bean;
                resolved.rootPath = current.rootPath;
                resolved.bean = current.bean;
                resolved.beanClass = current.beanClass;
                resolved.beanType = current.beanType;
                resolved.name = null;
                resolved.parentPath = null;
                return resolved;
            }

            // Get the property type and value from the current node reference.
            // These will become the bean and bean class after transition.
            Class<?> beanClass = current.getPropertyType();
            Object bean = current.get();
            if (bean instanceof Copyable) {
                bean = CopyUtils.unwrap((Copyable) bean);
                if (!beanClass.isInstance(bean)) {
                    beanClass = bean.getClass();
                }
            }

            // Determine the parameterized property type, if applicable.
            // This facilitates type conversion when setting/getting typed collections.
            Type beanType;
            Method readMethod = ObjectPropertyUtils.getReadMethod(current.getImplClass(), current.name);
            if (readMethod == null) {
                beanType = beanClass;
            } else {
                beanType = readMethod.getGenericReturnType();
            }

            // When parsing for a set() operation, automatically initialize values.
            if (grow) {
                Object newBean = initialize(bean, beanClass);
                if (newBean != bean) {
                    current.set(newBean);
                    Object verify;
                    assert (verify = current.get()) == newBean : verify + " != " + newBean;
                    bean = newBean;
                }
            }

            // Modify the current reference to represent the next parse node, and return.
            current.bean = bean;
            current.beanClass = beanClass;
            current.beanType = beanType;
            current.name = next;
            current.parentPath = parentPath;

            return current;
        }
    }

    /**
     * Get the property value for a specific bean property of a known bean class.
     * 
     * @param propertyValue existing property value
     * @param propertyType the property type to initialize if the existing value is null
     * @return The property value for the specific bean property on the given bean.
     */
    private static Object initialize(Object propertyValue, Class<?> propertyType) {
        Object returnValue = propertyValue;
        
        if (propertyValue == null) {
            if (List.class.equals(propertyType)) {
                returnValue = new java.util.LinkedList<Object>();

            } else if (Map.class.equals(propertyType)) {
                returnValue = new java.util.HashMap<Object, Object>();

            } else if (!String.class.equals(propertyType)) {
                try {
                    returnValue = propertyType.newInstance();
                } catch (InstantiationException e) {
                    throw new IllegalStateException("Failed to create new object for setting property value", e);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Failed to create new object for setting property value", e);
                }
            }
        }
        
        return returnValue;
    }

    /**
     * Get a property value from an array.
     * 
     * <p>
     * NOTE: This method is null and bounds-safe. When the property name does not represent a valid
     * array index, or the array is null, then null is returned.
     * </p>
     * 
     * @param array The array.
     * @param name The name of the property value.
     * @return The property value for the named entry in the array. When name is 'size' or 'length',
     *         then the length of the array is returned, otherwise the property name is converted to
     *         an integer and used as the array index.
     */
    private static Object getArray(Object array, String name) {
        if (array == null) {
            return null;
        }

        for (int i = 0; i < name.length(); i++) {
            if (!Character.isDigit(name.charAt(i))) {
                return null;
            }
        }

        int i = Integer.parseInt(name);

        if (i >= Array.getLength(array)) {
            return null;
        }

        return Array.get(array, i);
    }

    /**
     * Set a property value in an array.
     * 
     * @param array The array.
     * @param name A string representation of the index in the array.
     * @param value The property value to set in the array.
     */
    private static void setArray(Object array, String name, Object value) {
        Array.set(array, Integer.parseInt(name), value);
    }

    /**
     * Get a property value from an list.
     * 
     * <p>
     * NOTE: This method is null and bounds-safe. When the property name does not represent a valid
     * list index, or the list is null, then null is returned.
     * </p>
     * 
     * @param list The list.
     * @param name The name of the property value.
     * @return The property value for the named entry in the list. When name is 'size' or 'length',
     *         then the length of the list is returned, otherwise the property name is converted to
     *         an integer and used as the list index.
     */
    private static Object getList(List<?> list, String name) {
        int length;
        if (list == null) {
            length = 0;
        } else {
            length = list.size();
        }

        for (int i = 0; i < name.length(); i++) {
            if (!Character.isDigit(name.charAt(i))) {
                return null;
            }
        }

        int i = Integer.parseInt(name);
        if (i >= length) {
            return null;
        }

        return list.get(i);
    }

    /**
     * Set a property value in a list.
     * 
     * @param list The list.
     * @param name A string representation of the list index.
     * @param value The value to add to the list.
     */
    @SuppressWarnings("unchecked")
    private static void setList(List<?> list, String name, Object value) {
        int i = Integer.parseInt(name);
        while (i >= list.size()) {
            list.add(null);
        }
        ((List<Object>) list).set(i, value);
    }

    /**
     * Get a property value from an map.
     * 
     * @param map The map.
     * @param name The name of the property value.
     * @return The property value for the named entry in the map.
     */
    private static Object getMap(Map<?, ?> map, String name) {
        if (map != null && map.containsKey(name)) {
            return map.get(name);
        }
        return null;
    }

    /**
     * Determine if a warning should be logged on when an invalid property is encountered
     * on the current thread.
     * @return True to log warnings when invalid properties are encountered, false to ignore
     *        invalid properties.
     */
    public static boolean isWarning() {
        return Boolean.TRUE.equals(TL_WARN.get());
    }

    /**
     * Indicate whether or not a warning should be logged on when an invalid property is encountered
     * on the current thread.
     * @param warning True to log warnings when invalid properties are encountered, false to ignore
     *        invalid properties.
     */
    public static void setWarning(boolean warning) {
        if (warning) {
            TL_WARN.set(true);
        } else {
            TL_WARN.remove();
        }
    }

    /**
     * Resolve a path expression on a bean.
     * 
     * @param bean The bean.
     * @param beanClass The bean class.
     * @param propertyPath The property path expression.
     * @param grow True to create objects while traversing the path, false to traverse class
     *        structure only when referring to null.
     * @return A reference to the final parse node involved in parsing the path expression.
     */
    public static ObjectPropertyReference resolvePath(Object bean, Class<?> beanClass, String propertyPath, boolean grow) {
        if (ObjectPathExpressionParser.isPath(propertyPath)) {

            // Parse the path expression.  This requires a new reference object since object read
            // methods could potentially call this method recursively.
            ObjectPropertyReference reference = new ObjectPropertyReference();
            reference.beanClass = beanClass;
            reference.rootPath = propertyPath;
            if (bean instanceof Copyable) {
                reference.bean = CopyUtils.unwrap((Copyable) bean);
                reference.rootBean = reference.bean;
                if (!(beanClass.isInstance(reference.bean))) {
                    reference.beanClass = reference.bean.getClass();
                }
            } else {
                reference.bean = bean;
                reference.rootBean = bean;
            }

            ObjectPropertyReference resolved = (ObjectPropertyReference) ObjectPathExpressionParser
                    .parsePathExpression(reference, propertyPath,
                            grow ? MUTATE_REF_PATH_ENTRY : LOOKUP_REF_PATH_ENTRY);

            reference.bean = resolved.bean;
            reference.beanClass = resolved.beanClass;
            reference.beanType = resolved.beanType;
            reference.name = resolved.name;
            return reference;

        } else {

            return resolveProperty(bean, beanClass, propertyPath);

        }
    }

    /**
     * Get a single-use reference for resolving a property on a bean.
     *
     * <p>
     * When using this method, the property name will be treated as-is, and will not be resolved as
     * a path expression.
     * </p>
     *
     * @param bean The bean.
     * @param beanClass The bean class.
     * @param propertyPath The property path.
     * @return A single-use reference to the final parse node involved in parsing the path
     *         expression. Note that the reference returned by this method will be reused and
     *         modified by the next call, so should not be set to a variable.
     */
    public static ObjectPropertyReference resolveProperty(Object bean, Class<?> beanClass, String propertyPath) {
        ObjectPropertyReference reference = TL_BUILDER_REF.get();
        if (reference == null) {
            reference = new ObjectPropertyReference();
            TL_BUILDER_REF.set(reference);
        }
        reference.beanClass = beanClass;
        if (bean instanceof Copyable) {
            reference.bean = CopyUtils.unwrap((Copyable) bean);
            if (!(beanClass.isInstance(reference.bean)) && reference.bean != null) {
                reference.beanClass = reference.bean.getClass();
            }
        } else {
            reference.bean = bean;
        }
        reference.rootBean = reference.bean;
        reference.rootPath = propertyPath;
        reference.beanType = reference.beanClass;
        reference.name = propertyPath;
        return reference;
    }

    /**
     * The root bean, may be null for traversing only class data.
     */
    private Object rootBean;

    /**
     * The bean, may be null for traversing only class data.
     */
    private Object bean;

    /**
     * The bean class.
     */
    private Class<?> beanClass;

    /**
     * The bean type.
     */
    private Type beanType;

    /**
     * The property name.
     */
    private String name;

    /**
     * The parent property path.
     */
    private String parentPath;

    /**
     * The root property path.
     */
    private String rootPath;

    /**
     * Internal private constructor.
     */
    private ObjectPropertyReference() {}

    /**
     * Convert a string property value to the targeted property type.
     * 
     * @param propertyValue The string property value.
     * @return The property value, converted to the property type.
     */
    private Object convertStringToPropertyType(String propertyValue) {
        Class<?> propertyType = getPropertyType();

        // TODO: these methods, and their inversions (below) need to be either support escaping
        // or be removed.  Both have been included for equivalence with previous BeanWrapper
        // implementation.
        if (List.class.equals(propertyType)) {
            return KRADUtils.convertStringParameterToList(propertyValue);

        } else if (Map.class.equals(propertyType)) {
            return KRADUtils.convertStringParameterToMap(propertyValue);

        } else {

            PropertyEditor editor = ObjectPropertyUtils.getPropertyEditor(rootBean, rootPath);
            if (editor == null) {
                throw new IllegalArgumentException("No property editor available for converting '" + propertyValue
                        + "' to " + propertyType);
            }

            editor.setAsText((String) propertyValue);
            return editor.getValue();
        }

    }
    
    /**
     * Convert a property value to a string.
     * 
     * @param propertyValue The property value.
     * @return The property value, converted to a string.
     */
    private Object convertPropertyValueToString(Object propertyValue) {

        // TODO: these methods, and their inversions (above) need to be either support escaping
        // or be removed.  Both have been included for equivalence with previous BeanWrapper
        // implementation.
        // FIXME: Where are these conversions used?  Can they be removed?
        if (propertyValue instanceof List) {
            StringBuilder listStringBuilder = new StringBuilder();
            for (Object item : (List<?>) propertyValue) {
                if (listStringBuilder.length() > 0) {
                    listStringBuilder.append(',');
                }
                listStringBuilder.append((String) item);
            }
            return listStringBuilder.toString();

        } else if (propertyValue instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> mapPropertyValue = (Map<String, String>) propertyValue;
            return KRADUtils.buildMapParameterString(mapPropertyValue);

        } else {

            PropertyEditor editor = ObjectPropertyUtils
                    .getPropertyEditor(ObjectPropertyUtils.getPrimitiveType(propertyValue.getClass()));
            if (editor == null) {
                throw new IllegalArgumentException("No property editor available for converting '" + propertyValue
                        + "' from " + propertyValue.getClass());
            }

            editor.setValue(propertyValue);
            return editor.getAsText();
        }
    }

    /**
     * Convert a property value to the targeted property type.
     * 
     * @param propertyValue The property value.
     * @return The property value, converted to the property type.
     */
    private Object convertToPropertyType(Object propertyValue) {
        Class<?> propertyType = getPropertyType();

        if (propertyValue == null) {
            return  primitiveDefault(propertyType);
        }

        if (propertyType.isInstance(propertyValue)) {
            return propertyValue;
        }

        if (propertyValue instanceof String) {
            return convertStringToPropertyType((String) propertyValue);
        }
        
        if (propertyType.equals(String.class)) {
            return convertPropertyValueToString(propertyValue);
        }

        return propertyValue;
    }

    /**
     * Get default values for primitives
     *
     * @param object The property value.
     * @return The default value for the Object type passed
     */
    private Object primitiveDefault(Class<?> object) {
        if (!object.isPrimitive()){
            return null;
        } else if (object.equals(boolean.class)) {
            return DEFAULT_BOOLEAN;
        } else if (object.equals(byte.class)) {
            return DEFAULT_BYTE;
        } else if (object.equals(char.class)) {
            return DEFAULT_CHAR;
        } else if (object.equals(short.class)) {
            return DEFAULT_SHORT;
        } else if (object.equals(int.class)) {
            return DEFAULT_INT;
        } else if (object.equals(long.class)) {
            return DEFAULT_LONG;
        } else if (object.equals(float.class)) {
            return DEFAULT_FLOAT;
        } else if (object.equals(double.class)) {
            return DEFAULT_DOUBLE;
        }

        return null;
    }

    /**
     * Get the bean.
     * @return The bean
     */
    public Object getBean() {
        return this.bean;
    }

    /**
     * Get the bean class.
     * 
     * <p>
     * The bean class may be a super-class of the bean, and is likely to be an abstract class or
     * interface.
     * </p>
     * 
     * @return The bean class. It is expected that the value returned by {@link #getBean()} is
     *         either null, or that {@link #getBeanClass()}.{@link Class#isInstance(Object)
     *         isInstance(}{@link #getBean()}{@link Class#isInstance(Object) )} will always return
     *         true.
     */
    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    /**
     * Get the bean implementation class.
     * 
     * @return The the bean implementation class. The class returned by this method should always be
     *         the same class or a subclass of the class returned by {@link #getBeanClass()}. When
     *         {@link #getBean()} returns a non-null value it is expected that {@link #getBean()}.
     *         {@link Object#getClass() getClass()} == {@link #getImplClass()}.
     */
    public Class<?> getImplClass() {
        assert bean == null || beanClass.isInstance(bean) : bean + " is not a " + beanClass;
        return bean == null ? beanClass : bean.getClass();
    }

    /**
     * Get the property name.
     * 
     * @return The property name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Determine if a list or array property is readable.
     * 
     * @return True if the property is a list or array, and is readable, false if not.
     */
    private boolean isListOrArrayAndCanReadOrWrite() {
        Class<?> implClass = getImplClass();

        if (!implClass.isArray() && !List.class.isAssignableFrom(implClass)) {
            return false;
        }
        
        if (name.length() == 0) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            if (!Character.isDigit(name.charAt(i))) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Determine if a list or array property is readable.
     * 
     * @return True if the property is a list or array, and is readable, false if not.
     */
    private Boolean canReadOrWriteSimple() {
        if (name == null) {
            // self reference
            return true;
        }

        Class<?> implClass = getImplClass();
        
        if (implClass == null) {
            return false;
        }

        if (isListOrArrayAndCanReadOrWrite()) {
            return true;
        }
        
        if (Map.class.isAssignableFrom(implClass)) {
            return true;
        }

        return null;
    }

    /**
     * Determine if the bean property is readable.
     * 
     * @return True if the property is readable, false if not.
     */
    public boolean canRead() {
        Boolean simple = canReadOrWriteSimple();
        
        if (simple != null) {
            return simple;
        }

        return ObjectPropertyUtils.getReadMethod(getImplClass(), name) != null;
    }

    /**
     * Determine if the property is writable.
     * 
     * @return True if the property is writable, false if not.
     */
    public boolean canWrite() {
        Boolean simple = canReadOrWriteSimple();
        
        if (simple != null) {
            return simple;
        }

        return ObjectPropertyUtils.getWriteMethod(getImplClass(), name) != null;
    }
    
    /**
     * Get the property value for a specific bean property of a known bean class.
     * 
     * @return The property value for the specific bean property on the given bean.
     */
    public Object getFromReadMethod() {
        Class<?> implClass = getImplClass();

        Method readMethod = ObjectPropertyUtils.getReadMethod(implClass, name);

        if (readMethod == null) {
            if (isWarning()) {
                IllegalArgumentException missingPropertyException = new IllegalArgumentException("No property name '"
                        + name + "' is readable on " +
                        (implClass == beanClass ? implClass.toString() : "impl " + implClass + ", bean " + beanClass));
                LOG.warn(missingPropertyException);
            }

            return null;
        }

        try {
            return readMethod.invoke(bean);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Illegal access invoking property read method " + readMethod, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new IllegalStateException("Unexpected invocation target exception invoking property read method "
                    + readMethod, e);
        }
    }

    /**
     * Get the property value for a specific bean property of a known bean class.
     * 
     * @return The property value for the specific bean property on the given bean.
     */
    public Object get() {
        if (name == null) {
            return bean;
        }

        Class<?> implClass = getImplClass();

        if (implClass == null || bean == null) {
            return null;

        } else if (implClass.isArray()) {
            return getArray(bean, name);
        
        } else if (List.class.isAssignableFrom(implClass)) {
            return getList((List<?>) bean, name);
        
        } else if (Map.class.isAssignableFrom(implClass)) {
            return getMap((Map<?, ?>) bean, name);
        
        } else {
            return getFromReadMethod();
        }
    }

    /**
     * Get the type of a specific property on a collection.
     * 
     * @return The type of the referenced element in the collection, if non-null. When null, the
     *         parameterized type of the collection will be returned, or Object if the collection is
     *         not parameterized. If this is not a reference to an indexed collection, the null is
     *         returned.
     */
    private Class<?> getCollectionPropertyType() {
        Class<?> implClass = getImplClass();
        boolean isMap = Map.class.isAssignableFrom(implClass);
        boolean isList = List.class.isAssignableFrom(implClass);

        Object refBean;

        if (isMap) {
            refBean = getMap((Map<?, ?>) bean, name);
        } else if (isList) {
            refBean = getList((List<?>) bean, name);
        } else {
            return null;
        }

        if (refBean != null) {
            return refBean.getClass();
        }

        if (beanType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) beanType;
            Type valueType = parameterizedType.getActualTypeArguments()[isList ? 0 : 1];

            if (valueType instanceof Class) {
                return (Class<?>) valueType;
            }
        }

        return Object.class;
    }
    
    /**
     * Get the type of a specific property on a given bean class.
     * 
     * @return The type of the specific property on the given bean class.
     */
    private Class<?> getPropertyTypeFromReadOrWriteMethod() {
        Class<?> implClass = getImplClass();

        Method readMethod = ObjectPropertyUtils.getReadMethod(implClass, name);
        Method writeMethod;

        if (readMethod == null) {

            writeMethod = ObjectPropertyUtils.getWriteMethod(implClass, name);
            assert writeMethod == null || writeMethod.getParameterTypes().length == 1 : "Invalid write method "
                    + writeMethod;

            if (writeMethod == null && isWarning()) {
                IllegalArgumentException missingPropertyException = new IllegalArgumentException("No property name '"
                        + name + "' is readable or writable on " +
                        (implClass == beanClass ? implClass.toString() : "impl " + implClass + ", bean " + beanClass));
                LOG.warn(missingPropertyException);
            }

            return writeMethod == null ? null : writeMethod.getParameterTypes()[0];

        } else {
            Class<?> returnType = readMethod.getReturnType();
            assert (writeMethod = ObjectPropertyUtils.getWriteMethod(implClass, name)) == null
                    || writeMethod.getParameterTypes()[0].isAssignableFrom(returnType) : "Property types don't match "
                    + readMethod + " " + writeMethod;
            return returnType;
        }
    }
    
    /**
     * Get the type of a specific property on the implementation class.
     * 
     * @return The type of the specific property on the implementation class.
     */
    public Class<?> getPropertyType() {
        Class<?> implClass = getImplClass();

        if (implClass == null) {
            return null;
        }

        if (name == null) {
            // self reference
            return getImplClass();
        }

        Class<?> propertyType = getCollectionPropertyType();

        if (propertyType != null) {
            return propertyType;
        } else {
            return getPropertyTypeFromReadOrWriteMethod();
        }
    }

    /**
     * Set the property to a specific value using the property's write method.
     * 
     * @param propertyValue The property value.
     */
    private void setUsingWriteMethod(Object propertyValue) {
        Class<?> implClass = getImplClass();
        Method writeMethod = ObjectPropertyUtils.getWriteMethod(implClass, name);
        
        if (writeMethod == null) {
            throw new IllegalArgumentException("No property name '" + name + "' is writable on " +
                    (implClass == beanClass ? implClass.toString() : "impl " + implClass + ", bean " + beanClass));
        }

        try {
            writeMethod.invoke(bean, propertyValue);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Illegal access invoking property write method " + writeMethod, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new IllegalStateException(
                    "Unexpected invocation target exception invoking property write method "
                            + writeMethod, e);
        }
    }

    /**
     * Set the property to a specific value.
     * 
     * @param propertyValue The property value.
     */
    public void set(Object propertyValue) {
        if (name == null) {
            throw new IllegalArgumentException("Cannot modify a self-reference");
        }

        if (bean == null) {
            throw new IllegalArgumentException("Reference is null");
        }

        propertyValue = convertToPropertyType(propertyValue);

        Class<?> implClass = getImplClass();

        if (implClass == null) {
            throw new IllegalArgumentException("No property name '" + name + "' is writable on " + beanClass);
        }

        if (implClass.isArray()) {
            setArray(bean, name, propertyValue);

        } else if (List.class.isAssignableFrom(implClass)) {
            setList((List<?>) bean, name, propertyValue);

        } else if (Map.class.isAssignableFrom(implClass)) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> uncheckedMap = (Map<Object, Object>) bean;
            uncheckedMap.put(name, propertyValue);

        } else {
            setUsingWriteMethod(propertyValue);
        }
    }

}
