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
package edu.iu.uis.eden.routing.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * A Struts Action which handles requests for the Doc Handler for all documents
 * that are part of the core workflow system (i.e. Workgroup, Rules).
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkflowDocHandlerAction extends WorkflowAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowDocHandlerAction.class);

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return null;
    }

    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering blanketApprove() method ...");
        DocHandlerForm docHandlerForm = (DocHandlerForm) form;
        docHandlerForm.getFlexDoc().blanketApprove(docHandlerForm.getAnnotation());
        saveDocumentActionMessage("general.routing.blanketApproved", request);
        LOG.info("forwarding to actionTaken from blanketApprove()");
        return mapping.findForward("actionTaken");
    }

    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering approve() method ...");
        DocHandlerForm docHandlerForm = (DocHandlerForm) form;
        docHandlerForm.getFlexDoc().approve(docHandlerForm.getAnnotation());
        saveDocumentActionMessage("general.routing.approved", request);
        LOG.info("forwarding to actionTaken from approve()");
        return mapping.findForward("actionTaken");
    }

    public ActionForward complete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering complete() method ...");
        DocHandlerForm docHandlerForm = (DocHandlerForm) form;
        docHandlerForm.getFlexDoc().complete(docHandlerForm.getAnnotation());
        saveDocumentActionMessage("general.routing.completed", request);
        LOG.info("forwarding to actionTaken from complete()");
        return mapping.findForward("actionTaken");
    }

    public ActionForward disapprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering disapprove() method ...");
        DocHandlerForm docHandlerForm = (DocHandlerForm) form;
        docHandlerForm.getFlexDoc().disapprove(docHandlerForm.getAnnotation());
        saveDocumentActionMessage("general.routing.disapproved", request);
        LOG.info("forwarding to actionTaken from disapprove()");
        return mapping.findForward("actionTaken");
    }

    /*public ActionForward returnToPreviousRouteLevel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering returnToPreviousRouteLevel method ...");
        DocHandlerForm docHandlerForm = (DocHandlerForm) form;
        docHandlerForm.getFlexDoc().returnToPreviousRouteLevel(docHandlerForm.getAnnotation(), docHandlerForm.getDestRouteLevel());
        saveDocumentActionMessage("general.routing.returnedToPreviousRouteLevel", request, docHandlerForm.getDestRouteLevel().toString());
        LOG.info("forwarding to actionTaken from returnToPreviousRouteLevel()");
        return mapping.findForward("actionTaken");
    }*/

    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering cancel() method ...");
        DocHandlerForm docHandlerForm = (DocHandlerForm) form;
        docHandlerForm.getFlexDoc().cancel(docHandlerForm.getAnnotation());
        saveDocumentActionMessage("general.routing.canceled", request);
        LOG.info("forwarding to actionTaken from cancel()");
        return mapping.findForward("actionTaken");
    }

    public ActionForward fyi(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering fyi() method ...");
        DocHandlerForm docHandlerForm = (DocHandlerForm) form;
        docHandlerForm.getFlexDoc().fyi();
        saveDocumentActionMessage("general.routing.fyied", request);
        LOG.info("forwarding to actionTaken from fyi()");
        return mapping.findForward("actionTaken");
    }

    public ActionForward acknowledge(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering acknowledge() method ...");
        DocHandlerForm docHandlerForm = (DocHandlerForm) form;
        docHandlerForm.getFlexDoc().acknowledge(docHandlerForm.getAnnotation());
        saveDocumentActionMessage("general.routing.acknowledged", request);
        LOG.info("forwarding to actionTaken from acknowledge()");
        return mapping.findForward("actionTaken");
    }

    public void saveDocumentActionMessage(String messageKey, HttpServletRequest request, String secondMessageParameter) {
        ActionMessages messages = new ActionMessages();

        if (Utilities.isEmpty(secondMessageParameter)) {
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(messageKey, "document"));
        } else {
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(messageKey, "document", secondMessageParameter));
        }
        saveMessages(request, messages);
    }

    public void saveDocumentActionMessage(String messageKey, HttpServletRequest request) {
        saveDocumentActionMessage(messageKey, request, null);
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        DocHandlerForm docHandlerForm = (DocHandlerForm) form;
        try {
            WorkflowDocument flexDoc = new WorkflowDocument(new WorkflowIdVO(getUserSession(request).getWorkflowUser().getWorkflowId()), docHandlerForm.getDocId());
            docHandlerForm.setFlexDoc(flexDoc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DocHandlerForm docHandlerForm = (DocHandlerForm) form;
        if(!Utilities.isEmpty(docHandlerForm.getDocHandlerRedirectUrl())){
            StringBuffer url = new StringBuffer(docHandlerForm.getDocHandlerRedirectUrl());
            
            if(request.getParameter("networkId") != null){
                url.append("&networkId=").append(request.getParameter("networkId"));
            } else if(request.getParameter("workgroupId") != null){
                url.append("&workgroupId=").append(request.getParameter("workgroupId"));
            }
            url.append("&appSpecificRouteActionRequestCd=").append(docHandlerForm.getAppSpecificRouteActionRequestCd());
            return new ActionForward(url.toString(), true);  
        } 
        return mapping.findForward("actionTaken");
    }
   
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DocHandlerForm docHandlerForm = (DocHandlerForm) form;
        docHandlerForm.setDocHandlerRedirectUrl(request.getParameter(EdenConstants.DOC_HANDLER_RETURN_URL));
        
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + mapping.getModuleConfig().getPrefix();
        StringBuffer lookupUrl = new StringBuffer(basePath);
        lookupUrl.append("/Lookup.do?methodToCall=start&docFormKey=").append(getUserSession(request).addObject(form)).append("&lookupableImplServiceName=");
        lookupUrl.append(request.getParameter("lookupableImplServiceName"));
        
        lookupUrl.append("&returnLocation=").append(basePath).append(mapping.getPath()).append(".do");
        return new ActionForward(lookupUrl.toString(), true);
    }
}