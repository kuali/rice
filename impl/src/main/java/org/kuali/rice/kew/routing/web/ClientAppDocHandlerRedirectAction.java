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
package org.kuali.rice.kew.routing.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.kuali.rice.kew.doctype.SecuritySession;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.service.RouteHeaderService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.WorkflowAction;
import org.kuali.rice.kew.web.session.UserSession;


/**
 * A Struts Action for redirecting from the KEW web application to the appropriate
 * Doc Handler for a document.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ClientAppDocHandlerRedirectAction extends WorkflowAction {

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DocHandlerForm docHandlerForm = (DocHandlerForm) form;

        String docHandler = null;

        if (request.getParameter(KEWConstants.ROUTEHEADER_ID_PARAMETER) != null) {
            RouteHeaderService rhSrv = (RouteHeaderService) KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
            DocumentRouteHeaderValue routeHeader = rhSrv.getRouteHeader(docHandlerForm.getDocId());

            if (!KEWServiceLocator.getDocumentSecurityService().routeLogAuthorized(UserSession.getAuthenticatedUser(), routeHeader, new SecuritySession(UserSession.getAuthenticatedUser()))) {
            	return mapping.findForward("NotAuthorized");
            }
            docHandler = routeHeader.getDocumentType().getDocHandlerUrl();
            if (docHandler.indexOf("?") == -1) {
                docHandler += "?";
            } else {
                docHandler += "&";
            }
            docHandler += KEWConstants.ROUTEHEADER_ID_PARAMETER + "=" + docHandlerForm.getDocId();
        } else if (request.getParameter(KEWConstants.DOCTYPE_PARAMETER) != null) {
            DocumentTypeService documentTypeService = (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
            DocumentType documentType = documentTypeService.findByName(docHandlerForm.getDocTypeName());
            docHandler = documentType.getDocHandlerUrl();
            if (docHandler.indexOf("?") == -1) {
                docHandler += "?";
            } else {
                docHandler += "&";
            }
            docHandler += KEWConstants.DOCTYPE_PARAMETER + "=" + docHandlerForm.getDocTypeName();
        } else {
//TODO what should happen here if parms are missing; no proper ActionForward from here
            throw new RuntimeException ("Cannot determine document handler");
        }

        docHandler += "&" + KEWConstants.COMMAND_PARAMETER + "=" + docHandlerForm.getCommand();
        if (getUserSession(request).isBackdoorInUse()) {
            docHandler += "&" + KEWConstants.BACKDOOR_ID_PARAMETER + "=" + getUserSession(request).getPrincipalName();
        }
        return new ActionForward(docHandler, true);
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        return null;
    }
}