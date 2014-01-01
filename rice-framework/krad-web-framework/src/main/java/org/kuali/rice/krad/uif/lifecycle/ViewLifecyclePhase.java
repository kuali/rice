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

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;

/**
 * Represents a phase in the view lifecycle. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewLifecyclePhase extends Runnable {
    
    /**
     * Gets the component this lifecycle phase is responsible for processing.
     * 
     * @return component this lifecycle phase is responsible for processing
     */
    Component getComponent();
    
    /**
     * Gets the model to use in processing this phase.
     * 
     * @return model to use in processing this phase
     */
    Object getModel();
    
    /**
     * Gets the parent component.
     * 
     * @return parent component
     */
    Component getParent();

    /**
     * Gets the index within a parent phase's original list of successors of this phase.
     * 
     * @return index of this phase within a successor list
     */
    int getIndex();
    
    /**
     * Determines if this lifecycle phase has completed processing.
     * 
     * <p>
     * This method will return true when this phase's tasks have been processed, but does not
     * necessarily indicate that successor phases have been completed. Use {@link #isComplete()} to
     * determine if the lifecycle has been fully completed for this phase.
     * </p>
     * 
     * @return true if this phase has been processed, false if not
     */
    boolean isProcessed();
    
    /**
     * Determines if this lifecycle phase and all successor phases, have completed processing.
     * 
     * @return true if this phase and all successor phases have been processed, false if not
     * @see Component#notifyCompleted(ViewLifecyclePhase)
     */
    boolean isComplete();
    
    /**
     * Gets the view lifecycle phase constant that corresponds to this phase processing task.
     * 
     * @return view lifecycle phase constant corresponding to this phase
     * @see org.kuali.rice.krad.uif.UifConstants.ViewPhases
     */
    String getViewPhase();

    /**
     * Gets the event to notify on completion.
     * 
     * @return lifecycle event to notify on completion
     * @see ViewLifecycle.LifecycleEvent
     */
    LifecycleEvent getEventToNotify();

    /**
     * Gets the expected view status prior to phase execution.
     * 
     * @return expected view status prior to phase execution
     */
    String getStartViewStatus();

    /**
     * Gets the expected view status after phase execution.
     * 
     * @return expected view status after phase execution
     */
    String getEndViewStatus();
    
    /**
     * Gets the lifecycle phase that directly precedes this phase.
     * 
     * @return lifecycle phase that directly precedes this phase
     */
    ViewLifecyclePhase getPredecessor();

}
