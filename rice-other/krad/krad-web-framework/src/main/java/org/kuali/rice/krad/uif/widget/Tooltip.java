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

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.view.View;

/**
 * Widget that renders a Tooltip on a component
 *
 * <p>
 * Tooltips can display extra information about an element. The content can be plain text or rich HTML. Tooltips
 * can be triggered by focus or mouse hover events.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags(
        {@BeanTag(name = "tooltip", parent = "Uif-Tooltip"), @BeanTag(name = "tooltipHelp", parent = "Uif-TooltipHelp"),
                @BeanTag(name = "tooltipFocus", parent = "Uif-TooltipFocus")})
public class Tooltip extends WidgetBase {

    private String tooltipContent;

    private boolean onFocus;
    private boolean onMouseHover;

    public Tooltip() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);
    }

    /**
     * Plain text or HTML string that will be used to render the tooltip div
     *
     * @return String
     */
    @BeanTagAttribute(name = "tooltipContent")
    public String getTooltipContent() {
        return tooltipContent;
    }

    /**
     * Setter for the tooltip content text
     *
     * @param tooltipContent
     */
    public void setTooltipContent(String tooltipContent) {
        this.tooltipContent = tooltipContent.replace("\"", "&quot;").replace("'", "&apos;");
    }

    /**
     * Indicates the tooltip should be triggered by focus/blur
     *
     * @return boolean
     */
    @BeanTagAttribute(name = "onFocus")
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
    @BeanTagAttribute(name = "onMouseHover")
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