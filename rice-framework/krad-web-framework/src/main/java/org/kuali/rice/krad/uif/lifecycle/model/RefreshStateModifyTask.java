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
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.lifecycle.LifecycleElementState;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RefreshStateModifyTask extends ViewLifecycleTaskBase<Component> {

    /**
     * Default constructor.
     */
    public RefreshStateModifyTask() {
        super(Component.class);
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase#performLifecycleTask()
     */
    @Override
    protected void performLifecycleTask() {
        LifecycleElementState phase = getElementState();
        if (!ViewLifecycle.isRefreshComponent(phase.getViewPhase(), phase.getViewPath())) {
            return;
        }

        Component component = (Component) getElementState().getElement();

        // force the component to render on a refresh
        component.setRender(true);

        // reset data if needed
        if (component.isResetDataOnRefresh()) {
            // TODO: this should handle groups as well, going through nested data fields
            if (component instanceof DataField) {
                // TODO: should check default value

                // clear value
                ObjectPropertyUtils.initializeProperty(ViewLifecycle.getModel(),
                        ((DataField) component).getBindingInfo().getBindingPath());
            }
        }
    }

}
