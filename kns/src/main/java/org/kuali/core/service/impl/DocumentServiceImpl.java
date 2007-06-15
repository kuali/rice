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
package org.kuali.core.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.AdHocRouteRecipient;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.DocumentDao;
import org.kuali.core.dao.DocumentHeaderDao;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocumentBase;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.exceptions.DocumentAuthorizationException;
import org.kuali.core.exceptions.InactiveDocumentTypeAuthorizationException;
import org.kuali.core.exceptions.UnknownDocumentTypeException;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rule.event.BlanketApproveDocumentEvent;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.rule.event.RouteDocumentEvent;
import org.kuali.core.rule.event.SaveDocumentEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.DocumentAuthorizationService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.MaintenanceDocumentService;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.Timer;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;


/**
 * This class is the service implementation for the Document structure. It contains all of the document level type of processing and
 * calling back into documents for various centralization of functionality. This is the default, Kuali delivered implementation
 * which utilizes OneStart Workflow.
 */
@Transactional
public class DocumentServiceImpl implements DocumentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentServiceImpl.class);
    private DocumentHeaderDao documentHeaderDao;
    private DocumentTypeService documentTypeService;
    private DateTimeService dateTimeService;
    private KualiRuleService kualiRuleService;
    private DictionaryValidationService dictionaryValidationService;
    private MaintenanceDocumentService maintenanceDocumentService;
    private NoteService noteService;
    protected WorkflowDocumentService workflowDocumentService;
    protected BusinessObjectService businessObjectService;
    protected DocumentAuthorizationService documentAuthorizationService;
    protected DocumentDao documentDao;
    

    /**
     * @see org.kuali.core.service.DocumentService#saveDocument(org.kuali.core.document.Document, java.lang.String, java.util.List)
     */
    public Document saveDocument(Document document) throws WorkflowException, ValidationException {
        checkForNulls(document);
        if (!getDocumentActionFlags(document).getCanSave()) {
            throw buildAuthorizationException("save", document);
        }
        document.prepareForSave();
        validateAndPersistDocumentAndSaveAdHocRoutingRecipients(document);
        prepareWorkflowDocument(document);
        workflowDocumentService.save(document.getDocumentHeader().getWorkflowDocument(), null, null);
        GlobalVariables.getUserSession().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());

        return document;
    }

    /**
     * @see org.kuali.core.service.DocumentService#routeDocument(org.kuali.core.document.Document, java.lang.String, java.util.List)
     */
    public Document routeDocument(Document document, String annotation, List adHocRecipients) throws ValidationException, WorkflowException {
        checkForNulls(document);
        if (!getDocumentActionFlags(document).getCanRoute()) {
            throw buildAuthorizationException("route", document);
        }
        document.prepareForSave();
        validateAndPersistDocument(document, new RouteDocumentEvent(document));
        prepareWorkflowDocument(document);
        workflowDocumentService.route(document.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);
        GlobalVariables.getUserSession().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        businessObjectService.delete(document.getAdHocRoutePersons());
        businessObjectService.delete(document.getAdHocRouteWorkgroups());
        return document;
    }

    /**
     * @see org.kuali.core.service.DocumentService#approveDocument(org.kuali.core.document.Document, java.lang.String,
     *      java.util.List)
     */
    public Document approveDocument(Document document, String annotation, List adHocRecipients) throws ValidationException, WorkflowException {
        checkForNulls(document);
        if (!getDocumentActionFlags(document).getCanApprove()) {
            throw buildAuthorizationException("approve", document);
        }
        document.prepareForSave();
        validateAndPersistDocument(document, new ApproveDocumentEvent(document));
        prepareWorkflowDocument(document);
        workflowDocumentService.approve(document.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);
        GlobalVariables.getUserSession().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        return document;
    }


    /**
     * @see org.kuali.core.service.DocumentService#superUserApproveDocument(org.kuali.core.document.Document, java.lang.String)
     */
    public Document superUserApproveDocument(Document document, String annotation) throws WorkflowException {
        documentDao.save(document);
        prepareWorkflowDocument(document);
        workflowDocumentService.superUserApprove(document.getDocumentHeader().getWorkflowDocument(), annotation);
        GlobalVariables.getUserSession().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        return document;
    }

    /**
     * @see org.kuali.core.service.DocumentService#disapproveDocument(org.kuali.core.document.Document, java.lang.String)
     */
    public Document disapproveDocument(Document document, String annotation) throws Exception {
        checkForNulls(document);
        if (!getDocumentActionFlags(document).getCanDisapprove()) {
            throw buildAuthorizationException("disapprove", document);
        }

        Note note = createNoteFromDocument(document,annotation);
        addNoteToDocument(document, note);
        
        //SAVE THE NOTE
        //Note: This save logic is replicated here and in KualiDocumentAction, when to save (based on doc state) should be moved
        //      into a doc service method
        noteService.save(note);
        
        prepareWorkflowDocument(document);
        workflowDocumentService.disapprove(document.getDocumentHeader().getWorkflowDocument(), annotation);
        GlobalVariables.getUserSession().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        return document;
    }

    /**
     * @see org.kuali.core.service.DocumentService#cancelDocument(org.kuali.core.document.Document, java.lang.String)
     */
    public Document cancelDocument(Document document, String annotation) throws WorkflowException {
        checkForNulls(document);
        if (!getDocumentActionFlags(document).getCanCancel()) {
            throw buildAuthorizationException("cancel", document);
        }
        prepareWorkflowDocument(document);
        workflowDocumentService.cancel(document.getDocumentHeader().getWorkflowDocument(), annotation);
        GlobalVariables.getUserSession().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        businessObjectService.delete(document.getAdHocRoutePersons());
        businessObjectService.delete(document.getAdHocRouteWorkgroups());
        return document;
    }

    /**
     * @see org.kuali.core.service.DocumentService#acknowledgeDocument(org.kuali.core.document.Document, java.lang.String,
     *      java.util.List)
     */
    public Document acknowledgeDocument(Document document, String annotation, List adHocRecipients) throws WorkflowException {
        checkForNulls(document);
        if (!getDocumentActionFlags(document).getCanAcknowledge()) {
            throw buildAuthorizationException("acknowledge", document);
        }
        prepareWorkflowDocument(document);
        workflowDocumentService.acknowledge(document.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);
        GlobalVariables.getUserSession().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        return document;
    }

    /**
     * @see org.kuali.core.service.DocumentService#blanketApproveDocument(org.kuali.core.document.Document, java.lang.String,
     *      java.util.List)
     */
    public Document blanketApproveDocument(Document document, String annotation, List adHocRecipients) throws ValidationException, WorkflowException {
        checkForNulls(document);
        if (!getDocumentActionFlags(document).getCanBlanketApprove()) {
            throw buildAuthorizationException("blanket approve", document);
        }
        document.prepareForSave();
        validateAndPersistDocument(document, new BlanketApproveDocumentEvent(document));
        prepareWorkflowDocument(document);
        workflowDocumentService.blanketApprove(document.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);
        GlobalVariables.getUserSession().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        return document;
    }

    /**
     * @see org.kuali.core.service.DocumentService#clearDocumentFyi(org.kuali.core.document.Document, java.util.List)
     */
    public Document clearDocumentFyi(Document document, List adHocRecipients) throws WorkflowException {
        checkForNulls(document);
        if (!getDocumentActionFlags(document).getCanFYI()) {
            throw buildAuthorizationException("clear FYI", document);
        }
        // TODO delyea - added this here
        // populate document content so searchable attributes will be indexed properly
        document.populateDocumentForRouting();
        workflowDocumentService.clearFyi(document.getDocumentHeader().getWorkflowDocument(), adHocRecipients);
        GlobalVariables.getUserSession().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        return document;
    }

    protected void checkForNulls(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        else if (document.getDocumentNumber() == null) {
            throw new IllegalStateException("invalid (null) documentHeaderId");
        }
    }

    private DocumentActionFlags getDocumentActionFlags(Document document) {
        UniversalUser currentUser = GlobalVariables.getUserSession().getUniversalUser();

        return documentAuthorizationService.getDocumentAuthorizer(document).getDocumentActionFlags(document, currentUser);
    }

    private DocumentAuthorizationException buildAuthorizationException(String action, Document document) {
        UniversalUser currentUser = GlobalVariables.getUserSession().getUniversalUser();

        return new DocumentAuthorizationException(currentUser.getPersonUserIdentifier(), action, document.getDocumentNumber());
    }

    private void validateAndPersistDocumentAndSaveAdHocRoutingRecipients(Document document) throws WorkflowException {
        /*
         * Using this method to wrap validateAndPersistDocument to keep everything in one transaction. This avoids modifying the
         * signature on validateAndPersistDocument method
         */
        ArrayList<AdHocRouteRecipient> adHocRoutingRecipients = new ArrayList();
        adHocRoutingRecipients.addAll(document.getAdHocRoutePersons());
        adHocRoutingRecipients.addAll(document.getAdHocRouteWorkgroups());

        for (AdHocRouteRecipient recipient : adHocRoutingRecipients)
            recipient.setdocumentNumber(document.getDocumentNumber());
        HashMap criteria = new HashMap();
        criteria.put("documentNumber", document.getDocumentNumber());
        businessObjectService.deleteMatching(AdHocRouteRecipient.class, criteria);

        businessObjectService.save(adHocRoutingRecipients);
        validateAndPersistDocument(document, new SaveDocumentEvent(document));
    }

    /**
     * @see org.kuali.core.service.DocumentService#documentExists(java.lang.String)
     */
    public boolean documentExists(String documentHeaderId) {
        boolean exists = false;
        

        // validate parameters
        if (StringUtils.isBlank(documentHeaderId)) {
            throw new IllegalArgumentException("invalid (blank) documentHeaderId");
        }
        if (GlobalVariables.getUserSession() == null) {
            throw new IllegalStateException("GlobalVariables must be populated with a valid UserSession before a document can be fetched");
        }

        // look for workflowDocumentHeader, since that supposedly won't break the transaction
        if (!workflowDocumentService.workflowDocumentExists(documentHeaderId)) {
            exists = false;
        }
        else {
            // look for docHeaderId, since that fails without breaking the transaction
            return documentHeaderDao.getByDocumentHeaderId(documentHeaderId) != null;
        }

        return exists;
    }


    /**
     * Creates a new document by class.
     * 
     * @see org.kuali.core.service.DocumentService#getNewDocument(java.lang.Class)
     */
    public Document getNewDocument(Class documentClass) throws WorkflowException {
        if (documentClass == null) {
            throw new IllegalArgumentException("invalid (null) documentClass");
        }

        String documentTypeName = documentTypeService.getDocumentTypeNameByClass(documentClass);
        return getNewDocument(documentTypeName);
    }


    /**
     * Creates a new document by document type name.
     * 
     * @see org.kuali.core.service.DocumentService#getNewDocument(java.lang.String)
     */
    public Document getNewDocument(String documentTypeName) throws WorkflowException {

        // argument validation
        Timer t0 = new Timer("DocumentServiceImpl.getNewDocument");
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }
        if (GlobalVariables.getUserSession() == null) {
            throw new IllegalStateException("GlobalVariables must be populated with a valid UserSession before a new document can be created");
        }

        // get the class for this docTypeName
        Class documentClass = documentTypeService.getClassByName(documentTypeName);
        if (documentClass == null) {
            throw new UnknownDocumentTypeException("unknown document type '" + documentTypeName + "'");
        }

        // get the current user
        UniversalUser currentUser = GlobalVariables.getUserSession().getUniversalUser();

        // document must be maint doc or finanancial doc
        if (!documentTypeService.getDocumentTypeByName(documentTypeName).isFinDocumentTypeActiveIndicator()) {
            throw new InactiveDocumentTypeAuthorizationException("initiate", documentTypeName);
        }

        // get the authorization
        DocumentAuthorizer documentAuthorizer = documentAuthorizationService.getDocumentAuthorizer(documentTypeName);

        // make sure this person is authorized to initiate
        LOG.debug("calling canInitiate from getNewDocument()");
        documentAuthorizer.canInitiate(documentTypeName, currentUser);

        // initiate new workflow entry, get the workflow doc
        KualiWorkflowDocument workflowDocument = workflowDocumentService.createWorkflowDocument(documentTypeName, GlobalVariables.getUserSession().getUniversalUser());
        GlobalVariables.getUserSession().setWorkflowDocument(workflowDocument);

        // create a new document header object
        DocumentHeader documentHeader = new DocumentHeader();
        documentHeader.setWorkflowDocument(workflowDocument);
        documentHeader.setDocumentNumber(workflowDocument.getRouteHeaderId().toString());
        // status and notes are initialized correctly in the constructor

        // build Document of specified type
        Document document = null;
        try {
            // all maintenance documents have same class
            if (documentClass.equals(MaintenanceDocumentBase.class)) {
                document = new MaintenanceDocumentBase(documentTypeName);
            }
            else {
                // non-maintenance document
                document = (Document) documentClass.newInstance();
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }

        document.setDocumentHeader(documentHeader);
        document.setDocumentNumber(documentHeader.getDocumentNumber());

        t0.log();
        return document;
    }

    /**
     * This is temporary until workflow 2.0 and reads from a table to get documents whose status has changed to A (approved - no
     * outstanding approval actions requested)
     * 
     * @param documentHeaderId
     * @throws WorkflowException
     * @return Document
     */
    public Document getByDocumentHeaderId(String documentHeaderId) throws WorkflowException {
        if (documentHeaderId == null) {
            throw new IllegalArgumentException("invalid (null) documentHeaderId");
        }
        if (GlobalVariables.getUserSession() == null) {
            throw new IllegalStateException("GlobalVariables must be populated with a valid UserSession before a document can be fetched");
        }

        KualiWorkflowDocument workflowDocument = null;

            LOG.info("Retrieving doc id: " + documentHeaderId + " from workflow service.");
            workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(documentHeaderId), GlobalVariables.getUserSession().getUniversalUser());
            GlobalVariables.getUserSession().setWorkflowDocument(workflowDocument);

        Class documentClass = documentTypeService.getClassByName(workflowDocument.getDocumentType());

        // retrieve the Document
        Document document = documentDao.findByDocumentHeaderId(documentClass, documentHeaderId);
        return postProcessDocument(documentHeaderId, workflowDocument, document);
    }

    /**
     * @see org.kuali.core.service.DocumentService#findByDocumentHeaderStatusCode(java.lang.Class, java.lang.String)
     */
    public Collection findByDocumentHeaderStatusCode(Class clazz, String statusCode) throws WorkflowException {
        Collection foundDocuments = documentDao.findByDocumentHeaderStatusCode(clazz, statusCode);
        Collection returnDocuments = new ArrayList();
        for (Iterator iter = foundDocuments.iterator(); iter.hasNext();) {
            Document doc = (Document) iter.next();
            returnDocuments.add(getByDocumentHeaderId(doc.getDocumentNumber()));
        }
        return returnDocuments;
    }

    /**
     * Performs required post-processing for every document from the documentDao
     * 
     * @param documentHeaderId
     * @param workflowDocument
     * @param document
     */
    private Document postProcessDocument(String documentHeaderId, KualiWorkflowDocument workflowDocument, Document document) {
        if (document != null) {
            document.getDocumentHeader().setWorkflowDocument(workflowDocument);

            // set correctedByDocumentId manually, since OJB doesn't maintain that relationship
            DocumentHeader correctingDocumentHeader = documentHeaderDao.getCorrectingDocumentHeader(documentHeaderId);
            if (correctingDocumentHeader != null) {
                document.getDocumentHeader().setCorrectedByDocumentId(correctingDocumentHeader.getDocumentNumber());
            }

            // set the ad hoc route recipients too, since OJB doesn't maintain that relationship
            // TODO - see KULNRVSYS-1054

            document.processAfterRetrieve();
        }

        return document;
    }


    /**
     * The default implementation - this retrieves all documents by a list of documentHeader for a given class.
     * 
     * @see org.kuali.core.service.DocumentService#getDocumentsByListOfDocumentHeaderIds(java.lang.Class, java.util.List)
     */
    public List getDocumentsByListOfDocumentHeaderIds(Class clazz, List documentHeaderIds) throws WorkflowException {
        // validate user session
        if (GlobalVariables.getUserSession() == null) {
            throw new IllegalStateException("GlobalVariables must be populated with a valid UserSession before a document can be fetched");
        }

        // make sure that the supplied class is of the document type
        if (!Document.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("invalid (non-document) class of " + clazz.getName());
        }

        // validate documentHeaderIdList and contents
        if (documentHeaderIds == null) {
            throw new IllegalArgumentException("invalid (null) documentHeaderId list");
        }
        int index = 0;
        for (Iterator i = documentHeaderIds.iterator(); i.hasNext(); index++) {
            String documentHeaderId = (String) i.next();
            if (StringUtils.isBlank(documentHeaderId)) {
                throw new IllegalArgumentException("invalid (blank) documentHeaderId at list index " + index);
            }
        }

        // retrieve all documents that match the document header ids
        List rawDocuments = documentDao.findByDocumentHeaderIds(clazz, documentHeaderIds);

        // post-process them
        List documents = new ArrayList();
        for (Iterator i = rawDocuments.iterator(); i.hasNext();) {
            Document document = (Document) i.next();

            KualiWorkflowDocument workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(document.getDocumentNumber()), GlobalVariables.getUserSession().getUniversalUser());

            document = postProcessDocument(document.getDocumentNumber(), workflowDocument, document);
            documents.add(document);
        }

        return documents;
    }

    /**
     * @see org.kuali.core.service.DocumentService#getFinalDocumentHeadersByDate(Date documentFinalDate)
     */
    public Collection getFinalDocumentHeadersByDate(Date documentFinalDate) throws WorkflowException {
        Collection finalDocumentHeaders = documentHeaderDao.getByDocumentFinalDate(new java.sql.Date(documentFinalDate.getTime()));
        Iterator finalDocumentHeaderItr = finalDocumentHeaders.iterator();
        while (finalDocumentHeaderItr.hasNext()) {
            DocumentHeader finalDocumentHeader = (DocumentHeader) finalDocumentHeaderItr.next();
            finalDocumentHeader.setWorkflowDocument(workflowDocumentService.createWorkflowDocument(Long.valueOf(finalDocumentHeader.getDocumentNumber()), GlobalVariables.getUserSession().getUniversalUser()));
        }
        return finalDocumentHeaders;
    }


    /* Helper Methods */

    /**
     * Validates and persists a document.
     * 
     * @see org.kuali.core.service.DocumentService#validateAndPersistDocument(org.kuali.core.document.Document, java.lang.String)
     */
    public void validateAndPersistDocument(Document document, KualiDocumentEvent event) throws WorkflowException, ValidationException {
        if (document == null) {
            LOG.error("document passed to validateAndPersist was null");
            throw new IllegalArgumentException("invalid (null) document");
        }
        LOG.info("validating and preparing to persist document " + document.getDocumentNumber());
        
        document.validateBusinessRules(event);
        document.prepareForSave(event);

        // save the document
        try {
            LOG.info("storing document " + document.getDocumentNumber());
            documentDao.save(document);
        }
        catch (OptimisticLockingFailureException e) {
            LOG.error("exception encountered on store of document " + e.getMessage());
            throw e;
        }
        
        document.postProcessSave(event);


    }

    
    /**
     * Sets the title and app document id in the flex document
     * 
     * @param document
     * @throws WorkflowException
     */
    public void prepareWorkflowDocument(Document document) throws WorkflowException {
        // TODO delyea - added this here
        // populate document content so searchable attributes will be indexed properly
        document.populateDocumentForRouting();
        
        // make sure we push the document title into the FlexDoc
        populateDocumentTitle(document);

        // make sure we push the application document id into the FlexDoc
        populateApplicationDocumentId(document);
    }

    /**
     * This method will grab the generated document title from the document and add it to the FlexDoc so that it gets pushed into
     * workflow when routed.
     * 
     * @param document
     * @throws WorkflowException
     */
    private void populateDocumentTitle(Document document) throws WorkflowException {
        String documentTitle = document.getDocumentTitle();
        if (StringUtils.isNotBlank(documentTitle)) {
            document.getDocumentHeader().getWorkflowDocument().setTitle(documentTitle);
        }
    }

    /**
     * This method will grab the organization document number from the document and add it to the FlexDoc so that it gets pushed
     * into workflow when routed.
     * 
     * @param document
     */
    private void populateApplicationDocumentId(Document document) {
        String organizationDocumentNumber = document.getDocumentHeader().getOrganizationDocumentNumber();
        if (StringUtils.isNotBlank(organizationDocumentNumber)) {
            document.getDocumentHeader().getWorkflowDocument().setAppDocId(organizationDocumentNumber);
        }
    }

    /**
     * This is to allow for updates of document statuses and other related requirements for updates outside of the initial save and
     * route
     */
    public void updateDocument(Document document) {
        checkForNulls(document);
        documentDao.save(document);
    }
    
    /**
     * 
     * @see org.kuali.core.service.DocumentService#createNoteFromDocument(org.kuali.core.document.Document, java.lang.String)
     */
    public Note createNoteFromDocument(Document document, String text) throws Exception {
        Note note = new Note();
        
        note.setNotePostedTimestamp(dateTimeService.getCurrentTimestamp());
        note.setVersionNumber(new Long(1));
        note.setNoteText(text);
        if(document.isBoNotesSupport()) {
            note.setNoteTypeCode(Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
        } else {
            note.setNoteTypeCode(Constants.NoteTypeEnum.DOCUMENT_HEADER_NOTE_TYPE.getCode());
        }
        
        PersistableBusinessObject bo = null;
        String propertyName = noteService.extractNoteProperty(note);
        bo = (PersistableBusinessObject)ObjectUtils.getPropertyValue(document, propertyName);
        return (bo==null)?null:noteService.createNote(note,bo);    
    }
    
    
    /**
     * @see org.kuali.core.service.DocumentService#addNoteToDocument(org.kuali.core.document.Document, org.kuali.core.bo.Note)
     */
    public boolean addNoteToDocument(Document document, Note note) {
        PersistableBusinessObject parent = getNoteParent(document,note);
        return parent.addNote(note);
    }
    
    public PersistableBusinessObject getNoteParent(Document document, Note newNote) {
        //get the property name to set (this assumes this is a document type note)
        String propertyName = noteService.extractNoteProperty(newNote);
        //get BO to set
        PersistableBusinessObject noteParent = (PersistableBusinessObject)ObjectUtils.getPropertyValue(document, propertyName);
        return noteParent;
    }

    /**
     * @param documentTypeName
     * @return DocumentAuthorizer instance for the given documentType name
     */
    private DocumentAuthorizer getDocumentAuthorizer(String documentTypeName) {
        return documentAuthorizationService.getDocumentAuthorizer(documentTypeName);
    }


    /**
     * spring injected document type service
     * 
     * @param documentTypeService
     */
    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    /**
     * spring injected date time service
     * 
     * @param dateTimeService
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * @param kualiRuleService The kualiRuleService to set.
     */
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    /**
     * @param dictionaryValidationService The dictionaryValidationService to set.
     */
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

    /**
     * dao injected by spring
     * 
     * @param documentHeaderDao
     */
    public void setDocumentHeaderDao(DocumentHeaderDao documentHeaderDao) {
        this.documentHeaderDao = documentHeaderDao;
    }

    /**
     * Sets the maintenanceDocumentService attribute value.
     * 
     * @param maintenanceDocumentService The maintenanceDocumentService to set.
     */
    public final void setMaintenanceDocumentService(MaintenanceDocumentService maintenanceDocumentService) {
        this.maintenanceDocumentService = maintenanceDocumentService;
    }
    
    /**
     * Sets the noteService attribute value.
     * @param noteService The noteService to set.
     */
    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    /**
     * Sets the workflowDocumentService attribute value.
     *
     * @param workflowDocumentService The workflowDocumentService to set.
     */
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    /**
     * Sets the documentAuthorizationService attribute value.
     *
     * @param documentAuthorizationService The documentAuthorizationService to set.
     */
    public void setDocumentAuthorizationService(DocumentAuthorizationService documentAuthorizationService) {
        this.documentAuthorizationService = documentAuthorizationService;
    }

    /**
     * Sets the documentDao attribute value.
     *
     * @param documentDao The documentDao to set.
     */
    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }


}