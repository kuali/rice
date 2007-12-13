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

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.core.Core;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionlist.ActionListService;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.clientapp.IDocHandler;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.plugin.attributes.CustomEmailAttribute;
import edu.iu.uis.eden.preferences.Preferences;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.server.BeanConverter;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.useroptions.UserOptions;
import edu.iu.uis.eden.useroptions.UserOptionsService;
import edu.iu.uis.eden.util.Utilities;

/**
 * ActionListeEmailService which generates messages whose body and subject can be customized via
 * KEW configuration parameters, 'immediate.reminder.email.message' and 'immediate.reminder.email.subject'.
 * The immediate reminder email message key should specify a MessageFormat string.  See code for the parameters
 * to this MessageFormat.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionListEmailServiceImpl implements ActionListEmailService {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(ActionListEmailServiceImpl.class);

	private static final String DEFAULT_EMAIL_FROM_ADDRESS = "workflow@indiana.edu";

	private static final String ACTION_LIST_REMINDER = "OneStart Action List Reminder";

    private static final String IMMEDIATE_REMINDER_EMAIL_MESSAGE_KEY = "immediate.reminder.email.message";

    private static final String IMMEDIATE_REMINDER_EMAIL_SUBJECT_KEY = "immediate.reminder.email.subject";

    private static final String DAILY_TRIGGER_NAME = "Daily Email Trigger";
    private static final String DAILY_JOB_NAME = "Daily Email";
    private static final String WEEKLY_TRIGGER_NAME = "Weekly Email Trigger";
    private static final String WEEKLY_JOB_NAME = "Weekly Email";

	private String deploymentEnvironment;

	public String getDocumentTypeEmailAddress(DocumentType documentType) {
		String fromAddress = (documentType == null ? null : documentType
				.getNotificationFromAddress());
		if (Utilities.isEmpty(fromAddress)) {
			fromAddress = getApplicationEmailAddress();
		}
		return fromAddress;
	}

	public String getApplicationEmailAddress() {
		// first check the configured value
		String fromAddress = Utilities
				.getApplicationConstant(EdenConstants.EMAIL_REMINDER_FROM_ADDRESS_KEY);
		// if there's no value configured, use the default
		if (Utilities.isEmpty(fromAddress)) {
			fromAddress = DEFAULT_EMAIL_FROM_ADDRESS;
		}
		return fromAddress;
	}

	private String getHelpLink() {
		return getHelpLink(null);
	}

	private String getHelpLink(DocumentType documentType) {
		return "For additional help, email " + "<mailto:"
				+ getDocumentTypeEmailAddress(documentType) + ">";
	}

	public EmailSubject getEmailSubject() {
        String subject = Core.getCurrentContextConfig().getProperty(IMMEDIATE_REMINDER_EMAIL_SUBJECT_KEY);
        if (subject == null) {
            subject = ACTION_LIST_REMINDER;
	}
		return new EmailSubject(subject);
	}

	public EmailSubject getEmailSubject(String customSubject) {
        String subject = Core.getCurrentContextConfig().getProperty(IMMEDIATE_REMINDER_EMAIL_SUBJECT_KEY);
        if (subject == null) {
            subject = ACTION_LIST_REMINDER;
	}
		return new EmailSubject(subject + " " + customSubject);
	}

	private EmailFrom getEmailFrom(DocumentType documentType) {
		return new EmailFrom(getDocumentTypeEmailAddress(documentType));
	}

	private void sendEmail(WorkflowUser user, EmailSubject subject,
			EmailBody body) {
		sendEmail(user, subject, body, null);
	}

	private void sendEmail(WorkflowUser user, EmailSubject subject,
			EmailBody body, DocumentType documentType) {
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

	public void sendImmediateReminder(WorkflowUser user, ActionItem actionItem) {
        boolean shouldSendActionListEmailNotification = sendActionListEmailNotification();
		if (shouldSendActionListEmailNotification) {
            LOG.debug("sending immediate reminder");
			DocumentRouteHeaderValue document = KEWServiceLocator
					.getRouteHeaderService().getRouteHeader(
							actionItem.getRouteHeaderId());
			StringBuffer emailBody = new StringBuffer(
					buildImmediateReminderBody(user, actionItem, document
							.getDocumentType()));
			StringBuffer emailSubject = new StringBuffer();
			try {
				CustomEmailAttribute customEmailAttribute = actionItem
						.getRouteHeader().getCustomEmailAttribute();
				if (customEmailAttribute != null) {
					RouteHeaderVO routeHeaderVO = BeanConverter
							.convertRouteHeader(actionItem.getRouteHeader(),
									user);
					ActionRequestValue actionRequest = KEWServiceLocator
							.getActionRequestService().findByActionRequestId(
									actionItem.getActionRequestId());
					ActionRequestVO actionRequestVO = BeanConverter
							.convertActionRequest(actionRequest);
					customEmailAttribute.setRouteHeaderVO(routeHeaderVO);
					customEmailAttribute.setActionRequestVO(actionRequestVO);
					String customBody = customEmailAttribute
							.getCustomEmailBody();
					if (!Utilities.isEmpty(customBody)) {
						emailBody.append(customBody);
					}
					String customEmailSubject = customEmailAttribute
							.getCustomEmailSubject();
					if (!Utilities.isEmpty(customEmailSubject)) {
						emailSubject.append(customEmailSubject);
					}
				}
			} catch (Exception e) {
				LOG
						.error(
								"Error when checking for custom email body and subject.",
								e);
			}
            LOG.debug("Sending email to " + user);
			sendEmail(user, getEmailSubject(emailSubject.toString()),
					new EmailBody(emailBody.toString()), document
							.getDocumentType());
		}

	}

	private boolean isProduction() {
		return EdenConstants.PROD_DEPLOYMENT_CODE.equals(getDeploymentEnvironment());
	}

	public void sendDailyReminder() {
		if (sendActionListEmailNotification()) {
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
		}
		LOG.debug("Daily action list emails sent successful");
	}

	public void sendWeeklyReminder() {
		if (sendActionListEmailNotification()) {
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
		}
		LOG.debug("Weekly action list emails sent successful");
	}

	private void sendReminder(WorkflowUser user, Collection actionItems,
			String emailSetting) {
		String emailBody = null;
		actionItems = filterActionItemsToNotify(user, actionItems);
		// if there are no action items after being filtered, there's no
		// reason to send the email
		if (actionItems.isEmpty()) {
			return;
		}
		if (EdenConstants.EMAIL_RMNDR_DAY_VAL.equals(emailSetting)) {
			emailBody = buildDailyReminderBody(user, actionItems);
		} else if (EdenConstants.EMAIL_RMNDR_WEEK_VAL.equals(emailSetting)) {
			emailBody = buildWeeklyReminderBody(user, actionItems);
		}
		sendEmail(user, getEmailSubject(), new EmailBody(emailBody));
	}

	/**
	 * Returns a filtered Collection of {@link ActionItem}s which are
	 * filtered according to the user's preferences.  If they have opted
	 * not to recieve secondary or primary delegation emails then they
	 * will not be included.
	 */
	private Collection filterActionItemsToNotify(WorkflowUser user, Collection actionItems) {
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

    /**
     * 0 = actionItem.getRouteHeaderId()
     * 1 = actionItem.getRouteHeader().getInitiatorUser().getDisplayName()
     * 2 = actionItem.getRouteHeader().getDocumentType().getName()
     * 3 = actionItem.getDocTitle()
     * 4 = docHandlerUrl
     * 5 = getActionListUrl()
     * 6 = getPreferencesUrl()
     * 7 = getHelpLink(documentType)
     */
    private static final MessageFormat DEFAULT_IMMEDIATE_REMINDER = new MessageFormat(
        "Your OneStart Action List has an eDoc(electronic document) that needs your attention: \n\n" +
        "Document ID:\t{0,number,#}\n" +
        "Initiator:\t\t{1}\n" +
        "Type:\t\tAdd/Modify {2}\n" +
        "Title:\t\t{3}\n" +
        "\n\n" +
        "To respond to this eDoc: \n" +
        "\tGo to {4}\n\n" +
        "\tOr you may access the eDoc from your Action List: \n" +
        "\tGo to {5}, and then click on the numeric Document ID: {0,number,#} in the first column of the List. \n" +
        "\n\n\n" +
        "To change how these email notifications are sent(daily, weekly or none): \n" +
        "\tGo to {6}\n" +
        "\n\n\n" +
        "{7}\n\n\n"
    );

	public String buildImmediateReminderBody(WorkflowUser user,
			ActionItem actionItem, DocumentType documentType) {
		String docHandlerUrl = actionItem.getRouteHeader().getDocumentType()
				.getDocHandlerUrl();
		if (docHandlerUrl.indexOf("?") == -1) {
			docHandlerUrl += "?";
		} else {
			docHandlerUrl += "&";
		}
		docHandlerUrl += IDocHandler.ROUTEHEADER_ID_PARAMETER + "="
				+ actionItem.getRouteHeaderId();
		docHandlerUrl += "&" + IDocHandler.COMMAND_PARAMETER + "="
				+ IDocHandler.ACTIONLIST_COMMAND;
		StringBuffer sf = new StringBuffer();

		/*sf
				.append("Your OneStart Action List has an eDoc(electronic document) that needs your attention: \n\n");
		sf.append("Document ID:\t" + actionItem.getRouteHeaderId() + "\n");
		sf.append("Initiator:\t\t");
		try {
			sf.append(actionItem.getRouteHeader().getInitiatorUser()
					.getDisplayName()
					+ "\n");
		} catch (Exception e) {
			LOG.error("Error retrieving initiator for action item "
					+ actionItem.getRouteHeaderId());
			sf.append("\n");
		}
		sf.append("Type:\t\t" + "Add/Modify "
				+ actionItem.getRouteHeader().getDocumentType().getName()
				+ "\n");
		sf.append("Title:\t\t" + actionItem.getDocTitle() + "\n");
		sf.append("\n\n");
		sf.append("To respond to this eDoc: \n");
		sf.append("\tGo to " + docHandlerUrl + "\n\n");
		sf.append("\tOr you may access the eDoc from your Action List: \n");
		sf.append("\tGo to " + getActionListUrl()
				+ ", and then click on the numeric Document ID: "
				+ actionItem.getRouteHeaderId()
				+ " in the first column of the List. \n");
		sf.append("\n\n\n");
		sf
				.append("To change how these email notifications are sent(daily, weekly or none): \n");
		sf.append("\tGo to " + getPreferencesUrl() + "\n");
		sf.append("\n\n\n");
		sf.append(getHelpLink(documentType) + "\n\n\n");*/

        MessageFormat messageFormat = null;
        String stringMessageFormat = Core.getCurrentContextConfig().getProperty(IMMEDIATE_REMINDER_EMAIL_MESSAGE_KEY);
        LOG.debug("Immediate reminder email message from configuration (" + IMMEDIATE_REMINDER_EMAIL_MESSAGE_KEY + "): " + stringMessageFormat);
        if (stringMessageFormat == null) {
            messageFormat = DEFAULT_IMMEDIATE_REMINDER;
        } else {
            messageFormat = new MessageFormat(stringMessageFormat);
        }
        String initiatorUser = "";
        try {
            initiatorUser = actionItem.getRouteHeader().getInitiatorUser().getDisplayName();
        } catch (Exception e) {
            LOG.error("Error retrieving initiator for action item "
                    + actionItem.getRouteHeaderId());
        }
        Object[] args = {
            actionItem.getRouteHeaderId(),
            initiatorUser,
            actionItem.getRouteHeader().getDocumentType().getName(),
            actionItem.getDocTitle(),
            docHandlerUrl,
            getActionListUrl(),
            getPreferencesUrl(),
            getHelpLink(documentType)
        };

        messageFormat.format(args, sf, new FieldPosition(0));

        LOG.debug("default immediate reminder: " + DEFAULT_IMMEDIATE_REMINDER.format(args));
        LOG.debug("immediate reminder: " + sf);

		// for debugging purposes on the immediate reminder only
		if (!isProduction()) {
			try {
				sf.append("Action Item sent to "
						+ actionItem.getUser().getAuthenticationUserId()
								.getAuthenticationId());
				if (actionItem.getDelegationType() != null) {
					sf.append(" for delegation type "
							+ actionItem.getDelegationType());
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return sf.toString();
	}

	public String buildDailyReminderBody(WorkflowUser user,
			Collection actionItems) {
		StringBuffer sf = new StringBuffer();
		sf.append(getDailyWeeklyMessageBody(actionItems));
		sf
				.append("To change how these email notifications are sent (immediately, weekly or none): \n");
		sf.append("\tGo to " + getPreferencesUrl() + "\n");
		// sf.append("\tSend as soon as you get an eDoc\n\t" +
		// getPreferencesUrl() + "\n\n");
		// sf.append("\tSend weekly\n\t" + getPreferencesUrl() + "\n\n");
		// sf.append("\tDo not send\n\t" + getPreferencesUrl() + "\n");
		sf.append("\n\n\n");
		sf.append(getHelpLink() + "\n\n\n");
		return sf.toString();
	}

	public String buildWeeklyReminderBody(WorkflowUser user,
			Collection actionItems) {
		StringBuffer sf = new StringBuffer();
		sf.append(getDailyWeeklyMessageBody(actionItems));
		sf
				.append("To change how these email notifications are sent (immediately, daily or none): \n");
		sf.append("\tGo to " + getPreferencesUrl() + "\n");
		// sf.append("\tSend as soon as you get an eDoc\n\t" +
		// getPreferencesUrl() + "\n\n");
		// sf.append("\tSend daily\n\t" + getPreferencesUrl() + "\n\n");
		// sf.append("\tDo not send\n\t" + getPreferencesUrl() + "\n");
		sf.append("\n\n\n");
		sf.append(getHelpLink() + "\n\n\n");
		return sf.toString();
	}

	String getDailyWeeklyMessageBody(Collection actionItems) {
		StringBuffer sf = new StringBuffer();
		HashMap docTypes = getActionListItemsStat(actionItems);

		sf
				.append("Your OneStart Action List has "
						+ actionItems.size()
						+ " eDocs(electronic documents) that need your attention: \n\n");
		Iterator iter = docTypes.keySet().iterator();
		while (iter.hasNext()) {
			String docTypeName = (String) iter.next();
			sf.append("\t" + ((Integer) docTypes.get(docTypeName)).toString()
					+ "\t" + docTypeName + "\n");
		}
		sf.append("\n\n");
		sf.append("To respond to each of these eDocs: \n");
		sf
				.append("\tGo to "
						+ getActionListUrl()
						+ ", and then click on its numeric Document ID in the first column of the List.\n");
		sf.append("\n\n\n");
		return sf.toString();
	}

	private HashMap getActionListItemsStat(Collection actionItems) {
		HashMap docTypes = new LinkedHashMap();
		Iterator iter = actionItems.iterator();

		while (iter.hasNext()) {
			String docTypeName = ((ActionItem) iter.next()).getRouteHeader()
					.getDocumentType().getName();
			if (docTypes.containsKey(docTypeName)) {
				docTypes.put(docTypeName, new Integer(((Integer) docTypes
						.get(docTypeName)).intValue() + 1));
			} else {
				docTypes.put(docTypeName, new Integer(1));
			}
		}
		return docTypes;
	}

	private boolean sendActionListEmailNotification() {
        LOG.debug("actionlistsendconstant: " + Utilities.getApplicationConstant(EdenConstants.ACTION_LIST_SEND_EMAIL_NOTIFICATION_KEY));
		return EdenConstants.ACTION_LIST_SEND_EMAIL_NOTIFICATION_VALUE
				.equals(Utilities
						.getApplicationConstant(EdenConstants.ACTION_LIST_SEND_EMAIL_NOTIFICATION_KEY));
	}

	public void scheduleBatchEmailReminders() throws Exception {
	    String emailBatchGroup = "Email Batch";
	    String dailyCron = Core.getCurrentContextConfig().getProperty(EdenConstants.DAILY_EMAIL_CRON_EXPRESSION);
	    if (!StringUtils.isBlank(dailyCron)) {
		LOG.info("Scheduling Daily Email batch with cron expression: " + dailyCron);
		CronTrigger dailyTrigger = new CronTrigger(DAILY_TRIGGER_NAME, emailBatchGroup, dailyCron);
		JobDetail dailyJobDetail = new JobDetail(DAILY_JOB_NAME, emailBatchGroup, DailyEmailJob.class);
		dailyTrigger.setJobName(dailyJobDetail.getName());
		dailyTrigger.setJobGroup(dailyJobDetail.getGroup());
		addJobToScheduler(dailyJobDetail);
		addTriggerToScheduler(dailyTrigger);
	    } else {
		LOG.warn("No " + EdenConstants.DAILY_EMAIL_CRON_EXPRESSION + " parameter was configured.  Daily Email batch was not scheduled!");
	    }

	    String weeklyCron = Core.getCurrentContextConfig().getProperty(EdenConstants.WEEKLY_EMAIL_CRON_EXPRESSION);
	    if (!StringUtils.isBlank(dailyCron)) {
		LOG.info("Scheduling Weekly Email batch with cron expression: " + weeklyCron);
		CronTrigger weeklyTrigger = new CronTrigger(WEEKLY_TRIGGER_NAME, emailBatchGroup, weeklyCron);
		JobDetail weeklyJobDetail = new JobDetail(WEEKLY_JOB_NAME, emailBatchGroup, WeeklyEmailJob.class);
		weeklyTrigger.setJobName(weeklyJobDetail.getName());
		weeklyTrigger.setJobGroup(weeklyJobDetail.getGroup());
		addJobToScheduler(weeklyJobDetail);
		addTriggerToScheduler(weeklyTrigger);
	    } else {
		LOG.warn("No " + EdenConstants.WEEKLY_EMAIL_CRON_EXPRESSION + " parameter was configured.  Weekly Email batch was not scheduled!");
	    }
	}

	private void addJobToScheduler(JobDetail jobDetail) throws SchedulerException {
		getScheduler().addJob(jobDetail, true);
	}

	private void addTriggerToScheduler(Trigger trigger) throws SchedulerException {
		boolean triggerExists = (getScheduler().getTrigger(trigger.getName(), trigger.getGroup()) != null);
		if (!triggerExists) {
			try {
				getScheduler().scheduleJob(trigger);
			} catch (ObjectAlreadyExistsException ex) {
				getScheduler().rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
			}
		} else {
		    getScheduler().rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
		}
	}

	private Scheduler getScheduler() {
		return KSBServiceLocator.getScheduler();
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

	public String getDeploymentEnvironment() {
		return deploymentEnvironment;
	}

	public void setDeploymentEnvironment(String deploymentEnvironment) {
		this.deploymentEnvironment = deploymentEnvironment;
	}

	private String getActionListUrl() {
		return Core.getCurrentContextConfig().getBaseUrl()
				+ Utilities
						.getApplicationConstant(EdenConstants.APPLICATION_CONTEXT_KEY)
				+ "/" + "ActionList.do";
	}

	private String getPreferencesUrl() {
		return Core.getCurrentContextConfig().getBaseUrl()
				+ Utilities
						.getApplicationConstant(EdenConstants.APPLICATION_CONTEXT_KEY)
				+ "/" + "Preferences.do";
	}
}