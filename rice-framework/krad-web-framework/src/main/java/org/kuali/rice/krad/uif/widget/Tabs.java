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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.ClientSideState;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.TabGroup;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Widget used for configuring tab options, use componentOptions for most options.
 * See http://jqueryui.com/demos/tabs/ for usable options
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "tabs", parent = "Uif-Tabs")
public class Tabs extends WidgetBase {
    private static final long serialVersionUID = 2L;

    @ClientSideState(variableName = "activeTab")
    private String defaultActiveTabId;

    private String tabContentClass;
    private String tabNavClass;

    private UifConstants.Position position = UifConstants.Position.TOP;

    public Tabs() {
        super();
    }

    /**
     * The following is performed:
     *
     * <ul>
     * <li>If the active tab id is configured, set the active plugin option</li>
     * </ul>
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        if (!(parent instanceof TabGroup)) {
            throw new RuntimeException("Parent for tabs widget should be tab group, not " + parent.getClass());
        }

        TabGroup tabGroup = (TabGroup) parent;

        if (StringUtils.isBlank(defaultActiveTabId) && CollectionUtils.isNotEmpty(tabGroup.getItems())) {
            defaultActiveTabId = tabGroup.getItems().get(0).getId();
        }

        if (position.equals(UifConstants.Position.LEFT) || position.equals(UifConstants.Position.RIGHT)) {
            tabNavClass = tabNavClass + " col-sm-3";
            tabContentClass = tabContentClass + " col-sm-9";
        }

        if (position.equals(UifConstants.Position.LEFT)) {
            ((TabGroup) parent).addStyleClass(CssConstants.Tabs.TABS_LEFT);
        } else if (position.equals(UifConstants.Position.RIGHT)) {
            ((TabGroup) parent).addStyleClass(CssConstants.Tabs.TABS_RIGHT);
        } else if (position.equals(UifConstants.Position.BOTTOM)) {
            ((TabGroup) parent).addStyleClass(CssConstants.Tabs.TABS_BOTTOM);
        }
    }

    /**
     * Id for the group within the tab group that should be active (shown first), by default the first
     * group is active
     *
     * @return id for the group within the tab group that should be initially active
     */
    @BeanTagAttribute
    public String getDefaultActiveTabId() {
        return defaultActiveTabId;
    }

    /**
     * Setter for the active group id
     *
     * @param defaultActiveTabId
     */
    public void setDefaultActiveTabId(String defaultActiveTabId) {
        this.defaultActiveTabId = defaultActiveTabId;
    }

    /**
     * The position the tabs will appear related to the group, options are TOP, BOTTOM, RIGHT, or LEFT
     *
     * @return position for tabs
     */
    @BeanTagAttribute
    public UifConstants.Position getPosition() {
        return position;
    }

    /**
     * Setter for the tabs position
     *
     * @param position
     */
    public void setPosition(UifConstants.Position position) {
        this.position = position;
    }

    /**
     * Css class for the div which wraps the tab content panels, the default bean defines this as "tabs-content"
     *
     * @return css tab content css class
     */
    @BeanTagAttribute(name = "tabContentClass")
    public String getTabContentClass() {
        return tabContentClass;
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.Tabs#getTabContentClass()
     */
    public void setTabContentClass(String tabContentClass) {
        this.tabContentClass = tabContentClass;
    }

    /**
     * Css class for the ul list of tab navigation links, the default bean defines this as "nav nav-tabs"
     *
     * @return the ul tab navigation css class
     */
    @BeanTagAttribute(name = "tabNavClass")
    public String getTabNavClass() {
        return tabNavClass;
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.Tabs#getTabNavClass()
     */
    public void setTabNavClass(String tabNavClass) {
        this.tabNavClass = tabNavClass;
    }
}
