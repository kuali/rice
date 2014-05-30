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
package org.kuali.rice.krad.uif.element;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ListAware;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Renders a toggle menu (aka sub menu, dropdown menu) of items.
 *
 * <p>The toggle menu component can be used to build context menus or full application menus. Essentially the
 * component is configured by first setting the text that will appear as a link (optionally with a caret). When the
 * user clicks the link, the items ({@link #getMenuItems()} will be presented.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "dropdownMenu", parent = "Uif-DropdownToggleMenu"),
        @BeanTag(name = "sidebarMenu", parent = "Uif-SidebarToggleMenu")})
public class ToggleMenu extends ContentElementBase implements ListAware {
    private static final long serialVersionUID = -1759659012620124641L;

    private String toggleText;
    private Message toggleMessage;

    private String toggleCaretClass;
    private String iconClass;
    private boolean renderToggleButton;
    private boolean renderedInList;

    private List<Component> menuItems;
    private Group menuGroup;

    public ToggleMenu() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);

        if ((this.menuItems != null) && !this.menuItems.isEmpty()) {
            this.menuGroup.setItems(menuItems);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        if (StringUtils.isNotBlank(toggleText) && StringUtils.isBlank(toggleMessage.getMessageText())) {
            toggleMessage.setMessageText(toggleText);
        }
    }

    /**
     * Text to display as the toggle menu toggle link
     * 
     * <p>
     * This text will appear as a link for the user to click on, which then will bring up the toggle
     * menu menu. This property is a shortcut for {@link #getToggleMessage()}
     * {@link Message#setMessageText(String) .setMessageText}. This text is not required, in which
     * case only the caret will render
     * </p>
     * 
     * @return text to display for the toggle menu toggle link
     */
    @BeanTagAttribute
    public String getToggleText() {
        return toggleText;
    }

    /**
     * @see ToggleMenu#getToggleText()
     */
    public void setToggleText(String toggleText) {
        this.toggleText = toggleText;
    }

    /**
     * {@code Message} component that is associated with the toggle menu toggle text, can be used to adjust styling
     * and so forth
     *
     * @return Message instance for toggle text
     */
    @BeanTagAttribute
    public Message getToggleMessage() {
        return toggleMessage;
    }

    /**
     * @see ToggleMenu#getToggleMessage()
     */
    public void setToggleMessage(Message toggleMessage) {
        this.toggleMessage = toggleMessage;
    }

    /**
     * Css class to use when rendering a caret icon which will appear to the right of the toggleText
     *
     * @return the caret icon class
     */
    @BeanTagAttribute
    public String getToggleCaretClass() {
        return toggleCaretClass;
    }

    /**
     * @see org.kuali.rice.krad.uif.element.ToggleMenu#getToggleCaretClass()
     */
    public void setToggleCaretClass(String toggleCaretClass) {
        this.toggleCaretClass = toggleCaretClass;
    }

    /**
     * Css class for an icon that will appear to the left of the toggleText
     *
     * @return the css class for an icon
     */
    @BeanTagAttribute
    public String getIconClass() {
        return iconClass;
    }

    /**
     * @see org.kuali.rice.krad.uif.element.ToggleMenu#getIconClass()
     */
    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    /**
     * Indicates whether a caret button should be rendered to the right of the toggle text (if present)
     *
     * @return boolean true if caret button should be rendered, false if not
     */
    @BeanTagAttribute
    public boolean isRenderToggleButton() {
        return renderToggleButton;
    }

    /**
     * @see ToggleMenu#isRenderToggleButton()
     */
    public void setRenderToggleButton(boolean renderToggleButton) {
        this.renderToggleButton = renderToggleButton;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ListAware#setRenderedInList(boolean)
     */
    @BeanTagAttribute
    public boolean isRenderedInList() {
        return renderedInList;
    }

    /**
     * @see ToggleMenu#isRenderedInList()
     */
    public void setRenderedInList(boolean renderedInList) {
        this.renderedInList = renderedInList;
    }

    /**
     * List of components that should be rendered for the toggle menu.
     *
     * <p>Items for the menu are configured through this list. The order of the items within the list is
     * the order they will appear in the toggle menu</p>
     *
     * @return List of menu items for the toggle menu
     */
    @ViewLifecycleRestriction
    @BeanTagAttribute
    public List<Component> getMenuItems() {
        return menuItems;
    }

    /**
     * @see ToggleMenu#getMenuItems()
     */
    public void setMenuItems(List<Component> menuItems) {
        this.menuItems = menuItems;
    }

    /**
     * Group instance that is rendered when the toggle menu is toggled.
     *
     * <p>Note in most cases this group will be a simple list group. The component allows for the list group
     * to be initialized in a base bean, then child beans can simply define the item using
     * {@link ToggleMenu#getMenuItems()}</p>
     *
     * @return Group instance
     */
    @BeanTagAttribute
    public Group getMenuGroup() {
        return menuGroup;
    }

    /**
     * @see ToggleMenu#getMenuGroup()
     */
    public void setMenuGroup(Group menuGroup) {
        this.menuGroup = menuGroup;
    }
}
