/**
 * Copyright 2005-2013 The Kuali Foundation
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
import java.beans.PropertyEditorManager;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.kuali.rice.krad.uif.util.ObjectPathExpressionParser.PathEntry;
import org.kuali.rice.krad.util.KRADUtils;

/**
 * Represents a property reference in a path expression, for use in implementing
 * {@link ObjectPropertyUtils.PathEntry}.
 * 
 * <p>
 * This class defers the actual resolution of property references nodes in a path expression until
 * the transition between parse nodes. This facilitates traversal to the final node in the path.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @version 2.4
 * @see ObjectPropertyUtils#parsePathExpression(Object, String,
 *      org.kuali.rice.krad.uif.util.ObjectPropertyUtils.PathEntry)
 */
public class ObjectPropertyReference {

    /**
     * Reference for single use.
     */
    private static ThreadLocal<ObjectPropertyReference> TL_BUILDER_REF = new ThreadLocal<ObjectPropertyReference>();

    /**
     * Singleton reference path entry, to be used when parsing for looking up a bean property
     * without modifying.
     */
    private static ReferencePathEntry LOOKUP_REF_PATH_ENTRY = new ReferencePathEntry(false);

    /**
     * Singleton reference path entry, to be used when parsing for modifying the bean property.
     */
    private static ReferencePathEntry MUTATE_REF_PATH_ENTRY = new ReferencePathEntry(true);

    /**
     * Internal path entry implementation.
     */
    private static class ReferencePathEntry implements PathEntry {

        /**
         * Determine if {@link ObjectPropertyReference#get()} or
         * {@link ObjectPropertyReference#grow()} should be used.
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
         * 
         * @see org.kuali.rice.krad.uif.util.ObjectPathExpressionParser.PathEntry#parse(java.lang.Object,
         *      java.lang.String, boolean)
         */
        @Override
        public Object parse(Object node, String next, boolean inherit) {
            ObjectPropertyReference current = (ObjectPropertyReference) node;

            // At the initial parse node, copy to a new property reference.
            // Otherwise, we will modify the existing reference to reduce object construction
            // due to object reference parsing.
            if (next == null) {
                ObjectPropertyReference resolved = new ObjectPropertyReference();
                resolved.bean = current.bean;
                resolved.beanClass = current.beanClass;
                resolved.beanType = current.beanType;
                resolved.name = null;
                return resolved;
            }

            // Get the property type and value from the current node reference.
            // These will become the bean and bean class after transition.
            Object bean = current.get();
            Class<?> beanClass = current.getPropertyType();

            // Determine the parameterized proprty type, if applicable.
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

            return current;
        }

        /**
         * {@inheritDoc}
         * 
         * <p>
         * NOTE: Preparation is not needed for this implementation.
         * </p>
         * 
         * @see org.kuali.rice.krad.uif.util.ObjectPathExpressionParser.PathEntry#prepare(java.lang.Object)
         */
        @Override
        public Object prepare(Object prev) {
            return prev;
        }

        /**
         * Resolve the current parse node, and return as a string.
         * 
         * @see org.kuali.rice.krad.uif.util.ObjectPathExpressionParser.PathEntry#dereference(java.lang.Object)
         * @throws ClassCastException If a bean property is used as a key reference in a
         *         subexpression, and that bean property does not resolve to a String
         */
        @Override
        public String dereference(Object prev) {
            ObjectPropertyReference prevReference = (ObjectPropertyReference) prev;

            // Detect integer literal and return as-is, rather than parse/toString()
            if (prevReference.name != null && Character.isDigit(prevReference.name.charAt(0))) {
                return prevReference.name;
            }

            // Expect that any other reference resolves to null or a string
            return (String) prevReference.get();
        }
    }

