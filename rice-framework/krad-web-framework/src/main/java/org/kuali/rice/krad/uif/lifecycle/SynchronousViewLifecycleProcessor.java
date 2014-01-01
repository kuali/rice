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
     * Creates a new synchronous processor for a lifecycle.
     * 
     * @param lifecycle The lifecycle to process.
     */
    public SynchronousViewLifecycleProcessor(ViewLifecycle lifecycle) {
        super(lifecycle);
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public ViewLifecyclePhase getActivePhase() {
        return activePhase;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setActivePhase(ViewLifecyclePhase phase) {
        if (activePhase != null && phase != null) {
            throw new IllegalStateException("Another phase is already active on this lifecycle thread " + activePhase);
        }

        activePhase = phase;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void offerPendingPhase(ViewLifecyclePhase pendingPhase) {
        pendingPhases.offer(pendingPhase);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushPendingPhase(ViewLifecyclePhase phase) {
        pendingPhases.push(phase);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performPhase(ViewLifecyclePhase initialPhase) {
        offerPendingPhase(initialPhase);
        while (!pendingPhases.isEmpty()) {
            pendingPhases.poll().run();
        }
    }

}
