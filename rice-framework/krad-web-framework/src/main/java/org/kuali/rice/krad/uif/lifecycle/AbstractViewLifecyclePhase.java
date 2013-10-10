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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base abstract implementation for a lifecycle phase.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class AbstractViewLifecyclePhase implements ViewLifecyclePhase {

    private final Logger LOG = LoggerFactory.getLogger(AbstractViewLifecyclePhase.class);

    private final Component component;
    private final Object model;
    private final List<? extends ViewLifecyclePhase> predecessors;
    private final List<ViewLifecyclePhase> successors;
    private final List<? extends ViewLifecyclePhase> unmodifiableSuccessors;

    private boolean processed;

    /**
     * Create a lifecycle processing task for initializing a component.
     * 
     * @param component The component to initialize.
     * @param model The model to initialize the component from.
     */
    protected AbstractViewLifecyclePhase(Component component, Object model,
            List<? extends ViewLifecyclePhase> predecessors) {
        this.component = component;

        if (component.getViewStatus().equals(getEndViewStatus())) {
            ViewLifecycle.reportIllegalState(
                    "Component is already in the expected end status " + getEndViewStatus()
                            + " before this phase " + component.getClass() + " " + component.getId());
        }

        this.model = model;
        this.predecessors = Collections.unmodifiableList(new ArrayList<ViewLifecyclePhase>(predecessors));
        this.successors = new LinkedList<ViewLifecyclePhase>();
        this.unmodifiableSuccessors = Collections.unmodifiableList(successors);
    }

    /**
     * Perform phase-specific lifecycle processing tasks.
     */
    protected abstract void performLifecyclePhase();

    /**
     * Initialize list of successor phases.
     * 
     * <p>
     * This method will be called after {@link #performLifecyclePhase()} while processing this
     * phase.
     * </p>
     */
    protected abstract void initializeSuccessors(List<ViewLifecyclePhase> successors);

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getComponent()
     */
    @Override
    public final Component getComponent() {
        return component;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getModel()
     */
    @Override
    public final Object getModel() {
        return model;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#isProcessed()
     */
    @Override
    public final boolean isProcessed() {
        return processed;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#isComplete()
     */
    @Override
    public final boolean isComplete() {
        if (!processed) {
            return false;
        }

        for (ViewLifecyclePhase successor : successors) {
            if (!successor.isComplete()) {
                return false;
            }
        }

        return true;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getPredecessors()
     */
    @Override
    public final List<? extends ViewLifecyclePhase> getPredecessors() {
        return predecessors;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getSuccessors()
     */
    @Override
    public final List<? extends ViewLifecyclePhase> getSuccessors() {
        return unmodifiableSuccessors;
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
        if (processed) {
            throw new IllegalStateException("Lifecycle phase has already been processed");
        }

        for (ViewLifecyclePhase predecessor : getPredecessors()) {
            if (!predecessor.isProcessed()) {
                throw new IllegalStateException("Predecessor phase has not completely processed");
            }
        }

        if (!ViewLifecycle.isLifecycleActive()) {
            throw new IllegalStateException("No view lifecyle is not active on the current thread");
        }
        
        if (ProcessLogger.isTraceActive()) {
            ProcessLogger.ntrace("lc-" + getStartViewStatus() + "-" + getEndViewStatus() + ":", ":"
                    + getComponent().getClass().getSimpleName(), 1000);
            ProcessLogger.countBegin("lc-" + getStartViewStatus() + "-" + getEndViewStatus());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(getComponent().getClass() + " " + getComponent().getId() + " " +
                    getStartViewStatus() + " -> " + getEndViewStatus());
        }
        
        ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();
        try {
            viewLifecycle.setActivePhase(this);

            if (!component.getViewStatus().equals(getStartViewStatus())) {
                ViewLifecycle.reportIllegalState(
                        "Component is not in the expected status " + getStartViewStatus()
                                + " at the start of this phase, found " + component.getClass() + " "
                                + component.getId() + " " + component.getViewStatus());
            }

            performLifecyclePhase();

            component.setViewStatus(getEndViewStatus());
            processed = true;
            
            initializeSuccessors(successors);

        } finally {
            viewLifecycle.setActivePhase(null);

            if (ProcessLogger.isTraceActive()) {
                ProcessLogger.countEnd("lc-" + getStartViewStatus() + "-" + getEndViewStatus(), getComponent()
                        .getClass() + " " + getComponent().getId());
            }
        }
    }

}
