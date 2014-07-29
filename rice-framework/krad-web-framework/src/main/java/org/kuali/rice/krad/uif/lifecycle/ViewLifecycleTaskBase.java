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

import org.kuali.rice.krad.uif.component.ReferenceCopy;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base abstract implementation for a lifecycle task.
 *
 * @param <T> Top level element type for this task
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ViewLifecycleTaskBase<T> implements ViewLifecycleTask<T> {
    private final Logger LOG = LoggerFactory.getLogger(ViewLifecycleTaskBase.class);

    private final Class<T> elementType;

    @ReferenceCopy
    private LifecycleElementState elementState;

    /**
     * Creates a lifecycle processing task for a specific phase.
     *
     * @param elementType Top level element type
     */
    protected ViewLifecycleTaskBase(Class<T> elementType) {
        this.elementType = elementType;
    }

    /**
     * Executes the lifecycle task.
     *
     * <p>
     * This method performs state validation and updates component view status. Override
     * {@link #performLifecycleTask()} to provide task-specific behavior.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public final void run() {
        try {
            if (!getElementType().isInstance(elementState.getElement())) {
                return;
            }

            if (ProcessLogger.isTraceActive()) {
                ProcessLogger.countBegin("lc-task-" + elementState.getViewPhase());
            }

            try {
                performLifecycleTask();
            } finally {

                if (ProcessLogger.isTraceActive()) {
                    ProcessLogger.countEnd("lc-task-" + elementState.getViewPhase(),
                            getClass().getName() + " " + elementState.getClass().getName() + " " + elementState
                                    .getElement().getClass().getName() + " " + elementState.getElement().getId());
                }
            }

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
     * Performs phase-specific lifecycle processing tasks.
     */
    protected abstract void performLifecycleTask();

    /**
     * {@inheritDoc}
     */
    @Override
    public LifecycleElementState getElementState() {
        return elementState;
    }

    /**
     * Sets the phase on a recycled task.
     *
     * @param elementState The phase to set.
     * @see #getElementState()
     */
    public void setElementState(LifecycleElementState elementState) {
        this.elementState = elementState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getElementType() {
        return this.elementType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewLifecycleTaskBase<T> clone() throws CloneNotSupportedException {
        return (ViewLifecycleTaskBase<T>) super.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName()
                + " "
                + getElementState().getElement().getClass().getSimpleName()
                + " "
                + getElementState().getElement().getId();
    }

}
