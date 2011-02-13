/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kns.web.spring.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.ParameterConstants;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.util.SessionTicket;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.spring.form.DocumentFormBase;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class DocumentControllerBase extends UifControllerBase {
    
    private BusinessObjectService businessObjectService;
    private BusinessObjectAuthorizationService businessObjectAuthorizationService;
    private BusinessObjectMetaDataService businessObjectMetaDataService;
    private DataDictionaryService dataDictionaryService;
    private DocumentService documentService;
    private DocumentHelperService documentHelperService;


    @Override
    public abstract DocumentFormBase createInitialForm(HttpServletRequest request);
   
    @RequestMapping(params = "methodToCall=save")
    public ModelAndView save(@ModelAttribute("KualiForm") DocumentFormBase form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        doProcessingAfterPost(form, request);
        
        //get any possible changes to to adHocWorkgroups
        // TODO turn this back on
        //refreshAdHocRoutingWorkgroupLookups(request, form);
        
        Document document = form.getDocument();
        
        String viewName = checkAndWarnAboutSensitiveData(form, request, response, KNSPropertyConstants.DOCUMENT_EXPLANATION,
                                    document.getDocumentHeader().getExplanation(), "save", "");
        // TODO if the happens we may need to save form to session or account for it
        if (viewName != null) {
            return new ModelAndView(viewName);
        }

        // save in workflow
        getDocumentService().saveDocument(document);

        GlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_SAVED);
        form.setAnnotation("");

        return getUIFModelAndView(form);
    }
    
    /**
     * This method does all special processing on a document that should happen on each HTTP post (ie, save, route, approve, etc).
     * 
     * @param form
     * @param request
     */
    protected void doProcessingAfterPost(DocumentFormBase form, HttpServletRequest request) {
        getBusinessObjectService().linkUserFields(form.getDocument());
    }

    // TODO this needs more analysis before porting can finish
    /*
    protected void refreshAdHocRoutingWorkgroupLookups(HttpServletRequest request, DocumentFormBase form) throws WorkflowException {
        for (Enumeration<String> i = request.getParameterNames(); i.hasMoreElements();) {
            String parameterName = i.nextElement();
            
            // TODO does this really belong in the loop
            if (parameterName.equals("newAdHocRouteWorkgroup.recipientName") && !"".equals(request.getParameter(parameterName))) {
                //check for namespace
                String namespace = KimConstants.KIM_GROUP_DEFAULT_NAMESPACE_CODE;
                if (request.getParameter("newAdHocRouteWorkgroup.recipientNamespaceCode") != null &&
                        !"".equals(request.getParameter("newAdHocRouteWorkgroup.recipientName").trim())) {
                    
                    namespace = request.getParameter("newAdHocRouteWorkgroup.recipientNamespaceCode").trim();
                }
                Group group = getIdentityManagementService().getGroupByName(namespace, request.getParameter(parameterName));
                if (group != null) {
                    form.getNewAdHocRouteWorkgroup().setId(group.getGroupId());
                    form.getNewAdHocRouteWorkgroup().setRecipientName(group.getGroupName());
                    form.getNewAdHocRouteWorkgroup().setRecipientNamespaceCode(group.getNamespaceCode());
                } else {
                    throw new RuntimeException("Invalid workgroup id passed as parameter.");
                }
            }
            
            // TODO need to look at screen, will most of this just be bound to the form by spring?
            if (parameterName.startsWith("adHocRouteWorkgroup[") && !"".equals(request.getParameter(parameterName))) {
                if (parameterName.endsWith(".recipientName")) {
                    int lineNumber = Integer.parseInt(StringUtils.substringBetween(parameterName, "[", "]"));
                  //check for namespace
                    String namespaceParam = "adHocRouteWorkgroup[" + lineNumber + "].recipientNamespaceCode";
                    String namespace = KimConstants.KIM_GROUP_DEFAULT_NAMESPACE_CODE;
                    if (request.getParameter(namespaceParam) != null && !"".equals(request.getParameter(namespaceParam).trim())) {
                        namespace = request.getParameter(namespaceParam).trim();
                    }
                    Group group = getIdentityManagementService().getGroupByName(namespace, request.getParameter(parameterName));
                    if (group != null) {
                        form.getAdHocRouteWorkgroup(lineNumber).setId(group.getGroupId());
                        form.getAdHocRouteWorkgroup(lineNumber).setRecipientName(group.getGroupName());
                        form.getAdHocRouteWorkgroup(lineNumber).setRecipientNamespaceCode(group.getNamespaceCode());
                    } else {
                        throw new RuntimeException("Invalid workgroup id passed as parameter.");
                    }
                }
            }
        }
    }
    */
    
    /**
     * Checks if the given value matches patterns that indicate sensitive data and if configured to give a warning for sensitive data will
     * prompt the user to continue.
     * 
     * @param form
     * @param request
     * @param response
     * @param fieldName - name of field with value being checked
     * @param fieldValue - value to check for sensitive data
     * @param caller - method that should be called back from question
     * @param context - additional context that needs to be passed back with the question response
     * @return - view for spring to forward to, or null if processing should continue
     * @throws Exception
     */
    protected String checkAndWarnAboutSensitiveData(DocumentFormBase form, HttpServletRequest request, HttpServletResponse response,
            String fieldName, String fieldValue, String caller, String context) throws Exception {
        
        String viewName = null;
        Document document = form.getDocument();

        boolean containsSensitiveData = WebUtils.containsSensitiveDataPatternMatch(fieldValue);

        // check if warning is configured in which case we will prompt, or if not business rules will thrown an error
        boolean warnForSensitiveData = KNSServiceLocator.getParameterService().getIndicatorParameter(
                KNSConstants.KNS_NAMESPACE, ParameterConstants.ALL_COMPONENT,
                KNSConstants.SystemGroupParameterNames.SENSITIVE_DATA_PATTERNS_WARNING_IND);

        // determine if the question has been asked yet
        Map<String, String> ticketContext = new HashMap<String, String>();
        ticketContext.put(KNSPropertyConstants.DOCUMENT_NUMBER, document.getDocumentNumber());
        ticketContext.put(KNSConstants.CALLING_METHOD, caller);
        ticketContext.put(KNSPropertyConstants.NAME, fieldName);

        boolean questionAsked = GlobalVariables.getUserSession().hasMatchingSessionTicket(
                KNSConstants.SENSITIVE_DATA_QUESTION_SESSION_TICKET, ticketContext);

        // start in logic for confirming the sensitive data
        if (containsSensitiveData && warnForSensitiveData && !questionAsked) {
            Object question = request.getParameter(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null || !KNSConstants.DOCUMENT_SENSITIVE_DATA_QUESTION.equals(question)) {

                // TODO not ready for question framework yet
                /*
                // question hasn't been asked, prompt to continue
                return this.performQuestionWithoutInput(mapping, form, request, response,
                        KNSConstants.DOCUMENT_SENSITIVE_DATA_QUESTION, getKualiConfigurationService()
                                .getPropertyString(RiceKeyConstants.QUESTION_SENSITIVE_DATA_DOCUMENT),
                        KNSConstants.CONFIRMATION_QUESTION, caller, context);
                */
                viewName = "ask_user_questions";
            }
            else {
                Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
                
                // if no button clicked just reload the doc
                if (ConfirmationQuestion.NO.equals(buttonClicked)) {
                    // TODO figure out what to return
                    viewName = "user_says_no";
                }
                
                // answered yes, create session ticket so we not to ask question again if there are further question requests
                SessionTicket ticket = new SessionTicket(KNSConstants.SENSITIVE_DATA_QUESTION_SESSION_TICKET);
                ticket.setTicketContext(ticketContext);
                GlobalVariables.getUserSession().putSessionTicket(ticket);
            }
        }

        // returning null will indicate processing should continue (no redirect)
        return viewName;
    }
    
    public BusinessObjectService getBusinessObjectService() {
        if(this.businessObjectService == null) {
            this.businessObjectService = KNSServiceLocator.getBusinessObjectService();
        }
        return this.businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
        if(this.businessObjectAuthorizationService == null) {
            this.businessObjectAuthorizationService = KNSServiceLocator.getBusinessObjectAuthorizationService();
        }
        return this.businessObjectAuthorizationService;
    }

    public void setBusinessObjectAuthorizationService(BusinessObjectAuthorizationService businessObjectAuthorizationService) {
        this.businessObjectAuthorizationService = businessObjectAuthorizationService;
    }


    public BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        if(this.businessObjectMetaDataService == null) {
            this.businessObjectMetaDataService = KNSServiceLocator.getBusinessObjectMetaDataService();
        }
        return this.businessObjectMetaDataService;
    }


    public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService businessObjectMetaDataService) {
        this.businessObjectMetaDataService = businessObjectMetaDataService;
    }


    public DataDictionaryService getDataDictionaryService() {
        if(this.dataDictionaryService == null) {
            this.dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
        }
        return this.dataDictionaryService;
    }


    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }


    public DocumentService getDocumentService() {
        if(this.documentService == null) {
            this.documentService = KNSServiceLocator.getDocumentService();
        }
        return this.documentService;
    }


    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }


    public DocumentHelperService getDocumentHelperService() {
        if(this.documentHelperService == null) {
            this.documentHelperService = KNSServiceLocator.getDocumentHelperService();
        }
        return this.documentHelperService;
    }


    public void setDocumentHelperService(DocumentHelperService documentHelperService) {
        this.documentHelperService = documentHelperService;
    }
}
