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
package org.kuali.rice.kew.superuser.web;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ArrayUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.util.JSTLConstants;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.service.WorkflowDocumentActions;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.AppSpecificRouteRecipient;
import org.kuali.rice.kew.web.KewKualiAction;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.util.GlobalVariables;


/**
 * A Struts Action which provides super user functionality.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SuperUserAction extends KewKualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SuperUserAction.class);
    public static final String UNAUTHORIZED = "authorizationFailure";

    //public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    //	defaultDispatch(mapping, form, request, response);
    //}

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        initForm(request, form);
        return super.execute(mapping, form, request, response);
    }

    public ActionForward displaySuperUserDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SuperUserForm superUserForm = (SuperUserForm) form;
        superUserForm.setDocHandlerUrl(KEWConstants.DOC_HANDLER_REDIRECT_PAGE + "?docId=" + superUserForm.getRouteHeaderId() + "&" + KEWConstants.COMMAND_PARAMETER + "=" + KEWConstants.SUPERUSER_COMMAND);
        return defaultDispatch(mapping, form, request, response);
    }

    public ActionForward routeLevelApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering routeLevelApprove()...");
        SuperUserForm superUserForm = (SuperUserForm) form;
        Long documentId = superUserForm.getRouteHeader().getRouteHeaderId();
        WorkflowDocumentActions documentActions = getWorkflowDocumentActions(documentId);
        documentActions.superUserNodeApproveAction(getUserSession(request).getPrincipalId(), documentId, superUserForm.getDestNodeName(), superUserForm.getAnnotation(), superUserForm.isRunPostProcessorLogic());
        saveDocumentMessage("general.routing.superuser.routeLevelApproved", request, superUserForm.getRouteHeaderIdString(), null);
        LOG.info("exiting routeLevelApprove()...");
        superUserForm.getActionRequests().clear();
        initForm(request, form);
        return defaultDispatch(mapping, form, request, response);
    }

    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering approve() ...");
        SuperUserForm superUserForm = (SuperUserForm) form;
        Long documentId = superUserForm.getRouteHeader().getRouteHeaderId();
        WorkflowDocumentActions documentActions = getWorkflowDocumentActions(documentId);
        documentActions.superUserApprove(getUserSession(request).getPrincipalId(), DTOConverter.convertRouteHeader(superUserForm.getRouteHeader(), null), superUserForm.getAnnotation(), superUserForm.isRunPostProcessorLogic());
        saveDocumentMessage("general.routing.superuser.approved", request, superUserForm.getRouteHeaderIdString(), null);
        LOG.info("exiting approve() ...");
        superUserForm.getActionRequests().clear();
        initForm(request, form);
        return defaultDispatch(mapping, form, request, response);
    }

    public ActionForward disapprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering disapprove() ...");
        SuperUserForm superUserForm = (SuperUserForm) form;
        Long documentId = superUserForm.getRouteHeader().getRouteHeaderId();
        WorkflowDocumentActions documentActions = getWorkflowDocumentActions(documentId);
        documentActions.superUserDisapprove(getUserSession(request).getPrincipalId(), DTOConverter.convertRouteHeader(superUserForm.getRouteHeader(), null), superUserForm.getAnnotation(), superUserForm.isRunPostProcessorLogic());
        saveDocumentMessage("general.routing.superuser.disapproved", request, superUserForm.getRouteHeaderIdString(), null);
        LOG.info("exiting disapprove() ...");
        superUserForm.getActionRequests().clear();
        initForm(request, form);
        return defaultDispatch(mapping, form, request, response);
    }

    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering cancel() ...");
        SuperUserForm superUserForm = (SuperUserForm) form;
        Long documentId = superUserForm.getRouteHeader().getRouteHeaderId();
        WorkflowDocumentActions documentActions = getWorkflowDocumentActions(documentId);
        documentActions.superUserCancel(getUserSession(request).getPrincipalId(), DTOConverter.convertRouteHeader(superUserForm.getRouteHeader(), null), superUserForm.getAnnotation(), superUserForm.isRunPostProcessorLogic());
        saveDocumentMessage("general.routing.superuser.canceled", request, superUserForm.getRouteHeaderIdString(), null);
        LOG.info("exiting cancel() ...");
        superUserForm.getActionRequests().clear();
        initForm(request, form);
        return defaultDispatch(mapping, form, request, response);
    }

    public ActionForward returnToPreviousNode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering returnToPreviousNode() ...");
        SuperUserForm superUserForm = (SuperUserForm) form;
        Long documentId = superUserForm.getRouteHeader().getRouteHeaderId();
        WorkflowDocumentActions documentActions = getWorkflowDocumentActions(documentId);
        documentActions.superUserReturnToPreviousNode(getUserSession(request).getPrincipalId(), documentId, superUserForm.getReturnDestNodeName(), superUserForm.getAnnotation(), superUserForm.isRunPostProcessorLogic());
        saveDocumentMessage("general.routing.returnedToPreviousNode", request, "document", superUserForm.getReturnDestNodeName().toString());
        LOG.info("exiting returnToPreviousRouteLevel() ...");
        superUserForm.getActionRequests().clear();
        initForm(request, form);
        return defaultDispatch(mapping, form, request, response);
    }

    public ActionForward test(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering test() ...");
        SuperUserForm superUserForm = (SuperUserForm) form;
        LOG.info("Value of runPostProcessorLogic: " + superUserForm.isRunPostProcessorLogic());
        if (superUserForm.getActionRequestRunPostProcessorCheck() != null) {
            for (int i = 0; i < superUserForm.getActionRequestRunPostProcessorCheck().length; i++) {
                String actionRequestId = superUserForm.getActionRequestRunPostProcessorCheck()[i];
                LOG.info("Action Request with id " + actionRequestId + " is checked for post process actions");
            }
        } else {
            LOG.info("Action request checkbox array is null");
        }
        superUserForm.getActionRequests().clear();
        initForm(request, form);
        return defaultDispatch(mapping, form, request, response);
    }

    public ActionForward actionRequestApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering actionRequestApprove() ...");
        SuperUserForm superUserForm = (SuperUserForm) form;
        LOG.debug("Routing super user action request approve action");
        boolean runPostProcessorLogic = ArrayUtils.contains(superUserForm.getActionRequestRunPostProcessorCheck(), superUserForm.getActionTakenActionRequestId());
        Long documentId = superUserForm.getRouteHeader().getRouteHeaderId();
        WorkflowDocumentActions documentActions = getWorkflowDocumentActions(documentId);
        documentActions.superUserActionRequestApproveAction(getUserSession(request).getPrincipalId(), documentId, new Long(superUserForm.getActionTakenActionRequestId()), superUserForm.getAnnotation(), runPostProcessorLogic);
        String messageString;
        String actionReqest = (String) request.getParameter("buttonClick");
        if (actionReqest.equalsIgnoreCase("acknowlege")){
        	messageString = "general.routing.superuser.actionRequestAcknowleged";
        }else if (actionReqest.equalsIgnoreCase("FYI")){
        	messageString = "general.routing.superuser.actionRequestFYI";
        }else if (actionReqest.equalsIgnoreCase("complete")){
        	messageString = "general.routing.superuser.actionRequestCompleted";
        }else if (actionReqest.equalsIgnoreCase("approved")){
        	messageString = "general.routing.superuser.actionRequestApproved";
        }else {
        	messageString = "general.routing.superuser.actionRequestApproved";
        }
        saveDocumentMessage(messageString, request, superUserForm.getRouteHeaderIdString(), superUserForm.getActionTakenActionRequestId());
        LOG.info("exiting actionRequestApprove() ...");
        superUserForm.getActionRequests().clear();
        initForm(request, form);
        return defaultDispatch(mapping, form, request, response);
    }

    public ActionForward initForm(HttpServletRequest request, ActionForm form) throws Exception {
        request.setAttribute("Constants", new JSTLConstants(KEWConstants.class));
        SuperUserForm superUserForm = (SuperUserForm) form;
        DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(superUserForm.getRouteHeaderId());
        superUserForm.setRouteHeader(routeHeader);
        String principalId = getUserSession(request).getPrincipalId();
        boolean isAuthorized = KEWServiceLocator.getDocumentTypePermissionService().canAdministerRouting(principalId, routeHeader.getDocumentType());
        superUserForm.setAuthorized(isAuthorized);
        if (!isAuthorized) {
            saveDocumentMessage("general.routing.superuser.notAuthorized", request, superUserForm.getRouteHeaderIdString(), null);
            return null;
        }

        superUserForm.setFutureNodeNames(KEWServiceLocator.getRouteNodeService().findFutureNodeNames(routeHeader.getRouteHeaderId()));


        Collection actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(routeHeader.getRouteHeaderId());
        Iterator requestIterator = actionRequests.iterator();
        while (requestIterator.hasNext()) {
            ActionRequestValue req = (ActionRequestValue) requestIterator.next();
           // if (KEWConstants.ACTION_REQUEST_APPROVE_REQ.equalsIgnoreCase(req.getActionRequested())) {
                superUserForm.getActionRequests().add(req);
           // }
        }


        superUserForm.setDocId(superUserForm.getRouteHeaderId());
        if (superUserForm.getDocId() != null) {
            superUserForm.setWorkflowDocument(new WorkflowDocument(getUserSession(request).getPrincipalId(), superUserForm.getDocId()));
            superUserForm.establishVisibleActionRequestCds();
        }

        return null;
    }

    private void saveDocumentMessage(String messageKey, HttpServletRequest request, String subVariable1, String subVariable2) {
        if (subVariable2 == null) {
            GlobalVariables.getErrorMap().putInfo("document", messageKey, subVariable1);
        } else {
            GlobalVariables.getErrorMap().putInfo("document", messageKey, subVariable1, subVariable2);
        }
    }

    public ActionForward routeToAppSpecificRecipient(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	SuperUserForm superUserForm = (SuperUserForm) form;
    	//super.routeToAppSpecificRecipient(mapping, form, request, response);

    	//WorkflowRoutingForm routingForm = (WorkflowRoutingForm) form;
        AppSpecificRouteRecipient recipient = superUserForm.getAppSpecificRouteRecipient();
        recipient.setActionRequested(superUserForm.getAppSpecificRouteActionRequestCd());
        recipient.setType(superUserForm.getAppSpecificRouteRecipientType());
        validateAppSpecificRoute(recipient);

        try {
            String routeNodeName = getAdHocRouteNodeName(superUserForm.getWorkflowDocument().getRouteHeaderId());
            if (KEWConstants.PERSON.equals(recipient.getType())) {
                String recipientPrincipalId = KEWServiceLocator.getIdentityHelperService().getIdForPrincipalName(recipient.getId());
                superUserForm.getWorkflowDocument().adHocRouteDocumentToPrincipal(recipient.getActionRequested(), routeNodeName, superUserForm.getAnnotation(), recipientPrincipalId, "", true);
            } else {
                superUserForm.getWorkflowDocument().adHocRouteDocumentToGroup(recipient.getActionRequested(), routeNodeName, superUserForm.getAnnotation(), recipient.getId(), "", true);
            }
            superUserForm.getAppSpecificRouteList().add(recipient);
            superUserForm.resetAppSpecificRoute();
        } catch (Exception e) {
            LOG.error("Error generating app specific route request", e);
            throw new WorkflowServiceErrorException("AppSpecific Route Error", new WorkflowServiceErrorImpl("AppSpecific Route Error", "appspecificroute.systemerror"));
        }

    	superUserForm.getActionRequests().clear();
    	initForm(request, form);
        return start(mapping, form, request, response);
    }


    private WorkflowDocumentActions getWorkflowDocumentActions(Long documentId) {
    	DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByDocumentId(documentId);
    	String serviceNamespace = documentType.getServiceNamespace();
    	WorkflowDocumentActions service = (WorkflowDocumentActions)GlobalResourceLoader.getService(new QName(serviceNamespace, "WorkflowDocumentActionsService"));
    	if (service == null) {
    	    service = KEWServiceLocator.getWorkflowDocumentActionsService();
    	}
    	return service;
    }

    protected void validateAppSpecificRoute(AppSpecificRouteRecipient recipient) {
        if (recipient.getId() == null || recipient.getId().trim().equals("")) {
            GlobalVariables.getErrorMap().putError("appSpecificRouteRecipient.id", "appspecificroute.recipient.required");
        }

        if (KEWConstants.PERSON.equals(recipient.getType()) && recipient.getId() != null && !recipient.getId().trim().equals("")) {
            KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(recipient.getId());
            if (principal == null) {
                LOG.error("App Specific user recipient not found");
                GlobalVariables.getErrorMap().putError("appSpecificRouteRecipient.id", "appspecificroute.user.invalid");
            }
        }

        if (KEWConstants.WORKGROUP.equals(recipient.getType()) && recipient.getId() != null && !recipient.getId().trim().equals("")) {
            if (getIdentityManagementService().getGroup(recipient.getId()) == null) {
                GlobalVariables.getErrorMap().putError("appSpecificRouteRecipient.id", "appspecificroute.workgroup.invalid");
            }
        }

        if (GlobalVariables.getErrorMap().hasErrors()) {
            throw new ValidationException("AppSpecific Route validation Errors");
        }

    }

    protected String getAdHocRouteNodeName(Long routeHeaderId) throws WorkflowException {
        WorkflowInfo info = new WorkflowInfo();
        RouteNodeInstanceDTO[] nodeInstances = info.getActiveNodeInstances(routeHeaderId);
        if (nodeInstances == null || nodeInstances.length == 0) {
            nodeInstances = info.getTerminalNodeInstances(routeHeaderId);
        }
        if (nodeInstances == null || nodeInstances.length == 0) {
            throw new WorkflowException("Could not locate a node on the document to send the ad hoc request to.");
        }
        return nodeInstances[0].getName();
    }

    private IdentityManagementService getIdentityManagementService() {
        return (IdentityManagementService) KIMServiceLocator.getService(KIMServiceLocator.KIM_IDENTITY_MANAGEMENT_SERVICE);
    }
    public static UserSession getUserSession(HttpServletRequest request) {
        return UserSession.getAuthenticatedUser();
    }
}