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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ListAware;

/**
 * Renders a dropdown menu (context menu) of actions.
 *
 * <p>The dropdown menu component can be used to build context menus or full application menus. Essentially the
 * component is configured by first setting the text that will appear as a link (optionally with a caret). When the
 * user clicks the link, the dropdown of actions ({@link #getMenuActions()} will be presented.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DropdownMenu extends ContentElementBase implements ListAware {
    private static final long serialVersionUID = -1759659012620124641L;

    private String dropdownToggleText;
    private Message dropdownToggle;

    private boolean renderToggleCaret;
    private boolean renderToggleButton;
    private boolean renderedInList;

    private List<MenuAction> menuActions;

    public DropdownMenu() {
        super();

        renderToggleCaret = true;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#performApplyModel(Object,
     *      org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performApplyModel(Object model, Component parent) {
        super.performApplyModel(model, parent);

        if (StringUtils.isNotBlank(dropdownToggleText) && StringUtils.isBlank(dropdownToggle.getMessageText())) {
            dropdownToggle.setMessageText(dropdownToggleText);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(dropdownToggle);

        if (menuActions != null) {
            components.addAll(menuActions);
        }

        return components;
    }

    /**
     * Text to display as the dropdown toggle link
     *
     * <p>
     * This text will appear as a link for the user to click on, which then will bring up the dropdown menu. This
     * property is a shortcut for {@link #getDropdownToggle().setMessageText()}. This text is not required, in which
     * case only the caret will render
     * </p>
     *
     * @return text to display for the dropdown toggle link
     */
    public String getDropdownToggleText() {
        return dropdownToggleText;
    }

    /**
     * @see DropdownMenu#getDropdownToggleText()
     */
    public void setDropdownToggleText(String dropdownToggleText) {
        this.dropdownToggleText = dropdownToggleText;
    }

    /**
     * {@code Message} component that is associated with the dropdown toggle text, can be used to adjust styling
     * and so forth
     *
     * @return Message instance for toggle text
     */
    public Message getDropdownToggle() {
        return dropdownToggle;
    }

    /**
     * @see DropdownMenu#getDropdownToggle()
     */
    public void setDropdownToggle(Message dropdownToggle) {
        this.dropdownToggle = dropdownToggle;
    }

    /**
     * Indicates whether a caret icon should be rendered to the right of the toggle text (if present)
     *
     * @return boolean true if caret should be rendered, false if not
     */
    public boolean isRenderToggleCaret() {
        return renderToggleCaret;
    }

    /**
     * @see DropdownMenu#isRenderToggleCaret()
     */
    public void setRenderToggleCaret(boolean renderToggleCaret) {
        this.renderToggleCaret = renderToggleCaret;
    }

    /**
     * Indicates whether a caret button should be rendered to the right of the toggle text (if present)
     *
     * @return boolean true if caret button should be rendered, false if not
     */
    public boolean isRenderToggleButton() {
        return renderToggleButton;
    }

    /**
     * @see DropdownMenu#isRenderToggleButton()
     */
    public void setRenderToggleButton(boolean renderToggleButton) {
        this.renderToggleButton = renderToggleButton;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ListAware#setRenderedInList(boolean)
     */
    public boolean isRenderedInList() {
        return renderedInList;
    }

    /**
     * @see DropdownMenu#isRenderedInList()
     */
    public void setRenderedInList(boolean renderedInList) {
        this.renderedInList = renderedInList;
    }

    /**
     * List of {@link MenuAction} instances that should be rendered for the dropdown
     *
     * <p>
     * Actions for the menu are configured through this list. The order of the actions within the list is
     * the order they will appear in the dropdown
     * </p>
     *
     * @return List of menu actions for the dropdown
     */
    public List<MenuAction> getMenuActions() {
        return menuActions;
    }

    /**
     * @see DropdownMenu#getMenuActions()
     */
    public void setMenuActions(List<MenuAction> menuActions) {
        this.menuActions = menuActions;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#copy()
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        DropdownMenu dropdownCopy = (DropdownMenu) component;

        if (this.dropdownToggle != null) {
            dropdownCopy.setDropdownToggle((Message) this.dropdownToggle.copy());
        }
        dropdownCopy.setDropdownToggleText(this.dropdownToggleText);

        dropdownCopy.setRenderToggleCaret(this.renderToggleCaret);
        dropdownCopy.setRenderToggleButton(this.renderToggleButton);
        dropdownCopy.setRenderedInList(this.renderedInList);

        if (this.menuActions != null) {
            List<MenuAction> optionsCopy = new ArrayList<MenuAction>();

            for (MenuAction action : this.menuActions) {
                optionsCopy.add((MenuAction) action.copy());
            }
            dropdownCopy.setMenuActions(optionsCopy);
        }
    }
}
