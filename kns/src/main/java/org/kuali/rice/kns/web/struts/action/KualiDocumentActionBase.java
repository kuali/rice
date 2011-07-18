/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kns.web.struts.action;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.OptimisticLockException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.framework.parameter.ParameterConstants;
import org.kuali.rice.core.framework.parameter.ParameterService;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.rule.PromptBeforeValidation;
import org.kuali.rice.kns.rule.event.PromptBeforeValidationEvent;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.BlankFormFile;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.struts.form.KualiMaintenanceForm;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.bo.AdHocRouteWorkgroup;
import org.kuali.rice.krad.bo.Attachment;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.authorization.DocumentAuthorizer;
import org.kuali.rice.krad.document.authorization.DocumentAuthorizerBase;
import org.kuali.rice.krad.document.authorization.DocumentPresentationController;
import org.kuali.rice.krad.document.authorization.PessimisticLock;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.exception.DocumentAuthorizationException;
import org.kuali.rice.krad.exception.UnknownDocumentIdException;
import org.kuali.rice.krad.question.ConfirmationQuestion;
import org.kuali.rice.krad.rule.event.AddAdHocRoutePersonEvent;
import org.kuali.rice.krad.rule.event.AddAdHocRouteWorkgroupEvent;
import org.kuali.rice.krad.rule.event.AddNoteEvent;
import org.kuali.rice.krad.rule.event.SendAdHocRequestsEvent;
import org.kuali.rice.krad.service.AttachmentService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentHelperService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.service.PessimisticLockService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.NoteType;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.SessionTicket;
import org.kuali.rice.krad.util.UrlFactory;
import org.springmodules.orm.ojb.OjbOperationException;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * This class handles all of the document handling related actions in terms of passing them from here at a central point to the
 * distributed transactions that actually implement document handling.
 */
