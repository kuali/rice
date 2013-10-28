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

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;

/**
 * Represents a phase in the view lifecycle. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewLifecyclePhase extends Runnable {
    
    /**
     * Get the component this lifecycle phase is responsible for processing.
     * 
     * @return The component this lifecycle phase is responsible for processing.
     */
    Component getComponent();
    
    /**
     * Get the model to use in processing this phase.
     * 
     * @return The model to use in processing this phase.
     */
    Object getModel();
    
    /**
     * Get the parent component.
     * 
     * @return The parent component.
     */
    Component getParent();

    /**
     * Get the index within a parent phase's original list of successors of this phase.
     * 
     * @return The index within a parent phase's list of successors of this phase.
     */
    int getIndex();
    
    /**
     * Determine if this lifecycle phase has completed processing.
     * 
     * @return True if this phase has been processed, false if not.
     */
    boolean isProcessed();
    
    /**
     * Determine if this lifecycle phase, and all successor phases, have completed processing.
     * 
     * @return True if this phase and all successor phases have been processed, false if not.
     */
    boolean isComplete();
    
    /**
     * Get the view lifecycle phase constant that corresponds to this phase processing task.
     * 
     * @return The view lifecycle phase constant that corresponds to this phase processing task.
     * @see UifConstants.ViewPhases
     */
    String getViewPhase();

    /**
     * Get the event to notify on completion.
     * 
     * @return The lifecycle event to notify on completion.
     * @see ViewLifecycle.LifecycleEvent
     */
    LifecycleEvent getEventToNotify();

    /**
     * Get the expected view status prior to phase execution.
     * 
     * @return The expected view status prior to phase execution.
     */
    String getStartViewStatus();

    /**
     * Get the expected view status after phase execution.
     * 
     * @return The expected view status after phase execution.
     */
    String getEndViewStatus();
    
    /**
     * Get the lifecycle phase that directly precedes this phase..
     * 
     * @return The lifecycle phase that directly precedes this phase..
     */
    ViewLifecyclePhase getPredecessor();

}
