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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.lifecycle.LifecycleEventListener;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

import java.util.HashSet;
import java.util.Set;

/**
 * ValidationMessages for logic and options specific to pages.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "pageValidationMessages", parent = "Uif-PageValidationMessages")
public class PageValidationMessages extends GroupValidationMessages implements LifecycleEventListener {
    private static final long serialVersionUID = 6387432156428507958L;

    private boolean showPageSummaryHeader;

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateMessages(View view, Object model, Component parent) {
        super.generateMessages(view, model, parent);

        ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();
        viewLifecycle.registerLifecycleCompleteListener(view, this);
    }

    /**
     * Overridding to prevent the initial writing of data attributes until the view has been processed and
     * we collection unmatched messages (through the lifecycle event).
     *
     * {@inheritDoc}
     */
    @Override
    protected void addValidationMessageDataAttributes(Component parent) {
        // do nothing
    }

    /**
     * Check for message keys that are not matched anywhere on the page, these unmatched messages must still be
     * displayed at the page level.
     *
     * {@inheritDoc}
     */
    @Override
    public void processEvent(ViewLifecycle.LifecycleEvent lifecycleEvent, View view, Object model,
            LifecycleElement eventElement) {
        View eventComponent = (View) eventElement;
        PageGroup currentPage = eventComponent.getCurrentPage();

        Set<String> allPossibleKeys = new HashSet<String>();

        Set<String> renderedPropertyPaths = ViewLifecycle.getViewPostMetadata().getAllRenderedPropertyPaths();
        if (renderedPropertyPaths != null) {
            allPossibleKeys.addAll(renderedPropertyPaths);
        }

        addNestedGroupKeys(allPossibleKeys, currentPage);

        if (getAdditionalKeysToMatch() != null) {
            allPossibleKeys.addAll(getAdditionalKeysToMatch());
        }

        if (StringUtils.isNotBlank(currentPage.getId())) {
            allPossibleKeys.add(currentPage.getId());
        }

        MessageMap messageMap = GlobalVariables.getMessageMap();

        Set<String> messageKeys = new HashSet<String>();

        messageKeys.addAll(messageMap.getAllPropertiesWithErrors());
        messageKeys.addAll(messageMap.getAllPropertiesWithWarnings());
        messageKeys.addAll(messageMap.getAllPropertiesWithInfo());

        messageKeys.removeAll(allPossibleKeys);

        for (String key : messageKeys) {
            getErrors().addAll(getMessages(view, key, messageMap.getErrorMessagesForProperty(key, true)));
            getWarnings().addAll(getMessages(view, key, messageMap.getWarningMessagesForProperty(key, true)));
            getInfos().addAll(getMessages(view, key, messageMap.getInfoMessagesForProperty(key, true)));
        }

        super.addValidationMessageDataAttributes(currentPage);
    }

    /**
     * If true, shows the page summary header (message count header message in the message block).  Otherwise, this
     * header is not rendered.
     *
     * @return true if the header will show, false otherwise
     */
    @BeanTagAttribute
    public boolean isShowPageSummaryHeader() {
        return showPageSummaryHeader;
    }

    /**
     * Set the page summary header to show or not show.
     *
     * @param showPageSummaryHeader
     */
    public void setShowPageSummaryHeader(boolean showPageSummaryHeader) {
        this.showPageSummaryHeader = showPageSummaryHeader;
    }
}
