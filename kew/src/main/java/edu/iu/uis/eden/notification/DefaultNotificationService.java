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
package edu.iu.uis.eden.notification;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.ComparatorUtils;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionlist.dao.ActionItemComparator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.mail.ActionListImmediateEmailReminderService;
import edu.iu.uis.eden.messaging.MessageServiceNames;
import edu.iu.uis.eden.preferences.Preferences;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;

/**
 * The default implementation of the NotificationService.
 *   
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DefaultNotificationService implements NotificationService {

	protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());
	
	private static final Comparator notificationPriorityComparator = ComparatorUtils.reversedComparator(new ActionItemComparator());
	
	/**
	 * Queues up immediate email processors for ActionItem notification.  Prioritizes the list of
	 * Action Items passed in and attempts to not send out multiple emails to the same user.
	 */
	public void notify(List actionItems) {
		// sort the list of action items using the same comparator as the Action List
		Collections.sort(actionItems, notificationPriorityComparator);
		Set sentNotifications = new HashSet();
		for (Iterator iterator = actionItems.iterator(); iterator.hasNext();) {
			ActionItem actionItem = (ActionItem) iterator.next();
			if (!sentNotifications.contains(actionItem.getWorkflowId()) && shouldNotify(actionItem)) {
				sentNotifications.add(actionItem.getWorkflowId());
				ActionListImmediateEmailReminderService immediateEmailService = MessageServiceNames.getImmediateEmailService();
				immediateEmailService.sendReminder(actionItem, RouteContext.getCurrentRouteContext().isDoNotSendApproveNotificationEmails());
			}
		}
	}
	
	protected boolean shouldNotify(ActionItem actionItem) {
		try {
			WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(actionItem.getWorkflowId()));
			Preferences preferences = KEWServiceLocator.getPreferencesService().getPreferences(user);
			boolean sendEmail = false;
			if (EdenConstants.EMAIL_RMNDR_IMMEDIATE.equals(preferences.getEmailNotification())) {
				if (EdenConstants.DELEGATION_PRIMARY.equals(actionItem.getDelegationType())) {
					sendEmail = EdenConstants.PREFERENCES_YES_VAL.equals(preferences.getNotifyPrimaryDelegation());
				} else if (EdenConstants.DELEGATION_SECONDARY.equals(actionItem.getDelegationType())) {
					sendEmail = EdenConstants.PREFERENCES_YES_VAL.equals(preferences.getNotifySecondaryDelegation());
				} else {
					sendEmail = true;
				}
			}
			// don't send notification if this action item came from a SAVE action and the NOTIFY_ON_SAVE policy is not set
			if (sendEmail && isItemOriginatingFromSave(actionItem) && !shouldNotifyOnSave(actionItem)) {
				sendEmail = false;
			}
			return sendEmail;
		} catch (EdenUserNotFoundException e) {
			throw new WorkflowRuntimeException("Error loading user with workflow id " + actionItem.getWorkflowId() + " for notification.", e);
		}
	}

	/**
	 * Returns true if the ActionItem doesn't represent a request generated from a "SAVE" action or, if it does,
	 * returns true if the document type policy
	 */
	protected boolean isItemOriginatingFromSave(ActionItem actionItem) {
		return actionItem.getResponsibilityId() != null && actionItem.getResponsibilityId().equals(EdenConstants.SAVED_REQUEST_RESPONSIBILITY_ID);
	}
	
	protected boolean shouldNotifyOnSave(ActionItem actionItem) {
		DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(actionItem.getRouteHeaderId());
		DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findById(document.getDocumentTypeId());
		return documentType.getNotifyOnSavePolicy().getPolicyValue().booleanValue();
	}
	
}
