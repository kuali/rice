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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.util.UrlInfo;

/**
 * A special action component that is used within a {@link DropdownMenu}
 *
 * <p>
 * An action that adds an option to behave as a standard link (using {@link #getActionUrl()}, and options for
 * menu divders and headers
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MenuAction extends Action {
    private static final long serialVersionUID = 143935176537425843L;

    private UrlInfo actionUrl;

    private boolean menuDivider;
    private boolean menuHeader;

    public MenuAction() {
        super();
    }

    /**
     * If the {@link #getActionUrl()} is configured, sets up the action script to open the configured URL
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(Object model, Component parent) {
        if (StringUtils.isBlank(getActionScript()) && (actionUrl != null) && actionUrl.isFullyConfigured()) {
            String actionScript = ScriptUtils.buildFunctionCall(UifConstants.JsFunctions.REDIRECT, actionUrl.getHref());

            setActionScript(actionScript);
        }

        super.performFinalize(model, parent);
    }

    /**
     * Url to open when the action item is selected
     *
     * <p>
     * This makes the menu action behave like a standard link. Instead of posting the form, the configured URL will
     * simply be opened (using window.open). For using standard post actions these does not need to be configured
     * </p>
     *
     * @return Url info instance for the configuration action link
     */
    public UrlInfo getActionUrl() {
        return actionUrl;
    }

    /**
     * @see MenuAction#getActionUrl()
     */
    public void setActionUrl(UrlInfo actionUrl) {
        this.actionUrl = actionUrl;
    }

    /**
     * Indicates whether the menu action should be rendered as a divider
     *
     * <p>
     * When set, a divider is placed into the menu (at the position of the action). Since an action is not
     * rendered, no other properties need to be set
     * </p>
     *
     * @return boolean true if a divider should be rendered, false if not
     */
    public boolean isMenuDivider() {
        return menuDivider;
    }

    /**
     * @see MenuAction#isMenuDivider()
     */
    public void setMenuDivider(boolean menuDivider) {
        this.menuDivider = menuDivider;
    }

    /**
     * Indicates whether the menu action should be rendered as a header
     *
     * <p>
     * When set, a header is placed into the menu (at the position of the action). The property {@link
     * #getActionLabel()} is used for the header text. No other properties are required to be configured
     * </p>
     *
     * @return boolean true if a header should be rendered, false if not
     */
    public boolean isMenuHeader() {
        return menuHeader;
    }

    /**
     * @see MenuAction#isMenuHeader()
     */
    public void setMenuHeader(boolean menuHeader) {
        this.menuHeader = menuHeader;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#copyProperties(Object)
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        MenuAction menuActionCopy = (MenuAction) component;

        if (this.actionUrl != null) {
            menuActionCopy.setActionUrl((UrlInfo) this.actionUrl.copy());
        }

        menuActionCopy.setMenuDivider(this.menuDivider);
        menuActionCopy.setMenuHeader(this.menuHeader);
    }
}
