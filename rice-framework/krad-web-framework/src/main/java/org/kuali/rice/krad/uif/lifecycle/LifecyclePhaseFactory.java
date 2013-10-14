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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;

import org.kuali.rice.krad.uif.component.Component;

/**
 * Responsible for creating lifecycle tasks.
 * 
 * <p>
 * This factory recycles completed tasks to reduce object creation during the lifecycle.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class LifecyclePhaseFactory {

    private final static ThreadLocal<Map<Class<?>, Reference<Recycler<?>>>> RECYCLE = new ThreadLocal<Map<Class<?>, Reference<Recycler<?>>>>();

    /**
     * Create a new lifecycle phase processing task for performing initialization on a component.
     * 
     * @param component The component.
     * @param model The model
     */
    public static InitializeComponentPhase initialize(Component component, Object model) {
        InitializeComponentPhase initializePhase = (InitializeComponentPhase) getRecycler(
                InitializeComponentPhase.class).getInstance();
        if (initializePhase == null) {
            initializePhase = new InitializeComponentPhase();
        }
        initializePhase.prepare(component, model, Collections.<ViewLifecyclePhase> emptyList());
        return initializePhase;
    }

    /**
     * Create a new lifecycle phase processing task for performing initialization on a component.
     * 
     * @param component The component.
     * @param model The model.
     * @param parentPhase The initialize phase for the component's parent.
     */
    public static InitializeComponentPhase initialize(Component component, Object model,
            InitializeComponentPhase parentPhase) {
        InitializeComponentPhase initializePhase = (InitializeComponentPhase) getRecycler(
                InitializeComponentPhase.class).getInstance();
        if (initializePhase == null) {
            initializePhase = new InitializeComponentPhase();
        }
        initializePhase.prepare(component, model, Collections.<ViewLifecyclePhase> singletonList(parentPhase));
        return initializePhase;
    }

    /**
     * Create a new lifecycle phase processing task for applying the model to a component.
     * 
     * @param component The component.
     * @param model The model
     */
    public static ApplyModelComponentPhase applyModel(Component component, Object model) {
        return applyModel(component, model, null);
    }

    /**
     * Create a new lifecycle phase processing task for applying the model to a component.
     * 
     * @param component The component.
     * @param model The model
     * @param parent The parent component.
     */
    public static ApplyModelComponentPhase applyModel(Component component, Object model, Component parent) {
        return applyModel(component, model, parent, new HashSet<String>(), null);
    }

    /**
     * Create a new lifecycle phase processing task for applying the model to a component.
     * 
     * @param component The component.
     * @param model The model
     * @param parent The parent component.
     */
    public static ApplyModelComponentPhase applyModel(Component component, Object model, Component parent,
            Set<String> visitedIds, ApplyModelComponentPhase parentPhase) {
        ApplyModelComponentPhase applyModelPhase = (ApplyModelComponentPhase) getRecycler(
                ApplyModelComponentPhase.class).getInstance();
        if (applyModelPhase == null) {
            applyModelPhase = new ApplyModelComponentPhase();
        }
        applyModelPhase.prepare(component, model, parent, visitedIds, parentPhase);
        return applyModelPhase;
    }

    /**
     * Create a new lifecycle phase processing task for finalizing a component.
     * 
     * @param component the component instance that should be updated
     * @param model top level object containing the data
     */
    public static FinalizeComponentPhase finalize(Component component, Object model) {
        return finalize(component, model, null);
    }

    /**
     * Create a new lifecycle phase processing task for finalizing a component.
     * 
     * @param component the component instance that should be updated
     * @param model top level object containing the data
     * @param parent parent component for the component being finalized
     */
    public static FinalizeComponentPhase finalize(Component component, Object model, Component parent) {
        return finalize(component, model, parent, null);
    }

    /**
     * Create a new lifecycle phase processing task for finalizing a component.
     * 
     * @param component the component instance that should be updated
     * @param model top level object containing the data
     * @param parent parent component for the component being finalized
     */
    public static FinalizeComponentPhase finalize(Component component, Object model, Component parent,
            FinalizeComponentPhase parentPhase) {
        FinalizeComponentPhase finalizePhase = (FinalizeComponentPhase) getRecycler(
                FinalizeComponentPhase.class).getInstance();
        if (finalizePhase == null) {
            finalizePhase = new FinalizeComponentPhase();
        }

        finalizePhase.prepare(component, model, parent, parentPhase);
        return finalizePhase;
    }

    /**
     * Create a new lifecycle phase processing task for finalizing a component.
     * 
     * @param component the component instance that should be updated
     * @param model top level object containing the data
     * @param parent parent component for the component being finalized
     */
    public static RenderComponentPhase render(Component component, Object model,
            FinalizeComponentPhase finalizer, RenderComponentPhase parent, List<RenderComponentPhase> siblings) {
        RenderComponentPhase renderPhase = (RenderComponentPhase) getRecycler(
                RenderComponentPhase.class).getInstance();
        if (renderPhase == null) {
            renderPhase = new RenderComponentPhase();
        }

        renderPhase.prepare(component, model, finalizer, parent, siblings);
        return renderPhase;
    }

    /**
     * Recycle a task instance after processing.
     * 
     * @param task The task to recycle.
     */
    static void recycle(AbstractViewLifecyclePhase task) {
        getRecycler(task.getClass()).recycle(task);
    }

    @SuppressWarnings("unchecked")
    private final static <T extends AbstractViewLifecyclePhase> Recycler<T> getRecycler(Class<?> phaseClass) {
        Map<Class<?>, Reference<Recycler<?>>> recycleMap = RECYCLE.get();
        if (recycleMap == null) {
            recycleMap = new WeakHashMap<Class<?>, Reference<Recycler<?>>>();
            RECYCLE.set(recycleMap);
        }

        Reference<Recycler<?>> recyclerRef = recycleMap.get(phaseClass);
        Recycler<T> recycler = recyclerRef == null ? null : (Recycler<T>) recyclerRef.get();
        if (recycler == null) {
            recycler = new Recycler<T>();
            recycleMap.put(phaseClass, new WeakReference<Recycler<?>>(recycler));
        }

        return recycler;
    }

    private final static class Recycler<T extends AbstractViewLifecyclePhase> {

        private final Queue<T> recycleQueue = new LinkedList<T>();

        private Recycler() {}

        private void recycle(T phase) {
            phase.recycle();
            recycleQueue.offer(phase);
        }

        private T getInstance() {
            return recycleQueue.poll();
        }
    }

}
