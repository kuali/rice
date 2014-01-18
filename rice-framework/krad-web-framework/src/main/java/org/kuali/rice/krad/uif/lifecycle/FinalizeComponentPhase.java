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
package org.kuali.rice.krad.uif.lifecycle;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Queue;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.lifecycle.finalize.AddViewTemplatesTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.ComponentDefaultFinalizeTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.FinalizeViewTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.HelperCustomFinalizeTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.InvokeFinalizerTask;
import org.kuali.rice.krad.uif.util.LifecycleElement;

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

    /**
     * Lifecycle phase to render this component after finalization, if in-lifecycle rendering is
     * enabled.
     * 
     * @see ViewLifecycle#isRenderInLifecycle()
     */
    private RenderComponentPhase renderPhase;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void recycle() {
        super.recycle();
        renderPhase = null;
    }

    /**
     * Creates a new lifecycle phase processing task for finalizing a component.
     * 
     * @param element The component instance the model should be applied to
     * @param model Top level object containing the data
     * @param path The path to the element relative to its parent.
     * @param parent The parent component.
     */
    protected void prepare(LifecycleElement element, Object model, String path, Component parent) {
        super.prepare(element, model, path, parent, null);
    }

    /**
     * {@inheritDoc}
     * @return UifConstants.ViewPhases.FINALIZE
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.FINALIZE;
    }

    /**
     * {@inheritDoc}
     * @return UifConstants.ViewStatus.MODEL_APPLIED
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.MODEL_APPLIED;
    }

    /**
     * {@inheritDoc}
     * @return UifConstants.ViewStatus.FINAL
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.FINAL;
    }

    /**
     * {@inheritDoc}
     * @return LifecycleEvent.LIFECYCLE_COMPLETE
     */
    @Override
    public LifecycleEvent getEventToNotify() {
        return LifecycleEvent.LIFECYCLE_COMPLETE;
    }

    /**
     * Update state of the component and perform final preparation for rendering.
     * {@inheritDoc}
     */
    @Override
    protected void initializePendingTasks(Queue<ViewLifecycleTask<?>> tasks) {
        tasks.add(LifecycleTaskFactory.getTask(InvokeFinalizerTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(ComponentDefaultFinalizeTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(AddViewTemplatesTask.class, this));
        tasks.offer(LifecycleTaskFactory.getTask(FinalizeViewTask.class, this));
        getElement().initializePendingTasks(this, tasks);
        tasks.offer(LifecycleTaskFactory.getTask(RunComponentModifiersTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(HelperCustomFinalizeTask.class, this));
    }

    /**
     * Define all nested lifecycle components, and component prototypes, as successors.
     * {@inheritDoc}
     */
    @Override
    protected void initializeSuccessors(Queue<ViewLifecyclePhase> successors) {
        LifecycleElement element = getElement();
        Object model = getModel();

        String nestedPathPrefix;
        Component nestedParent;
        if (element instanceof Component) {
            nestedParent = (Component) element;
            nestedPathPrefix = "";
        } else {
            nestedParent = getParent();
            nestedPathPrefix = element.getPath() + ".";
        }

        // initialize nested components
        int pendingChildren = 0;

        for (Entry<String, LifecycleElement> nestedComponentEntry :
                ViewLifecycleUtils.getElementsForLifecycle(element, getViewPhase()).entrySet()) {
            String nestedPath = nestedPathPrefix + nestedComponentEntry.getKey();
            LifecycleElement nestedElement = nestedComponentEntry.getValue();

            if (nestedElement != null && !nestedElement.isFinal()) {
                pendingChildren++;
                
                FinalizeComponentPhase nestedFinalizePhase = LifecyclePhaseFactory
                        .finalize(nestedElement, model, nestedPath, nestedParent);
                if (nestedElement.isModelApplied()) {
                    successors.add(nestedFinalizePhase);
                    continue;
                }
                
                ApplyModelComponentPhase nestedApplyModelPhase = LifecyclePhaseFactory
                        .applyModel(nestedElement, model, nestedPath, nestedParent,
                                nestedFinalizePhase, new HashSet<String>());
                if (nestedElement.isInitialized()) {
                    successors.add(nestedApplyModelPhase);
                    continue;
                }
                
                InitializeComponentPhase nestedInitializePhase = LifecyclePhaseFactory
                        .initialize(nestedElement, model, nestedPath, nestedParent,
                                nestedApplyModelPhase);
                successors.add(nestedInitializePhase);
            }
        }

        if (ViewLifecycle.isRenderInLifecycle()) {
            RenderComponentPhase parentRenderPhase = null;
            
            ViewLifecyclePhase predecessor = getPredecessor();
            if (predecessor instanceof FinalizeComponentPhase) {
                parentRenderPhase = ((FinalizeComponentPhase) predecessor).renderPhase;
            }

            renderPhase = LifecyclePhaseFactory.render(this, parentRenderPhase, pendingChildren);
        }

        if (successors.isEmpty() && renderPhase != null) {
            successors.add(renderPhase);
        }
    }

}
