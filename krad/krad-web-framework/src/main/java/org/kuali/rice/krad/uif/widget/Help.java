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
import org.kuali.rice.krad.uif.field.DataField;
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

    private String tooltipHelpHtml;

    private static ParameterService parameterService;

    /**
     * Finalize the help widget for usage
     *
     * - Build the external help Url and set the javascript action to open the external help.
     *
     * @see org.kuali.rice.krad.uif.widget.WidgetBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);
        buildExternalHelpUrl();

        // set the javascript action to open the external help
        getHelpActionField().setClientSideJs("openHelpWindow('" + externalHelpUrl + "')");

        buildTooltipHelpText(parent);
    }

    /**
     * The external help Url is build from the parameter table record that matches the {@link HelpDefinition};
     * however, the external help Url can be overwritten and specified directly in the datadictionary/Uif deceleration.
     */
    private void buildExternalHelpUrl() {
        if (StringUtils.isBlank(externalHelpUrl)) {
            if ((helpDefinition != null) && StringUtils.isNotBlank(helpDefinition.getParameterNamespace())
                    && StringUtils.isNotBlank(helpDefinition.getParameterDetailType())
                    && StringUtils.isNotBlank(helpDefinition.getParameterName())) {
                externalHelpUrl = getParameterService().getParameterValueAsString(helpDefinition.getParameterNamespace(),
                        helpDefinition.getParameterDetailType(), helpDefinition.getParameterName());
            }
        }
    }

    private void buildTooltipHelpText(Component parent) {
        if (StringUtils.isBlank(tooltipHelpHtml) && (parent instanceof DataField)) {
            tooltipHelpHtml = ((DataField) parent).getHelpSummary();
        }

        if (StringUtils.isNotBlank(tooltipHelpHtml)) {
            // make sure that we are the component's native help and not a misconfigured standalone help bean.
            if ((parent instanceof Helpable) && (((Helpable) parent).getHelp() == this)) {
                // ToDo: Tooltip will have three different beans.  To get the proper definition we copy the help tooltip bean to the label's tooltip
                //((Helpable) parent).setTooltipOfComponent(this.getToolTip());
                ((Helpable) parent).getTooltipOfComponent().setTooltipContentHTML(tooltipHelpHtml);
                ((Helpable) parent).getTooltipOfComponent().setHelpFlag(true);
            }
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

    /**
     * HelpActionField is used for rendering external help
     *
     * @return ActionField for external help
     */
    public ActionField getHelpActionField() {
        return helpActionField;
    }

    /**
     * Setter for helpActionField
     *
     * @param helpActionField
     */
    public void setHelpActionField(ActionField helpActionField) {
        this.helpActionField = helpActionField;
    }

    /**
     * The help definition is used as the key to retrieve the external help Url from the parameter table of
     * the database.
     *
     * @return
     */
    public HelpDefinition getHelpDefinition() {
        return helpDefinition;
    }

    /**
     * Setter for the help definition of the database.
     *
     * @param helpDefinition
     */
    public void setHelpDefinition(HelpDefinition helpDefinition) {
        this.helpDefinition = helpDefinition;
    }

    /**
     * The external help Url.
     *
     * @return Url of the external help
     */
    public String getExternalHelpUrl() {
        return this.externalHelpUrl;
    }

    /**
     * Setter for externalHelpUrl.
     *
     * This should contain a valid Url.  When specified it overrides the help parameters in the database.
     *
     * @param externalHelpUrl
     */
    public void setExternalHelpUrl(String externalHelpUrl) {
        this.externalHelpUrl = externalHelpUrl;
    }

    /**
     * TooltipHelpHtml
     *
     * @return TooltipHelpHtml
     */
    public String getTooltipHelpHtml() {
        return this.tooltipHelpHtml;
    }

    /**
     * Setter for tooltipHelpHtml.
     *
     * @param tooltipHelpHtml
     */
    public void setTooltipHelpHtml(String tooltipHelpHtml) {
        this.tooltipHelpHtml = tooltipHelpHtml;
    }

    /**
     * Retrieve the parameter service.
     *
     * @return ParameterService
     */
    private ParameterService getParameterService() {
        if ( parameterService == null ) {
            parameterService = CoreFrameworkServiceLocator.getParameterService();
        }
        return parameterService;
    }

}
