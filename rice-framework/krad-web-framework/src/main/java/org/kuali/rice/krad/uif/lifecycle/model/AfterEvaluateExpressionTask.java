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

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.LifecycleElementState;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;

/**
 * Perform post-expression evaluation tasks.
 * 
 * <p>
 * This task is used for optimization in core KRAD features. Since expression evaluation can be
 * heavy, common behavior defined as expressions in delivered UIF components can be moved to this
 * task in order to handle evaluation in code instead.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AfterEvaluateExpressionTask extends ViewLifecycleTaskBase<Component> {

    /**
     * Create a task to assign component IDs during the apply model phase.
     * 
     * @param phase The apply model phase for the component.
     */
    public AfterEvaluateExpressionTask() {
        super(Component.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        LifecycleElementState elementState = getElementState();
        if (!(elementState instanceof ViewLifecyclePhase)) {
            return;
        }
        
        ViewLifecyclePhase phase = (ViewLifecyclePhase) elementState;
        Component component = (Component) phase.getElement();
        component.afterEvaluateExpression();
    }

}
