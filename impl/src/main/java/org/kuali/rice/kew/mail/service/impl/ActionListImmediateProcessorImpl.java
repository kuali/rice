/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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
package org.kuali.rice.kew.mail.service.impl;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.mail.service.ActionListEmailService;
import org.kuali.rice.kew.mail.service.ActionListImmediateEmailReminderService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;


/**
 * Implementation of the {@link ActionListImmediateEmailReminderService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionListImmediateProcessorImpl implements ActionListImmediateEmailReminderService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionListImmediateProcessorImpl.class);

	public void sendReminder(ActionItem actionItem, boolean doNotSendApproveNotificationEmails) {
		if (actionItem != null) {
		    if (! actionItem.getActionRequestCd().equals(KEWConstants.ACTION_REQUEST_APPROVE_REQ) ||
            			! doNotSendApproveNotificationEmails) {
            	if (LOG.isDebugEnabled()) {
            		LOG.debug("sending immediate reminder to " + actionItem.getPrincipalId());
            	}
            	getActionListEmailService().sendImmediateReminder(actionItem.getPerson(), actionItem);
            }
        }
	}

	private ActionListEmailService getActionListEmailService(){
	    return (ActionListEmailService) KEWServiceLocator.getService(KEWServiceLocator.ACTION_LIST_EMAIL_SERVICE);
	}
}
