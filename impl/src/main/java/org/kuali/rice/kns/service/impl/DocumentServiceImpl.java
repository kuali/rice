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

package org.kuali.rice.kns.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.services.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.framework.persistence.jta.TransactionalNoValidationExceptionRollback;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.bo.AdHocRouteWorkgroup;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.datadictionary.exception.UnknownDocumentTypeException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.DocumentPresentationController;
import org.kuali.rice.kns.exception.DocumentAuthorizationException;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.rule.event.ApproveDocumentEvent;
import org.kuali.rice.kns.rule.event.BlanketApproveDocumentEvent;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.rule.event.RouteDocumentEvent;
import org.kuali.rice.kns.rule.event.SaveDocumentEvent;
import org.kuali.rice.kns.rule.event.SaveEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHeaderService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocatorInternal;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.springframework.dao.OptimisticLockingFailureException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * This class is the service implementation for the Document structure. It contains all of the document level type of processing and
 * calling back into documents for various centralization of functionality. This is the default, Kuali delivered implementation
 * which utilizes Workflow.
 */
@TransactionalNoValidationExceptionRollback
public class DocumentServiceImpl implements DocumentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentServiceImpl.class);

    private DateTimeService dateTimeService;

    private NoteService noteService;

    protected WorkflowDocumentService workflowDocumentService;

    protected BusinessObjectService businessObjectService;

    /**
     * Don't access directly, use synchronized getter and setter to access
     */
    private DocumentDao documentDao;

    private DataDictionaryService dataDictionaryService;

    private DocumentHeaderService documentHeaderService;

    private PersonService personService;

    private DocumentHelperService documentHelperService;

    private ConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.rice.kns.service.DocumentService#saveDocument(org.kuali.rice.kns.document.Document)
     */
    @Override
	public Document saveDocument(Document document) throws WorkflowException, ValidationException {
	return saveDocument(document, SaveDocumentEvent.class);
    }

    @Override
	public Document saveDocument(Document document, Class<? extends KualiDocumentEvent> kualiDocumentEventClass) throws WorkflowException, ValidationException {
        checkForNulls(document);
        if (kualiDocumentEventClass == null) {
            throw new IllegalArgumentException("invalid (null) kualiDocumentEventClass");
        }
        // if event is not an instance of a SaveDocumentEvent or a SaveOnlyDocumentEvent
        if (!SaveEvent.class.isAssignableFrom(kualiDocumentEventClass)) {
	    throw new ConfigurationException("The KualiDocumentEvent class '" + kualiDocumentEventClass.getName() + "' does not implement the class '" + SaveEvent.class.getName() + "'");
        }
//        if (!getDocumentActionFlags(document).getCanSave()) {
//            throw buildAuthorizationException("save", document);
//        }
        document.prepareForSave();
        Document savedDocument = validateAndPersistDocumentAndSaveAdHocRoutingRecipients(document, generateKualiDocumentEvent(document, kualiDocumentEventClass));
        prepareWorkflowDocument(savedDocument);
        getWorkflowDocumentService().save(savedDocument.getDocumentHeader().getWorkflowDocument(), null);
        
        KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(),savedDocument.getDocumentHeader().getWorkflowDocument());

        return savedDocument;
    }

    private KualiDocumentEvent generateKualiDocumentEvent(Document document, Class<? extends KualiDocumentEvent> eventClass) throws ConfigurationException {
    	String potentialErrorMessage = "Found error trying to generate Kuali Document Event using event class '" + 
    	eventClass.getName() + "' for document " + document.getDocumentNumber();
    	
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
    			if (ObjectUtils.isNotNull(usableConstructor)) {
    				break;
    			}
    		}
    		if (usableConstructor == null) {
    			throw new RuntimeException("Cannot find a constructor for class '" + eventClass.getName() + "' that takes in a document parameter");
    		}
    		return (KualiDocumentEvent) usableConstructor.newInstance(paramList.toArray());
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
     * @see org.kuali.rice.kns.service.DocumentService#routeDocument(org.kuali.rice.kns.document.Document, java.lang.String, java.util.List)
     */
    @Override
	public Document routeDocument(Document document, String annotation, List<AdHocRouteRecipient> adHocRecipients) throws ValidationException, WorkflowException {
        checkForNulls(document);
        //if (!getDocumentActionFlags(document).getCanRoute()) {
        //    throw buildAuthorizationException("route", document);
        //}
        document.prepareForSave();
        Document savedDocument = validateAndPersistDocument(document, new RouteDocumentEvent(document));
        prepareWorkflowDocument(savedDocument);
        getWorkflowDocumentService().route(savedDocument.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);
        KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(),savedDocument.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(savedDocument);
        return savedDocument;
    }

    /**
     * @see org.kuali.rice.kns.service.DocumentService#approveDocument(org.kuali.rice.kns.document.Document, java.lang.String,
     *      java.util.List)
     */
    @Override
	public Document approveDocument(Document document, String annotation, List<AdHocRouteRecipient> adHocRecipients) throws ValidationException, WorkflowException {
        checkForNulls(document);
        //if (!getDocumentActionFlags(document).getCanApprove()) {
        //    throw buildAuthorizationException("approve", document);
        //}
        document.prepareForSave();
        Document savedDocument = validateAndPersistDocument(document, new ApproveDocumentEvent(document));
        prepareWorkflowDocument(savedDocument);
        getWorkflowDocumentService().approve(savedDocument.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);
        KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(),savedDocument.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(savedDocument);
        return savedDocument;
    }


    /**
     * @see org.kuali.rice.kns.service.DocumentService#superUserApproveDocument(org.kuali.rice.kns.document.Document, java.lang.String)
     */
    @Override
	public Document superUserApproveDocument(Document document, String annotation) throws WorkflowException {
        getDocumentDao().save(document);
        prepareWorkflowDocument(document);
        getWorkflowDocumentService().superUserApprove(document.getDocumentHeader().getWorkflowDocument(), annotation);
        KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(),document.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(document);
        return document;
    }

    /**
     * @see org.kuali.rice.kns.service.DocumentService#superUserCancelDocument(org.kuali.rice.kns.document.Document, java.lang.String)
     */
    @Override
	public Document superUserCancelDocument(Document document, String annotation) throws WorkflowException {
        getDocumentDao().save(document);
        prepareWorkflowDocument(document);
        getWorkflowDocumentService().superUserCancel(document.getDocumentHeader().getWorkflowDocument(), annotation);
        KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(),document.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(document);
        return document;
    }

    /**
     * @see org.kuali.rice.kns.service.DocumentService#superUserCancelDocument(org.kuali.rice.kns.document.Document, java.lang.String)
     */
    @Override
	public Document superUserDisapproveDocument(Document document, String annotation) throws WorkflowException {
        getDocumentDao().save(document);
        prepareWorkflowDocument(document);
        getWorkflowDocumentService().superUserDisapprove(document.getDocumentHeader().getWorkflowDocument(), annotation);
        KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(),document.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(document);
        return document;
    }

    /**
     * @see org.kuali.rice.kns.service.DocumentService#disapproveDocument(org.kuali.rice.kns.document.Document, java.lang.String)
     */
    @Override
	public Document disapproveDocument(Document document, String annotation) throws Exception {
        checkForNulls(document);

        Note note = createNoteFromDocument(document,annotation);
        document.addNote(note);

        //SAVE THE NOTE
        //Note: This save logic is replicated here and in KualiDocumentAction, when to save (based on doc state) should be moved
        //      into a doc service method
        getNoteService().save(note);

        prepareWorkflowDocument(document);
        getWorkflowDocumentService().disapprove(document.getDocumentHeader().getWorkflowDocument(), annotation);
        KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(),document.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(document);
        return document;
    }

    /**
     * @see org.kuali.rice.kns.service.DocumentService#cancelDocument(org.kuali.rice.kns.document.Document, java.lang.String)
     */
    @Override
	public Document cancelDocument(Document document, String annotation) throws WorkflowException {
        checkForNulls(document);
        //if (!getDocumentActionFlags(document).getCanCancel()) {
        //    throw buildAuthorizationException("cancel", document);
        //}
        if (document instanceof MaintenanceDocument) {
        	MaintenanceDocument maintDoc = ((MaintenanceDocument) document);
        	if (maintDoc.getOldMaintainableObject() != null) {
        		maintDoc.getOldMaintainableObject().getBusinessObject().refresh();
        	}
       		maintDoc.getNewMaintainableObject().getBusinessObject().refresh();
        }
        prepareWorkflowDocument(document);
        getWorkflowDocumentService().cancel(document.getDocumentHeader().getWorkflowDocument(), annotation);
        KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(),document.getDocumentHeader().getWorkflowDocument());
        //getBusinessObjectService().delete(document.getAdHocRoutePersons());
        //getBusinessObjectService().delete(document.getAdHocRouteWorkgroups());
        removeAdHocPersonsAndWorkgroups(document);
        return document;
    }

    /**
     * @see org.kuali.rice.kns.service.DocumentService#acknowledgeDocument(org.kuali.rice.kns.document.Document, java.lang.String,
     *      java.util.List)
     */
    @Override
	public Document acknowledgeDocument(Document document, String annotation, List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException {
        checkForNulls(document);
        //if (!getDocumentActionFlags(document).getCanAcknowledge()) {
        //    throw buildAuthorizationException("acknowledge", document);
        //}
        prepareWorkflowDocument(document);
        getWorkflowDocumentService().acknowledge(document.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);
        KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(),document.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(document);
        return document;
    }

    /**
     * @see org.kuali.rice.kns.service.DocumentService#blanketApproveDocument(org.kuali.rice.kns.document.Document, java.lang.String,
     *      java.util.List)
     */
    @Override
	public Document blanketApproveDocument(Document document, String annotation, List<AdHocRouteRecipient> adHocRecipients) throws ValidationException, WorkflowException {
        checkForNulls(document);
        //if (!getDocumentActionFlags(document).getCanBlanketApprove()) {
        //    throw buildAuthorizationException("blanket approve", document);
        //}
        document.prepareForSave();
        Document savedDocument = validateAndPersistDocument(document, new BlanketApproveDocumentEvent(document));
        prepareWorkflowDocument(savedDocument);
        getWorkflowDocumentService().blanketApprove(savedDocument.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);
        KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(),savedDocument.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(savedDocument);
        return savedDocument;
    }

    /**
     * @see org.kuali.rice.kns.service.DocumentService#clearDocumentFyi(org.kuali.rice.kns.document.Document, java.util.List)
     */
    @Override
	public Document clearDocumentFyi(Document document, List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException {
        checkForNulls(document);
        // populate document content so searchable attributes will be indexed properly
        document.populateDocumentForRouting();
        getWorkflowDocumentService().clearFyi(document.getDocumentHeader().getWorkflowDocument(), adHocRecipients);
        KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(),document.getDocumentHeader().getWorkflowDocument());
        removeAdHocPersonsAndWorkgroups(document);
        return document;
    }

    protected void checkForNulls(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        if (document.getDocumentNumber() == null) {
            throw new IllegalStateException("invalid (null) documentHeaderId");
        }
    }

    private Document validateAndPersistDocumentAndSaveAdHocRoutingRecipients(Document document, KualiDocumentEvent event) {
        /*
         * Using this method to wrap validateAndPersistDocument to keep everything in one transaction. This avoids modifying the
         * signature on validateAndPersistDocument method
         */
        List<AdHocRouteRecipient> adHocRoutingRecipients = new ArrayList<AdHocRouteRecipient>();
        adHocRoutingRecipients.addAll(document.getAdHocRoutePersons());
        adHocRoutingRecipients.addAll(document.getAdHocRouteWorkgroups());

        for (AdHocRouteRecipient recipient : adHocRoutingRecipients) {
			recipient.setdocumentNumber(document.getDocumentNumber());
		}
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("documentNumber", document.getDocumentNumber());
        getBusinessObjectService().deleteMatching(AdHocRouteRecipient.class, criteria);

        getBusinessObjectService().save(adHocRoutingRecipients);
        return validateAndPersistDocument(document, event);
    }

    /**
     * @see org.kuali.rice.kns.service.DocumentService#documentExists(java.lang.String)
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
	        	GlobalVariables.setUserSession(new UserSession(KNSConstants.SYSTEM_USER));
	        	GlobalVariables.clear();
	        }

	        // look for workflowDocumentHeader, since that supposedly won't break the transaction
	        if (getWorkflowDocumentService().workflowDocumentExists(documentHeaderId)) {
	            // look for docHeaderId, since that fails without breaking the transaction
	            return getDocumentHeaderService().getDocumentHeaderById(documentHeaderId) != null;
	        }

	        return false;
    	} finally {
    		// if a user session was established for this call, clear it our
    		if ( internalUserSession ) {
    			GlobalVariables.clear();
    			GlobalVariables.setUserSession(null);
    		}
    	}
    }

    /**
     * Creates a new document by class.
     *
     * @see org.kuali.rice.kns.service.DocumentService#getNewDocument(java.lang.Class)
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
            throw new UnknownDocumentTypeException("unable to get documentTypeName for unknown documentClass '" + documentClass.getName() + "'");
        }
        return getNewDocument(documentTypeName);
    }


    /**
     * Creates a new document by document type name.
     *
     * @see org.kuali.rice.kns.service.DocumentService#getNewDocument(java.lang.String)
     */
    @Override
	public Document getNewDocument(String documentTypeName) throws WorkflowException {

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
            throw new IllegalStateException("GlobalVariables must be populated with a valid UserSession before a new document can be created");
        }

        // get the class for this docTypeName
        Class<? extends Document> documentClass = getDocumentClassByTypeName(documentTypeName);

        // get the current user
        Person currentUser = GlobalVariables.getUserSession().getPerson();

        // get the authorization
        DocumentAuthorizer documentAuthorizer = getDocumentHelperService().getDocumentAuthorizer(documentTypeName);
        DocumentPresentationController documentPresentationController = getDocumentHelperService().getDocumentPresentationController(documentTypeName);
        // make sure this person is authorized to initiate
        LOG.debug("calling canInitiate from getNewDocument()");
        if (!documentPresentationController.canInitiate(documentTypeName) || !documentAuthorizer.canInitiate(documentTypeName, currentUser)) {
        	throw new DocumentAuthorizationException(currentUser.getPrincipalName(), "initiate", documentTypeName);
        }

        // initiate new workflow entry, get the workflow doc
        KualiWorkflowDocument workflowDocument = getWorkflowDocumentService().createWorkflowDocument(documentTypeName, GlobalVariables.getUserSession().getPerson());
        KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(),workflowDocument);

        // create a new document header object
        DocumentHeader documentHeader = null;
        try {
            // create a new document header object
            Class<? extends DocumentHeader> documentHeaderClass = getDocumentHeaderService().getDocumentHeaderBaseClass();
            documentHeader = documentHeaderClass.newInstance();
            documentHeader.setWorkflowDocument(workflowDocument);
            documentHeader.setDocumentNumber(workflowDocument.getRouteHeaderId().toString());
            // status and notes are initialized correctly in the constructor
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Error instantiating DocumentHeader", e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Error instantiating DocumentHeader", e);
        }

        // build Document of specified type
        Document document = null;
        try {
            // all maintenance documents have same class
            if (MaintenanceDocumentBase.class.isAssignableFrom(documentClass)) {
                Class<?>[] defaultConstructor = new Class[]{String.class};
                Constructor<? extends Document> cons = documentClass.getConstructor(defaultConstructor);
                if (ObjectUtils.isNull(cons)) {
                    throw new ConfigurationException("Could not find constructor with document type name parameter needed for Maintenance Document Base class");
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
            throw new RuntimeException("Error instantiating Maintenance Document: No constructor with String parameter found", e);
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
     * This is temporary until workflow 2.0 and reads from a table to get documents whose status has changed to A (approved - no
     * outstanding approval actions requested)
     *
     * @param documentHeaderId
     * @throws WorkflowException
     * @return Document
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
	        	GlobalVariables.setUserSession(new UserSession(KNSConstants.SYSTEM_USER));
	        	GlobalVariables.clear();
	        }

	        KualiWorkflowDocument workflowDocument = null;

	        if ( LOG.isDebugEnabled() ) {
	        	LOG.debug("Retrieving doc id: " + documentHeaderId + " from workflow service.");
	        }
	        workflowDocument = getWorkflowDocumentService().createWorkflowDocument(Long.valueOf(documentHeaderId), GlobalVariables.getUserSession().getPerson());
	        KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(),workflowDocument);

	        Class<? extends Document> documentClass = getDocumentClassByTypeName(workflowDocument.getDocumentType());

	        // retrieve the Document
	        Document document = getDocumentDao().findByDocumentHeaderId(documentClass, documentHeaderId);
	        
	        return postProcessDocument(documentHeaderId, workflowDocument, document);
    	} finally {
    		// if a user session was established for this call, clear it out
    		if ( internalUserSession ) {
    			GlobalVariables.clear();
    			GlobalVariables.setUserSession(null);
    		}
    	}
    }

	/**
	 * @see org.kuali.rice.kns.service.DocumentService#getByDocumentHeaderIdSessionless(java.lang.String)
	 */
	@Override
	public Document getByDocumentHeaderIdSessionless(String documentHeaderId)
			throws WorkflowException {
        if (documentHeaderId == null) {
            throw new IllegalArgumentException("invalid (null) documentHeaderId");
        }

        KualiWorkflowDocument workflowDocument = null;

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("Retrieving doc id: " + documentHeaderId + " from workflow service.");
        }

        Person person = getPersonService().getPersonByPrincipalName(KNSConstants.SYSTEM_USER);
        workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(documentHeaderId), person);

        Class<? extends Document> documentClass = getDocumentClassByTypeName(workflowDocument.getDocumentType());

        // retrieve the Document
        Document document = getDocumentDao().findByDocumentHeaderId(documentClass, documentHeaderId);
        
        return postProcessDocument(documentHeaderId, workflowDocument, document);
	}

    private Class<? extends Document> getDocumentClassByTypeName(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }

        Class<? extends Document> clazz = getDataDictionaryService().getDocumentClassByTypeName(documentTypeName);
        if (clazz == null) {
            throw new UnknownDocumentTypeException("unable to get class for unknown documentTypeName '" + documentTypeName + "'");
        }
        return clazz;
    }
    
    /**
     * Loads the Notes for the note target on this Document.
     * 
     * @param document the document for which to load the notes
     */
    protected void loadNotes(Document document) {
    	if (isNoteTargetReady(document)) {
    		List<Note> notes = getNoteService().getByRemoteObjectId(document.getNoteTarget().getObjectId());
    		// KULRNE-5692 - force a refresh of the attachments
            // they are not (non-updateable) references and don't seem to update properly upon load
    		for (Note note : notes) {
    			note.refreshReferenceObject("attachment");
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
    private Document postProcessDocument(String documentHeaderId, KualiWorkflowDocument workflowDocument, Document document) {
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
     * @see org.kuali.rice.kns.service.DocumentService#getDocumentsByListOfDocumentHeaderIds(java.lang.Class, java.util.List)
     */
    @Override
	public List<Document> getDocumentsByListOfDocumentHeaderIds(Class<? extends Document> documentClass, List<String> documentHeaderIds) throws WorkflowException {
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
	        	GlobalVariables.setUserSession(new UserSession(KNSConstants.SYSTEM_USER));
	        	GlobalVariables.clear();
	        }

	        // retrieve all documents that match the document header ids
	        List<? extends Document> rawDocuments = getDocumentDao().findByDocumentHeaderIds(documentClass, documentHeaderIds);

	        // post-process them
	        List<Document> documents = new ArrayList<Document>();
	        for (Document document : rawDocuments) {
	            KualiWorkflowDocument workflowDocument = getWorkflowDocumentService().createWorkflowDocument(Long.valueOf(document.getDocumentNumber()), GlobalVariables.getUserSession().getPerson());

	            document = postProcessDocument(document.getDocumentNumber(), workflowDocument, document);
	            documents.add(document);
	        }
	        return documents;
    	} finally {
    		// if a user session was established for this call, clear it our
    		if ( internalUserSession ) {
    			GlobalVariables.clear();
    			GlobalVariables.setUserSession(null);
    		}
    	}
    }

    /* Helper Methods */

    /**
     * Validates and persists a document.
     *
     * @see org.kuali.rice.kns.service.DocumentService#validateAndPersistDocument(org.kuali.rice.kns.document.Document, java.lang.String)
     */
    public Document validateAndPersistDocument(Document document, KualiDocumentEvent event) throws ValidationException {
        if (document == null) {
            LOG.error("document passed to validateAndPersist was null");
            throw new IllegalArgumentException("invalid (null) document");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("validating and preparing to persist document " + document.getDocumentNumber());
        }

        document.validateBusinessRules(event);
        document.prepareForSave(event);

        // save the document
        Document savedDocument = null;
        try {
        	if ( LOG.isInfoEnabled() ) {
        		LOG.info("storing document " + document.getDocumentNumber());
        	}
            savedDocument = getDocumentDao().save(document);
        }
        catch (OptimisticLockingFailureException e) {
            LOG.error("exception encountered on store of document " + e.getMessage());
            throw e;
        }
        
        boolean notesSaved = saveDocumentNotes(document);
        if (!notesSaved) {
        	if (LOG.isInfoEnabled()) {
        		LOG.info("Notes not saved during validateAndPersistDocument, likely means that note save needs to be deferred because note target is not ready.");
        	}
        }

        savedDocument.postProcessSave(event);

        return savedDocument;
    }


    /**
     * Sets the title and app document id in the flex document
     *
     * @param document
     * @throws WorkflowException
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
     * This method will grab the generated document title from the document and add it to the workflowDocument so that it gets pushed into
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
     * This method will grab the organization document number from the document and add it to the workflowDocument so that it gets pushed
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
    @Override
	public Document updateDocument(Document document) {
        checkForNulls(document);
        return getDocumentDao().save(document);
    }

    /**
     *
     * @see org.kuali.rice.kns.service.DocumentService#createNoteFromDocument(org.kuali.rice.kns.document.Document, java.lang.String)
     */
    @Override
	public Note createNoteFromDocument(Document document, String text) {
        Note note = new Note();

        note.setNotePostedTimestamp(getDateTimeService().getCurrentTimestamp());
        note.setVersionNumber(Long.valueOf(1));
        note.setNoteText(text);
        note.setNoteTypeCode(document.getNoteType().getCode());

        PersistableBusinessObject bo = document.getNoteTarget();
        // TODO gah! this is awful
        Person kualiUser = GlobalVariables.getUserSession().getPerson();
        if (kualiUser == null) {
        	throw new IllegalStateException("Current UserSession has a null Person.");
        }
        return bo == null ? null : getNoteService().createNote(note, bo, kualiUser.getPrincipalId());
    }
    
    
	/**
	 * @see org.kuali.rice.kns.service.DocumentService#saveDocumentNotes(org.kuali.rice.kns.document.Document)
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
	 * Determines if the given document's note target is ready for notes to be
	 * attached and persisted against it.  This method verifies that the document's
	 * note target is non-null as well as checking that it has a non-empty object id. 
	 *
	 * @param the document on which to check for note target readiness
	 * @return true if the note target is ready, false otherwise
	 */
	protected boolean isNoteTargetReady(Document document) {
		PersistableBusinessObject noteTarget = document.getNoteTarget();
		if (noteTarget == null || StringUtils.isBlank(noteTarget.getObjectId())) {
			return false;
		}
		return true;
	}
	
	private void linkNoteRemoteObjectId(Note note, PersistableBusinessObject noteTarget) {
    	String objectId = noteTarget.getObjectId();
    	if (StringUtils.isBlank(objectId)) {
    		throw new IllegalStateException("Attempted to link a Note with a PersistableBusinessObject with no object id");
    	}
    	note.setRemoteObjectIdentifier(noteTarget.getObjectId());
    }

    /**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kns.service.DocumentService#sendAdHocRequests(org.kuali.rice.kns.document.Document, java.util.List)
	 */
	@Override
	public void sendAdHocRequests(Document document, String annotation, List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException{
		prepareWorkflowDocument(document);
		getWorkflowDocumentService().sendWorkflowNotification(document.getDocumentHeader().getWorkflowDocument(),
        		annotation, adHocRecipients);
		KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(),document.getDocumentHeader().getWorkflowDocument());
		//getBusinessObjectService().delete(document.getAdHocRoutePersons());
		//getBusinessObjectService().delete(document.getAdHocRouteWorkgroups());
		removeAdHocPersonsAndWorkgroups(document);
	}

	/**
     * spring injected date time service
     *
     * @param dateTimeService
     */
    public synchronized void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the DateTimeService, lazily initializing if necessary
     * @return the DateTimeService
     */
    private synchronized DateTimeService getDateTimeService() {
        if (this.dateTimeService == null) {
            this.dateTimeService = CoreApiServiceLocator.getDateTimeService();
        }
        return this.dateTimeService;
    }

    /**
     * Sets the noteService attribute value.
     * @param noteService The noteService to set.
     */
    public synchronized void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Gets the NoteService, lazily initializing if necessary
     * @return the NoteService
     */
    protected synchronized NoteService getNoteService() {
        if (this.noteService == null) {
            this.noteService = KNSServiceLocator.getNoteService();
        }
        return this.noteService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public synchronized void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the {@link BusinessObjectService}, lazily initializing if necessary
     * @return the {@link BusinessObjectService}
     */
    protected synchronized BusinessObjectService getBusinessObjectService() {
        if (this.businessObjectService == null) {
            this.businessObjectService = KNSServiceLocator.getBusinessObjectService();
        }
        return this.businessObjectService;
    }

    /**
     * Sets the workflowDocumentService attribute value.
     *
     * @param workflowDocumentService The workflowDocumentService to set.
     */
    public synchronized void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    /**
     * Gets the {@link WorkflowDocumentService}, lazily initializing if necessary
     * @return the {@link WorkflowDocumentService}
     */
    protected synchronized WorkflowDocumentService getWorkflowDocumentService() {
        if (this.workflowDocumentService == null) {
            this.workflowDocumentService = KNSServiceLocatorWeb.getWorkflowDocumentService();
        }
        return this.workflowDocumentService;
    }

    /**
     * Sets the documentDao attribute value.
     *
     * @param documentDao The documentDao to set.
     */
    public synchronized void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    /**
     * Gets the {@link DocumentDao}, lazily initializing if necessary
     * @return the {@link DocumentDao}
     */
    protected synchronized DocumentDao getDocumentDao() {
        if (this.documentDao == null) {
            this.documentDao = KNSServiceLocatorInternal.getDocumentDao();
        }
        return documentDao;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     *
     * @param dataDictionaryService
     */
    public synchronized void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the {@link DataDictionaryService}, lazily initializing if necessary
     * @return the {@link DataDictionaryService}
     */
    protected synchronized DataDictionaryService getDataDictionaryService() {
        if (this.dataDictionaryService == null) {
            this.dataDictionaryService = KNSServiceLocatorWeb.getDataDictionaryService();
        }
        return this.dataDictionaryService;
    }

    /**
     * @param documentHeaderService the documentHeaderService to set
     */
    public synchronized void setDocumentHeaderService(DocumentHeaderService documentHeaderService) {
        this.documentHeaderService = documentHeaderService;
    }

    /**
     * Gets the {@link DocumentHeaderService}, lazily initializing if necessary
     * @return the {@link DocumentHeaderService}
     */
    protected synchronized DocumentHeaderService getDocumentHeaderService() {
        if (this.documentHeaderService == null) {
            this.documentHeaderService = KNSServiceLocatorWeb.getDocumentHeaderService();
        }
        return this.documentHeaderService;
    }

    /**
	 * @param personService the personService to set
	 */
	public PersonService getPersonService() {
		if (personService == null) {
			personService = KIMServiceLocator.getPersonService();
		}
		return personService;
	}

    /**
     * @return the documentHelperService
     */
    public DocumentHelperService getDocumentHelperService() {
        if (documentHelperService == null) {
            this.documentHelperService = KNSServiceLocatorWeb.getDocumentHelperService();
        }
        return this.documentHelperService;
    }

    /**
     * @param documentHelperService the documentHelperService to set
     */
    public void setDocumentHelperService(DocumentHelperService documentHelperService) {
        this.documentHelperService = documentHelperService;
    }
    
    private void removeAdHocPersonsAndWorkgroups(Document document){
    	List<AdHocRoutePerson> adHocRoutePersons = new ArrayList<AdHocRoutePerson>();
    	List<AdHocRouteWorkgroup> adHocRouteWorkgroups = new ArrayList<AdHocRouteWorkgroup>();
    	getBusinessObjectService().delete(document.getAdHocRoutePersons());
    	getBusinessObjectService().delete(document.getAdHocRouteWorkgroups());
    	document.setAdHocRoutePersons(adHocRoutePersons);
    	document.setAdHocRouteWorkgroups(adHocRouteWorkgroups);
    }

    /**
     * @see org.kuali.rice.kns.service.DocumentService
     */
    @Override
    public void sendNoteRouteNotification(Document document, Note note, Person sender) throws WorkflowException {
        AdHocRouteRecipient routeRecipient = note.getAdHocRouteRecipient();

        // build notification request
        Person requestedUser = this.getPersonService().getPersonByPrincipalName(routeRecipient.getId());
        String senderName = sender.getFirstName() + " " + sender.getLastName();
        String requestedName = requestedUser.getFirstName() + " " + requestedUser.getLastName();

        String notificationText = kualiConfigurationService.getPropertyString(RiceKeyConstants.MESSAGE_NOTE_NOTIFICATION_ANNOTATION);
        if (StringUtils.isBlank(notificationText)) {
            throw new RuntimeException("No annotation message found for note notification. Message needs added to application resources with key:" + RiceKeyConstants.MESSAGE_NOTE_NOTIFICATION_ANNOTATION);
        }
        notificationText = MessageFormat.format(notificationText, new Object[]{senderName, requestedName, note.getNoteText()});

        List<AdHocRouteRecipient> routeRecipients = new ArrayList<AdHocRouteRecipient>();
        routeRecipients.add(routeRecipient);

        workflowDocumentService.sendWorkflowNotification(document.getDocumentHeader().getWorkflowDocument(), notificationText, routeRecipients, KNSConstants.NOTE_WORKFLOW_NOTIFICATION_REQUEST_LABEL);

        // clear recipient allowing an notification to be sent to another person
        note.setAdHocRouteRecipient(new AdHocRoutePerson());
    }

    /**
     * @param kualiConfigurationService the kualiConfigurationService to set
     */
    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