    /**
     * Determine if a property name is a path or a plain property reference.
     * 
     * <p>
     * This method is used to eliminate parsing and object creation overhead when resolving an
     * object property reference with a non-complex property path.
     * </p>
     * 
     * @return True if the name is a path, false if a plain reference.
     */
    private static boolean isPath(String propertyName) {
        if (propertyName == null) {
            return false;
        }

        int length = propertyName.length();
        for (int i = 0; i < length; i++) {
            char c = propertyName.charAt(i);
            if (c != '_' && c != '$' && !Character.isLetterOrDigit(c)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the property value for a specific bean property of a known bean class.
     * 
     * @param bean The bean.
     * @param bc The bean class.
     * @param name The name of the property.
     * @return The property value for the specific bean property on the given bean.
     */
    private static Object initialize(Object propertyValue, Class<?> propertyType) {
        if (propertyValue == null) {
            if (List.class.equals(propertyType)) {
                propertyValue = new java.util.LinkedList<Object>();

            } else if (Map.class.equals(propertyType)) {
                propertyValue = new java.util.HashMap<Object, Object>();

            } else if (!String.class.equals(propertyType)) {
                try {
                    propertyValue = propertyType.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException("Failed to create new object for setting property value");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to create new object for setting property value");
                }
            }
        }
        return propertyValue;
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
        int length;
        if (array == null) {
            length = 0;
        } else {
            length = Array.getLength(array);
        }

        if ("length".equals(name) || "size".equals(name)) {
            return new Integer(length);
        }

        if (array == null) {
            return null;
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

        return Array.get(array, i);
    }

    /**
     * Set a property value in an array.
     * 
     * @param array The array.
     * @param name A string representation of the index in the array.
     * @param The property value to set in the array.
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
     * @param array The list.
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
     * @param array The list.
     * @param name A string representation of the list index.
     * @param value The value to add to the list.
     */
    @SuppressWarnings("unchecked")
    private static void setList(List<?> list, String name, Object value) {
        int length = list.size();
        int i = Integer.parseInt(name);
        while (i >= length) {
            list.add(null);
        }
        ((List<Object>) list).set(i, value);
    }

    /**
     * Get a property value from an map.
     * 
     * @param array The map.
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
     * Resolve a path expression on a bean.
     * 
     * @param bean The bean.
     * @param propertyPath The property path expression.
     * @param resolver Logical resolver implementation.
     * @param grow True to create objects while traversing the path, false to traverse class
     *        structure only when referring to null.
     * @return A reference to the final parse node involved in parsing the path expression.
     */
    public static ObjectPropertyReference resolvePath(Object bean, Class<?> beanClass, String propertyPath, boolean grow) {
        if (isPath(propertyPath)) {

            // Parse the path expression.  This requires a new reference object since object read
            // methods could potentially call this method recursively.
            ObjectPropertyReference reference = new ObjectPropertyReference();
            reference.bean = bean;
            reference.beanClass = beanClass;

            ObjectPropertyReference resolved = (ObjectPropertyReference) ObjectPathExpressionParser
                    .parsePathExpression(
                            reference,
                            propertyPath,
                            grow ? MUTATE_REF_PATH_ENTRY : LOOKUP_REF_PATH_ENTRY);

            reference.bean = resolved.bean;
            reference.beanClass = resolved.beanClass;
            reference.beanType = resolved.beanType;
            reference.name = resolved.name;
            return reference;

        } else {

            ObjectPropertyReference reference = TL_BUILDER_REF.get();
            if (reference == null) {
                TL_BUILDER_REF.set(reference = new ObjectPropertyReference());
            }
            reference.bean = bean;
            reference.beanClass = beanClass;
            reference.beanType = beanClass;
            reference.name = propertyPath;
            return reference;

        }
    }

    /**
     * Convert to a primitive type if available.
     * 
     * @param type The type to convert.
     * @return A primitive type, if available, that corresponds to the type.
     */
    private static Class<?> getPrimitiveType(Class<?> type) {
        if (Byte.class.equals(type)) {
            return Byte.TYPE;

        } else if (Short.class.equals(type)) {
            return Short.TYPE;

        } else if (Integer.class.equals(type)) {
            return Integer.TYPE;

        } else if (Long.class.equals(type)) {
            return Long.TYPE;

        } else if (Boolean.class.equals(type)) {
            return Boolean.TYPE;

        } else if (Float.class.equals(type)) {
            return Float.TYPE;

        } else if (Double.class.equals(type)) {
            return Double.TYPE;
        }

        return type;
    }

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
     * Internal private constructor.
     */
    private ObjectPropertyReference() {}

    /**
     * Convert a property value to the targeted property type.
     * 
     * @param propertyValue The property value.
     * @param propertyType The property type.
     * @return The property value, converted to the property type.
     */
    @SuppressWarnings("unchecked")
    private Object convertToPropertyType(Object propertyValue) {
        Class<?> propertyType = getPropertyType();

        if ((propertyValue instanceof String) && !propertyType.equals(String.class)) {

            // TODO: these methods, and their inversions (below) need to be either support escaping
            // or be removed.  Both have been included for equivalence with previous BeanWrapper
            // implementation.
            if (List.class.equals(propertyType)) {
                return KRADUtils.convertStringParameterToList((String) propertyValue);

            } else if (Map.class.equals(propertyType)) {
                return KRADUtils.convertStringParameterToMap((String) propertyValue);

            } else {

                // TODO: Determine if a different PropertyEditor registry exists for KRAD
                PropertyEditor editor = PropertyEditorManager.findEditor(propertyType);
                if (editor == null) {
                    throw new IllegalArgumentException("No property editor available for converting '" + propertyValue
                            + "' to " + propertyType);
                }

                editor.setAsText((String) propertyValue);
                return editor.getValue();
            }
        }

        if (propertyValue != null && !(propertyValue instanceof String) && propertyType.equals(String.class)) {

            // TODO: these methods, and their inversions (above) need to be either support escaping
            // or be removed.  Both have been included for equivalence with previous BeanWrapper
            // implementation.
            if (propertyValue instanceof List) {
                StringBuilder listStringBuilder = new StringBuilder();
                for (String item : (List<String>) propertyValue) {
                    if (listStringBuilder.length() > 0) {
                        listStringBuilder.append(',');
                    }
                    listStringBuilder.append(item);
                }
                return listStringBuilder.toString();

            } else if (Map.class.equals(propertyType)) {
                return KRADUtils.buildMapParameterString((Map<String, String>) propertyValue);

            } else {

                // TODO: Determine if a different PropertyEditor registry exists for KRAD
                PropertyEditor editor = PropertyEditorManager
                        .findEditor(getPrimitiveType(propertyValue.getClass()));
                if (editor == null) {
                    throw new IllegalArgumentException("No property editor available for converting '" + propertyValue
                            + "' from " + propertyValue.getClass());
                }

                editor.setValue(propertyValue);
                return editor.getAsText();
            }
        }

        return propertyValue;
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
     * Determine if the bean property is readable.
     * 
     * @return True if the property is readable, false if not.
     */
    public boolean canRead() {
        if (name == null) {
            // self reference
            return true;
        }

        Class<?> implClass = getImplClass();

        if (implClass.isArray() || List.class.isAssignableFrom(implClass)) {
            if ("length".equals(name) || "size".equals(name)) {
                return true;
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

        if (Map.class.isAssignableFrom(implClass)) {
            return true;
        }

        return ObjectPropertyUtils.getReadMethod(implClass, name) != null;
    }

    /**
     * Determine if the property is writable.
     * 
     * @return True if the property is writable, false if not.
     */
    public boolean canWrite() {
        if (name == null) {
            // self reference
            return false;
        }

        Class<?> implClass = getImplClass();

        if (implClass.isArray() || List.class.isAssignableFrom(implClass)) {
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

        if (Map.class.isAssignableFrom(implClass)) {
            return true;
        }

        return ObjectPropertyUtils.getWriteMethod(implClass, name) != null;
    }

    /**
     * Get the property value for a specific bean property of a known bean class.
     * 
     * @param bean The bean.
     * @param bc The bean class.
     * @param name The name of the property.
     * @return The property value for the specific bean property on the given bean.
     */
    public Object get() {
        if (name == null) {
            return bean;
        }

        Class<?> implClass = getImplClass();

        if (bean != null) {
            if (implClass.isArray()) {
                return getArray(bean, name);
            }

            if (List.class.isAssignableFrom(implClass)) {
                return getList((List<?>) bean, name);
            }

            if (Map.class.isAssignableFrom(implClass)) {
                return getMap((Map<?, ?>) bean, name);
            }
        }

        if (name != null && name.length() > 0) {
            // Inspect the first character in the name to detect literals
            char firstChar = name.charAt(0);

            // String literal
            if (firstChar == '\'' || firstChar == '\"') {
                assert firstChar == name.charAt(name.length() - 1);
                StringBuilder stringLiteral = new StringBuilder(name);
                stringLiteral.deleteCharAt(0);
                stringLiteral.deleteCharAt(stringLiteral.length() - 1);
                for (int i = 0; i < stringLiteral.length(); i++) {
                    if (stringLiteral.charAt(i) == '\\') {
                        stringLiteral.deleteCharAt(i);
                    }
                }
                return stringLiteral.toString();
            }

            // Integer literal
            if (Character.isDigit(firstChar)) {
                return Integer.parseInt(name);
            }

            // Null literal
            if ("null".equals(name)) {
                return null;
            }
        }

        if (bean == null) {
            return null;
        }

        Method readMethod = ObjectPropertyUtils.getReadMethod(implClass, name);
        if (readMethod == null) {
            throw new IllegalArgumentException("No property name '" + name + "' is readable on " +
                    (implClass == beanClass ? implClass.toString() : "impl " + implClass + ", bean " + beanClass));
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
     * Get the type of a specific property on a given bean class.
     * 
     * @param bc The bean class.
     * @param name The name of the property;
     * @return The type of the specific property on the given bean class.
     */
    public Class<?> getPropertyType() {
        Class<?> implClass = getImplClass();

        if (name == null) {
            // self reference
            return getImplClass();
        }

        if (implClass.isArray()) {
            if ("length".equals(name) || "size".equals(name)) {
                return int.class;
            } else {
                return implClass.getComponentType();
            }
        }

        boolean isMap = Map.class.isAssignableFrom(implClass);
        boolean isList = List.class.isAssignableFrom(implClass);
        if (isMap || isList) {

            if (isList && ("length".equals(name) || "size".equals(name))) {
                return int.class;
            } else {
                Object refBean = null;

                if (isMap) {
                    refBean = getMap((Map<?, ?>) bean, name);
                } else if (isList) {
                    refBean = getList((List<?>) bean, name);
                }

                if (refBean != null) {
                    return refBean.getClass();
                }

                if (!(beanType instanceof ParameterizedType)) {
                    return Object.class;
                }

                ParameterizedType parameterizedType = (ParameterizedType) beanType;
                Type valueType = parameterizedType.getActualTypeArguments()[isList ? 0 : 1];

                if (valueType instanceof Class) {
                    return (Class<?>) valueType;
                }

                return Object.class;
            }
        }

        Method readMethod = ObjectPropertyUtils.getReadMethod(implClass, name);
        Method writeMethod;
        if (readMethod == null) {
            writeMethod = ObjectPropertyUtils.getWriteMethod(implClass, name);
            assert writeMethod == null || writeMethod.getParameterTypes().length == 1 : "Invalid write method "
                    + writeMethod;
            return writeMethod == null ? null : writeMethod.getParameterTypes()[0];
        } else {
            Class<?> returnType = readMethod.getReturnType();
            assert (writeMethod = ObjectPropertyUtils.getWriteMethod(implClass, name)) == null
                    || writeMethod.getParameterTypes()[0].equals(returnType) : "Property types don't match "
                    + readMethod + " " + writeMethod;
            return returnType;
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

        if (implClass.isArray()) {
            setArray(bean, name, propertyValue);

        } else if (List.class.isAssignableFrom(implClass)) {
            setList((List<?>) bean, name, propertyValue);

        } else if (Map.class.isAssignableFrom(implClass)) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> uncheckedMap = (Map<Object, Object>) bean;
            uncheckedMap.put(name, propertyValue);

        } else {

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
    }

}
