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

import java.util.Deque;
import java.util.LinkedList;

import org.kuali.rice.krad.uif.freemarker.LifecycleRenderingContext;

/**
 * Single-threaded view lifecycle processor implementation.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SynchronousViewLifecycleProcessor extends ViewLifecycleProcessorBase {

    /**
     * Pending lifecycle phases.
     */
    private final Deque<ViewLifecyclePhase> pendingPhases = new LinkedList<ViewLifecyclePhase>();

    /**
     * The phase currently active on this lifecycle.
     */
    private ViewLifecyclePhase activePhase;

    /**
     * The rendering context.
     */
    private LifecycleRenderingContext renderingContext;

    /**
     * Create a new synchronous processor for a lifecycle.
     */
    public SynchronousViewLifecycleProcessor(ViewLifecycle lifecycle) {
        super(lifecycle);
    }

    /**
     * @return the renderingContext
     */
    public LifecycleRenderingContext getRenderingContext() {
        if (renderingContext == null && ViewLifecycle.isRenderInLifecycle()) {
            ViewLifecycle lifecycle = getLifecycle();
            this.renderingContext = new LifecycleRenderingContext(
                    lifecycle.model, lifecycle.request, lifecycle.response);
        }

        return this.renderingContext;
    }

    /**
     * @see ViewLifecycleProcessor#getActivePhase()
     */
    @Override
    public ViewLifecyclePhase getActivePhase() {
        return activePhase;
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
    void setActivePhase(ViewLifecyclePhase phase) {
        if (activePhase != null && phase != null) {
            throw new IllegalStateException("Another phase is already active on this lifecycle thread " + activePhase);
        }

        activePhase = phase;
    }

    /**
     * Offer a pending phase, to be executed after the completion of the active phase and all
     * currently pending phases.
     * 
     * @param pendingPhase The pending phase.
     */
    public void offerPendingPhase(ViewLifecyclePhase pendingPhase) {
        pendingPhases.offer(pendingPhase);
    }

    /**
     * @see ViewLifecycleProcessor#pushPendingPhase(ViewLifecyclePhase)
     */
    @Override
    public void pushPendingPhase(ViewLifecyclePhase phase) {
        pendingPhases.push(phase);
    }

    /**
     * Execute all pending lifecycle phases.
     */
    @Override
    public void performPhase(ViewLifecyclePhase initialPhase) {
        offerPendingPhase(initialPhase);
        while (!pendingPhases.isEmpty()) {
            pendingPhases.poll().run();
        }
    }

}
