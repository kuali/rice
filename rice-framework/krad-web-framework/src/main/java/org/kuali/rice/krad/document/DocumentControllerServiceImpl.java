/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.document;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.action.DocumentActionParameters;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.UserSessionUtils;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.bo.Attachment;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.exception.DocumentAuthorizationException;
import org.kuali.rice.krad.exception.UnknownDocumentIdException;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.rules.rule.event.AddNoteEvent;
import org.kuali.rice.krad.rules.rule.event.DocumentEvent;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.service.AttachmentService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.DocumentView;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.NoteType;
import org.kuali.rice.krad.web.form.DialogResponse;
import org.kuali.rice.krad.web.form.DocumentFormBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.CollectionControllerService;
import org.kuali.rice.krad.web.service.ModelAndViewService;
import org.kuali.rice.krad.web.service.NavigationControllerService;
import org.kuali.rice.krad.web.service.impl.ControllerServiceImpl;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Default implementation of the document controller service.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentControllerServiceImpl extends ControllerServiceImpl implements DocumentControllerService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            DocumentControllerServiceImpl.class);

    // COMMAND constants which cause docHandler to load an existing document instead of creating a new one
    protected static final String[] DOCUMENT_LOAD_COMMANDS =
            {KewApiConstants.ACTIONLIST_COMMAND, KewApiConstants.DOCSEARCH_COMMAND, KewApiConstants.SUPERUSER_COMMAND,
                    KewApiConstants.HELPDESK_ACTIONLIST_COMMAND};
    protected static final String SENSITIVE_DATA_DIALOG = "DialogGroup-SensitiveData";
    protected static final String EXPLANATION_DIALOG = "DisapproveExplanationDialog";

    private LegacyDataAdapter legacyDataAdapter;
    private DataDictionaryService dataDictionaryService;
    private DocumentService documentService;
    private DocumentDictionaryService documentDictionaryService;
    private AttachmentService attachmentService;
    private NoteService noteService;
    private ModelAndViewService modelAndViewService;
    private NavigationControllerService navigationControllerService;
    private ConfigurationService configurationService;
    private CollectionControllerService collectionControllerService;
    private ParameterService parameterService;

    /**
     * Determines whether a new document instance needs created or we need to load an existing document by
     * checking the {@link org.kuali.rice.krad.web.form.DocumentFormBase#getCommand()} value, then delegates to
     * a helper method to carry out the action.
     *
     * {@inheritDoc}
     */
    @Override
    public ModelAndView docHandler(DocumentFormBase form) throws WorkflowException {
        String command = form.getCommand();
        DocumentView view = (DocumentView) form.getView();

        if (ArrayUtils.contains(DOCUMENT_LOAD_COMMANDS, command) && (form.getDocId() != null)) {
            checkReturnLocationForDocSearch(command, form);

            loadDocument(form);

            if (KewApiConstants.SUPERUSER_COMMAND.equals(command)) {
                view.setSuperUserView(true);
            }
        } else if (KewApiConstants.INITIATE_COMMAND.equals(command)) {
            if (view != null) {
                form.setApplyDefaultValues(true);
            }

            createDocument(form);
        } else {
            LOG.error("docHandler called with invalid parameters");
            throw new IllegalArgumentException("docHandler called with invalid parameters");
        }

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * Determines if the DOCSEARCH_COMMAND is the present command value and updates the return location to the base
     * application url
     *
     * @param command the current command value
     * @param form the form with the updated return location
     */
    private void checkReturnLocationForDocSearch(String command, DocumentFormBase form) {
        if (KewApiConstants.DOCSEARCH_COMMAND.equals(command)) {
            form.setReturnLocation(ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.APPLICATION_URL_KEY));
        }
    }

    /**
     * Loads the document by its provided document header id on the given form.
     *
     * <p>This has been abstracted out so that it can be overridden in children if the need arises</p>
     *
     * @param form form instance that contains the doc id parameter and where
     * the retrieved document instance should be set
     */
    protected void loadDocument(DocumentFormBase form) throws WorkflowException {
        String docId = form.getDocId();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading document" + docId);
        }

        Document document = getDocumentService().getByDocumentHeaderId(docId);
        if (document == null) {
            throw new UnknownDocumentIdException(
                    "Document no longer exists.  It may have been cancelled before being saved.");
        }

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!getDocumentDictionaryService().getDocumentAuthorizer(document).canOpen(document,
                GlobalVariables.getUserSession().getPerson())) {
            throw buildAuthorizationException("open", document);
        }

        // re-retrieve the document using the current user's session - remove
        // the system user from the WorkflowDcument object
        if (workflowDocument != document.getDocumentHeader().getWorkflowDocument()) {
            LOG.warn("Workflow document changed via canOpen check");
            document.getDocumentHeader().setWorkflowDocument(workflowDocument);
        }

        form.setDocument(document);
        form.setDocTypeName(workflowDocument.getDocumentTypeName());

        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(), workflowDocument);
    }

    /**
     * Creates a new document of the type specified by the docTypeName property of the given form.
     *
     * <p>This has been abstracted out so that it can be overridden in children if the need arises</p>
     *
     * @param form form instance that contains the doc type parameter and where
     * the new document instance should be set
     */
    protected void createDocument(DocumentFormBase form) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating new document instance for doc type: " + form.getDocTypeName());
        }

        Document doc = getDocumentService().getNewDocument(form.getDocTypeName());

        form.setDocument(doc);
        form.setDocTypeName(doc.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView cancel(UifFormBase form) {
        performWorkflowAction((DocumentFormBase) form, UifConstants.WorkflowAction.CANCEL);

        return getNavigationControllerService().returnToPrevious(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView reload(DocumentFormBase form) throws WorkflowException {
        Document document = form.getDocument();

        // prepare the reload action by calling dochandler (set doc id and command)
        form.setDocId(document.getDocumentNumber());
        form.setCommand(DOCUMENT_LOAD_COMMANDS[1]);

        form.setEvaluateFlagsAndModes(true);
        form.setCanEditView(null);

        GlobalVariables.getMessageMap().putInfo(KRADConstants.GLOBAL_MESSAGES, RiceKeyConstants.MESSAGE_RELOADED);

        return docHandler(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView recall(DocumentFormBase form) {
        performWorkflowAction(form, UifConstants.WorkflowAction.RECALL);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView save(DocumentFormBase form) {
        return save(form, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView save(DocumentFormBase form, SaveDocumentEvent saveDocumentEvent) {
        Document document = form.getDocument();

        // get the explanation from the document and check it for sensitive data
        String explanation = document.getDocumentHeader().getExplanation();
        ModelAndView sensitiveDataDialogModelAndView = checkSensitiveDataAndWarningDialog(explanation, form);

        // if a sensitive data warning dialog is returned then display it
        if (sensitiveDataDialogModelAndView != null) {
            return sensitiveDataDialogModelAndView;
        }

        performWorkflowAction(form, UifConstants.WorkflowAction.SAVE, saveDocumentEvent);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView complete(DocumentFormBase form) {
        performWorkflowAction(form, UifConstants.WorkflowAction.COMPLETE);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView route(DocumentFormBase form) {
        performWorkflowAction(form, UifConstants.WorkflowAction.ROUTE);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView blanketApprove(DocumentFormBase form) {
        Document document = form.getDocument();
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        //disable blanket approve if adhoc route completion exists
        List<AdHocRouteRecipient> adHocRecipients = new ArrayList<AdHocRouteRecipient>();
        adHocRecipients.addAll(document.getAdHocRoutePersons());
        adHocRecipients.addAll(document.getAdHocRouteWorkgroups());

        //check for ad hoc completion request
        for (AdHocRouteRecipient adHocRouteRecipient : adHocRecipients) {
            String actionRequestedCode = adHocRouteRecipient.getActionRequested();

            //add error and send back
            if (KewApiConstants.ACTION_REQUEST_COMPLETE_REQ.equals(actionRequestedCode)) {
                GlobalVariables.getMessageMap().putError(KRADConstants.NEW_AD_HOC_ROUTE_WORKGROUP_PROPERTY_NAME,
                        RiceKeyConstants.ERROR_ADHOC_COMPLETE_BLANKET_APPROVE_NOT_ALLOWED);

                return getModelAndViewService().getModelAndView(form);
            }
        }

        performWorkflowAction(form, UifConstants.WorkflowAction.BLANKETAPPROVE);

        if (GlobalVariables.getMessageMap().hasErrors()) {
            return getModelAndViewService().getModelAndView(form);
        }

        return getNavigationControllerService().returnToPrevious(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView approve(DocumentFormBase form) {
        performWorkflowAction(form, UifConstants.WorkflowAction.APPROVE);

        return getNavigationControllerService().returnToPrevious(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView disapprove(DocumentFormBase form) {
        // get the explanation for disapproval from the disapprove dialog and check it for sensitive data
        String explanationData = generateDisapprovalNote(form);
        ModelAndView sensitiveDataDialogModelAndView = checkSensitiveDataAndWarningDialog(explanationData, form);

        // if a sensitive data warning dialog is returned then display it
        if (sensitiveDataDialogModelAndView != null) {
            return sensitiveDataDialogModelAndView;
        }

        performWorkflowAction(form, UifConstants.WorkflowAction.DISAPPROVE);

        return getNavigationControllerService().returnToPrevious(form);
    }

    /**
     * Convenience method for generating disapproval note with text from the explanation dialog.
     *
     * @param form document form instance containing the explanation dialog
     * @return
     */
    protected String generateDisapprovalNote(DocumentFormBase form) {
        String explanationData = form.getDialogExplanations().get(EXPLANATION_DIALOG);
        if(explanationData == null) {
            return "";
        }

        // build out full message to return
        String introNoteMessage = getConfigurationService().getPropertyValueAsString(
                RiceKeyConstants.MESSAGE_DISAPPROVAL_NOTE_TEXT_INTRO) + KRADConstants.BLANK_SPACE;

        return introNoteMessage + explanationData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView fyi(DocumentFormBase form) {
        performWorkflowAction(form, UifConstants.WorkflowAction.FYI);

        return getNavigationControllerService().returnToPrevious(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView acknowledge(DocumentFormBase form) {
        performWorkflowAction(form, UifConstants.WorkflowAction.ACKNOWLEDGE);

        return getNavigationControllerService().returnToPrevious(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView sendAdHocRequests(DocumentFormBase form) {
        performWorkflowAction(form, UifConstants.WorkflowAction.SENDADHOCREQUESTS);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView supervisorFunctions(DocumentFormBase form) {
        String workflowSuperUserUrl = getConfigurationService().getPropertyValueAsString(KRADConstants.WORKFLOW_URL_KEY)
                + "/"
                + KRADConstants.SUPERUSER_ACTION;

        Properties props = new Properties();
        props.setProperty(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.DISPLAY_SUPER_USER_DOCUMENT);
        props.setProperty(UifPropertyPaths.DOCUMENT_ID, form.getDocument().getDocumentNumber());

        return getModelAndViewService().performRedirect(form, workflowSuperUserUrl, props);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView close(DocumentFormBase form) {
        return getNavigationControllerService().returnToPrevious(form);
    }

    /**
     * Validates the note, saves attachment, adds the time stamp and author, and calls the
     * generic addLine method.
     *
     * {@inheritDoc}
     */
    @Override
    public ModelAndView insertNote(DocumentFormBase form) {
        Document document = form.getDocument();

        Note newNote = getAddLineNoteInstance(form);
        setNewNoteProperties(form, document, newNote);

        Attachment attachment = getNewNoteAttachment(form, document, newNote);

        // validate the note
        boolean rulesPassed = KRADServiceLocatorWeb.getKualiRuleService().applyRules(new AddNoteEvent(document,
                newNote));
        if (!rulesPassed) {
            return getModelAndViewService().getModelAndView(form);
        }

        // adding the attachment after refresh gets called, since the attachment record doesn't get persisted
        // until the note does (and therefore refresh doesn't have any attachment to autoload based on the id, nor
        // does it autopopulate the id since the note hasn't been persisted yet)
        if (attachment != null) {
            newNote.addAttachment(attachment);
        }

        // check for sensitive data within the note and display warning dialog if necessary
        ModelAndView sensitiveDataDialogModelAndView = checkSensitiveDataAndWarningDialog(newNote.getNoteText(), form);
        if (sensitiveDataDialogModelAndView != null) {
            return sensitiveDataDialogModelAndView;
        }

        saveNewNote(form, document, newNote);

        return getCollectionControllerService().addLine(form);
    }

    /**
     * Retrieves the note instance on the form that should be added to the document notes.
     *
     * @param form form instance containing the add note instance
     * @return
     */
    protected Note getAddLineNoteInstance(DocumentFormBase form) {
        String selectedCollectionId = form.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_ID);

        BindingInfo addLineBindingInfo = (BindingInfo) form.getViewPostMetadata().getComponentPostData(
                selectedCollectionId, UifConstants.PostMetadata.ADD_LINE_BINDING_INFO);

        String addLinePath = addLineBindingInfo.getBindingPath();
        Object addLine = ObjectPropertyUtils.getPropertyValue(form, addLinePath);

        return (Note) addLine;
    }

    /**
     * Defaults properties (posted timestamp, object id, author) on the note instance that will be added.
     *
     * @param form form instance containing the add note instance
     * @param document document instance the note will be added to
     * @param newNote note instance to set properties on
     */
    protected void setNewNoteProperties(DocumentFormBase form, Document document, Note newNote) {
        newNote.setNotePostedTimestampToCurrent();
        newNote.setRemoteObjectIdentifier(document.getNoteTarget().getObjectId());

        Person kualiUser = GlobalVariables.getUserSession().getPerson();
        if (kualiUser == null) {
            throw new IllegalStateException("Current UserSession has a null Person.");
        }

        newNote.setAuthorUniversalIdentifier(kualiUser.getPrincipalId());
    }

    /**
     * Builds an attachment for the file (if any) associated with the add note instance.
     *
     * @param form form instance containing the attachment file
     * @param document document instance the attachment should be associated with
     * @param newNote note instance the attachment should be associated with
     * @return Attachment instance for the note, or null if no attachment file was present
     */
    protected Attachment getNewNoteAttachment(DocumentFormBase form, Document document, Note newNote) {
        MultipartFile attachmentFile = form.getAttachmentFile();

        if ((attachmentFile == null) || StringUtils.isBlank(attachmentFile.getOriginalFilename())) {
            return null;
        }

        if (attachmentFile.getSize() == 0) {
            GlobalVariables.getMessageMap().putError(String.format("%s.%s",
                    KRADConstants.NEW_DOCUMENT_NOTE_PROPERTY_NAME, KRADConstants.NOTE_ATTACHMENT_FILE_PROPERTY_NAME),
                    RiceKeyConstants.ERROR_UPLOADFILE_EMPTY, attachmentFile.getOriginalFilename());

            return null;
        }

        String attachmentTypeCode = null;
        if (newNote.getAttachment() != null) {
            attachmentTypeCode = newNote.getAttachment().getAttachmentTypeCode();
        }

        DocumentAuthorizer documentAuthorizer = getDocumentDictionaryService().getDocumentAuthorizer(document);
        if (!documentAuthorizer.canAddNoteAttachment(document, attachmentTypeCode,
                GlobalVariables.getUserSession().getPerson())) {
            throw buildAuthorizationException("annotate", document);
        }

        Attachment attachment;
        try {
            attachment = getAttachmentService().createAttachment(document.getNoteTarget(),
                    attachmentFile.getOriginalFilename(), attachmentFile.getContentType(),
                    (int) attachmentFile.getSize(), attachmentFile.getInputStream(), attachmentTypeCode);
        } catch (IOException e) {
            throw new RiceRuntimeException("Unable to store attachment", e);
        }

        return attachment;
    }

    /**
     * Saves a new note instance to the data store if the document state allows it.
     *
     * @param form form instance containing the add note instance
     * @param document document instance the note is associated with
     * @param newNote note instance to save
     */
    protected void saveNewNote(DocumentFormBase form, Document document, Note newNote) {
        DocumentHeader documentHeader = document.getDocumentHeader();

        if (!documentHeader.getWorkflowDocument().isInitiated() && StringUtils.isNotEmpty(
                document.getNoteTarget().getObjectId()) && !(document instanceof MaintenanceDocument && NoteType
                .BUSINESS_OBJECT.getCode().equals(newNote.getNoteTypeCode()))) {

            getNoteService().save(newNote);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView deleteNote(DocumentFormBase form) {
        String selectedLineIndex = form.getActionParamaterValue(UifParameters.SELECTED_LINE_INDEX);

        Document document = form.getDocument();

        Note note = document.getNote(Integer.parseInt(selectedLineIndex));
        Attachment attachment = note.getAttachment();

        String attachmentTypeCode = null;
        if (attachment != null) {
            attachmentTypeCode = attachment.getAttachmentTypeCode();
        }

        // verify the user has permissions to delete the note
        Person user = GlobalVariables.getUserSession().getPerson();
        if (!getDocumentDictionaryService().getDocumentAuthorizer(document).canDeleteNoteAttachment(document,
                attachmentTypeCode, note.getAuthorUniversalIdentifier(), user)) {
            throw buildAuthorizationException("annotate", document);
        }

        if (attachment != null && attachment.isComplete()) {
            getAttachmentService().deleteAttachmentContents(attachment);
        }

        // if document is not saved there is no need to delete the note (it is not persisted)
        if (!document.getDocumentHeader().getWorkflowDocument().isInitiated()) {
            getNoteService().deleteNote(note);
        }

        return getCollectionControllerService().deleteLine(form);
    }

    /**
     * Retrieves a note attachment by either the line index of the note within the documents note collection, or
     * by the note identifier.
     *
     * {@inheritDoc}
     */
    @Override
    public ModelAndView downloadAttachment(DocumentFormBase form, HttpServletResponse response) {
        Attachment attachment = null;

        String selectedLineIndex = form.getActionParamaterValue(UifParameters.SELECTED_LINE_INDEX);
        if (StringUtils.isNotBlank(selectedLineIndex)) {
            Note note = form.getDocument().getNote(Integer.parseInt(selectedLineIndex));
            attachment = note.getAttachment();
        } else {
            Long noteIdentifier = Long.valueOf(form.getActionParamaterValue(KRADConstants.NOTE_IDENTIFIER));
            Note note = getNoteService().getNoteByNoteId(noteIdentifier);
            if ((note != null) && (note.getAttachment() != null)) {
                attachment = note.getAttachment();

                // make sure the reference back to note is set for the note service dependencies
                attachment.setNote(note);
            }
        }

        if (attachment == null) {
            throw new RuntimeException("Unable to find attachment for action parameters passed.");
        }

        try {
            KRADUtils.addAttachmentToResponse(response, getAttachmentService().retrieveAttachmentContents(attachment),
                    attachment.getAttachmentMimeTypeCode(), attachment.getAttachmentFileName(),
                    attachment.getAttachmentFileSize().longValue());
        } catch (IOException e) {
            throw new RuntimeException("Unable to download note attachment", e);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView cancelAttachment(DocumentFormBase form) {
        form.setAttachmentFile(null);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView superUserTakeActions(DocumentFormBase form) {
        Document document = form.getDocument();

        if (StringUtils.isBlank(document.getSuperUserAnnotation())) {
            GlobalVariables.getMessageMap().putErrorForSectionId(
                    "Uif-SuperUserAnnotation", RiceKeyConstants.ERROR_SUPER_USER_TAKE_ACTIONS_MISSING);
        }

        Set<String> selectedActionRequests = form.getSelectedCollectionLines().get(UifPropertyPaths.ACTION_REQUESTS);

        if (CollectionUtils.isEmpty(selectedActionRequests)) {
            GlobalVariables.getMessageMap().putErrorForSectionId(
                    "Uif-SuperUserActionRequests", RiceKeyConstants.ERROR_SUPER_USER_TAKE_ACTIONS_NONE_SELECTED);
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            return getModelAndViewService().getModelAndView(form);
        }

        List<ActionRequest> actionRequests = new ArrayList<ActionRequest>();
        for (String selectedActionRequest : selectedActionRequests) {
            ActionRequest actionRequest = ObjectPropertyUtils.getPropertyValue(document, selectedActionRequest);
            actionRequests.add(actionRequest);
        }

        for (ActionRequest actionRequest : actionRequests) {
            if (StringUtils.equals(actionRequest.getActionRequested().getCode(), ActionRequestType.COMPLETE.getCode()) ||
                    StringUtils.equals(actionRequest.getActionRequested().getCode(), ActionRequestType.APPROVE.getCode())) {
                document = getDocumentService().validateAndPersistDocument(document, new RouteDocumentEvent(document));
                form.setDocument(document);
            }

            performSuperUserWorkflowAction(form, UifConstants.SuperUserWorkflowAction.TAKEACTION, actionRequest);
        }

        document.setSuperUserAnnotation("");
        form.getSelectedCollectionLines().remove(UifPropertyPaths.ACTION_REQUESTS);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView superUserApprove(DocumentFormBase form) {
        Document document = form.getDocument();

        if (StringUtils.isBlank(document.getSuperUserAnnotation())) {
            GlobalVariables.getMessageMap().putErrorForSectionId(
                    "Uif-SuperUserAnnotation", RiceKeyConstants.ERROR_SUPER_USER_APPROVE_MISSING);
        }

        Set<String> selectedCollectionLines = form.getSelectedCollectionLines().get(UifPropertyPaths.ACTION_REQUESTS);

        if (!CollectionUtils.isEmpty(selectedCollectionLines)) {
            GlobalVariables.getMessageMap().putErrorForSectionId(
                    "Uif-SuperUserActionRequests", RiceKeyConstants.ERROR_SUPER_USER_APPROVE_ACTIONS_CHECKED);
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            return getModelAndViewService().getModelAndView(form);
        }

        performSuperUserWorkflowAction(form, UifConstants.SuperUserWorkflowAction.APPROVE);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView superUserDisapprove(DocumentFormBase form) {
        Document document = form.getDocument();

        if (StringUtils.isBlank(document.getSuperUserAnnotation())) {
            GlobalVariables.getMessageMap().putErrorForSectionId(
                    "Uif-SuperUserAnnotation", RiceKeyConstants.ERROR_SUPER_USER_DISAPPROVE_MISSING);
        }

        Set<String> selectedCollectionLines = form.getSelectedCollectionLines().get(UifPropertyPaths.ACTION_REQUESTS);

        if (!CollectionUtils.isEmpty(selectedCollectionLines)) {
            GlobalVariables.getMessageMap().putErrorForSectionId(
                    "Uif-SuperUserActionRequests", RiceKeyConstants.ERROR_SUPER_USER_DISAPPROVE_ACTIONS_CHECKED);
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            return getModelAndViewService().getModelAndView(form);
        }

        performSuperUserWorkflowAction(form, UifConstants.SuperUserWorkflowAction.DISAPPROVE);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performWorkflowAction(DocumentFormBase form, UifConstants.WorkflowAction action) {
        performWorkflowAction(form, action, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performWorkflowAction(DocumentFormBase form, UifConstants.WorkflowAction action,
            DocumentEvent documentEvent) {
        Document document = form.getDocument();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Performing workflow action " + action.name() + "for document: " + document.getDocumentNumber());
        }

        // evaluate flags on save only if we are saving for the first time (transitioning to saved status)
        if (!UifConstants.WorkflowAction.SAVE.equals(action) || document.getDocumentHeader().getWorkflowDocument()
                .isInitiated()) {
            form.setEvaluateFlagsAndModes(true);
            form.setCanEditView(null);
        }

        try {
            String successMessageKey = null;
            switch (action) {
                case SAVE:
                    if (documentEvent == null) {
                        document = getDocumentService().saveDocument(document);
                    } else {
                        document = getDocumentService().saveDocument(document, documentEvent);
                    }

                    successMessageKey = RiceKeyConstants.MESSAGE_SAVED;
                    break;
                case ROUTE:
                    document = getDocumentService().routeDocument(document, form.getAnnotation(),
                            combineAdHocRecipients(form));
                    successMessageKey = RiceKeyConstants.MESSAGE_ROUTE_SUCCESSFUL;
                    break;
                case BLANKETAPPROVE:
                    document = getDocumentService().blanketApproveDocument(document, form.getAnnotation(),
                            combineAdHocRecipients(form));
                    successMessageKey = RiceKeyConstants.MESSAGE_ROUTE_APPROVED;
                    break;
                case APPROVE:
                    document = getDocumentService().approveDocument(document, form.getAnnotation(),
                            combineAdHocRecipients(form));
                    successMessageKey = RiceKeyConstants.MESSAGE_ROUTE_APPROVED;
                    break;
                case DISAPPROVE:
                    String disapprovalNoteText = generateDisapprovalNote(form);
                    document = getDocumentService().disapproveDocument(document, disapprovalNoteText);
                    successMessageKey = RiceKeyConstants.MESSAGE_ROUTE_DISAPPROVED;
                    break;
                case FYI:
                    document = getDocumentService().clearDocumentFyi(document, combineAdHocRecipients(form));
                    successMessageKey = RiceKeyConstants.MESSAGE_ROUTE_FYIED;
                    break;
                case ACKNOWLEDGE:
                    document = getDocumentService().acknowledgeDocument(document, form.getAnnotation(),
                            combineAdHocRecipients(form));
                    successMessageKey = RiceKeyConstants.MESSAGE_ROUTE_ACKNOWLEDGED;
                    break;
                case CANCEL:
                    if (getDocumentService().documentExists(document.getDocumentNumber())) {
                        document = getDocumentService().cancelDocument(document, form.getAnnotation());
                        successMessageKey = RiceKeyConstants.MESSAGE_CANCELLED;
                    }
                    break;
                case COMPLETE:
                    if (getDocumentService().documentExists(document.getDocumentNumber())) {
                        document = getDocumentService().completeDocument(document, form.getAnnotation(),
                                combineAdHocRecipients(form));
                        successMessageKey = RiceKeyConstants.MESSAGE_ROUTE_SUCCESSFUL;
                    }
                    break;
                case SENDADHOCREQUESTS:
                    getDocumentService().sendAdHocRequests(document, form.getAnnotation(), combineAdHocRecipients(
                            form));
                    successMessageKey = RiceKeyConstants.MESSAGE_ROUTE_SUCCESSFUL;
                    break;
                case RECALL:
                    if (getDocumentService().documentExists(document.getDocumentNumber())) {
                        String recallExplanation = form.getDialogExplanations().get(
                                KRADConstants.QUESTION_ACTION_RECALL_REASON);
                        document = getDocumentService().recallDocument(document, recallExplanation, true);
                        successMessageKey = RiceKeyConstants.MESSAGE_ROUTE_RECALLED;
                    }
                    break;
            }

            // push potentially updated document back into the form
            form.setDocument(document);

            if (successMessageKey != null) {
                GlobalVariables.getMessageMap().putInfo(KRADConstants.GLOBAL_MESSAGES, successMessageKey);
            }
        } catch (ValidationException e) {
            // log the error and swallow exception so screen will draw with errors.
            // we don't want the exception to bubble up and the user to see an incident page, but instead just return to
            // the page and display the actual errors. This would need a fix to the API at some point.
            KRADUtils.logErrors();
            LOG.error("Validation Exception occured for document :" + document.getDocumentNumber(), e);

            // if no errors in map then throw runtime because something bad happened
            if (GlobalVariables.getMessageMap().hasNoErrors()) {
                throw new RiceRuntimeException("Validation Exception with no error message.", e);
            }
        } catch (Exception e) {
            throw new RiceRuntimeException(
                    "Exception trying to invoke action " + action.name() + " for document: " + document
                            .getDocumentNumber(), e);
        }

        form.setAnnotation("");
    }

    /**
     * Convenience method to combine the two lists of ad hoc recipients into one which should be done before
     * calling any of the document service methods that expect a list of ad hoc recipients.
     *
     * @param form document form instance containing the ad hod lists
     * @return List<AdHocRouteRecipient> combined ad hoc recipients
     */
    protected List<AdHocRouteRecipient> combineAdHocRecipients(DocumentFormBase form) {
        Document document = form.getDocument();

        List<AdHocRouteRecipient> adHocRecipients = new ArrayList<AdHocRouteRecipient>();
        adHocRecipients.addAll(document.getAdHocRoutePersons());
        adHocRecipients.addAll(document.getAdHocRouteWorkgroups());

        return adHocRecipients;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performSuperUserWorkflowAction(DocumentFormBase form, UifConstants.SuperUserWorkflowAction action) {
        performSuperUserWorkflowAction(form, action, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performSuperUserWorkflowAction(DocumentFormBase form, UifConstants.SuperUserWorkflowAction action,
            ActionRequest actionRequest) {
        Document document = form.getDocument();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Performing super user workflow action " + action.name() + "for document: " + document.getDocumentNumber());
        }

        try {
            String documentTypeId = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeId();
            String documentNumber = document.getDocumentNumber();
            String principalId = GlobalVariables.getUserSession().getPrincipalId();
            String superUserAnnotation = document.getSuperUserAnnotation();

            WorkflowDocumentActionsService documentActions = getWorkflowDocumentActionsService(documentTypeId);
            DocumentActionParameters parameters = DocumentActionParameters.create(documentNumber, principalId, superUserAnnotation);

            String successMessageKey = null;
            switch (action) {
                case TAKEACTION:
                    if (actionRequest != null) {
                        documentActions.superUserTakeRequestedAction(parameters, true, actionRequest.getId());

                        String actionRequestedCode = actionRequest.getActionRequested().getCode();
                        if (StringUtils.equals(actionRequestedCode, ActionRequestType.ACKNOWLEDGE.getCode())) {
                            successMessageKey = RiceKeyConstants.MESSAGE_SUPER_USER_ACTION_REQUEST_ACKNOWLEDGED;
                        } else if (StringUtils.equals(actionRequestedCode, ActionRequestType.FYI.getCode())) {
                            successMessageKey = RiceKeyConstants.MESSAGE_SUPER_USER_ACTION_REQUEST_FYIED;
                        } else if (StringUtils.equals(actionRequestedCode, ActionRequestType.COMPLETE.getCode())) {
                            successMessageKey = RiceKeyConstants.MESSAGE_SUPER_USER_ACTION_REQUEST_COMPLETED;
                        } else if (StringUtils.equals(actionRequestedCode, ActionRequestType.APPROVE.getCode())) {
                            successMessageKey = RiceKeyConstants.MESSAGE_SUPER_USER_ACTION_REQUEST_APPROVED;
                        } else {
                            successMessageKey = RiceKeyConstants.MESSAGE_SUPER_USER_ACTION_REQUEST_APPROVED;
                        }
                    }
                    break;
                case APPROVE:
                    documentActions.superUserBlanketApprove(parameters, true);

                    successMessageKey = RiceKeyConstants.MESSAGE_SUPER_USER_APPROVED;
                    break;
                case DISAPPROVE:
                    documentActions.superUserDisapprove(parameters, true);

                    successMessageKey = RiceKeyConstants.MESSAGE_SUPER_USER_DISAPPROVED;
                    break;
            }

            form.setEvaluateFlagsAndModes(true);
            form.setCanEditView(null);

            if (successMessageKey != null) {
                if (actionRequest != null) {
                    GlobalVariables.getMessageMap().putInfo(KRADConstants.GLOBAL_MESSAGES, successMessageKey,
                            document.getDocumentNumber(), actionRequest.getId());
                } else {
                    GlobalVariables.getMessageMap().putInfo(KRADConstants.GLOBAL_MESSAGES, successMessageKey,
                            document.getDocumentNumber());
                }
            }
        } catch (ValidationException e) {
            // log the error and swallow exception so screen will draw with errors.
            // we don't want the exception to bubble up and the user to see an incident page, but instead just return to
            // the page and display the actual errors. This would need a fix to the API at some point.
            KRADUtils.logErrors();
            LOG.error("Validation Exception occured for document :" + document.getDocumentNumber(), e);

            // if no errors in map then throw runtime because something bad happened
            if (GlobalVariables.getMessageMap().hasNoErrors()) {
                throw new RiceRuntimeException("Validation Exception with no error message.", e);
            }
        } catch (Exception e) {
            throw new RiceRuntimeException(
                    "Exception trying to invoke action " + action.name() + " for document: " + document
                            .getDocumentNumber(), e);
        }

        document.setSuperUserAnnotation("");
    }

    /**
     * Helper method to get the correct {@link WorkflowDocumentActionsService} from the {@code applicationId} of the
     * document type.
     *
     * @param documentTypeId the document type to get the application id from
     *
     * @return the correct {@link WorkflowDocumentActionsService} from the {@code applicationId} of the document type
     */
    protected WorkflowDocumentActionsService getWorkflowDocumentActionsService(String documentTypeId) {
        DocumentType documentType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeById(documentTypeId);
        String applicationId = documentType.getApplicationId();
        QName serviceName = new QName(KewApiConstants.Namespaces.KEW_NAMESPACE_2_0,
                KewApiConstants.ServiceNames.WORKFLOW_DOCUMENT_ACTIONS_SERVICE_SOAP);

        WorkflowDocumentActionsService service = (WorkflowDocumentActionsService) KsbApiServiceLocator.getServiceBus()
                .getService(serviceName, applicationId);

        if (service == null) {
            service = KewApiServiceLocator.getWorkflowDocumentActionsService();
        }

        return service;
    }

    /**
     * Helper method to check if sensitive data is present in a given string and dialog display.
     *
     * <p>If the string is sensitive we want to return a dialog box to make sure user wants to continue,
     * else we just return null</p>
     *
     * @param field the string to check for sensitive data
     * @param form the form to add the dialog to
     * @return the model and view for the dialog or null if there isn't one
     */
    protected ModelAndView checkSensitiveDataAndWarningDialog(String field, UifFormBase form) {
        boolean hasSensitiveData = KRADUtils.containsSensitiveDataPatternMatch(field);
        Boolean warnForSensitiveData = getParameterService().getParameterValueAsBoolean(KRADConstants.KNS_NAMESPACE,
                ParameterConstants.ALL_COMPONENT,
                KRADConstants.SystemGroupParameterNames.SENSITIVE_DATA_PATTERNS_WARNING_IND);

        // if there is sensitive data and the flag to warn for sensitive data is set,
        // then we want a dialog returned if there is not already one
        if (hasSensitiveData && warnForSensitiveData.booleanValue()) {
            DialogResponse sensitiveDataDialogResponse = form.getDialogResponse(SENSITIVE_DATA_DIALOG);

            if (sensitiveDataDialogResponse == null) {
                // no sensitive data dialog found, so create one on the form and return it
                return getModelAndViewService().showDialog(SENSITIVE_DATA_DIALOG, true, form);
            }
        }

        return null;
    }

    /**
     * Convenience method for building document authorization exceptions.
     *
     * @param action the action that was requested
     * @param document document instance the action was requested for
     */
    protected DocumentAuthorizationException buildAuthorizationException(String action, Document document) {
        return new DocumentAuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(),
                action, document.getDocumentNumber());
    }

    protected LegacyDataAdapter getLegacyDataAdapter() {
        if (this.legacyDataAdapter == null) {
            this.legacyDataAdapter = KRADServiceLocatorWeb.getLegacyDataAdapter();
        }
        return this.legacyDataAdapter;
    }

    public void setLegacyDataAdapter(LegacyDataAdapter legacyDataAdapter) {
        this.legacyDataAdapter = legacyDataAdapter;
    }

    protected DataDictionaryService getDataDictionaryService() {
        if (this.dataDictionaryService == null) {
            this.dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }
        return this.dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    protected DocumentService getDocumentService() {
        if (this.documentService == null) {
            this.documentService = KRADServiceLocatorWeb.getDocumentService();
        }
        return this.documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    protected DocumentDictionaryService getDocumentDictionaryService() {
        if (this.documentDictionaryService == null) {
            this.documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return this.documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }

    protected AttachmentService getAttachmentService() {
        if (attachmentService == null) {
            attachmentService = KRADServiceLocator.getAttachmentService();
        }
        return this.attachmentService;
    }

    public void setAttachmentService(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    protected NoteService getNoteService() {
        if (noteService == null) {
            noteService = KRADServiceLocator.getNoteService();
        }

        return this.noteService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    protected ModelAndViewService getModelAndViewService() {
        return modelAndViewService;
    }

    public void setModelAndViewService(ModelAndViewService modelAndViewService) {
        this.modelAndViewService = modelAndViewService;
    }

    protected NavigationControllerService getNavigationControllerService() {
        return navigationControllerService;
    }

    public void setNavigationControllerService(NavigationControllerService navigationControllerService) {
        this.navigationControllerService = navigationControllerService;
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    protected CollectionControllerService getCollectionControllerService() {
        return collectionControllerService;
    }

    public void setCollectionControllerService(CollectionControllerService collectionControllerService) {
        this.collectionControllerService = collectionControllerService;
    }

    protected ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
