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
package org.kuali.rice.krad.uif.lifecycle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.kuali.rice.krad.uif.util.RecycleUtils;

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

    private final static Map<Class<?>, Constructor<?>> TASK_CONSTRUCTOR =
            Collections.synchronizedMap(new WeakHashMap<Class<?>, Constructor<?>>());

    /**
     * Gets a task instance by class.
     * 
     * @param <T> The lifecycle task type to return.
     * @param taskClass The task class.
     * @param phase The lifecycle phase.
     * @return A lifecycle processing task for the indicated phase, ready for processing.
     */
    public static <T extends ViewLifecycleTaskBase> T getTask(Class<T> taskClass, ViewLifecyclePhase phase) {
        T task = RecycleUtils.getRecycledInstance(taskClass);

        if (task == null) {
            try {
                task = taskClass.cast(getConstructor(taskClass).newInstance(phase));
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

    /**
     * Recycles a task instance after processing.
     * 
     * @param task The task to recycle.
     */
    static void recycle(ViewLifecycleTaskBase task) {
        task.recycle();
        RecycleUtils.recycle(task);
    }

    /**
     * Gets a single-arg constructor on for the task class for creating new tasks based on a view.
     * 
     * @param taskClass The task class.
     * @return A single-arg constructor on for the task class for creating new tasks based on a
     *         view.
     */
    private final static Constructor<?> getConstructor(Class<?> taskClass) {
        Constructor<?> constructor = TASK_CONSTRUCTOR.get(taskClass);

        if (constructor == null) {
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
            TASK_CONSTRUCTOR.put(taskClass, constructor);
        }

        return constructor;
    }

}
