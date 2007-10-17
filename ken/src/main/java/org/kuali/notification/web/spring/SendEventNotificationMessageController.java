/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.notification.web.spring;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.notification.bo.Notification;
import org.kuali.notification.bo.NotificationChannel;
import org.kuali.notification.bo.NotificationChannelReviewer;
import org.kuali.notification.bo.NotificationContentType;
import org.kuali.notification.bo.NotificationPriority;
import org.kuali.notification.bo.NotificationProducer;
import org.kuali.notification.bo.NotificationRecipient;
import org.kuali.notification.bo.NotificationSender;
import org.kuali.notification.dao.BusinessObjectDao;
import org.kuali.notification.document.kew.NotificationWorkflowDocument;
import org.kuali.notification.exception.ErrorList;
import org.kuali.notification.service.NotificationChannelService;
import org.kuali.notification.service.NotificationMessageContentService;
import org.kuali.notification.service.NotificationRecipientService;
import org.kuali.notification.service.NotificationService;
import org.kuali.notification.service.NotificationWorkflowDocumentService;
import org.kuali.notification.util.NotificationConstants;
import org.kuali.notification.util.Util;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.routetemplate.GenericAttributeContent;

/**
 * This class is the controller for sending Event notification messages via an end user interface.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SendEventNotificationMessageController extends MultiActionController {
    /** Logger for this class and subclasses */
    private static final Logger LOG = Logger
	    .getLogger(SendEventNotificationMessageController.class);

    private static final String NONE_CHANNEL = "___NONE___";
    private static final long REASONABLE_IMMEDIATE_TIME_THRESHOLD = 1000 * 60 * 5; // <= 5 minutes is "immediate"

    /**
     * Returns whether the specified time is considered "in the future", based on some reasonable threshold
     * @param time the time to test
     * @return whether the specified time is considered "in the future", based on some reasonable threshold
     */
    private boolean timeIsInTheFuture(long time) {
        boolean future = (time - System.currentTimeMillis()) > REASONABLE_IMMEDIATE_TIME_THRESHOLD;
        LOG.info("Time: " + new Date(time) + " is in the future? " + future);
        return future;
    }

    /**
     * Returns whether the specified Notification can be reasonably expected to have recipients.
     * This is determined on whether the channel has default recipients, is subscribably, and whether
     * the send date time is far enough in the future to expect that if there are no subscribers, there
     * may actually be some by the time the notification is sent. 
     * @param notification the notification to test
     * @return whether the specified Notification can be reasonably expected to have recipients
     */
    private boolean hasPotentialRecipients(Notification notification) {
        LOG.info("notification channel " + notification.getChannel() + " is subscribable: " + notification.getChannel().isSubscribable());
        return notification.getChannel().getRecipientLists().size() > 0 ||
               notification.getChannel().getSubscriptions().size() > 0 ||
               (notification.getChannel().isSubscribable() && timeIsInTheFuture(notification.getSendDateTime().getTime()));
    }

    protected NotificationService notificationService;

    protected NotificationWorkflowDocumentService notificationWorkflowDocService;

    protected NotificationChannelService notificationChannelService;

    protected NotificationRecipientService notificationRecipientService;

    protected NotificationMessageContentService messageContentService;

    protected BusinessObjectDao businessObjectDao;

    /**
     * Set the NotificationService
     * @param notificationService
     */
    public void setNotificationService(NotificationService notificationService) {
	this.notificationService = notificationService;
    }

    /**
     * This method sets the NotificationWorkflowDocumentService
     * @param s
     */
    public void setNotificationWorkflowDocumentService(
	    NotificationWorkflowDocumentService s) {
	this.notificationWorkflowDocService = s;
    }

    /**
     * Sets the notificationChannelService attribute value.
     * @param notificationChannelService The notificationChannelService to set.
     */
    public void setNotificationChannelService(
	    NotificationChannelService notificationChannelService) {
	this.notificationChannelService = notificationChannelService;
    }

    /**
     * Sets the notificationRecipientService attribute value.
     * @param notificationRecipientService
     */
    public void setNotificationRecipientService(
	    NotificationRecipientService notificationRecipientService) {
	this.notificationRecipientService = notificationRecipientService;
    }

    /**
     * Sets the messageContentService attribute value.
     * @param messageContentService
     */
    public void setMessageContentService(
	    NotificationMessageContentService notificationMessageContentService) {
	this.messageContentService = notificationMessageContentService;
    }

    /**
     * Sets the businessObjectDao attribute value.
     * @param businessObjectDao The businessObjectDao to set.
     */
    public void setBusinessObjectDao(BusinessObjectDao businessObjectDao) {
	this.businessObjectDao = businessObjectDao;
    }

    /**
     * Handles the display of the form for sending an event notification message
     * @param request : a servlet request
     * @param response : a servlet response
     * @throws ServletException : an exception
     * @throws IOException : an exception
     * @return a ModelAndView object
     */
    public ModelAndView sendEventNotificationMessage(
	    HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	String view = "SendEventNotificationMessage";
	LOG.debug("remoteUser: " + request.getRemoteUser());

	Map<String, Object> model = setupModelForSendEventNotification(request);
	model.put("errors", new ErrorList()); // need an empty one so we don't have an NPE

	return new ModelAndView(view, model);
    }

    /**
     * This method prepares the model used for the send event notification message form.
     * @param request
     * @return Map<String, Object>
     */
    private Map<String, Object> setupModelForSendEventNotification(
	    HttpServletRequest request) {
	Map<String, Object> model = new HashMap<String, Object>();
	model.put("defaultSender", notificationRecipientService
		.getUserDisplayName(request.getRemoteUser()));
	model.put("channels", notificationChannelService
		.getAllNotificationChannels());
	model.put("priorities", businessObjectDao
		.findAll(NotificationPriority.class));
        // set sendDateTime to current datetime if not provided
	String sendDateTime = request.getParameter("sendDateTime");
	String currentDateTime = Util.getCurrentDateTime();
	if (StringUtils.isEmpty(sendDateTime)) {	    
	    sendDateTime = currentDateTime;
	}
	model.put("sendDateTime", sendDateTime); 
	
	// retain the original date time or set to current if
	// it was not in the request
	if (request.getParameter("originalDateTime") == null) {
	   model.put("originalDateTime", currentDateTime);
	} else {
	   model.put("originalDateTime", request.getParameter("originalDateTime"));
	}
	
        // set user and workgroup recipients in case of error
	// unfortunately we have to copy the lists into maps so that jstl
	// can test if the map contains a named key
	String[] userRecipients = request.getParameterValues("userRecipients");
	if (userRecipients != null && userRecipients.length > 0) {
	    HashMap<String, String> userRecipientsSelected = new HashMap<String, String>();
	    for (int i=0; i< userRecipients.length ; i++) {
		userRecipientsSelected.put(userRecipients[i], userRecipients[i]);
	    }
	    model.put("userRecipientsSelected", userRecipientsSelected);
	}
	 
	String[] workgroupRecipients = request.getParameterValues("workgroupRecipients");
	if (workgroupRecipients != null && workgroupRecipients.length > 0) {
	    HashMap<String, String> workgroupRecipientsSelected = new HashMap<String, String>();
	    for (int i=0; i< workgroupRecipients.length ; i++) {
		workgroupRecipientsSelected.put(workgroupRecipients[i], workgroupRecipients[i]);
	    }
	    model.put("workgroupRecipientsSelected", workgroupRecipientsSelected);
	}
	
        model.put("summary", request.getParameter("summary"));
        model.put("description", request.getParameter("description"));
        model.put("location", request.getParameter("location"));
        model.put("startDateTime", request.getParameter("startDateTime"));
        model.put("stopDateTime", request.getParameter("stopDateTime"));
        
	model.put("allUsers", notificationRecipientService.getAllUsers());
	model.put("allGroups", notificationRecipientService.getAllGroups());
	return model;
    }

    /**
     * This method handles submitting the actual event notification message.
     * @param request
     * @param response
     * @return ModelAndView
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView submitEventNotificationMessage(
	    HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	LOG.debug("remoteUser: " + request.getRemoteUser());

	// obtain a workflow user object first
	WorkflowIdVO initiator = new WorkflowIdVO(request.getRemoteUser());

	// now construct the workflow document, which will interact with workflow
	NotificationWorkflowDocument document;
	Map<String, Object> model = new HashMap<String, Object>();
        String view;
	try {
	    document = new NotificationWorkflowDocument(
		    initiator,
		    NotificationConstants.KEW_CONSTANTS.SEND_NOTIFICATION_REQ_DOC_TYPE);

	    //parse out the application content into a Notification BO
	    Notification notification = populateNotificationInstance(request, model);

	    // now get that content in an understandable XML format and pass into document
	    String notificationAsXml = messageContentService
		    .generateNotificationMessage(notification);

            Map<String, String> attrFields = new HashMap<String,String>();
            List<NotificationChannelReviewer> reviewers = notification.getChannel().getReviewers();
            int ui = 0;
            int gi = 0;
            for (NotificationChannelReviewer reviewer: reviewers) {
                String prefix;
                int index;
                if (NotificationConstants.RECIPIENT_TYPES.USER.equals(reviewer.getReviewerType())) {
                    prefix = "user";
                    index = ui;
                    ui++;
                } else if (NotificationConstants.RECIPIENT_TYPES.GROUP.equals(reviewer.getReviewerType())) {
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
            document.getDocumentContent().setApplicationContent(notificationAsXml);
            document.getDocumentContent().setAttributeContent("<attributeContent>" + gac.generateContent(attrFields) + "</attributeContent>");
	    
            document.setTitle(notification.getTitle());

	    document.routeDocument("This message was submitted via the event notification message submission form by user "
			    + initiator.getWorkflowId());

	    view = "HomePage";
	} catch (ErrorList el) {
	    // route back to the send form again
	    Map<String, Object> model2 = setupModelForSendEventNotification(request);
	    model.putAll(model2);
	    model.put("errors", el);

	    view = "SendEventNotificationMessage";
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}

	return new ModelAndView(view, model);
    }

    /**
     * This method creates a new Notification instance from the event form values.
     * @param request
     * @param model
     * @return Notification
     * @throws IllegalArgumentException
     */
    private Notification populateNotificationInstance(
	    HttpServletRequest request, Map<String, Object> model)
	    throws IllegalArgumentException, ErrorList {
	ErrorList errors = new ErrorList();

	Notification notification = new Notification();

	// grab data from form
	// channel name
	String channelName = request.getParameter("channelName");
        if (StringUtils.isEmpty(channelName) || StringUtils.equals(channelName, NONE_CHANNEL)) {
	    errors.addError("You must choose a channel.");
	} else {
	    model.put("channelName", channelName);
	}

	// priority name
	String priorityName = request.getParameter("priorityName");
	if (StringUtils.isEmpty(priorityName)) {
	    errors.addError("You must choose a priority.");
	} else {
	    model.put("priorityName", priorityName);
	}

	// sender names
	String senderNames = request.getParameter("senderNames");
	String[] senders = null;
	if (StringUtils.isEmpty(senderNames)) {
	    errors.addError("You must enter at least one sender.");
	} else {
	    senders = StringUtils.split(senderNames, ",");

	    model.put("senderNames", senderNames);
	}

	// delivery type
	String deliveryType = request.getParameter("deliveryType");
	if (StringUtils.isEmpty(deliveryType)) {
	    errors.addError("You must choose a type.");
	} else {
	    if (deliveryType
		    .equalsIgnoreCase(NotificationConstants.DELIVERY_TYPES.FYI)) {
		deliveryType = NotificationConstants.DELIVERY_TYPES.FYI;
	    } else {
		deliveryType = NotificationConstants.DELIVERY_TYPES.ACK;
	    }
	    model.put("deliveryType", deliveryType);
	}

	//get datetime when form was initially rendered
	String originalDateTime = request.getParameter("originalDateTime");
	Date origdate = null;
	Date senddate = null;
	Date removedate = null;
	try {
            origdate = Util.parseUIDateTime(originalDateTime);
        } catch (ParseException pe) {
            errors.addError("Original date is invalid.");
        }
	// send date time
	String sendDateTime = request.getParameter("sendDateTime");
	if (StringUtils.isBlank(sendDateTime)) {	    
	    sendDateTime = Util.getCurrentDateTime();	    
	}
	
	try {
            senddate = Util.parseUIDateTime(sendDateTime);
        } catch (ParseException pe) {
            errors.addError("You specified an invalid Send Date/Time.  Please use the calendar picker.");
        }
        
        if(senddate != null && senddate.before(origdate)) {
            errors.addError("Send Date/Time cannot be in the past.");
        }
        
        model.put("sendDateTime", sendDateTime);    
	
	// auto remove date time
	String autoRemoveDateTime = request.getParameter("autoRemoveDateTime");
	if (StringUtils.isNotBlank(autoRemoveDateTime)) {
	    try {
                removedate = Util.parseUIDateTime(autoRemoveDateTime);
            } catch (ParseException pe) {
                errors.addError("You specified an invalid Auto-Remove Date/Time.  Please use the calendar picker.");
            }
            
            if(removedate != null) {
        	if(removedate.before(origdate)) {
        	    errors.addError("Auto-Remove Date/Time cannot be in the past.");
        	} else if (senddate != null && removedate.before(senddate)){
        	    errors.addError("Auto-Remove Date/Time cannot be before the Send Date/Time.");
        	}
            }
	}
	
	model.put("autoRemoveDateTime", autoRemoveDateTime);
	
	// user recipient names
	String[] userRecipients = request.getParameterValues("userRecipients");

	// workgroup recipient names
	String[] workgroupRecipients = request.getParameterValues("workgroupRecipients");

	// title
        String title = request.getParameter("title");
        if (!StringUtils.isEmpty(title)) {
            model.put("title", title);
        } else {
            errors.addError("You must fill in a title");
        }

	// message
	String message = request.getParameter("message");
	if (StringUtils.isEmpty(message)) {
	    errors.addError("You must fill in a message.");
	} else {
	    model.put("message", message);
	}

        // all event fields are mandatory for event type
        
	// start date time
        String startDateTime = request.getParameter("startDateTime");
        if (StringUtils.isEmpty(startDateTime)) {
            errors.addError("You must fill in a start date/time.");
        } else {
            model.put("startDateTime", startDateTime);
        }

        // stop date time
        String stopDateTime = request.getParameter("stopDateTime");
        if (StringUtils.isEmpty(stopDateTime)) {
            errors.addError("You must fill in a stop date/time.");
        } else {
            model.put("stopDateTime", stopDateTime);
        }

        // summary
        String summary = request.getParameter("summary");
        if (StringUtils.isEmpty(summary)) {
            errors.addError("You must fill in a summary.");
        } else {
            model.put("summary", summary);
        }
        
        // description
        String description = request.getParameter("description");
        if (StringUtils.isEmpty(description)) {
            errors.addError("You must fill in a description.");
        } else {
            model.put("description", description);
        }

        // location
        String location = request.getParameter("location");
        if (StringUtils.isEmpty(location)) {
            errors.addError("You must fill in a location.");
        } else {
            model.put("location", location);
        }

	// stop processing if there are errors
	if (errors.getErrors().size() > 0) {
	    throw errors;
	}

	// now populate the notification BO instance
	NotificationChannel channel = Util.retrieveFieldReference("channel",
		"name", channelName, NotificationChannel.class,
		businessObjectDao);
	notification.setChannel(channel);

	NotificationPriority priority = Util.retrieveFieldReference("priority",
		"name", priorityName, NotificationPriority.class,
		businessObjectDao);
	notification.setPriority(priority);

	NotificationContentType contentType = Util.retrieveFieldReference(
		"contentType", "name",
		NotificationConstants.CONTENT_TYPES.EVENT_CONTENT_TYPE,
		NotificationContentType.class, businessObjectDao);
	notification.setContentType(contentType);

	NotificationProducer producer = Util
		.retrieveFieldReference(
			"producer",
			"name",
			NotificationConstants.KEW_CONSTANTS.NOTIFICATION_SYSTEM_USER_NAME,
			NotificationProducer.class, businessObjectDao);
	notification.setProducer(producer);

	for (String senderName : senders) {
	    if (StringUtils.isEmpty(senderName)) {
		errors.addError("A sender's name cannot be blank.");
	    } else {
		NotificationSender ns = new NotificationSender();
		ns.setSenderName(senderName.trim());
		notification.addSender(ns);
	    }
	}

	boolean recipientsExist = false;

	if (userRecipients != null && userRecipients.length > 0) {
	    recipientsExist = true;
	    for (String userRecipientId : userRecipients) {
		NotificationRecipient recipient = new NotificationRecipient();
		recipient
			.setRecipientType(NotificationConstants.RECIPIENT_TYPES.USER);
		recipient.setRecipientId(userRecipientId);
		notification.addRecipient(recipient);
	    }
	}

	if (workgroupRecipients != null && workgroupRecipients.length > 0) {
	    recipientsExist = true;
	    for (String workgroupRecipientId : workgroupRecipients) {
		NotificationRecipient recipient = new NotificationRecipient();
		recipient
			.setRecipientType(NotificationConstants.RECIPIENT_TYPES.GROUP);
		recipient.setRecipientId(workgroupRecipientId);
		notification.addRecipient(recipient);
	    }
	}

	// check to see if there were any errors
	if (errors.getErrors().size() > 0) {
	    throw errors;
	}

        notification.setTitle(title);

	notification.setDeliveryType(deliveryType);

        Date startDate = null;
        Date stopDate = null;
	// simpledateformat is not threadsafe, have to sync and validate
        Date d = null;
        if (StringUtils.isNotBlank(sendDateTime)) {
            try {
                d = Util.parseUIDateTime(sendDateTime);
            } catch (ParseException pe) {
                errors.addError("You specified an invalid send date and time.  Please use the calendar picker.");
            }
            notification.setSendDateTime(new Timestamp(d.getTime()));
        }   

        Date d2 = null;
        if (StringUtils.isNotBlank(autoRemoveDateTime)) {
            try {
                d2 = Util.parseUIDateTime(autoRemoveDateTime);
                if (d2.before(d)) {
                    errors.addError("Auto Remove Date/Time cannot be before Send Date/Time.");
                }
            } catch (ParseException pe) {
                errors.addError("You specified an invalid auto-remove date and time.  Please use the calendar picker.");
            }
            notification.setAutoRemoveDateTime(new Timestamp(d2.getTime()));
        }

        if (StringUtils.isNotBlank(startDateTime)) {
            try {
                startDate = Util.parseUIDateTime(startDateTime);
            } catch (ParseException pe) {
                errors.addError("You specified an invalid start date and time.  Please use the calendar picker.");
            }
        }

        if (StringUtils.isNotBlank(stopDateTime)) {
            try {
                stopDate = Util.parseUIDateTime(stopDateTime);
            } catch (ParseException pe) {
                errors.addError("You specified an invalid stop date and time.  Please use the calendar picker.");
            }
        }

        if(stopDate != null && startDate != null) {
            if (stopDate.before(startDate)) {
                errors.addError("Event Stop Date/Time cannot be before Event Start Date/Time.");
            }
        }

        if (!recipientsExist && !hasPotentialRecipients(notification)) {
            errors.addError("You must specify at least one user or group recipient.");
        }

	// check to see if there were any errors
	if (errors.getErrors().size() > 0) {
	    throw errors;
	}

	notification
		.setContent(NotificationConstants.XML_MESSAGE_CONSTANTS.CONTENT_EVENT_OPEN
			+ NotificationConstants.XML_MESSAGE_CONSTANTS.MESSAGE_OPEN
			+ message
			+ NotificationConstants.XML_MESSAGE_CONSTANTS.MESSAGE_CLOSE
                        + "<event>\n"
                        + "  <summary>" + summary + "</summary>\n"
                        + "  <description>" + description + "</description>\n"
                        + "  <location>" + location + "</location>\n"
                        + "  <startDateTime>" + Util.toXSDDateTimeString(startDate) + "</startDateTime>\n"
                        + "  <stopDateTime>" + Util.toXSDDateTimeString(stopDate) + "</stopDateTime>\n"
                        + "</event>"
			+ NotificationConstants.XML_MESSAGE_CONSTANTS.CONTENT_CLOSE);

	return notification;
    }
}