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
package edu.iu.uis.eden.superuser.web;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.clientapp.IDocHandler;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * A Struts Action which provides super user functionality.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SuperUserAction extends WorkflowAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SuperUserAction.class);
    public static final String UNAUTHORIZED = "authorizationFailure";
    //private static String DOCUMENT_TYPE = "EDENSERVICE-DOCS.WKGRPREQ";

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	return mapping.findForward("basic");
    }

    public ActionForward displaySuperUserDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SuperUserForm superUserForm = (SuperUserForm) form;
        superUserForm.setDocHandlerUrl(EdenConstants.DOC_HANDLER_REDIRECT_PAGE + "?docId=" + superUserForm.getRouteHeaderId() + "&" + IDocHandler.COMMAND_PARAMETER + "=" + IDocHandler.SUPERUSER_COMMAND);
        return mapping.findForward("basic");
    }

    public ActionForward routeLevelApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering routeLevelApprove()...");
        SuperUserForm superUserForm = (SuperUserForm) form;
        //SpringServiceLocator.getWorkflowDocumentService().superUserRouteLevelApproveAction(getUserSession(request).getWorkflowUser(), superUserForm.getRouteHeader(), superUserForm.getRouteLevel(), superUserForm.getAnnotation());
        KEWServiceLocator.getWorkflowDocumentService().superUserNodeApproveAction(getUserSession(request).getWorkflowUser(), superUserForm.getRouteHeader().getRouteHeaderId(), superUserForm.getDestNodeName(), superUserForm.getAnnotation());
        saveDocumentActionMessage("general.routing.superuser.routeLevelApproved", request, superUserForm.getRouteHeaderIdString(), null);
        LOG.info("exiting routeLevelApprove()...");
        superUserForm.getActionRequests().clear();
        establishRequiredState(request, form);
        return mapping.findForward("basic");
    }

    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering approve() ...");
        SuperUserForm superUserForm = (SuperUserForm) form;
        KEWServiceLocator.getWorkflowDocumentService().superUserApprove(getUserSession(request).getWorkflowUser(), superUserForm.getRouteHeader(), superUserForm.getAnnotation());
        saveDocumentActionMessage("general.routing.superuser.approved", request, superUserForm.getRouteHeaderIdString(), null);
        LOG.info("exiting approve() ...");
        superUserForm.getActionRequests().clear();
        establishRequiredState(request, form);
        return mapping.findForward("basic");
    }

    public ActionForward disapprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering disapprove() ...");
        SuperUserForm superUserForm = (SuperUserForm) form;
        KEWServiceLocator.getWorkflowDocumentService().superUserDisapproveAction(getUserSession(request).getWorkflowUser(), superUserForm.getRouteHeader(), superUserForm.getAnnotation());
        saveDocumentActionMessage("general.routing.superuser.disapproved", request, superUserForm.getRouteHeaderIdString(), null);
        LOG.info("exiting disapprove() ...");
        superUserForm.getActionRequests().clear();
        establishRequiredState(request, form);
        return mapping.findForward("basic");
    }

    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering cancel() ...");
        SuperUserForm superUserForm = (SuperUserForm) form;
        KEWServiceLocator.getWorkflowDocumentService().superUserCancelAction(getUserSession(request).getWorkflowUser(), superUserForm.getRouteHeader(), superUserForm.getAnnotation());
        saveDocumentActionMessage("general.routing.superuser.canceled", request, superUserForm.getRouteHeaderIdString(), null);
        LOG.info("exiting cancel() ...");
        superUserForm.getActionRequests().clear();
        establishRequiredState(request, form);
        return mapping.findForward("basic");
    }

    public ActionForward returnToPreviousNode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering returnToPreviousNode() ...");
        SuperUserForm superUserForm = (SuperUserForm) form;
        KEWServiceLocator.getWorkflowDocumentService().superUserReturnDocumentToPreviousNode(getUserSession(request).getWorkflowUser(), superUserForm.getRouteHeaderId(), superUserForm.getReturnDestNodeName(), superUserForm.getAnnotation());
        saveDocumentActionMessage("general.routing.returnedToPreviousNode", request, "document", superUserForm.getReturnDestNodeName().toString());
        LOG.info("exiting returnToPreviousRouteLevel() ...");
        superUserForm.getActionRequests().clear();
        establishRequiredState(request, form);
        return mapping.findForward("basic");
    }

    public ActionForward actionRequestApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering actionRequestApprove() ...");
        SuperUserForm superUserForm = (SuperUserForm) form;
        LOG.debug("Routing super user action request approve action");
        KEWServiceLocator.getWorkflowDocumentService().superUserActionRequestApproveAction(getUserSession(request).getWorkflowUser(), superUserForm.getRouteHeader(), new Long(superUserForm.getActionTakenActionRequestId()), superUserForm.getAnnotation());
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
        saveDocumentActionMessage(messageString, request, superUserForm.getRouteHeaderIdString(), superUserForm.getActionTakenActionRequestId());
        LOG.info("exiting actionRequestApprove() ...");
        superUserForm.getActionRequests().clear();
        establishRequiredState(request, form);
        return mapping.findForward("basic");
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        SuperUserForm superUserForm = (SuperUserForm) form;
        DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(superUserForm.getRouteHeaderId());
        superUserForm.setRouteHeader(routeHeader);
        WorkflowUser user = getUserSession(request).getWorkflowUser();
        boolean isAuthorized = routeHeader.getDocumentType().isSuperUser(user);
        superUserForm.setAuthorized(isAuthorized);
        if (!isAuthorized) {
            saveDocumentActionMessage("general.routing.superuser.notAuthorized", request, superUserForm.getRouteHeaderIdString(), null);
            return null;
        }

        superUserForm.setFutureNodeNames(KEWServiceLocator.getRouteNodeService().findFutureNodeNames(routeHeader.getRouteHeaderId()));


        Collection actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(routeHeader.getRouteHeaderId());
        Iterator requestIterator = actionRequests.iterator();
        while (requestIterator.hasNext()) {
            ActionRequestValue req = (ActionRequestValue) requestIterator.next();
           // if (EdenConstants.ACTION_REQUEST_APPROVE_REQ.equalsIgnoreCase(req.getActionRequested())) {
                superUserForm.getActionRequests().add(req);
           // }
        }


        superUserForm.setDocId(superUserForm.getRouteHeaderId());
        if (superUserForm.getDocId() != null) {
            superUserForm.setFlexDoc(new WorkflowDocument(new WorkflowIdVO(getUserSession(request).getWorkflowUser().getWorkflowId()), superUserForm.getDocId()));
            superUserForm.establishVisibleActionRequestCds();
        }

        return null;
    }

    public void saveDocumentActionMessage(String messageKey, HttpServletRequest request, String subVariable1, String subVariable2) {
        ActionMessages messages = new ActionMessages();
        ActionMessage actionMessage = null;
        if (subVariable2 == null) {
            actionMessage = new ActionMessage(messageKey, subVariable1);
        } else {
            actionMessage = new ActionMessage(messageKey, subVariable1, subVariable2);
        }
        messages.add(ActionMessages.GLOBAL_MESSAGE, actionMessage);
        saveMessages(request, messages);
    }

    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SuperUserForm superUserForm = (SuperUserForm) form;

        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + mapping.getModuleConfig().getPrefix();
        StringBuffer lookupUrl = new StringBuffer(basePath);
        lookupUrl.append("/Lookup.do?methodToCall=start&docFormKey=").append(getUserSession(request).addObject(form)).append("&lookupableImplServiceName=");
        lookupUrl.append(request.getParameter("lookupableImplServiceName"));

        String lookupType = superUserForm.getLookupType();
        superUserForm.setLookupType(null);

        if (lookupType != null && !lookupType.equals("")) {
            lookupUrl.append("&conversionFields=");
            WorkflowLookupable workflowLookupable = (WorkflowLookupable) GlobalResourceLoader.getService(request.getParameter("lookupableImplServiceName"));//SpringServiceLocator.getExtensionService().getLookupable(request.getParameter("lookupableImplServiceName"));
            for (Iterator iterator = workflowLookupable.getDefaultReturnType().iterator(); iterator.hasNext();) {
                String returnType = (String) iterator.next();
                lookupUrl.append(returnType).append(":").append(lookupType);
            }
        }

        lookupUrl.append("&returnLocation=").append(basePath).append(mapping.getPath()).append(".do");
        return new ActionForward(lookupUrl.toString(), true);
    }


    public ActionForward routeToAppSpecificRecipient(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	SuperUserForm superUserForm = (SuperUserForm) form;
    	super.routeToAppSpecificRecipient(mapping, form, request, response);
    	superUserForm.getActionRequests().clear();
        establishRequiredState(request, form);
        return start(mapping, form, request, response);
    }
}