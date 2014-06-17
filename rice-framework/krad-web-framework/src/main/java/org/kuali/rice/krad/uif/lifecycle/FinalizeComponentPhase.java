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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.lifecycle.finalize.AddRefreshComponentDataAttributesTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.AddViewTemplatesTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.ComponentDefaultFinalizeTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.FinalizeViewTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.HelperCustomFinalizeTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.InvokeFinalizerTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.RegisterPropertyEditorTask;
import org.kuali.rice.krad.uif.lifecycle.model.RefreshStateModifyTask;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.RecycleUtils;

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
     * @param path The path to the element relative to its parent
     * @param refreshPaths list of paths to run lifecycle on when executing a refresh lifecycle
     * @param parent The parent component
     */
    protected void prepare(LifecycleElement element, Object model, String path, List<String> refreshPaths,
            Component parent) {
        super.prepare(element, model, path, refreshPaths, parent, null);
    }

    /**
     * {@inheritDoc}
     *
     * @return UifConstants.ViewPhases.FINALIZE
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.FINALIZE;
    }

    /**
     * {@inheritDoc}
     *
     * @return UifConstants.ViewStatus.MODEL_APPLIED
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.MODEL_APPLIED;
    }

    /**
     * {@inheritDoc}
     *
     * @return UifConstants.ViewStatus.FINAL
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.FINAL;
    }

    /**
     * {@inheritDoc}
     *
     * @return LifecycleEvent.LIFECYCLE_COMPLETE
     */
    @Override
    public LifecycleEvent getEventToNotify() {
        return LifecycleEvent.LIFECYCLE_COMPLETE;
    }

    /**
     * Verify that the render phase has no pending children.
     */
    @Override
    protected void verifyCompleted() {
        super.verifyCompleted();
        
        if (renderPhase != null) {
            renderPhase.verifyCompleted();
        }
    }

    /**
     * Update state of the component and perform final preparation for rendering.
     *
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
        tasks.add(LifecycleTaskFactory.getTask(RegisterPropertyEditorTask.class, this));
        if (ViewLifecycle.isRefreshComponent(getViewPhase(), getViewPath())) {
            tasks.add(LifecycleTaskFactory.getTask(AddRefreshComponentDataAttributesTask.class, this));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeSuccessors(Queue<ViewLifecyclePhase> successors) {
        super.initializeSuccessors(successors);

        if (ViewLifecycle.isRenderInLifecycle()) {
            RenderComponentPhase parentRenderPhase = null;

            ViewLifecyclePhase predecessor = getPredecessor();
            if (predecessor instanceof FinalizeComponentPhase) {
                parentRenderPhase = ((FinalizeComponentPhase) predecessor).renderPhase;
            }
            
            @SuppressWarnings("unchecked")
            Set<String> pendingChildren = RecycleUtils.getInstance(LinkedHashSet.class);
            for (ViewLifecyclePhase successor : successors) {
                boolean skipSuccessor;
                if (successor instanceof ViewLifecyclePhaseBase) {
                    skipSuccessor = ((ViewLifecyclePhaseBase) successor).shouldSkipLifecycle(); 
                } else {
                    // TODO: consider moving shouldSkipLifecycle to public interface
                    skipSuccessor = successor.getElement().skipLifecycle();
                }

                // Don't queue successors that will be skipped.
                // Doing so would cause notification issues for the render phase.
                if (skipSuccessor) {
                    continue;
                }
                
                // Queue the successor, with strict validation that it hasn't already been queued
                if (!pendingChildren.add(successor.getParentPath())) {
                    ViewLifecycle.reportIllegalState("Successor is already pending " + pendingChildren + "\n"
                            + successor + "\n" + this);
                }
            }

            renderPhase = LifecyclePhaseFactory.render(this, getRefreshPaths(), parentRenderPhase, pendingChildren);
            trace("create-render " + getElement().getId() + " " + pendingChildren);
        }

        if (successors.isEmpty() && renderPhase != null) {
            successors.add(renderPhase);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewLifecyclePhase initializeSuccessor(LifecycleElement nestedElement, String nestedPath,
            Component parent) {
        FinalizeComponentPhase finalizeComponentPhase = LifecyclePhaseFactory.finalize(nestedElement, getModel(),
                nestedPath, getRefreshPaths(), parent);
        if (nestedElement.isModelApplied()) {
            return finalizeComponentPhase;
        }

        ApplyModelComponentPhase applyModelPhase = LifecyclePhaseFactory.applyModel(nestedElement, getModel(),
                nestedPath, getRefreshPaths(), parent, finalizeComponentPhase, new HashSet<String>());

        if (nestedElement.isInitialized()) {
            return applyModelPhase;
        }

        return LifecyclePhaseFactory.initialize(nestedElement, getModel(), nestedPath, getRefreshPaths(), parent,
                applyModelPhase);
    }

}
