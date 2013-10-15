/**
 * Copyright 2005-2013 The Kuali Foundation
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

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecycleTask;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;

/**
 * Assign a unique ID to the component, if one has not already been assigned.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AssignIdsTask extends AbstractViewLifecycleTask {

    /**
     * Create a task to assign component IDs during the initialize phase.
     * 
     * @param phase The initialize phase for the component.
     */
    public AssignIdsTask(ViewLifecyclePhase phase) {
        super(phase);
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecycleTask#performLifecycleTask()
     */
    @Override
    protected void performLifecycleTask() {
        Component component = getPhase().getComponent();

        if (StringUtils.isBlank(component.getId())) {
            component.setId(UifConstants.COMPONENT_ID_PREFIX + UUID.randomUUID().toString());
        } // TODO: else validate that component ID is unique

        if (component instanceof Container) {
            LayoutManager layoutManager = ((Container) component).getLayoutManager();

            if ((layoutManager != null) && StringUtils.isBlank(layoutManager.getId())) {
                layoutManager.setId(UifConstants.COMPONENT_ID_PREFIX + UUID.randomUUID().toString());
            } // TODO: else validate that layout manager ID is unique
        }
    }

}
