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
package org.kuali.rice.krad.uif.widget;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.datadictionary.HelpDefinition;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.field.ActionField;
import org.kuali.rice.krad.uif.view.View;

import java.util.List;

/**
 * Widget that renders help on a component
 *
 * - if help URL is specified display help icon
 * - if help summary is specified display help tooltip
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Help extends WidgetBase {
	private static final long serialVersionUID = -1514436681476297241L;

    private ActionField helpActionField;
    private HelpDefinition helpDefinition;

    private String externalHelpUrl;

    private static ParameterService parameterService;

    public Help() {
        super();
	}

    /**
     * @see org.kuali.rice.krad.uif.widget.WidgetBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);
        buildExternalHelpUrl();
        getHelpActionField().setClientSideJs("openHelpWindow('" + externalHelpUrl + "')");
    }

    private void buildExternalHelpUrl() {
        if (StringUtils.isBlank(externalHelpUrl)) {
            externalHelpUrl = getParameterService().getParameterValueAsString(helpDefinition.getParameterNamespace(),
                    helpDefinition.getParameterDetailType(), helpDefinition.getParameterName());
            //TODO: add some code to handle when we did not get an external help url
        }
    }
    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(helpActionField);

        return components;
    }

    public ActionField getHelpActionField() {
        return helpActionField;
    }

    public void setHelpActionField(ActionField helpActionField) {
        this.helpActionField = helpActionField;
    }

    public HelpDefinition getHelpDefinition() {
        return helpDefinition;
    }

    public void setHelpDefinition(HelpDefinition helpDefinition) {
        this.helpDefinition = helpDefinition;
    }

    public String getExternalHelpUrl() {
        return this.externalHelpUrl;
    }

    public void setExternalHelpUrl(String externalHelpUrl) {
        this.externalHelpUrl = externalHelpUrl;
    }

    private ParameterService getParameterService() {
        if ( parameterService == null ) {
            parameterService = CoreFrameworkServiceLocator.getParameterService();
        }
        return parameterService;
    }

}
