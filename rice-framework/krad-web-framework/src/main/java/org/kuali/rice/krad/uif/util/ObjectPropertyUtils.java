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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

/**
 * Utility methods to get/set property values and working with objects.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class ObjectPropertyUtils {

    private static final Logger LOG = Logger.getLogger(ObjectPropertyUtils.class);

    /**
     * Internal property descriptor cache.
     * 
     * <p>
     * NOTE: WeakHashMap is used as the internal cache representation. Since class objects are used
     * as the keys, this allows property descriptors to stay in cache until the class loader is
     * unloaded, but will not prevent the class loader itself from unloading. PropertyDescriptor
     * instances do not hold hard references back to the classes they refer to, so weak value
     * maintenance is not necessary.
     * </p>
     */
    private static final Map<Class<?>, Map<String, PropertyDescriptor>> PROPERTY_DESCRIPTOR_CACHE = Collections
            .synchronizedMap(new WeakHashMap<Class<?>, Map<String, PropertyDescriptor>>(2048));

    /**
     * Get a mapping of property descriptors by property name for a bean class.
     * 
     * @param beanClass The bean class.
     * @return A mapping of all property descriptors for the bean class, by property name.
     */
    public static Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> beanClass) {
        Map<String, PropertyDescriptor> propertyDescriptors = PROPERTY_DESCRIPTOR_CACHE.get(beanClass);

        if (propertyDescriptors == null) {
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(beanClass);
            } catch (IntrospectionException e) {
                LOG.warn(
                        "Bean Info not found for bean " + beanClass, e);
                beanInfo = null;
            }

            Map<String, PropertyDescriptor> mutablePropertyDescriptorMap = new java.util.LinkedHashMap<String, PropertyDescriptor>();

            if (beanInfo != null) {
                for (PropertyDescriptor propertyDescriptor : beanInfo
                        .getPropertyDescriptors()) {
                    mutablePropertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
                }
            }

            propertyDescriptors = Collections.unmodifiableMap(mutablePropertyDescriptorMap);
            PROPERTY_DESCRIPTOR_CACHE.put(beanClass, propertyDescriptors);
        }

        return propertyDescriptors;
    }

    /**
     * Get a property descriptor from a class by property name.
     * 
     * @param beanClass The bean class.
     * @param propertyName The bean property name.
     * @return The property descriptor named on the bean class.
     */
    public static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException("Null property name");
        }

        PropertyDescriptor propertyDescriptor = getPropertyDescriptors(beanClass).get(propertyName);
        if (propertyDescriptor != null) {
            return propertyDescriptor;
        } else {
            throw new IllegalArgumentException("Property " + propertyName
                    + " not found for bean " + beanClass);
        }
    }

    /**
     * Infer the read method based on method name.
     * 
     * @param beanClass The bean class.
     * @param propertyName The property name.
     * @return The read method for the property.
     */
    private static Method getReadMethodByName(Class<?> beanClass, String propertyName) {

        try {
            return beanClass.getMethod("get" + Character.toUpperCase(propertyName.charAt(0))
                    + propertyName.substring(1));
        } catch (SecurityException e) {
            // Ignore
        } catch (NoSuchMethodException e) {
            // Ignore
        }

        try {
            Method readMethod = beanClass.getMethod("is"
                    + Character.toUpperCase(propertyName.charAt(0))
                    + propertyName.substring(1));
            
            if (readMethod.getReturnType() == Boolean.class
                    || readMethod.getReturnType() == Boolean.TYPE) {
                return readMethod;
            }
        } catch (SecurityException e) {
            // Ignore
        } catch (NoSuchMethodException e) {
            // Ignore
        }
        
        return null;
    }
    
    /**
     * Get the read method for a specific property on a bean class.
     * 
     * @param beanClass The bean class.
     * @param propertyName The property name.
     * @return The read method for the property.
     */
    public static Method getReadMethod(Class<?> beanClass, String propertyName) {
        if (propertyName == null || propertyName.length() == 0) {
            return null;
        }

        PropertyDescriptor propertyDescriptor = getPropertyDescriptors(beanClass).get(propertyName);

        if (propertyDescriptor != null) {
            Method readMethod = propertyDescriptor.getReadMethod();
            
            if (readMethod != null) {
                return readMethod;
            }
        }

        return getReadMethodByName(beanClass, propertyName);
    }

    /**
     * Get the read method for a specific property on a bean class.
     * 
     * @param beanClass The bean class.
     * @param propertyName The property name.
     * @return The read method for the property.
     */
    public static Method getWriteMethod(Class<?> beanClass, String propertyName) {
        PropertyDescriptor propertyDescriptor = ObjectPropertyUtils.getPropertyDescriptors(beanClass).get(propertyName);

        if (propertyDescriptor != null) {
            Method writeMethod = propertyDescriptor.getWriteMethod();
            assert writeMethod == null
                    || (writeMethod.getParameterTypes().length == 1 && writeMethod.getParameterTypes()[0] != null) : writeMethod;
            return writeMethod;
        } else {
            return null;
        }
    }

    /**
     * Copy properties from a string map to an object.
     * 
     * @param properties The string map. The keys of this map must be valid property path
     *        expressions in the context of the target object. The values are the string
     *        representations of the target bean properties.
     * @param object The target object, to copy the property values to.
     * @see ObjectPathExpressionParser
     */
    public static void copyPropertiesToObject(Map<String, String> properties, Object object) {
        for (Map.Entry<String, String> property : properties.entrySet()) {
            setPropertyValue(object, property.getKey(), property.getValue());
        }
    }

    /**
     * Get the type of a bean property.
     * 
     * <p>
     * Note that this method does not instantiate the bean class before performing introspection, so
     * will not dynamic initialization behavior into account. When dynamic initialization is needed
     * to accurate inspect the inferred property type, use {@link #getPropertyType(Object, String)}
     * instead of this method. This method is, however, intended for use on the implementation
     * class; to avoid instantiation simply to infer the property type, consider overriding the
     * return type on the property read method.
     * </p>
     * 
     * @param beanClass The bean class.
     * @param propertyPath A valid property path expression in the context of the bean class.
     * @return The property type referred to by the provided bean class and property path.
     * @see ObjectPathExpressionParser
     */
    public static Class<?> getPropertyType(Class<?> beanClass, String propertyPath) {
        try {
            ObjectPropertyReference.setWarning(true);
            return ObjectPropertyReference.resolvePath(null, beanClass, propertyPath, false).getPropertyType();
        } finally {
            ObjectPropertyReference.setWarning(false);
        }
    }

    /**
     * Get the type of a bean property.
     * 
     * @param object The bean instance. Use {@link #getPropertyType(Class, String)} to look up
     *        property types when an instance is not available.
     * @param propertyPath A valid property path expression in the context of the bean.
     * @return The property type referred to by the provided bean and property path.
     * @see ObjectPathExpressionParser
     */
    public static Class<?> getPropertyType(Object object, String propertyPath) {
        try {
            ObjectPropertyReference.setWarning(true);
            return ObjectPropertyReference.resolvePath(object, object.getClass(), propertyPath, false)
                    .getPropertyType();
        } finally {
            ObjectPropertyReference.setWarning(false);
        }
    }

    /**
     * Look up a property value.
     * 
     * @param object The bean instance to look up a property value for.
     * @param propertyPath A valid property path expression in the context of the bean.
     * @return The value of the property referred to by the provided bean and property path.
     * @see ObjectPathExpressionParser
     */
    @SuppressWarnings("unchecked")
    public static <T extends Object> T getPropertyValue(Object object, String propertyPath) {
        boolean trace = ProcessLogger.isTraceActive() && object != null; 
        if (trace) {
            // May be uncommented for debugging high execution count
            // ProcessLogger.ntrace(object.getClass().getSimpleName() + ":r:" + propertyPath, "", 1000);
            ProcessLogger.countBegin("bean-property-read");
        }

        try {
            ObjectPropertyReference.setWarning(true);

            return (T) ObjectPropertyReference.resolvePath(object, object.getClass(), propertyPath, false).get();

        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Error getting property '" + propertyPath + "' from " + object, e);
        } finally {
            ObjectPropertyReference.setWarning(false);
            if (trace) {
                ProcessLogger.countEnd("bean-property-read", object.getClass().getSimpleName() + ":" + propertyPath);
            }
        }

    }

    /**
     * Initialize a property value.
     * 
     * <p>
     * Upon returning from this method, the property referred to by the provided bean and property
     * path will have been initialized with a default instance of the indicated property type.
     * </p>
     * 
     * @param object The bean instance to initialize a property value for.
     * @param propertyPath A valid property path expression in the context of the bean.
     * @see #getPropertyType(Object, String)
     * @see #setPropertyValue(Object, String, Object)
     * @see ObjectPathExpressionParser
     */
    public static void initializeProperty(Object object, String propertyPath) {
        Class<?> propertyType = getPropertyType(object, propertyPath);
        try {
            setPropertyValue(object, propertyPath, propertyType.newInstance());
        } catch (InstantiationException e) {
            // just set the value to null
            setPropertyValue(object, propertyPath, null);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Unable to set new instance for property: " + propertyPath, e);
        }
    }

    /**
     * Modify a property value.
     * 
     * <p>
     * Upon returning from this method, the property referred to by the provided bean and property
     * path will have been populated with property value provided. If the propertyValue does not
     * match the type of the indicated property, then type conversion will be attempted using
     * {@link PropertyEditorManager}.
     * </p>
     * 
     * @param object The bean instance to initialize a property value for.
     * @param propertyPath A valid property path expression in the context of the bean.
     * @param propertyValue The value to populate value in the property referred to by the provided
     *        bean and property path.
     * @see ObjectPathExpressionParser
     * @throws RuntimeException If the property path is not valid in the context of the bean
     *         provided.
     */
    public static void setPropertyValue(Object object, String propertyPath, Object propertyValue) {
        if (ProcessLogger.isTraceActive() && object != null) {
            // May be uncommented for debugging high execution count
            // ProcessLogger.ntrace(object.getClass().getSimpleName() + ":w:" + propertyPath + ":", "", 1000);
            ProcessLogger.countBegin("bean-property-write");
        }

        try {
            ObjectPropertyReference.setWarning(true);

            ObjectPropertyReference.resolvePath(object, object.getClass(), propertyPath, true).set(propertyValue);

        } catch (RuntimeException e) {
            throw new IllegalArgumentException(
                    "Error setting property '" + propertyPath + "' on " + object + " with " + propertyValue, e);
        } finally {
            ObjectPropertyReference.setWarning(false);

            if (ProcessLogger.isTraceActive() && object != null) {
                ProcessLogger.countEnd("bean-property-write", object.getClass().getSimpleName() + ":" + propertyPath);
            }
        }

    }

    /**
     * Modify a property value.
     * 
     * <p>
     * Upon returning from this method, the property referred to by the provided bean and property
     * path will have been populated with property value provided. If the propertyValue does not
     * match the type of the indicated property, then type conversion will be attempted using
     * {@link PropertyEditorManager}.
     * </p>
     * 
     * @param object The bean instance to initialize a property value for.
     * @param propertyPath A property path expression in the context of the bean.
     * @param propertyValue The value to populate value in the property referred to by the provided
     *        bean and property path.
     * @param ignore True if invalid property values should be ignored, false to throw a
     *        RuntimeException if the property refernce is invalid.
     * @see ObjectPathExpressionParser
     */
    public static void setPropertyValue(Object object, String propertyPath, Object propertyValue, boolean ignoreUnknown) {
        try {
            setPropertyValue(object, propertyPath, propertyValue);
        } catch (RuntimeException e) {
            // only throw exception if they have indicated to not ignore unknown
            if (!ignoreUnknown) {
                throw e;
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("Ignoring exception thrown during setting of property '" + propertyPath + "': "
                        + e.getLocalizedMessage());
            }
        }
    }

    /**
     * Determine if a property is readable.
     * 
     * @param object The bean instance to initialize a property value for.
     * @param propertyPath A property path expression in the context of the bean.
     * @return True if the path expression resolves to a valid readable property reference in the
     *         context of the bean provided.
     */
    public static boolean isReadableProperty(Object object, String propertyPath) {
        return ObjectPropertyReference.resolvePath(object, object.getClass(), propertyPath, false).canRead();
    }

    /**
     * Determine if a property is writable.
     * 
     * @param object The bean instance to initialize a property value for.
     * @param propertyPath A property path expression in the context of the bean.
     * @return True if the path expression resolves to a valid writable property reference in the
     *         context of the bean provided.
     */
    public static boolean isWritableProperty(Object object, String propertyPath) {
        return ObjectPropertyReference.resolvePath(object, object.getClass(), propertyPath, false).canWrite();
    }

    /**
     * Private constructor - utility class only.
     */
    private ObjectPropertyUtils() {}

}
