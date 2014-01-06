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
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.coreservice.api.namespace.Namespace;
import org.kuali.rice.coreservice.api.namespace.NamespaceService;
import org.kuali.rice.ken.bo.NotificationBo;
import org.kuali.rice.ken.bo.NotificationChannelReviewerBo;
import org.kuali.rice.ken.core.GlobalNotificationServiceLocator;
import org.kuali.rice.ken.core.NotificationServiceLocator;
import org.kuali.rice.ken.core.SpringNotificationServiceLocator;
import org.kuali.rice.ken.document.kew.NotificationWorkflowDocument;
import org.kuali.rice.ken.exception.ErrorList;
import org.kuali.rice.ken.service.NotificationMessageContentService;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.rule.GenericAttributeContent;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.coreservice.api.CoreServiceApiServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
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
    
    private static IdentityService identityService;
    private static GroupService groupService;
    private static NamespaceService namespaceService;
    private static NotificationMessageContentService notificationMessageContentService;

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

    protected static NotificationMessageContentService getNotificationMessageContentService() {
        if ( notificationMessageContentService == null ) {
            notificationMessageContentService = GlobalNotificationServiceLocator.getInstance().getNotificationMessageContentService();
        }
        return notificationMessageContentService;
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
        String notificationAsXml = getNotificationMessageContentService().generateNotificationMessage(notification);

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
     * Creates a new Notification instance.
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
        return new NotificationBo();
    }

    /**
     * Prepares the model used for sending the notification.
     *
     * @param request the servlet request
     *
     * @return the Spring MVC model
     */
    protected Map<String, Object> setupModelForSendNotification(HttpServletRequest request) {
        return new HashMap<String, Object>();
    }
}
