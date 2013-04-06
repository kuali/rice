/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.element;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.view.View;

/**
 * ValidationMessages for logic and options specific to groups
 */
@BeanTag(name = "fieldValidationMessages-bean", parent = "Uif-FieldValidationMessages")
public class FieldValidationMessages extends ValidationMessages {

    private boolean useTooltip;
    private boolean showIcons;

    @Override
    /**
     * Calls super and add dataAttributes that are appropriate for field level validationMessages
     * data.  This data is used by the validation framework clientside.
     *
     * @see krad.validate.js
     */
    public void generateMessages(boolean reset, View view, Object model, Component parent) {
        super.generateMessages(reset, view, model, parent);
        boolean hasMessages = false;
        if (!this.getErrors().isEmpty() || !this.getWarnings().isEmpty() || !this.getInfos().isEmpty()) {
            hasMessages = true;
        }
        parent.addDataAttribute("validationMessages", "{"
                + "displayMessages:"
                + this.isDisplayMessages()
                + ","
                + "useTooltip:"
                + useTooltip
                + ","
                + "messagingEnabled:"
                + this.isDisplayMessages()
                + ","
                + "hasOwnMessages:"
                + hasMessages
                + ","
                + "showIcons:"
                + showIcons
                + ","
                + "serverErrors:"
                + ScriptUtils.convertStringListToJsArray(ScriptUtils.escapeHtml(this.getErrors()))
                + ","
                + "serverWarnings:"
                + ScriptUtils.convertStringListToJsArray(ScriptUtils.escapeHtml(this.getWarnings()))
                + ","
                + "serverInfo:"
                + ScriptUtils.convertStringListToJsArray(ScriptUtils.escapeHtml(this.getInfos()))
                + "}");
    }

    /**
     * When true, use the tooltip on fields to display their relevant messages.  When false, these messages
     * will appear directly below the control.
     *
     * @return true if using tooltips for messages, false to display below control
     */
    @BeanTagAttribute(name = "useTooltip")
    public boolean isUseTooltip() {
        return useTooltip;
    }

    /**
     * Set the useTooltip flag
     *
     * @param useTooltip if true, show tooltip, otherwise show messages below field control
     */
    public void setUseTooltip(boolean useTooltip) {
        this.useTooltip = useTooltip;
    }

    /**
     * If true, display dynamic icons next to fields which have messages.  Otherwise, do not render these icons.
     *
     * @return true if icons will be displayed, false otherwise
     */
    @BeanTagAttribute(name = "showIcons")
    public boolean isShowIcons() {
        return showIcons;
    }

    /**
     * Set whether field validation icons should display or not.
     *
     * @param showIcons
     */
    public void setShowIcons(boolean showIcons) {
        this.showIcons = showIcons;
    }
}
