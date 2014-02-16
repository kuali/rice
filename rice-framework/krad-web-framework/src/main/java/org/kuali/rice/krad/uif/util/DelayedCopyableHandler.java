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
 * TODO mark don't forget to fill this in. 
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
     * This overridden method ...
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
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
                ":", ":"+methodName+":"+original.getClass().getSimpleName(), 1000);
        
        if (copy == null && !atomic) {
            copy = original.copy();
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
