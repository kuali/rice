/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.container;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.Growls;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.springframework.util.AutoPopulatingList;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PageGroup extends Group {
    private static final long serialVersionUID = 7571981300587270274L;

    private boolean autoFocus;

    /**
     * Perform finalize here adds to its document ready script the
     * setupValidator js function for setting up the validator for this view.
     *
     * @see org.kuali.rice.krad.uif.container.ContainerBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        String prefixScript = "";
        if (this.getOnDocumentReadyScript() != null) {
            prefixScript = this.getOnDocumentReadyScript();
        }

        // growls are setup here because they are relevant to the current page, but their
        // settings are global to the view
        String growlScript = "";
        if (view.isGrowlMessagingEnabled()) {
            Growls gw = view.getGrowls();

            //Setup defaults
            if (!gw.getComponentOptions().isEmpty()) {
                growlScript = "setGrowlDefaults(" + gw.getComponentOptionsJSString() + ");";
            }

            ConfigurationService configService = KRADServiceLocator.getKualiConfigurationService();
            MessageMap messageMap = GlobalVariables.getMessageMap();
            if (messageMap.hasErrors()) {
                String message = configService.getPropertyValueAsString("growl.hasErrors");
                if (StringUtils.isNotBlank(message)) {
                    growlScript =
                            growlScript + "showGrowl('" + message + "', '" + configService.getPropertyValueAsString(
                                    "general.error") + "', 'errorGrowl');";
                }
            }

            if (messageMap.hasWarnings()) {
                String message = configService.getPropertyValueAsString("growl.hasWarnings");
                if (StringUtils.isNotBlank(message)) {
                    growlScript =
                            growlScript + "showGrowl('" + message + "', '" + configService.getPropertyValueAsString(
                                    "general.warning") + "', 'warningGrowl');";
                }
            }

            if (messageMap.hasInfo()) {
                List<String> properties = messageMap.getPropertiesWithInfo();
                String message = "";
                for (String property : properties) {
                    List<AutoPopulatingList<ErrorMessage>> lists = messageMap.getInfoMessagesForProperty(property,
                            true);
                    for (List<ErrorMessage> errorList : lists) {
                        if (errorList != null) {
                            for (ErrorMessage e : errorList) {
                                if (StringUtils.isBlank(message)) {
                                    message = configService.getPropertyValueAsString(e.getErrorKey());
                                } else {
                                    message = message + "<br/>" + configService.getPropertyValueAsString(
                                            e.getErrorKey());
                                }
                                if (e.getMessageParameters() != null) {
                                    message = message.replace("'", "''");
                                    message = MessageFormat.format(message, (Object[]) e.getMessageParameters());
                                }
                            }
                        }
                    }
                }

                if (StringUtils.isNotBlank(message)) {
                    growlScript =
                            growlScript + "showGrowl('" + message + "', '" + configService.getPropertyValueAsString(
                                    "general.info") + "', 'infoGrowl');";
                }
            }
        }

        this.setOnDocumentReadyScript(prefixScript + "\nsetupValidator();" + growlScript);
    }

    /**
     * When this is true, the first field of the kualiForm will be focused by
     * default, unless the parameter focusId is set on the form (by an
     * actionField), then that field will be focused instead. When this setting
     * if false, no field will be focused.
     *
     * @return the autoFocus
     */
    public boolean isAutoFocus() {
        return this.autoFocus;
    }

    /**
     * @param autoFocus the autoFocus to set
     */
    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

}
