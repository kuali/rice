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
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.kuali.rice.krad.uif.view.View;

/**
 * Performs the pre-process phase on a view, for use prior to caching.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewLifecyclePreProcessBuild implements Runnable {

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        View view = ViewLifecycle.getView();

        ViewLifecyclePhase phase = KRADServiceLocatorWeb.getViewLifecyclePhaseBuilder().buildPhase(view,
                UifConstants.ViewPhases.PRE_PROCESS, null);

        ProcessLogger.trace("pre-view-lifecycle:" + view.getId());

        ViewLifecycle.getProcessor().performPhase(phase);
    }

}
