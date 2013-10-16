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
import org.kuali.rice.krad.uif.view.ViewIndex;

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
     * <p>
     * This method used a product of primes similar to the one used for generating String hash
     * codes. In order to minimize to collisions a large prime is used, then when collisions are
     * detected a different large prime is used to generate an alternate ID.
     * </p>
     * 
     * <p>
     * The hash code that the generated ID is based on is equivalent (though not identical) to
     * taking the hash code of the string concenation of all class names, non-null IDs, and
     * successor index positions in the lifecycle phase tree for all predecessors of the current
     * phase.  This technique leads to a reliably unique ID that is also repeatable across server
     * instances and test runs.
     * </p>
     * 
     * @param element The lifecycle element for which to generate an ID.
     * @return An ID, unique within the current view, for the given element.
     * 
     * @see ViewIndex#observeAssignedId(String)
     * @see String#hashCode() for the algorithm this method is based on.
     */
    private String generateId(LifecycleElement element) {
        // Calculate a hash code based on the path to the top of the phase tree
        // without building a string.

        final int prime = 6971; // Seed prime for hashing

        // Initialize hash to the class of the lifecycle element
        int hash = element.getClass().getName().hashCode();

        // Reuse a tail recursion queue to avoid object creation overhead.
        Queue<ViewLifecyclePhase> phaseQueue = TAIL_QUEUE.get();
        try {
            // Start with the current phase.
            phaseQueue.offer(getPhase());
            while (!phaseQueue.isEmpty()) {

                // Poll the queue for the next phase to calculate
                ViewLifecyclePhase phase = phaseQueue.poll();

                // Include the class name and ID of the component
                // at the current phase to the hash
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

                // Include the index of the current phase in the successor
                // list of the predecessor phase that defined it.
                hash = prime * hash + phase.getIndex();

                // Include all predecessors in the hash.
                phaseQueue.addAll(phase.getPredecessors());
            }
        } finally {
            // Ensure that the recursion queue is clear to prevent
            // corruption by pooled thread reuse.
            phaseQueue.clear();
        }

        String id;
        do {
            // Iteratively take the product of the hash and another large prime
            hash *= 4507; // until a unique ID has been generated.
            // The use of large primes will minimize collisions, reducing the
            // likelihood of race conditions leading to components coming out
            // with different IDs on different server instances and/or test runs.
            
            // Eliminate negatives without losing precision, and express in base-36
            id = Long.toString(((long) hash) - ((long) Integer.MIN_VALUE), 36);
            
            // Use the view index to detect collisions, keep looping until an
            // id unique to the current view has been generated.
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
