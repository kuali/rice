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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.lifecycle.initialize.AddComponentStateToViewIndexTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.AssignIdsTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.PopulatePathTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.PrepareForCacheTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.SortContainerTask;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.springframework.util.StringUtils;

/**
 * Lifecycle phase implementation representing the pre-process phase. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PreProcessElementPhase extends ViewLifecyclePhaseBase {

    /**
     * {@inheritDoc}
     */
    @Override
    public LifecycleEvent getEventToNotify() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.CREATED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.CACHED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.PRE_PROCESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializePendingTasks(Queue<ViewLifecycleTask<?>> tasks) {
        tasks.offer(LifecycleTaskFactory.getTask(AssignIdsTask.class, this));
        tasks.offer(LifecycleTaskFactory.getTask(PopulatePathTask.class, this));
        tasks.offer(LifecycleTaskFactory.getTask(SortContainerTask.class, this));
        tasks.offer(LifecycleTaskFactory.getTask(PrepareForCacheTask.class, this));
        tasks.offer(LifecycleTaskFactory.getTask(AddComponentStateToViewIndexTask.class, this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeSuccessors(Queue<ViewLifecyclePhase> successors) {
        LifecycleElement element = getElement();

        String nestedPathPrefix;
        Component nestedParent;
        if (element instanceof Component) {
            nestedParent = (Component) element;
            nestedPathPrefix = "";
        } else {
            nestedParent = getParent();
            nestedPathPrefix = element.getPath() + ".";
        }

        Map<String, LifecycleElement> nestedElements =
                ViewLifecycleUtils.getElementsForLifecycle(element, getViewPhase()); 
        for (Entry<String, LifecycleElement> nestedElementEntry : nestedElements.entrySet()) {
            String nestedPath = nestedPathPrefix + nestedElementEntry.getKey();
            LifecycleElement nestedElement = nestedElementEntry.getValue();

            if (nestedElement != null &&
                    !UifConstants.ViewStatus.CACHED.equals(nestedElement.getViewStatus())) {
                successors.offer(LifecyclePhaseFactory.preProcess(
                        nestedElement, nestedPath, nestedParent));
            }
        }
        ViewLifecycleUtils.recycleElementMap(nestedElements);
    }

    /**
     * Prepares the phase for recycled use.
     * 
     * @param element lifecycle element
     * @param path path to the element relative to the parent component
     * @param parent the parent component
     */
    public void prepare(LifecycleElement element, String path, Component parent) {
        super.prepare(element, null, path, parent, null);
    }

}
