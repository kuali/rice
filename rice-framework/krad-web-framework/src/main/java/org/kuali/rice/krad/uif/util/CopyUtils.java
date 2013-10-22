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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;

import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.krad.datadictionary.Copyable;
import org.kuali.rice.krad.uif.component.ReferenceCopy;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.util.KRADConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a lightweight "hands-free" copy implementation to replace the need for copyProperties()
 * in building {@link LifecycleElement} implementations.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class CopyUtils {

    private static Logger LOG = LoggerFactory.getLogger(CopyUtils.class);

    private static Boolean useClone;

    /**
     * Determine whether or not to use cloning during deep copy.
     * 
     * <p>
     * In Rice 2.3, the copy() implementation required developers to implement copyProperties() for
     * data dictionary and related objects in for faster copying performance than possible using
     * {@link CloneUtils}. However, copyProperties() adds overhead to the development of DD
     * implementations, in particular UIF components. To negate this overhead while achieving a
     * faster copy implementation overall, a generic deep copy mechanism leveraging the standard
     * {@link Cloneable} interface as a super-interface for {@link Copyable} is included in this
     * class. When cloning is enabled, copyProperties() is not use.
     * </p>
     * 
     * <p>
     * The cloning feature is currently experimental, and disabled by default. Eventually, however,
     * the intent is to deprecate copyProperties() and leverage the new deep clone algorithm to
     * simplify the development of new data dictionary components.
     * </p>
     * 
     * <p>
     * This value is controlled by the parameter &quot;krad.uif.copyable.useClone&quot;. By default,
     * the copyProperties() mechanism will be used.
     * </p>
     * 
     * @return True if exceptions will be thrown due to strictness violations, false if a warning
     *         should be logged instead.
     */
    public static boolean isUseClone() {
        if (useClone == null) {
            boolean defaultUseClone = false;
            Config config = ConfigContext.getCurrentContextConfig();
            useClone = config == null ? defaultUseClone : config.getBooleanProperty(
                    KRADConstants.ConfigParameters.KRAD_USE_CLONE, defaultUseClone);
        }

        return useClone;
    }

    /**
     * Thread local stack for detecting circular chains during copy.
     */
    private static final ThreadLocal<Deque<Copyable>> COPY_STACK = new ThreadLocal<Deque<Copyable>>();

    /**
     * Helper for {@link #copy(Copyable)} for preserving original copyProperties() method signature.
     * 
     * @param obj The original copyable object.
     * @param copy The copied object.
     * @param copyProperties the copyProperties() method.
     */
    private static void doCopyProperties(Copyable obj, Object copy, Method copyProperties) {
        // Detect circular chains while copying.  Without this mechanism in place,
        // a circular chain of components will result in StackOverfowError which
        // is much more difficult to debug.  This mechanism aims to simplify the
        // location of the circular chain so that it can be resolved.
        Deque<Copyable> copyStack = COPY_STACK.get();
        boolean outer = copyStack == null;
        if (outer) {
            // ComponentBase.copy() is head recursive.  When COPY_STACK is null, then
            // we are at the head.
            copyStack = new LinkedList<Copyable>();
            COPY_STACK.set(copyStack);
        }

        try {
            // Detected circular chain prior to pushing this component onto the copy stack,
            // and still push this component to include in error reporting and simplify the
            // finally block assertion that this component is at the head of the stack.
            boolean dup = copyStack.contains(obj);
            copyStack.push(obj);

            if (dup) {
                // Prevent stack overflow and report a meaningful exception if a circular
                // component chain has been defined.
                StringBuilder msg = new StringBuilder("Detected circular chain in component structure during copy");
                msg.append("\nStack: ");
                for (Copyable c : copyStack) {
                    msg.append("\n  ").append(c.getClass());

                    if (c instanceof LifecycleElement) {
                        msg.append(" ").append(((LifecycleElement) c).getId());
                    }
                }
                msg.append("\nPhase: ").append(ViewLifecycle.getPhase());
                throw new IllegalStateException(msg.toString());
            }

            try {
                copyProperties.invoke(obj, copy);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Access error copying properties with " + copyProperties);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();

                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                } else {
                    throw new IllegalStateException("Unexpected error in copyProperties()", e);
                }
            }

        } finally {
            // Take *this* component off the stack, and assert that *this* was in fact popped.
            Copyable popped = copyStack.pop();
            assert obj == popped;
            if (outer) {
                // Remove the stack from the thread when the entire tree has been copied.
                COPY_STACK.remove();
            }
        }
    }

    /**
     * Mix-in copy implementation for objects that implement the {@link Copyable} interface}
     * 
     * @param obj The object to copy.
     * @return A deep copy of the object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T copy(Copyable obj) {
        if (obj == null) {
            return null;
        }

        // Use cloning when enabled.
        // The rest of this method supports the original copyProperties() implementation.
        if (isUseClone()) {
            return (T) getDeepCopy(obj);
        }

        Class<?> copyClass = obj.getClass();
        T copy;
        try {
            copy = (T) copyClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Unabled to instantiate " + copyClass);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Access error attempting to instantiate " + copyClass);
        }

        ClassMetadata metadata = getMetadata(copyClass);
        if (metadata.copyPropertiesMethod != null) {
            doCopyProperties(obj, copy, metadata.copyPropertiesMethod);
        } else {
            LOG.warn(copyClass + "does not define a public or protected copyProperties(T) method");
        }

        return copy;
    }

    /**
     * Determine if shallow copying is available on an object.
     * 
     * @param obj The object to check.
     * @return True if {@link #getShallowCopy(Object)} may be expected to return a shallow copy of
     *         the object. False if a null return value is expected.
     */
    public static <T> boolean isShallowCopyAvailable(T obj) {
        return obj != null &&
                (isDeepCopyAvailable(obj.getClass()) ||
                getMetadata(obj.getClass()).cloneMethod != null);
    }

    /**
     * Determine if deep copying is available for a type.
     * 
     * @param type The type to check.
     * @return True if {@link #getDeepCopy(Object)} may be expected to follow references to this
     *         type. False if the type should not be deeply copied.
     */
    public static boolean isDeepCopyAvailable(Class<?> type) {
        return type != null
                && (Copyable.class.isAssignableFrom(type)
                        || List.class.isAssignableFrom(type)
                        || Map.class.isAssignableFrom(type)
                        || (type.isArray() && isDeepCopyAvailable(type.getComponentType())));
    }

    /**
     * Get a shallow copy (clone) of an object.
     * 
     * <p>
     * This method simplifies access to the clone() method.
     * </p>
     * 
     * @param obj The object to clone.
     * @return A shallow copy of obj, or null if obj is null.
     * @throws CloneNotSupportedException If copying is not available on the object, or if thrown by
     *         clone() itself. When isShallowCopyAvailable() returns true, then this exception is
     *         not expected and may be considered an internal error.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getShallowCopy(T obj) throws CloneNotSupportedException {
        if (obj == null) {
            return null;
        }

        // Create new mutable, cloneable instances of List/Map to replace wrappers
        if (!(obj instanceof Cloneable)) {
            if (obj instanceof List) {
                return (T) new ArrayList<Object>((List<Object>) obj);
            } else if (obj instanceof Map) {
                return (T) new HashMap<Object, Object>((Map<Object, Object>) obj);
            } else {
                throw new UnsupportedOperationException(
                        "Not cloneable, and not a supported collection.  This condition should not be reached.");
            }
        }

        // Bypass reflection overhead for commonly used types.
        // There is not need for these checks to be exhaustive - any Cloneable types
        // that define a public clone method and aren't specifically noted below
        // will be cloned by reflection.
        if (obj instanceof Copyable) {
            return (T) ((Copyable) obj).clone();
        }

        if (obj instanceof Object[]) {
            return (T) ((Object[]) obj).clone();
        }

        if (obj instanceof ArrayList) {
            return (T) ((ArrayList<?>) obj).clone();
        }

        if (obj instanceof LinkedList) {
            return (T) ((LinkedList<?>) obj).clone();
        }

        if (obj instanceof HashSet) {
            return (T) ((HashSet<?>) obj).clone();
        }

        if (obj instanceof HashMap) {
            return (T) ((HashMap<?, ?>) obj).clone();
        }

        // Use reflection to invoke a public clone() method on the object, if available.
        Method cloneMethod = getMetadata(obj.getClass()).cloneMethod;
        if (cloneMethod == null) {
            throw new CloneNotSupportedException(obj.getClass() + " does not define a public clone() method");
        } else {
            try {

                return (T) cloneMethod.invoke(obj);

            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Access error invoking clone()", e);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                } else if (cause instanceof CloneNotSupportedException) {
                    throw (CloneNotSupportedException) cause;
                } else {
                    throw new IllegalStateException("Unexpected error invoking clone()", e);
                }
            }
        }
    }

    /**
     * Helper for {@link #preventModification(Copyable)} and {@link #getDeepCopy(Object)} for
     * detecting whether or not to queue deep references from the current node.
     */
    private static boolean isDeep(CopyReference ref, Object source) {
        if (!(ref instanceof FieldReference)) {
            return true;
        }

        FieldReference fieldRef = (FieldReference) ref;
        Field field = fieldRef.field;
        
        if (field.isAnnotationPresent(ReferenceCopy.class)) {
            return false;
        }
 
        if (!(source instanceof Copyable) &&
                ((source instanceof Map) || (source instanceof List))) {
            Class<?> collectionType = getMetadata(fieldRef.source.getClass())
                    .collectionTypeByField.get(field);
            
            if (!Object.class.equals(collectionType)
                    && !isDeepCopyAvailable(collectionType)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Helper for {@link #getDeepCopy(Object)} to 
     * detect whether or not to copy the current node or to keep the cloned reference.
     */
    private static boolean isCopy(CopyReference ref) {
        if (!(ref instanceof FieldReference)) {
            return true;
        }

        FieldReference fieldRef = (FieldReference) ref;
        Field field = fieldRef.field;
        ReferenceCopy refCopy = (ReferenceCopy) field.getAnnotation(ReferenceCopy.class);

        return refCopy == null || refCopy.newCollectionInstance();
    }

    /**
     * Prepare a copyable object for caching by calling {@link Copyable#preventModification()} on
     * all copyable instances located by a deep traversal of the object.
     * 
     * @param obj The object to prepare for caching.
     * @return A deep copy of the object.
     */
    public static void preventModification(Copyable obj) {
        CopyState copyState = RecycleUtils.getRecycledInstance(CopyState.class);
        if (copyState == null) {
            copyState = new CopyState();
        }

        SimpleReference topReference = getSimpleReference(obj);
        try {
            copyState.queue.offer(topReference);

            while (!copyState.queue.isEmpty()) {
                CopyReference toCache = copyState.queue.poll();
                Object source = toCache.get();

                if (!isShallowCopyAvailable(source)) {
                    continue;
                }

                if (source instanceof Copyable) {
                    ((Copyable) source).preventModification();
                }

                if (isDeep(toCache, source)) {
                    copyState.queueDeepCopyReferences(source, null);
                }

                if (toCache != topReference) {
                    recycle(toCache);
                }
            }

        } finally {
            recycle(topReference);
            copyState.recycle();
        }
    }

    /**
     * Get a deep copy of an object using cloning.
     * 
     * @param obj The object to get a deep copy of.
     * @return A deep copy of the object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getDeepCopy(T obj) {
        CopyState copyState = RecycleUtils.getRecycledInstance(CopyState.class);
        if (copyState == null) {
            copyState = new CopyState();
        }

        SimpleReference topReference = getSimpleReference(obj);
        try {
            copyState.queue.offer(topReference);

            while (!copyState.queue.isEmpty()) {
                CopyReference toCopy = copyState.queue.poll();
                Object source = toCopy.get();

                if (!isShallowCopyAvailable(source) || !isCopy(toCopy)) {
                    continue;
                }

                toCopy.set(copyState.getTarget(source, isDeep(toCopy, source)));

                if (toCopy != topReference) {
                    recycle(toCopy);
                }
            }

            return (T) topReference.get();

        } finally {
            recycle(topReference);
            copyState.recycle();
        }
    }

    /**
     * Holds copy state for use with {@link #getDeepCopy(Object)}.
     */
    private static class CopyState {

        private final Queue<CopyReference> queue = new LinkedList<CopyReference>();
        private final Map<Object, Object> cache = new IdentityHashMap<Object, Object>();

        /**
         * Get a shallow copy of the source object appropriate for the current copy operation.
         * 
         * @param source The original source object to copy.
         * @return A shallow copy of the source object.
         */
        private Object getTarget(Object source, boolean queueDeepReferences) {
            boolean useCache = source != Collections.EMPTY_LIST && source != Collections.EMPTY_MAP;

            Object target = useCache ? cache.get(source) : null;

            if (target == null) {
                try {
                    target = getShallowCopy(source);
                } catch (CloneNotSupportedException e) {
                    throw new IllegalStateException("Unexpected cloning error during shallow copy", e);
                }

                if (queueDeepReferences) {
                    queueDeepCopyReferences(source, target);
                }
                
                if (useCache) {
                    cache.put(source, target);
                }
            }

            return target;
        }

        /**
         * Queues references for deep copy after performing the shallow copy.
         * 
         * @param source The original source object at the current node.
         * @param target A shallow copy of the source object, to be analyzed for deep copy.
         */
        private void queueDeepCopyReferences(Object source, Object target) {
            Class<?> type = source.getClass();
            if (!isDeepCopyAvailable(type)) {
                return;
            }

            // Don't queue references if the source has already been seen
            if (cache.containsKey(source)) {
                return;
            } else if (target == null) {
                cache.put(source, source);
            }

            if (Copyable.class.isAssignableFrom(type)) {
                for (Field field : getMetadata(type).cloneFields) {
                    queue.offer(getFieldReference(source, target, field));
                }

                // Used fields for deep copying, even if List or Map is implemented.
                // The wrapped list/map should be picked up as a field during deep copy.
                return;
            }

            if (List.class.isAssignableFrom(type)) {
                List<?> sourceList = (List<?>) source;
                List<?> targetList = (List<?>) target;

                for (int i = 0; i < sourceList.size(); i++) {
                    queue.offer(getListReference(sourceList, targetList, i));
                }
            }

            if (Map.class.isAssignableFrom(type)) {
                Map<?, ?> sourceMap = (Map<?, ?>) source;
                Map<?, ?> targetMap = (Map<?, ?>) target;

                for (Map.Entry<?, ?> sourceEntry : sourceMap.entrySet()) {
                    queue.offer(getMapReference(sourceEntry, targetMap));
                }
            }

            if (type.isArray()) {
                for (int i = 0; i < Array.getLength(source); i++) {
                    queue.offer(getArrayReference(source, target, i));
                }
            }
        }

        /**
         * Clear queue and cache, and recycle this state object.
         */
        private void recycle() {
            queue.clear();
            cache.clear();
            RecycleUtils.recycle(this);
        }
    }

    /**
     * Represents a abstract reference to a targeted value for use during deep copying.
     */
    private interface CopyReference {

        /**
         * Retrieve the targeted value for populating the reference.
         * 
         * <p>
         * This value returned by this method will typically come from a source object, then after
         * copy operations have been performed {@link #set(Object)} will be called to populate the
         * target value on the destination object.
         * </p>
         * 
         * @return The targeted value for populating the reference.
         */
        Object get();

        /**
         * Modify the value targeted by the reference.
         * 
         * <p>
         * This value passed to this method will have typically come {@link #get()}. After copy
         * operations have been performed, this method will be called to populate the target value
         * on the destination object.
         * </p>
         * 
         * @param value The value to modify the reference as.
         */
        void set(Object value);

        /**
         * Clean the reference for recycling.
         */
        void clean();

    }

    /**
     * Recycle a copy reference for later use, once the copy has been performed.
     */
    private static void recycle(CopyReference ref) {
        ref.clean();
        RecycleUtils.recycle(ref);
    }

    /**
     * Simple copy reference for holding top-level value to be later inspected and returned. Values
     * held by this class will be modified in place.
     */
    private static class SimpleReference implements CopyReference {

        private Object value;

        /**
         * Get the value.
         * 
         * @return The value.
         */
        @Override
        public Object get() {
            return value;
        }

        /**
         * Set the a value.
         * 
         * @param obj The value to set.
         */
        @Override
        public void set(Object value) {
            this.value = value;
        }

        @Override
        public void clean() {
            this.value = null;
        }
    }

    /**
     * Get a simple reference for temporary use while deep cloning.
     * 
     * <p>
     * Call {@link #recycle(CopyReference)} when done working with the reference.
     * </p>
     * 
     * @param value The initial object to refer to.
     * 
     * @return A simple reference for temporary use while deep cloning.
     */
    private static SimpleReference getSimpleReference(Object value) {
        SimpleReference ref = RecycleUtils.getRecycledInstance(SimpleReference.class);

        if (ref == null) {
            ref = new SimpleReference();
        }

        ref.value = value;

        return ref;
    }

    /**
     * Reference implementation for a field on an object.
     */
    private static class FieldReference implements CopyReference {

        private Object source;
        private Object target;
        private Field field;

        /**
         * Get a value from the field on the source object.
         * 
         * @return The value referred to by the field on the source object.
         */
        @Override
        public Object get() {
            try {
                return field.get(source);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Access error attempting to get from " + field, e);
            }
        }

        /**
         * Set a value for the field on the target object.
         * 
         * @param obj The value to set for the field on the target object.
         */
        @Override
        public void set(Object value) {
            try {
                field.set(target, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Access error attempting to set " + field, e);
            }
        }

        @Override
        public void clean() {
            source = null;
            target = null;
            field = null;
        }

    }

    /**
     * Get a field reference for temporary use while deep cloning.
     * 
     * <p>
     * Call {@link #recycle(CopyReference)} when done working with the reference.
     * </p>
     * 
     * @param source The source object.
     * @param target The target object.
     * @param field The field to use as the reference target.
     * 
     * @return A field reference for temporary use while deep cloning.
     */
    private static FieldReference getFieldReference(Object source, Object target, Field field) {
        FieldReference ref = RecycleUtils.getRecycledInstance(FieldReference.class);

        if (ref == null) {
            ref = new FieldReference();
        }

        ref.source = source;
        ref.target = target;
        ref.field = field;

        return ref;
    }

    /**
     * Reference implementation for an entry in an array.
     */
    private static class ArrayReference implements CopyReference {

        private Object source;
        private Object target;
        private int index = -1;

        /**
         * Get the value of the indicated entry in the source array.
         * 
         * @return The value of the indicated entry in the source array.
         */
        @Override
        public Object get() {
            return Array.get(source, index);
        }

        /**
         * Modify the value of the indicated entry in the target array.
         * 
         * @param value The value to set on the indicated entry in the target array.
         */
        @Override
        public void set(Object value) {
            Array.set(target, index, value);
        }

        @Override
        public void clean() {
            source = null;
            target = null;
            index = -1;
        }
    }

    /**
     * Get an array reference for temporary use while deep cloning.
     * 
     * <p>
     * Call {@link #recycle(CopyReference)} when done working with the reference.
     * </p>
     * 
     * @param source The source array.
     * @param target The target array.
     * @param index The array index.
     * 
     * @return An array reference for temporary use while deep cloning.
     */
    private static ArrayReference getArrayReference(Object source, Object target, int index) {
        ArrayReference ref = RecycleUtils.getRecycledInstance(ArrayReference.class);

        if (ref == null) {
            ref = new ArrayReference();
        }

        ref.source = source;
        ref.target = target;
        ref.index = index;

        return ref;
    }

    /**
     * Reference implementation for an item in a list.
     */
    private static class ListReference implements CopyReference {

        private List<Object> source;
        private List<Object> target;
        private int index = -1;

        /**
         * Get the value of the indicated item in the source array.
         * 
         * @return The value of the indicated item in the source array.
         */
        @Override
        public Object get() {
            return source.get(index);
        }

        /**
         * Modify the list item.
         * 
         * @param The value to modify the list item as.
         */
        @Override
        public void set(Object value) {
            target.set(index, value);
        }

        @Override
        public void clean() {
            source = null;
            target = null;
            index = -1;
        }
    }

    /**
     * Get a list reference for temporary use while deep cloning.
     * 
     * <p>
     * Call {@link #recycle(CopyReference)} when done working with the reference.
     * </p>
     * 
     * @param source The source list.
     * @param target The target list.
     * @param index The index of the list item.
     * 
     * @return A list reference for temporary use while deep cloning.
     */
    @SuppressWarnings("unchecked")
    private static ListReference getListReference(List<?> source, List<?> target, int index) {
        ListReference ref = RecycleUtils.getRecycledInstance(ListReference.class);

        if (ref == null) {
            ref = new ListReference();
        }

        ref.source = (List<Object>) source;
        ref.target = (List<Object>) target;
        ref.index = index;

        return ref;
    }

    /**
     * Reference implementation for an entry in a map.
     */
    private static class MapReference implements CopyReference {

        private Map.Entry<Object, Object> sourceEntry;
        private Map<Object, Object> target;

        /**
         * Get the value of the map entry.
         * 
         * @return The value of the map entry.
         */
        @Override
        public Object get() {
            return sourceEntry.getValue();
        }

        /**
         * Modify the map entry.
         * 
         * @param The value to modify the map entry with.
         */
        @Override
        public void set(Object value) {
            target.put(sourceEntry.getKey(), value);
        }

        @Override
        public void clean() {
            sourceEntry = null;
            target = null;
        }
    }

    /**
     * Get a map reference for temporary use while deep cloning.
     * 
     * <p>
     * Call {@link #recycle(CopyReference)} when done working with the reference.
     * </p>
     * 
     * @param sourceEntry The source entry.
     * @param target The target map.
     * 
     * @return A map reference for temporary use while deep cloning.
     */
    @SuppressWarnings("unchecked")
    private static MapReference getMapReference(Map.Entry<?, ?> sourceEntry, Map<?, ?> target) {
        MapReference ref = RecycleUtils.getRecycledInstance(MapReference.class);

        if (ref == null) {
            ref = new MapReference();
        }

        ref.sourceEntry = (Map.Entry<Object, Object>) sourceEntry;
        ref.target = (Map<Object, Object>) target;

        return ref;
    }

    /**
     * Internal field cache meta-data node, for reducing field lookup overhead.
     * 
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    private static class ClassMetadata {

        /**
         * The public clone method, if defined for this type.
         */
        private final Method cloneMethod;

        /**
         * The protected copyProperties() method, if defined for this type.
         */
        private final Method copyPropertiesMethod;

        /**
         * All fields on the class that should have a shallow copy performed during a deep copy
         * operation.
         */
        private final List<Field> cloneFields;

        /**
         * Mapping from field to generic collection type, for Map and List fields that should be
         * deep copied.
         */
        private final Map<Field, Class<?>> collectionTypeByField;

        /**
         * Create a new field reference for a target class.
         * 
         * @param targetClass The class to inspect for meta-data.
         */
        private ClassMetadata(Class<?> targetClass) {

            Method targetCloneMethod = null;
            if (Cloneable.class.isAssignableFrom(targetClass) && !targetClass.isArray()) {
                try {
                    targetCloneMethod = targetClass.getMethod("clone");
                } catch (NoSuchMethodException e) {
                    LOG.warn("Target " + targetClass + " is cloneable, but does not define a public clone() method", e);
                } catch (SecurityException e) {
                    LOG.warn("Target " + targetClass + " is cloneable, but the public clone() method is restricted", e);
                }
            }
            cloneMethod = targetCloneMethod;

            // Create mutable collections for building meta-data indexes.
            List<Field> cloneList = new ArrayList<Field>();
            Map<Field, Class<?>> collectionTypeMap = new HashMap<Field, Class<?>>();

            Method targetCopyPropertiesMethod = null;
            Class<?> currentClass = targetClass;
            while (currentClass != Object.class && currentClass != null) {

                if (targetCopyPropertiesMethod == null) {
                    try {
                        Method copyProperties = currentClass.getDeclaredMethod("copyProperties", Object.class);

                        int mod = copyProperties.getModifiers();
                        if ((mod & Modifier.PUBLIC) == Modifier.PUBLIC
                                || (mod & Modifier.PROTECTED) == Modifier.PROTECTED) {
                            copyProperties.setAccessible(true);
                            targetCopyPropertiesMethod = copyProperties;
                        }
                    } catch (NoSuchMethodException e) {} catch (SecurityException e) {
                        LOG.warn("copyProperties() method is restricted for " + currentClass, e);
                    }
                }

                for (Field currentField : currentClass.getDeclaredFields()) {
                    if ((currentField.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                        continue;
                    }

                    Class<?> type = currentField.getType();

                    if (type.isArray()
                            || isDeepCopyAvailable(type)
                            || Cloneable.class.isAssignableFrom(type)) {
                        currentField.setAccessible(true);
                        cloneList.add(currentField);
                    }

                    boolean isList = List.class.isAssignableFrom(type);
                    boolean isMap = Map.class.isAssignableFrom(type);
                    if (!isList && !isMap) {
                        continue;
                    }

                    Type genericType = currentField.getGenericType();
                    Class<?> collectionType = Object.class;
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) genericType;
                        Type valueType = parameterizedType.getActualTypeArguments()[isList ? 0 : 1];

                        if (valueType instanceof Class) {
                            collectionType = (Class<?>) valueType;
                        }
                    }

                    if (collectionType.equals(Object.class) || isDeepCopyAvailable(collectionType)) {
                        collectionTypeMap.put(currentField, collectionType);
                    }
                }

                currentClass = currentClass.getSuperclass();
            }

            // Seal index collections to prevent external modification.
            cloneFields = Collections.unmodifiableList(cloneList);
            collectionTypeByField = Collections.unmodifiableMap(collectionTypeMap);
            copyPropertiesMethod = targetCopyPropertiesMethod;
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
