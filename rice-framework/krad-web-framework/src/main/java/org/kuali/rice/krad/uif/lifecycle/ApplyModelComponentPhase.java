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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.lifecycle.model.ApplyAuthAndPresentationLogicTask;
import org.kuali.rice.krad.uif.lifecycle.model.ComponentDefaultApplyModelTask;
import org.kuali.rice.krad.uif.lifecycle.model.EvaluateExpressionsTask;
import org.kuali.rice.krad.uif.lifecycle.model.HelperCustomApplyModelTask;
import org.kuali.rice.krad.uif.lifecycle.model.PopulateComponentContextTask;
import org.kuali.rice.krad.uif.lifecycle.model.SyncClientSideStateTask;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.View;

/**
 * Lifecycle phase processing task for applying the model to a component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ApplyModelComponentPhase extends AbstractViewLifecyclePhase {

    private Component parent;
    private Set<String> visitedIds;
    private Map<String, Object> commonContext;

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#recycle()
     */
    @Override
    protected void recycle() {
        super.recycle();
        parent = null;
        visitedIds = null;
        commonContext = null;
    }

    /**
     * Create a new lifecycle phase processing task for applying the model to a component.
     * 
     * @param component The component instance the model should be applied to
     * @param model Top level object containing the data
     * @param parent The parent component.
     * @param visitedIds Tracks components ids that have been seen for adjusting duplicates.
     * @param parentPhase The apply model phase processed on the parent component.
     */
    protected void prepare(Component component, Object model, Component parent,
                Set<String> visitedIds, ApplyModelComponentPhase parentPhase) {
        super.prepare(component, model, parentPhase == null ?
                Collections.<ViewLifecyclePhase> emptyList() :
                Collections.<ViewLifecyclePhase> singletonList(parentPhase));
        this.parent = parent;
        this.visitedIds = visitedIds;
        
        Map<String, Object> commonContext = new HashMap<String, Object>();

        View view = ViewLifecycle.getView();
        Map<String, Object> viewContext = view.getContext();
        if (viewContext != null) {
            commonContext.putAll(view.getContext());
        }

        commonContext.put(UifConstants.ContextVariableNames.THEME_IMAGES, view.getTheme().getImageDirectory());
        commonContext.put(UifConstants.ContextVariableNames.COMPONENT, getComponent());

        this.commonContext = Collections.unmodifiableMap(commonContext);
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getViewPhase()
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.APPLY_MODEL;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getStartViewStatus()
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.INITIALIZED;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getEndViewStatus()
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.MODEL_APPLIED;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getEventToNotify()
     */
    @Override
    public LifecycleEvent getEventToNotify() {
        return null;
    }

    /**
     * Gets global objects for the context map and pushes them to the context for the component
     * 
     * @param view view instance for component
     * @param component component instance to push context to
     */
    public Map<String, Object> getCommonContext() {
        return commonContext;
    }

    /**
     * @return the parent
     */
    public Component getParent() {
        return this.parent;
    }
    
    /**
     * Visit a lifecycle element.
     * 
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
     * Applies the model data to a component of the View instance
     * 
     * <p> TODO: Revise -
     * The component is invoked to to apply the model data. Here the component can generate any
     * additional fields needed or alter the configured fields. After the component is invoked a
     * hook for custom helper service processing is invoked. Finally the method is recursively
     * called for all the component children
     * </p>
     * 
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#initializePendingTasks(java.util.Queue)
     */
    @Override
    protected void initializePendingTasks(Queue<ViewLifecycleTask> tasks) {
        tasks.add(LifecycleTaskFactory.getTask(PopulateComponentContextTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(EvaluateExpressionsTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(SyncClientSideStateTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(ApplyAuthAndPresentationLogicTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(ComponentDefaultApplyModelTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(HelperCustomApplyModelTask.class, this));
        tasks.add(LifecycleTaskFactory.getTask(RunComponentModifiersTask.class, this));
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

        // initialize nested components
        for (Component nestedComponent : component.getComponentsForLifecycle()) {
            if (nestedComponent != null) {
                successors.offer(LifecyclePhaseFactory.applyModel(nestedComponent, model, component, visitedIds, this));
            }
        }
    }

}
