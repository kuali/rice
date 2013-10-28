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

import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.RecycleUtils;

/**
 * Responsible for creating lifecycle tasks.
 * 
 * <p>
 * This factory recycles completed tasks to reduce object creation during the lifecycle.
 * </p>
h * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class LifecyclePhaseFactory {

    /**
     * Create a new lifecycle phase processing task for performing initialization on a component.
     * 
     * @param component The component.
     * @param model The model
     * @return A lifecycle processing task for processing the initialize phase on the component.
     */
    public static InitializeComponentPhase initialize(Component component, Object model) {
        return initialize(component, model, 0, null, null);
    }
    
    /**
     * Create a new lifecycle phase processing task for performing initialization on a component.
     * 
     * @param component The component.
     * @param model The model
     * @param parent The parent component.
     * @param index The index of the phase within the nested component list.
     * @param nextPhase The applyModel phase to spawn after the successful completion of the
     *        initialize phase.
     * @return A lifecycle processing task for processing the initialize phase on the component.
     */
    public static InitializeComponentPhase initialize(Component component, Object model,
            int index, Component parent, ApplyModelComponentPhase nextPhase) {
        InitializeComponentPhase initializePhase = RecycleUtils.getInstance(InitializeComponentPhase.class);
        initializePhase.prepare(component, model, index, parent, nextPhase);
        return initializePhase;
    }

    /**
     * Create a new lifecycle phase processing task for applying the model to a component.
     * 
     * @param component The component.
     * @param model The model
     * @return A lifecycle processing task for processing the apply model phase on the component.
     */
    public static ApplyModelComponentPhase applyModel(Component component, Object model) {
        return applyModel(component, model, 0, null, null, new HashSet<String>());
    }
    
    /**
     * Create a new lifecycle phase processing task for applying the model to a component.
     * 
     * @param component The component.
     * @param model The model
     * @param parent The component.
     * @return A lifecycle processing task for processing the apply model phase on the component.
     */
    public static ApplyModelComponentPhase applyModel(Component component, Object model, Component parent) {
        return applyModel(component, model, 0, parent, null, new HashSet<String>());
    }
    
    /**
     * Create a new lifecycle phase processing task for applying the model to a component.
     * 
     * @param component The component.
     * @param model The model
     * @param parent The parent component.
     * @param index The index of the phase within the nested component list.
     * @param nextPhase The applyModel phase to spawn after the successful completion of the
     *        initialize phase.
     * @param visitedIds The set of visited IDs to track while applying model.
     * @return A lifecycle processing task for processing the apply model phase on the component.
     */
    public static ApplyModelComponentPhase applyModel(Component component, Object model,
            int index, Component parent, FinalizeComponentPhase nextPhase, Set<String> visitedIds) {
        ApplyModelComponentPhase applyModelPhase = RecycleUtils.getInstance(ApplyModelComponentPhase.class);
        applyModelPhase.prepare(component, model, index, parent, nextPhase, visitedIds);
        return applyModelPhase;
    }

    /**
     * Create a new lifecycle phase processing task for finalizing a nested component.
     * 
     * @param component The component.
     * @param model The model
     * @return A lifecycle processing task for processing the finalize phase on the component.
     */
    public static FinalizeComponentPhase finalize(Component component, Object model) {
        return finalize(component, model, 0, null);
    }
    
    /**
     * Create a new lifecycle phase processing task for finalizing a nested component.
     * 
     * @param component The component.
     * @param model The model
     * @param parent The parent component.
     * @return A lifecycle processing task for processing the finalize phase on the component.
     */
    public static FinalizeComponentPhase finalize(Component component, Object model, Component parent) {
        return finalize(component, model, 0, parent);
    }
    
    /**
     * Create a new lifecycle phase processing task for finalizing a nested component.
     * 
     * @param component The component.
     * @param model The model
     * @param parent The parent component.
     * @param index The index of the phase within the nested component list.
     * @return A lifecycle processing task for processing the finalize phase on the component.
     */
    public static FinalizeComponentPhase finalize(Component component, Object model,
            int index, Component parent) {
        FinalizeComponentPhase finalizePhase = RecycleUtils.getInstance(FinalizeComponentPhase.class);
        finalizePhase.prepare(component, model, index, parent);
        return finalizePhase;
    }

    /**
     * Create a new lifecycle phase processing task for rendering a component.
     * 
     * @param component The component to render.
     * @param model The model associated with the component.
     * @param index The position of the associated finalize phase's within its predecessor's
     *        successor queue.
     * @return A lifecycle processing task for processing the render phase on the component.
     */
    public static RenderComponentPhase render(Component component, Object model, int index) {
        RenderComponentPhase renderPhase = RecycleUtils.getInstance(RenderComponentPhase.class);
        renderPhase.prepare(component, model, index, null, 0);
        return renderPhase;
    }

    /**
     * Create a new lifecycle phase processing task for rendering a component.
     * 
     * @param finalizePhase The finalize component phase associated with this rendering phase.
     * @param parent The rendering phase for the parent of the component associated with this phase.
     * @param pendingChildren The number of child phases to expect to be queued with this phase as
     *        their rendering parent.
     * @return A lifecycle processing task for processing the render phase on the component.
     */
    public static RenderComponentPhase render(
            FinalizeComponentPhase finalizePhase, RenderComponentPhase parent, int pendingChildren) {
        RenderComponentPhase renderPhase = RecycleUtils.getInstance(RenderComponentPhase.class);
        renderPhase.prepare(finalizePhase.getComponent(), finalizePhase.getModel(),
                finalizePhase.getIndex(), parent, pendingChildren);
        return renderPhase;
    }

    /**
     * Recycle a task instance after processing.
     * 
     * @param phase The task to recycle.
     */
    static void recycle(ViewLifecyclePhaseBase phase) {
        phase.recycle();
        RecycleUtils.recycle(phase);
    }

}
