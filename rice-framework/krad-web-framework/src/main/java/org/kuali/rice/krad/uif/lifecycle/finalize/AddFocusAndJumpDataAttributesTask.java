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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.LifecycleElementState;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.web.form.UifFormBase;

/**
 * Add the focusId, jumpToId and jumpToName as dataAttributes to the component during the finalize phase.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AddFocusAndJumpDataAttributesTask extends ViewLifecycleTaskBase<Component> {

    /**
     * Constructor.
     *
     * @param phase The finalize phase for the component.
     */
    public AddFocusAndJumpDataAttributesTask() {
        super(Component.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        LifecycleElementState elementState = getElementState();
        String phase = elementState.getViewPhase();
        String viewPath = elementState.getViewPath();
        if (!ViewLifecycle.isRefreshComponent(phase, viewPath)) {
            return;
        }

        Component component = (Component) getElementState().getElement();
        Object model = ViewLifecycle.getModel();

        UifFormBase formBase = (UifFormBase) model;

        // If AutoFocus then set the focus_id to FIRST field, unless focus_id is also specified
        if (((UifFormBase) model).getView().getCurrentPage().isAutoFocus() && StringUtils.isNotBlank(formBase.getFocusId())) {
            component.addDataAttribute(UifConstants.ActionDataAttributes.FOCUS_ID, formBase.getFocusId());
        } else if (((UifFormBase) model).getView().getCurrentPage().isAutoFocus()) {
            component.addDataAttribute(UifConstants.ActionDataAttributes.FOCUS_ID, UifConstants.Order.FIRST.name());
        }

        // Add jumpToId as a data attribute
        if (StringUtils.isNotBlank(formBase.getJumpToId())) {
            component.addDataAttribute(UifConstants.ActionDataAttributes.JUMP_TO_ID, formBase.getJumpToId());
        }

        // Add jumpToName as a data attribute
        if (StringUtils.isNotBlank(formBase.getJumpToName())) {
            component.addDataAttribute(UifConstants.ActionDataAttributes.JUMP_TO_NAME, formBase.getJumpToName());
        }
    }

}