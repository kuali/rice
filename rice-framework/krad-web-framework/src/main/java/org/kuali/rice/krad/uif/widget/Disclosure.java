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
package org.kuali.rice.krad.uif.widget;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.component.ClientSideState;
import org.kuali.rice.krad.uif.component.Component;

/**
 * Decorates a group with collapse/expand functionality
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "disclosure-bean", parent = "Uif-Disclosure")
public class Disclosure extends WidgetBase {
    private static final long serialVersionUID = 1238789480161901850L;

    private String collapsedIconClass;
    private String expandedIconClass;

    private boolean renderIcon;

    private int animationSpeed;

    @ClientSideState(variableName = "open")
    private boolean defaultOpen;
    private boolean ajaxRetrievalWhenOpened;

    public Disclosure() {
        super();

        defaultOpen = true;
        renderIcon = true;
    }

    /**
     * Sets forceSessionPersistence when using the ajax retrieval option
     *
     * @see Component#performApplyModel(Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performApplyModel(Object model, Component parent) {
        super.performFinalize(model, parent);

        if (ajaxRetrievalWhenOpened) {
            parent.setForceSessionPersistence(true);
        }
    }

    /**
     * Class for the icon that should be rendered when the disclosure group is disclosed.
     *
     * <p>Note this is only applicable when {@link #isRenderIcon()} is true</p>
     *
     * @return class for collapsed icon
     */
    public String getCollapsedIconClass() {
        return collapsedIconClass;
    }

    /**
     * @see Disclosure#getCollapsedIconClass()
     */
    public void setCollapsedIconClass(String collapsedIconClass) {
        this.collapsedIconClass = collapsedIconClass;
    }

    /**
     * Class for the icon that should be rendered when the disclosure group is expanded.
     *
     * <p>Note this is only applicable when {@link #isRenderIcon()} is true</p>
     *
     * @return class for expanded icon
     */
    public String getExpandedIconClass() {
        return expandedIconClass;
    }

    /**
     * @see Disclosure#getExpandedIconClass()
     */
    public void setExpandedIconClass(String expandedIconClass) {
        this.expandedIconClass = expandedIconClass;
    }

    /**
     * Indicates whether the expanded and collapsed icons should be rendered for the disclosure.
     *
     * @return boolean true if icons should be rendered, false if not
     */
    public boolean isRenderIcon() {
        return renderIcon;
    }

    /**
     * @see Disclosure#isRenderIcon()
     */
    public void setRenderIcon(boolean renderIcon) {
        this.renderIcon = renderIcon;
    }

    /**
     * Gives the speed for the open/close animation, a smaller int will result
     * in a faster animation
     *
     * @return animation speed
     */
    @BeanTagAttribute(name = "animationSpeed")
    public int getAnimationSpeed() {
        return this.animationSpeed;
    }

    /**
     * Setter for the open/close animation speed
     *
     * @param animationSpeed
     */
    public void setAnimationSpeed(int animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    /**
     * Indicates whether the group should be initially open
     *
     * @return true if group should be initially open, false if it
     *         should be closed
     */
    @BeanTagAttribute(name = "defaultOpen")
    public boolean isDefaultOpen() {
        return this.defaultOpen;
    }

    /**
     * Setter for the default open indicator
     *
     * @param defaultOpen
     */
    public void setDefaultOpen(boolean defaultOpen) {
        this.defaultOpen = defaultOpen;
    }

    /**
     * When true, the group content will be retrieved when the disclosure is opened
     *
     * <p>This only works if by default, the disclosure is closed.</p>
     *
     * @return true if use ajax retrieval when disclosure opens, false otherwise
     */
    public boolean isAjaxRetrievalWhenOpened() {
        return ajaxRetrievalWhenOpened;
    }

    /**
     * Set ajaxRetrievalWhenOpened
     *
     * @param ajaxRetrievalWhenOpened
     */
    public void setAjaxRetrievalWhenOpened(boolean ajaxRetrievalWhenOpened) {
        this.ajaxRetrievalWhenOpened = ajaxRetrievalWhenOpened;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#copyProperties(Object)
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        Disclosure disclosureCopy = (Disclosure) component;

        disclosureCopy.setCollapsedIconClass(this.collapsedIconClass);
        disclosureCopy.setExpandedIconClass(this.expandedIconClass);
        disclosureCopy.setRenderIcon(this.renderIcon);
        disclosureCopy.setAnimationSpeed(this.animationSpeed);
        disclosureCopy.setDefaultOpen(this.defaultOpen);
        disclosureCopy.setAjaxRetrievalWhenOpened(this.ajaxRetrievalWhenOpened);
    }
}
