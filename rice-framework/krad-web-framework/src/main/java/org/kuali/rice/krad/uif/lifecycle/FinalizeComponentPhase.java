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

import java.util.ArrayList;
import java.util.Collections;
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
 * Lifecycle phase processing task for applying the model to a component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FinalizeComponentPhase extends AbstractViewLifecyclePhase {

    private Component parent;
    private RenderComponentPhase renderPhase;

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#recycle()
     */
    @Override
    protected void recycle() {
        super.recycle();
        parent = null;
        renderPhase = null;
    }

    /**
     * Create a new lifecycle phase processing task for finalizing a component.
     * 
     * @param component The component instance the model should be applied to
     * @param model Top level object containing the data
     * @param parentPhase The finalize phase processed on the parent component.
     */
    protected void prepare(Component component, Object model, Component parent,
            FinalizeComponentPhase parentPhase) {
        super.prepare(component, model, parentPhase == null ?
                Collections.<ViewLifecyclePhase> emptyList() :
                Collections.<ViewLifecyclePhase> singletonList(parentPhase));
        this.parent = parent;

        if (ViewLifecycle.isRenderInLifecycle()) {
            ArrayList<RenderComponentPhase> topList = new ArrayList<RenderComponentPhase>(1);
            this.renderPhase = LifecyclePhaseFactory.render(
                    component, model, this, null, Collections.unmodifiableList(topList));
            topList.add(this.renderPhase);
        }
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
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#notifyCompleted()
     */
    @Override
    protected void notifyCompleted() {
        super.notifyCompleted();
        assert renderPhase == null || renderPhase.isComplete();
        renderPhase = null;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#isComplete()
     */
    @Override
    public boolean isComplete() {
        return super.isComplete() &&
                (renderPhase == null || renderPhase.isComplete());
    }

    /**
     * @return the parent
     */
    public Component getParent() {
        return this.parent;
    }

    /**
     * Update state of the component and perform final preparation for rendering.
     * 
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#initializePendingTasks(java.util.Queue)
     */
    @Override
    protected void initializePendingTasks(Queue<ViewLifecycleTask> tasks) {
        tasks.add(LifecycleTaskFactory.getTask(SetReadOnlyOnDataBindingTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(InvokeFinalizerTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(ComponentDefaultFinalizeTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(HelperCustomFinalizeTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(RunComponentModifiersTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(AddViewTemplatesTask.class, this));
    }

    /**
     * Define all nested lifecycle components, and component prototypes, as successors.
     * 
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#initializeSuccessors(java.util.List)
     */
    @Override
    protected void initializeSuccessors(Queue<ViewLifecyclePhase> successors) {
        Component component = getComponent();
        Object model = getModel();

        List<Component> nestedComponents = component.getComponentsForLifecycle();
        List<RenderComponentPhase> renderPhases = null;

        if (ViewLifecycle.isRenderInLifecycle()) {
            renderPhases = new ArrayList<RenderComponentPhase>(nestedComponents.size());
        }

        // initialize nested components
        for (Component nestedComponent : nestedComponents) {
            if (nestedComponent != null) {
                FinalizeComponentPhase nestedFinalizePhase = LifecyclePhaseFactory.finalize(
                        nestedComponent, model, component, this);

                if (ViewLifecycle.isRenderInLifecycle()) {
                    RenderComponentPhase nestedRenderPhase = LifecyclePhaseFactory.render(
                            nestedComponent, model, nestedFinalizePhase, this.renderPhase,
                            renderPhases);
                    renderPhases.add(nestedRenderPhase);
                    nestedFinalizePhase.renderPhase = nestedRenderPhase;
                }

                successors.add(nestedFinalizePhase);
            }
        }

        if (successors.isEmpty() && renderPhase != null) {
            successors.add(renderPhase);
        }
    }

}
