/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mocks;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.feedback.web.FeedbackForm;
import org.kuali.rice.kew.mail.EmailContent;
import org.kuali.rice.kew.mail.service.impl.StyleableEmailContentServiceImpl;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;


/**
 * This is a class used to substitute for a StyleableEmailContentServiceImpl class
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class MockStyleableEmailContentServiceImpl extends StyleableEmailContentServiceImpl implements MockStyleableEmailContentService {

    private boolean wasAccessed = false;

    @Override
    public EmailContent generateImmediateReminder(Person user, ActionItem actionItem, DocumentType documentType) {
        wasAccessed = true;
        return super.generateImmediateReminder(user, actionItem, documentType);
    }

    @Override
    public EmailContent generateDailyReminder(Person user, Collection<ActionItem> actionItems) {
        wasAccessed = true;
        return super.generateDailyReminder(user, actionItems);
    }

    @Override
    public EmailContent generateWeeklyReminder(Person user, Collection<ActionItem> actionItems) {
        wasAccessed = true;
        return super.generateWeeklyReminder(user, actionItems);
    }

    @Override
    public EmailContent generateFeedback(FeedbackForm form) {
        wasAccessed = true;
        return super.generateFeedback(form);
    }

    /**
     * This overridden method is used in case the action item has an null route header attached
     *
     * @see org.kuali.rice.kew.mail.service.impl.StyleableEmailContentServiceImpl#getRouteHeader(org.kuali.rice.kew.actionitem.ActionItem)
     */
    @Override
    public DocumentRouteHeaderValue getRouteHeader(ActionItem actionItem) {
        if (actionItem.getRouteHeader() != null) {
            return super.getRouteHeader(actionItem);
        }
        DocumentRouteHeaderValue routeHeader = new DocumentRouteHeaderValue();
        routeHeader.setDocRouteStatus(KEWConstants.ROUTE_HEADER_ENROUTE_CD);
        routeHeader.setCreateDate(new Timestamp(new Date().getTime()));
        return routeHeader;
    }

    /**
     * This method returns whether this service is being used
     */
    public boolean wasServiceAccessed() {
        return this.wasAccessed;
    }

    /**
     * This method returns whether this service is being used
     */
    public void resetServiceAccessed() {
        this.wasAccessed = false;
    }

}
