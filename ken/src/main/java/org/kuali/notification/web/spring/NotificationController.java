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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.kuali.notification.bo.Notification;
import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.bo.NotificationRecipient;
import org.kuali.notification.bo.NotificationSender;
import org.kuali.notification.service.NotificationMessageDeliveryService;
import org.kuali.notification.service.NotificationService;
import org.kuali.notification.service.NotificationWorkflowDocumentService;
import org.kuali.notification.util.NotificationConstants;
import org.kuali.notification.util.Util;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;

import edu.iu.uis.eden.clientapp.IDocHandler;

/**
 * This class is the controller for the basic notification related actions - viewing, etc.
 * @author John Fereira - jaf30 at cornell dot edu
 * @author Aaron Godert - ag266 at cornell dot edu
 */
public class NotificationController extends MultiActionController {
    /** Logger for this class and subclasses */
    private static final Logger LOG = Logger.getLogger(NotificationController.class);
    
    protected NotificationService notificationService;
    protected NotificationWorkflowDocumentService notificationWorkflowDocService;
    protected NotificationMessageDeliveryService messageDeliveryService;
   
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
    public void setNotificationWorkflowDocumentService(NotificationWorkflowDocumentService s) {
        this.notificationWorkflowDocService = s;
    }

    /**
     * Sets the messageDeliveryService attribute value.
     * @param messageDeliveryService The messageDeliveryService to set.
     */
    public void setMessageDeliveryService(NotificationMessageDeliveryService messageDeliveryService) {
        this.messageDeliveryService = messageDeliveryService;
    }

    /**
     * Handles the display of the main home page in the system.
     * @param request : a servlet request
     * @param response : a servlet response
     * @throws ServletException : an exception
     * @throws IOException : an exception
     * @return a ModelAndView object
     */   
    public ModelAndView displayHome(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String view = "HomePage";
        LOG.debug("remoteUser: "+request.getRemoteUser());
        Map<String, Object> model = new HashMap<String, Object>(); 
        return new ModelAndView(view, model);
    }
   
    /**
     * This method handles displaying the notifications that an individual sent.
     * @param request
     * @param response
     * @return ModelAndView
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView displayNotificationsSent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String view = "NotificationsSent";
        LOG.debug("remoteUser: "+request.getRemoteUser());
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("userId", request.getRemoteUser());
        return new ModelAndView(view, model);
    }

    /**
     * This method handles displaying the search screen.
     * @param request
     * @param response
     * @return ModelAndView
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView displaySearch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String view = "Search";
        LOG.debug("remoteUser: "+request.getRemoteUser());
        Map<String, Object> model = new HashMap<String, Object>(); 
        return new ModelAndView(view, model);
    }

    /**
     * This method displays the user lookup screen.
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView displayLookupUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String view = "LookupUsers";
        LOG.debug("remoteUser: "+request.getRemoteUser());
        Map<String, Object> model = new HashMap<String, Object>(); 
        return new ModelAndView(view, model);
    }

    /**
     * This method displays the workgroup lookup screen.
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView displayLookupWorkgroups(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String view = "LookupWorkgroups";
        LOG.debug("remoteUser: "+request.getRemoteUser());
        Map<String, Object> model = new HashMap<String, Object>(); 
        return new ModelAndView(view, model);
    }

    /**
     * Logs out a user and redirects to CAS appropriately.
     * @param request : a servlet request
     * @param response : a servlet response
     * @throws ServletException : an exception
     * @throws IOException : an exception
     * @return RedirectView
     */   
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().invalidate();

