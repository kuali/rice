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

import static org.kuali.rice.krad.uif.UifConstants.ViewPhases.APPLY_MODEL;
import static org.kuali.rice.krad.uif.UifConstants.ViewPhases.FINALIZE;
import static org.kuali.rice.krad.uif.UifConstants.ViewPhases.INITIALIZE;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.View;

import java.util.List;

/**
 * Default phase builder implementation.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewLifecyclePhaseBuilderBase implements ViewLifecyclePhaseBuilder {

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewLifecyclePhase buildPhase(View view, String viewPhase, List<String> refreshPaths) {
        return buildPhase(viewPhase, view, null, "", refreshPaths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewLifecyclePhase buildPhase(String viewPhase, LifecycleElement element, Component parent,
            String parentPath, List<String> refreshPaths) {
        ViewLifecyclePhase phase = LifecyclePhaseFactory.buildPhase(viewPhase);
        phase.prepare(element, parent, parentPath, refreshPaths);

        return finishBuildPhase(phase);
    }

    /**
     * Determines if the previous phases have been run for the given element and if not backs up
     * until it finds the first phase we need to run.
     *
     * @param phase phase being requested
     * @return phase that should be run
     */
    protected ViewLifecyclePhase finishBuildPhase(ViewLifecyclePhase phase) {
        String previousViewPhase = getPreviousViewPhase(phase);

        while (previousViewPhase != null) {
            ViewLifecyclePhase prevPhase = LifecyclePhaseFactory.buildPhase(previousViewPhase);
            prevPhase.prepare(phase.getElement(), phase.getParent(), phase.getParentPath(), phase.getRefreshPaths());

            prevPhase.setNextPhase(phase);
            phase = prevPhase;

            previousViewPhase = getPreviousViewPhase(phase);
        }

        return phase;
    }

    /**
     * Return the previous view phase, for automatic phase spawning.
     *
     * @return phase view phase
     */
    protected static String getPreviousViewPhase(ViewLifecyclePhase phase) {
        String viewPhase = phase.getViewPhase();
        if (FINALIZE.equals(viewPhase) && !phase.getElement().isModelApplied()) {
            return APPLY_MODEL;
        }

        if (APPLY_MODEL.equals(viewPhase) && !phase.getElement().isInitialized()) {
            return INITIALIZE;
        }

        return null;
    }

}
