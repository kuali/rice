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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.kuali.rice.krad.datadictionary.Copyable;

/**
 * Proxy invocation handler for delaying deep copy for framework objects that may not need to be
 * fully traversed by each transaction.
 * 
 * <p>
 * Proxied objects served by this handler will refer to the original source object until a
 * potentially read-write method is invoked. Once such a method is invoked, then the original source
 * is copied to a new object on the fly and the call is forwarded to the copy.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DelayedCopyableHandler implements InvocationHandler {

    private static final String COPY = "copy";

    private final Copyable original;
    private Copyable copy;

    DelayedCopyableHandler(Copyable original) {
        this.original = original;
    }

    /**
     * Intercept method calls, and copy the original source object as needed. The determination that
     * a method is read-write is made based on the method name and/or return type as follows:
     * 
     * <ul>
     * <li>Methods starting with "get" or "is", are considered read-only</li>
     * <li>Methods returning Copyable, List, Map, or an array, are considered read-write regardless
     * of name</li>
     * </ul>
     * 
     * {@inheritDoc}
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?> returnType = method.getReturnType();
        boolean atomic = copy == null && (COPY.equals(methodName) ||
                ((methodName.startsWith("get") || methodName.startsWith("is"))
                        && !Copyable.class.isAssignableFrom(returnType)
                        && !List.class.isAssignableFrom(returnType)
                        && !Map.class.isAssignableFrom(returnType)
                        && !returnType.isArray()));
        ProcessLogger.ntrace("delay-" + (copy != null ? "dup" : atomic ? "atomic" : "copy") +
                ":", ":" + methodName + ":" + original.getClass().getSimpleName(), 1000);

        if (copy == null && !atomic) {
            copy = CopyUtils.copy(original);
        }

        try {
            return method.invoke(copy == null ? original : copy, args);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw e.getCause();
            } else {
                throw e;
            }
        }
    }

    /**
     * Copy a source object if needed, and unwrap from the proxy.
     *
     * @param source The object to unwrap.
     * @return The non-proxied bean represented by source, copied if needed. When source is not
     *         copyable, or not proxied, it is returned as-is.
     */
    static <T> T unwrap(T source) {
        if (!(source instanceof Copyable)) {
            return source;
        }

        Class<?> sourceClass = source.getClass();
        if (!Proxy.isProxyClass(sourceClass)) {
            return source;
        }

        InvocationHandler handler = Proxy.getInvocationHandler(source);
        if (!(handler instanceof DelayedCopyableHandler)) {
            return source;
        }

        DelayedCopyableHandler sourceHandler = (DelayedCopyableHandler) handler;
        if (sourceHandler.copy == null) {
            sourceHandler.copy = CopyUtils.copy(sourceHandler.original);
        }

        @SuppressWarnings("unchecked")
        T rv = (T) sourceHandler.copy;
        return unwrap(rv);
    }

    /**
     * Determins if a source object is a delayed copy proxy that hasn't been copied yet.
     *
     * @param source The object to check.
     * @return True if source is a delayed copy proxy instance, and hasn't been copied yet.
     */
    public static boolean isPendingDelayedCopy(Copyable source) {
        Class<?> sourceClass = source.getClass();

        // Unwrap proxied source objects from an existing delayed copy handler, if applicable
        if (Proxy.isProxyClass(sourceClass)) {
            InvocationHandler handler = Proxy.getInvocationHandler(source);
            if (handler instanceof DelayedCopyableHandler) {
                DelayedCopyableHandler sourceHandler = (DelayedCopyableHandler) handler;
                return sourceHandler.copy == null;
            }
        }

        return false;
    }

    /**
     * Get a proxy instance providing delayed copy behavior on a source component.
     * @param source The source object
     * @return proxy instance wrapping the object
     */
    public static Copyable getDelayedCopy(Copyable source) {
        Class<?> sourceClass = source.getClass();

        // Unwrap proxied source objects from an existing delayed copy handler, if applicable
        if (Proxy.isProxyClass(sourceClass)) {
            InvocationHandler handler = Proxy.getInvocationHandler(source);
            if (handler instanceof DelayedCopyableHandler) {
                DelayedCopyableHandler sourceHandler = (DelayedCopyableHandler) handler;
                return getDelayedCopy(sourceHandler.copy == null
                        ? sourceHandler.original : sourceHandler.copy);
            }
        }

        return (Copyable) Proxy.newProxyInstance(sourceClass.getClassLoader(),
                getMetadata(sourceClass).interfaces, new DelayedCopyableHandler(source));
    }

    /**
     * Internal field cache meta-data node, for reducing interface lookup overhead.
     * 
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    private static class ClassMetadata {

        /**
         * All interfaces implemented by the class.
         */
        private final Class<?>[] interfaces;

        /**
         * Create a new field reference for a target class.
         * 
         * @param targetClass The class to inspect for meta-data.
         */
        private ClassMetadata(Class<?> targetClass) {
            List<Class<?>> interfaceList = new ArrayList<Class<?>>();

            Class<?> currentClass = targetClass;
            while (currentClass != Object.class && currentClass != null) {
                for (Class<?> ifc : currentClass.getInterfaces()) {
                    if (!interfaceList.contains(ifc)) {
                        interfaceList.add(ifc);
                    }
                }
                currentClass = currentClass.getSuperclass();
            }

            // Seal index collections to prevent external modification.
            interfaces = interfaceList.toArray(new Class<?>[interfaceList.size()]);
        }
    }

    /**
     * Static cache for reducing annotated field lookup overhead.
     */
    private static final Map<Class<?>, ClassMetadata> CLASS_META_CACHE =
            Collections.synchronizedMap(new WeakHashMap<Class<?>, ClassMetadata>());

    /**
     * Get copy metadata for a class.
     * @param targetClass The class.
     * @return Copy metadata for the class.
     */
    private static final ClassMetadata getMetadata(Class<?> targetClass) {
        ClassMetadata metadata = CLASS_META_CACHE.get(targetClass);

        if (metadata == null) {
            CLASS_META_CACHE.put(targetClass, metadata = new ClassMetadata(targetClass));
        }

        return metadata;
    }

}
