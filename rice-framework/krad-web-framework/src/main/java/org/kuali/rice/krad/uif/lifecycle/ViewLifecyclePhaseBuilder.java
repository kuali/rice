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

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.View;

import java.util.List;

/**
 * Encapsulates the concept of creating {@link ViewLifecyclePhase} instances during a the view
 * lifecycle build process.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see ViewLifecycleBuild
 */
public interface ViewLifecyclePhaseBuilder {

    /**
     * Creates a lifecycle phase instance for the given view.
     *
     * @param view view instance the phase should be built for
     * @param viewPhase View phase to build an instance for
     * @return pre-process phase instance
     * @see ViewLifecycle#getView()
     */
    ViewLifecyclePhase buildPhase(View view, String viewPhase, List<String> refreshPaths);

    /**
     * Creates a lifecycle phase instance for a specific component in the current lifecycle.
     *
     * @param viewPhase view phase to build an instance for
     * @param element lifecycle element to build an instance for
     * @param parent parent component
     * @param parentPath path relative to the parent component referring to the lifecycle element
     * @return phase instance
     */
    ViewLifecyclePhase buildPhase(String viewPhase, LifecycleElement element, Component parent, String parentPath,
            List<String> refreshPaths);

}
