package org.kuali.rice.core.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class for Java reflection
 */
public class ReflectionUtils {
    private ReflectionUtils() {
        throw new UnsupportedOperationException("do not instantiate");
    }

    public static <T> T getField(Object object, String name, Class<T> type) {
        Field field = org.springframework.util.ReflectionUtils.findField(object.getClass(), name, type);
        if (field == null) {
            throw  new RuntimeException("Could not obtain field '" + name + "'", new NoSuchFieldException(object.getClass() + "." + name));
        }
        org.springframework.util.ReflectionUtils.makeAccessible(field);
        return (T) org.springframework.util.ReflectionUtils.getField(field, object);
    }

    public static <T> T invokeViaReflection(Object object, String methodName, Class[] paramTypes, Object... args) {
        return invokeViaReflection(object.getClass(), object, methodName, paramTypes, args);
    }

    public static <T> T invokeViaReflection(Class clazz, Object object, String methodName, Class[] paramTypes, Object... args) {
        Method method = org.springframework.util.ReflectionUtils.findMethod(clazz, methodName, paramTypes);
        if (method == null) {
            throw new RuntimeException("Could not invoke method '" + methodName + "'", new NoSuchMethodException(clazz + "." + methodName));
        }
        org.springframework.util.ReflectionUtils.makeAccessible(method);
        return (T) org.springframework.util.ReflectionUtils.invokeMethod(method, object, args);
    }

    public static <T> T invokeViaReflection(Object object, String methodName, T defaultReturnValue, Class[] paramTypes, Object... args) {
        return invokeViaReflection(object.getClass(), object, methodName, defaultReturnValue, paramTypes, args);
    }

    public static <T> T invokeViaReflection(Class clazz, Object object, String methodName, T defaultReturnValue, Class[] paramTypes, Object... args) {
        Method method = org.springframework.util.ReflectionUtils.findMethod(clazz, methodName, paramTypes);
        if (method == null) {
            return defaultReturnValue;
        }
        return (T) org.springframework.util.ReflectionUtils.invokeMethod(method, object, args);
    }
}
