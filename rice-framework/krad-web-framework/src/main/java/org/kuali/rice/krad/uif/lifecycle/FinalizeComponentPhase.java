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

import java.util.List;
import java.util.Queue;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.lifecycle.finalize.AddViewTemplatesTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.ComponentDefaultFinalizeTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.HelperCustomFinalizeTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.InvokeFinalizerTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.SetReadOnlyOnDataBindingTask;

/**
 * Lifecycle phase processing task for finalizing a component.
 * 
 * <p>
 * The finalize phase is the last phase before the view is rendered. Here final preparations can be
 * made based on the updated view state.
 * </p>
 * 
 * <p>
 * The finalize phase runs after the apply model phase and can be called multiple times for the
 * view's lifecylce (however typically only once per request)
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FinalizeComponentPhase extends ViewLifecyclePhaseBase {

    private RenderComponentPhase renderPhase;

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhaseBase#recycle()
     */
    @Override
    protected void recycle() {
        super.recycle();
        renderPhase = null;
    }

    /**
     * Create a new lifecycle phase processing task for finalizing a component.
     * 
     * @param component The component instance the model should be applied to
     * @param model Top level object containing the data
     * @param parentPhase The finalize phase processed on the parent component.
     */
    protected void prepare(Component component, Object model, int index, Component parent) {
        super.prepare(component, model, index, parent, null);
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getViewPhase()
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.FINALIZE;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getStartViewStatus()
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.MODEL_APPLIED;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getEndViewStatus()
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.FINAL;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getEventToNotify()
     */
    @Override
    public LifecycleEvent getEventToNotify() {
        return LifecycleEvent.LIFECYCLE_COMPLETE;
    }

    /**
     * Update state of the component and perform final preparation for rendering.
     * 
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhaseBase#initializePendingTasks(java.util.Queue)
     */
    @Override
    protected void initializePendingTasks(Queue<ViewLifecycleTask> tasks) {
        tasks.add(LifecycleTaskFactory.getTask(SetReadOnlyOnDataBindingTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(InvokeFinalizerTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(ComponentDefaultFinalizeTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(HelperCustomFinalizeTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(RunComponentModifiersTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(AddViewTemplatesTask.class, this));

        getComponent().initializePendingTasks(this, tasks);
    }

    /**
     * Define all nested lifecycle components, and component prototypes, as successors.
     * 
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhaseBase#initializeSuccessors(java.util.List)
     */
    @Override
    protected void initializeSuccessors(Queue<ViewLifecyclePhase> successors) {
        Component component = getComponent();
        Object model = getModel();

        List<Component> nestedComponents = component.getComponentsForLifecycle();
        
        // initialize nested components
        int index = 0;
        for (Component nestedComponent : nestedComponents) {
            if (nestedComponent != null) {
                FinalizeComponentPhase nestedFinalizePhase = LifecyclePhaseFactory.finalize(
                        nestedComponent, model, index, component);
                successors.add(nestedFinalizePhase);
                index++;
            }
        }

        if (ViewLifecycle.isRenderInLifecycle()) {
            RenderComponentPhase parentRenderPhase = null;
            
            ViewLifecyclePhase predecessor = getPredecessor();
            if (predecessor instanceof FinalizeComponentPhase) {
                parentRenderPhase = ((FinalizeComponentPhase) predecessor).renderPhase;
            }
            
            renderPhase = LifecyclePhaseFactory.render(this, parentRenderPhase, index);
        }

        if (successors.isEmpty() && renderPhase != null) {
            successors.add(renderPhase);
        }
    }

}
