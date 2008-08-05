/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
// Created on Jan 18, 2007

package edu.iu.uis.eden.mail;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.util.KEWConstants;

import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * ActionListEmailService implementation whose content is configurable/parameterizable
 * via a pluggable EmailContentService
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class CustomizableActionListEmailServiceImpl extends ActionListEmailServiceImpl {
    private static final Logger LOG = Logger.getLogger(CustomizableActionListEmailServiceImpl.class);

    private EmailContentService contentService;

    // ---- Spring property

    public void setEmailContentGenerator(EmailContentService contentService) {
        this.contentService = contentService;
    }
    
    protected EmailContentService getEmailContentGenerator() {
        return contentService;
    }

    public void sendImmediateReminder(WorkflowUser user, ActionItem actionItem) {
        if (!sendActionListEmailNotification()) {
            LOG.debug("not sending immediate reminder");
            return;
        }
        // since this is a message for a single document, we can customize the from
        // line based on DocumentType
        DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(actionItem.getRouteHeaderId());
        EmailContent content = getEmailContentGenerator().generateImmediateReminder(user, actionItem, document.getDocumentType());
        sendEmail(user, new EmailSubject(content.getSubject()),
                        new EmailBody(content.getBody()), document.getDocumentType());
    }

    @Override
    protected void sendPeriodicReminder(WorkflowUser user, Collection actionItems, String emailSetting) {
        actionItems = filterActionItemsToNotify(user, actionItems);
        // if there are no action items after being filtered, there's no
        // reason to send the email
        if (actionItems.isEmpty()) {
            return;
        }
        EmailContent content;
        if (KEWConstants.EMAIL_RMNDR_DAY_VAL.equals(emailSetting)) {
            content = getEmailContentGenerator().generateDailyReminder(user, actionItems);
        } else if (KEWConstants.EMAIL_RMNDR_WEEK_VAL.equals(emailSetting)) {
            content = getEmailContentGenerator().generateWeeklyReminder(user, actionItems);
        } else {
            // else...refactor this...
            throw new RuntimeException("invalid email setting. this code needs refactoring");
        }
        sendEmail(user, new EmailSubject(content.getSubject()), new EmailBody(content.getBody()));
    }

}