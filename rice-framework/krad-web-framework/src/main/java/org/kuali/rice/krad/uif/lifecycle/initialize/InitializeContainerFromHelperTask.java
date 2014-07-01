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

import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.view.ViewModel;

/**
 * Invoke custom initialization on the container from the view helper.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InitializeContainerFromHelperTask extends ViewLifecycleTaskBase<Container> {

    /**
     * Default constructor.
     */
    public InitializeContainerFromHelperTask() {
        super(Container.class);
    }

    /**
     * Invoke custom initialization based on the view helper.
     * 
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        // invoke hook point for adding components through code
        ViewLifecycle.getHelper().addCustomContainerComponents((ViewModel)ViewLifecycle.getModel(),
                (Container) getElementState().getElement());
    }

}
