/**
 * Copyright 2005-2018 The Kuali Foundation
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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Simple utility class for implementing an object recycling factory pattern.
 *
 * <p>Weak references to objects are held by a thread-local queue. When a process has finished working
 * with an object, the {@link #recycle} method may be offer the recycled object to the queue for
 * consideration as reusable on the same thread.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class RecycleUtils {

    // Thread local reference to recycled objects.
    private static final Map<Class<?>, Reference<Map<String, Queue<Object>>>> RECYCLE = Collections.synchronizedMap(
            new WeakHashMap<Class<?>, Reference<Map<String, Queue<Object>>>>());

    // Field cache to reduce reflection overhead during clean operations.
    private static final Map<Class<?>, List<Field>> FIELD_CACHE = Collections.synchronizedMap(
            new WeakHashMap<Class<?>, List<Field>>());

    /**
     * Private constructor - utility class only.
     */
    private RecycleUtils() {}

    /**
     * Get an instance of the given class that has previously been recycled on the same thread, if
     * an instance of available.
     *
     * @param <T> recycled instance type
     * @param c class to get instance of
     * @return instance of the given class previously recycled on the same thread, if one is
     * available. If no instance is available, then null is returned.
     */
    public static <T> T getRecycledInstance(Class<T> c) {
        return getRecycledInstance(null, c);
    }

    /**
     * Get an instance of the given class that has previously been recycled on the same thread, if
     * an instance of available.
     *
     * @param <T> recycled instance type
     * @param name bean name
     * @param c class to get instance of
     * @return instance of the given class previously recycled on the same thread, if one is
     * available. If no instance is available, then null is returned.
     */
    public static <T> T getRecycledInstance(String name, Class<T> c) {
        return c.cast(getRecycleQueue(name, c).poll());
    }

    /**
     * Get an instance of the given class that has previously been recycled on the same thread, or a
     * new instance using a default constructor if a recycled instance is not available.
     *
     * @param <T> recycled instance type
     * @param c class to get instance of
     * @return An instance of the given class previously recycled on the same thread, if one is
     * available. If no instance is available, then null is returned
     */
    public static <T> T getInstance(Class<T> c) {
        T rv = getRecycledInstance(c);

        if (rv == null) {
            try {
                rv = c.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Unabled to instantiate " + c, e);
            }
        }

        return rv;
    }

    /**
     * Recycle a instance by its class, for later retrieval in the same thread.
     *
     * <p>Note that this method does not clean the instance, it only queues it for later retrieval by
     * {@link #getRecycledInstance(Class)}. The state of the instance should be cleared before
     * passing to this method. For a flexible means to clean instances using reflection
     * {@link #clean(Object, Class)} may be considered, however note that a manually implemented
     * clean operation will generally perform faster.</p>
     *
     * @param instance instance to recycle
     */
    public static void recycle(Object instance) {
        recycle(null, instance);
    }

    /**
     * Recycle a instance by its class and given name, for later retrieval in the same thread.
     *
     * @see RecycleUtils#recycle(java.lang.Object)
     *
     * @param name bean name
     * @param instance instance to recycle
     */
    public static void recycle(String name, Object instance) {
        if (instance != null) {
            getRecycleQueue(name, instance.getClass()).offer(instance);
        }
    }

    /**
     * Recycle a instance by the given class and given name, for later retrieval in the same thread.
     *
     * @see RecycleUtils#recycle(java.lang.Object)
     *
     * @param name bean name
     * @param instance instance to recycle
     * @param recycleClass class to register the instance under
     */
    public static void recycle(String name, Object instance, Class<?> recycleClass) {
        if (instance != null) {
            getRecycleQueue(name, recycleClass).offer(instance);
        }
    }

    /**
     * Clean all instance fields.
     *
     * @param <T> recycled instance type
     * @param instance instance to clean
     */
    public static <T> void clean(T instance) {
        clean(instance, Object.class);
    }

    /**
     * Clean all instance fields, walking up the class hierarchy to the indicated super class.
     *
     * @param <T> recycled instance type
     * @param instance instance to clean
     * @param top point in the class hierarchy at which to stop cleaning fields
     */
    public static <T> void clean(T instance, Class<? super T> top) {
        Class<?> c = instance.getClass();
        while (c != null && c != top && top.isAssignableFrom(c)) {

            List<Field> fields;
            synchronized (FIELD_CACHE) {
                // Get within synchronized, because FIELD_CACHE is a WeakHashMap
                fields = FIELD_CACHE.get(c);
                if (fields == null) {
                    Field[] declared = c.getDeclaredFields();
                    fields = new ArrayList<Field>(declared.length);

                    // Don't clean static fields.
                    for (Field field : fields) {
                        if ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
                            field.setAccessible(true);
                            fields.add(field);
                        }
                    }

                    fields = Collections.unmodifiableList(fields);
                    FIELD_CACHE.put(c, fields);
                }
            }

            for (Field field : fields) {
                try {
                    Class<?> type = field.getType();

                    if (type.isPrimitive()) {

                        if (type == Integer.TYPE) {
                            field.set(instance, 0);
                        } else if (type == Boolean.TYPE) {
                            field.set(instance, false);
                        } else if (type == Long.TYPE) {
                            field.set(instance, 0L);
                        } else if (type == Character.TYPE) {
                            field.set(instance, '\0');
                        } else if (type == Double.TYPE) {
                            field.set(instance, 0.0);
                        } else if (type == Float.TYPE) {
                            field.set(instance, 0.0f);
                        } else if (type == Short.TYPE) {
                            field.set(instance, (short) 0);
                        } else if (type == Byte.TYPE) {
                            field.set(instance, (byte) 0);
                        }
                    } else {
                        field.set(instance, null);
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Unexpected error setting " + field, e);
                }
            }

            c = c.getSuperclass();
        }
    }

    /**
     * Get a recycle queue by class.
     *
     * @param c class to get a recycle queue for
     */
    private static Queue<Object> getRecycleQueue(String name, Class<?> c) {
        Reference<Map<String, Queue<Object>>> recycleMapRef = RECYCLE.get(c);
        Map<String, Queue<Object>> recycleMap = null;

        if (recycleMapRef != null) {
            recycleMap = recycleMapRef.get();
        }

        if (recycleMap == null) {
            recycleMap = new HashMap<String, Queue<Object>>();
            recycleMapRef = new WeakReference<Map<String, Queue<Object>>>(recycleMap);
            synchronized (RECYCLE) {
                RECYCLE.put(c, recycleMapRef);
            }
        }

        Queue<Object> recycleQueue = recycleMap.get(name);
        if (recycleQueue == null) {
            recycleQueue = new ConcurrentLinkedQueue<Object>();
            synchronized (recycleMap) {
                recycleMap.put(name, recycleQueue);
            }
        }

        return recycleQueue;
    }
}
