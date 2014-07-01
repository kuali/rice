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
package org.kuali.rice.krad.uif.lifecycle.initialize;

import org.kuali.rice.krad.uif.lifecycle.LifecycleElementState;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Assigns a lifecycle element's path property {@link LifecycleElement#setViewPath(String)} and
 * {@link LifecycleElement#setPath(String)}, based on the paths to the element from
 * {@link LifecycleElementState}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PopulatePathTask extends ViewLifecycleTaskBase<LifecycleElement> {

    /**
     * Creates an instance based on element state.
     */
    protected PopulatePathTask() {
        super(LifecycleElement.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        LifecycleElementState elementState = getElementState();
        LifecycleElement element = elementState.getElement();

        element.setViewPath(elementState.getViewPath());
    }

}
