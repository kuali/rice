/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.web.struts.action;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.OptimisticLockException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteWorkgroup;
import org.kuali.core.bo.Attachment;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.DocumentAuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.exceptions.UnknownDocumentIdException;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.rule.PreRulesCheck;
import org.kuali.core.rule.event.AddAdHocRoutePersonEvent;
import org.kuali.core.rule.event.AddAdHocRouteWorkgroupEvent;
import org.kuali.core.rule.event.AddNoteEvent;
import org.kuali.core.rule.event.PreRulesCheckEvent;
import org.kuali.core.service.DocumentAuthorizationService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.Timer;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.struts.form.BlankFormFile;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.workflow.DocumentInitiator;
import org.kuali.core.workflow.KualiDocumentXmlMaterializer;
import org.kuali.core.workflow.KualiTransactionalDocumentInformation;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.KNSServiceLocator;
import org.springmodules.orm.ojb.OjbOperationException;

import edu.iu.uis.eden.clientapp.IDocHandler;
import edu.iu.uis.eden.clientapp.vo.WorkflowGroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;
import edu.iu.uis.eden.exception.InvalidWorkgroupException;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles all of the document handling related actions in terms of passing them from here at a central point to the
 * distributed transactions that actually implement document handling.
 */
public class KualiDocumentActionBase extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiDocumentActionBase.class);

    // COMMAND constants which cause docHandler to load an existing document instead of creating a new one
    private static final String[] DOCUMENT_LOAD_COMMANDS = { IDocHandler.ACTIONLIST_COMMAND, IDocHandler.DOCSEARCH_COMMAND, IDocHandler.SUPERUSER_COMMAND, IDocHandler.HELPDESK_ACTIONLIST_COMMAND };

    protected void checkAuthorization( ActionForm form, String methodToCall ) throws AuthorizationException {
        if ( !(form instanceof KualiDocumentFormBase) ) {
            super.checkAuthorization(form, methodToCall);
        } else {
            AuthorizationType documentAuthorizationType = new AuthorizationType.Document(((KualiDocumentFormBase)form).getDocument().getClass(), ((KualiDocumentFormBase)form).getDocument());
            if ( !KNSServiceLocator.getKualiModuleService().isAuthorized( GlobalVariables.getUserSession().getUniversalUser(), documentAuthorizationType ) ) {
                LOG.error("User not authorized to use this document: " + ((KualiDocumentFormBase)form).getDocument().getClass().getName() );
                throw new ModuleAuthorizationException( GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), documentAuthorizationType, getKualiModuleService().getResponsibleModule(((KualiDocumentFormBase)form).getDocument().getClass()) );
            }
        }
    }

    /**
     * Entry point to all actions.
     *
     * NOTE: No need to hook into execute for handling framwork setup anymore. Just implement the methodToCall for the framework
     * setup, Constants.METHOD_REQUEST_PARAMETER will contain the full parameter, which can be sub stringed for getting framework
     * parameters.
     *
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Timer t0 = new Timer("KualiDocumentActionBase.execute");
        ActionForward returnForward = mapping.findForward(Constants.MAPPING_BASIC);

        // if found methodToCall, pass control to that method
        try {
            returnForward = super.execute(mapping, form, request, response);
        }
        catch (OjbOperationException e) {
            // special handling for OptimisticLockExceptions
            OjbOperationException ooe = (OjbOperationException) e;

            Throwable cause = ooe.getCause();
            if (cause instanceof OptimisticLockException) {
                OptimisticLockException ole = (OptimisticLockException) cause;
                GlobalVariables.getErrorMap().putError(Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_OPTIMISTIC_LOCK);
                logOjbOptimisticLockException(ole);
            }
            else {
                throw e;
            }
        }

        // populates authorization-related fields in KualiDocumentFormBase instances, which are derived from
        // information which is contained in the form but which may be unavailable until this point
        if (form instanceof KualiDocumentFormBase) {
            KualiDocumentFormBase formBase = (KualiDocumentFormBase) form;
            Document document = formBase.getDocument();
            DocumentAuthorizer documentAuthorizer = KNSServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(document);
            formBase.populateAuthorizationFields(documentAuthorizer);

            // set returnToActionList flag, if needed
            if ("displayActionListView".equals(formBase.getCommand())) {
                formBase.setReturnToActionList(true);
            }
        }
        t0.log();

        return returnForward;
    }

    /**
     * This method may be used to funnel all document handling through, we could do useful things like log and record various
     * openings and status Additionally it may be nice to have a single dispatcher that can know how to dispatch to a redirect url
     * for document specific handling but we may not need that as all we should need is the document to be able to load itself based
     * on document id and then which actionforward or redirect is pertinent for the document type.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        String command = kualiDocumentFormBase.getCommand();

        // in all of the following cases we want to load the document
        if (ArrayUtils.contains(DOCUMENT_LOAD_COMMANDS, command) && kualiDocumentFormBase.getDocId() != null) {
            loadDocument(kualiDocumentFormBase);
        }
        else if (IDocHandler.INITIATE_COMMAND.equals(command)) {
            createDocument(kualiDocumentFormBase);
        }
        else {
            LOG.error("docHandler called with invalid parameters");
            throw new IllegalStateException("docHandler called with invalid parameters");
        }

        // attach any extra JS from the data dictionary
        if (LOG.isDebugEnabled()) {
            LOG.debug("kualiDocumentFormBase.getAdditionalScriptFiles(): " + kualiDocumentFormBase.getAdditionalScriptFiles());
        }
        if ( kualiDocumentFormBase.getAdditionalScriptFiles().isEmpty() ) {
            DocumentEntry docEntry = KNSServiceLocator.getDataDictionaryService().getDataDictionary().getDocumentEntry( kualiDocumentFormBase.getDocument().getDocumentHeader().getWorkflowDocument().getDocumentType() );
            kualiDocumentFormBase.getAdditionalScriptFiles().addAll(docEntry.getWebScriptFiles());
        }
        if (IDocHandler.SUPERUSER_COMMAND.equalsIgnoreCase(command)) {
            kualiDocumentFormBase.setSuppressAllButtons(true);
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method loads the document by its provided document header id. This has been abstracted out so that it can be overridden
     * in children if the need arises.
     *
     * @param kualiDocumentFormBase
     * @throws WorkflowException
     */
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        String docId = kualiDocumentFormBase.getDocId();
        Document doc = null;
        doc = KNSServiceLocator.getDocumentService().getByDocumentHeaderId(docId);
        if (doc == null) {
            throw new UnknownDocumentIdException("Document no longer exists.  It may have been cancelled before being saved.");
        }

        kualiDocumentFormBase.setDocument(doc);
        KualiWorkflowDocument workflowDoc = doc.getDocumentHeader().getWorkflowDocument();
        kualiDocumentFormBase.setDocTypeName(workflowDoc.getDocumentType());
        // KualiDocumentFormBase.populate() needs this updated in the session
        GlobalVariables.getUserSession().setWorkflowDocument(workflowDoc);
    }


    /**
     * This method creates a new document of the type specified by the docTypeName property of the given form. This has been
     * abstracted out so that it can be overridden in children if the need arises.
     *
     * @param kualiDocumentFormBase
     * @throws WorkflowException
     */
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        Document doc = KNSServiceLocator.getDocumentService().getNewDocument(kualiDocumentFormBase.getDocTypeName());

        kualiDocumentFormBase.setDocument(doc);
        kualiDocumentFormBase.setDocTypeName(doc.getDocumentHeader().getWorkflowDocument().getDocumentType());
    }

    /**
     * This method will insert the new ad hoc person from the from into the list of ad hoc person recipients, put a new new record
     * in place and return like normal.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertAdHocRoutePerson(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        Document document = kualiDocumentFormBase.getDocument();

        // check authorization
        DocumentActionFlags flags = getDocumentActionFlags(document);
        if (!flags.getCanAdHocRoute()) {
            throw buildAuthorizationException("ad-hoc route", document);
        }

        // check business rules
        boolean rulePassed = KNSServiceLocator.getKualiRuleService().applyRules(new AddAdHocRoutePersonEvent(document, (AdHocRoutePerson) kualiDocumentFormBase.getNewAdHocRoutePerson()));

        // if the rule evaluation passed, let's add the ad hoc route person
        if (rulePassed) {
            // uppercase userid for consistency
            kualiDocumentFormBase.getNewAdHocRoutePerson().setId(StringUtils.upperCase(kualiDocumentFormBase.getNewAdHocRoutePerson().getId()));
            kualiDocumentFormBase.getAdHocRoutePersons().add(kualiDocumentFormBase.getNewAdHocRoutePerson());
            AdHocRoutePerson person = new AdHocRoutePerson();
            kualiDocumentFormBase.setNewAdHocRoutePerson(person);
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method will delete one of the ad hoc persons from the list of ad hoc persons to route to based on the line number of the
     * delete button that was clicked. then it will return to the form.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteAdHocRoutePerson(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        Document document = kualiDocumentFormBase.getDocument();

        DocumentActionFlags flags = getDocumentActionFlags(document);
        if (!flags.getCanAdHocRoute()) {
            throw buildAuthorizationException("delete ad-hoc route persons", document);
        }

        kualiDocumentFormBase.getAdHocRoutePersons().remove(this.getLineToDelete(request));
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method will insert the new ad hoc workgroup into the list of ad hoc workgroup recipients put a nuew record in place and
     * then return like normal.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertAdHocRouteWorkgroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        Document document = kualiDocumentFormBase.getDocument();

        // check authorization
        DocumentActionFlags flags = getDocumentActionFlags(document);
        if (!flags.getCanAdHocRoute()) {
            throw buildAuthorizationException("add ad-hoc routing", document);
        }

        // check business rules
        boolean rulePassed = KNSServiceLocator.getKualiRuleService().applyRules(new AddAdHocRouteWorkgroupEvent(document, (AdHocRouteWorkgroup) kualiDocumentFormBase.getNewAdHocRouteWorkgroup()));

        // if the rule evaluation passed, let's add the ad hoc route workgroup
        if (rulePassed) {
            kualiDocumentFormBase.getAdHocRouteWorkgroups().add(kualiDocumentFormBase.getNewAdHocRouteWorkgroup());
            AdHocRouteWorkgroup workgroup = new AdHocRouteWorkgroup();
            kualiDocumentFormBase.setNewAdHocRouteWorkgroup(workgroup);
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method will delete one of the ad hoc workgroups from the list of ad hoc workgroups to route to based on the line number
     * of the delete button that was clicked. then it will return
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteAdHocRouteWorkgroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        Document document = kualiDocumentFormBase.getDocument();

        DocumentActionFlags flags = getDocumentActionFlags(document);
        if (!flags.getCanAdHocRoute()) {
            throw buildAuthorizationException("delete ad-hoc route workgroups", document);
        }

        kualiDocumentFormBase.getAdHocRouteWorkgroups().remove(this.getLineToDelete(request));
        return mapping.findForward(Constants.MAPPING_BASIC);
    }


    /**
     * This method will reload the document.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        Document document = kualiDocumentFormBase.getDocument();

        // check authorization for reloading document
        DocumentActionFlags flags = getDocumentActionFlags(document);
        if (!flags.getCanReload()) {
            throw buildAuthorizationException("reload", document);
        }

        // prepare for the reload action - set doc id and command
        kualiDocumentFormBase.setDocId(document.getDocumentNumber());
        kualiDocumentFormBase.setCommand(DOCUMENT_LOAD_COMMANDS[1]);

        // forward off to the doc handler
        ActionForward actionForward = docHandler(mapping, form, request, response);

        GlobalVariables.getMessageList().add(KeyConstants.MESSAGE_RELOADED);

        return actionForward;
    }

    /**
     * This method will save the document, which will then be available via the action list for the person who saved the document.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        DocumentService documentService = KNSServiceLocator.getDocumentService();
        Document document = kualiDocumentFormBase.getDocument();

        // save in workflow
        documentService.saveDocument(document);

        GlobalVariables.getMessageList().add(KeyConstants.MESSAGE_SAVED);
        kualiDocumentFormBase.setAnnotation("");

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * route the document using the document service
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performRouteReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;

        ActionForward preRulesForward = preRulesCheck(mapping, form, request, response);
        if (preRulesForward != null) {
            return preRulesForward;
        }

        Document document = kualiDocumentFormBase.getDocument();
        String documentTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
        KualiTransactionalDocumentInformation transInfo = new KualiTransactionalDocumentInformation();
        DocumentInitiator initiatior = new DocumentInitiator();
        String initiatorNetworkId = document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
        try {
            UniversalUser initiatorUser = KNSServiceLocator.getUniversalUserService().getUniversalUser(new AuthenticationUserId(initiatorNetworkId));
            initiatior.setUniversalUser(initiatorUser);
        }
        catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        transInfo.setDocumentInitiator(initiatior);
        KualiDocumentXmlMaterializer xmlWrapper = new KualiDocumentXmlMaterializer();
        xmlWrapper.setDocument(document);
        xmlWrapper.setKualiTransactionalDocumentInformation(transInfo);
        String xml = KNSServiceLocator.getXmlObjectSerializerService().toXml(xmlWrapper);

//        String workflowRouteReportUrl = KNSServiceLocator.getKualiConfigurationService().getPropertyString(Constants.WORKFLOW_BASE_URL_KEY) + "/RoutingReport.do?documentTypeParam=" + documentTypeName + "&initiatorNetworkId=" + initiatorNetworkId + "&documentContent=";
        String workflowRouteReportUrl = KNSServiceLocator.getKualiConfigurationService().getPropertyString(Constants.WORKFLOW_URL_KEY) + "/RoutingReport.do";
        // at this point we need to get the document content xml to the jsp so we can post to URL above
        // TODO delyea - make this a map for ease of expansion later.... use Workflow constants for attribute names?
        request.setAttribute("workflowRouteReportUrl", workflowRouteReportUrl);
        request.setAttribute("initiatorNetworkId", initiatorNetworkId);
        request.setAttribute("documentTypeName", documentTypeName);
        request.setAttribute("documentContent", xml);

//        GlobalVariables.getMessageList().add(KeyConstants.MESSAGE_ROUTE_SUCCESSFUL);
//        kualiDocumentFormBase.setAnnotation("");

        return mapping.findForward(Constants.MAPPING_ROUTE_REPORT);
    }

    /**
     * route the document using the document service
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;

        ActionForward preRulesForward = preRulesCheck(mapping, form, request, response);
        if (preRulesForward != null) {
            return preRulesForward;
        }

        DocumentService documentService = KNSServiceLocator.getDocumentService();

        Document document = kualiDocumentFormBase.getDocument();

        documentService.routeDocument(document, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
        GlobalVariables.getMessageList().add(KeyConstants.MESSAGE_ROUTE_SUCCESSFUL);
        kualiDocumentFormBase.setAnnotation("");

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Calls the document service to blanket approve the document
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        DocumentService docService = KNSServiceLocator.getDocumentService();

        ActionForward preRulesForward = preRulesCheck(mapping, form, request, response);
        if (preRulesForward != null) {
            return preRulesForward;
        }
        docService.blanketApproveDocument(kualiDocumentFormBase.getDocument(), kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
        GlobalVariables.getMessageList().add(KeyConstants.MESSAGE_ROUTE_APPROVED);
        kualiDocumentFormBase.setAnnotation("");
        return returnToSender(mapping, kualiDocumentFormBase);
    }

    /**
     * Calls the document service to approve the document
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        DocumentService docService = KNSServiceLocator.getDocumentService();

        ActionForward preRulesForward = preRulesCheck(mapping, form, request, response);
        if (preRulesForward != null) {
            return preRulesForward;
        }
        docService.approveDocument(kualiDocumentFormBase.getDocument(), kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
        GlobalVariables.getMessageList().add(KeyConstants.MESSAGE_ROUTE_APPROVED);
        kualiDocumentFormBase.setAnnotation("");
        return returnToSender(mapping, kualiDocumentFormBase);
    }

    /**
     * Calls the document service to disapprove the document
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward disapprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;

        Object question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(Constants.QUESTION_REASON_ATTRIBUTE_NAME);
        String disapprovalNoteText = "";

        KualiConfigurationService kualiConfiguration = KNSServiceLocator.getKualiConfigurationService();

        // start in logic for confirming the disapproval
        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithInput(mapping, form, request, response, Constants.DOCUMENT_DISAPPROVE_QUESTION, kualiConfiguration.getPropertyString(KeyConstants.QUESTION_DISAPPROVE_DOCUMENT), Constants.CONFIRMATION_QUESTION, Constants.MAPPING_DISAPPROVE, "");
        }
        else {
            Object buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);
            if ((Constants.DOCUMENT_DISAPPROVE_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                // if no button clicked just reload the doc
                return mapping.findForward(Constants.MAPPING_BASIC);
            }
            else {
                // have to check length on value entered
                String introNoteMessage = kualiConfiguration.getPropertyString(KeyConstants.MESSAGE_DISAPPROVAL_NOTE_TEXT_INTRO) + Constants.BLANK_SPACE;

                // build out full message
                disapprovalNoteText = introNoteMessage + reason;
                int disapprovalNoteTextLength = disapprovalNoteText.length();

                // get note text max length from DD
                int noteTextMaxLength = KNSServiceLocator.getDataDictionaryService().getAttributeMaxLength(Note.class, Constants.NOTE_TEXT_PROPERTY_NAME).intValue();

                if (StringUtils.isBlank(reason) || (disapprovalNoteTextLength > noteTextMaxLength)) {
                    // figure out exact number of characters that the user can enter
                    int reasonLimit = noteTextMaxLength - disapprovalNoteTextLength;

                    if (reason == null) {
                        // prevent a NPE by setting the reason to a blank string
                        reason = "";
                    }
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, Constants.DOCUMENT_DISAPPROVE_QUESTION, kualiConfiguration.getPropertyString(KeyConstants.QUESTION_DISAPPROVE_DOCUMENT), Constants.CONFIRMATION_QUESTION, Constants.MAPPING_DISAPPROVE, "", reason, KeyConstants.ERROR_DOCUMENT_DISAPPROVE_REASON_REQUIRED, Constants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
                }
            }
        }

        KNSServiceLocator.getDocumentService().disapproveDocument(kualiDocumentFormBase.getDocument(), disapprovalNoteText);
        GlobalVariables.getMessageList().add(KeyConstants.MESSAGE_ROUTE_DISAPPROVED);
        kualiDocumentFormBase.setAnnotation("");

        return returnToSender(mapping, kualiDocumentFormBase);
    }

    /**
     * Calls the document service to cancel the document
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        // this should probably be moved into a private instance variable
        KualiConfigurationService kualiConfiguration = KNSServiceLocator.getKualiConfigurationService();

        // logic for cancel question
        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithoutInput(mapping, form, request, response, Constants.DOCUMENT_CANCEL_QUESTION, kualiConfiguration.getPropertyString("document.question.cancel.text"), Constants.CONFIRMATION_QUESTION, Constants.MAPPING_CANCEL, "");
        }
        else {
            Object buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);
            if ((Constants.DOCUMENT_CANCEL_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                // if no button clicked just reload the doc
                return mapping.findForward(Constants.MAPPING_BASIC);
            }
            // else go to cancel logic below
        }

        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        KNSServiceLocator.getDocumentService().cancelDocument(kualiDocumentFormBase.getDocument(), kualiDocumentFormBase.getAnnotation());

        return returnToSender(mapping, kualiDocumentFormBase);
    }

    /**
     * Close the document and take the user back to the index; only after asking the user if they want to save the document first.
     * Only users who have the "canSave()" permission are given this option.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase docForm = (KualiDocumentFormBase) form;

        // only want to prompt them to save if they already can save
        if (docForm.getDocumentActionFlags().getCanSave()) {
            Object question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
            KualiConfigurationService kualiConfiguration = KNSServiceLocator.getKualiConfigurationService();

            // logic for close question
            if (question == null) {
                // ask question if not already asked
                return this.performQuestionWithoutInput(mapping, form, request, response, Constants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, kualiConfiguration.getPropertyString(KeyConstants.QUESTION_SAVE_BEFORE_CLOSE), Constants.CONFIRMATION_QUESTION, Constants.MAPPING_CLOSE, "");
            }
            else {
                Object buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);
                if ((Constants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                    // if yes button clicked - save the doc
                    KNSServiceLocator.getDocumentService().saveDocument(docForm.getDocument());
                }
                // else go to close logic below
            }
        }

        return returnToSender(mapping, docForm);
    }

    /**
     * call the document service to clear the fyis
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward fyi(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        KNSServiceLocator.getDocumentService().clearDocumentFyi(kualiDocumentFormBase.getDocument(), combineAdHocRecipients(kualiDocumentFormBase));
        GlobalVariables.getMessageList().add(KeyConstants.MESSAGE_ROUTE_FYIED);
        kualiDocumentFormBase.setAnnotation("");
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * call the document service to acknowledge
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward acknowledge(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        KNSServiceLocator.getDocumentService().acknowledgeDocument(kualiDocumentFormBase.getDocument(), kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
        GlobalVariables.getMessageList().add(KeyConstants.MESSAGE_ROUTE_ACKNOWLEDGED);
        kualiDocumentFormBase.setAnnotation("");
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * redirect to the supervisor functions that exist.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward supervisorFunctions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;


        String workflowSuperUserUrl = KNSServiceLocator.getKualiConfigurationService().getPropertyString(Constants.WORKFLOW_URL_KEY) + "/SuperUser.do?methodToCall=displaySuperUserDocument&routeHeaderId=" + kualiDocumentFormBase.getDocument().getDocumentHeader().getDocumentNumber();
        response.sendRedirect(workflowSuperUserUrl);

        return null;
    }

    /**
     * Convenience method to combine the two lists of ad hoc recipients into one which should be done before calling any of the
     * document service methods that expect a list of ad hoc recipients
     *
     * @param kualiDocumentFormBase
     * @return List
     */
    protected List combineAdHocRecipients(KualiDocumentFormBase kualiDocumentFormBase) {
        List adHocRecipients = new ArrayList();
        adHocRecipients.addAll(kualiDocumentFormBase.getAdHocRoutePersons());
        adHocRecipients.addAll(kualiDocumentFormBase.getAdHocRouteWorkgroups());
        return adHocRecipients;
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);
        KualiDocumentFormBase kualiForm = (KualiDocumentFormBase) form;
        refreshAdHocRoutingWorkgroupLookups(request, kualiForm);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * special refresh needed to get the workgroups populated correctly when coming back from workgroup lookups
     *
     * @param request
     * @param kualiForm
     * @throws InvalidWorkgroupException
     * @throws WorkflowException
     */
    protected void refreshAdHocRoutingWorkgroupLookups(HttpServletRequest request, KualiDocumentFormBase kualiForm) throws InvalidWorkgroupException, WorkflowException {
        for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
            String parameterName = (String) i.nextElement();
            // TODO replace this with the workflow workgroup service call
            // once it is done
            // can kuali workgroup service work here it is backed by workflow groups
            if (parameterName.equals("newAdHocRouteWorkgroup.id") && !"".equals(request.getParameter(parameterName))) {
                if (Long.parseLong(request.getParameter(parameterName)) > 0) {
                    WorkgroupVO workgroupVo = KNSServiceLocator.getWorkflowInfoService().getWorkgroup(new WorkflowGroupIdVO(new Long(request.getParameter(parameterName))));
                    kualiForm.getNewAdHocRouteWorkgroup().setId(workgroupVo.getWorkgroupName());
                }
                else {
                    throw new RuntimeException("Invalid workgroup id passed as parameter.");
                }
            }
            if (parameterName.startsWith("adHocRouteWorkgroup[") && !"".equals(request.getParameter(parameterName))) {
                if (Long.getLong(request.getParameter(parameterName)) != null) {
                    WorkgroupVO workgroupVo = KNSServiceLocator.getWorkflowInfoService().getWorkgroup(new WorkflowGroupIdVO(new Long(request.getParameter(parameterName))));
                    int lineNumber = Integer.parseInt(StringUtils.substringBetween(parameterName, "[", "]"));
                    kualiForm.getAdHocRouteWorkgroup(lineNumber).setId(workgroupVo.getWorkgroupName());
                }
                else {
                    throw new RuntimeException("Invalid workgroup id passed as parameter.");
                }
            }
        }
    }


    /**
     * Cancels the pending attachment, if any.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward cancelBOAttachment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase documentForm = (KualiDocumentFormBase) form;

        // blank current attachmentFile
        documentForm.setAttachmentFile(new BlankFormFile());

        // remove current attachment, if any
        Note note = documentForm.getNewNote();
        note.removeAttachment();
        documentForm.setNewNote(note);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }


    /**
     * Downloads the selected attachment to the user's browser
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward downloadBOAttachment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int attachmentIndex = selectedAttachmentIndex(request);
        
        Note newNote = ((KualiDocumentFormBase) form).getNewNote();
        PersistableBusinessObject noteParent = getNoteParent(((KualiDocumentFormBase) form).getDocument(), newNote);

        
        if (attachmentIndex >= 0) {
            Note note = noteParent.getBoNote(attachmentIndex);
            Attachment attachment = note.getAttachment();
            //make sure attachment is setup with backwards reference to note (rather then doing this we could also just call the attachment service (with a new method that took in the note)
            attachment.setNote(note);
            
            WebUtils.saveMimeInputStreamAsFile(response, attachment.getAttachmentMimeTypeCode(), attachment.getAttachmentContents(), attachment.getAttachmentFileName(), attachment.getAttachmentFileSize().intValue());
            return null;
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    
    /**
     * @param request
     * @return index of the attachment whose download button was just pressed
     */
    protected int selectedAttachmentIndex(HttpServletRequest request) {
        int attachmentIndex = -1;

        String parameterName = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            String attachmentIndexParam = StringUtils.substringBetween(parameterName, ".attachment[", "].");

            try {
                attachmentIndex = Integer.parseInt(attachmentIndexParam);
            }
            catch (NumberFormatException ignored) {
            }
        }

        return attachmentIndex;
    }


    /**
     * insert a note into the document
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertBONote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        Document document = kualiDocumentFormBase.getDocument();

        // check authorization for adding notes
        DocumentActionFlags flags = getDocumentActionFlags(document);
        if (!flags.getCanAnnotate()) {
            buildAuthorizationException("annotate", document);
        }

        Note newNote = kualiDocumentFormBase.getNewNote();
        PersistableBusinessObject noteParent = getNoteParent(document, newNote);


        // create the attachment first, so that failure-to-create-attachment can be treated as a validation failure
        FormFile attachmentFile = kualiDocumentFormBase.getAttachmentFile();
        if (attachmentFile == null) {
            GlobalVariables.getErrorMap().putError("newDocumentNote.attachmentFile", KeyConstants.ERROR_UPLOADFILE_NULL);
            return mapping.findForward(Constants.MAPPING_BASIC);
        }

        Attachment attachment = null;
        if (!StringUtils.isBlank(attachmentFile.getFileName())) {
            if (attachmentFile.getFileSize() == 0) {
                GlobalVariables.getErrorMap().putError("newDocumentNote.attachmentFile", KeyConstants.ERROR_UPLOADFILE_EMPTY, attachmentFile.getFileName());
                return mapping.findForward(Constants.MAPPING_BASIC);
            }
            else {
                String attachmentType = null;
                Attachment newAttachment = kualiDocumentFormBase.getNewNote().getAttachment();
                if (newAttachment != null) {
                    attachmentType = newAttachment.getAttachmentTypeCode();
                }
                attachment = KNSServiceLocator.getAttachmentService().createAttachment(noteParent, attachmentFile.getFileName(), attachmentFile.getContentType(), attachmentFile.getFileSize(), attachmentFile.getInputStream(), attachmentType);
            }
        }

        DataDictionary dataDictionary = KNSServiceLocator.getDataDictionaryService().getDataDictionary();
        DocumentEntry entry = dataDictionary.getDocumentEntry(document.getClass().getName());

        if(entry.getDisplayTopicFieldInNotes()) {
            String topicText = kualiDocumentFormBase.getNewNote().getNoteTopicText();
            if(StringUtils.isBlank(topicText)) {
                GlobalVariables.getErrorMap().putError("newDocumentNote.financialDocumentNoteTopicText", KeyConstants.ERROR_REQUIRED, "Note Topic (Note Topic)");
            }
        }

        // create a new note from the data passed in
        Note tmpNote = KNSServiceLocator.getNoteService().createNote(newNote, noteParent);

        // validate the note
        boolean rulePassed = KNSServiceLocator.getKualiRuleService().applyRules(new AddNoteEvent(document, tmpNote));

        // if the rule evaluation passed, let's add the note
        if (rulePassed) {
            tmpNote.refresh();


            DocumentHeader documentHeader = kualiDocumentFormBase.getDocument().getDocumentHeader();

            // associate note with object now
            noteParent.addNote(tmpNote);

            // persist the note if the document is already saved the getObjectId check is to get around a bug with certain documents where
            // "saved" doesn't really persist, if you notice any problems with missing notes check this line
            if (!documentHeader.getWorkflowDocument().stateIsInitiated()&&StringUtils.isNotEmpty(noteParent.getObjectId())) {
                KNSServiceLocator.getNoteService().save(tmpNote);
            }
            // adding the attachment after refresh gets called, since the attachment record doesn't get persisted
            // until the note does (and therefore refresh doesn't have any attachment to autoload based on the id, nor does it
            // autopopulate the id since the note hasn't been persisted yet)
            if (attachment != null) {
                tmpNote.addAttachment(attachment);
                // save again for attachment, note this is because sometimes the attachment is added first to the above then ojb tries to save
                //without the PK on the attachment I think it is safer then trying to get the sequence manually 
                if (!documentHeader.getWorkflowDocument().stateIsInitiated()&&StringUtils.isNotEmpty(noteParent.getObjectId())) {
                    KNSServiceLocator.getNoteService().save(tmpNote);
                }
            }

            
            // reset the new note back to an empty one
            kualiDocumentFormBase.setNewNote(new Note());
        }


        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method...
     * @param document
     * @param newNote
     * @return
     */
    private PersistableBusinessObject getNoteParent(Document document, Note newNote) {
        //get the property name to set (this assumes this is a document type note)
        String propertyName = KNSServiceLocator.getNoteService().extractNoteProperty(newNote);
        //get BO to set
        PersistableBusinessObject noteParent = (PersistableBusinessObject)ObjectUtils.getPropertyValue(document, propertyName);
        return noteParent;
    }

    /**
     * delete a note from the document
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteBONote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        Document document = kualiDocumentFormBase.getDocument();


        DataDictionary dataDictionary = KNSServiceLocator.getDataDictionaryService().getDataDictionary();
        DocumentEntry entry = dataDictionary.getDocumentEntry(document.getClass().getName());

        // check authorization for adding notes
        DocumentActionFlags flags = getDocumentActionFlags(document);
        if (!flags.getCanAnnotate() || !entry.getAllowsNoteDelete()) {
            buildAuthorizationException("annotate", document);
            return mapping.findForward(Constants.MAPPING_BASIC);
        }

        // ok to delete the note/attachment
        // derive the note property from the newNote on the form
        Note newNote = kualiDocumentFormBase.getNewNote();
        PersistableBusinessObject noteParent = this.getNoteParent(document, newNote);

        Note note = noteParent.getBoNote(getLineToDelete(request));

        Attachment attachment = note.getAttachment();

        // delete the note if the document is already saved
        if (!document.getDocumentHeader().getWorkflowDocument().stateIsInitiated()) {
            KNSServiceLocator.getNoteService().deleteNote(note);
        }

        if (attachment != null) {
            KNSServiceLocator.getAttachmentService().deleteAttachmentContents(attachment);
        }

        noteParent.deleteNote(note);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }


    /**
     * Generates detailed log messages for OptimisticLockExceptions
     *
     * @param e
     */
    private final void logOjbOptimisticLockException(OptimisticLockException e) {
        StringBuffer message = new StringBuffer("caught OptimisticLockException, caused by ");
        Object sourceObject = e.getSourceObject();
        String infix = null;
        try {
            // try to add instance details
            infix = sourceObject.toString();
        }
        catch (Exception e2) {
            // just use the class name
            infix = sourceObject.getClass().getName();
        }
        message.append(infix);

        if (sourceObject instanceof PersistableBusinessObject) {
            PersistableBusinessObject persistableObject = (PersistableBusinessObject) sourceObject;
            message.append(" [versionNumber = " + persistableObject.getVersionNumber() + "]");
        }

        LOG.info(message.toString());
    }


    /**
     * Makes calls to any pre rules classes specified for the document. If the rule class returns an actionforward, that forward
     * will be returned, or null.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward preRulesCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return preRulesCheck(mapping, form, request, response, "route");
    }

    /**
     * Makes calls to any pre rules classes specified for the document. If the rule class returns an actionforward, that forward
     * will be returned, or null.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param methodToCall
     * @return
     * @throws Exception
     */
    public ActionForward preRulesCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String methodToCall) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;

        /* callback to any pre rules check class */
        Class preRulesClass = KNSServiceLocator.getDataDictionaryService().getPreRulesCheckClass(kualiDocumentFormBase.getDocTypeName());
        if (LOG.isDebugEnabled()) {
            LOG.debug("PreRulesCheckClass: " + preRulesClass);
        }
        if (preRulesClass != null) {
            PreRulesCheck preRules = (PreRulesCheck) preRulesClass.newInstance();
            PreRulesCheckEvent event = new PreRulesCheckEvent("Pre Maint route Check", "", kualiDocumentFormBase.getDocument());
            boolean continueRoute = preRules.processPreRuleChecks(form, request, event);
            if (!continueRoute) {
                if (event.isPerformQuestion()) {
                    return super.performQuestionWithoutInput(mapping, kualiDocumentFormBase, request, response, event.getQuestionId(), event.getQuestionText(), event.getQuestionType(), methodToCall, event.getQuestionContext());
                }
                else {
                    // This error section is here to avoid a silent and very confusing failure. If the PreRule
                    // instance returns a null for the processPreRuleChecks above, but does not set an
                    // ActionForwardName on the event, processing will just silently fail here, and the user
                    // will be presented with a blank frame.
                    //
                    // If the processPreRuleCheck() returns a false, an ActionForwardName needs to be set before hand
                    // by the PreRule class.
                    ActionForward actionForward = mapping.findForward(event.getActionForwardName());
                    if (actionForward == null) {
                        throw new RuntimeException("No ActionForwardName defined on this Event, no further actions will be processed.");
                    }
                    return actionForward;
                }
            }
        }

        return null;
    }


    /**
     * Convenience method for retrieving current DocumentActionFlags
     *
     * @param document
     */
    protected DocumentActionFlags getDocumentActionFlags(Document document) {
        UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();

        DocumentAuthorizationService documentAuthorizationService = KNSServiceLocator.getDocumentAuthorizationService();
        DocumentActionFlags flags = documentAuthorizationService.getDocumentAuthorizer(document).getDocumentActionFlags(document, kualiUser);

        return flags;
    }

    /**
     * Convenience method for building authorization exceptions
     *
     * @param action
     * @param document
     */
    protected DocumentAuthorizationException buildAuthorizationException(String action, Document document) {
        return new DocumentAuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), action, document.getDocumentNumber());
    }


    /**
     * If the given form has returnToActionList set to true, this method returns an ActionForward that should take the user back to
     * their action list; otherwise, it returns them to the portal.
     *
     * @param form
     * @return
     */
    protected ActionForward returnToSender(ActionMapping mapping, KualiDocumentFormBase form) {
        ActionForward dest = null;
        if (form.isReturnToActionList()) {
            KualiConfigurationService kcs = KNSServiceLocator.getKualiConfigurationService();
            String workflowBase = kcs.getPropertyString(Constants.WORKFLOW_URL_KEY);
            String actionListUrl = workflowBase + "/ActionList.do";

            dest = new ActionForward(actionListUrl, true);
        }
        else {
            dest = mapping.findForward(Constants.MAPPING_PORTAL);
        }

        return dest;
    }
}
