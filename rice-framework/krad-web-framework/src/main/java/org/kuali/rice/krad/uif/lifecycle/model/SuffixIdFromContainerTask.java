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
package org.kuali.rice.krad.uif.lifecycle.model;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.util.ComponentUtils;

/**
 * Adjusts the id for elements that are within a component configured with a suffix to apply for
 * all children (such as components that are within a collection line).
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SuffixIdFromContainerTask extends ViewLifecycleTaskBase<Component> {

    /**
     * Constructor.
     *
     * @param phase The apply model phase for the component.
     */
    public SuffixIdFromContainerTask() {
        super(Component.class);
    }

    /**
     *  Pulls the container id suffix from the parent component and updates the id on the given element,
     *  then sets the container id suffix on the component so the suffixing will apply to all its children.
     * 
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        Component component = (Component) getElementState().getElement();
        ViewLifecyclePhase phase = (ViewLifecyclePhase) getElementState();
        
        Component parent = phase.getParent();
        if (parent == null) {
            return;
        }


        String containerIdSuffix = phase.getParent().getContainerIdSuffix();
        if (StringUtils.isBlank(parent.getContainerIdSuffix())) {
            return;
        }
                
        ComponentUtils.updateIdWithSuffix(component, containerIdSuffix);

        // container suffixes should concatenate if multiple are found within a node
        if (StringUtils.isNotBlank(component.getContainerIdSuffix())) {
            containerIdSuffix = component.getContainerIdSuffix() + containerIdSuffix;
        }

        component.setContainerIdSuffix(containerIdSuffix);
    }

}
