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
package org.kuali.rice.krad.uif.lifecycle.initialize;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecycleTask;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Assign a unique ID to the component, if one has not already been assigned.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AssignIdsTask extends AbstractViewLifecycleTask {

    /**
     * Reusable linked queue for walking the lifecycle phase tree while generating an ID, without
     * object creation.
     */
    private static final ThreadLocal<Queue<ViewLifecyclePhase>> TAIL_QUEUE = new ThreadLocal<Queue<ViewLifecyclePhase>>() {
        @Override
        protected Queue<ViewLifecyclePhase> initialValue() {
            return new LinkedList<ViewLifecyclePhase>();
        }
    };

    /**
     * Create a task to assign component IDs during the initialize phase.
     * 
     * @param phase The initialize phase for the component.
     */
    public AssignIdsTask(ViewLifecyclePhase phase) {
        super(phase);
    }

    /**
     * Generate a new ID for a lifecycle element at the current phase.
     * 
     * @param element The lifecycle element for which to generate an ID.
     * @return An ID, unique within the current view, for the given element.
     */
    private String generateId(LifecycleElement element) {
        final int prime = 6971;
        int hash = element.getClass().getName().hashCode();
        Queue<ViewLifecyclePhase> phaseQueue = TAIL_QUEUE.get();
        try {
            phaseQueue.offer(getPhase());
            while (!phaseQueue.isEmpty()) {
                ViewLifecyclePhase phase = phaseQueue.poll();
                
                Component component = phase.getComponent();
                hash *= prime;
                if (component != null) {
                    hash += component.getClass().getName().hashCode();
                    
                    String id = component.getId();
                    hash *= prime;
                    if (id != null) {
                        hash += id.hashCode();
                    }
                }

                hash = prime * hash + phase.getIndex();
                
                phaseQueue.addAll(phase.getPredecessors());
            }
        } finally {
            phaseQueue.clear();
        }

        String id;
        do {
            hash *= 4507;
            id = Integer.toString(hash, 36);
        } while (!ViewLifecycle.getView().getViewIndex().observeAssignedId(id));

        return id;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecycleTask#performLifecycleTask()
     */
    @Override
    protected void performLifecycleTask() {
        Component component = getPhase().getComponent();

        if (StringUtils.isBlank(component.getId())) {
            component.setId(UifConstants.COMPONENT_ID_PREFIX + generateId(component));
        }

        if (component instanceof Container) {
            LayoutManager layoutManager = ((Container) component).getLayoutManager();

            if ((layoutManager != null) && StringUtils.isBlank(layoutManager.getId())) {
                layoutManager.setId(UifConstants.COMPONENT_ID_PREFIX + generateId(layoutManager));
            }
        }
    }

}
