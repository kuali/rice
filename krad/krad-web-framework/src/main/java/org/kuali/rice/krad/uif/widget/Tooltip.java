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
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Tooltip extends WidgetBase {

    private String tooltipContent;

    private boolean onFocus;

    private boolean onMouseHover;
//    private boolean helpFlag;
//
//    private Map<String, String> helpTemplateOptions;
//    private Map<String, String> focusTemplateOptions;

    public Tooltip() {
        super();
    }

    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);
    }

    /**
     * Plain text or HTML string that will be used to render the tooltip div
     *
     * @return String
     */
    public String getTooltipContent() {
        return tooltipContent;
    }

    /**
     * Setter for the tooltip content text
     *
     * @param tooltipContent
     */
    public void setTooltipContent(String tooltipContent) {
        this.tooltipContent = tooltipContent;
    }

    /**
     * Indicates the tooltip should be triggered by focus/blur
     *
     * @return boolean
     */
    public boolean isOnFocus() {
        return onFocus;
    }

    /**
     * Setter for the onFocus
     *
     * @param onFocus
     */
    public void setOnFocus(boolean onFocus) {
        this.onFocus = onFocus;
    }

    /**
     * Indicates the tooltip should be triggered by mouse hover
     *
     * @return boolean
     */
    public boolean isOnMouseHover() {
        return onMouseHover;
    }

    /**
     * Setter for onMouseHover
     *
     * @param onMouseHover
     */
    public void setOnMouseHover(boolean onMouseHover) {
        this.onMouseHover = onMouseHover;
    }
}