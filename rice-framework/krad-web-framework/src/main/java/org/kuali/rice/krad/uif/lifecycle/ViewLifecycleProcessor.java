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

import org.kuali.rice.krad.uif.freemarker.LifecycleRenderingContext;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;

/**
 * Interface for controlling the execution of the view lifecycle.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewLifecycleProcessor {

    /**
     * Performs a lifecycle phase according to this processor's semantics, blocking until the phase
     * has been completely processed. Once the initial phase has been completely processed, this
     * method will return.
     * 
     * @param initialPhase The initial lifecycle phase
     */
    void performPhase(ViewLifecyclePhase initialPhase);

    /**
     * Pushes lifecycle phases to be processed within the lifecycle associated with this processor.
     * 
     * <p>A phase submitted using this method will be added to the front of the queue, to be processed
     * by the next available processor.</p>
     * 
     * @param phase The phase to be processed within the lifecycle associated with this processor.
     */
    void pushPendingPhase(ViewLifecyclePhase phase);

    /**
     * Queues a lifecycle phase to be processed within the lifecycle associated with this processor.
     * 
     * <p>A phase submitted using this method will be added to the end of the queue, to be processed
     * after all other phases currently in the queue have been submitted.</p>
     * 
     * @param phase The phase to be processed within the lifecycle associated with this processor.
     */
    void offerPendingPhase(ViewLifecyclePhase phase);

    /**
     * Gets the phase actively being processing on the current thread.
     *
     * @return lifecycle phase active on the current thread
     */
    ViewLifecyclePhase getActivePhase();

    /**
     * Gets the lifecycle associated with this processor.
     *
     * @return lifecycle associated with this processor
     */
    ViewLifecycle getLifecycle();

    /**
     * Gets a thread-local rending context for invoking FreeMarker operations on the current thread.
     *
     * @return rending context for invoking FreeMarker operations on the current thread
     */
    LifecycleRenderingContext getRenderingContext();

    /**
     * Returns an instance of {@link org.kuali.rice.krad.uif.view.ExpressionEvaluator} that can be
     * used for evaluating expressions contained on the view.
     * 
     * <p>A ExpressionEvaluator must be initialized with a model for expression evaluation. One
     * instance is constructed for the view lifecycle and made available to all components/helpers
     * through this method</p>
     * 
     * @return instance of ExpressionEvaluator
     */
    ExpressionEvaluator getExpressionEvaluator();

}
