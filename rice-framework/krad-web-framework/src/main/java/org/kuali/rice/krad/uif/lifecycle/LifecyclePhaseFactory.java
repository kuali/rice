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

import org.kuali.rice.krad.uif.util.RecycleUtils;

/**
 * Responsible for creating lifecycle phases.
 *
 * <p>This factory recycles completed phases to reduce object creation during the lifecycle.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class LifecyclePhaseFactory {

    /**
     * Private constructor - utility class only.
     */
    private LifecyclePhaseFactory() {}

    /**
     * Build a phase instance for processing the given view phase.
     *
     * @param viewPhase view phase
     * @return phase instance
     */
    @SuppressWarnings("unchecked")
    public static <T extends ViewLifecyclePhase> T buildPhase(String viewPhase) {
        return RecycleUtils.<T>getInstance("ViewLifecyclePhase-" + viewPhase, (Class<T>) ViewLifecyclePhase.class);
    }

    /**
     * Recycles a task instance after processing.
     *
     * @param phase The task to recycle.
     */
    static void recycle(ViewLifecyclePhase phase) {
        phase.recycle();
        RecycleUtils.recycle(phase);
    }

}
