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
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;

/**
 * Perform custom apply model behavior for the component defined by the helper.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class HelperCustomApplyModelTask extends ViewLifecycleTaskBase<Component> {

    /**
     * Default constructor.
     */
    public HelperCustomApplyModelTask() {
        super(Component.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        // invoke service override hook
        ViewLifecycle.getHelper().performCustomApplyModel(getElementState().getElement(), ViewLifecycle.getModel());
    }

}
