/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.lifecycle;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;

/**
 * Responsible for creating lifecycle tasks.
 * 
 * <p>
 * This factory recycles completed tasks to reduce object creation during the lifecycle.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class LifecycleTaskFactory {

    private final static ThreadLocal<Map<Class<?>, Reference<Recycler<?>>>> RECYCLE = new ThreadLocal<Map<Class<?>, Reference<Recycler<?>>>>();

    /**
     * Get a task instance by class.
     * 
     * @param taskClass The task class.
     * @param phase The lifecycle phase.
     * @return A lifecycle processing task for the indicated phase, ready for processing.
     */
    public static <T extends AbstractViewLifecycleTask> T getTask(Class<T> taskClass, ViewLifecyclePhase phase) {
        return taskClass.cast(getRecycler(taskClass).getInstance(phase));
    }

    /**
     * Recycle a task instance after processing.
     * 
     * @param task The task to recycle.
     */
    static void recycle(AbstractViewLifecycleTask task) {
        getRecycler(task.getClass()).recycle(task);
    }

    @SuppressWarnings("unchecked")
    private final static <T extends AbstractViewLifecycleTask> Recycler<T> getRecycler(Class<?> taskClass) {
        Map<Class<?>, Reference<Recycler<?>>> recycleMap = RECYCLE.get();
        if (recycleMap == null) {
            recycleMap = new WeakHashMap<Class<?>, Reference<Recycler<?>>>();
            RECYCLE.set(recycleMap);
        }

        Reference<Recycler<?>> recyclerRef = recycleMap.get(taskClass);
        Recycler<T> recycler = recyclerRef == null ? null : (Recycler<T>) recyclerRef.get();
        if (recycler == null) {
            recycler = new Recycler<T>((Class<T>) taskClass);
            recycleMap.put(taskClass, new WeakReference<Recycler<?>>(recycler));
        }

        return recycler;
    }

    private final static class Recycler<T extends AbstractViewLifecycleTask> {

        private final Queue<T> recycleQueue = new LinkedList<T>();
        private final Constructor<T> constructor;

        private Recycler(Class<T> taskClass) {
            try {
                constructor = taskClass.getDeclaredConstructor(ViewLifecyclePhase.class);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(taskClass
                        + " doesn't define an available lifecycle phase constructor.", e);
            } catch (SecurityException e) {
                throw new IllegalArgumentException(taskClass
                        + " doesn't define an available lifecycle phase constructor.", e);
            }
            constructor.setAccessible(true);
        }

        private void recycle(T task) {
            task.recycle();
            recycleQueue.offer(task);
        }

        private T getInstance(ViewLifecyclePhase phase) {
            T task = recycleQueue.poll();

            if (task == null) {
                try {
                    task = constructor.newInstance(phase);
                } catch (InstantiationException e) {
                    throw new IllegalStateException("Error creating lifecycle task", e);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Error creating lifecycle task", e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException("Error creating lifecycle task", e);
                }
            } else {
                task.setPhase(phase);
            }

            return task;
        }
    }

}
