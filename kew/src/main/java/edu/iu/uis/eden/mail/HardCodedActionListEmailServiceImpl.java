package edu.iu.uis.eden.mail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.kuali.rice.core.Core;

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
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.server.BeanConverter;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.useroptions.UserOptions;
import edu.iu.uis.eden.useroptions.UserOptionsService;
import edu.iu.uis.eden.util.Utilities;

/**
 * Old hardcoded implementation for unit testing purposes only
 * @deprecated This is the original hardcoded actionlistemailservice implementation
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class HardCodedActionListEmailServiceImpl extends ActionListEmailServiceImpl {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(HardCodedActionListEmailServiceImpl.class);

	private static final String DEFAULT_EMAIL_FROM_ADDRESS = "workflow@indiana.edu";

	private static final String ACTION_LIST_REMINDER = "OneStart Action List Reminder";

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
		return new EmailSubject(ACTION_LIST_REMINDER);
	}

	public EmailSubject getEmailSubject(String customSubject) {
		return new EmailSubject(ACTION_LIST_REMINDER + " " + customSubject);
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
		if (sendActionListEmailNotification()) {
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
			sendEmail(user, getEmailSubject(emailSubject.toString()),
					new EmailBody(emailBody.toString()), document
							.getDocumentType());
		}

	}

	private boolean isProduction() {
		return getDeploymentEnvironment().equals(
				EdenConstants.PROD_DEPLOYMENT_CODE);
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
		if (EdenConstants.EMAIL_RMNDR_DAY_VAL.equals(emailSetting)) {
			emailBody = buildDailyReminderBody(user, actionItems);
		} else if (EdenConstants.EMAIL_RMNDR_WEEK_VAL.equals(emailSetting)) {
			emailBody = buildWeeklyReminderBody(user, actionItems);
		}
		sendEmail(user, getEmailSubject(), new EmailBody(emailBody));
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

		sf
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
		sf.append(getHelpLink(documentType) + "\n\n\n");

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

	protected String getDailyWeeklyMessageBody(Collection actionItems) {
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
		HashMap docTypes = new HashMap();
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
		return EdenConstants.ACTION_LIST_SEND_EMAIL_NOTIFICATION_VALUE
				.equals(Utilities
						.getApplicationConstant(EdenConstants.ACTION_LIST_SEND_EMAIL_NOTIFICATION_KEY));
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