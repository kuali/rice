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

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.lifecycle.finalize.SetReadOnlyOnDataBindingTask;
import org.kuali.rice.krad.uif.lifecycle.model.AfterEvaluateExpressionTask;
import org.kuali.rice.krad.uif.lifecycle.model.ApplyAuthAndPresentationLogicTask;
import org.kuali.rice.krad.uif.lifecycle.model.ComponentDefaultApplyModelTask;
import org.kuali.rice.krad.uif.lifecycle.model.EvaluateExpressionsTask;
import org.kuali.rice.krad.uif.lifecycle.model.HelperCustomApplyModelTask;
import org.kuali.rice.krad.uif.lifecycle.model.PopulateComponentContextTask;
import org.kuali.rice.krad.uif.lifecycle.model.RefreshStateModifyTask;
import org.kuali.rice.krad.uif.lifecycle.model.SuffixIdFromContainerTask;
import org.kuali.rice.krad.uif.lifecycle.model.SyncClientSideStateTask;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Lifecycle phase processing task for applying the model to a component.
 *
 * <p>
 * During the apply model phase each component of the tree if invoked to setup any state based on
 * the given model data
 * </p>
 *
 * <p>
 * Part of the view lifecycle that applies the model data to the view. Should be called after the
 * model has been populated before the view is rendered. The main things that occur during this
 * phase are:
 * <ul>
 * <li>Generation of dynamic fields (such as collection rows)</li>
 * <li>Execution of conditional logic (hidden, read-only, required settings based on model values)</li>
 * </ul>
 * </p>
 *
 * <p>
 * The update phase can be called multiple times for the view's lifecycle (typically only once per
 * request)
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ApplyModelComponentPhase extends ViewLifecyclePhaseBase {

    /**
     * Set of IDs that have been visited during the view's apply model phase.
     *
     * <p>
     * This reference is typically shared by all component apply model phases.
     * </p>
     */
    private Set<String> visitedIds;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void recycle() {
        super.recycle();
        visitedIds = null;
    }

    /**
     * Create a new lifecycle phase processing task for applying the model to a element.
     *
     * @param element The element the model should be applied to
     * @param model Top level object containing the data
     * @param path The path to the element relative to the parent element
     * @param refreshPaths list of paths to run lifecycle on when executing a refresh lifecycle
     * @param parent The parent element
     * @param nextPhase The phase to queue directly upon completion of this phase, if applicable
     * @param visitedIds Tracks components ids that have been seen for adjusting duplicates
     */
    protected void prepare(LifecycleElement element, Object model, String path, List<String> refreshPaths,
            Component parent, ViewLifecyclePhaseBase nextPhase, Set<String> visitedIds) {
        super.prepare(element, model, path, refreshPaths, parent, nextPhase);

        this.visitedIds = visitedIds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.APPLY_MODEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.INITIALIZED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.MODEL_APPLIED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LifecycleEvent getEventToNotify() {
        return null;
    }

    /**
     * Visit a lifecycle element.
     *
     * @param element The lifecycle element (component or layout manager) to mark as visisted.
     * @return True if the element has been visited before, false if this was the first visit.
     */
    public boolean visit(LifecycleElement element) {
        if (visitedIds.contains(element.getId())) {
            return true;
        }

        synchronized (visitedIds) {
            return !visitedIds.add(element.getId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeSkipLifecyclePendingTasks(Queue<ViewLifecycleTask<?>> tasks) {
        super.initializeSkipLifecyclePendingTasks(tasks);

        if ((getParent() != null) && StringUtils.isNotBlank(getParent().getContainerIdSuffix())) {
            tasks.add(LifecycleTaskFactory.getTask(SuffixIdFromContainerTask.class, this));
        }
    }

    /**
     * Applies the model data to a component of the View instance
     *
     * <p>
     * TODO: Revise - The component is invoked to to apply the model data. Here the component can
     * generate any additional fields needed or alter the configured fields. After the component is
     * invoked a hook for custom helper service processing is invoked. Finally the method is
     * recursively called for all the component children
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    protected void initializePendingTasks(Queue<ViewLifecycleTask<?>> tasks) {
        if ((getParent() != null) && StringUtils.isNotBlank(getParent().getContainerIdSuffix())) {
            tasks.add(LifecycleTaskFactory.getTask(SuffixIdFromContainerTask.class, this));
        }

        tasks.add(LifecycleTaskFactory.getTask(PopulateComponentContextTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(EvaluateExpressionsTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(AfterEvaluateExpressionTask.class, this));
        
        tasks.add(LifecycleTaskFactory.getTask(SyncClientSideStateTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(ApplyAuthAndPresentationLogicTask.class, this));

        if (ViewLifecycle.isRefreshComponent(getViewPhase(), getViewPath())) {
            tasks.add(LifecycleTaskFactory.getTask(RefreshStateModifyTask.class, this));
        }

        tasks.add(LifecycleTaskFactory.getTask(ComponentDefaultApplyModelTask.class, this));
        getElement().initializePendingTasks(this, tasks);
        tasks.offer(LifecycleTaskFactory.getTask(RunComponentModifiersTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(HelperCustomApplyModelTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(SetReadOnlyOnDataBindingTask.class, this));
    }

    /**
     * Define all nested lifecycle components, and component prototypes, as successors.
     * 
     * {@inheritDoc}
     */
    @Override
    protected ViewLifecyclePhase initializeSuccessor(LifecycleElement nestedElement, String nestedPath,
            Component parent) {
        ApplyModelComponentPhase applyModelPhase = LifecyclePhaseFactory.applyModel(nestedElement, getModel(),
                nestedPath, getRefreshPaths(), parent, null, visitedIds);

        if (nestedElement.isInitialized()) {
            return applyModelPhase;
        }

        return LifecyclePhaseFactory.initialize(nestedElement, getModel(), nestedPath, getRefreshPaths(), parent,
                applyModelPhase);
    }

}
