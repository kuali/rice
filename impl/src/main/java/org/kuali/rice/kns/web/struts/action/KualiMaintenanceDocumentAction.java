
/*
 * Copyright 2005-2007 The Kuali Foundation
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.jpa.metadata.EntityDescriptor;
import org.kuali.rice.core.jpa.metadata.FieldDescriptor;
import org.kuali.rice.core.jpa.metadata.MetadataManager;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.DocumentAttachment;
import org.kuali.rice.kns.bo.PersistableAttachment;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObjectExtension;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.rice.kns.exception.DocumentTypeAuthorizationException;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.rule.event.KualiAddLineEvent;
import org.kuali.rice.kns.service.*;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.MaintenanceUtils;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.struts.form.KualiMaintenanceForm;

/**
 * This class handles actions for maintenance documents. These include creating new edit, and copying of maintenance records.
 */
public class KualiMaintenanceDocumentAction extends KualiDocumentActionBase {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiMaintenanceDocumentAction.class);

    protected MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService = null;
    protected EncryptionService encryptionService;
    protected LookupService lookupService;
    protected LookupResultsService lookupResultsService;

	public KualiMaintenanceDocumentAction() {
		super();
		maintenanceDocumentDictionaryService = KNSServiceLocatorWeb.getMaintenanceDocumentDictionaryService();
		encryptionService = KNSServiceLocator.getEncryptionService();
	}

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {	
		request.setAttribute(KNSConstants.PARAM_MAINTENANCE_VIEW_MODE, KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_MAINTENANCE);
		return super.execute(mapping, form, request, response);
	}

	/**
	 * Calls setup Maintenance for new action.
	 */
	public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(KNSConstants.MAINTENANCE_ACTN, KNSConstants.MAINTENANCE_NEW_ACTION);
		return setupMaintenance(mapping, form, request, response, KNSConstants.MAINTENANCE_NEW_ACTION);
	}

	/**
	 * Calls setupMaintenance for copy action.
	 */
	public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// check for copy document number
		if (request.getParameter("document." + KNSPropertyConstants.DOCUMENT_NUMBER) == null) { // object copy
			return setupMaintenance(mapping, form, request, response, KNSConstants.MAINTENANCE_COPY_ACTION);
		}
		else { // document copy
			throw new UnsupportedOperationException("System does not support copying of maintenance documents.");
		}
	}

	/**
	 * Calls setupMaintenance for edit action.
	 */
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		return setupMaintenance(mapping, form, request, response, KNSConstants.MAINTENANCE_EDIT_ACTION);
	}

	/**
	 * KUALRice 3070 Calls setupMaintenance for delete action.
	 */
	public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (isFormRepresentingLockObject((KualiDocumentFormBase)form)) {
			 return super.delete(mapping, form, request, response);
		}
		GlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_DELETE);
		return setupMaintenance(mapping, form, request, response, KNSConstants.MAINTENANCE_DELETE_ACTION);
	}
	
	/**
	 * Calls setupMaintenance for new object that have existing objects attributes.
	 */
	public ActionForward newWithExisting(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return setupMaintenance(mapping, form, request, response, KNSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);
	}

	/**
	 * Gets a new document for a maintenance record. The maintainable is specified with the documentTypeName or business object
	 * class request parameter and request parameters are parsed for key values for retrieving the business object. Forward to the
	 * maintenance jsp which renders the page based on the maintainable's field specifications. Retrieves an existing business
	 * object for edit and copy. Checks locking on edit.
	 */
    protected ActionForward setupMaintenance(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String maintenanceAction) throws Exception {
		KualiMaintenanceForm maintenanceForm = (KualiMaintenanceForm) form;
		MaintenanceDocument document = null;

		// create a new document object, if required (on NEW object, or other reasons)
		if (maintenanceForm.getDocument() == null) {
			if (StringUtils.isEmpty(maintenanceForm.getBusinessObjectClassName()) && StringUtils.isEmpty(maintenanceForm.getDocTypeName())) {
				throw new IllegalArgumentException("Document type name or bo class not given!");
			}

			String documentTypeName = maintenanceForm.getDocTypeName();
			// get document type if not passed
			if (StringUtils.isEmpty(documentTypeName)) {
				documentTypeName = maintenanceDocumentDictionaryService.getDocumentTypeName(Class.forName(maintenanceForm.getBusinessObjectClassName()));
				maintenanceForm.setDocTypeName(documentTypeName);
			}

			if (StringUtils.isEmpty(documentTypeName)) {
				throw new RuntimeException("documentTypeName is empty; does this Business Object have a maintenance document definition? " + maintenanceForm.getBusinessObjectClassName());
			}

			// check doc type allows new or copy if that action was requested
			if (KNSConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction) || KNSConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
				Class boClass = maintenanceDocumentDictionaryService.getBusinessObjectClass(documentTypeName);
				boolean allowsNewOrCopy = getBusinessObjectAuthorizationService().canCreate(boClass, GlobalVariables.getUserSession().getPerson(), documentTypeName);
				if (!allowsNewOrCopy) {
					LOG.error("Document type " + documentTypeName + " does not allow new or copy actions.");
					throw new DocumentTypeAuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalId(), "newOrCopy", documentTypeName);
				}
			}

			// get new document from service
			document = (MaintenanceDocument) getDocumentService().getNewDocument(maintenanceForm.getDocTypeName());
			// Check for an auto-incrementing PK and set it if needed
			//            if (document.getNewMaintainableObject().getBoClass().isAnnotationPresent(Sequence.class)) {
			//    			Sequence sequence = (Sequence) document.getNewMaintainableObject().getBoClass().getAnnotation(Sequence.class);
			//    			Long pk = OrmUtils.getNextAutoIncValue(sequence);
			//    			OrmUtils.populateAutoIncValue(document.getOldMaintainableObject().getBusinessObject(), pk);
			//    			OrmUtils.populateAutoIncValue(document.getNewMaintainableObject().getBusinessObject(), pk);
			//    			document.getOldMaintainableObject().getBusinessObject().setAutoIncrementSet(true);
			//    			document.getNewMaintainableObject().getBusinessObject().setAutoIncrementSet(true);
			//            }
			maintenanceForm.setDocument(document);
		}
		else {
			document = (MaintenanceDocument) maintenanceForm.getDocument();
		}

		// retrieve business object from request parameters
		if (!(KNSConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)) && !(KNSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION.equals(maintenanceAction))) {
			Map requestParameters = buildKeyMapFromRequest(document.getNewMaintainableObject(), request);
            PersistableBusinessObject oldBusinessObject = null;
            try {
            	oldBusinessObject = (PersistableBusinessObject) getLookupService().findObjectBySearch(Class.forName(maintenanceForm.getBusinessObjectClassName()), requestParameters);
            } catch ( ClassNotPersistenceCapableException ex ) {
            	if ( !document.getOldMaintainableObject().isExternalBusinessObject() ) {
            		throw new RuntimeException( "BO Class: " + maintenanceForm.getBusinessObjectClassName() + " is not persistable and is not externalizable - configuration error" );
            	}
            	// otherwise, let fall through
            }
			if (oldBusinessObject == null && !document.getOldMaintainableObject().isExternalBusinessObject()) {
                throw new RuntimeException("Cannot retrieve old record for maintenance document, incorrect parameters passed on maint url: " + requestParameters );
			} 

			if(document.getOldMaintainableObject().isExternalBusinessObject()){
            	if ( oldBusinessObject == null ) {
            		try {
            			oldBusinessObject = (PersistableBusinessObject)document.getOldMaintainableObject().getBoClass().newInstance();
            		} catch ( Exception ex ) {
            			throw new RuntimeException( "External BO maintainable was null and unable to instantiate for old maintainable object.", ex );
            		}
            	}
				populateBOWithCopyKeyValues(request, oldBusinessObject, document.getOldMaintainableObject());
				document.getOldMaintainableObject().prepareBusinessObject(oldBusinessObject);
            	oldBusinessObject = document.getOldMaintainableObject().getBusinessObject();
			}

			// Temp solution for loading extension objects - need to find a better way
			final String TMP_NM = oldBusinessObject.getClass().getName();
			final int START_INDEX = TMP_NM.indexOf('.', TMP_NM.indexOf('.') + 1) + 1;
			if ( ( OrmUtils.isJpaEnabled() || OrmUtils.isJpaEnabled(TMP_NM.substring(START_INDEX, TMP_NM.indexOf('.', TMP_NM.indexOf('.', START_INDEX) + 1))) ) &&
					OrmUtils.isJpaAnnotated(oldBusinessObject.getClass()) && oldBusinessObject.getExtension() != null && OrmUtils.isJpaAnnotated(oldBusinessObject.getExtension().getClass())) {
				if (oldBusinessObject.getExtension() != null) {
					PersistableBusinessObjectExtension boe = oldBusinessObject.getExtension();
					EntityDescriptor entity = MetadataManager.getEntityDescriptor(oldBusinessObject.getExtension().getClass());
					org.kuali.rice.core.jpa.criteria.Criteria extensionCriteria = new org.kuali.rice.core.jpa.criteria.Criteria(boe.getClass().getName());
					for (FieldDescriptor fieldDescriptor : entity.getPrimaryKeys()) {
						try {
							Field field = oldBusinessObject.getClass().getDeclaredField(fieldDescriptor.getName());
							field.setAccessible(true);
							extensionCriteria.eq(fieldDescriptor.getName(), field.get(oldBusinessObject));
						} catch (Exception e) {
							LOG.error(e.getMessage(),e);
						}
					}				
					try {
						boe = (PersistableBusinessObjectExtension) new org.kuali.rice.core.jpa.criteria.QueryByCriteria(getEntityManagerFactory().createEntityManager(), extensionCriteria).toQuery().getSingleResult();
					} catch (PersistenceException e) {}
					oldBusinessObject.setExtension(boe);
				}
			}

			PersistableBusinessObject newBusinessObject = (PersistableBusinessObject) ObjectUtils.deepCopy(oldBusinessObject);

			// set business object instance for editing
			Class<? extends PersistableBusinessObject> businessObjectClass = ClassLoaderUtils.getClass(maintenanceForm.getBusinessObjectClassName(), PersistableBusinessObject.class); 
			document.getOldMaintainableObject().setBusinessObject(oldBusinessObject);
			document.getOldMaintainableObject().setBoClass(businessObjectClass);
			document.getNewMaintainableObject().setBusinessObject(newBusinessObject);
			document.getNewMaintainableObject().setBoClass(businessObjectClass);

			// on a COPY, clear any fields that this user isnt authorized for, and also
			// clear the primary key fields
			if (KNSConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
				if (!document.isFieldsClearedOnCopy()) {
					//for issue KULRice 3072
					Class boClass = maintenanceDocumentDictionaryService.getBusinessObjectClass(maintenanceForm.getDocTypeName());
					if(!maintenanceDocumentDictionaryService.getPreserveLockingKeysOnCopy(boClass))
						clearPrimaryKeyFields(document);

					clearUnauthorizedNewFields(document);

					Maintainable maintainable = document.getNewMaintainableObject();

					maintainable.processAfterCopy( document, request.getParameterMap() );

					// mark so that this clearing doesnt happen again
					document.setFieldsClearedOnCopy(true);

					// mark so that blank required fields will be populated with default values
					maintainable.setGenerateBlankRequiredValues(maintenanceForm.getDocTypeName());
				}
			}
			else if (KNSConstants.MAINTENANCE_EDIT_ACTION.equals(maintenanceAction)) {						
				boolean allowsEdit = getBusinessObjectAuthorizationService().canMaintain(oldBusinessObject, GlobalVariables.getUserSession().getPerson(), document.getDocumentHeader().getWorkflowDocument().getDocumentType());  
				if (!allowsEdit) {
					LOG.error("Document type " + document.getDocumentHeader().getWorkflowDocument().getDocumentType() + " does not allow edit actions.");
					throw  new DocumentTypeAuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalId(), "edit", document.getDocumentHeader().getWorkflowDocument().getDocumentType());
				}
				document.getNewMaintainableObject().processAfterEdit( document, request.getParameterMap() );
			}
			//3070
			else if (KNSConstants.MAINTENANCE_DELETE_ACTION.equals(maintenanceAction)) {			
				boolean allowsDelete = getBusinessObjectAuthorizationService().canMaintain(oldBusinessObject, GlobalVariables.getUserSession().getPerson(), document.getDocumentHeader().getWorkflowDocument().getDocumentType());  
				if (!allowsDelete) {
					LOG.error("Document type " + document.getDocumentHeader().getWorkflowDocument().getDocumentType() + " does not allow delete actions.");
					throw  new DocumentTypeAuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalId(), "delete", document.getDocumentHeader().getWorkflowDocument().getDocumentType());
				}	
				//document.getNewMaintainableObject().processAfterEdit( document, request.getParameterMap() );
			}
			// Check for an auto-incrementing PK and set it if needed
			//            if (document.getNewMaintainableObject().getBoClass().isAnnotationPresent(Sequence.class)) {
			//    			Sequence sequence = (Sequence) document.getNewMaintainableObject().getBoClass().getAnnotation(Sequence.class);
			//    			Long pk = OrmUtils.getNextAutoIncValue(sequence);
			//    			OrmUtils.populateAutoIncValue(document.getNewMaintainableObject().getBusinessObject(), pk);
			//    			document.getNewMaintainableObject().getBusinessObject().setAutoIncrementSet(true);
			//            }
		}
		// if new with existing we need to populate we need to populate with passed in parameters
		if (KNSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION.equals(maintenanceAction)) {
			// TODO: this code should be abstracted out into a helper
			// also is it a problem that we're not calling setGenerateDefaultValues? it blanked out the below values when I did
			// maybe we need a new generateDefaultValues that doesn't overwrite?
			PersistableBusinessObject newBO = document.getNewMaintainableObject().getBusinessObject();
			Map<String, String> parameters = buildKeyMapFromRequest(document.getNewMaintainableObject(), request);
			copyParametersToBO(parameters, newBO);
			newBO.refresh();
			document.getNewMaintainableObject().setupNewFromExisting( document, request.getParameterMap() );
		}

		// for new maintainble need to pick up default values
		if (KNSConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)) {
			document.getNewMaintainableObject().setGenerateDefaultValues(maintenanceForm.getDocTypeName());
			document.getNewMaintainableObject().processAfterNew( document, request.getParameterMap() );

			// If a maintenance lock exists, warn the user.
			MaintenanceUtils.checkForLockingDocument(document.getNewMaintainableObject(), false);
		}

		// set maintenance action state
		document.getNewMaintainableObject().setMaintenanceAction(maintenanceAction);
		maintenanceForm.setMaintenanceAction(maintenanceAction);

		// attach any extra JS from the data dictionary
		if (LOG.isDebugEnabled()) {
			LOG.debug("maintenanceForm.getAdditionalScriptFiles(): " + maintenanceForm.getAdditionalScriptFiles());
		}
		if (maintenanceForm.getAdditionalScriptFiles().isEmpty()) {
			DocumentEntry docEntry = getDataDictionaryService().getDataDictionary().getDocumentEntry(document.getDocumentHeader().getWorkflowDocument().getDocumentType());
			maintenanceForm.getAdditionalScriptFiles().addAll(docEntry.getWebScriptFiles());
		}

		// Retrieve notes topic display flag from data dictionary and add to document
		//      
		DocumentEntry entry = maintenanceDocumentDictionaryService.getMaintenanceDocumentEntry(document.getDocumentHeader().getWorkflowDocument().getDocumentType());
		document.setDisplayTopicFieldInNotes(entry.getDisplayTopicFieldInNotes());

		return mapping.findForward(RiceConstants.MAPPING_BASIC);
	}

    protected void populateBOWithCopyKeyValues(HttpServletRequest request, PersistableBusinessObject oldBusinessObject, Maintainable oldMaintainableObject) throws Exception{
		List keyFieldNamesToCopy = new ArrayList();
		Map<String, String> parametersToCopy;
		if (!StringUtils.isBlank(request.getParameter(KNSConstants.COPY_KEYS))) {
			String[] copyKeys = request.getParameter(KNSConstants.COPY_KEYS).split(KNSConstants.FIELD_CONVERSIONS_SEPARATOR);
			for (String copyKey: copyKeys) {
				keyFieldNamesToCopy.add(copyKey);
			}
		}
		parametersToCopy = getRequestParameters(keyFieldNamesToCopy, oldMaintainableObject, request);
		if(parametersToCopy!=null && parametersToCopy.size()>0){
			copyParametersToBO(parametersToCopy, oldBusinessObject);
		}
	}

    protected void copyParametersToBO(Map<String, String> parameters, PersistableBusinessObject newBO) throws Exception{
		for (String parmName : parameters.keySet()) {
			String propertyValue = parameters.get(parmName);

			if (StringUtils.isNotBlank(propertyValue)) {
				String propertyName = parmName;
				// set value of property in bo
				if (PropertyUtils.isWriteable(newBO, propertyName)) {
					Class type = ObjectUtils.easyGetPropertyType(newBO, propertyName);
					if (type != null && Formatter.getFormatter(type) != null) {
						Formatter formatter = Formatter.getFormatter(type);
						Object obj = formatter.convertFromPresentationFormat(propertyValue);
						ObjectUtils.setObjectProperty(newBO, propertyName, obj.getClass(), obj);
					}
					else {
						ObjectUtils.setObjectProperty(newBO, propertyName, String.class, propertyValue);
					}
				}
			}
		}
	}

	/**
	 * Downloads the attachment to the user's browser
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward downloadAttachment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		KualiDocumentFormBase documentForm = (KualiDocumentFormBase) form;
		MaintenanceDocumentBase document = (MaintenanceDocumentBase) documentForm.getDocument();
		document.refreshReferenceObject("attachment");
		DocumentAttachment attachment = document.getAttachment();
		if(attachment != null) {
			streamToResponse(attachment.getAttachmentContent(), attachment.getFileName(), attachment.getContentType(), response); 
		}
		return null;
	}


	/**
	 * 
	 * This method used to replace the attachment
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward replaceAttachment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		KualiDocumentFormBase documentForm = (KualiDocumentFormBase) form;
		MaintenanceDocumentBase document = (MaintenanceDocumentBase) documentForm.getDocument();
		document.refreshReferenceObject("attachment");
		getBusinessObjectService().delete(document.getAttachment());
		return mapping.findForward(RiceConstants.MAPPING_BASIC);
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
	@Override
	public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		KualiDocumentFormBase documentForm = (KualiDocumentFormBase) form;
		MaintenanceDocumentBase document = (MaintenanceDocumentBase) documentForm.getDocument();

		ActionForward forward = super.route(mapping, form, request, response);
		if(document.getNewMaintainableObject().getBusinessObject() instanceof PersistableAttachment) {
			PersistableAttachment bo = (PersistableAttachment) getBusinessObjectService().retrieve(document.getNewMaintainableObject().getBusinessObject());
			request.setAttribute("fileName", bo.getFileName());
		}

		return forward;
	}

	/**
	 * Handles creating and loading of documents.
	 */
	@Override
	public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		super.docHandler(mapping, form, request, response);
		KualiMaintenanceForm kualiMaintenanceForm = (KualiMaintenanceForm) form;

		if (KEWConstants.ACTIONLIST_COMMAND.equals(kualiMaintenanceForm.getCommand()) || KEWConstants.DOCSEARCH_COMMAND.equals(kualiMaintenanceForm.getCommand()) || KEWConstants.SUPERUSER_COMMAND.equals(kualiMaintenanceForm.getCommand()) || KEWConstants.HELPDESK_ACTIONLIST_COMMAND.equals(kualiMaintenanceForm.getCommand()) && kualiMaintenanceForm.getDocId() != null) {
			if (kualiMaintenanceForm.getDocument() instanceof MaintenanceDocument) {
				kualiMaintenanceForm.setReadOnly(true);
				kualiMaintenanceForm.setMaintenanceAction(((MaintenanceDocument) kualiMaintenanceForm.getDocument()).getNewMaintainableObject().getMaintenanceAction());

				//Retrieving the FileName from BO table
				Maintainable tmpMaintainable = ((MaintenanceDocument) kualiMaintenanceForm.getDocument()).getNewMaintainableObject();
				if(tmpMaintainable.getBusinessObject() instanceof PersistableAttachment) {
					PersistableAttachment bo = (PersistableAttachment) getBusinessObjectService().retrieve(tmpMaintainable.getBusinessObject());
					if(bo != null)
						request.setAttribute("fileName", bo.getFileName());
				}
			}
			else {
				LOG.error("Illegal State: document is not a maintenance document");
				throw new IllegalStateException("Document is not a maintenance document");
			}
		}
		else if (KEWConstants.INITIATE_COMMAND.equals(kualiMaintenanceForm.getCommand())) {
			kualiMaintenanceForm.setReadOnly(false);
			return setupMaintenance(mapping, form, request, response, KNSConstants.MAINTENANCE_NEW_ACTION);
		}
		else {
			LOG.error("We should never have gotten to here");
			throw new IllegalStateException("docHandler called with invalid parameters");
		}
		return mapping.findForward(RiceConstants.MAPPING_BASIC);
	}

	/**
	 * Called on return from a lookup.
	 */
	@Override
	public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		KualiMaintenanceForm maintenanceForm = (KualiMaintenanceForm) form;

		WebUtils.reuseErrorMapFromPreviousRequest(maintenanceForm);
		maintenanceForm.setDerivedValuesOnForm(request);

		refreshAdHocRoutingWorkgroupLookups(request, maintenanceForm);
		MaintenanceDocument document = (MaintenanceDocument) maintenanceForm.getDocument();

		// call refresh on new maintainable
		Map<String, String> requestParams = new HashMap<String, String>();
		for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
			String requestKey = (String) i.nextElement();
			String requestValue = request.getParameter(requestKey);
			requestParams.put(requestKey, requestValue);
		}

		// Add multiple values from Lookup
		Collection<PersistableBusinessObject> rawValues = null;
		if (StringUtils.equals(KNSConstants.MULTIPLE_VALUE, maintenanceForm.getRefreshCaller())) {
			String lookupResultsSequenceNumber = maintenanceForm.getLookupResultsSequenceNumber();
			if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
				// actually returning from a multiple value lookup
				String lookupResultsBOClassName = maintenanceForm.getLookupResultsBOClassName();
				Class lookupResultsBOClass = Class.forName(lookupResultsBOClassName);

				rawValues = getLookupResultsService().retrieveSelectedResultBOs(lookupResultsSequenceNumber, lookupResultsBOClass, GlobalVariables.getUserSession().getPerson().getPrincipalId());
			}
		}

		if (rawValues != null) { // KULCOA-1073 - caused by this block running unnecessarily?
			// we need to run the business rules on all the newly added items to the collection
			// KULCOA-1000, KULCOA-1004 removed business rule validation on multiple value return
			// (this was running before the objects were added anyway)
			// getKualiRuleService().applyRules(new SaveDocumentEvent(document));
			String collectionName = maintenanceForm.getLookedUpCollectionName();
			//TODO: Cathy remember to delete this block of comments after I've tested.            
			//            PersistableBusinessObject bo = document.getNewMaintainableObject().getBusinessObject();
			//            Collection maintCollection = this.extractCollection(bo, collectionName);
			//            String docTypeName = ((MaintenanceDocument) maintenanceForm.getDocument()).getDocumentHeader().getWorkflowDocument().getDocumentType();
			//            Class collectionClass = extractCollectionClass(docTypeName, collectionName);
			//
			//            List<MaintainableSectionDefinition> sections = maintenanceDocumentDictionaryService.getMaintainableSections(docTypeName);
			//            Map<String, String> template = MaintenanceUtils.generateMultipleValueLookupBOTemplate(sections, collectionName);
			//            for (PersistableBusinessObject nextBo : rawValues) {
			//                PersistableBusinessObject templatedBo = (PersistableBusinessObject) ObjectUtils.createHybridBusinessObject(collectionClass, nextBo, template);
			//                templatedBo.setNewCollectionRecord(true);
			//                maintCollection.add(templatedBo);
			//            }
			document.getNewMaintainableObject().addMultipleValueLookupResults(document, collectionName, rawValues, false, document.getNewMaintainableObject().getBusinessObject());
			if (LOG.isInfoEnabled()) {
				LOG.info("********************doing editing 3 in refersh()***********************.");
			}
			boolean isEdit = KNSConstants.MAINTENANCE_EDIT_ACTION.equals(maintenanceForm.getMaintenanceAction());
			boolean isCopy = KNSConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceForm.getMaintenanceAction());

			if (isEdit || isCopy) {
				document.getOldMaintainableObject().addMultipleValueLookupResults(document, collectionName, rawValues, true, document.getOldMaintainableObject().getBusinessObject());
				document.getOldMaintainableObject().refresh(maintenanceForm.getRefreshCaller(), requestParams, document);
			}
		}

		document.getNewMaintainableObject().refresh(maintenanceForm.getRefreshCaller(), requestParams, document);

		//pass out customAction from methodToCall parameter. Call processAfterPost
		String fullParameter = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
		if(StringUtils.contains(fullParameter, KNSConstants.CUSTOM_ACTION)){
			String customAction = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
			String[] actionValue = new String[1];
			actionValue[0]= StringUtils.substringAfter(customAction, ".");
			Map<String,String[]> paramMap = request.getParameterMap();
			paramMap.put(KNSConstants.CUSTOM_ACTION, actionValue);
			doProcessingAfterPost( (KualiMaintenanceForm) form, paramMap );
		}

		return mapping.findForward(RiceConstants.MAPPING_BASIC);
	}

	/**
	 * Gets keys for the maintainable business object from the persistence metadata explorer. Checks for existence of key property
	 * names as request parameters, if found adds them to the returned hash map.
	 */
    protected Map buildKeyMapFromRequest(Maintainable maintainable, HttpServletRequest request) {
		List keyFieldNames = null;
		// are override keys listed in the request? If so, then those need to be our keys,
		// not the primary keye fields for the BO
		if (!StringUtils.isBlank(request.getParameter(KNSConstants.OVERRIDE_KEYS))) {
			String[] overrideKeys = request.getParameter(KNSConstants.OVERRIDE_KEYS).split(KNSConstants.FIELD_CONVERSIONS_SEPARATOR);
			keyFieldNames = new ArrayList();
			for (String overrideKey : overrideKeys) {
				keyFieldNames.add(overrideKey);
			}
		}
		else {
			keyFieldNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(maintainable.getBusinessObject().getClass());
		}
		return getRequestParameters(keyFieldNames, maintainable, request);
	}

    protected Map<String, String> getRequestParameters(List keyFieldNames, Maintainable maintainable, HttpServletRequest request){

		Map<String, String> requestParameters = new HashMap<String, String>();


		for (Iterator iter = keyFieldNames.iterator(); iter.hasNext();) {
			String keyPropertyName = (String) iter.next();

			if (request.getParameter(keyPropertyName) != null) {
				String keyValue = request.getParameter(keyPropertyName);

				// Check if this element was encrypted, if it was decrypt it
                if (getBusinessObjectAuthorizationService().attributeValueNeedsToBeEncryptedOnFormsAndLinks(maintainable.getBoClass(), keyPropertyName)) {
					try {
                    	keyValue = StringUtils.removeEnd(keyValue, EncryptionService.ENCRYPTION_POST_PREFIX);
						keyValue = encryptionService.decrypt(keyValue);
					}
					catch (GeneralSecurityException e) {
						throw new RuntimeException(e);
					}
				}


				requestParameters.put(keyPropertyName, keyValue);
			}
		}

		return requestParameters;

	}

	/**
	 * Convert a Request into a Map<String,String>. Technically, Request parameters do not neatly translate into a Map of Strings,
	 * because a given parameter may legally appear more than once (so a Map of String[] would be more accurate.) This method should
	 * be safe for business objects, but may not be reliable for more general uses.
	 */
	String extractCollectionName(HttpServletRequest request, String methodToCall) {
		// collection name and underlying object type from request parameter
		String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
		String collectionName = null;
		if (StringUtils.isNotBlank(parameterName)) {
			collectionName = StringUtils.substringBetween(parameterName, methodToCall + ".", ".(");
		}
		return collectionName;
	}

	Collection extractCollection(PersistableBusinessObject bo, String collectionName) {
		// retrieve the collection from the business object
		Collection maintCollection = (Collection) ObjectUtils.getPropertyValue(bo, collectionName);
		return maintCollection;
	}

	Class extractCollectionClass(String docTypeName, String collectionName) {
		return maintenanceDocumentDictionaryService.getCollectionBusinessObjectClass(docTypeName, collectionName);
	}

	/**
	 * Adds a line to a collection being maintained in a many section.
	 */
	public ActionForward addLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		KualiMaintenanceForm maintenanceForm = (KualiMaintenanceForm) form;
		MaintenanceDocument document = (MaintenanceDocument) maintenanceForm.getDocument();
		Maintainable oldMaintainable = document.getOldMaintainableObject();
		Maintainable newMaintainable = document.getNewMaintainableObject();

		String collectionName = extractCollectionName(request, KNSConstants.ADD_LINE_METHOD);
		if (collectionName == null) {
			LOG.error("Unable to get find collection name and class in request.");
			throw new RuntimeException("Unable to get find collection name and class in request.");
		}

		// if dealing with sub collection it will have a "["
		if ((StringUtils.lastIndexOf(collectionName, "]") + 1) == collectionName.length()) {
			collectionName = StringUtils.substringBeforeLast(collectionName, "[");
		}

		PersistableBusinessObject bo = newMaintainable.getBusinessObject();
		Collection maintCollection = extractCollection(bo, collectionName);
		Class collectionClass = extractCollectionClass(((MaintenanceDocument) maintenanceForm.getDocument()).getDocumentHeader().getWorkflowDocument().getDocumentType(), collectionName);

		// TODO: sort of collection, new instance should be first

		// get the BO from the new collection line holder
		PersistableBusinessObject addBO = newMaintainable.getNewCollectionLine(collectionName);
		if (LOG.isDebugEnabled()) {
			LOG.debug("obtained addBO from newCollectionLine: " + addBO);
		}

		// link up the user fields, if any
		getBusinessObjectService().linkUserFields(addBO);

		//KULRICE-4264 - a hook to change the state of the business object, which is the "new line" of a collection, before it is validated
		newMaintainable.processBeforeAddLine(collectionName, collectionClass, addBO);
		
		// apply rules to the addBO
		boolean rulePassed = false;
		if (LOG.isDebugEnabled()) {
			LOG.debug("about to call AddLineEvent applyRules: document=" + document + "\ncollectionName=" + collectionName + "\nBO=" + addBO);
		}
		rulePassed = getKualiRuleService().applyRules(new KualiAddLineEvent(document, collectionName, addBO));

		// if the rule evaluation passed, let's add it
		if (rulePassed) {
			if (LOG.isInfoEnabled()) {
				LOG.info("********************doing editing 4 in addline()***********************.");
			}
			// if edit or copy action, just add empty instance to old maintainable
			boolean isEdit = KNSConstants.MAINTENANCE_EDIT_ACTION.equals(maintenanceForm.getMaintenanceAction());
			boolean isCopy = KNSConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceForm.getMaintenanceAction());


			if (isEdit || isCopy) {
				PersistableBusinessObject oldBo = oldMaintainable.getBusinessObject();
				Collection oldMaintCollection = (Collection) ObjectUtils.getPropertyValue(oldBo, collectionName);

				if (oldMaintCollection == null) {
					oldMaintCollection = new ArrayList();
				}
				if (PersistableBusinessObject.class.isAssignableFrom(collectionClass)) {
					PersistableBusinessObject placeholder = (PersistableBusinessObject) collectionClass.newInstance();
					// KULRNE-4538: must set it as a new collection record, because the maintainable will set the BO that gets added
					// to the new maintainable as a new collection record

					// if not set, then the subcollections of the newly added object will appear as read only
					// see FieldUtils.getContainerRows on how the delete button is rendered
					placeholder.setNewCollectionRecord(true);
					((List) oldMaintCollection).add(placeholder);
				}
				else {
					LOG.warn("Should be a instance of PersistableBusinessObject");
					((List) oldMaintCollection).add(collectionClass.newInstance());
				}
				// update collection in maintenance business object
				ObjectUtils.setObjectProperty(oldBo, collectionName, List.class, oldMaintCollection);
			}

			newMaintainable.addNewLineToCollection(collectionName);
			int subCollectionIndex = 0;
			for (Object aSubCollection : maintCollection) {
				subCollectionIndex += getSubCollectionIndex(aSubCollection, maintenanceForm.getDocTypeName());
			}
			//TODO: Should we keep this logic and continue using currentTabIndex as the key in the tabStates HashMap ?
			//            
			//            String parameter = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
			//            String indexStr = StringUtils.substringBetween(parameter, Constants.METHOD_TO_CALL_PARM13_LEFT_DEL, Constants.METHOD_TO_CALL_PARM13_RIGHT_DEL);
			//            // + 1 is for the fact that the first element of a collection is on the next tab
			//            int index = Integer.parseInt(indexStr) + subCollectionIndex + 1;
			//            Map<String, String> tabStates = maintenanceForm.getTabStates();
			//            Map<String, String> copyOfTabStates = new HashMap<String, String>();
			//
			//            int incrementor = 0;
			//            for (String tabState : tabStates.keySet()) {
			//            	String originalValue = maintenanceForm.getTabState(Integer.toString(incrementor));
			//                copyOfTabStates.put(Integer.toString(incrementor), originalValue);
			//                incrementor++;
			//            }
			//
			//            int i = index;
			//        	if (tabStates.containsKey(Integer.toString(i-1))) {
			//        		tabStates.remove(Integer.toString(i-1));
			//        	}
			//            while (i < copyOfTabStates.size() + 1) {
			//                String originalValue = copyOfTabStates.get(Integer.toString(i-1));
			//                if (tabStates.containsKey(Integer.toString(i))) {
			//                    tabStates.remove(Integer.toString(i));
			//                }
			//                tabStates.put(Integer.toString(i), originalValue);
			//                i++;
			//            }


			// End of whether we should continue to keep this logic and use currentTabIndex as the key            
		}
		doProcessingAfterPost( (KualiMaintenanceForm) form, request );

		return mapping.findForward(RiceConstants.MAPPING_BASIC);
	}

    protected int getSubCollectionIndex(Object object, String documentTypeName) {
		int index = 1;
		MaintainableCollectionDefinition theCollectionDefinition = null;
		for (MaintainableCollectionDefinition maintainableCollectionDefinition : maintenanceDocumentDictionaryService.getMaintainableCollections(documentTypeName)) {
			if (maintainableCollectionDefinition.getBusinessObjectClass().equals(object.getClass())) {
				// we've found the collection we were looking for, so let's find all of its subcollections
				theCollectionDefinition = maintainableCollectionDefinition;
				break;
			}
		}
		if (theCollectionDefinition != null) {
			for (MaintainableCollectionDefinition subCollDef : theCollectionDefinition.getMaintainableCollections()) {
				String name = subCollDef.getName();
				String capitalFirst = name.substring(0, 1).toUpperCase();
				String methodName = "get" + capitalFirst + name.substring(1);
				List subCollectionList = new ArrayList();
				try {
					subCollectionList = (List) object.getClass().getMethod(methodName).invoke(object);
				}
				catch (InvocationTargetException ite) {
					// this shouldn't happen
				}
				catch (IllegalAccessException iae) {
					// this shouldn't happen
				}
				catch (NoSuchMethodException nme) {
					// this shouldn't happen
				}
				index += subCollectionList.size();
			}
		}
		return index;
	}

	/**
	 * Deletes a collection line that is pending by this document. The collection name and the index to delete is embedded into the
	 * delete button name. These parameters are extracted, the collection pulled out of the parent business object, and finally the
	 * collection record at the specified index is removed for the new maintainable, and the old if we are dealing with an edit.
	 */
	public ActionForward deleteLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		KualiMaintenanceForm maintenanceForm = (KualiMaintenanceForm) form;
		MaintenanceDocument document = (MaintenanceDocument) maintenanceForm.getDocument();
		Maintainable oldMaintainable = document.getOldMaintainableObject();
		Maintainable newMaintainable = document.getNewMaintainableObject();

		String collectionName = extractCollectionName(request, KNSConstants.DELETE_LINE_METHOD);
		if (collectionName == null) {
			LOG.error("Unable to get find collection name in request.");
			throw new RuntimeException("Unable to get find collection class in request.");
		}

		PersistableBusinessObject bo = newMaintainable.getBusinessObject();
		Collection maintCollection = extractCollection(bo, collectionName);
		if (collectionName == null) {
			LOG.error("Collection is null in parent business object.");
			throw new RuntimeException("Collection is null in parent business object.");
		}

		int deleteRecordIndex = getLineToDelete(request);
		if (deleteRecordIndex < 0 || deleteRecordIndex > maintCollection.size() - 1) {
			if (collectionName == null) {
				LOG.error("Invalid index for deletion of collection record: " + deleteRecordIndex);
				throw new RuntimeException("Invalid index for deletion of collection record: " + deleteRecordIndex);
			}
		}

		((List) maintCollection).remove(deleteRecordIndex);

		// if it's either an edit or a copy, need to remove the collection from the old maintainable as well
		if (KNSConstants.MAINTENANCE_EDIT_ACTION.equals(maintenanceForm.getMaintenanceAction()) ||
				KNSConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceForm.getMaintenanceAction())) {
			bo = oldMaintainable.getBusinessObject();
			maintCollection = extractCollection(bo, collectionName);

			if (collectionName == null) {
				LOG.error("Collection is null in parent business object.");
				throw new RuntimeException("Collection is null in parent business object.");
			}

			((List) maintCollection).remove(deleteRecordIndex);
		}

		// remove the tab state information of the tab that the deleted element originally occupied, so that it will keep tab states
		// consistent
		//        String parameter = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
		//        String indexStr = StringUtils.substringBetween(parameter, Constants.METHOD_TO_CALL_PARM13_LEFT_DEL, Constants.METHOD_TO_CALL_PARM13_RIGHT_DEL);
		//        int index = Integer.parseInt(indexStr);
		//        maintenanceForm.removeTabState(index);


		//      TODO: Should we keep this logic and continue using currentTabIndex as the key in the tabStates HashMap ?        
		//        
		//        String parameter = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
		//        String indexStr = StringUtils.substringBetween(parameter, Constants.METHOD_TO_CALL_PARM13_LEFT_DEL, Constants.METHOD_TO_CALL_PARM13_RIGHT_DEL);
		//        // + 1 is for the fact that the first element of a collection is on the next tab
		//        int index = Integer.parseInt(indexStr) +  1;
		//        Map<String, String> tabStates = maintenanceForm.getTabStates();
		//        Map<String, String> copyOfTabStates = new HashMap<String, String>();
		//
		//        int incrementor = 0;
		//        for (String tabState : tabStates.keySet()) {
		//        	String originalValue = maintenanceForm.getTabState(Integer.toString(incrementor));
		//            copyOfTabStates.put(Integer.toString(incrementor), originalValue);
		//            incrementor++;
		//        }
		//
		//        int i = index;
		//
		//        while (i < copyOfTabStates.size() ) {
		//            String originalValue = copyOfTabStates.get(Integer.toString(i));
		//            if (tabStates.containsKey(Integer.toString(i-1))) {
		//                tabStates.remove(Integer.toString(i-1));
		//            }
		//            tabStates.put(Integer.toString(i-1), originalValue);
		//            i++;
		//        }
		//
		//        
		//End of whether we should continue to keep this logic and use currentTabIndex as the key            

		doProcessingAfterPost( (KualiMaintenanceForm) form, request );

		return mapping.findForward(RiceConstants.MAPPING_BASIC);
	}

	/**
	 * Turns on (or off) the inactive record display for a maintenance collection.
	 */
	public ActionForward toggleInactiveRecordDisplay(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		KualiMaintenanceForm maintenanceForm = (KualiMaintenanceForm) form;
		MaintenanceDocument document = (MaintenanceDocument) maintenanceForm.getDocument();
		Maintainable oldMaintainable = document.getOldMaintainableObject();
		Maintainable newMaintainable = document.getNewMaintainableObject();

		String collectionName = extractCollectionName(request, KNSConstants.TOGGLE_INACTIVE_METHOD);
		if (collectionName == null) {
			LOG.error("Unable to get find collection name in request.");
			throw new RuntimeException("Unable to get find collection class in request.");
		}  

		String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
		boolean showInactive = Boolean.parseBoolean(StringUtils.substringBetween(parameterName, KNSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, "."));

		oldMaintainable.setShowInactiveRecords(collectionName, showInactive);
		newMaintainable.setShowInactiveRecords(collectionName, showInactive);

		return mapping.findForward(RiceConstants.MAPPING_BASIC);
	}



	/**
	 * This method clears the value of the primary key fields on a Business Object.
	 * 
	 * @param document - document to clear the pk fields on
	 */
    protected void clearPrimaryKeyFields(MaintenanceDocument document) {
		// get business object being maintained and its keys
		PersistableBusinessObject bo = document.getNewMaintainableObject().getBusinessObject();
		List<String> keyFieldNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(bo.getClass());

		for (String keyFieldName : keyFieldNames) {
			try {
				ObjectUtils.setObjectProperty(bo, keyFieldName, null);
			}
			catch (Exception e) {
				LOG.error("Unable to clear primary key field: " + e.getMessage());
				throw new RuntimeException("Unable to clear primary key field: " + e.getMessage());
			}
		}
	}

	/**
	 * This method is used as part of the Copy functionality, to clear any field values that the user making the copy does not have
	 * permissions to modify. This will prevent authorization errors on a copy.
	 * 
	 * @param document - document to be adjusted
	 */
    protected void clearUnauthorizedNewFields(MaintenanceDocument document) {
		// get a reference to the current user
		Person user = GlobalVariables.getUserSession().getPerson();

		// get the correct documentAuthorizer for this document
		MaintenanceDocumentAuthorizer documentAuthorizer = (MaintenanceDocumentAuthorizer) getDocumentHelperService().getDocumentAuthorizer(document);

		// get a new instance of MaintenanceDocumentAuthorizations for this context
		MaintenanceDocumentRestrictions maintenanceDocumentRestrictions = getBusinessObjectAuthorizationService().getMaintenanceDocumentRestrictions(document, user);

		// get a reference to the newBo
		PersistableBusinessObject newBo = document.getNewMaintainableObject().getBusinessObject();

		document.getNewMaintainableObject().clearBusinessObjectOfRestrictedValues(maintenanceDocumentRestrictions);
	}

	/**
	 * This method does all special processing on a document that should happen on each HTTP post (ie, save, route, approve, etc).
	 * 
	 * @param form
	 */
	@SuppressWarnings("unchecked")
	protected void doProcessingAfterPost( KualiForm form, HttpServletRequest request ) {
		MaintenanceDocument document = (MaintenanceDocument) ((KualiMaintenanceForm)form).getDocument();
		Maintainable maintainable = document.getNewMaintainableObject();
		PersistableBusinessObject bo = maintainable.getBusinessObject();

		getBusinessObjectService().linkUserFields(bo);

		maintainable.processAfterPost(document, request.getParameterMap() );
	}

	protected void doProcessingAfterPost( KualiForm form, Map<String,String[]> parameters ) {
		MaintenanceDocument document = (MaintenanceDocument) ((KualiMaintenanceForm)form).getDocument();
		Maintainable maintainable = document.getNewMaintainableObject();
		PersistableBusinessObject bo = maintainable.getBusinessObject();

		getBusinessObjectService().linkUserFields(bo);

		maintainable.processAfterPost(document, parameters );
	}

	protected void populateAuthorizationFields(KualiDocumentFormBase formBase){
		super.populateAuthorizationFields(formBase);

		KualiMaintenanceForm maintenanceForm = (KualiMaintenanceForm) formBase;
		MaintenanceDocument maintenanceDocument = (MaintenanceDocument) maintenanceForm.getDocument();
		MaintenanceDocumentAuthorizer maintenanceDocumentAuthorizer = (MaintenanceDocumentAuthorizer) getDocumentHelperService().getDocumentAuthorizer(maintenanceDocument);
		Person user = GlobalVariables.getUserSession().getPerson();
		maintenanceForm.setReadOnly(!formBase.getDocumentActions().containsKey(KNSConstants.KUALI_ACTION_CAN_EDIT));
		MaintenanceDocumentRestrictions maintenanceDocumentAuthorizations = getBusinessObjectAuthorizationService().getMaintenanceDocumentRestrictions(maintenanceDocument, user);
		maintenanceForm.setAuthorizations(maintenanceDocumentAuthorizations);
	}

	public LookupService getLookupService() {
		if ( lookupService == null ) {
			lookupService = KNSServiceLocatorWeb.getLookupService();
		}
		return this.lookupService;
	}

	public LookupResultsService getLookupResultsService() {
		if ( lookupResultsService == null ) {
			lookupResultsService = KNSServiceLocatorWeb.getLookupResultsService();
		}
		return this.lookupResultsService;
	}

}
