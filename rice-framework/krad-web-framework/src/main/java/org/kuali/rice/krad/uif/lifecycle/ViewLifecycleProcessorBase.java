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

/**
 * Abstract base lifecycle processor implementation.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ViewLifecycleProcessorBase implements ViewLifecycleProcessor {

    /**
     * The view lifecycle to be processed.
     */
    private final ViewLifecycle lifecycle;

    /**
     * Create a new synchronous processor for a lifecycle.
     */
    ViewLifecycleProcessorBase(ViewLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    /**
     * @see ViewLifecycleProcessor#getLifecycle()
     */
    @Override
    public ViewLifecycle getLifecycle() {
        return lifecycle;
    }

    /**
     * Report a phase as active on this lifecycle thread.
     * 
     * <p>
     * Since each {@link ViewLifecycle} instance is specific to a thread, only one phase may be
     * active at a time.
     * </p>
     * 
     * @param phase The phase to report as activate. Set as null to when the phase has been
     *        completed to indicate that no phase is active.
     */
    abstract void setActivePhase(ViewLifecyclePhase phase);

}
