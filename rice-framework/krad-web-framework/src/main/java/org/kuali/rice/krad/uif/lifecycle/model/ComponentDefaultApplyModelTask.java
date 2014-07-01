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
import org.kuali.rice.krad.uif.lifecycle.ApplyModelComponentPhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;

/**
 * Perform default apply model behavior defined for the component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentDefaultApplyModelTask extends ViewLifecycleTaskBase<Component> {

    /**
     * Create a task to assign component IDs during the apply model phase.
     */
    public ComponentDefaultApplyModelTask() {
        super(Component.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplyModelComponentPhase getElementState() {
        return (ApplyModelComponentPhase) super.getElementState();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void performLifecycleTask() {
        ApplyModelComponentPhase phase = getElementState();
        phase.getElement().performApplyModel(ViewLifecycle.getModel(), phase.getParent());
    }

}
