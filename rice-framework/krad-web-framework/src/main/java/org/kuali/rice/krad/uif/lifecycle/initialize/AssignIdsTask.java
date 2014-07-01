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
package org.kuali.rice.krad.uif.lifecycle.initialize;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewIndex;

/**
 * Assign a unique ID to the component, if one has not already been assigned.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AssignIdsTask extends ViewLifecycleTaskBase<LifecycleElement> {

    /**
     * Create a task to assign component IDs during the initialize phase.
     */
    public AssignIdsTask() {
        super(LifecycleElement.class);
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
     * phase. This technique leads to a reliably unique ID that is also repeatable across server
     * instances and test runs.
     * </p>
     * 
     * <p>
     * The use of large primes by this method minimizes collisions, and therefore reduces the
     * likelihood of a race condition causing components to come out with different IDs on different
     * server instances and/or test runs.
     * </p>
     * 
     * @param element The lifecycle element for which to generate an ID.
     * @param view View containing the lifecycle element.
     * @return An ID, unique within the current view, for the given element.
     * 
     * @see ViewIndex#observeAssignedId(String)
     * @see String#hashCode() for the algorithm this method is based on.
     */
    public static String generateId(LifecycleElement element, View view) {
        // Calculate a hash code based on the path to the top of the phase tree
        // without building a string.
        int prime = 6971;

        // Initialize hash to the class of the lifecycle element
        int hash = element.getClass().getName().hashCode();
        
        // Add the element's path to the hash code.
        hash += prime;
        if (element.getViewPath() != null) {
            hash += element.getViewPath().hashCode();
        }
        
        // Eliminate negatives without losing precision, and express in base-36
        String id = Long.toString(((long) hash) - ((long) Integer.MIN_VALUE), 36);
        while (!view.getViewIndex().observeAssignedId(id)) {
            // Iteratively take the product of the hash and another large prime
            // until a unique ID has been generated.
            hash *= 4507;
            id = Long.toString(((long) hash) - ((long) Integer.MIN_VALUE), 36);
        }
        
        return UifConstants.COMPONENT_ID_PREFIX + id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        LifecycleElement element = getElementState().getElement();

        if (StringUtils.isBlank(element.getId())) {
            element.setId(generateId(element, ViewLifecycle.getView()));
        }
    }

}
