/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.mail;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;

/**
 * Implementation of the {@link ActionListImmediateEmailReminderService}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionListImmediateProcessorImpl implements ActionListImmediateEmailReminderService {
	
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionListImmediateProcessorImpl.class);
    
	public void sendReminder(ActionItem actionItem, boolean doNotSendApproveNotificationEmails) {
		if (actionItem != null) {
            if (! actionItem.getActionRequestCd().equals(EdenConstants.ACTION_REQUEST_APPROVE_REQ) || 
            			! doNotSendApproveNotificationEmails) {
                try {
                	if (LOG.isDebugEnabled()) {
                		LOG.debug("sending immediate reminder to " + actionItem.getUser().getAuthenticationUserId().getAuthenticationId());
                	}
					getActionListEmailService().sendImmediateReminder(actionItem.getUser(), actionItem);
				} catch (EdenUserNotFoundException e) {
					throw new WorkflowRuntimeException(e);
				}    
            }
        }
	}
    
	private ActionListEmailService getActionListEmailService(){
	    return (ActionListEmailService) KEWServiceLocator.getService(KEWServiceLocator.ACTION_LIST_EMAIL_SERVICE);
	}
}