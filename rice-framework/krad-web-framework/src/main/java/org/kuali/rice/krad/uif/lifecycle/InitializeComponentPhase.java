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

import java.util.Queue;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.lifecycle.initialize.AddComponentStateToViewIndexTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.AssignIdsTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.ComponentDefaultInitializeTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.HelperCustomInitializeTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.PopulateComponentFromExpressionGraphTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.PopulateReplacersAndModifiersFromExpressionGraphTask;
import org.kuali.rice.krad.uif.view.View;

/**
 * Lifecycle phase processing task for initializing a component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InitializeComponentPhase extends ViewLifecyclePhaseBase {

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getViewPhase()
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.INITIALIZE;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getStartViewStatus()
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.CREATED;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getEndViewStatus()
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.INITIALIZED;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getEventToNotify()
     */
    @Override
    public LifecycleEvent getEventToNotify() {
        return null;
    }

    /**
     * Queues initialization phase tasks.
     */
    @Override
    protected void initializePendingTasks(Queue<ViewLifecycleTask> tasks) {
        tasks.offer(LifecycleTaskFactory.getTask(AssignIdsTask.class, this));

        if (!(getComponent() instanceof View)) {
            tasks.offer(LifecycleTaskFactory.getTask(AddComponentStateToViewIndexTask.class, this));
        }

        tasks.offer(LifecycleTaskFactory.getTask(PopulateComponentFromExpressionGraphTask.class, this));
        tasks.offer(LifecycleTaskFactory.getTask(ComponentDefaultInitializeTask.class, this));
        tasks.offer(LifecycleTaskFactory.getTask(PopulateReplacersAndModifiersFromExpressionGraphTask.class, this));

        getComponent().initializePendingTasks(this, tasks);

        tasks.offer(LifecycleTaskFactory.getTask(HelperCustomInitializeTask.class, this));
        tasks.offer(LifecycleTaskFactory.getTask(RunComponentModifiersTask.class, this));
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

        // initialize nested components
        int index = 0;
        for (Component nestedComponent : component.getComponentsForLifecycle()) {
            if (nestedComponent != null && !nestedComponent.isInitialized()) {
                successors.offer(LifecyclePhaseFactory.initialize(nestedComponent, model, index++, this));
            }
        }

        // initialize component prototypes
        for (Component nestedComponent : component.getComponentPrototypes()) {
            if (nestedComponent != null) {
                successors.add(LifecyclePhaseFactory.initialize(nestedComponent, model, index++, this));
            }
        }
    }

}
