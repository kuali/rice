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

import org.kuali.rice.krad.datadictionary.Copyable;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.lifecycle.LifecycleElementState;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Invokes {@link Copyable#preventModification()} on the lifecycle element.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PrepareForCacheTask extends ViewLifecycleTaskBase<LifecycleElement> {

    /**
     * Creates an instance based on element state.
     */
    protected PrepareForCacheTask() {
        super(LifecycleElement.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        LifecycleElementState elementState = getElementState();
        LifecycleElement element = elementState.getElement();
        String viewStatus = element.getViewStatus();
        
        element.setViewStatus(UifConstants.ViewStatus.CACHED);
        if (!UifConstants.ViewStatus.CREATED.equals(viewStatus) && !UifConstants.ViewStatus.CACHED.equals(viewStatus)) {
            ViewLifecycle.reportIllegalState("View status is "
                    + viewStatus
                    + " prior to caching "
                    + getClass().getName()
                    + " "
                    + element.getId()
                    + ", expected C or X");
        }

        viewStatus = UifConstants.ViewStatus.CACHED;
    }

}
