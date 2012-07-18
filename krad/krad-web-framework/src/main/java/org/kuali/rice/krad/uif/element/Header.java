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
package org.kuali.rice.krad.uif.element;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Content element that renders a header element and optionally a <code>Group</code> to
 * present along with the header text
 *
 * <p>
 * Generally the group is used to display content to the right of the header,
 * such as links for the group or other information
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Header extends ContentElementBase {
    private static final long serialVersionUID = -6950408292923393244L;

    private String headerText;
    private String headerLevel;

    private String headerTagStyle;
    private List<String> headerTagCssClasses;

    private Group upperGroup;
    private Group rightGroup;
    private Group lowerGroup;

    public Header() {
        super();

        headerTagCssClasses = new ArrayList<String>();
    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>Set render on header group to false if no items are configured</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        // don't render header groups if no items were configured
        if ((getUpperGroup() != null) && (getUpperGroup().getItems().isEmpty())) {
            getUpperGroup().setRender(false);
        }

        if ((getRightGroup() != null) && (getRightGroup().getItems().isEmpty())) {
            getRightGroup().setRender(false);
        }

        if ((getLowerGroup() != null) && (getLowerGroup().getItems().isEmpty())) {
            getLowerGroup().setRender(false);
        }
        
        //add preset styles to header groups
        if(getUpperGroup() != null){
            getUpperGroup().addStyleClass("uif-header-upperGroup");
        }

        if(getRightGroup() != null){
            getRightGroup().addStyleClass("uif-header-rightGroup");
        }

        if(getLowerGroup() != null){
            getLowerGroup().addStyleClass("uif-header-lowerGroup");
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(upperGroup);
        components.add(rightGroup);
        components.add(lowerGroup);

        return components;
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
     * Style classes that should be applied to the header text (h tag)
     *
     * <p>
     * Note the style class given here applies to only the header text. The
     * style class property inherited from the <code>Component</code> interface
     * can be used to set the class for the whole field div (which could
     * include a nested <code>Group</code>)
     * </p>
     *
     * @return List<String> list of style classes
     * @see org.kuali.rice.krad.uif.component.Component#getCssClasses()
     */
    public List<String> getHeaderTagCssClasses() {
        return this.headerTagCssClasses;
    }

    /**
     * Setter for the list of classes to apply to the header h tag
     *
     * @param headerTagCssClasses
     */
    public void setHeaderTagCssClasses(List<String> headerTagCssClasses) {
        this.headerTagCssClasses = headerTagCssClasses;
    }

    /**
     * Builds the HTML class attribute string by combining the headerStyleClasses list
     * with a space delimiter
     *
     * @return String class attribute string
     */
    public String getHeaderStyleClassesAsString() {
        if (headerTagCssClasses != null) {
            return StringUtils.join(headerTagCssClasses, " ");
        }

        return "";
    }

    /**
     * Style that should be applied to the header h tag
     *
     * <p>
     * Note the style given here applies to only the header text. The style
     * property inherited from the <code>Component</code> interface can be used
     * to set the style for the whole header div (which could include a nested
     * <code>Group</code>)
     * </p>
     *
     * @return String header style
     * @see org.kuali.rice.krad.uif.component.Component#getStyle()
     */
    public String getHeaderTagStyle() {
        return this.headerTagStyle;
    }

    /**
     * Setter for the header h tag style
     *
     * @param headerTagStyle
     */
    public void setHeaderTagStyle(String headerTagStyle) {
        this.headerTagStyle = headerTagStyle;
    }

    /**
     * Nested group instance that can be used to render contents above the header text
     *
     * <p>
     * The header group is useful for adding content such as links or actions that is presented with the header
     * </p>
     *
     * @return Group instance
     */
    public Group getUpperGroup() {
        return upperGroup;
    }

    /**
     * Setter for the header group instance that is rendered above the header text
     *
     * @param upperGroup
     */
    public void setUpperGroup(Group upperGroup) {
        this.upperGroup = upperGroup;
    }

    /**
     * Nested group instance that can be used to render contents to the right of the header text
     *
     * <p>
     * The header group is useful for adding content such as links or actions that is presented with the header
     * </p>
     *
     * @return Group instance
     */
    public Group getRightGroup() {
        return rightGroup;
    }

    /**
     * Setter for the header group instance that is rendered to the right of the header text
     *
     * @param rightGroup
     */
    public void setRightGroup(Group rightGroup) {
        this.rightGroup = rightGroup;
    }

    /**
     * Nested group instance that can be used to render contents below the header text
     *
     * <p>
     * The header group is useful for adding content such as links or actions that is presented with the header
     * </p>
     *
     * @return Group instance
     */
    public Group getLowerGroup() {
        return lowerGroup;
    }

    /**
     * Setter for the header group instance that is rendered below the header text
     *
     * @param lowerGroup
     */
    public void setLowerGroup(Group lowerGroup) {
        this.lowerGroup = lowerGroup;
    }

    /**
     * List of <code>Component</code> instances contained in the lower header group
     *
     * <p>
     * Convenience method for configuration to get the items List from the
     * lower header group
     * </p>
     *
     * @return List<? extends Component> items
     */
    public List<? extends Component> getItems() {
        if (lowerGroup != null) {
            return lowerGroup.getItems();
        }

        return null;
    }

    /**
     * Setter for the lower group's items
     *
     * <p>
     * Convenience method for configuration to set the items List for the
     * lower header group
     * </p>
     *
     * @param items
     */
    public void setItems(List<? extends Component> items) {
        if (lowerGroup != null) {
            lowerGroup.setItems(items);
        }
    }
}
