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

import java.util.Set;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Lifecycle phase processing task for applying the model to a component.
 *
 * <p>
 * During the apply model phase each component of the tree if invoked to setup any state based on
 * the given model data
 * </p>
 *
 * <p>
 * Part of the view lifecycle that applies the model data to the view. Should be called after the
 * model has been populated before the view is rendered. The main things that occur during this
 * phase are:
 * <ul>
 * <li>Generation of dynamic fields (such as collection rows)</li>
 * <li>Execution of conditional logic (hidden, read-only, required settings based on model values)</li>
 * </ul>
 * </p>
 *
 * <p>
 * The update phase can be called multiple times for the view's lifecycle (typically only once per
 * request)
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ApplyModelComponentPhase extends ViewLifecyclePhaseBase {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.APPLY_MODEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.INITIALIZED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.MODEL_APPLIED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LifecycleEvent getEventToNotify() {
        return null;
    }

    /**
     * Visit a lifecycle element.
     *
     * @param element The lifecycle element (component or layout manager) to mark as visisted.
     * @return True if the element has been visited before, false if this was the first visit.
     */
    public boolean visit(LifecycleElement element) {
        Set<String> visitedIds = ViewLifecycle.getVisitedIds();
        if (visitedIds.contains(element.getId())) {
            return true;
        }

        synchronized (visitedIds) {
            return !visitedIds.add(element.getId());
        }
    }

}
