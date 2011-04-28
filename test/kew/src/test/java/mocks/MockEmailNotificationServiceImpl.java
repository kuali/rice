/*
 * Copyright 2006-2011 The Kuali Foundation
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
package mocks;

import org.apache.log4j.Logger;
import org.kuali.rice.core.mail.Mailer;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.mail.service.EmailContentService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimPrincipal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MockEmailNotificationServiceImpl /*extends CustomizableActionListEmailServiceImpl*/ implements MockEmailNotificationService {
    private static final Logger LOG = Logger.getLogger(MockEmailNotificationServiceImpl.class);

    private static Map<String,List> immediateReminders = new HashMap<String,List>();
    private static Map<String,Integer> aggregateReminderCount = new HashMap<String,Integer>();
    public static boolean SEND_DAILY_REMINDER_CALLED = false;
    public static boolean SEND_WEEKLY_REMINDER_CALLED = false;

    private EmailContentService contentService;
    private String deploymentEnvironment;

	private Mailer mailer;

    /**
     * Resets the reminder counts
     */
    public void resetReminderCounts() {
        aggregateReminderCount.clear();
        immediateReminders.clear();
    }

    /**
     * This overridden method will perform the standard operations from org.kuali.rice.kew.mail.ActionListEmailServiceImpl but will also keep track of action
     * items processed
     */
    @Override
    public void sendImmediateReminder(Person user, ActionItem actionItem) {
        //super.sendImmediateReminder(user, actionItem);
        List actionItemsSentUser = (List)immediateReminders.get(user.getPrincipalId());
        if (actionItemsSentUser == null) {
            actionItemsSentUser = new ArrayList();
            immediateReminders.put(user.getPrincipalId(), actionItemsSentUser);
        }
        actionItemsSentUser.add(actionItem);
    }

    /**
     * This overridden method returns a value of true always
     */
    //@Override
    protected boolean sendActionListEmailNotification() {

        return true;
    }

    @Override
	public void sendDailyReminder() {
        resetStyleService();
	    //super.sendDailyReminder();
		SEND_DAILY_REMINDER_CALLED = true;
    }

    @Override
    public void sendWeeklyReminder() {
        resetStyleService();
        //super.sendWeeklyReminder();
    	SEND_WEEKLY_REMINDER_CALLED = true;
    }

    @Override
    public void scheduleBatchEmailReminders() throws Exception {
        //do nothing
    }

    //@Override
    protected void sendPeriodicReminder(Person user, Collection<ActionItem> actionItems, String emailSetting) {
        //super.sendPeriodicReminder(user, actionItems, emailSetting);
        if (!aggregateReminderCount.containsKey(emailSetting)) {
            aggregateReminderCount.put(emailSetting, actionItems.size());
        } else {
            aggregateReminderCount.put(emailSetting, aggregateReminderCount.get(emailSetting) + actionItems.size());
        }
    }

    public Integer getTotalPeriodicRemindersSent(String emailReminderConstant) {
        Integer returnVal = aggregateReminderCount.get(emailReminderConstant);
        if (returnVal == null) {
            returnVal = Integer.valueOf(0);
        }
        return returnVal;
    }

    public Integer getTotalPeriodicRemindersSent() {
        int total = 0;
        for (Map.Entry<String, Integer> mapEntry : aggregateReminderCount.entrySet()) {
            Integer value = mapEntry.getValue();
            total += (value == null) ? 0 : value.intValue();
        }
        return Integer.valueOf(total);
    }

    public boolean wasStyleServiceAccessed() {
        return getEmailContentGenerator().wasServiceAccessed();
    }

    private void resetStyleService() {
        getEmailContentGenerator().resetServiceAccessed();
    }

    public int immediateReminderEmailsSent(String networkId, Long documentId, String actionRequestCd) {
    	KimPrincipal principal = KimApiServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(networkId);
        List actionItemsSentUser = immediateReminders.get(principal.getPrincipalId());
        if (actionItemsSentUser == null) {
            return 0;
        }
        int emailsSent = 0;
        for (Iterator iter = actionItemsSentUser.iterator(); iter.hasNext();) {
            ActionItem actionItem = (ActionItem) iter.next();
            if (actionItem.getRouteHeaderId().equals(documentId) && actionItem.getActionRequestCd().equals(actionRequestCd)) {
                emailsSent++;
            }
        }
        return emailsSent;
    }

    public void setEmailContentGenerator(EmailContentService contentService) {
        this.contentService = contentService;
    }

    protected MockStyleableEmailContentService getEmailContentGenerator() {
        return (MockStyleableEmailContentService) contentService;
    }

	public void setMailer(Mailer mailer){
		this.mailer = mailer;
	}

	public void setDeploymentEnvironment(String deploymentEnvironment) {
		this.deploymentEnvironment = deploymentEnvironment;
	}
}
