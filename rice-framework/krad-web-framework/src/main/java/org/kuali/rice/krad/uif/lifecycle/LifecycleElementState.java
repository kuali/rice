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

import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.View;

/**
 * Handles delivery of per-node parse state for path-based view lifecycle algorithms.
 * 
 * <p>
 * The interface is used for assigning the path property value during the {@link View}'s pre-process
 * phase.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LifecycleElementState {
    
    /**
     * Gets the view lifecycle phase constant that corresponds to this phase processing task.
     * 
     * @return view lifecycle phase constant corresponding to this phase
     * @see org.kuali.rice.krad.uif.UifConstants.ViewPhases
     */
    String getViewPhase();

    /**
     * Gets the path relative to the view.
     * 
     * @return path relative the the view
     */
    String getViewPath();
    
    /**
     * Gets the element this lifecycle phase is responsible for processing.
     * 
     * @return element this lifecycle phase is responsible for processing
     */
    LifecycleElement getElement();

    /**
     * Gets the path relative the predecessor phase's component.
     * 
     * @return path relative the predecessor phase's component
     */
    String getParentPath();

    /**
     * Gets the depth of the element in the lifecycle tree.
     * 
     * @return The depth of the element in the lifecycle tree.
     */
    int getDepth();

}
