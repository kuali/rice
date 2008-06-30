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
package org.kuali.core.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.datadictionary.WorkflowProperties;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.document.authorization.PessimisticLock;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.DocumentSerializerService;
import org.kuali.core.util.ErrorMessage;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypeUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.util.documentserializer.AlwaysTruePropertySerializibilityEvaluator;
import org.kuali.core.util.documentserializer.BusinessObjectPropertySerializibilityEvaluator;
import org.kuali.core.util.documentserializer.PropertySerializabilityEvaluator;
import org.kuali.core.workflow.DocumentInitiator;
import org.kuali.core.workflow.KualiDocumentXmlMaterializer;
import org.kuali.core.workflow.KualiTransactionalDocumentInformation;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * @see Document
 */
@MappedSuperclass
public abstract class DocumentBase extends PersistableBusinessObjectBase implements Document {
    private static final Logger LOG = Logger.getLogger(DocumentBase.class);
    
    @Id
    @Column(name="FDOC_NBR")
    protected String documentNumber;
    @OneToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST})
	@JoinColumn(name="FDOC_NBR", insertable=false, updatable=false)
    protected DocumentHeader documentHeader;
    @Transient
    protected transient PersistableBusinessObject documentBusinessObject; //here for reflection
    // TODO Nate and David get together and fix this with a ManyToOne reference on the other side of this OneToMany
    @Transient
    private List<PessimisticLock> pessimisticLocks;

    @Transient
    private List adHocRoutePersons;
    @Transient
    private List adHocRouteWorkgroups;

    /**
     * Constructs a DocumentBase.java.
     */
    public DocumentBase() {
        try {
            // create a new document header object
            Class documentHeaderClass = KNSServiceLocator.getDocumentHeaderService().getDocumentHeaderBaseClass();
            setDocumentHeader((DocumentHeader) documentHeaderClass.newInstance());
            pessimisticLocks = new ArrayList<PessimisticLock>();
            adHocRoutePersons = new ArrayList();
            adHocRouteWorkgroups = new ArrayList();
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Error instantiating DocumentHeader", e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Error instantiating DocumentHeader", e);
        }
    }

    /**
     * @see org.kuali.core.document.Document#getAllowsCopy()
     */
    public boolean getAllowsCopy() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * This is the default document title implementation. It concatenates the document's data dictionary file label attribute and
     * the document's document header description together. This title is used to populate workflow and will show up in document
     * search results and user action lists.
     *
     * @see org.kuali.core.document.Document#getDocumentTitle()
     */
    public String getDocumentTitle() {
        String documentTypeLabel = KNSServiceLocator.getDataDictionaryService().getDataDictionary().getDocumentEntry(this.getDocumentHeader().getWorkflowDocument().getDocumentType()).getLabel();
        if (null == documentTypeLabel) {
            documentTypeLabel = "";
        }

        String description = this.getDocumentHeader().getDocumentDescription();
        if (null == description) {
            description = "";
        }

        return documentTypeLabel + " - " + description;
    }

    /**
     * Uses the persistence service's implementation of OJB's retrieveNonKey() fields method.
     *
     * @see org.kuali.core.bo.BusinessObject#refresh()
     */
    public void refresh() {
        KNSServiceLocator.getPersistenceService().retrieveNonKeyFields(this);
    }

    /**
     * Checks to see if the objectId value is empty. If so, it will try to refresh the object from the DB.
     *
     * @see org.kuali.core.document.Document#refreshIfEmpty()
     */
    public void refreshIfEmpty() {
        if (null == this.getDocumentHeader()) {
            this.refresh();
        }
        else if (StringUtils.isEmpty(this.getDocumentHeader().getObjectId())) {
            this.refresh();
        }
    }

    /**
     * Uses the persistence service to retrieve a reference object of a parent.
     *
     * @see org.kuali.core.document.Document#refreshReferenceObject(java.lang.String)
     */
    public void refreshReferenceObject(String referenceObjectName) {
        KNSServiceLocator.getPersistenceService().retrieveReferenceObject(this, referenceObjectName);
    }

    /**
     * @param fieldValues
     * @return consistently-formatted String containing the given fieldnames and their values
     */
    protected String toStringBuilder(LinkedHashMap fieldValues) {
        String built = null;
        String className = StringUtils.uncapitalize(StringUtils.substringAfterLast(this.getClass().getName(), "."));

        if ((fieldValues == null) || fieldValues.isEmpty()) {
            built = super.toString();
        }
        else {

            StringBuffer prefix = new StringBuffer(className);
            StringBuffer suffix = new StringBuffer("=");

            prefix.append("documentHeaderId");
            suffix.append(this.getDocumentNumber());

            prefix.append("(");
            suffix.append("(");
            for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
                Map.Entry e = (Map.Entry) i.next();

                String fieldName = e.getKey().toString();
                Object fieldValue = e.getValue();

                String fieldValueString = String.valueOf(e.getValue()); // prevent NullPointerException;


                if ((fieldValue == null) || TypeUtils.isSimpleType(fieldValue.getClass())) {
                    prefix.append(fieldName);
                    suffix.append(fieldValueString);
                }
                else {
                    prefix.append("{");
                    prefix.append(fieldName);
                    prefix.append("}");

                    suffix.append("{");
                    suffix.append(fieldValueString);
                    suffix.append("}");
                }

                if (i.hasNext()) {
                    prefix.append(",");
                    suffix.append(",");
                }
            }
            prefix.append(")");
            suffix.append(")");

            built = prefix.toString() + suffix.toString();
        }

        return built;
    }

    /**
     * @return Map containing the fieldValues of the key fields for this class, indexed by fieldName
     */
    protected LinkedHashMap toStringMapper(){
        LinkedHashMap m = new LinkedHashMap();

        m.put("documentNumber", getDocumentNumber());
        m.put("versionNumber", getVersionNumber());

        return m;
    }

    /**
     * @see java.lang.Object#toString()
     */
    final public String toString() {
        return toStringBuilder(toStringMapper());
    }

    /**
     * @see org.kuali.core.document.Document#prepareForSave()
     */
    public void prepareForSave() {
        // do nothing
    }

    /**
     * This is the default implementation which ensures that document note attachment references are loaded.
     *
     * @see org.kuali.core.document.Document#processAfterRetrieve()
     */
    public void processAfterRetrieve() {
        // KULRNE-5692 - force a refresh of the attachments
        // they are not (non-updateable) references and don't seem to update properly upon load
        DocumentHeader dh = getDocumentHeader();
        if (dh != null) {
            List<Note> notes = dh.getBoNotes();
            if (notes != null) {
                for (Note note : notes) {
                    note.refreshReferenceObject("attachment");
                }
            }
        }
    }

    /**
     * This is the default implementation which checks for a different workflow statuses, and updates the Kuali status accordingly.
     *
     * @see org.kuali.core.document.Document#handleRouteStatusChange()
     */
    public void handleRouteStatusChange() {
        // do nothing
    }

    /**
     * The the default implementation for RouteLevelChange does nothing, but is meant to provide a hook for documents to implement
     * for other needs.
     *
     * @see org.kuali.core.document.Document#handleRouteLevelChange(edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO)
     */
    public void handleRouteLevelChange(DocumentRouteLevelChangeVO levelChangeEvent) {
        // do nothing
    }
    
    /**
     * @see org.kuali.core.document.Document#doActionTaken(edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO)
     */
    public void doActionTaken(ActionTakenEventVO event) {
        if ( (KNSServiceLocator.getDataDictionaryService().getDataDictionary().getDocumentEntry(this.getClass().getName()).getUseWorkflowPessimisticLocking()) && (!getNonLockingActionTakenCodes().contains(event.getActionTaken().getActionTaken())) ) {
            DocumentAuthorizer documentAuthorizer = KNSServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(this);
            documentAuthorizer.establishWorkflowPessimisticLocking(this);
        }
    }
    
    protected List<String> getNonLockingActionTakenCodes() {
        List<String> actionTakenStatusCodes = new ArrayList<String>();
        actionTakenStatusCodes.add(EdenConstants.ACTION_TAKEN_SAVED_CD);
        actionTakenStatusCodes.add(EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD);
        actionTakenStatusCodes.add(EdenConstants.ACTION_TAKEN_FYI_CD);
        actionTakenStatusCodes.add(EdenConstants.ACTION_TAKEN_DENIED_CD);
        actionTakenStatusCodes.add(EdenConstants.ACTION_TAKEN_CANCELED_CD);
        actionTakenStatusCodes.add(EdenConstants.ACTION_TAKEN_LOG_DOCUMENT_ACTION_CD);
        return actionTakenStatusCodes;
    }

    /**
     * The the default implementation for afterWorkflowEngineProcess does nothing, but is meant to provide a hook for
     * documents to implement for other needs.
     * 
     * @see org.kuali.core.document.Document#afterWorkflowEngineProcess(boolean)
     */
    public void afterWorkflowEngineProcess(boolean successfullyProcessed) {
        if (KNSServiceLocator.getDataDictionaryService().getDataDictionary().getDocumentEntry(this.getClass().getName()).getUseWorkflowPessimisticLocking()) {
            if (successfullyProcessed) {
                DocumentAuthorizer documentAuthorizer = KNSServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(this);
                documentAuthorizer.releaseWorkflowPessimisticLocking(this);
            }
        }
    }

    /**
     * The the default implementation for beforeWorkflowEngineProcess does nothing, but is meant to provide a hook for
     * documents to implement for other needs.
     * 
     * @see org.kuali.core.document.Document#beforeWorkflowEngineProcess()
     */
    public void beforeWorkflowEngineProcess() {
    // do nothing
    }

    /**
     * @see org.kuali.core.document.Copyable#toCopy()
     */
    public void toCopy() throws WorkflowException, IllegalStateException {
        if (!this.getAllowsCopy()) {
            throw new IllegalStateException(this.getClass().getName() + " does not support document-level copying");
        }
        String sourceDocumentHeaderId = getDocumentNumber();
        setNewDocumentHeader();
        
        getDocumentBusinessObject().getBoNotes();
        
        getDocumentHeader().setDocumentTemplateNumber(sourceDocumentHeaderId);

        addCopyErrorDocumentNote("copied from document " + sourceDocumentHeaderId);
    }

    /**
     * Gets a new document header for this documents type and sets in the document instance.
     * 
     * @throws WorkflowException
     */
    protected void setNewDocumentHeader() throws WorkflowException {
        TransactionalDocument newDoc = (TransactionalDocument) KNSServiceLocator.getDocumentService().getNewDocument(getDocumentHeader().getWorkflowDocument().getDocumentType());
        newDoc.getDocumentHeader().setDocumentDescription(getDocumentHeader().getDocumentDescription());
        newDoc.getDocumentHeader().setOrganizationDocumentNumber(getDocumentHeader().getOrganizationDocumentNumber());

        try {
            ObjectUtils.setObjectPropertyDeep(this, KNSPropertyConstants.DOCUMENT_NUMBER, documentNumber.getClass(), newDoc.getDocumentNumber());
        }
        catch (Exception e) {
            LOG.error("Unable to set document number property in copied document " + e.getMessage());
            throw new RuntimeException("Unable to set document number property in copied document " + e.getMessage());
        }

        // replace current documentHeader with new documentHeader
        setDocumentHeader(newDoc.getDocumentHeader());
    }

    /**
     * Adds a note to the document indicating it was created by a copy or error correction.
     * 
     * @param noteText - text for note
     */
    protected void addCopyErrorDocumentNote(String noteText) {
        Note note = null;
        try {
            note = KNSServiceLocator.getDocumentService().createNoteFromDocument(this,noteText);
        }
        catch (Exception e) {
         logErrors();
         throw new RuntimeException("Couldn't create note on copy or error");
        }
        KNSServiceLocator.getDocumentService().addNoteToDocument(this, note);
    }

    /**
     * @see org.kuali.core.document.Document#getXmlForRouteReport()
     */
    public String getXmlForRouteReport() {
	prepareForSave();
	populateDocumentForRouting();
	return getDocumentHeader().getWorkflowDocument().getApplicationContent();
    }

    /**
     * @see org.kuali.core.document.Document#populateDocumentForRouting()
     */
    public void populateDocumentForRouting() {
        getDocumentHeader().getWorkflowDocument().setApplicationContent(serializeDocumentToXml());
    }
    
    /**
     * @see org.kuali.core.document.Document#serializeDocumentToXml()
     */
    public String serializeDocumentToXml() {
        DocumentSerializerService documentSerializerService = KNSServiceLocator.getDocumentSerializerService();
        String xml = documentSerializerService.serializeDocumentToXmlForRouting(this);
        return xml;
    }

    /**
     * Wraps a document in an instance of KualiDocumentXmlMaterializer, that provides additional metadata for serialization
     * 
     * @see org.kuali.core.document.Document#wrapDocumentWithMetadataForXmlSerialization()
     */
    public KualiDocumentXmlMaterializer wrapDocumentWithMetadataForXmlSerialization() {
        KualiTransactionalDocumentInformation transInfo = new KualiTransactionalDocumentInformation();
        DocumentInitiator initiatior = new DocumentInitiator();
        String initiatorNetworkId = getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
        try {
            UniversalUser initiatorUser = KNSServiceLocator.getUniversalUserService().getUniversalUser(new AuthenticationUserId(initiatorNetworkId));
            initiatorUser.getModuleUsers(); // init the module users map for serialization
            initiatior.setUniversalUser(initiatorUser);
        }
        catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        transInfo.setDocumentInitiator(initiatior);
        KualiDocumentXmlMaterializer xmlWrapper = new KualiDocumentXmlMaterializer();
        xmlWrapper.setDocument(this);
        xmlWrapper.setKualiTransactionalDocumentInformation(transInfo);
        return xmlWrapper;
    }

    /**
     * If workflowProperties have been defined within the data dictionary for this document, then it returns an instance of 
     * {@link BusinessObjectPropertySerializibilityEvaluator} initialized with the properties.  If none have been defined, then returns 
     * {@link AlwaysTruePropertySerializibilityEvaluator}.
     * 
     * @see org.kuali.core.document.Document#getDocumentPropertySerizabilityEvaluator()
     */
    public PropertySerializabilityEvaluator getDocumentPropertySerizabilityEvaluator() {
        String docTypeName = getDocumentHeader().getWorkflowDocument().getDocumentType();
        DocumentEntry documentEntry = KNSServiceLocator.getDataDictionaryService().getDataDictionary().getDocumentEntry(docTypeName);
        WorkflowProperties workflowProperties = documentEntry.getWorkflowProperties();
        return createPropertySerializabilityEvaluator(workflowProperties);
    }
    
    protected PropertySerializabilityEvaluator createPropertySerializabilityEvaluator(WorkflowProperties workflowProperties) {
        if (workflowProperties == null) {
            return new AlwaysTruePropertySerializibilityEvaluator();
        }
        else {
            PropertySerializabilityEvaluator evaluator = new BusinessObjectPropertySerializibilityEvaluator();
            evaluator.initializeEvaluator(this);
            return evaluator;
        }
    }
    
    /**
     * Returns the POJO property name of "this" document in the object returned by {@link #wrapDocumentWithMetadataForXmlSerialization()}
     * 
     * @see org.kuali.core.document.Document#getBasePathToDocumentDuringSerialization()
     */
    public String getBasePathToDocumentDuringSerialization() {
        return "document";
    }
    
    
    /**
     * @see org.kuali.core.document.Document#getDocumentHeader()
     */
    public DocumentHeader getDocumentHeader() {
        return this.documentHeader;
    }

    /**
     * @see org.kuali.core.document.Document#setDocumentHeader(org.kuali.core.document.DocumentHeader)
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    /**
     * @see org.kuali.core.document.Document#getDocumentNumber()
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * @see org.kuali.core.document.Document#setDocumentNumber(java.lang.String)
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @see org.kuali.core.document.Document#getAdHocRoutePersons()
     */
    public List getAdHocRoutePersons() {
        return adHocRoutePersons;
    }

    /**
     * @see org.kuali.core.document.Document#setAdHocRoutePersons(java.util.List)
     */
    public void setAdHocRoutePersons(List adHocRoutePersons) {
        this.adHocRoutePersons = adHocRoutePersons;
}
    /**
     * @see org.kuali.core.document.Document#getAdHocRouteWorkgroups()
     */
    public List getAdHocRouteWorkgroups() {
        return adHocRouteWorkgroups;
    }

    /**
     * @see org.kuali.core.document.Document#setAdHocRouteWorkgroups(java.util.List)
     */
    public void setAdHocRouteWorkgroups(List adHocRouteWorkgroups) {
        this.adHocRouteWorkgroups = adHocRouteWorkgroups;
    }

    public void postProcessSave(KualiDocumentEvent event) {
        // TODO Auto-generated method stub

	}

    /**
     * Override this method with implementation specific prepareForSave logic
     * 
     * @see org.kuali.core.document.Document#prepareForSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    public void prepareForSave(KualiDocumentEvent event) {

    }

    public void validateBusinessRules(KualiDocumentEvent event) {
        if (!GlobalVariables.getErrorMap().isEmpty()) {
            logErrors();
            throw new ValidationException("errors occured before business rule");
        }

        // perform validation against rules engine
        LOG.info("invoking rules engine on document " + getDocumentNumber());
        boolean isValid = true;
        isValid = KNSServiceLocator.getKualiRuleService().applyRules(event);

        // check to see if the br eval passed or failed
        if (!isValid) {
            logErrors();
            // TODO: better error handling at the lower level and a better error message are
            // needed here
            throw new ValidationException("business rule evaluation failed");
        }
        else if (!GlobalVariables.getErrorMap().isEmpty()) {
            logErrors();
            throw new ValidationException("Unreported errors occured during business rule evaluation (rule developer needs to put meaningful error messages into global ErrorMap)");
        }
        LOG.debug("validation completed");

    }

    /**
     * This method logs errors.
     */
    protected void logErrors() {
        if (!GlobalVariables.getErrorMap().isEmpty()) {

            for (Iterator i = GlobalVariables.getErrorMap().entrySet().iterator(); i.hasNext();) {
                Map.Entry e = (Map.Entry) i.next();

                StringBuffer logMessage = new StringBuffer();
                logMessage.append("[" + e.getKey() + "] ");
                boolean first = true;

                TypedArrayList errorList = (TypedArrayList) e.getValue();
                for (Iterator j = errorList.iterator(); j.hasNext();) {
                    ErrorMessage em = (ErrorMessage) j.next();

                    if (first) {
                        first = false;
                    }
                    else {
                        logMessage.append(";");
                    }
                    logMessage.append(em);
                }

                LOG.error(logMessage);
            }
        }
    }

    /**
     * Hook for override
     * 
     * @see org.kuali.core.document.Document#generateSaveEvents()
     */
    public List generateSaveEvents() {
        return new ArrayList();
    }

    public void doRouteStatusChange(DocumentRouteStatusChangeVO statusChangeEvent) throws Exception {
    }

    /**
     * Gets the documentBusinessObject attribute.
     * 
     * @return Returns the documentBusinessObject.
     */
    public PersistableBusinessObject getDocumentBusinessObject() {
        if (documentBusinessObject == null) {
            documentBusinessObject = this;
        }
        return documentBusinessObject;

    }

    @Override
    public void afterLookup(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.afterLookup(persistenceBroker);
        refreshPessimisticLocks();
    }

    /**
     * @see org.kuali.core.document.Document#getPessimisticLocks()
     */
    public List<PessimisticLock> getPessimisticLocks() {
        return this.pessimisticLocks;
    }
    
    /**
     * @see org.kuali.core.document.Document#refreshPessimisticLocks()
     */
    public void refreshPessimisticLocks() {
        this.pessimisticLocks.clear();
        this.pessimisticLocks = KNSServiceLocator.getPessimisticLockService().getPessimisticLocksForDocument(this.documentNumber);
    }

    /**
     * @param pessimisticLocks the PessimisticLock objects to set
     */
    public void setPessimisticLocks(List<PessimisticLock> pessimisticLocks) {
        this.pessimisticLocks = pessimisticLocks;
    }
    
    /**
     * @see org.kuali.core.document.Document#addPessimisticLock(org.kuali.core.document.authorization.PessimisticLock)
     */
    public void addPessimisticLock(PessimisticLock lock) {
        this.pessimisticLocks.add(lock);
    }
    
    /**
     * @see org.kuali.core.document.Document#getLockClearningMethodNames()
     */
    public List<String> getLockClearningMethodNames() {
        List<String> methodToCalls = new ArrayList<String>();
        methodToCalls.add(KNSConstants.CLOSE_METHOD);
        methodToCalls.add(KNSConstants.CANCEL_METHOD);
//        methodToCalls.add(RiceConstants.BLANKET_APPROVE_METHOD);
        methodToCalls.add(KNSConstants.ROUTE_METHOD);
        methodToCalls.add(KNSConstants.APPROVE_METHOD);
        methodToCalls.add(KNSConstants.DISAPPROVE_METHOD);
        return methodToCalls;
    }

}