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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.kuali.rice.krad.uif.component.Component;

/**
 * Base abstract implementation for a lifecycle phase.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class AbstractViewLifecyclePhase implements ViewLifecyclePhase {

    private final Component component;
    private final Object model;
    private final List<ViewLifecyclePhase> predecessors;
    private final List<ViewLifecyclePhase> successors;
    private final List<ViewLifecyclePhase> unmodifiableSuccessors;

    private boolean processed;

    /**
     * Create a lifecycle processing task for initializing a component.
     * 
     * @param component The component to initialize.
     * @param model The model to initialize the component from.
     */
    protected AbstractViewLifecyclePhase(Component component, Object model, List<ViewLifecyclePhase> predecessors) {
        this.component = component;
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
     * This method will be called after {@link #performLifecyclePhase()} while processing this phase.
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
    public final List<ViewLifecyclePhase> getPredecessors() {
        return predecessors;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getSuccessors()
     */
    @Override
    public final List<ViewLifecyclePhase> getSuccessors() {
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
            if (!predecessor.isComplete()) {
                throw new IllegalStateException("Predecessor phase has not completely processed");
            }
        }

        if (!component.getViewStatus().equals(getStartViewStatus())) {
            throw new IllegalStateException("Component is not in the expected status " + getStartViewStatus()
                    + " at the start of this phase, found " + component.getViewStatus());
        }

        performLifecyclePhase();
        initializeSuccessors(successors);

        component.setViewStatus(getEndViewStatus());
        processed = true;
    }

}
