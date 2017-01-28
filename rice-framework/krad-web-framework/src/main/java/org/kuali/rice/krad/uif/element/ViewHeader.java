/**
 * Copyright 2005-2017 The Kuali Foundation
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
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.View;

/**
 * The ViewHeader component represents the header for the view.
 *
 * <p>This header has support for a "Unified" header in
 * which both the page title and view title appear in its content.  An "area title" and "metadata" can also be set
 * to provide context. </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "viewHeader", parent = "Uif-ViewHeader")
public class ViewHeader extends Header {
    private static final long serialVersionUID = -974112303431464467L;

    private Message areaTitleMessage;
    private Message supportTitleMessage;
    private Message metadataMessage;
    private boolean sticky;

    /**
     * Sets the supportTitleMessage if one has not been set and unified header is being used, based on the value
     * of page title
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        View view = ViewLifecycle.getView();
        if (supportTitleMessage != null &&
                view.getCurrentPage() != null && view.getCurrentPage().getHeader() != null &&
                view.isUnifiedHeader()) {
            Header pageHeader = view.getCurrentPage().getHeader();

            // hide page header text
            pageHeader.addStyleClass(CssConstants.Classes.HIDE_HEADER_TEXT_STYLE_CLASS);

            Message pageHeaderMessage = pageHeader.getRichHeaderMessage();

            if (pageHeaderMessage != null && StringUtils.isBlank(supportTitleMessage.getMessageText())) {
                pageHeaderMessage.addStyleClass(CssConstants.Classes.SUPPORT_TITLE_STYLE_CLASS);

                // use page header rich content
                supportTitleMessage = pageHeaderMessage;
            } else if (StringUtils.isNotBlank(pageHeader.getHeaderText()) && StringUtils.isBlank(
                    supportTitleMessage.getMessageText())) {
                // use set page header text
                supportTitleMessage.setMessageText(pageHeader.getHeaderText().trim());
            }
        }

        // Add content container classes
        this.getCssClasses().addAll(0, view.getContentContainerCssClasses());

        // Add sticky data attribute marker
        if (this.isSticky()) {
            this.addDataAttribute(UifConstants.DataAttributes.STICKY, "true");
        }
    }

    /**
     * Represents the area in which this view and page exist (conceptially in the site);
     * this title appears above the view title.
     *
     * @return the areaTitle text
     */
    @BeanTagAttribute
    public String getAreaTitleText() {
        return areaTitleMessage.getMessageText();
    }

    /**
     * Set the areaTitle
     *
     * @param areaTitle
     */
    public void setAreaTitleText(String areaTitle) {
        areaTitleMessage.setMessageText(areaTitle);
    }

    /**
     * Message object backing areaTitleText
     *
     * @return the areaTitle Message object
     */
    @BeanTagAttribute
    public Message getAreaTitleMessage() {
        return areaTitleMessage;
    }

    /**
     * Set the areaTitleMessage object
     *
     * @param areaTitleMessage
     */
    public void setAreaTitleMessage(Message areaTitleMessage) {
        this.areaTitleMessage = areaTitleMessage;
    }

    /**
     * The supportTitleText represents the sub-area of this view that explains what the page is displaying; this is
     * the text used in supportTitleMessage
     *
     * <p>This title appears below the view title and will be automatically set to the page title if not set.</p>
     *
     * @return the supportTitle text
     */
    @BeanTagAttribute
    public String getSupportTitleText() {
        return supportTitleMessage.getMessageText();
    }

    /**
     * Set the supportTitleText
     *
     * @param supportTitle
     */
    public void setSupportTitleText(String supportTitle) {
        supportTitleMessage.setMessageText(supportTitle);
    }

    /**
     * The supportTitleMessage represents the sub-area of this view that supports what the page is displaying, this is
     * the Message component
     *
     * <p>This title appears below the view title and will be automatically set to the page title if not messageText is
     * not set.</p>
     *
     * @return the supportTitle Message object
     */
    @BeanTagAttribute
    public Message getSupportTitleMessage() {
        return supportTitleMessage;
    }

    /**
     * Set the supportTitleMessage
     *
     * @param supportTitleMessage
     */
    public void setSupportTitleMessage(Message supportTitleMessage) {
        this.supportTitleMessage = supportTitleMessage;
    }

    /**
     * The metadataText represents any relevant metadata about the view (last saved, etc).
     * This message will appear in the bottom right of the ViewHeader container.
     *
     * @return the metadataText string
     */
    @BeanTagAttribute
    public String getMetadataText() {
        return metadataMessage.getMessageText();
    }

    /**
     * Set the metadataText
     *
     * @param metadataText
     */
    public void setMetadataText(String metadataText) {
        metadataMessage.setMessageText(metadataText);
    }

    /**
     * The metadataMessage represents any relevant metadata about the view (last saved, etc).
     * This message will appear in the bottom right of the ViewHeader container.
     *
     * @return the metadataMessage object
     */
    @BeanTagAttribute
    public Message getMetadataMessage() {
        return metadataMessage;
    }

    /**
     * Set the metadataMessage
     *
     * @param metadataMessage
     */
    public void setMetadataMessage(Message metadataMessage) {
        this.metadataMessage = metadataMessage;
    }

    /**
     * If true, this ViewHeader will be sticky (fixed to top of window, stays at top during scrolling)
     *
     * @return true if sticky, false otherwise
     */
    @BeanTagAttribute
    public boolean isSticky() {
        return sticky;
    }

    /**
     * Set to true to make this ViewHeader sticky
     *
     * @param sticky
     */
    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }
}
