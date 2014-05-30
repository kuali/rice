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
package org.kuali.rice.krad.uif.container;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.ToggleMenu;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * A navigation group which renders a menu with items, that is shown at the side of the page with collapse.
 * functionality
 *
 * <p>Items of this menu should only be of {@link org.kuali.rice.krad.uif.element.Header}, {@link Action}, and
 * {@link ToggleMenu} types.  Actions and ToggleMenus must have icons to render correctly when using the collapse
 * functionality, but will inherit the defaultItemIconClass if their iconClass properties are not set.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "sidebarNavigation", parent = "Uif-SidebarNavigationGroup"),
        @BeanTag(name = "menuNavigation", parent = "Uif-MenuNavigationGroup")})
public class SidebarNavigationGroup extends GroupBase {
    private static final long serialVersionUID = -8388015161780120970L;

    private boolean renderCollapse;
    private String openedToggleIconClass;
    private String closedToggleIconClass;
    private String defaultItemIconClass;

    private static final String ARROW_CSS = "arrow";

    /**
     * Adds icons and classes to {@link Action} and {@link ToggleMenu} items which exist in its items
     * for rendering purposes.
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        for (Component item : this.getItems()) {
            if (item instanceof ToggleMenu) {
                ((ToggleMenu) item).setRenderedInList(true);
                ((ToggleMenu) item).setToggleCaretClass(ARROW_CSS + " " + closedToggleIconClass);

                if (StringUtils.isBlank(((ToggleMenu) item).getIconClass())) {
                    ((ToggleMenu) item).setIconClass(defaultItemIconClass);
                }
            } else if (item instanceof Action) {
                ((Action) item).setRenderInnerTextSpan(true);

                if (StringUtils.isBlank(((Action) item).getIconClass())) {
                    ((Action) item).setIconClass(defaultItemIconClass);
                }
            }
        }
    }

    /**
     * When true, render the collapse icon (an icon that the user can click to close/open the sidebar navigation).
     *
     * @return true if the collapse icon should be rendered, false otherwise
     */
    @BeanTagAttribute
    public boolean isRenderCollapse() {
        return renderCollapse;
    }

    /**
     * @see org.kuali.rice.krad.uif.container.SidebarNavigationGroup#isRenderCollapse()
     */
    public void setRenderCollapse(boolean renderCollapse) {
        this.renderCollapse = renderCollapse;
    }

    /**
     * Icon class to use to render a opened icon for sub menus (the {@link ToggleMenu} items) that exist
     * in this navigation menu.
     *
     * @return the opened ToggleMenu icon
     */
    @BeanTagAttribute
    public String getOpenedToggleIconClass() {
        return openedToggleIconClass;
    }

    /**
     * @see org.kuali.rice.krad.uif.container.SidebarNavigationGroup#getOpenedToggleIconClass()
     */
    public void setOpenedToggleIconClass(String openedToggleIconClass) {
        this.openedToggleIconClass = openedToggleIconClass;
    }

    /**
     * Icon class to use to render a closed icon for sub menus (the {@link ToggleMenu} items) that exist
     * in this navigation menu.
     *
     * @return the closed ToggleMenu icon
     */
    @BeanTagAttribute
    public String getClosedToggleIconClass() {
        return closedToggleIconClass;
    }

    /**
     * @see org.kuali.rice.krad.uif.container.SidebarNavigationGroup#getClosedToggleIconClass()
     */
    public void setClosedToggleIconClass(String closedToggleIconClass) {
        this.closedToggleIconClass = closedToggleIconClass;
    }

    /**
     * The default css class to use for the icons of the items which exist in this navigation menu if they are not set
     * on the items themselves (icons are required by {@link Action} and {@link ToggleMenu} items in this menu).
     *
     * @return the default icon class
     */
    @BeanTagAttribute
    public String getDefaultItemIconClass() {
        return defaultItemIconClass;
    }

    /**
     * @see org.kuali.rice.krad.uif.container.SidebarNavigationGroup#getDefaultItemIconClass()
     */
    public void setDefaultItemIconClass(String defaultItemIconClass) {
        this.defaultItemIconClass = defaultItemIconClass;
    }
}
