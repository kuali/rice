/*
 * Copyright 2006-2012 The Kuali Foundation
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

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.view.View;

import java.util.Map;

/**
 * Widget that renders a Tooltip on a component
 *
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Tooltip extends WidgetBase {

    private String tooltipContentHTML;

    private boolean onFocusFlag;

    private boolean errorFlag;

    private boolean helpFlag;

    private Map<String, String> helpTemplateOptions;
    private Map<String, String> errorTemplateOptions;
    private Map<String, String> focusTemplateOptions;

    public Tooltip() {
        super();
    }

    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);
    }

    public String getTooltipContentHTML() {
        return tooltipContentHTML;
    }

    public void setTooltipContentHTML(String tooltipContentHTML) {
        this.tooltipContentHTML = tooltipContentHTML;
    }


    public boolean isOnFocusFlag() {
        return onFocusFlag;
    }

    public void setOnFocusFlag(boolean onFocusFlag) {
        if (onFocusFlag) {
            getTemplateOptions().putAll(focusTemplateOptions);
        }
        this.onFocusFlag = onFocusFlag;
    }

    /**
     * Template options that changes the style to help
     *
     * @return Map of focus template options
     */
    public Map<String, String> getHelpTemplateOptions() {
        return helpTemplateOptions;
    }


    public void setHelpTemplateOptions(Map<String, String> helpTemplateOptions) {
        this.helpTemplateOptions = helpTemplateOptions;
    }

    /**
     * Template options that changes the style to error
     *
     * @return Map of focus template options
     */
    public Map<String, String> getErrorTemplateOptions() {
        return errorTemplateOptions;
    }

    public void setErrorTemplateOptions(Map<String, String> errorTemplateOptions) {
        this.errorTemplateOptions = errorTemplateOptions;
    }

    public boolean isErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(boolean errorFlag) {
        if (errorFlag) {
            getTemplateOptions().putAll(errorTemplateOptions);
        }
        this.errorFlag = errorFlag;
    }

    public boolean isHelpFlag() {
        return helpFlag;
    }

    public void setHelpFlag(boolean helpFlag) {
        if (helpFlag) {
            getTemplateOptions().putAll(helpTemplateOptions);
        }
        this.helpFlag = helpFlag;
    }

    /**
     * Template options that changes the event trigger to focus/blur
     *
     * @return Map of focus template options
     */
    public Map<String, String> getFocusTemplateOptions() {
        return focusTemplateOptions;
    }

    /**
     * Setter for focusTemplateOptions
     * @param focusTemplateOptions
     */
    public void setFocusTemplateOptions(Map<String, String> focusTemplateOptions) {
        this.focusTemplateOptions = focusTemplateOptions;
    }
}