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

import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.authorization.FieldAuthorization;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.datadictionary.MaintainableCollectionDefinition;
import org.kuali.core.datadictionary.MaintainableSectionDefinition;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.MaintenanceNewCopyAuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.rule.event.KualiAddLineEvent;
import org.kuali.core.service.DocumentAuthorizationService;
import org.kuali.core.service.EncryptionService;
import org.kuali.core.service.MaintenanceDocumentDictionaryService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.MaintenanceUtils;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TabState;
import org.kuali.core.web.format.Formatter;
import org.kuali.core.web.struts.form.KualiMaintenanceForm;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.clientapp.IDocHandler;

/**
 * This class handles actions for maintenance documents. These include creating new edit, and copying of maintenance records.
 */
public class KualiMaintenanceDocumentAction extends KualiDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiMaintenanceDocumentAction.class);

    private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService = null;
    private EncryptionService encryptionService;


    public KualiMaintenanceDocumentAction() {
        super();
        maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
        encryptionService = KNSServiceLocator.getEncryptionService();
    }

    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        if (!(form instanceof KualiMaintenanceForm)) {
            super.checkAuthorization(form, methodToCall);
        }
        else {
            AuthorizationType documentAuthorizationType = new AuthorizationType.Document(((MaintenanceDocument) ((KualiMaintenanceForm) form).getDocument()).getDocumentBusinessObject().getClass(), ((KualiMaintenanceForm) form).getDocument());
            if (!KNSServiceLocator.getKualiModuleService().isAuthorized(GlobalVariables.getUserSession().getUniversalUser(), documentAuthorizationType)) {
                LOG.error("User not authorized to use this document: " + ((MaintenanceDocument) ((KualiMaintenanceForm) form).getDocument()).getDocumentBusinessObject().getClass().getName());
                throw new ModuleAuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), documentAuthorizationType, getKualiModuleService().getResponsibleModule(((MaintenanceDocument) ((KualiMaintenanceForm) form).getDocument()).getDocumentBusinessObject().getClass()));
            }
        }
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(Constants.PARAM_MAINTENANCE_VIEW_MODE, Constants.PARAM_MAINTENANCE_VIEW_MODE_MAINTENANCE);
        return super.execute(mapping, form, request, response);
    }

    /**
     * Calls setup Maintenance for new action.
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return setupMaintenance(mapping, form, request, response, Constants.MAINTENANCE_NEW_ACTION);
    }

    /**
     * Calls setupMaintenance for copy action.
     */
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // check for copy document number
        if (request.getParameter("document." + PropertyConstants.DOCUMENT_NUMBER) == null) { // object copy
            return setupMaintenance(mapping, form, request, response, Constants.MAINTENANCE_COPY_ACTION);
        }
        else { // document copy
            throw new UnsupportedOperationException("System does not support copying of maintenance documents.");
        }
    }

    /**
     * Calls setupMaintenance for edit action.
     */
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return setupMaintenance(mapping, form, request, response, Constants.MAINTENANCE_EDIT_ACTION);
    }

    /**
     * Calls setupMaintenance for new object that have existing objects attributes.
     */
    public ActionForward newWithExisting(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return setupMaintenance(mapping, form, request, response, Constants.MAINTENANCE_NEWWITHEXISTING_ACTION);
    }

    /**
     * Gets a new document for a maintenance record. The maintainable is specified with the documentTypeName or business object
     * class request parameter and request parameters are parsed for key values for retrieving the business object. Forward to the
     * maintenance jsp which renders the page based on the maintainable's field specifications. Retrieves an existing business
     * object for edit and copy. Checks locking on edit.
     */
    private ActionForward setupMaintenance(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String maintenanceAction) throws Exception {
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
            if (Constants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction) || Constants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
                boolean allowsNewOrCopy = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getAllowsNewOrCopy(documentTypeName);
                if (!allowsNewOrCopy) {
                    LOG.error("Document type " + documentTypeName + " does not allow new or copy actions.");
                    throw new MaintenanceNewCopyAuthorizationException(documentTypeName);
                }
            }


            // get new document from service
            document = (MaintenanceDocument) KNSServiceLocator.getDocumentService().getNewDocument(maintenanceForm.getDocTypeName());
            maintenanceForm.setDocument(document);
        }
        else {
            document = (MaintenanceDocument) maintenanceForm.getDocument();
        }

        // retrieve business object from request parameters
        if (!(Constants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)) && !(Constants.MAINTENANCE_NEWWITHEXISTING_ACTION.equals(maintenanceAction))) {
            Map requestParameters = buildKeyMapFromRequest(document.getNewMaintainableObject(), request);
            PersistableBusinessObject oldBusinessObject = (PersistableBusinessObject) KNSServiceLocator.getLookupService().findObjectBySearch(Class.forName(maintenanceForm.getBusinessObjectClassName()), requestParameters);
            if (oldBusinessObject == null) {
                throw new RuntimeException("Cannot retrieve old record for maintenance document, incorrect parameters passed on maint url.");
            }
            PersistableBusinessObject newBusinessObject = (PersistableBusinessObject) ObjectUtils.deepCopy(oldBusinessObject);

            // set business object instance for editing
            document.getOldMaintainableObject().setBusinessObject(oldBusinessObject);
            document.getOldMaintainableObject().setBoClass(Class.forName(maintenanceForm.getBusinessObjectClassName()));
            document.getNewMaintainableObject().setBusinessObject(newBusinessObject);
            document.getNewMaintainableObject().setBoClass(Class.forName(maintenanceForm.getBusinessObjectClassName()));

            // on a COPY, clear any fields that this user isnt authorized for, and also
            // clear the primary key fields
            if (Constants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
                if (!document.isFieldsClearedOnCopy()) {
                    clearPrimaryKeyFields(document);
                    clearUnauthorizedNewFields(document);

                    Maintainable maintainable = document.getNewMaintainableObject();

                    maintainable.processAfterCopy();

                    // mark so that this clearing doesnt happen again
                    document.setFieldsClearedOnCopy(true);

                    // mark so that blank required fields will be populated with default values
                    maintainable.setGenerateBlankRequiredValues(true);
                }
            }
            else if (Constants.MAINTENANCE_EDIT_ACTION.equals(maintenanceAction)) {
                document.getNewMaintainableObject().processAfterEdit();
            }
        }
        // if new with existing we need to populate we need to populate with passed in parameters
        if (Constants.MAINTENANCE_NEWWITHEXISTING_ACTION.equals(maintenanceAction)) {
            // TODO: this code should be abstracted out into a helper
            // also is it a problem that we're not calling setGenerateDefaultValues? it blanked out the below values when I did
            // maybe we need a new generateDefaultValues that doesn't overwrite?
            PersistableBusinessObject newBO = document.getNewMaintainableObject().getBusinessObject();
            Map<String, String> parameters = buildKeyMapFromRequest(document.getNewMaintainableObject(), request);
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
            newBO.refresh();
            document.getNewMaintainableObject().setupNewFromExisting();
        }

        // for new maintainble need to pick up default values
        if (Constants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)) {
            document.getNewMaintainableObject().setGenerateDefaultValues(true);
        }

        // set maintenance action state
        document.getNewMaintainableObject().setMaintenanceAction(maintenanceAction);
        maintenanceForm.setMaintenanceAction(maintenanceAction);

        // attach any extra JS from the data dictionary
        if (LOG.isDebugEnabled()) {
            LOG.debug("maintenanceForm.getAdditionalScriptFiles(): " + maintenanceForm.getAdditionalScriptFiles());
        }
        if (maintenanceForm.getAdditionalScriptFiles().isEmpty()) {
            DocumentEntry docEntry = KNSServiceLocator.getDataDictionaryService().getDataDictionary().getDocumentEntry(document.getDocumentHeader().getWorkflowDocument().getDocumentType());
            maintenanceForm.getAdditionalScriptFiles().addAll(docEntry.getWebScriptFiles());
        }

        // Retrieve notes topic display flag from data dictionary and add to document
