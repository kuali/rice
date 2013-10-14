/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.lifecycle.model;

import java.util.Map;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecycleTask;
import org.kuali.rice.krad.uif.lifecycle.ApplyModelComponentPhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;

/**
* * Push attributes to the component context.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PopulateComponentContextTask extends AbstractViewLifecycleTask {

    /**
     * Constructor.
     * 
     * @param phase The apply model phase for the component.
     */
    public PopulateComponentContextTask(ViewLifecyclePhase phase) {
        super(phase);
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecycleTask#getPhase()
     */
    @Override
    public ApplyModelComponentPhase getPhase() {
        return (ApplyModelComponentPhase) super.getPhase();
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecycleTask#performLifecycleTask()
     */
    @Override
    protected void performLifecycleTask() {
        Component component = getPhase().getComponent();
        Component parent = getPhase().getParent();
        Map<String, Object> commonContext = getPhase().getCommonContext();
        
        if (parent != null) {
            component.pushObjectToContext(UifConstants.ContextVariableNames.PARENT, parent);
        }

        // set context on component for evaluating expressions
        component.pushAllToContext(commonContext);

        // set context evaluate expressions on the layout manager
        if (component instanceof Container) {
            LayoutManager layoutManager = ((Container) component).getLayoutManager();

            if (layoutManager != null) {
                layoutManager.pushAllToContext(commonContext);
                layoutManager.pushObjectToContext(UifConstants.ContextVariableNames.PARENT, component);
                layoutManager.pushObjectToContext(UifConstants.ContextVariableNames.MANAGER, layoutManager);
            }
        }
    }

}
