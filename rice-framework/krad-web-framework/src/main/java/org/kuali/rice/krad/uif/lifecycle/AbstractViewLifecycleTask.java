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

import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base abstract implementation for a lifecycle task.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class AbstractViewLifecycleTask implements ViewLifecycleTask {

    private final Logger LOG = LoggerFactory.getLogger(AbstractViewLifecycleTask.class);

    private ViewLifecyclePhase phase;

    /**
     * Create a lifecycle processing task.
     * 
     * @param phase The phase this task is a part of.
     */
    protected AbstractViewLifecycleTask(ViewLifecyclePhase phase) {
        this.phase = phase;
    }

    /**
     * Perform phase-specific lifecycle processing tasks.
     */
    protected abstract void performLifecycleTask();

    /**
     * Reset this task, to facilitate recycling.
     */
    void recycle() {
        this.phase = null;
    }

    /**
     * Set the phase on a recycled task.
     * 
     * @param phase The phase to set.
     */
    void setPhase(ViewLifecyclePhase phase) {
        this.phase = phase;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTask#getPhase()
     */
    @Override
    public ViewLifecyclePhase getPhase() {
        return phase;
    }

    /**
     * Execute the lifecycle phase.
     * 
     * <p>
     * This method performs state validation and updates component view status. Override
     * {@link #performLifecyclePhase()} to provide phase-specific behavior.
     * </p>
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public final void run() {
        try {
            if (ViewLifecycle.getPhase() != phase) {
                throw new IllegalStateException("The phase this task is a part of is not active.");
            }

            if (ProcessLogger.isTraceActive()) {
                ProcessLogger.countBegin("lc-task-" + phase.getViewPhase());
            }

            try {
                performLifecycleTask();
            } finally {

                if (ProcessLogger.isTraceActive()) {
                    ProcessLogger.countEnd("lc-task-" + phase.getViewPhase(), getClass().getName() + " "
                            + phase.getClass().getName() + " " + phase.getComponent().getClass().getName() + " "
                            + phase.getComponent().getId());
                }
            }

            // Only recycle successfully processed tasks
            LifecycleTaskFactory.recycle(this);
            
        } catch (Throwable t) {
            LOG.warn("Error in lifecycle phase " + this, t);

            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else {
                throw new IllegalStateException("Unexpected error in lifecycle phase " + this, t);
            }
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getSimpleName()
                + " " + getPhase().getComponent().getClass().getSimpleName()
                + " " + getPhase().getComponent().getId();
    }

}