//      
        DocumentEntry entry = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getMaintenanceDocumentEntry(document.getDocumentHeader().getWorkflowDocument().getDocumentType());
        document.setDisplayTopicFieldInNotes(entry.getDisplayTopicFieldInNotes());

        return mapping.findForward(Constants.MAPPING_BASIC);
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
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiMaintenanceForm maintForm = (KualiMaintenanceForm) form;
        MaintenanceDocument maintDoc = (MaintenanceDocument) maintForm.getDocument();

        doProcessingAfterPost((KualiMaintenanceForm) form);
        return super.save(mapping, form, request, response);
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
        doProcessingAfterPost((KualiMaintenanceForm) form);
        return super.route(mapping, form, request, response);
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
    @Override
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        doProcessingAfterPost((KualiMaintenanceForm) form);
        return super.blanketApprove(mapping, form, request, response);
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
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        doProcessingAfterPost((KualiMaintenanceForm) form);
        return super.approve(mapping, form, request, response);
    }

    /**
     * Handles creating and loading of documents.
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.docHandler(mapping, form, request, response);
        KualiMaintenanceForm kualiMaintenanceForm = (KualiMaintenanceForm) form;

        if (IDocHandler.ACTIONLIST_COMMAND.equals(kualiMaintenanceForm.getCommand()) || IDocHandler.DOCSEARCH_COMMAND.equals(kualiMaintenanceForm.getCommand()) || IDocHandler.SUPERUSER_COMMAND.equals(kualiMaintenanceForm.getCommand()) || IDocHandler.HELPDESK_ACTIONLIST_COMMAND.equals(kualiMaintenanceForm.getCommand()) && kualiMaintenanceForm.getDocId() != null) {
            if (kualiMaintenanceForm.getDocument() instanceof MaintenanceDocument) {
                kualiMaintenanceForm.setReadOnly(true);
                kualiMaintenanceForm.setMaintenanceAction(((MaintenanceDocument) kualiMaintenanceForm.getDocument()).getNewMaintainableObject().getMaintenanceAction());
            }
            else {
                LOG.error("Illegal State: document is not a maintenance document");
                throw new IllegalStateException("Document is not a maintenance document");
            }
        }
        else if (IDocHandler.INITIATE_COMMAND.equals(kualiMaintenanceForm.getCommand())) {
            kualiMaintenanceForm.setReadOnly(false);
            return setupMaintenance(mapping, form, request, response, Constants.MAINTENANCE_NEW_ACTION);
        }
        else {
            LOG.error("We should never have gotten to here");
            throw new IllegalStateException("docHandler called with invalid parameters");
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Called on return from a lookup.
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiMaintenanceForm maintenanceForm = (KualiMaintenanceForm) form;
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
        if (StringUtils.equals(Constants.MULTIPLE_VALUE, maintenanceForm.getRefreshCaller())) {
            String lookupResultsSequenceNumber = maintenanceForm.getLookupResultsSequenceNumber();
            if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
                // actually returning from a multiple value lookup
                String lookupResultsBOClassName = maintenanceForm.getLookupResultsBOClassName();
                Class lookupResultsBOClass = Class.forName(lookupResultsBOClassName);

                rawValues = KNSServiceLocator.getLookupResultsService().retrieveSelectedResultBOs(lookupResultsSequenceNumber, lookupResultsBOClass, GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
            }
        }

        if (rawValues != null) { // KULCOA-1073 - caused by this block running unnecessarily?
            // we need to run the business rules on all the newly added items to the collection
            // KULCOA-1000, KULCOA-1004 removed business rule validation on multiple value return
            // (this was running before the objects were added anyway)
            // KNSServiceLocator.getKualiRuleService().applyRules(new SaveDocumentEvent(document));
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
            document.getNewMaintainableObject().addMultipleValueLookupResults(document, collectionName, rawValues);
        }

        document.getNewMaintainableObject().refresh(maintenanceForm.getRefreshCaller(), requestParams, document);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Gets keys for the maintainable business object from the persistence metadata explorer. Checks for existence of key property
     * names as request parameters, if found adds them to the returned hash map.
     */
    private Map buildKeyMapFromRequest(Maintainable maintainable, HttpServletRequest request) {
        List keyFieldNames = null;
        // are override keys listed in the request? If so, then those need to be our keys,
        // not the primary keye fields for the BO
        if (!StringUtils.isBlank(request.getParameter(Constants.OVERRIDE_KEYS))) {
            String[] overrideKeys = request.getParameter(Constants.OVERRIDE_KEYS).split(Constants.FIELD_CONVERSIONS_SEPERATOR);
            keyFieldNames = new ArrayList();
            for (String overrideKey : overrideKeys) {
                keyFieldNames.add(overrideKey);
            }
        }
        else {
            keyFieldNames = KNSServiceLocator.getPersistenceStructureService().listPrimaryKeyFieldNames(maintainable.getBusinessObject().getClass());
        }

        Map requestParameters = new HashMap();


        // List of encrypted values
        String encryptedString = request.getParameter(Constants.ENCRYPTED_LIST_PREFIX);
        List encryptedList = new ArrayList();
        if (StringUtils.isNotBlank(encryptedString)) {
            encryptedList = Arrays.asList(StringUtils.split(encryptedString, Constants.FIELD_CONVERSIONS_SEPERATOR));
        }


        for (Iterator iter = keyFieldNames.iterator(); iter.hasNext();) {
            String keyPropertyName = (String) iter.next();

            if (request.getParameter(keyPropertyName) != null) {
                String keyValue = request.getParameter(keyPropertyName);

                // Check if this element was encrypted, if it was decrypt it
                if (encryptedList.contains(keyPropertyName)) {
                    try {
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
        String parameterName = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
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

        String collectionName = extractCollectionName(request, Constants.ADD_LINE_METHOD);
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
        KNSServiceLocator.getBusinessObjectService().linkUserFields(addBO);

        // apply rules to the addBO
        boolean rulePassed = false;
        if (LOG.isDebugEnabled()) {
            LOG.debug("about to call AddLineEvent applyRules: document=" + document + "\ncollectionName=" + collectionName + "\nBO=" + addBO);
        }
        rulePassed = KNSServiceLocator.getKualiRuleService().applyRules(new KualiAddLineEvent(document, collectionName, addBO));

        // if the rule evaluation passed, let's add it
        if (rulePassed) {

            // if edit or copy action, just add empty instance to old maintainable
            boolean isEdit = Constants.MAINTENANCE_EDIT_ACTION.equals(maintenanceForm.getMaintenanceAction());
            boolean isCopy = Constants.MAINTENANCE_COPY_ACTION.equals(maintenanceForm.getMaintenanceAction());
            
            
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

            String parameter = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
            String indexStr = StringUtils.substringBetween(parameter, Constants.METHOD_TO_CALL_PARM13_LEFT_DEL, Constants.METHOD_TO_CALL_PARM13_RIGHT_DEL);
            // + 1 is for the fact that the first element of a collection is on the next tab
            int index = Integer.parseInt(indexStr) + subCollectionIndex + 1;
            List<TabState> tabStates = maintenanceForm.getTabStates();
            List<TabState> copyOfTabStates = new ArrayList();

            for (TabState tabState : tabStates) {
                TabState newTabState = new TabState();
                newTabState.setOpen(tabState.isOpen());
                copyOfTabStates.add(newTabState);
            }

            int i = index;
            while (i < tabStates.size()) {
                TabState original = tabStates.get(i);
                TabState copy = copyOfTabStates.get(i - 1);
                original.setOpen(copy.isOpen());
                i++;
            }
            tabStates.get(index - 1).setOpen(false);
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    private int getSubCollectionIndex(Object object, String documentTypeName) {
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

        String collectionName = extractCollectionName(request, Constants.DELETE_LINE_METHOD);
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

        // if not an edit, need to remove the collection from the old maintainable as well
        if (Constants.MAINTENANCE_EDIT_ACTION.equals(maintenanceForm.getMaintenanceAction())) {
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
        String parameter = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
        String indexStr = StringUtils.substringBetween(parameter, Constants.METHOD_TO_CALL_PARM13_LEFT_DEL, Constants.METHOD_TO_CALL_PARM13_RIGHT_DEL);
        int index = Integer.parseInt(indexStr);
        maintenanceForm.removeTabState(index);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Turns on (or off) the inactive record display for a maintenance collection.
     */
    public ActionForward toggleInactiveRecordDisplay(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiMaintenanceForm maintenanceForm = (KualiMaintenanceForm) form;
        MaintenanceDocument document = (MaintenanceDocument) maintenanceForm.getDocument();
        Maintainable oldMaintainable = document.getOldMaintainableObject();
        Maintainable newMaintainable = document.getNewMaintainableObject();
        
        String collectionName = extractCollectionName(request, Constants.TOGGLE_INACTIVE_METHOD);
        if (collectionName == null) {
            LOG.error("Unable to get find collection name in request.");
            throw new RuntimeException("Unable to get find collection class in request.");
        }  
        
        String parameterName = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
        boolean showInactive = Boolean.parseBoolean(StringUtils.substringBetween(parameterName, Constants.METHOD_TO_CALL_BOPARM_LEFT_DEL, "."));

        oldMaintainable.setShowInactiveRecords(collectionName, showInactive);
        newMaintainable.setShowInactiveRecords(collectionName, showInactive);
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }


    /**
     * Sets error message for lock and forwards to document which has the record locked.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param lockedDocument
     * @return
     * @throws Exception
     * @deprecated
     */
    private ActionForward handleLockedDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, Document lockedDocument) throws Exception {

        // post an error about the locked document
        LOG.debug("Maintenance record: " + lockedDocument.getDocumentHeader().getDocumentNumber() + "is locked.");
        GlobalVariables.getErrorMap().put(Constants.GLOBAL_ERRORS, KeyConstants.ERROR_MAINTENANCE_LOCKED1);
        // TODO: post message about no other validations have been run, and it hasnt been saved

        // load the blocking document
        KualiMaintenanceForm kualiMaintenanceForm = (KualiMaintenanceForm) form;
        kualiMaintenanceForm.setDocId(lockedDocument.getDocumentNumber());
        kualiMaintenanceForm.setDocument(lockedDocument);

        // document is read only
        kualiMaintenanceForm.setReadOnly(true);
        kualiMaintenanceForm.setDocTypeName(lockedDocument.getDocumentHeader().getWorkflowDocument().getDocumentType());
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method clears the value of the primary key fields on a Business Object.
     * 
     * @param document - document to clear the pk fields on
     */
    private void clearPrimaryKeyFields(MaintenanceDocument document) {
        // get business object being maintained and its keys
        PersistableBusinessObject bo = document.getNewMaintainableObject().getBusinessObject();
        PersistenceStructureService psService = KNSServiceLocator.getPersistenceStructureService();
        List<String> keyFieldNames = psService.listPrimaryKeyFieldNames(bo.getClass());

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
    private void clearUnauthorizedNewFields(MaintenanceDocument document) {
        MaintenanceDocumentDictionaryService maintDocDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();

        // get a reference to the current user
        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();

        // get the correct documentAuthorizer for this document
        DocumentAuthorizationService documentAuthorizationService = KNSServiceLocator.getDocumentAuthorizationService();
        MaintenanceDocumentAuthorizer documentAuthorizer = (MaintenanceDocumentAuthorizer) documentAuthorizationService.getDocumentAuthorizer(document);

        // get a new instance of MaintenanceDocumentAuthorizations for this context
        MaintenanceDocumentAuthorizations auths = documentAuthorizer.getFieldAuthorizations(document, user);

        // get a reference to the newBo
        PersistableBusinessObject newBo = document.getNewMaintainableObject().getBusinessObject();

        // walk through all the restrictions
        Collection restrictedFields = auths.getAuthFieldNames();
        for (Iterator iter = restrictedFields.iterator(); iter.hasNext();) {
            String fieldName = (String) iter.next();

            // get the specific field authorization structure
            FieldAuthorization fieldAuthorization = auths.getAuthFieldAuthorization(fieldName);

            // if there are any restrictions, then clear the field value
            if (fieldAuthorization.isRestricted()) {

                // get the default value for this field, if any
                Object newValue = null;
                newValue = maintDocDictionaryService.getFieldDefaultValue(newBo.getClass(), fieldName);

                try {
                    ObjectUtils.setObjectProperty(newBo, fieldName, newValue);
                }
                catch (Exception e) {
                    LOG.error("Unable to reset unauthorized field: " + e.getMessage());
                    throw new RuntimeException("Unable to reset unauthorized field: " + e.getMessage());
                }
            }
        }
    }

    /**
     * This method does all special processing on a document that should happen on each HTTP post (ie, save, route, approve, etc).
     * 
     * @param form
     */
    private void doProcessingAfterPost(KualiMaintenanceForm form) {
        MaintenanceDocument document = (MaintenanceDocument) form.getDocument();
        Maintainable maintainable = document.getNewMaintainableObject();
        PersistableBusinessObject bo = maintainable.getBusinessObject();

        KNSServiceLocator.getBusinessObjectService().linkUserFields(bo);
    }

    /**
     * This method updates the version number on the new maintainable object in a maintenance document to the successor of the
     * current saved version of that is in the database.
     * 
     * @param doc the MaintenanceDocument that holds the new BO whose version number needs updating
     */
    private void updateBOVersionNumber(MaintenanceDocument doc) {
        PersistableBusinessObject newBO = doc.getNewMaintainableObject().getBusinessObject();
        // 1. get the PK for this business object
        Map pkValues = KNSServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(newBO);
        // 2. get the current object with that PK
        PersistableBusinessObject currBO = KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(newBO.getClass(), pkValues);
        // 3. set the bo's version number to 1 + curr bo's ver #
        if (currBO != null) {
            newBO.setVersionNumber(new Long(currBO.getVersionNumber().longValue() + 1L));
        }
    }
}