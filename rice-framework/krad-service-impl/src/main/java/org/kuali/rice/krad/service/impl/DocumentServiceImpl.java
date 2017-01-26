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
package org.kuali.rice.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.framework.persistence.jta.TransactionalNoValidationExceptionRollback;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.UserSessionUtils;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.bo.AdHocRouteWorkgroup;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.datadictionary.exception.UnknownDocumentTypeException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.DocumentAuthorizer;
import org.kuali.rice.krad.document.DocumentPresentationController;
import org.kuali.rice.krad.exception.DocumentAuthorizationException;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.maintenance.Maintainable;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.MaintenanceDocumentBase;
import org.kuali.rice.krad.rules.rule.event.ApproveDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.BlanketApproveDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.CompleteDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.DocumentEvent;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveEvent;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentAdHocService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.DocumentHeaderService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.NoteType;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.OptimisticLockingFailureException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for the Document structure. It contains all of the document level type of
 * processing and calling back into documents for various centralization of functionality. This is the default,
 * Kuali delivered implementation which utilizes Workflow.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@TransactionalNoValidationExceptionRollback
public class DocumentServiceImpl implements DocumentService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentServiceImpl.class);

    protected DateTimeService dateTimeService;
    protected NoteService noteService;
    protected WorkflowDocumentService workflowDocumentService;
    protected LegacyDataAdapter legacyDataAdapter;
    protected DataDictionaryService dataDictionaryService;
    protected DocumentDictionaryService documentDictionaryService;
    protected PersonService personService;
    protected ConfigurationService kualiConfigurationService;
    protected DocumentHeaderService documentHeaderService;
    protected DocumentAdHocService documentAdHocService;

    /**
     * @see org.kuali.rice.krad.service.DocumentService#saveDocument(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Document saveDocument(Document document) throws WorkflowException, ValidationException {
        return saveDocument(document, SaveDocumentEvent.class);
    }

    /**
     * saves the document with the custom document event passed in
     *
     * {@inheritDoc}
     */
    @Override
    public Document saveDocument( Document document, DocumentEvent event ) throws WorkflowException {
        checkForNulls(document);

        if( event == null ) {
            throw new IllegalArgumentException( "invalid (null) DocumentEvent instance" );
        }

        // if event is not an instance of a SaveDocumentEvent or a SaveOnlyDocumentEvent
        if ( !SaveEvent.class.isAssignableFrom( event.getClass() ) ) {
            throw new ConfigurationException( "The KualiDocumentEvent class '" + event.getClass().getName() +
                    "' does not implement the class '" + SaveEvent.class.getName() + "'");
        }

        document.prepareForSave();
        Document savedDocument = validateAndPersistDocumentAndSaveAdHocRoutingRecipients( document, event );

        prepareWorkflowDocument( savedDocument );
        getWorkflowDocumentService().save( savedDocument.getDocumentHeader().getWorkflowDocument(), null );

        UserSessionUtils.addWorkflowDocument( GlobalVariables.getUserSession(),
                savedDocument.getDocumentHeader().getWorkflowDocument() );

        return savedDocument;
    }

    @Override
    public Document saveDocument(Document document,
            Class<? extends DocumentEvent> kualiDocumentEventClass) throws WorkflowException, ValidationException {
        checkForNulls(document);
        if (kualiDocumentEventClass == null) {
            throw new IllegalArgumentException("invalid (null) kualiDocumentEventClass");
        }
        // if event is not an instance of a SaveDocumentEvent or a SaveOnlyDocumentEvent
        if (!SaveEvent.class.isAssignableFrom(kualiDocumentEventClass)) {
            throw new ConfigurationException("The KualiDocumentEvent class '" + kualiDocumentEventClass.getName() +
                    "' does not implement the class '" + SaveEvent.class.getName() + "'");
        }
//        if (!getDocumentActionFlags(document).getCanSave()) {
//            throw buildAuthorizationException("save", document);
//        }
        document.prepareForSave();
        Document savedDocument = validateAndPersistDocumentAndSaveAdHocRoutingRecipients(document,
                generateKualiDocumentEvent(document, kualiDocumentEventClass));
        prepareWorkflowDocument(savedDocument);
        getWorkflowDocumentService().save(savedDocument.getDocumentHeader().getWorkflowDocument(), null);

        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(),
                savedDocument.getDocumentHeader().getWorkflowDocument());

        return savedDocument;
    }

    private DocumentEvent generateKualiDocumentEvent(Document document,
            Class<? extends DocumentEvent> eventClass) throws ConfigurationException {
        String potentialErrorMessage =
                "Found error trying to generate Kuali Document Event using event class '" + eventClass.getName() +
                        "' for document " + document.getDocumentNumber();

        try {
            Constructor<?> usableConstructor = null;
            List<Object> paramList = new ArrayList<Object>();
            for (Constructor<?> currentConstructor : eventClass.getConstructors()) {
                for (Class<?> parameterClass : currentConstructor.getParameterTypes()) {
                    if (Document.class.isAssignableFrom(parameterClass)) {
                        usableConstructor = currentConstructor;
                        paramList.add(document);
                    } else {
                        paramList.add(null);
                    }
                }
                if (KRADUtils.isNotNull(usableConstructor)) {
                    break;
                }
            }
            if (usableConstructor == null) {
                throw new RuntimeException("Cannot find a constructor for class '" + eventClass.getName() +
                        "' that takes in a document parameter");
            }
            return (DocumentEvent) usableConstructor.newInstance(paramList.toArray());
        } catch (SecurityException e) {
            throw new ConfigurationException(potentialErrorMessage, e);
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException(potentialErrorMessage, e);
        } catch (InstantiationException e) {
            throw new ConfigurationException(potentialErrorMessage, e);
        } catch (IllegalAccessException e) {
            throw new ConfigurationException(potentialErrorMessage, e);
        } catch (InvocationTargetException e) {
            throw new ConfigurationException(potentialErrorMessage, e);
        }
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#routeDocument(org.kuali.rice.krad.document.Document,
     *      java.lang.String, java.util.List)
     */
    @Override
    public Document routeDocument(Document document, String annotation,
            List<AdHocRouteRecipient> adHocRecipients) throws ValidationException, WorkflowException {
        checkForNulls(document);
        //if (!getDocumentActionFlags(document).getCanRoute()) {
        //    throw buildAuthorizationException("route", document);
        //}
        document.prepareForSave();
        Document savedDocument = validateAndPersistDocument(document, new RouteDocumentEvent(document));
        prepareWorkflowDocument(savedDocument);
        getWorkflowDocumentService()
                .route(savedDocument.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);
        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(),
                savedDocument.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(savedDocument);
        return savedDocument;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#approveDocument(org.kuali.rice.krad.document.Document,
     *      java.lang.String,
     *      java.util.List)
     */
    @Override
    public Document approveDocument(Document document, String annotation,
            List<AdHocRouteRecipient> adHocRecipients) throws ValidationException, WorkflowException {
        checkForNulls(document);
        //if (!getDocumentActionFlags(document).getCanApprove()) {
        //    throw buildAuthorizationException("approve", document);
        //}
        document.prepareForSave();
        Document savedDocument = validateAndPersistDocument(document, new ApproveDocumentEvent(document));
        prepareWorkflowDocument(savedDocument);
        getWorkflowDocumentService()
                .approve(savedDocument.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);
        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(),
                savedDocument.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(savedDocument);
        return savedDocument;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#superUserApproveDocument(org.kuali.rice.krad.document.Document,
     *      java.lang.String)
     */
    @Override
    public Document superUserApproveDocument(Document document, String annotation) throws WorkflowException {
        Document savedDocument = getLegacyDataAdapter().saveDocument(document);
        savedDocument.processAfterRetrieve();
        // Need to preserve the workflow document header, which just got left behind
        savedDocument.getDocumentHeader().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        prepareWorkflowDocument(savedDocument);
        getWorkflowDocumentService().superUserApprove(savedDocument.getDocumentHeader().getWorkflowDocument(), annotation);
        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(),
        		savedDocument.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(savedDocument);
        return savedDocument;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#superUserCancelDocument(org.kuali.rice.krad.document.Document,
     *      java.lang.String)
     */
    @Override
    public Document superUserCancelDocument(Document document, String annotation) throws WorkflowException {
        Document savedDocument = getLegacyDataAdapter().saveDocument(document);
        savedDocument.processAfterRetrieve();
        // Need to preserve the workflow document header, which just got left behind
        savedDocument.getDocumentHeader().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        prepareWorkflowDocument(savedDocument);
        getWorkflowDocumentService().superUserCancel(savedDocument.getDocumentHeader().getWorkflowDocument(), annotation);
        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(),
        		savedDocument.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(savedDocument);
        return savedDocument;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#superUserCancelDocument(org.kuali.rice.krad.document.Document,
     *      java.lang.String)
     */
    @Override
    public Document superUserDisapproveDocument(Document document, String annotation) throws WorkflowException {
        Document savedDocument = getLegacyDataAdapter().saveDocument(document);
        savedDocument.processAfterRetrieve();
        // Need to preserve the workflow document header, which just got left behind
        savedDocument.getDocumentHeader().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        return superUserDisapproveDocumentWithoutSaving(savedDocument, annotation);
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#superUserCancelDocument(org.kuali.rice.krad.document.Document,
     *      java.lang.String)
     */
    @Override
    public Document superUserDisapproveDocumentWithoutSaving(Document document, String annotation) throws WorkflowException {
        prepareWorkflowDocument(document);
        getWorkflowDocumentService()
                .superUserDisapprove(document.getDocumentHeader().getWorkflowDocument(), annotation);
        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(),
                document.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(document);
        return document;
    }


    /**
     * @see org.kuali.rice.krad.service.DocumentService#disapproveDocument(org.kuali.rice.krad.document.Document,
     *      java.lang.String)
     */
    @Override
    public Document disapproveDocument(Document document, String annotation) throws Exception {
        checkForNulls(document);

        Note note = createNoteFromDocument(document, annotation);
        //if note type is BO, override and link disapprove notes to Doc Header
        if (document.getNoteType().equals(NoteType.BUSINESS_OBJECT)) {
            note.setNoteTypeCode(NoteType.DOCUMENT_HEADER.getCode());
            note.setRemoteObjectIdentifier(document.getDocumentHeader().getObjectId());
        }
        document.addNote(note);

        //SAVE THE NOTE
        //Note: This save logic is replicated here and in KualiDocumentAction, when to save (based on doc state) should be moved
        //      into a doc service method
        getNoteService().save(note);

        prepareWorkflowDocument(document);
        getWorkflowDocumentService().disapprove(document.getDocumentHeader().getWorkflowDocument(), annotation);
        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(),
                document.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(document);
        return document;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#cancelDocument(org.kuali.rice.krad.document.Document,
     *      java.lang.String)
     */
    @Override
    public Document cancelDocument(Document document, String annotation) throws WorkflowException {
        checkForNulls(document);
        //if (!getDocumentActionFlags(document).getCanCancel()) {
        //    throw buildAuthorizationException("cancel", document);
        //}
        if (document instanceof MaintenanceDocument) {
            MaintenanceDocument maintDoc = ((MaintenanceDocument) document);
            if (maintDoc.getOldMaintainableObject() != null &&
                    (maintDoc.getOldMaintainableObject().getDataObject() instanceof BusinessObject)) {
                ((BusinessObject) maintDoc.getOldMaintainableObject().getDataObject()).refresh();
            }

            if (maintDoc.getNewMaintainableObject().getDataObject() instanceof BusinessObject) {
                ((BusinessObject) maintDoc.getNewMaintainableObject().getDataObject()).refresh();
            }
        }
        prepareWorkflowDocument(document);
        getWorkflowDocumentService().cancel(document.getDocumentHeader().getWorkflowDocument(), annotation);
        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(),
                document.getDocumentHeader().getWorkflowDocument());

        removeAdHocPersonsAndWorkgroups(document);
        return document;
    }

    @Override
    public Document recallDocument(Document document, String annotation, boolean cancel) throws WorkflowException {
        checkForNulls(document);
        WorkflowDocument workflowDocument = KRADServiceLocatorWeb.getDocumentService().
                getByDocumentHeaderId(document.getDocumentNumber()).getDocumentHeader().getWorkflowDocument();

        if (!workflowDocument.isFinal() && !workflowDocument.isProcessed()) {
            Note note = createNoteFromDocument(document, annotation);
            document.addNote(note);
            getNoteService().save(note);
        }

        prepareWorkflowDocument(document);
        getWorkflowDocumentService().recall(document.getDocumentHeader().getWorkflowDocument(), annotation, cancel);
        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(),
                document.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(document);
        return document;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#acknowledgeDocument(org.kuali.rice.krad.document.Document,
     *      java.lang.String,
     *      java.util.List)
     */
    @Override
    public Document acknowledgeDocument(Document document, String annotation,
            List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException {
        checkForNulls(document);
        //if (!getDocumentActionFlags(document).getCanAcknowledge()) {
        //    throw buildAuthorizationException("acknowledge", document);
        //}
        prepareWorkflowDocument(document);
        getWorkflowDocumentService()
                .acknowledge(document.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);
        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(),
                document.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(document);
        return document;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#blanketApproveDocument(org.kuali.rice.krad.document.Document,
     *      java.lang.String,
     *      java.util.List)
     */
    @Override
    public Document blanketApproveDocument(Document document, String annotation,
            List<AdHocRouteRecipient> adHocRecipients) throws ValidationException, WorkflowException {
        checkForNulls(document);
        //if (!getDocumentActionFlags(document).getCanBlanketApprove()) {
        //    throw buildAuthorizationException("blanket approve", document);
        //}
        document.prepareForSave();
        Document savedDocument = validateAndPersistDocument(document, new BlanketApproveDocumentEvent(document));
        prepareWorkflowDocument(savedDocument);
        getWorkflowDocumentService()
                .blanketApprove(savedDocument.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);
        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(),
                savedDocument.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(savedDocument);
        return savedDocument;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#clearDocumentFyi(org.kuali.rice.krad.document.Document,
     *      java.util.List)
     */
    @Override
    public Document clearDocumentFyi(Document document,
            List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException {
        checkForNulls(document);
        // populate document content so searchable attributes will be indexed properly
        document.populateDocumentForRouting();
        getWorkflowDocumentService().clearFyi(document.getDocumentHeader().getWorkflowDocument(), adHocRecipients);
        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(),
                document.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(document);
        return document;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#completeDocument(org.kuali.rice.krad.document.Document,
     *      java.lang.String,
     *      java.util.List)
     */
    @Override
    public Document completeDocument(Document document, String annotation,
            List adHocRecipients) throws WorkflowException {
        checkForNulls(document);

        document.prepareForSave();
        Document savedDocument = validateAndPersistDocument(document, new CompleteDocumentEvent(document));

        prepareWorkflowDocument(savedDocument);
        getWorkflowDocumentService().complete(savedDocument.getDocumentHeader().getWorkflowDocument(), annotation,
                adHocRecipients);

        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(),
        		savedDocument.getDocumentHeader().getWorkflowDocument());

        removeAdHocPersonsAndWorkgroups(savedDocument);

        return savedDocument;
    }

    protected void checkForNulls(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        if (document.getDocumentNumber() == null) {
            throw new IllegalStateException("invalid (null) documentHeaderId");
        }
    }

    private Document validateAndPersistDocumentAndSaveAdHocRoutingRecipients(Document document,
            DocumentEvent event) {
        /*
         * Using this method to wrap validateAndPersistDocument to keep everything in one transaction. This avoids modifying the
         * signature on validateAndPersistDocument method
         */
        List<AdHocRouteRecipient> adHocRoutingRecipients = new ArrayList<AdHocRouteRecipient>();
        adHocRoutingRecipients.addAll(document.getAdHocRoutePersons());
        adHocRoutingRecipients.addAll(document.getAdHocRouteWorkgroups());

        documentAdHocService.replaceAdHocsForDocument( document.getDocumentNumber(), adHocRoutingRecipients );
        return validateAndPersistDocument(document, event);
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#documentExists(java.lang.String)
     */
    @Override
    public boolean documentExists(String documentHeaderId) {
        // validate parameters
        if (StringUtils.isBlank(documentHeaderId)) {
            throw new IllegalArgumentException("invalid (blank) documentHeaderId");
        }

        boolean internalUserSession = false;
        try {
            // KFSMI-2543 - allowed method to run without a user session so it can be used
            // by workflow processes
            if (GlobalVariables.getUserSession() == null) {
                internalUserSession = true;
                GlobalVariables.setUserSession(new UserSession(KRADConstants.SYSTEM_USER));
                GlobalVariables.clear();
            }

            // look for workflowDocumentHeader, since that supposedly won't break the transaction
            if (getWorkflowDocumentService().workflowDocumentExists(documentHeaderId)) {
                // look for docHeaderId, since that fails without breaking the transaction
                return documentHeaderService.getDocumentHeaderById(documentHeaderId) != null;
            }

            return false;
        } finally {
            // if a user session was established for this call, clear it our
            if (internalUserSession) {
                GlobalVariables.clear();
                GlobalVariables.setUserSession(null);
            }
        }
    }

    /**
     * Creates a new document by class.
     *
     * @see org.kuali.rice.krad.service.DocumentService#getNewDocument(java.lang.Class)
     */
    @Override
    public Document getNewDocument(Class<? extends Document> documentClass) throws WorkflowException {
        if (documentClass == null) {
            throw new IllegalArgumentException("invalid (null) documentClass");
        }
        if (!Document.class.isAssignableFrom(documentClass)) {
            throw new IllegalArgumentException("invalid (non-Document) documentClass");
        }

        String documentTypeName = getDataDictionaryService().getDocumentTypeNameByClass(documentClass);
        if (StringUtils.isBlank(documentTypeName)) {
            throw new UnknownDocumentTypeException(
                    "unable to get documentTypeName for unknown documentClass '" + documentClass.getName() + "'");
        }
        return getNewDocument(documentTypeName);
    }

    /**
     * Creates a new document by document type name. The principal name
     * passed in will be used as the document initiator.  If the  initiatorPrincipalNm
     * is null or blank, the current user will be used.
     *
     * @see org.kuali.rice.krad.service.DocumentService#getNewDocument(String, String)
     */
    @Override
    public Document getNewDocument(String documentTypeName, String initiatorPrincipalNm) throws WorkflowException {

        // argument validation
        String watchName = "DocumentServiceImpl.getNewDocument";
        StopWatch watch = new StopWatch();
        watch.start();
        if (LOG.isDebugEnabled()) {
            LOG.debug(watchName + ": started");
        }
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }
        if (GlobalVariables.getUserSession() == null) {
            throw new IllegalStateException(
                    "GlobalVariables must be populated with a valid UserSession before a new document can be created");
        }

        // get the class for this docTypeName
        Class<? extends Document> documentClass = getDocumentClassByTypeName(documentTypeName);

        // get the initiator
        Person initiator = null;
        if (StringUtils.isBlank(initiatorPrincipalNm)) {
            initiator = GlobalVariables.getUserSession().getPerson();
        } else {
            initiator = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(initiatorPrincipalNm);
            if (initiator == null) {
                initiator = GlobalVariables.getUserSession().getPerson();
            }
        }

        // get the authorization
        DocumentAuthorizer documentAuthorizer = getDocumentDictionaryService().getDocumentAuthorizer(documentTypeName);
        DocumentPresentationController documentPresentationController =
                getDocumentDictionaryService().getDocumentPresentationController(documentTypeName);
        // make sure this person is authorized to initiate
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("calling canInitiate from getNewDocument(" + documentTypeName + "," + initiatorPrincipalNm + ")");
        }
        if (!documentPresentationController.canInitiate(documentTypeName) ||
                !documentAuthorizer.canInitiate(documentTypeName, initiator)) {
            throw new DocumentAuthorizationException(initiator.getPrincipalName(), "initiate", documentTypeName);
        }

        // initiate new workflow entry, get the workflow doc
        WorkflowDocument workflowDocument = getWorkflowDocumentService().createWorkflowDocument(documentTypeName, initiator);
        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(), workflowDocument);

        // create a new document header object
        DocumentHeader documentHeader = new DocumentHeader();
        documentHeader.setWorkflowDocument(workflowDocument);
        documentHeader.setDocumentNumber(workflowDocument.getDocumentId());

        // build Document of specified type
        Document document = null;
        try {
            // all maintenance documents have same class
            if (MaintenanceDocumentBase.class.isAssignableFrom(documentClass)) {
                Class<?>[] defaultConstructor = new Class[]{String.class};
                Constructor<? extends Document> cons = documentClass.getConstructor(defaultConstructor);
                if (cons == null) {
                    throw new ConfigurationException(
                            "Could not find constructor with document type name parameter needed for Maintenance Document Base class");
                }
                document = cons.newInstance(documentTypeName);
            } else {
                // non-maintenance document
                document = documentClass.newInstance();
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error instantiating Document", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Error instantiating Document", e);
        } catch (SecurityException e) {
            throw new RuntimeException("Error instantiating Maintenance Document", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    "Error instantiating Maintenance Document: No constructor with String parameter found", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error instantiating Maintenance Document", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error instantiating Maintenance Document", e);
        }

        document.setDocumentHeader(documentHeader);
        document.setDocumentNumber(documentHeader.getDocumentNumber());

        watch.stop();
        if (LOG.isDebugEnabled()) {
            LOG.debug(watchName + ": " + watch.toString());
        }

        return document;
    }

    /**
     * Creates a new document by document type name.
     *
     * @see org.kuali.rice.krad.service.DocumentService#getNewDocument(java.lang.String)
     */
    @Override
    public Document getNewDocument(String documentTypeName) throws WorkflowException {
        return getNewDocument(documentTypeName, null);
    }


    /**
     * This is temporary until workflow 2.0 and reads from a table to get documents whose status has changed to A
     * (approved - no
     * outstanding approval actions requested)
     *
     * @param documentHeaderId
     * @return Document
     * @throws WorkflowException
     */
    @Override
    public Document getByDocumentHeaderId(String documentHeaderId) throws WorkflowException {
        if (documentHeaderId == null) {
            throw new IllegalArgumentException("invalid (null) documentHeaderId");
        }
        boolean internalUserSession = false;
        try {
            // KFSMI-2543 - allowed method to run without a user session so it can be used
            // by workflow processes
            if (GlobalVariables.getUserSession() == null) {
                internalUserSession = true;
                GlobalVariables.setUserSession(new UserSession(KRADConstants.SYSTEM_USER));
                GlobalVariables.clear();
            }

	        WorkflowDocument workflowDocument = null;

            if (LOG.isDebugEnabled()) {
                LOG.debug("Retrieving doc id: " + documentHeaderId + " from workflow service.");
            }
            workflowDocument = getWorkflowDocumentService()
                    .loadWorkflowDocument(documentHeaderId, GlobalVariables.getUserSession().getPerson());
            UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(), workflowDocument);

	        Class<? extends Document> documentClass = getDocumentClassByTypeName(workflowDocument.getDocumentTypeName());

            // retrieve the Document
	        Document document = getLegacyDataAdapter().findByDocumentHeaderId(documentClass, documentHeaderId);
            return postProcessDocument(documentHeaderId, workflowDocument, document);
        } finally {
            // if a user session was established for this call, clear it out
            if (internalUserSession) {
                GlobalVariables.clear();
                GlobalVariables.setUserSession(null);
            }
        }
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#getByDocumentHeaderIdSessionless(java.lang.String)
     */
    @Override
    public Document getByDocumentHeaderIdSessionless(String documentHeaderId) throws WorkflowException {
        if (documentHeaderId == null) {
            throw new IllegalArgumentException("invalid (null) documentHeaderId");
        }

        WorkflowDocument workflowDocument = null;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Retrieving doc id: " + documentHeaderId + " from workflow service.");
        }

        Person person = getPersonService().getPersonByPrincipalName(KRADConstants.SYSTEM_USER);
        workflowDocument = workflowDocumentService.loadWorkflowDocument(documentHeaderId, person);

        Class<? extends Document> documentClass = getDocumentClassByTypeName(workflowDocument.getDocumentTypeName());

        // retrieve the Document
        Document document = getLegacyDataAdapter().findByDocumentHeaderId(documentClass, documentHeaderId);

        return postProcessDocument(documentHeaderId, workflowDocument, document);
    }

    private Class<? extends Document> getDocumentClassByTypeName(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }

        Class<? extends Document> clazz = getDataDictionaryService().getDocumentClassByTypeName(documentTypeName);
        if (clazz == null) {
            throw new UnknownDocumentTypeException(
                    "unable to get class for unknown documentTypeName '" + documentTypeName + "'");
        }
        return clazz;
    }

    /**
     * Loads the Notes for the note target on this Document.
     *
     * @param document the document for which to load the notes
     */
    protected void loadNotes(final Document document) {
        if (isNoteTargetReady(document)) {
            Object legacyObjectClass;
            if (document instanceof MaintenanceDocument) {
                MaintenanceDocument mdoc = (MaintenanceDocument) document;
                legacyObjectClass = ((Maintainable) org.apache.commons.lang.ObjectUtils.defaultIfNull(mdoc.getOldMaintainableObject(), mdoc.getNewMaintainableObject())).getDataObjectClass();
            } else {
                legacyObjectClass = document.getClass();
            }

            List<Note> notes = new ArrayList<Note>();
            if (StringUtils.isNotBlank(document.getNoteTarget().getObjectId())) {
                notes.addAll(getNoteService().getByRemoteObjectId(document.getNoteTarget().getObjectId()));
            }
            //notes created on 'disapprove' are linked to Doc Header, so this checks that even if notetype = BO
            if (document.getNoteType().equals(NoteType.BUSINESS_OBJECT) && document.getDocumentHeader()
                    .getWorkflowDocument().isDisapproved()) {
                notes.addAll(getNoteService().getByRemoteObjectId(document.getDocumentHeader().getObjectId()));
            }

            document.setNotes(notes);
        }
    }

    /**
     * Performs required post-processing for every document from the documentDao
     *
     * @param documentHeaderId
     * @param workflowDocument
     * @param document
     */
    private Document postProcessDocument(String documentHeaderId, WorkflowDocument workflowDocument, Document document) {
        if (document != null) {
            document.getDocumentHeader().setWorkflowDocument(workflowDocument);
            document.processAfterRetrieve();
            loadNotes(document);
        }
        return document;
    }

    /**
     * The default implementation - this retrieves all documents by a list of documentHeader for a given class.
     *
     * @see org.kuali.rice.krad.service.DocumentService#getDocumentsByListOfDocumentHeaderIds(java.lang.Class,
     *      java.util.List)
     */
    @Override
    public List<Document> getDocumentsByListOfDocumentHeaderIds(Class<? extends Document> documentClass,
            List<String> documentHeaderIds) throws WorkflowException {
        // validate documentHeaderIdList and contents
        if (documentHeaderIds == null) {
            throw new IllegalArgumentException("invalid (null) documentHeaderId list");
        }
        int index = 0;
        for (String documentHeaderId : documentHeaderIds) {
            if (StringUtils.isBlank(documentHeaderId)) {
                throw new IllegalArgumentException("invalid (blank) documentHeaderId at list index " + index);
            }
            index++;
        }

        boolean internalUserSession = false;
        try {
            // KFSMI-2543 - allowed method to run without a user session so it can be used
            // by workflow processes
            if (GlobalVariables.getUserSession() == null) {
                internalUserSession = true;
                GlobalVariables.setUserSession(new UserSession(KRADConstants.SYSTEM_USER));
                GlobalVariables.clear();
            }

            // retrieve all documents that match the document header ids
            List<? extends Document> rawDocuments = getLegacyDataAdapter().findByDocumentHeaderIds(documentClass,
                    documentHeaderIds);

	        // post-process them
	        List<Document> documents = new ArrayList<Document>();
	        for (Document document : rawDocuments) {
	            WorkflowDocument workflowDocument = getWorkflowDocumentService().loadWorkflowDocument(document.getDocumentNumber(), GlobalVariables.getUserSession().getPerson());

                document = postProcessDocument(document.getDocumentNumber(), workflowDocument, document);
                documents.add(document);
            }
            return documents;
        } finally {
            // if a user session was established for this call, clear it our
            if (internalUserSession) {
                GlobalVariables.clear();
                GlobalVariables.setUserSession(null);
            }
        }
    }

    /* Helper Methods */

    /**
     * Validates and persists a document.
     */
    @Override
    public Document validateAndPersistDocument(Document document, DocumentEvent event) throws ValidationException {
        if (document == null) {
            LOG.error("document passed to validateAndPersist was null");
            throw new IllegalArgumentException("invalid (null) document");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("validating and preparing to persist document " + document.getDocumentNumber());
        }

        document.validateBusinessRules(event);
        document.prepareForSave(event);

        // save the document
        Document savedDocument = null;
        try {
            if (LOG.isInfoEnabled()) {
                LOG.info("storing document " + document.getDocumentNumber());
            }
            savedDocument = getLegacyDataAdapter().saveDocument(document);
            // Need to preserve the workflow document header, which just got left behind
            savedDocument.getDocumentHeader().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
            savedDocument.processAfterRetrieve();
        } catch (OptimisticLockingFailureException e) {
            LOG.error("exception encountered on store of document " + e.getMessage());
            throw e;
        }

        boolean notesSaved = saveDocumentNotes(savedDocument);
        if (!notesSaved) {
            if (LOG.isInfoEnabled()) {
                LOG.info(
                        "Notes not saved during validateAndPersistDocument, likely means that note save needs to be deferred because note target is not ready.");
            }
        }

        savedDocument.postProcessSave(event);

        return savedDocument;
    }

    /**
     * Sets the title and app document id in the flex document
     *
     * @param document
     * @throws org.kuali.rice.kew.api.exception.WorkflowException
     */
    @Override
    public void prepareWorkflowDocument(Document document) throws WorkflowException {
        // populate document content so searchable attributes will be indexed properly
        document.populateDocumentForRouting();

        // make sure we push the document title into the workflowDocument
        populateDocumentTitle(document);

        // make sure we push the application document id into the workflowDocument
        populateApplicationDocumentId(document);
    }

    /**
     * This method will grab the generated document title from the document and add it to the workflowDocument so that
     * it gets pushed into
     * workflow when routed.
     *
     * @param document
     * @throws org.kuali.rice.kew.api.exception.WorkflowException
     */
    private void populateDocumentTitle(Document document) throws WorkflowException {
        String documentTitle = document.getDocumentTitle();
        if (StringUtils.isNotBlank(documentTitle)) {
            document.getDocumentHeader().getWorkflowDocument().setTitle(documentTitle);
        }
    }

    /**
     * This method will grab the organization document number from the document and add it to the workflowDocument so
     * that it gets pushed
     * into workflow when routed.
     *
     * @param document
     */
    private void populateApplicationDocumentId(Document document) {
        String organizationDocumentNumber = document.getDocumentHeader().getOrganizationDocumentNumber();
        if (StringUtils.isNotBlank(organizationDocumentNumber)) {
            document.getDocumentHeader().getWorkflowDocument().setApplicationDocumentId(organizationDocumentNumber);
        }
    }

    /**
     * This is to allow for updates of document statuses and other related requirements for updates outside of the
     * initial save and
     * route
     */
    @Override
    public Document updateDocument(Document document) {
        checkForNulls(document);
        Document savedDocument = getLegacyDataAdapter().saveDocument(document);
        savedDocument.processAfterRetrieve();
        // Need to preserve the workflow document header, which just got left behind
        savedDocument.getDocumentHeader().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        return savedDocument;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#createNoteFromDocument(org.kuali.rice.krad.document.Document,
     *      java.lang.String)
     */
    @Override
    public Note createNoteFromDocument(Document document, String text) {
        Note note = new Note();

        note.setNotePostedTimestamp(getDateTimeService().getCurrentTimestamp());
        note.setNoteText(text);
        note.setNoteTypeCode(document.getNoteType().getCode());

        GloballyUnique bo = document.getNoteTarget();
        // TODO gah! this is awful
        Person kualiUser = GlobalVariables.getUserSession().getPerson();
        if (kualiUser == null) {
            throw new IllegalStateException("Current UserSession has a null Person.");
        }
        return bo == null ? null : getNoteService().createNote(note, bo, kualiUser.getPrincipalId());
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#saveDocumentNotes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean saveDocumentNotes(Document document) {
        if (isNoteTargetReady(document)) {
            List<Note> notes = document.getNotes();
            for (Note note : document.getNotes()) {
                linkNoteRemoteObjectId(note, document.getNoteTarget());
            }
            getNoteService().saveNoteList(notes);
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService
     */
    @Override
    public Document sendNoteRouteNotification(Document document, Note note, Person sender) throws WorkflowException {
        AdHocRouteRecipient routeRecipient = note.getAdHocRouteRecipient();

        // build notification request
        Person requestedUser = this.getPersonService().getPersonByPrincipalName(routeRecipient.getId());
        String senderName = sender.getFirstName() + " " + sender.getLastName();
        String requestedName = requestedUser.getFirstName() + " " + requestedUser.getLastName();

        String notificationText =
                kualiConfigurationService.getPropertyValueAsString(
                        RiceKeyConstants.MESSAGE_NOTE_NOTIFICATION_ANNOTATION);
        if (StringUtils.isBlank(notificationText)) {
            throw new RuntimeException(
                    "No annotation message found for note notification. Message needs added to application resources with key:" +
                            RiceKeyConstants.MESSAGE_NOTE_NOTIFICATION_ANNOTATION);
        }
        notificationText =
                MessageFormat.format(notificationText, new Object[]{senderName, requestedName, note.getNoteText()});

        List<AdHocRouteRecipient> routeRecipients = new ArrayList<AdHocRouteRecipient>();
        routeRecipients.add(routeRecipient);

        workflowDocumentService
                .sendWorkflowNotification(document.getDocumentHeader().getWorkflowDocument(), notificationText,
                        routeRecipients, KRADConstants.NOTE_WORKFLOW_NOTIFICATION_REQUEST_LABEL);

        // clear recipient allowing an notification to be sent to another person
        note.setAdHocRouteRecipient(new AdHocRoutePerson());
        return document;
    }

    /**
     * Determines if the given document's note target is ready for notes to be
     * attached and persisted against it.  This method verifies that the document's
     * note target is non-null as well as checking that it has a non-empty object id.
     *
     * @param document the document on which to check for note target readiness
     * @return true if the note target is ready, false otherwise
     */
    protected boolean isNoteTargetReady(Document document) {

        //special case for disappoved documents
        if (document.getDocumentHeader().getWorkflowDocument().isDisapproved()) {
            return true;
        }
        GloballyUnique noteTarget = document.getNoteTarget();
        if (noteTarget == null || StringUtils.isBlank(noteTarget.getObjectId())) {
            return false;
        }
        return true;
    }

    private void linkNoteRemoteObjectId(Note note, GloballyUnique noteTarget) {
        String objectId = noteTarget.getObjectId();
        if (StringUtils.isBlank(objectId)) {
            throw new IllegalStateException(
                    "Attempted to link a Note with a PersistableBusinessObject with no object id");
        }
        note.setRemoteObjectIdentifier(noteTarget.getObjectId());
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentService#sendAdHocRequests(org.kuali.rice.krad.document.Document, String, java.util.List)
     */
    @Override
    public Document sendAdHocRequests(Document document, String annotation,
            List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException {
    	// KULRICE-12987 : removing this line to prevent updates to the workflow document on an operation
    	// which should only be adding new notifications.
        //prepareWorkflowDocument(document);
    	
        getWorkflowDocumentService()
                .sendWorkflowNotification(document.getDocumentHeader().getWorkflowDocument(), annotation,
                        adHocRecipients);
        UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(),
                document.getDocumentHeader().getWorkflowDocument());

        removeAdHocPersonsAndWorkgroups(document);
        
        return document;
    }

    private void removeAdHocPersonsAndWorkgroups(Document document) {
    	documentAdHocService.replaceAdHocsForDocument(document.getDocumentNumber(), null);
        document.setAdHocRoutePersons(new ArrayList<AdHocRoutePerson>());
        document.setAdHocRouteWorkgroups(new ArrayList<AdHocRouteWorkgroup>());
    }

	@Required
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    protected DateTimeService getDateTimeService() {
        if (this.dateTimeService == null) {
            this.dateTimeService = CoreApiServiceLocator.getDateTimeService();
        }
        return this.dateTimeService;
    }

	@Required
    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    protected NoteService getNoteService() {
        if (this.noteService == null) {
            this.noteService = KRADServiceLocator.getNoteService();
        }
        return this.noteService;
    }

    public void setLegacyDataAdapter(LegacyDataAdapter legacyDataAdapter) {
        this.legacyDataAdapter = legacyDataAdapter;
    }

    protected LegacyDataAdapter getLegacyDataAdapter() {
        return this.legacyDataAdapter;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    protected WorkflowDocumentService getWorkflowDocumentService() {
        if (this.workflowDocumentService == null) {
            this.workflowDocumentService = KRADServiceLocatorWeb.getWorkflowDocumentService();
        }
        return this.workflowDocumentService;
    }

	@Required
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    protected DataDictionaryService getDataDictionaryService() {
        if (this.dataDictionaryService == null) {
            this.dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }
        return this.dataDictionaryService;
    }

    protected DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

	@Required
    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }

    public PersonService getPersonService() {
        if (personService == null) {
            personService = KimApiServiceLocator.getPersonService();
        }
        return personService;
    }

	@Required
    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

	public DocumentHeaderService getDocumentHeaderService() {
		return documentHeaderService;
	}

	@Required
	public void setDocumentHeaderService(DocumentHeaderService documentHeaderService) {
		this.documentHeaderService = documentHeaderService;
	}

	@Required
	public void setDocumentAdHocService(DocumentAdHocService documentAdHocService) {
		this.documentAdHocService = documentAdHocService;
	}

}
