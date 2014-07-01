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
package org.kuali.rice.krad.uif.lifecycle.finalize;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.FinalizeComponentPhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;

/**
 * Perform default finalize behavior defined for the component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentDefaultFinalizeTask extends ViewLifecycleTaskBase<Component> {

    /**
     * Default constructor.
     */
    public ComponentDefaultFinalizeTask() {
        super(Component.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FinalizeComponentPhase getElementState() {
        return (FinalizeComponentPhase) super.getElementState();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void performLifecycleTask() {
        FinalizeComponentPhase phase = getElementState();
        phase.getElement().performFinalize(ViewLifecycle.getModel(), phase.getParent());
    }

}
