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
package org.kuali.rice.krad.uif.widget;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;

/**
 * Widget that renders a Tooltip on a component.
 *
 * <p>
 * Tooltips can display extra information about an element. The content can be plain text or rich HTML. Tooltips
 * can be triggered by focus or mouse hover events.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "tooltip", parent = "Uif-Tooltip"),
        @BeanTag(name = "tooltipHelp", parent = "Uif-TooltipHelp"),
        @BeanTag(name = "tooltipFocus", parent = "Uif-TooltipFocus")})
public class Tooltip extends WidgetBase {
    private static final long serialVersionUID = -7641043761619191329L;

    private String tooltipContent;

    private boolean onFocus;
    private boolean onMouseHover;

    public Tooltip() {
        super();
    }

    /**
     * Plain text or HTML string that will be used to render the tooltip div
     *
     * @return String
     */
    @BeanTagAttribute
    public String getTooltipContent() {
        return tooltipContent;
    }

    /**
     * Setter for the tooltip content text
     *
     * @param tooltipContent
     */
    public void setTooltipContent(String tooltipContent) {
        if (tooltipContent != null) {
            this.tooltipContent = tooltipContent.replace("\"", "&quot;").replace("'", "&apos;");
        } else {
            this.tooltipContent = null;
        }
    }

    /**
     * Indicates the tooltip should be triggered by focus/blur
     *
     * @return boolean
     */
    @BeanTagAttribute
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
    @BeanTagAttribute
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