        Map<String, Object> model = new HashMap<String, Object>(); 
        return new ModelAndView(new RedirectView("/cas/logout"), model);
    }

    /**
     * This controller handles displaying the appropriate notification details for a specific record.
     * @param request : a servlet request
     * @param response : a servlet response
     * @throws ServletException : an exception
     * @throws IOException : an exception
     * @return a ModelAndView object
     */   
    public ModelAndView displayNotificationDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String view = "NotificationDetail"; // default to full view

        String user = request.getRemoteUser();
        String docId = request.getParameter(IDocHandler.ROUTEHEADER_ID_PARAMETER);
        String messageDeliveryId = request.getParameter(NotificationConstants.NOTIFICATION_CONTROLLER_CONSTANTS.MSG_DELIVERY_ID);
        String command = request.getParameter(NotificationConstants.NOTIFICATION_CONTROLLER_CONSTANTS.COMMAND);
        String standaloneWindow = request.getParameter(NotificationConstants.NOTIFICATION_CONTROLLER_CONSTANTS.STANDALONE_WINDOW);

        /**
         * We can get the NotificationMessageDelivery object given a workflow ID or a NotificationMessageDelivery
         * Id.  This method might be called either from a workflow action list or
         * as a link from a message deliverer endpoint such as an email message.
         */        
        Notification notification;
        NotificationMessageDelivery messageDelivery;
        boolean actionable = false;
        if (docId != null) {  // this means that the request was triggered via the action list
            LOG.debug("Looking up notification with workflowId: "+docId);
            try {
                messageDelivery = notificationService.getNotificationMessageDeliveryByNotificationDocumentWorkflowDocumentId(user, Long.decode(docId));

                // check to see if this was a standalone window by examining the command from KEW before setting it to INLINE to force an inline view
                if(command != null && 
                        (command.equals(NotificationConstants.NOTIFICATION_DETAIL_VIEWS.NORMAL_VIEW) || 
                                command.equals(NotificationConstants.NOTIFICATION_DETAIL_VIEWS.DOC_SEARCH_VIEW))) {
                    standaloneWindow = "true";
                }

                // we want all messages from the action list in line
                command = NotificationConstants.NOTIFICATION_DETAIL_VIEWS.INLINE;
            } catch (Exception e) {
                LOG.error("Could not get notification with workflowId.");
                LOG.error(e);
                throw new RuntimeException(e);
            }
        } else if (messageDeliveryId != null) { // this means that the request came in not from the action list, but rather from a delivery end point
            LOG.debug("Looking up notification with messageDeliveryId: "+messageDeliveryId);
            try {
                messageDelivery = messageDeliveryService.getNotificationMessageDelivery(new Long(messageDeliveryId));

            } catch (Exception e) {
                LOG.error("Could not get notification with notificationId.");
                LOG.error(e);
                throw new RuntimeException(e);
            } 
        } else {
            throw new RuntimeException("missing document workflow ID or notificationId");
        }

        // now get the notification from the message delivery object
        notification = messageDelivery.getNotification();

        actionable = user.equals(messageDelivery.getUserRecipientId()) && NotificationConstants.MESSAGE_DELIVERY_STATUS.DELIVERED.equals(messageDelivery.getMessageDeliveryStatus());

        List<NotificationSender> senders = notification.getSenders();
        List<NotificationRecipient> recipients = notification.getRecipients();

        String contenthtml = Util.transformContent(notification);

        // check to see if the details need to be rendered in line (no stuff around them)
        if (command != null && command.equals(NotificationConstants.NOTIFICATION_DETAIL_VIEWS.INLINE)) {
            view = "NotificationDetailInline";   
        } 

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("notification", notification);
        model.put("senders", senders);
        model.put("recipients", recipients);
        model.put("contenthtml", contenthtml);
        model.put("docId", docId);
        model.put("command", command);
        model.put("actionable", actionable);
        model.put(NotificationConstants.NOTIFICATION_CONTROLLER_CONSTANTS.STANDALONE_WINDOW, standaloneWindow);
        return new ModelAndView(view, model);
    }

    /**
     * This method takes an action on the message delivery - dismisses it with the action/cause that comes from the
     * UI layer
     * @param action the action or cause of the dismissal
     * @param message the message to display to the user
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @return an appropriate ModelAndView
     */
    private ModelAndView takeActionOnNotification(String action, String message, HttpServletRequest request, HttpServletResponse response) {
        String view = "NotificationDetail";

        String user = request.getRemoteUser();
        String docId = request.getParameter(IDocHandler.ROUTEHEADER_ID_PARAMETER);
        String command = request.getParameter(NotificationConstants.NOTIFICATION_CONTROLLER_CONSTANTS.COMMAND);
        String standaloneWindow = request.getParameter(NotificationConstants.NOTIFICATION_CONTROLLER_CONSTANTS.STANDALONE_WINDOW);

        if (docId == null) {
            throw new RuntimeException("A null docId was provided.");
        }

        LOG.debug("docId: "+docId);
        LOG.debug("command: "+command);

        /**
         * We can get the notification object given a workflow ID or a notification
         * Id.  This method might be called either from a workflow action list or
         * as a link from a message deliverer endpoint such as an email message.  
         */        
        NotificationMessageDelivery delivery;
        Notification notification;
        if (docId != null) {
            LOG.debug("Looking up notification with workflowId: "+docId);
            try {
                delivery = notificationService.getNotificationMessageDeliveryByNotificationDocumentWorkflowDocumentId(user, Long.decode(docId));
                notification = delivery.getNotification();
            } catch (Exception e) {
                LOG.error("Could not get notification with workflowId.");
                LOG.error(e); 
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("A null docId was provided.");
        }           

        /*
         * dismiss the message delivery
         */
        notificationService.dismissNotificationMessageDelivery(delivery.getId(), user, action);

        List<NotificationSender> senders = notification.getSenders();
        List<NotificationRecipient> recipients = notification.getRecipients();

        String contenthtml = Util.transformContent(notification);       

        // first check to see if this is a standalone window, b/c if it is, we'll want to close
        if(standaloneWindow != null && standaloneWindow.equals("true")) {
            view = "NotificationActionTakenCloseWindow";
        } else { // otherwise check to see if the details need to be rendered in line (no stuff around them)
            if (command != null && command.equals(NotificationConstants.NOTIFICATION_DETAIL_VIEWS.INLINE)) { 
                view = "NotificationDetailInline";   
            }
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("notification", notification);
        model.put("ackmessage", message);
        model.put("senders", senders);
        model.put("recipients", recipients);
        model.put("contenthtml", contenthtml);
        model.put("docId", docId);
        model.put("command", command);
        model.put(NotificationConstants.NOTIFICATION_CONTROLLER_CONSTANTS.STANDALONE_WINDOW, standaloneWindow);
        return new ModelAndView(view, model);
    }

    /**
     * This method controls a user acknowledging a notification.
     * @param request : a servlet request
     * @param response : a servlet response
     * @return a ModelAndView object
     */   
    public ModelAndView ackNotification(HttpServletRequest request, HttpServletResponse response) {
        return takeActionOnNotification("ack", "Notificaton acknowledged.  Please refresh your action list.", request, response);
    }

    /**
     * This method controls a user acknowledging a notification.
     * @param request : a servlet request
     * @param response : a servlet response
     * @return a ModelAndView object
     */   
    public ModelAndView fyiNotification(HttpServletRequest request, HttpServletResponse response) {
        return takeActionOnNotification("fyi", "Action Taken.  Please refresh your action list.", request, response);
    }
}