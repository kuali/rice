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
package org.kuali.rice.krad.uif.field;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.view.View;

/**
 * Field that contains a header element and optionally a <code>Group</code> to
 * present along with the header text
 *
 * <p>
 * Generally the group is used to display content to the right of the header,
 * such as links for the group or other information
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class HeaderField extends FieldGroup {
    private static final long serialVersionUID = -6950408292923393244L;

    private String headerText;
    private String headerLevel;
    private String headerStyleClasses;
    private String headerStyle;
    private String headerDivStyleClasses;
    private String headerDivStyle;

    public HeaderField() {
        super();
    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>Set render on group to false if no items are configured</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        // don't render header group if no items were configured
        if ((getGroup() != null) && (getGroup().getItems().isEmpty())) {
            getGroup().setRender(false);
        }
    }

    /**
     * Text that should be displayed on the header
     *
     * @return String header text
     */
    public String getHeaderText() {
        return this.headerText;
    }

    /**
     * Setter for the header text
     *
     * @param headerText
     */
    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    /**
     * HTML header level (h1 ... h6) that should be applied to the header text
     *
     * @return String header level
     */
    public String getHeaderLevel() {
        return this.headerLevel;
    }

    /**
     * Setter for the header level
     *
     * @param headerLevel
     */
    public void setHeaderLevel(String headerLevel) {
        this.headerLevel = headerLevel;
    }

    /**
     * Style class that should be applied to the header text (h tag)
     *
     * <p>
     * Note the style class given here applies to only the header text. The
     * style class property inherited from the <code>Component</code> interface
     * can be used to set the class for the whole field div (which could
     * include a nested <code>Group</code>)
     * </p>
     *
     * @return String style class
     * @see org.kuali.rice.krad.uif.Component.getStyleClasses()
     */
    public String getHeaderStyleClasses() {
        return this.headerStyleClasses;
    }

    /**
     * Setter for the header style class
     *
     * @param headerStyleClasses
     */
    public void setHeaderStyleClasses(String headerStyleClasses) {
        this.headerStyleClasses = headerStyleClasses;
    }

    /**
     * Style that should be applied to the header text
     *
     * <p>
     * Note the style given here applies to only the header text. The style
     * property inherited from the <code>Component</code> interface can be used
     * to set the style for the whole field div (which could include a nested
     * <code>Group</code>)
     * </p>
     *
     * @return String header style
     * @see org.kuali.rice.krad.uif.Component.getStyle()
     */
    public String getHeaderStyle() {
        return this.headerStyle;
    }

    /**
     * Setter for the header style
     *
     * @param headerStyle
     */
    public void setHeaderStyle(String headerStyle) {
        this.headerStyle = headerStyle;
    }

    /**
     * Style class that should be applied to the header div
     *
     * <p>
     * Note the style class given here applies to the div surrounding the header tag only
     * </p>
     *
     * @return String style class
     * @see org.kuali.rice.krad.uif.Component.getStyleClasses()
     */
    public String getHeaderDivStyleClasses() {
        return headerDivStyleClasses;
    }

    /**
     * Setter for the header div class
     *
     * @param headerStyleClasses
     */
    public void setHeaderDivStyleClasses(String headerDivStyleClasses) {
        this.headerDivStyleClasses = headerDivStyleClasses;
    }

    /**
     * Style that should be applied to the header div
     *
     * <p>
     * Note the style given here applies to the div surrounding the header tag only
     * </p>
     *
     * @return String header style
     * @see org.kuali.rice.krad.uif.Component.getStyle()
     */
    public String getHeaderDivStyle() {
        return headerDivStyle;
    }

    /**
     * Setter for the header div
     *
     * @param headerStyle
     */
    public void setHeaderDivStyle(String headerDivStyle) {
        this.headerDivStyle = headerDivStyle;
    }
}
