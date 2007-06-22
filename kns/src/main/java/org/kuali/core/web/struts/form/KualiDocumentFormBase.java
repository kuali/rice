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
package org.kuali.core.web.struts.form;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteRecipient;
import org.kuali.core.bo.AdHocRouteWorkgroup;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.exceptions.DocumentAuthorizationException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.format.NoOpStringFormatter;
import org.kuali.core.web.format.TimestampFullPrecisionFormatter;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.util.AutoPopulatingList;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * TODO we should not be referencing eden constants from this class and wedding ourselves to that workflow application This class is
 * the base action form for all documents.
 */
public abstract class KualiDocumentFormBase extends KualiForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiDocumentFormBase.class);

    private Document document;
    private static ResourceBundle docInfo = ResourceBundle.getBundle("org.kuali.core.web.struts.form.documents");
    private String annotation = "";
    private String command;

    private String docId;
    private String docTypeName;

    private List<String> additionalScriptFiles;

    private AdHocRouteRecipient newAdHocRoutePerson;
    private AdHocRouteRecipient newAdHocRouteWorkgroup;

    private Note newNote;
    
    //TODO: is this still needed? I think it's obsolete now
    private List boNotes;
    
    protected FormFile attachmentFile;

    protected Map editingMode;
    protected boolean suppressAllButtons;
    protected DocumentActionFlags documentActionFlags;

    private boolean returnToActionList;

    /** Special list class which doesn't blow up when setting an index which doesn't exist. */
    private static class AutoExpandingList extends AutoPopulatingList implements Serializable {
        AutoExpandingList( Class listClass ) {
            super( listClass );
        }
        
        public Object set( int index, Object value ) {
            get( index );
            return super.set( index, value );
        }
    }
    
    /**
     * no args constructor that just initializes things for us
     */
    public KualiDocumentFormBase() {
        super();
        newNote = new Note();
        this.editingMode = new HashMap();
        this.additionalScriptFiles = new AutoExpandingList( String.class );

        // set the initial record for persons up
        newAdHocRoutePerson = new AdHocRoutePerson();

        // set the initial record for workgroups up
        newAdHocRouteWorkgroup = new AdHocRouteWorkgroup();

        // to make sure it posts back the correct time
        setFormatterType("document.documentHeader.note.finDocNotePostedDttmStamp", TimestampFullPrecisionFormatter.class);
        setFormatterType("document.documentHeader.note.attachment.finDocNotePostedDttmStamp", TimestampFullPrecisionFormatter.class);
        //TODO: Chris - Notes: remove the above and change the below from boNotes when notes are finished
        //overriding note formatter to make sure they post back the full timestamp
        setFormatterType("document.documentHeader.boNote.notePostedTimestamp",TimestampFullPrecisionFormatter.class);
        setFormatterType("document.documentBusinessObject.boNote.notePostedTimestamp",TimestampFullPrecisionFormatter.class);

        setFormatterType("editingMode", NoOpStringFormatter.class);
        setFormatterType("editableAccounts", NoOpStringFormatter.class);

        // create a blank DocumentActionFlags instance, since form-recreation needs it
        setDocumentActionFlags(new DocumentActionFlags());
        suppressAllButtons = false;
    }

    /**
     * Setup workflow doc in the document.
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        if (hasDocumentId()) {
            // populate workflowDocument in documentHeader, if needed
            try {
                KualiWorkflowDocument workflowDocument = null;
                if (GlobalVariables.getUserSession().getWorkflowDocument(getDocument().getDocumentNumber()) != null) {
                    workflowDocument = GlobalVariables.getUserSession().getWorkflowDocument(getDocument().getDocumentNumber());
                }
                else {
                    // gets the workflow document from doc service, doc service will also set the workflow document in the user's session
                    Document retrievedDocument = KNSServiceLocator.getDocumentService().getByDocumentHeaderId(getDocument().getDocumentNumber());
                    if (retrievedDocument == null) {
                        throw new WorkflowException("Unable to get retrieve document # " + getDocument().getDocumentNumber() + " from document service getByDocumentHeaderId");
                }
                    workflowDocument = retrievedDocument.getDocumentHeader().getWorkflowDocument();
            }

                getDocument().getDocumentHeader().setWorkflowDocument(workflowDocument);
            }
            catch (WorkflowException e) {
                LOG.warn("Error while instantiating workflowDoc", e);
                throw new RuntimeException("error populating documentHeader.workflowDocument", e);
            }
        }
    }


    /**
     * Updates authorization-related form fields based on the current form contents
     */
    public void populateAuthorizationFields(DocumentAuthorizer documentAuthorizer) {
        if (isFormDocumentInitialized()) {
            useDocumentAuthorizer(documentAuthorizer);

            // graceless hack which takes advantage of the fact that here and only here will we have guaranteed access to the
            // correct DocumentAuthorizer
            if (getEditingMode().containsKey(AuthorizationConstants.EditMode.UNVIEWABLE)) {
                throw new DocumentAuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonName(), "view", document.getDocumentHeader().getDocumentNumber());
            }
        }
    }


    /**
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        // check that annotation does not exceed 2000 characters
        setAnnotation(StringUtils.stripToNull(getAnnotation()));
        int diff = StringUtils.defaultString(getAnnotation()).length() - Constants.DOCUMENT_ANNOTATION_MAX_LENGTH;
        if (diff > 0) {
            GlobalVariables.getErrorMap().putError("annotation", KeyConstants.ERROR_DOCUMENT_ANNOTATION_MAX_LENGTH_EXCEEDED, new String[] { Integer.toString(Constants.DOCUMENT_ANNOTATION_MAX_LENGTH), Integer.toString(diff) });
        }
        return super.validate(mapping, request);
    }

    /**
     * Refactored out actually calling the documentAuthorizer methods, since TransactionalDocuments call a differently-parameterized
     * version of getEditMode
     *
     * @param documentAuthorizer
     */
    protected void useDocumentAuthorizer(DocumentAuthorizer documentAuthorizer) {
        UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();

        setEditingMode(documentAuthorizer.getEditMode(document, kualiUser));
        setDocumentActionFlags(documentAuthorizer.getDocumentActionFlags(document, kualiUser));
    }


    /**
     * @return true if this document was properly initialized with a DocumentHeader and related KualiWorkflowDocument
     */
    final protected boolean isFormDocumentInitialized() {
        boolean initialized = false;

        if (document != null) {
            if (document.getDocumentHeader() != null) {
                initialized = document.getDocumentHeader().hasWorkflowDocument();
            }
        }

        return initialized;
    }


    /**
     * @return Map of editingModes for this document, as set during the most recent call to
     *         populate(javax.servlet.http.HttpServletRequest)
     */
    public Map getEditingMode() {
        return editingMode;
    }

    /**
     * Set editingMode for this document - unfortunately necessary, since validation failures bypass the normal
     * populateAuthorizationFields call. (Unfortunate because it makes the UI just a bit easier to hack, until we have the back-end
     * checks of editingMode et al in place.)
     */
    public void setEditingMode(Map editingMode) {
        this.editingMode = editingMode;
    }


    /**
     * @return DocumentActionFlags instance indicating what actions the current user can take on this document
     */
    public DocumentActionFlags getDocumentActionFlags() {
        return documentActionFlags;
    }

    /**
     * Protected, since no external code should need to set it.
     */
    protected void setDocumentActionFlags(DocumentActionFlags documentActionFlags) {
        this.documentActionFlags = documentActionFlags;
    }


    /**
     * @return a map of the possible action request codes that takes into account the users context on the document
     */
    public Map getAdHocActionRequestCodes() {
        Map adHocActionRequestCodes = new HashMap();
        if (getWorkflowDocument() != null) {
            if (getWorkflowDocument().isFYIRequested()) {
                adHocActionRequestCodes.put(EdenConstants.ACTION_REQUEST_FYI_REQ, EdenConstants.ACTION_REQUEST_FYI_REQ_LABEL);
            }
            else if (getWorkflowDocument().isAcknowledgeRequested()) {
                adHocActionRequestCodes.put(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
                adHocActionRequestCodes.put(EdenConstants.ACTION_REQUEST_FYI_REQ, EdenConstants.ACTION_REQUEST_FYI_REQ_LABEL);
            }
            else if (getWorkflowDocument().isApprovalRequested() || getWorkflowDocument().isCompletionRequested() || getWorkflowDocument().stateIsInitiated() || getWorkflowDocument().stateIsSaved()) {
                adHocActionRequestCodes.put(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
                adHocActionRequestCodes.put(EdenConstants.ACTION_REQUEST_FYI_REQ, EdenConstants.ACTION_REQUEST_FYI_REQ_LABEL);
                adHocActionRequestCodes.put(EdenConstants.ACTION_REQUEST_APPROVE_REQ, EdenConstants.ACTION_REQUEST_APPROVE_REQ_LABEL);
            }
        }
        return adHocActionRequestCodes;
    }


    /**
     * @return the list of ad hoc routing persons
     */
    public List getAdHocRoutePersons() {
        return document.getAdHocRoutePersons();
    }


    /**
     * @return attachmentFile
     */
    public FormFile getAttachmentFile() {
        return attachmentFile;
    }

    /**
     * @param attachmentFile The attachmentFile to set.
     */
    public void setAttachmentFile(FormFile attachmentFile) {
        this.attachmentFile = attachmentFile;
    }


    /**
     * set the ad hoc routing persons list
     *
     * @param adHocRouteRecipients
     */
    public void setAdHocRoutePersons(List adHocRouteRecipients) {
        document.setAdHocRoutePersons(adHocRouteRecipients);
    }

    /**
     * get the ad hoc routing workgroup requests
     *
     * @return
     */
    public List getAdHocRouteWorkgroups() {
        return document.getAdHocRouteWorkgroups();
    }

    /**
     * set the ad hoc routing workgroup requests
     *
     * @param adHocRouteWorkgroups
     */
    public void setAdHocRouteWorkgroups(List adHocRouteWorkgroups) {
        document.setAdHocRouteWorkgroups(adHocRouteWorkgroups);
    }

    /**
     * Special getter based on index to work with multi rows for ad hoc routing to persons struts page
     *
     * @param index
     * @return
     */
    public AdHocRoutePerson getAdHocRoutePerson(int index) {
        while (getAdHocRoutePersons().size() <= index) {
            getAdHocRoutePersons().add(new AdHocRoutePerson());
        }
        return (AdHocRoutePerson) getAdHocRoutePersons().get(index);
    }

    /**
     * Special getter based on index to work with multi rows for ad hoc routing to workgroups struts page
     *
     * @param index
     * @return
     */
    public AdHocRouteWorkgroup getAdHocRouteWorkgroup(int index) {
        while (getAdHocRouteWorkgroups().size() <= index) {
            getAdHocRouteWorkgroups().add(new AdHocRouteWorkgroup());
        }
        return (AdHocRouteWorkgroup) getAdHocRouteWorkgroups().get(index);
    }

    /**
     * @return the new ad hoc route person object
     */
    public AdHocRouteRecipient getNewAdHocRoutePerson() {
        return newAdHocRoutePerson;
    }

    /**
     * set the new ad hoc route person object
     *
     * @param newAdHocRoutePerson
     */
    public void setNewAdHocRoutePerson(AdHocRoutePerson newAdHocRoutePerson) {
        this.newAdHocRoutePerson = newAdHocRoutePerson;
    }

    /**
     * @return the new ad hoc route workgroup object
     */
    public AdHocRouteRecipient getNewAdHocRouteWorkgroup() {
        return newAdHocRouteWorkgroup;
    }

    /**
     * set the new ad hoc route workgroup object
     *
     * @param newAdHocRouteWorkgroup
     */
    public void setNewAdHocRouteWorkgroup(AdHocRouteWorkgroup newAdHocRouteWorkgroup) {
        this.newAdHocRouteWorkgroup = newAdHocRouteWorkgroup;
    }

    /**
     * @return Returns the Document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @param document
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * @return FlexDoc for this form's document
     */
    public KualiWorkflowDocument getWorkflowDocument() {
        return getDocument().getDocumentHeader().getWorkflowDocument();
    }

    /**
     * TODO rk implemented to account for caps coming from kuali user service from workflow
     */
    public boolean isUserDocumentInitiator() {
        if (getWorkflowDocument() != null) {
            return getWorkflowDocument().getInitiatorNetworkId().equalsIgnoreCase(GlobalVariables.getUserSession().getNetworkId());
        }
        return false;
    }

    /**
     * @return true if the workflowDocument associated with this form is currently enroute
     */
    public boolean isDocumentEnRoute() {
        return getWorkflowDocument().stateIsEnroute();
    }

    /**
     * @param annotation The annotation to set.
     */
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    /**
     * @return Returns the annotation.
     */
    public String getAnnotation() {
        return annotation;
    }

    /**
     * @return returns the command that was passed from workflow
     */
    public String getCommand() {
        return command;
    }

    /**
     * setter for the command that was passed from workflow on the url
     *
     * @param command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * @return returns the docId that was passed from workflow on the url
     */
    public String getDocId() {
        return docId;
    }

    /**
     * setter for the docId that was passed from workflow on the url
     *
     * @param docId
     */
    public void setDocId(String docId) {
        this.docId = docId;
    }

    /**
     * getter for the docTypeName that was passed from workflow on the url
     *
     * @return
     */
    public String getDocTypeName() {
        return docTypeName;
    }

    /**
     * setter for the docTypeName that was passed from workflow on the url
     *
     * @param docTypeName
     */
    public void setDocTypeName(String docTypeName) {
        this.docTypeName = docTypeName;
    }

    /**
     * getter for convenience that will return the initiators network id
     *
     * @return
     */
    public String getInitiatorNetworkId() {
        return this.getWorkflowDocument().getInitiatorNetworkId();
    }

    /**
     * Gets the suppressAllButtons attribute.
     *
     * @return Returns the suppressAllButtons.
     */
    public final boolean isSuppressAllButtons() {
        return suppressAllButtons;
    }

    /**
     * Sets the suppressAllButtons attribute value.
     *
     * @param suppressAllButtons The suppressAllButtons to set.
     */
    public final void setSuppressAllButtons(boolean suppressAllButtons) {
        this.suppressAllButtons = suppressAllButtons;
    }

    /**
     * @return true if this form's getDocument() method returns a Document, and if that Document's getDocumentHeaderId method
     *         returns a non-null
     */
    public boolean hasDocumentId() {
        boolean hasDocId = false;

        Document d = getDocument();
        if (d != null) {
            String docHeaderId = d.getDocumentNumber();

            hasDocId = StringUtils.isNotBlank(docHeaderId);
        }

        return hasDocId;
    }

    /**
     * Sets flag indicating whether upon completion of approve, blanketApprove, cancel, or disapprove, the user should be returned
     * to the actionList instead of to the portal
     *
     * @param returnToActionList
     */
    public void setReturnToActionList(boolean returnToActionList) {
        this.returnToActionList = returnToActionList;
    }

    public boolean isReturnToActionList() {
        return returnToActionList;
    }

    public List<String> getAdditionalScriptFiles() {
        return additionalScriptFiles;
    }

    public void setAdditionalScriptFiles(List<String> additionalScriptFiles) {
        this.additionalScriptFiles = additionalScriptFiles;
    }

    public void setAdditionalScriptFile( int index, String scriptFile ) {
        additionalScriptFiles.set( index, scriptFile );
	}

    public String getAdditionalScriptFile( int index ) {
        return additionalScriptFiles.get( index );
    }

    public Note getNewNote() {
        return newNote;
    }

    public void setNewNote(Note newNote) {
        this.newNote = newNote;
    }

    /**
     * Gets the boNotes attribute. 
     * @return Returns the boNotes.
     */
    public List getBoNotes() {
        return boNotes;
    }

    /**
     * Sets the boNotes attribute value.
     * @param boNotes The boNotes to set.
     */
    public void setBoNotes(List boNotes) {
        this.boNotes = boNotes;
    }
}