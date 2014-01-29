/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.ken.web.spring;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.framework.persistence.dao.GenericDao;
import org.kuali.rice.coreservice.api.namespace.Namespace;
import org.kuali.rice.coreservice.api.namespace.NamespaceService;
import org.kuali.rice.ken.bo.NotificationBo;
import org.kuali.rice.ken.bo.NotificationChannelBo;
import org.kuali.rice.ken.bo.NotificationChannelReviewerBo;
import org.kuali.rice.ken.bo.NotificationPriorityBo;
import org.kuali.rice.ken.bo.NotificationProducerBo;
import org.kuali.rice.ken.bo.NotificationRecipientBo;
import org.kuali.rice.ken.bo.NotificationSenderBo;
import org.kuali.rice.ken.document.kew.NotificationWorkflowDocument;
import org.kuali.rice.ken.exception.ErrorList;
import org.kuali.rice.ken.service.NotificationChannelService;
import org.kuali.rice.ken.service.NotificationMessageContentService;
import org.kuali.rice.ken.service.NotificationRecipientService;
import org.kuali.rice.ken.service.NotificationService;
import org.kuali.rice.ken.service.NotificationWorkflowDocumentService;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.ken.util.Util;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.rule.GenericAttributeContent;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for KEN controllers for sending notifications
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class BaseSendNotificationController extends MultiActionController {
    private static final Logger LOG = Logger.getLogger(BaseSendNotificationController.class);

    private static final String USER_RECIPS_PARAM = "userRecipients";
    private static final String WORKGROUP_RECIPS_PARAM = "workgroupRecipients";
    private static final String WORKGROUP_NAMESPACE_CODES_PARAM = "workgroupNamespaceCodes";
    private static final String SPLIT_REGEX = "(%2C|,)";

    private static final String NONE_CHANNEL = "___NONE___";
    private static final long REASONABLE_IMMEDIATE_TIME_THRESHOLD = 1000 * 60 * 5; // <= 5 minutes is "immediate"

    private static IdentityService identityService;
    private static GroupService groupService;
    private static NamespaceService namespaceService;

    protected NotificationService notificationService;
    protected NotificationWorkflowDocumentService notificationWorkflowDocService;
    protected NotificationChannelService notificationChannelService;
    protected NotificationRecipientService notificationRecipientService;
    protected NotificationMessageContentService notificationMessageContentService;
    protected DataObjectService dataObjectService;

    protected static IdentityService getIdentityService() {
        if ( identityService == null ) {
            identityService = KimApiServiceLocator.getIdentityService();
        }
        return identityService;
    }

    protected static GroupService getGroupService() {
        if ( groupService == null ) {
            groupService = KimApiServiceLocator.getGroupService();
        }
        return groupService;
    }

    protected static NamespaceService getNamespaceService() {
        if ( namespaceService == null ) {
            namespaceService = CoreServiceApiServiceLocator.getNamespaceService();
        }
        return namespaceService;
    }

    /**
     * Sets the {@link NotificationService}.
     *
     * @param notificationService the service to set
     */
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Sets the {@link NotificationWorkflowDocumentService}.
     *
     * @param notificationWorkflowDocService the service to set
     */
    public void setNotificationWorkflowDocumentService(NotificationWorkflowDocumentService notificationWorkflowDocService) {
        this.notificationWorkflowDocService = notificationWorkflowDocService;
    }

    /**
     * Sets the {@link NotificationChannelService}.
     *
     * @param notificationChannelService the service to set
     */
    public void setNotificationChannelService(NotificationChannelService notificationChannelService) {
        this.notificationChannelService = notificationChannelService;
    }

    /**
     * Sets the {@link NotificationRecipientService}.
     *
     * @param notificationRecipientService the service to set
     */
    public void setNotificationRecipientService(NotificationRecipientService notificationRecipientService) {
        this.notificationRecipientService = notificationRecipientService;
    }

    /**
     * Sets the {@link NotificationMessageContentService}.
     *
     * @param notificationMessageContentService the service to set
     */
    public void setNotificationMessageContentService(NotificationMessageContentService notificationMessageContentService) {
        this.notificationMessageContentService = notificationMessageContentService;
    }

    /**
     * Sets the businessObjectDao attribute value.
     * @param dataObjectService the service to set
     */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }


    protected String getParameter(HttpServletRequest request, String parameterName, Map<String, Object> model, ErrorList errors, String errorMessage) {
        String parameter = request.getParameter(parameterName);

        if (StringUtils.isNotEmpty(parameter)) {
            model.put(parameterName, parameter);
        } else {
            errors.addError(errorMessage);
        }

        return parameter;
    }

    protected String getParameter(HttpServletRequest request, String parameterName, Map<String, Object> model, ErrorList errors, String errorMessage, String defaultValue) {
        String parameter = StringUtils.defaultIfBlank(request.getParameter(parameterName), defaultValue);

        if (StringUtils.isNotEmpty(parameter)) {
            model.put(parameterName, parameter);
        } else {
            errors.addError(errorMessage);
        }

        return parameter;
    }

    protected String[] getParameterList(HttpServletRequest request, String parameterName, Map<String, Object> model, ErrorList errors, String errorMessage) {
        String parameter = request.getParameter(parameterName);
        String[] senders = null;

        if (StringUtils.isNotEmpty(parameter)) {
            senders = StringUtils.split(parameter, ",");
            model.put(parameterName, parameter);
        } else {
            errors.addError(errorMessage);
        }

        return senders;
    }

    protected Date getDate(String parameter, ErrorList errors, String errorMessage) {
        Date date = null;

        try {
            date = Util.parseUIDateTime(parameter);
        } catch (ParseException pe) {
            errors.addError(errorMessage);
        }

        return date;
    }
    
    protected String[] parseUserRecipients(HttpServletRequest request) {
        return parseCommaSeparatedValues(request, USER_RECIPS_PARAM);
    }

    protected String[] parseWorkgroupRecipients(HttpServletRequest request) {
        return parseCommaSeparatedValues(request, WORKGROUP_RECIPS_PARAM);
    }

    protected String[] parseWorkgroupNamespaceCodes(HttpServletRequest request) {
    	return parseCommaSeparatedValues(request, WORKGROUP_NAMESPACE_CODES_PARAM);
    }
    
    protected String[] parseCommaSeparatedValues(HttpServletRequest request, String param) {
        String vals = request.getParameter(param);
        if (vals != null) {
            String[] split = vals.split(SPLIT_REGEX);
            List<String> strs = new ArrayList<String>();
            for (String component: split) {
                if (StringUtils.isNotBlank(component)) {
                    strs.add(component.trim());
                }
            }
            return strs.toArray(new String[strs.size()]);
        } else {
            return new String[0];
        }
    }

    protected boolean isUserRecipientValid(String user, ErrorList errors) {
        boolean valid = true;
        Principal principal = getIdentityService().getPrincipalByPrincipalName(user);
        if (principal == null) {
        	valid = false;
        	errors.addError("'" + user + "' is not a valid principal name");
        }

        return valid;
    }

    protected boolean isWorkgroupRecipientValid(String groupName, String namespaceCode, ErrorList errors) {
    	Namespace nSpace = getNamespaceService().getNamespace(namespaceCode);
    	if (nSpace == null) {
    		errors.addError((new StringBuilder()).append('\'').append(namespaceCode).append("' is not a valid namespace code").toString());
    		return false;
    	} else {
    		Group i = getGroupService().getGroupByNamespaceCodeAndName(namespaceCode, groupName);
       		if (i == null) {
       			errors.addError((new StringBuilder()).append('\'').append(groupName).append(
       					"' is not a valid group name for namespace code '").append(namespaceCode).append('\'').toString());
       			return false;
       		} else {
       			return true;
       		}
    	}
    }
    protected String getPrincipalIdFromIdOrName(String principalIdOrName) {
        Principal principal = KimApiServiceLocator.getIdentityService().getPrincipal(principalIdOrName);
        if (principal == null) {
            principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(principalIdOrName);
        }
        if (principal == null) {
            throw new RiceIllegalArgumentException("Could not locate a principal as initiator with the given remoteUser of " + principalIdOrName);
        }
        return principal.getPrincipalId();
    }

    /**
     * Submits the actual event notification message.
     *
     * @param request the servlet request
     * @param routeMessage the message to attach to the route action
     * @param viewName the name of the view to forward to after completion
     *
     * @return the next view to show
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected ModelAndView submitNotificationMessage(HttpServletRequest request, String routeMessage, String viewName)
            throws ServletException, IOException {
        LOG.debug("remoteUser: " + request.getRemoteUser());

        // obtain a workflow user object first
        //WorkflowIdDTO initiator = new WorkflowIdDTO(request.getRemoteUser());
        String initiatorId = getPrincipalIdFromIdOrName( request.getRemoteUser());
        LOG.debug("initiatorId: " + initiatorId);

        // now construct the workflow document, which will interact with workflow
        Map<String, Object> model = new HashMap<String, Object>();

        try {
            WorkflowDocument document = createNotificationWorkflowDocument(request, initiatorId, model);

            document.route(routeMessage + initiatorId);

            // This ain't pretty, but it gets the job done for now.
            ErrorList el = new ErrorList();
            el.addError("Notification(s) sent.");
            model.put("errors", el);
        } catch (ErrorList el) {
            // route back to the send form again
            Map<String, Object> model2 = setupModelForSendNotification(request);
            model.putAll(model2);
            model.put("errors", el);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ModelAndView(viewName, model);
    }

    /**
     * Creates a notification {@link WorkflowDocument}.
     *
     * @param request the servlet request
     * @param initiatorId the user sending the notification
     * @param model the Spring MVC model
     *
     * @return a {@link WorkflowDocument} for the notification
     * @throws java.lang.IllegalArgumentException
     * @throws org.kuali.rice.ken.exception.ErrorList
     */
    protected WorkflowDocument createNotificationWorkflowDocument(HttpServletRequest request, String initiatorId,
            Map<String, Object> model) throws IllegalArgumentException, ErrorList {
        WorkflowDocument document = NotificationWorkflowDocument.createNotificationDocument(initiatorId,
                NotificationConstants.KEW_CONSTANTS.SEND_NOTIFICATION_REQ_DOC_TYPE);

        //parse out the application content into a Notification BO
        NotificationBo notification = populateNotificationInstance(request, model);

        // now get that content in an understandable XML format and pass into document
        String notificationAsXml = notificationMessageContentService.generateNotificationMessage(notification);

        Map<String, String> attrFields = new HashMap<String,String>();
        List<NotificationChannelReviewerBo> reviewers = notification.getChannel().getReviewers();
        int ui = 0;
        int gi = 0;
        for (NotificationChannelReviewerBo reviewer: reviewers) {
            String prefix;
            int index;
            if (KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE.getCode().equals(reviewer.getReviewerType())) {
                prefix = "user";
                index = ui;
                ui++;
            } else if (KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE.getCode().equals(reviewer.getReviewerType())) {
                prefix = "group";
                index = gi;
                gi++;
            } else {
                LOG.error("Invalid type for reviewer " + reviewer.getReviewerId() + ": " + reviewer.getReviewerType());
                continue;
            }
            attrFields.put(prefix + index, reviewer.getReviewerId());
        }
        GenericAttributeContent gac = new GenericAttributeContent("channelReviewers");
        document.setApplicationContent(notificationAsXml);
        document.setAttributeContent("<attributeContent>" + gac.generateContent(attrFields) + "</attributeContent>");

        document.setTitle(notification.getTitle());

        return document;
    }

    /**
     * Creates a new {@link NotificationBo} instance.
     *
     * @param request the servlet request
     * @param model the Spring MVC model
     *
     * @return a new notification
     * @throws java.lang.IllegalArgumentException
     * @throws org.kuali.rice.ken.exception.ErrorList
     */
    protected NotificationBo populateNotificationInstance(HttpServletRequest request, Map<String, Object> model)
            throws IllegalArgumentException, ErrorList {
        return createNotification(request, model, new ErrorList());
    }

    /**
     * Provides an overridable method in which to customize a created {@link NotificationBo} instance.
     *
     * @param request the servlet request
     * @param model the Spring MVC model
     * @param errors the error list
     *
     * @return a new notification
     * @throws ErrorList
     */
    protected NotificationBo createNotification(HttpServletRequest request, Map<String, Object> model, ErrorList errors)
            throws ErrorList {
        String channelName = getChannelName(request, model, errors);
        String priorityName = getParameter(request, "priorityName", model, errors, "You must choose a priority.");
        String[] senders = getParameterList(request, "senderNames", model, errors, "You must enter at least one sender.");
        String deliveryType = getDeliveryType(request, model, errors);

        Date originalDate = getDate(request.getParameter("originalDateTime"), errors, "Original date is invalid.");

        String sendDateTime = StringUtils.defaultIfBlank(request.getParameter("sendDateTime"), Util.getCurrentDateTime());
        Date sendDate = getDate(sendDateTime, errors, "You specified an invalid Send Date/Time.  Please use the calendar picker.");
        if (sendDate != null && sendDate.before(originalDate)) {
            errors.addError("Send Date/Time cannot be in the past.");
        }
        model.put("sendDateTime", sendDateTime);

        String autoRemoveDateTime = request.getParameter("autoRemoveDateTime");
        Date removeDate = getDate(autoRemoveDateTime, errors, "You specified an invalid Auto-Remove Date/Time.  Please use the calendar picker.");
        if (removeDate != null) {
            if (removeDate.before(originalDate)) {
                errors.addError("Auto-Remove Date/Time cannot be in the past.");
            } else if (sendDate != null && removeDate.before(sendDate)) {
                errors.addError("Auto-Remove Date/Time cannot be before the Send Date/Time.");
            }
        }
        model.put("autoRemoveDateTime", autoRemoveDateTime);

        // user recipient names
        String[] userRecipients = parseUserRecipients(request);

        // workgroup recipient names
        String[] workgroupRecipients = parseWorkgroupRecipients(request);

        // workgroup namespace codes
        String[] workgroupNamespaceCodes = parseWorkgroupNamespaceCodes(request);

        String title = getParameter(request, "title", model, errors, "You must fill in a title.");

        // check to see if there were any errors
        if (!errors.getErrors().isEmpty()) {
            throw errors;
        }

        return createNotification(title, deliveryType, sendDate, removeDate, channelName, priorityName,
                senders, userRecipients, workgroupRecipients, workgroupNamespaceCodes, errors);
    }

    private NotificationBo createNotification(String title, String deliveryType, Date sendDate, Date removeDate,
            String channelName, String priorityName, String[] senders, String[] userRecipients,
            String[] workgroupRecipients, String[] workgroupNamespaceCodes, ErrorList errors) throws ErrorList {
        NotificationBo notification = new NotificationBo();
        notification.setTitle(title);
        notification.setDeliveryType(deliveryType);
        notification.setSendDateTimeValue(new Timestamp(sendDate.getTime()));
        notification.setAutoRemoveDateTimeValue(new Timestamp(removeDate.getTime()));

        NotificationChannelBo channel = Util.retrieveFieldReference("channel", "name", channelName,
                NotificationChannelBo.class, dataObjectService);
        notification.setChannel(channel);

        NotificationPriorityBo priority = Util.retrieveFieldReference("priority", "name", priorityName,
                NotificationPriorityBo.class, dataObjectService);
        notification.setPriority(priority);

        NotificationProducerBo producer = Util.retrieveFieldReference("producer", "name",
                NotificationConstants.KEW_CONSTANTS.NOTIFICATION_SYSTEM_USER_NAME, NotificationProducerBo.class,
                dataObjectService);
        notification.setProducer(producer);

        for (String senderName : senders) {
            if (StringUtils.isEmpty(senderName)) {
                errors.addError("A sender's name cannot be blank.");
            } else {
                NotificationSenderBo ns = new NotificationSenderBo();
                ns.setSenderName(senderName.trim());
                notification.addSender(ns);
            }
        }

        if (userRecipients != null && userRecipients.length > 0) {
            for (String userRecipientId : userRecipients) {
                if (isUserRecipientValid(userRecipientId, errors)) {
                    NotificationRecipientBo recipient = new NotificationRecipientBo();
                    recipient.setRecipientType(KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE.getCode());
                    recipient.setRecipientId(userRecipientId);
                    notification.addRecipient(recipient);
                }
            }
        }

        if (workgroupRecipients != null && workgroupRecipients.length > 0) {
            if (workgroupNamespaceCodes != null && workgroupNamespaceCodes.length > 0) {
                if (workgroupNamespaceCodes.length == workgroupRecipients.length) {
                    for (int i = 0; i < workgroupRecipients.length; i++) {
                        if (isWorkgroupRecipientValid(workgroupRecipients[i], workgroupNamespaceCodes[i], errors)) {
                            NotificationRecipientBo recipient = new NotificationRecipientBo();
                            recipient.setRecipientType(KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE.getCode());
                            recipient.setRecipientId(
                                    getGroupService().getGroupByNamespaceCodeAndName(workgroupNamespaceCodes[i],
                                            workgroupRecipients[i]).getId());
                            notification.addRecipient(recipient);
                        }
                    }
                } else {
                    errors.addError("The number of groups must match the number of namespace codes");
                }
            } else {
                errors.addError("You must specify a namespace code for every group name");
            }
        } else if (workgroupNamespaceCodes != null && workgroupNamespaceCodes.length > 0) {
            errors.addError("You must specify a group name for every namespace code");
        }

        if (!recipientsExist(userRecipients, workgroupRecipients) && !hasPotentialRecipients(notification)) {
            errors.addError("You must specify at least one user or group recipient.");
        }

        notification.setContent(NotificationConstants.XML_MESSAGE_CONSTANTS.CONTENT_SIMPLE_OPEN
                + NotificationConstants.XML_MESSAGE_CONSTANTS.MESSAGE_OPEN
                + NotificationConstants.XML_MESSAGE_CONSTANTS.MESSAGE_CLOSE
                + NotificationConstants.XML_MESSAGE_CONSTANTS.CONTENT_CLOSE);

        return notification;
    }

    private String getChannelName(HttpServletRequest request, Map<String, Object> model, ErrorList errors) {
        String channelName = request.getParameter("channelName");

        if (StringUtils.isEmpty(channelName) || StringUtils.equals(channelName, NONE_CHANNEL)) {
            errors.addError("You must choose a channel.");
        } else {
            model.put("channelName", channelName);
        }

        return channelName;
    }

    private String getDeliveryType(HttpServletRequest request, Map<String, Object> model, ErrorList errors) {
        String deliveryType = request.getParameter("deliveryType");

        if (StringUtils.isNotEmpty(deliveryType)) {
            if (deliveryType.equalsIgnoreCase(NotificationConstants.DELIVERY_TYPES.FYI)) {
                deliveryType = NotificationConstants.DELIVERY_TYPES.FYI;
            } else {
                deliveryType = NotificationConstants.DELIVERY_TYPES.ACK;
            }
            model.put("deliveryType", deliveryType);
        } else {
            errors.addError("You must choose a delivery type.");
        }

        return deliveryType;
    }

    /**
     * Prepares the model used for sending the notification.
     *
     * @param request the servlet request
     *
     * @return the Spring MVC model
     */
    protected Map<String, Object> setupModelForSendNotification(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<String, Object>();

        model.put("defaultSender", request.getRemoteUser());
        model.put("channels", notificationChannelService.getAllNotificationChannels());
        model.put("priorities", dataObjectService.findMatching(NotificationPriorityBo.class,
                QueryByCriteria.Builder.create().build()).getResults());

        // set sendDateTime to current datetime if not provided
        String sendDateTime = request.getParameter("sendDateTime");
        String currentDateTime = Util.getCurrentDateTime();
        if (StringUtils.isEmpty(sendDateTime)) {
            sendDateTime = currentDateTime;
        }
        model.put("sendDateTime", sendDateTime);

        // retain the original date time or set to current if it was not in the request
        if (request.getParameter("originalDateTime") == null) {
            model.put("originalDateTime", currentDateTime);
        } else {
            model.put("originalDateTime", request.getParameter("originalDateTime"));
        }

        model.put("userRecipients", request.getParameter("userRecipients"));
        model.put("workgroupRecipients", request.getParameter("workgroupRecipients"));
        model.put("workgroupNamespaceCodes", request.getParameter("workgroupNamespaceCodes"));

        return model;
    }

    /**
     * Returns whether the specified time is considered "in the future", based on some reasonable threshold.
     *
     * @param time the time to test
     *
     * @return true if the specified time is considered "in the future", false otherwise
     */
    private boolean timeIsInTheFuture(long time) {
        boolean future = (time - System.currentTimeMillis()) > REASONABLE_IMMEDIATE_TIME_THRESHOLD;
        LOG.info("Time: " + new Date(time) + " is in the future? " + future);
        return future;
    }

    /**
     * Returns whether recipients exist either, from users or workgroups.
     *
     * @param userRecipients the list of user recipients
     * @param workgroupRecipients the list of workgroup recipients
     *
     * @return true if there are any recipients, false otherwise
     */
    private boolean recipientsExist(String[] userRecipients, String[] workgroupRecipients) {
        return (userRecipients != null && userRecipients.length > 0)
            || (workgroupRecipients != null && workgroupRecipients.length > 0);
    }

    /**
     * Returns whether the specified Notification can be reasonably expected to have recipients.
     *
     * This is determined on whether the channel has default recipients, is subscribable, and whether the send date time
     * is far enough in the future to expect that if there are no subscribers, there may actually be some by the time
     * the notification is sent.
     * @param notification the notification to test
     *
     * @return whether the specified Notification can be reasonably expected to have recipients
     */
    private boolean hasPotentialRecipients(NotificationBo notification) {
        LOG.info("notification channel " + notification.getChannel() + " is subscribable: " + notification.getChannel().isSubscribable());
        return !notification.getChannel().getRecipientLists().isEmpty() ||
               !notification.getChannel().getSubscriptions().isEmpty() ||
                (notification.getChannel().isSubscribable() && timeIsInTheFuture(notification.getSendDateTimeValue().getTime()));
    }
}
