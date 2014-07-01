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

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Represents a phase in the view lifecycle. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewLifecyclePhase extends LifecycleElementState, Runnable {
    
    /**
     * Gets the parent component.
     * 
     * @return parent component
     */
    Component getParent();

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
     * Gets the task currently running.
     * 
     * @return the task currently running, null if this phase is not active.
     */
    ViewLifecycleTask<?> getCurrentTask();
    
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

    /**
     * Prepares a recycled phase instance for processing the view.
     */
    void prepareView();
    
    /**
     * Prepares a recycled phase instance for processing a component.
     * 
     * @param element lifecycle element to prepare. In general, it should be true that the
     *        parentPath expression relative to parent will resolve to this same element. However
     *        this is not a strict requirement; the element passed here will be processed by the
     *        phase regardless of which element is present in the actual tree.
     * @param parentPath path to the element relative to its parent component
     * @param parent parent component
     */
    void prepareElement(LifecycleElement element, Component parent, String parentPath);
    
    /**
     * Sets the next phase, to queue for processing after this phase is completed.
     * 
     * @param nextPhase next phase
     * @throws IllegalArgumentException If nextPhase is null, or if the view status of the phases don't match.
     * @throws IllegalStateException If the nextPhase has been set to a non-null value already.
     */
    void setNextPhase(ViewLifecyclePhase nextPhase);

    /**
     * Sets the predecessor, for notification during processing.
     * 
     * @param phase predecessor phase
     */
    void setPredecessor(ViewLifecyclePhase phase);

    /**
     * Sets the refresh paths for this phase.
     * 
     * @param refreshPaths list of refresh paths.
     */
    void setRefreshPaths(List<String> refreshPaths);
    
    /**
     * Determines of there are any pending successors of this phase.
     * 
     * @return True if there are pending successors, false if no successors are pending.
     */
    boolean hasPendingSuccessors();

    /**
     * Remove a pending successor by path.
     * 
     * @param parentPath path
     */
    void removePendingSuccessor(String parentPath);

    /**
     * Invoked by the processor when this phase and all successors have completely processed.
     */
    void notifyCompleted();

    /**
     * Prepares this phase instance for recycled use.
     */
    void recycle();
    
}
