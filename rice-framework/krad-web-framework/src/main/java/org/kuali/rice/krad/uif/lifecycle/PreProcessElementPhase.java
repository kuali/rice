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

import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Lifecycle phase implementation representing the pre-process phase.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PreProcessElementPhase extends ViewLifecyclePhaseBase {

    /**
     * {@inheritDoc}
     */
    @Override
    public LifecycleEvent getEventToNotify() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.CREATED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.CACHED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.PRE_PROCESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewLifecyclePhase initializeSuccessor(LifecycleElement nestedElement, String nestedPath,
            Component nestedParent) {
        if (nestedElement != null && !UifConstants.ViewStatus.CACHED.equals(nestedElement.getViewStatus())) {
            ViewLifecyclePhase preProcessPhase = KRADServiceLocatorWeb.getViewLifecyclePhaseBuilder()
                    .buildPhase(UifConstants.ViewPhases.PRE_PROCESS, nestedElement, nestedParent, nestedPath);
            return preProcessPhase;
        }

        return null;
    }

}