public class KualiDocumentActionBase extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiDocumentActionBase.class);

    // COMMAND constants which cause docHandler to load an existing document instead of creating a new one
    protected static final String[] DOCUMENT_LOAD_COMMANDS = {
            KEWConstants.ACTIONLIST_COMMAND,
            KEWConstants.DOCSEARCH_COMMAND,
            KEWConstants.SUPERUSER_COMMAND,
            KEWConstants.HELPDESK_ACTIONLIST_COMMAND};

    private DataDictionaryService dataDictionaryService;
    private DocumentHelperService documentHelperService;
    private DocumentService documentService;
    private ConfigurationService kualiConfigurationService;
    private ParameterService parameterService;
    private PessimisticLockService pessimisticLockService;
    private KualiRuleService kualiRuleService;
    private GroupService groupService;
    private AttachmentService attachmentService;
    private NoteService noteService;
    private BusinessObjectAuthorizationService businessObjectAuthorizationService;
    private BusinessObjectService businessObjectService;
    private BusinessObjectMetaDataService businessObjectMetaDataService;
    private EntityManagerFactory entityManagerFactory;

    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        if (!(form instanceof KualiDocumentFormBase)) {
            super.checkAuthorization(form, methodToCall);
        }
    }

    /**
     * Entry point to all actions.
     * <p/>
     * NOTE: No need to hook into execute for handling framwork setup anymore. Just implement the methodToCall for the framework
     * setup, Constants.METHOD_REQUEST_PARAMETER will contain the full parameter, which can be sub stringed for getting framework
     * parameters.
     *
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward returnForward = mapping.findForward(RiceConstants.MAPPING_BASIC);

        // if found methodToCall, pass control to that method
        try {
            returnForward = super.execute(mapping, form, request, response);
        } catch (OjbOperationException e) {
            // special handling for OptimisticLockExceptions
            OjbOperationException ooe = e;

            Throwable cause = ooe.getCause();
            if (cause instanceof OptimisticLockException) {
                OptimisticLockException ole = (OptimisticLockException) cause;
                GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, RiceKeyConstants.ERROR_OPTIMISTIC_LOCK);
                logOjbOptimisticLockException(ole);
            } else {
                // if exceptions are from 'save'
                throw e;
            }
        } finally {
            if (form instanceof KualiDocumentFormBase) {
                ((KualiDocumentFormBase) form).setMessageMapFromPreviousRequest(GlobalVariables.getMessageMap());
            }
        }

        if (form instanceof KualiDocumentFormBase
                && ((KualiDocumentFormBase) form).isHasWorkflowDocument()) {
            KualiDocumentFormBase formBase = (KualiDocumentFormBase) form;
            Document document = formBase.getDocument();

            //KULRICE-2210 fix location of document header population
            WorkflowDocument workflowDocument = formBase.getDocument().getDocumentHeader().getWorkflowDocument();
            formBase.populateHeaderFields(workflowDocument);
            formBase.setDocId(document.getDocumentNumber());
            //End of KULRICE-2210 fix

            // check to see if document is a pessimistic lock document
            if (isFormRepresentingLockObject(formBase)) {
                // form represents a document using the BO class PessimisticLock so we need to skip the authorizations in the next logic check
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Form " + formBase + " represents a PessimisticLock BO object");
                }
            } else {
                // populates authorization-related fields in KualiDocumentFormBase instances, which are derived from
                // information which is contained in the form but which may be unavailable until this point
                //DocumentAuthorizer documentAuthorizer = KRADServiceLocatorInternal.getDocumentAuthorizationService().getDocumentAuthorizer(document);
                //formBase.populateAuthorizationFields(documentAuthorizer);
                populateAuthorizationFields(formBase);
                populateAdHocActionRequestCodes(formBase);

                //set the formBase into userSession if the document is a session document
                UserSession userSession = (UserSession) request.getSession().getAttribute(KRADConstants.USER_SESSION_KEY);

                if (WebUtils.isDocumentSession(document, formBase)) {
                    String formKey = formBase.getFormKey();
                    if (StringUtils.isBlank(formBase.getFormKey()) || userSession.retrieveObject(formBase.getFormKey()) == null) {
                        // generate doc form key here if it does not exist
                        formKey = GlobalVariables.getUserSession().addObjectWithGeneratedKey(form);
                        formBase.setFormKey(formKey);
                    }
                }


                // below used by KualiHttpSessionListener to handle lock expiration
                request.getSession().setAttribute(KRADConstants.DOCUMENT_HTTP_SESSION_KEY, document.getDocumentNumber());
                // set returnToActionList flag, if needed
                if ("displayActionListView".equals(formBase.getCommand())) {
                    formBase.setReturnToActionList(true);
                }

                String attachmentEnabled =
                        getKualiConfigurationService().getPropertyValueAsString(KRADConstants.NOTE_ATTACHMENT_ENABLED);
                // Override the document entry
                if (attachmentEnabled != null) {
                    // This is a hack for KULRICE-1602 since the document entry is modified by a
                    // global configuration that overrides the document templates without some sort
                    // of rules or control
                    //DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();
                    DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();

                    DocumentEntry entry = (DocumentEntry) dataDictionary.getDocumentEntry(document.getClass().getName());
                    entry.setAllowsNoteAttachments(Boolean.parseBoolean(attachmentEnabled));
                }
                //the request attribute will be used in KualiRequestProcess#processActionPerform
                if (exitingDocument()) {
                    request.setAttribute(KRADConstants.EXITING_DOCUMENT, Boolean.TRUE);
                }

                // pessimistic locking
                String methodCalledViaDispatch = (String) GlobalVariables.getUserSession().retrieveObject(DocumentAuthorizerBase.USER_SESSION_METHOD_TO_CALL_OBJECT_KEY);
                if ((StringUtils.isNotBlank(methodCalledViaDispatch)) && (exitingDocument())) {
                    GlobalVariables.getUserSession().removeObject(DocumentAuthorizerBase.USER_SESSION_METHOD_TO_CALL_COMPLETE_OBJECT_KEY);
                    attemptLockRelease(document, methodCalledViaDispatch);
                }
                setupPessimisticLockMessages(document, request);
                if (!document.getPessimisticLocks().isEmpty()) {
                    String warningMinutes = getParameterService().getParameterValueAsString(KRADConstants.KRAD_NAMESPACE, KRADConstants.DetailTypes.DOCUMENT_DETAIL_TYPE, KRADConstants.SESSION_TIMEOUT_WARNING_MESSAGE_TIME_PARM_NM);
                    request.setAttribute(KRADConstants.SESSION_TIMEOUT_WARNING_MINUTES, warningMinutes);
                    request.setAttribute(KRADConstants.SESSION_TIMEOUT_WARNING_MILLISECONDS, (request.getSession().getMaxInactiveInterval() - (Integer.valueOf(warningMinutes) * 60)) * 1000);
                }
            }
        }

        return returnForward;
    }

    protected boolean isFormRepresentingLockObject(KualiDocumentFormBase form) throws Exception {
        if (form instanceof KualiMaintenanceForm) {
            KualiMaintenanceForm maintForm = (KualiMaintenanceForm) form;
            if (ObjectUtils.isNotNull(maintForm.getBusinessObjectClassName())) {
                return PessimisticLock.class.isAssignableFrom(Class.forName(((KualiMaintenanceForm) form).getBusinessObjectClassName()));
            }
        }
        return false;
    }

    protected void attemptLockRelease(Document document, String methodToCall) {
        if ((document != null) && (!document.getPessimisticLocks().isEmpty())) {
            releaseLocks(document, methodToCall);
            // refresh pessimistic locks in case custom add/remove changes were made
            //document.refreshPessimisticLocks();
        }
    }

    protected void releaseLocks(Document document, String methodToCall) {
        // first check if the method to call is listed as required lock clearing
        if (document.getLockClearningMethodNames().contains(methodToCall)) {
            // find all locks for the current user and remove them
            getPessimisticLockService().releaseAllLocksForUser(document.getPessimisticLocks(), GlobalVariables.getUserSession().getPerson());
        }
    }

    protected void setupPessimisticLockMessages(Document document, HttpServletRequest request) {
        List<String> lockMessages = new ArrayList<String>();
        for (PessimisticLock lock : document.getPessimisticLocks()) {
            // if lock is owned by current user, do not display message for it
            if (!lock.isOwnedByUser(GlobalVariables.getUserSession().getPerson())) {
                lockMessages.add(generatePessimisticLockMessage(lock));
            }
        }
        request.setAttribute(KRADConstants.PESSIMISTIC_LOCK_MESSAGES, lockMessages);
    }

    protected String generatePessimisticLockMessage(PessimisticLock lock) {
        String descriptor = (lock.getLockDescriptor() != null) ? lock.getLockDescriptor() : "";
        // TODO: this should be pulled into a properties file
        return "This document currently has a " + descriptor + " lock owned by " + lock.getOwnedByUser().getName() + " as of " + RiceConstants.getDefaultTimeFormat().format(lock.getGeneratedTimestamp()) + " on " + RiceConstants.getDefaultDateFormat().format(lock.getGeneratedTimestamp());
    }

//    private void saveMessages(HttpServletRequest request) {
//        if (!GlobalVariables.getMessageList().isEmpty()) {
//            request.setAttribute(KRADConstants.GLOBAL_MESSAGES, GlobalVariables.getMessageList());
//        }
//    }

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
        } else if (KEWConstants.INITIATE_COMMAND.equals(command)) {
            createDocument(kualiDocumentFormBase);
        } else {
            LOG.error("docHandler called with invalid parameters");
            throw new IllegalStateException("docHandler called with invalid parameters");
        }

        // attach any extra JS from the data dictionary
        if (LOG.isDebugEnabled()) {
            LOG.debug("kualiDocumentFormBase.getAdditionalScriptFiles(): " + kualiDocumentFormBase.getAdditionalScriptFiles());
        }
        if (kualiDocumentFormBase.getAdditionalScriptFiles().isEmpty()) {
            DocumentEntry docEntry = (DocumentEntry) getDataDictionaryService().getDataDictionary().getDocumentEntry(kualiDocumentFormBase.getDocument().getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
            kualiDocumentFormBase.getAdditionalScriptFiles().addAll(docEntry.getWebScriptFiles());
        }
        if (KEWConstants.SUPERUSER_COMMAND.equalsIgnoreCase(command)) {
            kualiDocumentFormBase.setSuppressAllButtons(true);
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
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
        doc = getDocumentService().getByDocumentHeaderId(docId);
        if (doc == null) {
            throw new UnknownDocumentIdException("Document no longer exists.  It may have been cancelled before being saved.");
        }
        WorkflowDocument workflowDocument = doc.getDocumentHeader().getWorkflowDocument();
        if (!getDocumentHelperService().getDocumentAuthorizer(doc).canOpen(doc, GlobalVariables.getUserSession().getPerson())) {
            throw buildAuthorizationException("open", doc);
        }
        // re-retrieve the document using the current user's session - remove the system user from the WorkflowDcument object
        if (workflowDocument != doc.getDocumentHeader().getWorkflowDocument()) {
            LOG.warn("Workflow document changed via canOpen check");
            doc.getDocumentHeader().setWorkflowDocument(workflowDocument);
        }
        kualiDocumentFormBase.setDocument(doc);
        WorkflowDocument workflowDoc = doc.getDocumentHeader().getWorkflowDocument();
        kualiDocumentFormBase.setDocTypeName(workflowDoc.getDocumentTypeName());
        // KualiDocumentFormBase.populate() needs this updated in the session
        KRADServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(), workflowDoc);
    }


    /**
     * This method creates a new document of the type specified by the docTypeName property of the given form. This has been
     * abstracted out so that it can be overridden in children if the need arises.
     *
     * @param kualiDocumentFormBase
     * @throws WorkflowException
     */
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        Document doc = getDocumentService().getNewDocument(kualiDocumentFormBase.getDocTypeName());

        kualiDocumentFormBase.setDocument(doc);
        kualiDocumentFormBase.setDocTypeName(doc.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
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


        // check authorization for adding ad hoc route person
        DocumentAuthorizer documentAuthorizer = getDocumentHelperService().getDocumentAuthorizer(document);
        if (!documentAuthorizer.canSendAdHocRequests(document, kualiDocumentFormBase.getNewAdHocRoutePerson().getActionRequested(), GlobalVariables.getUserSession().getPerson())) {
            throw buildAuthorizationException("ad-hoc route", document);
        }

        // check business rules
        boolean rulePassed = getKualiRuleService().applyRules(new AddAdHocRoutePersonEvent(document, kualiDocumentFormBase.getNewAdHocRoutePerson()));

        // if the rule evaluation passed, let's add the ad hoc route person
        if (rulePassed) {
            // uppercase userid for consistency
//            kualiDocumentFormBase.getNewAdHocRoutePerson().setId(StringUtils.upperCase(kualiDocumentFormBase.getNewAdHocRoutePerson().getId()));
            kualiDocumentFormBase.getNewAdHocRoutePerson().setId(kualiDocumentFormBase.getNewAdHocRoutePerson().getId());
            kualiDocumentFormBase.getAdHocRoutePersons().add(kualiDocumentFormBase.getNewAdHocRoutePerson());
            AdHocRoutePerson person = new AdHocRoutePerson();
            kualiDocumentFormBase.setNewAdHocRoutePerson(person);
        }

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
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


        kualiDocumentFormBase.getAdHocRoutePersons().remove(this.getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
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

        // check authorization for add ad hoc route workgroup
        DocumentAuthorizer documentAuthorizer = getDocumentHelperService().getDocumentAuthorizer(document);
        if (!documentAuthorizer.canSendAdHocRequests(document, kualiDocumentFormBase.getNewAdHocRouteWorkgroup().getActionRequested(), GlobalVariables.getUserSession().getPerson())) {
            throw buildAuthorizationException("ad-hoc route", document);
        }

        // check business rules
        boolean rulePassed = getKualiRuleService().applyRules(new AddAdHocRouteWorkgroupEvent(document, kualiDocumentFormBase.getNewAdHocRouteWorkgroup()));

        // if the rule evaluation passed, let's add the ad hoc route workgroup
        if (rulePassed) {
            //fill id if not already filled
            AdHocRouteWorkgroup newWorkgroup = kualiDocumentFormBase.getNewAdHocRouteWorkgroup();
            if (newWorkgroup.getId() == null) {
                newWorkgroup.setId(KimApiServiceLocator.getGroupService().getGroupByName(newWorkgroup.getRecipientNamespaceCode(), newWorkgroup.getRecipientName()).getId());
            }
            kualiDocumentFormBase.getAdHocRouteWorkgroups().add(newWorkgroup);
            AdHocRouteWorkgroup workgroup = new AdHocRouteWorkgroup();
            kualiDocumentFormBase.setNewAdHocRouteWorkgroup(workgroup);
        }

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
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

        kualiDocumentFormBase.getAdHocRouteWorkgroups().remove(this.getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward sendAdHocRequests(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        Document document = kualiDocumentFormBase.getDocument();

        boolean rulePassed = getKualiRuleService().applyRules(new SendAdHocRequestsEvent(document));

        if (rulePassed) {
            getDocumentService().sendAdHocRequests(document, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
            KNSGlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_SEND_AD_HOC_REQUESTS_SUCCESSFUL);
        }

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
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

        // prepare for the reload action - set doc id and command
        kualiDocumentFormBase.setDocId(document.getDocumentNumber());
        kualiDocumentFormBase.setCommand(DOCUMENT_LOAD_COMMANDS[1]);

        // forward off to the doc handler
        ActionForward actionForward = docHandler(mapping, form, request, response);

        KNSGlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_RELOADED);
        // TODO: remove this when further testing passed
//        if (form instanceof KualiDocumentFormBase) {
//            UserSession userSession = (UserSession) request.getSession().getAttribute(RiceConstants.USER_SESSION_KEY);
//            // force to recreate formkey in execute method
//            if (document instanceof SessionDocumentService && userSession.retrieveObject(kualiDocumentFormBase.getFormKey()) != null) {
//        	userSession.removeObject(kualiDocumentFormBase.getFormKey());;
//            }
//        }

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
        doProcessingAfterPost(kualiDocumentFormBase, request);
        //get any possible changes to to adHocWorkgroups
        refreshAdHocRoutingWorkgroupLookups(request, kualiDocumentFormBase);
        Document document = kualiDocumentFormBase.getDocument();

        ActionForward forward = checkAndWarnAboutSensitiveData(mapping, form, request, response, KRADPropertyConstants.DOCUMENT_EXPLANATION, document.getDocumentHeader().getExplanation(), "save", "");
        if (forward != null) {
            return forward;
        }

        // save in workflow
        getDocumentService().saveDocument(document);

        KNSGlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_SAVED);
        kualiDocumentFormBase.setAnnotation("");

        // TODO: remove this when further testing passed
//        if (form instanceof KualiDocumentFormBase) {
//            UserSession userSession = (UserSession) request.getSession().getAttribute(RiceConstants.USER_SESSION_KEY);
//            // force to recreate formkey in execute method
//            if (document instanceof SessionDocumentService && userSession.retrieveObject(kualiDocumentFormBase.getFormKey()) != null) {
//        	userSession.removeObject(kualiDocumentFormBase.getFormKey());;
//            }
//        }

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * Checks if the given value matches patterns that indicate sensitive data and if configured to give a warning for sensitive data will
     * prompt the user to continue
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param fieldName  - name of field with value being checked
     * @param fieldValue - value to check for sensitive data
     * @param caller     - method that should be called back from question
     * @param context    - additional context that needs to be passed back with the question response
     * @return ActionForward which contains the question forward, or basic forward if user select no to prompt, otherwise will return null
     *         to indicate processing should continue
     * @throws Exception
     */
    protected ActionForward checkAndWarnAboutSensitiveData(ActionMapping mapping, ActionForm form,
                                                           HttpServletRequest request, HttpServletResponse response, String fieldName, String fieldValue, String caller, String context)
            throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        Document document = kualiDocumentFormBase.getDocument();

        boolean containsSensitiveData = KRADUtils.containsSensitiveDataPatternMatch(fieldValue);

        // check if warning is configured in which case we will prompt, or if not business rules will thrown an error
        boolean warnForSensitiveData = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(
                KRADConstants.KRAD_NAMESPACE, ParameterConstants.ALL_COMPONENT,
                KRADConstants.SystemGroupParameterNames.SENSITIVE_DATA_PATTERNS_WARNING_IND);

        // determine if the question has been asked yet
        Map<String, String> ticketContext = new HashMap<String, String>();
        ticketContext.put(KRADPropertyConstants.DOCUMENT_NUMBER, document.getDocumentNumber());
        ticketContext.put(KRADConstants.CALLING_METHOD, caller);
        ticketContext.put(KRADPropertyConstants.NAME, fieldName);

        boolean questionAsked = GlobalVariables.getUserSession().hasMatchingSessionTicket(
                KRADConstants.SENSITIVE_DATA_QUESTION_SESSION_TICKET, ticketContext);

        // start in logic for confirming the sensitive data
        if (containsSensitiveData && warnForSensitiveData && !questionAsked) {
            Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null || !KRADConstants.DOCUMENT_SENSITIVE_DATA_QUESTION.equals(question)) {

                // question hasn't been asked, prompt to continue
                return this.performQuestionWithoutInput(mapping, form, request, response,
                        KRADConstants.DOCUMENT_SENSITIVE_DATA_QUESTION, getKualiConfigurationService()
                        .getPropertyValueAsString(RiceKeyConstants.QUESTION_SENSITIVE_DATA_DOCUMENT),
                        KRADConstants.CONFIRMATION_QUESTION, caller, context);
            }

            Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
            if (question != null && KRADConstants.DOCUMENT_SENSITIVE_DATA_QUESTION.equals(question)) {
                // if no button clicked just reload the doc
                if (ConfirmationQuestion.NO.equals(buttonClicked)) {

                    return mapping.findForward(RiceConstants.MAPPING_BASIC);
                }

                // answered yes, create session ticket so we not to ask question again if there are further question requests
                SessionTicket ticket = new SessionTicket(KRADConstants.SENSITIVE_DATA_QUESTION_SESSION_TICKET);
                ticket.setTicketContext(ticketContext);
                GlobalVariables.getUserSession().putSessionTicket(ticket);
            }
        }

        // return null to indicate processing should continue (no redirect)
        return null;
    }

    /**
     * This method will verify that the form is representing a {@link PessimisticLock} object and delete it if possible
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        if (isFormRepresentingLockObject(kualiDocumentFormBase)) {
            String idValue = request.getParameter(KRADPropertyConstants.ID);
            getPessimisticLockService().delete(idValue);
            return returnToSender(request, mapping, kualiDocumentFormBase);
        }
        throw buildAuthorizationException(KRADConstants.DELETE_METHOD, kualiDocumentFormBase.getDocument());
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

        kualiDocumentFormBase.setDerivedValuesOnForm(request);
        ActionForward preRulesForward = promptBeforeValidation(mapping, form, request, response);
        if (preRulesForward != null) {
            return preRulesForward;
        }

        Document document = kualiDocumentFormBase.getDocument();
        // check authorization for reloading document
        //DocumentActionFlags flags = getDocumentActionFlags(document);
        if (!kualiDocumentFormBase.getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_PERFORM_ROUTE_REPORT)) {
            throw buildAuthorizationException("perform route report", document);
        }

        String backUrlBase = getReturnLocation(request, mapping);
        String globalVariableFormKey = GlobalVariables.getUserSession().addObjectWithGeneratedKey(form);
        // setup back form variables
        request.setAttribute("backUrlBase", backUrlBase);
        List<KeyValue> backFormParameters = new ArrayList<KeyValue>();
        backFormParameters.add(new ConcreteKeyValue(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.RETURN_METHOD_TO_CALL));
        backFormParameters.add(new ConcreteKeyValue(KRADConstants.DOC_FORM_KEY, globalVariableFormKey));
        request.setAttribute("backFormHiddenVariables", backFormParameters);

        // setup route report form variables
        request.setAttribute("workflowRouteReportUrl", getKualiConfigurationService().getPropertyValueAsString(
                KRADConstants.WORKFLOW_URL_KEY) + "/" + KEWConstants.DOCUMENT_ROUTING_REPORT_PAGE);
        List<KeyValue> generalRouteReportFormParameters = new ArrayList<KeyValue>();
        generalRouteReportFormParameters.add(new ConcreteKeyValue(KEWConstants.INITIATOR_ID_ATTRIBUTE_NAME, document.getDocumentHeader().getWorkflowDocument().getDocument().getInitiatorPrincipalId()));
        generalRouteReportFormParameters.add(new ConcreteKeyValue(KEWConstants.DOCUMENT_TYPE_NAME_ATTRIBUTE_NAME, document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName()));
        // prepareForRouteReport() method should populate document header workflow document application content xml
        String xml = document.getXmlForRouteReport();
        if (LOG.isDebugEnabled()) {
            LOG.debug("XML being used for Routing Report is: " + xml);
        }
        generalRouteReportFormParameters.add(new ConcreteKeyValue(KEWConstants.DOCUMENT_CONTENT_ATTRIBUTE_NAME, xml));

        // set up the variables for the form if java script is working (includes a close button variable and no back url)
        List<KeyValue> javaScriptFormParameters = new ArrayList<KeyValue>();
        javaScriptFormParameters.addAll(generalRouteReportFormParameters);
        javaScriptFormParameters.add(new ConcreteKeyValue(KEWConstants.DISPLAY_CLOSE_BUTTON_ATTRIBUTE_NAME, KEWConstants.DISPLAY_CLOSE_BUTTON_TRUE_VALUE));
        request.setAttribute("javaScriptFormVariables", javaScriptFormParameters);

        // set up the variables for the form if java script is NOT working (includes a back url but no close button)
        List<KeyValue> noJavaScriptFormParameters = new ArrayList<KeyValue>();
        noJavaScriptFormParameters.addAll(generalRouteReportFormParameters);
        Properties parameters = new Properties();
        for (KeyValue pair : backFormParameters) {
            parameters.put(pair.getKey(), pair.getValue());
        }
        noJavaScriptFormParameters.add(new ConcreteKeyValue(KEWConstants.RETURN_URL_ATTRIBUTE_NAME, UrlFactory.parameterizeUrl(backUrlBase, parameters)));
        request.setAttribute("noJavaScriptFormVariables", noJavaScriptFormParameters);

        return mapping.findForward(KRADConstants.MAPPING_ROUTE_REPORT);
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
        doProcessingAfterPost(kualiDocumentFormBase, request);

        kualiDocumentFormBase.setDerivedValuesOnForm(request);
        ActionForward preRulesForward = promptBeforeValidation(mapping, form, request, response);
        if (preRulesForward != null) {
            return preRulesForward;
        }

        Document document = kualiDocumentFormBase.getDocument();

        ActionForward forward = checkAndWarnAboutSensitiveData(mapping, form, request, response, KRADPropertyConstants.DOCUMENT_EXPLANATION, document.getDocumentHeader().getExplanation(), "route", "");
        if (forward != null) {
            return forward;
        }

        getDocumentService().routeDocument(document, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
        KNSGlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_ROUTE_SUCCESSFUL);
        kualiDocumentFormBase.setAnnotation("");

//        GlobalVariables.getUserSession().addObject(DocumentAuthorizerBase.USER_SESSION_METHOD_TO_CALL_COMPLETE_OBJECT_KEY,Boolean.TRUE);
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
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
        doProcessingAfterPost(kualiDocumentFormBase, request);

        kualiDocumentFormBase.setDerivedValuesOnForm(request);
        ActionForward preRulesForward = promptBeforeValidation(mapping, form, request, response);
        if (preRulesForward != null) {
            return preRulesForward;
        }

        Document document = kualiDocumentFormBase.getDocument();

        ActionForward forward = checkAndWarnAboutSensitiveData(mapping, form, request, response, KRADPropertyConstants.DOCUMENT_EXPLANATION, document.getDocumentHeader().getExplanation(), "blanketApprove", "");
        if (forward != null) {
            return forward;
        }

        getDocumentService().blanketApproveDocument(document, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
        KNSGlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_ROUTE_APPROVED);
        kualiDocumentFormBase.setAnnotation("");
        return returnToSender(request, mapping, kualiDocumentFormBase);
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
        doProcessingAfterPost(kualiDocumentFormBase, request);

        kualiDocumentFormBase.setDerivedValuesOnForm(request);
        ActionForward preRulesForward = promptBeforeValidation(mapping, form, request, response);
        if (preRulesForward != null) {
            return preRulesForward;
        }

        Document document = kualiDocumentFormBase.getDocument();

        ActionForward forward = checkAndWarnAboutSensitiveData(mapping, form, request, response, KRADPropertyConstants.DOCUMENT_EXPLANATION, document.getDocumentHeader().getExplanation(), "approve", "");
        if (forward != null) {
            return forward;
        }

        getDocumentService().approveDocument(document, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
        KNSGlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_ROUTE_APPROVED);
        kualiDocumentFormBase.setAnnotation("");
        return returnToSender(request, mapping, kualiDocumentFormBase);
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
    public ActionForward disapprove(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME);

        if (StringUtils.isBlank(reason)) {
            String context = request.getParameter(KRADConstants.QUESTION_CONTEXT);
            if (context != null && StringUtils.contains(context, KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME + "=")) {
                reason = StringUtils.substringAfter(context, KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME + "=");
            }
        }

        String disapprovalNoteText = "";

        // start in logic for confirming the disapproval
        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithInput(mapping, form, request, response,
                    KRADConstants.DOCUMENT_DISAPPROVE_QUESTION,
                    getKualiConfigurationService().getPropertyValueAsString(
                            RiceKeyConstants.QUESTION_DISAPPROVE_DOCUMENT),
                    KRADConstants.CONFIRMATION_QUESTION, KRADConstants.MAPPING_DISAPPROVE, "");
        }
        Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
        if ((KRADConstants.DOCUMENT_DISAPPROVE_QUESTION.equals(question))
                && ConfirmationQuestion.NO.equals(buttonClicked)) {
            // if no button clicked just reload the doc
            return mapping.findForward(RiceConstants.MAPPING_BASIC);
        }

        // have to check length on value entered
        String introNoteMessage = getKualiConfigurationService().getPropertyValueAsString(
                RiceKeyConstants.MESSAGE_DISAPPROVAL_NOTE_TEXT_INTRO)
                + KRADConstants.BLANK_SPACE;

        // build out full message
        disapprovalNoteText = introNoteMessage + reason;

        // check for sensitive data in note
        boolean warnForSensitiveData = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(
                KRADConstants.KRAD_NAMESPACE, ParameterConstants.ALL_COMPONENT,
                KRADConstants.SystemGroupParameterNames.SENSITIVE_DATA_PATTERNS_WARNING_IND);
        if (warnForSensitiveData) {
            String context = KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME + "=" + reason;
            ActionForward forward = checkAndWarnAboutSensitiveData(mapping, form, request, response,
                    KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME, disapprovalNoteText, "disapprove", context);
            if (forward != null) {
                return forward;
            }
        } else {
            if (KRADUtils.containsSensitiveDataPatternMatch(disapprovalNoteText)) {
                return this
                        .performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response,
                                KRADConstants.DOCUMENT_DISAPPROVE_QUESTION, getKualiConfigurationService()
                                .getPropertyValueAsString(RiceKeyConstants.QUESTION_DISAPPROVE_DOCUMENT),
                                KRADConstants.CONFIRMATION_QUESTION, KRADConstants.MAPPING_DISAPPROVE, "", reason,
                                RiceKeyConstants.ERROR_DOCUMENT_FIELD_CONTAINS_POSSIBLE_SENSITIVE_DATA,
                                KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME, "reason");
            }
        }

        int disapprovalNoteTextLength = disapprovalNoteText.length();

        // get note text max length from DD
        int noteTextMaxLength = getDataDictionaryService().getAttributeMaxLength(Note.class,
                KRADConstants.NOTE_TEXT_PROPERTY_NAME);

        if (StringUtils.isBlank(reason) || (disapprovalNoteTextLength > noteTextMaxLength)) {

            if (reason == null) {
                // prevent a NPE by setting the reason to a blank string
                reason = "";
            }
            return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response,
                    KRADConstants.DOCUMENT_DISAPPROVE_QUESTION,
                    getKualiConfigurationService().getPropertyValueAsString(
                            RiceKeyConstants.QUESTION_DISAPPROVE_DOCUMENT),
                    KRADConstants.CONFIRMATION_QUESTION, KRADConstants.MAPPING_DISAPPROVE, "", reason,
                    RiceKeyConstants.ERROR_DOCUMENT_DISAPPROVE_REASON_REQUIRED,
                    KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME, Integer.toString(noteTextMaxLength));
        }

        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        doProcessingAfterPost(kualiDocumentFormBase, request);
        getDocumentService().disapproveDocument(kualiDocumentFormBase.getDocument(), disapprovalNoteText);
        KNSGlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_ROUTE_DISAPPROVED);
        kualiDocumentFormBase.setAnnotation("");

        return returnToSender(request, mapping, kualiDocumentFormBase);
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
        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        // this should probably be moved into a private instance variable
        // logic for cancel question
        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithoutInput(mapping, form, request, response, KRADConstants.DOCUMENT_CANCEL_QUESTION, getKualiConfigurationService().getPropertyValueAsString(
                    "document.question.cancel.text"), KRADConstants.CONFIRMATION_QUESTION, KRADConstants.MAPPING_CANCEL, "");
        } else {
            Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
            if ((KRADConstants.DOCUMENT_CANCEL_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                // if no button clicked just reload the doc
                return mapping.findForward(RiceConstants.MAPPING_BASIC);
            }
            // else go to cancel logic below
        }

        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        doProcessingAfterPost(kualiDocumentFormBase, request);
        // KULRICE-4447 Call cancelDocument() only if the document exists
        if (getDocumentService().documentExists(kualiDocumentFormBase.getDocId())) {
            getDocumentService().cancelDocument(kualiDocumentFormBase.getDocument(), kualiDocumentFormBase.getAnnotation());
        }

        return returnToSender(request, mapping, kualiDocumentFormBase);
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
        doProcessingAfterPost(docForm, request);
        Document document = docForm.getDocument();
        // only want to prompt them to save if they already can save
        if (canSave(docForm)) {
            Object question = getQuestion(request);
            // logic for close question
            if (question == null) {
                // ask question if not already asked
                return this.performQuestionWithoutInput(mapping, form, request, response, KRADConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, getKualiConfigurationService().getPropertyValueAsString(
                        RiceKeyConstants.QUESTION_SAVE_BEFORE_CLOSE), KRADConstants.CONFIRMATION_QUESTION, KRADConstants.MAPPING_CLOSE, "");
            } else {
                Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
                if ((KRADConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                    // if yes button clicked - save the doc
                    ActionForward forward = checkAndWarnAboutSensitiveData(mapping, form, request, response, KRADPropertyConstants.DOCUMENT_EXPLANATION, document.getDocumentHeader().getExplanation(), "save", "");
                    if (forward != null) {
                        return forward;
                    }

                    getDocumentService().saveDocument(docForm.getDocument());
                }
                // else go to close logic below
            }
        }

        return returnToSender(request, mapping, docForm);
    }

    protected boolean canSave(ActionForm form) {
        KualiDocumentFormBase docForm = (KualiDocumentFormBase) form;
        return docForm.getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_CAN_SAVE);
    }

    protected Object getQuestion(HttpServletRequest request) {
        return request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
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
        doProcessingAfterPost(kualiDocumentFormBase, request);
        getDocumentService().clearDocumentFyi(kualiDocumentFormBase.getDocument(), combineAdHocRecipients(kualiDocumentFormBase));
        KNSGlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_ROUTE_FYIED);
        kualiDocumentFormBase.setAnnotation("");
        return returnToSender(request, mapping, kualiDocumentFormBase);
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
        doProcessingAfterPost(kualiDocumentFormBase, request);
        getDocumentService().acknowledgeDocument(kualiDocumentFormBase.getDocument(), kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
        KNSGlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_ROUTE_ACKNOWLEDGED);
        kualiDocumentFormBase.setAnnotation("");
        return returnToSender(request, mapping, kualiDocumentFormBase);
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


        String workflowSuperUserUrl = getKualiConfigurationService().getPropertyValueAsString(
                KRADConstants.WORKFLOW_URL_KEY) + "/SuperUser.do?methodToCall=displaySuperUserDocument&documentId=" + kualiDocumentFormBase.getDocument().getDocumentHeader().getDocumentNumber();
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
    protected List<AdHocRouteRecipient> combineAdHocRecipients(KualiDocumentFormBase kualiDocumentFormBase) {
        List<AdHocRouteRecipient> adHocRecipients = new ArrayList<AdHocRouteRecipient>();
        adHocRecipients.addAll(kualiDocumentFormBase.getAdHocRoutePersons());
        adHocRecipients.addAll(kualiDocumentFormBase.getAdHocRouteWorkgroups());
        return adHocRecipients;
    }

    /**
     * if the action desires to retain error messages generated by the rules framework for save/submit/etc. validation after returning from a lookup.
     *
     * @see KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiForm = (KualiDocumentFormBase) form;
        kualiForm.setDerivedValuesOnForm(request);

        super.refresh(mapping, form, request, response);
        refreshAdHocRoutingWorkgroupLookups(request, kualiForm);

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * special refresh needed to get the workgroups populated correctly when coming back from workgroup lookups
     *
     * @param request
     * @param kualiForm
     * @throws WorkflowException
     */
    @SuppressWarnings("unchecked")
    protected void refreshAdHocRoutingWorkgroupLookups(HttpServletRequest request, KualiDocumentFormBase kualiForm) throws WorkflowException {
        for (Enumeration<String> i = request.getParameterNames(); i.hasMoreElements();) {
            String parameterName = i.nextElement();
            if (parameterName.equals("newAdHocRouteWorkgroup.recipientName") && !"".equals(request.getParameter(parameterName))) {
                //check for namespace
                String namespace = KimConstants.KIM_GROUP_DEFAULT_NAMESPACE_CODE;
                if (request.getParameter("newAdHocRouteWorkgroup.recipientNamespaceCode") != null && !"".equals(request.getParameter("newAdHocRouteWorkgroup.recipientName").trim())) {
                    namespace = request.getParameter("newAdHocRouteWorkgroup.recipientNamespaceCode").trim();
                }
                Group group = getGroupService().getGroupByName(namespace, request.getParameter(parameterName));
                if (group != null) {
                    kualiForm.getNewAdHocRouteWorkgroup().setId(group.getId());
                    kualiForm.getNewAdHocRouteWorkgroup().setRecipientName(group.getName());
                    kualiForm.getNewAdHocRouteWorkgroup().setRecipientNamespaceCode(group.getNamespaceCode());
                } else {
                    throw new RuntimeException("Invalid workgroup id passed as parameter.");
                }
            }
            if (parameterName.startsWith("adHocRouteWorkgroup[") && !"".equals(request.getParameter(parameterName))) {
                if (parameterName.endsWith(".recipientName")) {
                    int lineNumber = Integer.parseInt(StringUtils.substringBetween(parameterName, "[", "]"));
                    //check for namespace
                    String namespaceParam = "adHocRouteWorkgroup[" + lineNumber + "].recipientNamespaceCode";
                    String namespace = KimConstants.KIM_GROUP_DEFAULT_NAMESPACE_CODE;
                    if (request.getParameter(namespaceParam) != null && !"".equals(request.getParameter(namespaceParam).trim())) {
                        namespace = request.getParameter(namespaceParam).trim();
                    }
                    Group group = getGroupService().getGroupByName(namespace, request.getParameter(parameterName));
                    if (group != null) {
                        kualiForm.getAdHocRouteWorkgroup(lineNumber).setId(group.getId());
                        kualiForm.getAdHocRouteWorkgroup(lineNumber).setRecipientName(group.getName());
                        kualiForm.getAdHocRouteWorkgroup(lineNumber).setRecipientNamespaceCode(group.getNamespaceCode());
                    } else {
                        throw new RuntimeException("Invalid workgroup id passed as parameter.");
                    }
                }
            }
            /*
            if (parameterName.startsWith("newAdHocRouteWorkgroup[") && !"".equals(request.getParameter(parameterName))) {
                if (parameterName.endsWith(".recipientName")) {
                    int lineNumber = Integer.parseInt(StringUtils.substringBetween(parameterName, "[", "]"));
                  //check for namespace
                    String namespaceParam = "newAdHocRouteWorkgroup[" + lineNumber + "].recipientNamespaceCode";
                    String namespace = KimConstants.KIM_GROUP_DEFAULT_NAMESPACE_CODE;
                    if (request.getParameter(namespaceParam) != null && !"".equals(request.getParameter(namespaceParam).trim())) {
                        namespace = request.getParameter(namespaceParam).trim();
                    }
                    KimGroup group = getIdentityManagementService().getGroupByName(namespace, request.getParameter(parameterName));
                    if (group != null) {
                        kualiForm.getAdHocRouteWorkgroup(lineNumber).setId(group.getGroupId());
                        kualiForm.getAdHocRouteWorkgroup(lineNumber).setRecipientName(group.getGroupName());
                        kualiForm.getAdHocRouteWorkgroup(lineNumber).setRecipientNamespaceCode(group.getNamespaceCode());
                    } else {
                        throw new RuntimeException("Invalid workgroup id passed as parameter.");
                    }
                }
            }
            */
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

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * Handy method to stream the byte array to response object
     *
     * @param fileContents
     * @param fileName
     * @param fileContentType
     * @param response
     * @throws Exception
     */
    protected void streamToResponse(byte[] fileContents, String fileName, String fileContentType, HttpServletResponse response) throws Exception {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream(fileContents.length);
            baos.write(fileContents);
            WebUtils.saveMimeOutputStreamAsFile(response, fileContentType, baos, fileName);
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                    baos = null;
                }
            } catch (IOException ioEx) {
                LOG.error("Error while downloading attachment");
                throw new RuntimeException("IOException occurred while downloading attachment", ioEx);
            }
        }
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
        KualiDocumentFormBase documentForm = (KualiDocumentFormBase) form;

        int attachmentIndex = selectedAttachmentIndex(request);
        if (attachmentIndex >= 0) {
            Note note = documentForm.getDocument().getNote(attachmentIndex);
            Attachment attachment = note.getAttachment();
            //make sure attachment is setup with backwards reference to note (rather then doing this we could also just call the attachment service (with a new method that took in the note)
            attachment.setNote(note);

            // since we're downloading a file, all of the editable properties from the previous request will continue to be editable.
            documentForm.copyPopulateEditablePropertiesToActionEditableProperties();

            WebUtils.saveMimeInputStreamAsFile(response, attachment.getAttachmentMimeTypeCode(), attachment.getAttachmentContents(), attachment.getAttachmentFileName(), attachment.getAttachmentFileSize().intValue());
            return null;
        }

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }


    /**
     * @param request
     * @return index of the attachment whose download button was just pressed
     */
    protected int selectedAttachmentIndex(HttpServletRequest request) {
        int attachmentIndex = -1;

        String parameterName = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            String attachmentIndexParam = StringUtils.substringBetween(parameterName, ".attachment[", "].");

            try {
                attachmentIndex = Integer.parseInt(attachmentIndexParam);
            } catch (NumberFormatException ignored) {
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
        Note newNote = kualiDocumentFormBase.getNewNote();
        newNote.setNotePostedTimestampToCurrent();

        String attachmentTypeCode = null;

        FormFile attachmentFile = kualiDocumentFormBase.getAttachmentFile();
        if (attachmentFile == null) {
            GlobalVariables.getMessageMap().putError(
                    String.format("%s.%s",
                            KRADConstants.NEW_DOCUMENT_NOTE_PROPERTY_NAME,
                            KRADConstants.NOTE_ATTACHMENT_FILE_PROPERTY_NAME),
                    RiceKeyConstants.ERROR_UPLOADFILE_NULL);
            // This line was removed in order to continue to validates other
            // return mapping.findForward(RiceConstants.MAPPING_BASIC);
        }

        if (newNote.getAttachment() != null) {
            attachmentTypeCode = newNote.getAttachment().getAttachmentTypeCode();
        }

        // check authorization for adding notes
        DocumentAuthorizer documentAuthorizer = getDocumentHelperService().getDocumentAuthorizer(document);
        if (!documentAuthorizer.canAddNoteAttachment(document, attachmentTypeCode, GlobalVariables.getUserSession().getPerson())) {
            throw buildAuthorizationException("annotate", document);
        }

        // create the attachment first, so that failure-to-create-attachment can be treated as a validation failure

        Attachment attachment = null;
        if (attachmentFile != null && !StringUtils.isBlank(attachmentFile.getFileName())) {
            if (attachmentFile.getFileSize() == 0) {
                GlobalVariables.getMessageMap().putError(
                        String.format("%s.%s",
                                KRADConstants.NEW_DOCUMENT_NOTE_PROPERTY_NAME,
                                KRADConstants.NOTE_ATTACHMENT_FILE_PROPERTY_NAME),
                        RiceKeyConstants.ERROR_UPLOADFILE_EMPTY,
                        attachmentFile.getFileName());
                // This line was removed in order to continue to validates other
//                return mapping.findForward(RiceConstants.MAPPING_BASIC);
            } else {
                String attachmentType = null;
                Attachment newAttachment = kualiDocumentFormBase.getNewNote().getAttachment();
                if (newAttachment != null) {
                    attachmentType = newAttachment.getAttachmentTypeCode();
                }
                attachment = getAttachmentService().createAttachment(document.getNoteTarget(), attachmentFile.getFileName(), attachmentFile.getContentType(), attachmentFile.getFileSize(), attachmentFile.getInputStream(), attachmentType);
            }
        }

        DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();
        org.kuali.rice.krad.datadictionary.DocumentEntry entry = dataDictionary.getDocumentEntry(document.getClass().getName());

        if (entry.getDisplayTopicFieldInNotes()) {
            String topicText = kualiDocumentFormBase.getNewNote().getNoteTopicText();
            if (StringUtils.isBlank(topicText)) {
                GlobalVariables.getMessageMap().putError(
                        String.format("%s.%s",
                                KRADConstants.NEW_DOCUMENT_NOTE_PROPERTY_NAME,
                                KRADConstants.NOTE_TOPIC_TEXT_PROPERTY_NAME),
                        RiceKeyConstants.ERROR_REQUIRED,
                        "Note Topic (Note Topic)");
            }
        }

        // create a new note from the data passed in
        // TODO gah! this is awful
        Person kualiUser = GlobalVariables.getUserSession().getPerson();
        if (kualiUser == null) {
            throw new IllegalStateException("Current UserSession has a null Person.");
        }
        Note tmpNote = getNoteService().createNote(newNote, document.getNoteTarget(), kualiUser.getPrincipalId());

        ActionForward forward = checkAndWarnAboutSensitiveData(mapping, form, request, response, KRADPropertyConstants.NOTE, tmpNote.getNoteText(), "insertBONote", "");
        if (forward != null) {
            return forward;
        }

        // validate the note
        boolean rulePassed = getKualiRuleService().applyRules(new AddNoteEvent(document, tmpNote));

        // if the rule evaluation passed, let's add the note
        if (rulePassed) {
            tmpNote.refresh();


            DocumentHeader documentHeader = document.getDocumentHeader();

            // associate note with object now
            document.addNote(tmpNote);

            // persist the note if the document is already saved the getObjectId check is to get around a bug with certain documents where
            // "saved" doesn't really persist, if you notice any problems with missing notes check this line
            //maintenance document BO note should only be saved into table when document is in the PROCESSED workflow status
            if (!documentHeader.getWorkflowDocument().isInitiated() && StringUtils.isNotEmpty(document.getNoteTarget().getObjectId())
                    && !(document instanceof MaintenanceDocument && NoteType.BUSINESS_OBJECT.getCode().equals(tmpNote.getNoteTypeCode()))
                    ) {
                getNoteService().save(tmpNote);
            }
            // adding the attachment after refresh gets called, since the attachment record doesn't get persisted
            // until the note does (and therefore refresh doesn't have any attachment to autoload based on the id, nor does it
            // autopopulate the id since the note hasn't been persisted yet)
            if (attachment != null) {
                tmpNote.addAttachment(attachment);
                // save again for attachment, note this is because sometimes the attachment is added first to the above then ojb tries to save
                //without the PK on the attachment I think it is safer then trying to get the sequence manually
                if (!documentHeader.getWorkflowDocument().isInitiated() && StringUtils.isNotEmpty(document.getNoteTarget().getObjectId())
                        && !(document instanceof MaintenanceDocument && NoteType.BUSINESS_OBJECT.getCode().equals(tmpNote.getNoteTypeCode()))
                        ) {
                    getNoteService().save(tmpNote);
                }
            }


            // reset the new note back to an empty one
            kualiDocumentFormBase.setNewNote(new Note());
        }


        return mapping.findForward(RiceConstants.MAPPING_BASIC);
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


//        DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();
//        DocumentEntry entry = dataDictionary.getDocumentEntry(document.getClass().getName());

        // check authorization for adding notes
        //DocumentActionFlags flags = getDocumentActionFlags(document);
        //if (!kualiDocumentFormBase.getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_CAN_ANNOTATE)) {
        //    buildAuthorizationException("annotate", document);
        //    return mapping.findForward(RiceConstants.MAPPING_BASIC);
        //}

        // ok to delete the note/attachment
        // derive the note property from the newNote on the form
        Note newNote = kualiDocumentFormBase.getNewNote();
        Note note = document.getNote(getLineToDelete(request));
        Attachment attachment = note.getAttachment();
        String attachmentTypeCode = null;
        if (attachment != null) {
            attachmentTypeCode = attachment.getAttachmentTypeCode();
        }
        String authorUniversalIdentifier = note.getAuthorUniversalIdentifier();
        if (!WebUtils.canDeleteNoteAttachment(document, attachmentTypeCode, authorUniversalIdentifier)) {
            throw buildAuthorizationException("annotate", document);
        }

        if (attachment != null) { // only do this if the note has been persisted
            //KFSMI-798 - refresh() changed to refreshNonUpdateableReferences()
            //All references for the business object Attachment are auto-update="none",
            //so refreshNonUpdateableReferences() should work the same as refresh()
            if (note.getNoteIdentifier() != null) { // KULRICE-2343 don't blow away note reference if the note wasn't persisted
                attachment.refreshNonUpdateableReferences();
            }
            getAttachmentService().deleteAttachmentContents(attachment);
        }
        // delete the note if the document is already saved
        if (!document.getDocumentHeader().getWorkflowDocument().isInitiated()) {
            getNoteService().deleteNote(note);
        }
        document.removeNote(note);

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * Override this to customize which routing action to take when sending a note.  This method reads the system parameter
     * KR-NS/Document/SEND_NOTE_WORKFLOW_NOTIFICATION_ACTIONS to determine which action to take
     *
     * @param request
     * @param note
     * @return a value from {@link KEWConstants}
     */
    protected String determineNoteWorkflowNotificationAction(HttpServletRequest request, KualiDocumentFormBase kualiDocumentFormBase, Note note) {
        return getParameterService().getParameterValueAsString(KRADConstants.KRAD_NAMESPACE, KRADConstants.DetailTypes.DOCUMENT_DETAIL_TYPE, KRADConstants.SEND_NOTE_WORKFLOW_NOTIFICATION_ACTIONS_PARM_NM);
    }

    public ActionForward sendNoteWorkflowNotification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        Document document = kualiDocumentFormBase.getDocument();

        Note note = document.getNote(getSelectedLine(request));

        // verify recipient was specified
        if (StringUtils.isBlank(note.getAdHocRouteRecipient().getId())) {
            GlobalVariables.getMessageMap().putError(KRADPropertyConstants.NEW_DOCUMENT_NOTE, RiceKeyConstants.ERROR_SEND_NOTE_NOTIFICATION_RECIPIENT);
            return mapping.findForward(RiceConstants.MAPPING_BASIC);
        }
        // check recipient is valid
        else {
            note.getAdHocRouteRecipient().setActionRequested(determineNoteWorkflowNotificationAction(request, kualiDocumentFormBase, note));

            boolean rulePassed = getKualiRuleService().applyRules(new AddAdHocRoutePersonEvent(KRADPropertyConstants.NEW_DOCUMENT_NOTE, document, (AdHocRoutePerson) note.getAdHocRouteRecipient()));
            if (!rulePassed) {
                return mapping.findForward(RiceConstants.MAPPING_BASIC);
            }
        }

        // if document is saved, send notification
        if (!document.getDocumentHeader().getWorkflowDocument().isInitiated()) {
            getDocumentService().sendNoteRouteNotification(document, note, GlobalVariables.getUserSession().getPerson());

            // add success message
            KNSGlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_SEND_NOTE_NOTIFICATION_SUCCESSFUL);
        } else {
            GlobalVariables.getMessageMap().putError(KRADPropertyConstants.NEW_DOCUMENT_NOTE, RiceKeyConstants.ERROR_SEND_NOTE_NOTIFICATION_DOCSTATUS);
        }

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }


    /**
     * Generates detailed log messages for OptimisticLockExceptions
     *
     * @param e
     */
    private final void logOjbOptimisticLockException(OptimisticLockException e) {
        if (LOG.isInfoEnabled()) {
            StringBuffer message = new StringBuffer("caught OptimisticLockException, caused by ");
            Object sourceObject = e.getSourceObject();
            String infix = null;
            try {
                // try to add instance details
                infix = sourceObject.toString();
            } catch (Exception e2) {
                // just use the class name
                infix = sourceObject.getClass().getName();
            }
            message.append(infix);

            if (sourceObject instanceof PersistableBusinessObject) {
                PersistableBusinessObject persistableObject = (PersistableBusinessObject) sourceObject;
                message.append(" [versionNumber = ").append(persistableObject.getVersionNumber()).append("]");
            }

            LOG.info(message.toString(), e);
        }
    }


    /**
     * Makes calls to the PromptBeforeValidation specified for the document. If the class returns an actionforward, that forward
     * will be returned (thus controlling how execution occurs), or null.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward promptBeforeValidation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return promptBeforeValidation(mapping, form, request, response, "route");
    }

    /**
     * Makes calls to the PromptBeforeValidation specified for the document. If the class returns an actionforward, that forward
     * will be returned (thus controlling how execution occurs), or null.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param methodToCall
     * @return
     * @throws Exception
     */
    public ActionForward promptBeforeValidation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String methodToCall) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;

        /* callback to any pre rules check class */
        Class<? extends PromptBeforeValidation> promptBeforeValidationClass = getDataDictionaryService().getPromptBeforeValidationClass(kualiDocumentFormBase.getDocTypeName());
        if (LOG.isDebugEnabled()) {
            LOG.debug("PromptBeforeValidationClass: " + promptBeforeValidationClass);
        }
        if (promptBeforeValidationClass != null) {
            PromptBeforeValidation promptBeforeValidation = promptBeforeValidationClass.newInstance();
            PromptBeforeValidationEvent event = new PromptBeforeValidationEvent("Pre Maint route Check", "", kualiDocumentFormBase.getDocument());
            boolean continueRoute = promptBeforeValidation.processPrompts(form, request, event);
            if (!continueRoute) {
                if (event.isPerformQuestion()) {
                    return super.performQuestionWithoutInput(mapping, kualiDocumentFormBase, request, response, event.getQuestionId(), event.getQuestionText(), event.getQuestionType(), methodToCall, event.getQuestionContext());
                } else {
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
     * Convenience method for building authorization exceptions
     *
     * @param action
     * @param document
     */
    protected DocumentAuthorizationException buildAuthorizationException(String action, Document document) {
        return new DocumentAuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(), action, document.getDocumentNumber());
    }

    protected boolean exitingDocument() {
    	String methodCalledViaDispatch = (String) GlobalVariables.getUserSession().retrieveObject(DocumentAuthorizerBase.USER_SESSION_METHOD_TO_CALL_OBJECT_KEY);
        String methodCompleted = (String) GlobalVariables.getUserSession().retrieveObject(DocumentAuthorizerBase.USER_SESSION_METHOD_TO_CALL_COMPLETE_OBJECT_KEY);
        return StringUtils.isNotEmpty(methodCompleted) && StringUtils.isNotEmpty(methodCalledViaDispatch) && methodCompleted.startsWith(methodCalledViaDispatch);
    }

    protected void setupDocumentExit() {
    	String methodCalledViaDispatch = (String) GlobalVariables.getUserSession().retrieveObject(DocumentAuthorizerBase.USER_SESSION_METHOD_TO_CALL_OBJECT_KEY);
    	if(StringUtils.isNotEmpty(methodCalledViaDispatch)) {
    		GlobalVariables.getUserSession().addObject(DocumentAuthorizerBase.USER_SESSION_METHOD_TO_CALL_COMPLETE_OBJECT_KEY, (Object) (methodCalledViaDispatch + DocumentAuthorizerBase.USER_SESSION_METHOD_TO_CALL_COMPLETE_MARKER));
    	}
    }

    /**
     * If the given form has returnToActionList set to true, this method returns an ActionForward that should take the user back to
     * their action list; otherwise, it returns them to the portal.
     *
     * @param form
     * @return
     */
    protected ActionForward returnToSender(HttpServletRequest request, ActionMapping mapping, KualiDocumentFormBase form) {
        final ActionForward dest;
        if (form.isReturnToActionList()) {
            String workflowBase = getKualiConfigurationService().getPropertyValueAsString(
                    KRADConstants.WORKFLOW_URL_KEY);
            String actionListUrl = workflowBase + "/ActionList.do";

            dest = new ActionForward(actionListUrl, true);
        } else if (StringUtils.isNotBlank(form.getBackLocation())) {
            dest = new ActionForward(form.getBackLocation(), true);
        } else {
            dest = mapping.findForward(KRADConstants.MAPPING_PORTAL);
        }

        setupDocumentExit();
        return dest;
    }

    @SuppressWarnings("unchecked")
    protected void populateAuthorizationFields(KualiDocumentFormBase formBase) {
        if (formBase.isFormDocumentInitialized()) {
            Document document = formBase.getDocument();
            Person user = GlobalVariables.getUserSession().getPerson();
            DocumentPresentationController documentPresentationController = KRADServiceLocatorWeb
                    .getDocumentHelperService().getDocumentPresentationController(document);
            DocumentAuthorizer documentAuthorizer = getDocumentHelperService().getDocumentAuthorizer(document);
            Set<String> documentActions = documentPresentationController.getDocumentActions(document);
            documentActions = documentAuthorizer.getDocumentActions(document, user, documentActions);

            if (getDataDictionaryService().getDataDictionary().getDocumentEntry(document.getClass().getName()).getUsePessimisticLocking()) {
                documentActions = getPessimisticLockService().getDocumentActions(document, user, documentActions);
            }

            //DocumentActionFlags flags = new DocumentActionFlags();
            formBase.setDocumentActions(convertSetToMap(documentActions));

        }
    }

    protected void populateAdHocActionRequestCodes(KualiDocumentFormBase formBase) {
        Document document = formBase.getDocument();
        DocumentAuthorizer documentAuthorizer = getDocumentHelperService().getDocumentAuthorizer(document);
        Map<String, String> adHocActionRequestCodes = new HashMap<String, String>();

        if (documentAuthorizer.canSendAdHocRequests(document, KEWConstants.ACTION_REQUEST_FYI_REQ, GlobalVariables.getUserSession().getPerson())) {
            adHocActionRequestCodes.put(KEWConstants.ACTION_REQUEST_FYI_REQ, KEWConstants.ACTION_REQUEST_FYI_REQ_LABEL);
        }
        if (!document.getDocumentHeader().getWorkflowDocument().isFinal() && documentAuthorizer.canSendAdHocRequests(document, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, GlobalVariables.getUserSession().getPerson())) {
            adHocActionRequestCodes.put(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
        }
        if (!(document.getDocumentHeader().getWorkflowDocument().isApproved() || document.getDocumentHeader().getWorkflowDocument().isProcessed() || document.getDocumentHeader().getWorkflowDocument().isFinal()) && documentAuthorizer.canSendAdHocRequests(document, KEWConstants.ACTION_REQUEST_APPROVE_REQ, GlobalVariables.getUserSession().getPerson())) {
            adHocActionRequestCodes.put(KEWConstants.ACTION_REQUEST_APPROVE_REQ, KEWConstants.ACTION_REQUEST_APPROVE_REQ_LABEL);
        }

        formBase.setAdHocActionRequestCodes(adHocActionRequestCodes);

    }


    @SuppressWarnings("unchecked")
    protected Map convertSetToMap(Set s) {
        Map map = new HashMap();
        Iterator i = s.iterator();
        while (i.hasNext()) {
            Object key = i.next();
            map.put(key, KRADConstants.KUALI_DEFAULT_TRUE_VALUE);
        }
        return map;
    }

    /**
     * @return the dataDictionaryService
     */
    protected DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
        }
        return dataDictionaryService;
    }

    protected DocumentHelperService getDocumentHelperService() {
        if (documentHelperService == null) {
            documentHelperService = KRADServiceLocatorWeb.getDocumentHelperService();
        }
        return this.documentHelperService;
    }

    protected DocumentService getDocumentService() {
        if (documentService == null) {
            documentService = KRADServiceLocatorWeb.getDocumentService();
        }
        return this.documentService;
    }

    protected ConfigurationService getKualiConfigurationService() {
        if (kualiConfigurationService == null) {
            kualiConfigurationService = KRADServiceLocator.getKualiConfigurationService();
        }
        return this.kualiConfigurationService;
    }

    protected ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = CoreFrameworkServiceLocator.getParameterService();
        }
        return this.parameterService;
    }

    protected PessimisticLockService getPessimisticLockService() {
        if (pessimisticLockService == null) {
            pessimisticLockService = KRADServiceLocatorWeb.getPessimisticLockService();
        }
        return this.pessimisticLockService;
    }

    protected KualiRuleService getKualiRuleService() {
        if (kualiRuleService == null) {
            kualiRuleService = KRADServiceLocatorWeb.getKualiRuleService();
        }
        return this.kualiRuleService;
    }

    protected GroupService getGroupService() {
        if (groupService == null) {
            groupService = KimApiServiceLocator.getGroupService();
        }
        return this.groupService;
    }

    protected AttachmentService getAttachmentService() {
        if (attachmentService == null) {
            attachmentService = KRADServiceLocator.getAttachmentService();
        }
        return this.attachmentService;
    }

    protected NoteService getNoteService() {
        if (noteService == null) {
            noteService = KRADServiceLocator.getNoteService();
        }
        return this.noteService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = KRADServiceLocator.getBusinessObjectService();
        }
        return this.businessObjectService;
    }

    @Override
    protected BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
        if (businessObjectAuthorizationService == null) {
            businessObjectAuthorizationService = KNSServiceLocator.getBusinessObjectAuthorizationService();
        }
        return businessObjectAuthorizationService;
    }

    public BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        if (businessObjectMetaDataService == null) {
            businessObjectMetaDataService = KNSServiceLocator.getBusinessObjectMetaDataService();
        }
        return this.businessObjectMetaDataService;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            entityManagerFactory = KRADServiceLocator.getApplicationEntityManagerFactory();
        }
        return this.entityManagerFactory;
    }

    /**
     * @see KualiAction#hideAllTabs(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward hideAllTabs(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (form instanceof KualiDocumentFormBase) {
            WebUtils.reuseErrorMapFromPreviousRequest((KualiDocumentFormBase) form);
        }
        return super.hideAllTabs(mapping, form, request, response);
    }

    /**
     * @see KualiAction#showAllTabs(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward showAllTabs(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (form instanceof KualiDocumentFormBase) {
            WebUtils.reuseErrorMapFromPreviousRequest((KualiDocumentFormBase) form);
        }
        return super.showAllTabs(mapping, form, request, response);
    }

    /**
     * @see KualiAction#toggleTab(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward toggleTab(ActionMapping mapping, ActionForm form,
                                   HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (form instanceof KualiDocumentFormBase) {
            WebUtils.reuseErrorMapFromPreviousRequest((KualiDocumentFormBase) form);
        }
        return super.toggleTab(mapping, form, request, response);
    }

    @Override
    protected void doProcessingAfterPost(KualiForm form, HttpServletRequest request) {
        super.doProcessingAfterPost(form, request);
        if (form instanceof KualiDocumentFormBase) {
            Document document = ((KualiDocumentFormBase) form).getDocument();

            getBusinessObjectService().linkUserFields(document);
        }
    }
}

