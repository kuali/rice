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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionlist.ActionListService;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.preferences.Preferences;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.useroptions.UserOptions;
import edu.iu.uis.eden.useroptions.UserOptionsService;
import edu.iu.uis.eden.util.Utilities;

/**
 * ActionListEmailService implementation whose content is configurable/parameterizable
 * via a pluggable EmailContentService
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class CustomizableActionListEmailServiceImpl extends ActionListEmailServiceImpl {
    private static final Logger LOG = Logger.getLogger(CustomizableActionListEmailServiceImpl.class);

    private EmailContentService contentService;
    private String deploymentEnvironment;

    // ---- Spring property

    public void setEmailContentGenerator(EmailContentService contentService) {
        this.contentService = contentService;
    }

    public void sendImmediateReminder(WorkflowUser user, ActionItem actionItem) {
        if (!sendActionListEmailNotification()) {
            LOG.debug("not sending immediate reminder");
            return;
        }
        // since this is a message for a single document, we can customize the from
        // line based on DocumentType
        DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(actionItem.getRouteHeaderId());
        EmailContent content = contentService.generateImmediateReminder(user, actionItem, document.getDocumentType());
        sendEmail(user, new EmailSubject(content.getSubject()),
                        new EmailBody(content.getBody()), document.getDocumentType());
    }

    public void sendDailyReminder() {
        if (!sendActionListEmailNotification()) {
            LOG.debug("not sending daily reminder");
            return;
        }
        Collection users = getUsersWithEmailSetting(EdenConstants.EMAIL_RMNDR_DAY_VAL);
        for (Iterator userIter = users.iterator(); userIter.hasNext();) {
            WorkflowUser user = (WorkflowUser) userIter.next();
            try {
                Collection actionItems = getActionListService()
                        .getActionList(user, null);
                if (actionItems != null && actionItems.size() > 0) {
                    sendReminder(user, actionItems,
                            EdenConstants.EMAIL_RMNDR_DAY_VAL);
                }
            } catch (Exception e) {
                LOG.error(
                        "Error sending daily action list reminder to user: "
                                + user.getEmailAddress(), e);
            }
        }
        LOG.debug("Daily action list emails sent successful");
    }


    public void sendWeeklyReminder() {
        if (!sendActionListEmailNotification()) {
            LOG.debug("not sending weekly reminder");
            return;
        }
        Collection users = getUsersWithEmailSetting(EdenConstants.EMAIL_RMNDR_WEEK_VAL);
        for (Iterator userIter = users.iterator(); userIter.hasNext();) {
            WorkflowUser user = (WorkflowUser) userIter.next();
            try {
                Collection actionItems = getActionListService()
                        .getActionList(user, null);
                if (actionItems != null && actionItems.size() > 0) {
                    sendReminder(user, actionItems,
                            EdenConstants.EMAIL_RMNDR_WEEK_VAL);
                }
            } catch (Exception e) {
                LOG.error(
                        "Error sending weekly action list reminder to user: "
                                + user.getEmailAddress(), e);
            }
        }
        LOG.debug("Weekly action list emails sent successful");
    }

    // ----

    private void sendReminder(WorkflowUser user, Collection<ActionItem> actionItems, String emailSetting) {
        actionItems = filterActionItemsToNotify(user, actionItems);
        // if there are no action items after being filtered, there's no
        // reason to send the email
        if (actionItems.isEmpty()) {
            return;
        }
        EmailContent content;
        if (EdenConstants.EMAIL_RMNDR_DAY_VAL.equals(emailSetting)) {
            content = contentService.generateDailyReminder(user, actionItems);
        } else if (EdenConstants.EMAIL_RMNDR_WEEK_VAL.equals(emailSetting)) {
            content = contentService.generateWeeklyReminder(user, actionItems);
        } else {
            // else...refactor this...
            throw new RuntimeException("invalid email setting. this code needs refactoring");
        }
        sendEmail(user, new EmailSubject(content.getSubject()), new EmailBody(content.getBody()));
    }

    /**
     * Returns a filtered Collection of {@link ActionItem}s which are
     * filtered according to the user's preferences.  If they have opted
     * not to recieve secondary or primary delegation emails then they
     * will not be included.
     */
    private Collection filterActionItemsToNotify(WorkflowUser user, Collection<ActionItem> actionItems) {
        List filteredItems = new ArrayList();
        Preferences preferences = KEWServiceLocator.getPreferencesService().getPreferences(user);
        for (Iterator iterator = actionItems.iterator(); iterator.hasNext();) {
            ActionItem actionItem = (ActionItem) iterator.next();
            if (!actionItem.getWorkflowId().equals(user.getWorkflowId())) {
                LOG.warn("Encountered an ActionItem with an incorrect workflow ID.  Was " + actionItem.getWorkflowId() +
                        " but expected " + user.getWorkflowId());
                continue;
            }
            boolean includeItem = true;
            if (EdenConstants.DELEGATION_PRIMARY.equals(actionItem.getDelegationType())) {
                includeItem = EdenConstants.PREFERENCES_YES_VAL.equals(preferences.getNotifyPrimaryDelegation());
            } else if (EdenConstants.DELEGATION_SECONDARY.equals(actionItem.getDelegationType())) {
                includeItem = EdenConstants.PREFERENCES_YES_VAL.equals(preferences.getNotifySecondaryDelegation());
            }
            if (includeItem) {
                filteredItems.add(actionItem);
            }
        }
        return filteredItems;
    }

    private boolean sendActionListEmailNotification() {
        return EdenConstants.ACTION_LIST_SEND_EMAIL_NOTIFICATION_VALUE
                .equals(Utilities
                        .getApplicationConstant(EdenConstants.ACTION_LIST_SEND_EMAIL_NOTIFICATION_KEY));
    }

    private void sendEmail(WorkflowUser user, EmailSubject subject, EmailBody body) {
        sendEmail(user, subject, body, null);
    }

    private void sendEmail(WorkflowUser user, EmailSubject subject, EmailBody body, DocumentType documentType) {
        try {
            if (isProduction()) {
                KEWServiceLocator.getEmailService().sendEmail(
                        getEmailFrom(documentType),
                        new EmailTo(user.getEmailAddress()), subject, body,
                        false);
            } else {
                KEWServiceLocator
                        .getEmailService()
                        .sendEmail(
                                getEmailFrom(documentType),
                                new EmailTo(
                                        Utilities
                                                .getApplicationConstant(EdenConstants.ACTIONLIST_EMAIL_TEST_ADDRESS)),
                                subject, body, false);
            }
        } catch (Exception e) {
            LOG.error("Error sending email.", e);
        }
    }

    private EmailFrom getEmailFrom(DocumentType documentType) {
        return new EmailFrom(contentService.getDocumentTypeEmailAddress(documentType));
    }

    private List getUsersWithEmailSetting(String setting) {
        List users = new ArrayList();
        Collection userOptions = getUserOptionsService().findByOptionValue(
                EdenConstants.EMAIL_RMNDR_KEY, setting);
        for (Iterator iter = userOptions.iterator(); iter.hasNext();) {
            String workflowId = ((UserOptions) iter.next()).getWorkflowId();
            try {
                users.add(getUserService().getWorkflowUser(
                        new WorkflowUserId(workflowId)));
            } catch (Exception e) {
                LOG.error("error retrieving workflow user with ID: "
                        + workflowId);
            }
        }
        return users;
    }

    public UserService getUserService() {
        return (UserService) KEWServiceLocator.getUserService();
    }

    private UserOptionsService getUserOptionsService() {
        return (UserOptionsService) KEWServiceLocator
                .getUserOptionsService();
    }

    private ActionListService getActionListService() {
        return (ActionListService) KEWServiceLocator.getActionListService();
    }

    private boolean isProduction() {
        return getDeploymentEnvironment().equals(
                EdenConstants.PROD_DEPLOYMENT_CODE);
    }

    public String getDeploymentEnvironment() {
        return deploymentEnvironment;
    }

    public void setDeploymentEnvironment(String deploymentEnvironment) {
        this.deploymentEnvironment = deploymentEnvironment;
    }
}