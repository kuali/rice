/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.view.View;

/**
 * Action field that performs an Ajax request and will result in updating of the page or a component
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AjaxActionField extends ActionField {
    private static final long serialVersionUID = -2831173647391138870L;

    private String refreshId;
    private String refreshPropertyName;

    public AjaxActionField() {
        super();
    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>Add methodToCall action parameter if set and setup event code for
     * setting action parameters</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        Component refreshComponent = null;

        // if refresh property name is given, adjust the binding and then attempt to find the
        // component in the view index
        if (StringUtils.isNotBlank(refreshPropertyName)) {
            // TODO: does this support all binding prefixes?
            if (refreshPropertyName.startsWith(UifConstants.NO_BIND_ADJUST_PREFIX)) {
                refreshPropertyName = StringUtils.removeStart(refreshPropertyName, UifConstants.NO_BIND_ADJUST_PREFIX);
            } else if (StringUtils.isNotBlank(view.getDefaultBindingObjectPath())) {
                refreshPropertyName = view.getDefaultBindingObjectPath() + "." + refreshPropertyName;
            }

            DataField dataField = view.getViewIndex().getDataFieldByPath(refreshPropertyName);
            if (dataField != null) {
               refreshComponent = dataField;
            }
        }
        else if (StringUtils.isNotBlank(refreshId)) {
            Component component = view.getViewIndex().getComponentById(refreshId);
            if (component != null) {
                refreshComponent = component;
            }
        }

        String actionScript = "";
        if (refreshComponent != null) {
            refreshComponent.setRefreshedByAction(true);
            // update initial state
            Component initialComponent = view.getViewIndex().getInitialComponentStates().get(
                    refreshComponent.getFactoryId());
            if (initialComponent != null) {
                initialComponent.setRefreshedByAction(true);
                view.getViewIndex().getInitialComponentStates().put(refreshComponent.getFactoryId(), initialComponent);
            }

            // refresh component for action
            actionScript = "retrieveComponent('" + refreshComponent.getId() + "','" + refreshComponent.getFactoryId()
                    + "','" + getMethodToCall() + "');";
        }
        else {
            // refresh page
            actionScript = "submitForm();";
        }

        // add action script to client JS
        if (StringUtils.isNotBlank(getClientSideJs())) {
            actionScript = getClientSideJs() + actionScript;
        }
        setClientSideJs(actionScript);

        super.performFinalize(view, model, parent);
    }

    /**
     * Id for the component that should be refreshed after the action completes
     *
     * <p>
     * Either refresh id or refresh property name can be set to configure the component that should
     * be refreshed after the action completes. If both are blank, the page will be refreshed
     * </p>
     *
     * @return String valid component id
     */
    public String getRefreshId() {
        return refreshId;
    }

    /**
     * Setter for the component refresh id
     *
     * @param refreshId
     */
    public void setRefreshId(String refreshId) {
        this.refreshId = refreshId;
    }

    /**
     * Property name for the {@link DataField} that should be refreshed after the action completes
     *
     * <p>
     * Either refresh id or refresh property name can be set to configure the component that should
     * be refreshed after the action completes. If both are blank, the page will be refreshed
     * </p>
     *
     * <p>
     * Property name will be adjusted to use the default binding path unless it contains the form prefix
     *
     * @return String valid property name with an associated DataField
     * @see org.kuali.rice.krad.uif.UifConstants#NO_BIND_ADJUST_PREFIX
     *      </p>
     */
    public String getRefreshPropertyName() {
        return refreshPropertyName;
    }

    /**
     * Setter for the property name of the DataField that should be refreshed
     *
     * @param refreshPropertyName
     */
    public void setRefreshPropertyName(String refreshPropertyName) {
        this.refreshPropertyName = refreshPropertyName;
    }
}
