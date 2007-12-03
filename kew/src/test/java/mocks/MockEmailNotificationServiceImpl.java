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
package mocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.mail.ActionListEmailServiceImpl;
import edu.iu.uis.eden.mail.EmailBody;
import edu.iu.uis.eden.mail.EmailSubject;
import edu.iu.uis.eden.mail.EmailTo;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.WorkflowUser;

public class MockEmailNotificationServiceImpl extends ActionListEmailServiceImpl implements MockEmailNotificationService {

    private static Map immediateReminders = new HashMap();
    public static boolean SEND_DAILY_REMINDER_CALLED = false;
    public static boolean SEND_WEEKLY_REMINDER_CALLED = false;

    public void sendImmediateReminder(WorkflowUser user, ActionItem actionItem) {
        List actionItemsSentUser = (List)immediateReminders.get(user.getWorkflowId());
        if (actionItemsSentUser == null) {
            actionItemsSentUser = new ArrayList();
            immediateReminders.put(user.getWorkflowId(), actionItemsSentUser);
        }
        actionItemsSentUser.add(actionItem);
    }



    public String getApplicationEmailAddress() {
		throw new UnsupportedOperationException("Not currently supported in test mode.");
	}



	public String getDocumentTypeEmailAddress(DocumentType documentType) {
		throw new UnsupportedOperationException("Not currently supported in test mode.");
	}

	public void sendDailyReminder() {
		SEND_DAILY_REMINDER_CALLED = true;
    }

    public void sendWeeklyReminder() {
    	SEND_WEEKLY_REMINDER_CALLED = true;
    }

    public void sendEmail(EmailTo to, EmailSubject subject, EmailBody body) {
    }

    public int emailsSent(String networkId, Long documentId, String actionRequestCd) throws EdenUserNotFoundException {
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(networkId));
        List actionItemsSentUser = (List)immediateReminders.get(user.getWorkflowId());
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

}