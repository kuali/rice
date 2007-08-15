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

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.RiceKeyConstants;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.GlobalBusinessObject;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.rule.event.SaveDocumentEvent;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.KNSServiceLocator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * The maintenance xml structure will be: <maintainableDocumentContents maintainableImplClass="className">
 * <oldMaintainableObject>... </oldMaintainableObject> <newMaintainableObject>... </newMaintainableObject>
 * </maintainableDocumentContents> Maintenance Document
 */
public final class MaintenanceDocumentBase extends DocumentBase implements MaintenanceDocument {
    private static final long serialVersionUID = -505085142412593305L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MaintenanceDocumentBase.class);
    public static final String MAINTAINABLE_IMPL_CLASS = "maintainableImplClass";
    public static final String OLD_MAINTAINABLE_TAG_NAME = "oldMaintainableObject";
    public static final String NEW_MAINTAINABLE_TAG_NAME = "newMaintainableObject";
    public static final String MAINTENANCE_ACTION_TAG_NAME = "maintenanceAction";

    protected Maintainable oldMaintainableObject;
    protected Maintainable newMaintainableObject;
    protected String xmlDocumentContents;
    protected boolean fieldsClearedOnCopy;
    protected boolean displayTopicFieldInNotes = false;

    public MaintenanceDocumentBase() {
        super();
        fieldsClearedOnCopy = false;
    }

    /**
     * Initializies the maintainables.
     */
    public MaintenanceDocumentBase(String documentTypeName) {
        this();
        Class clazz = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getMaintainableClass(documentTypeName);
        try {
            oldMaintainableObject = (Maintainable) clazz.newInstance();
            newMaintainableObject = (Maintainable) clazz.newInstance();

            // initialize maintainable with a business object
            Class boClazz = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getBusinessObjectClass(documentTypeName);
            oldMaintainableObject.setBusinessObject((PersistableBusinessObject) boClazz.newInstance());
            oldMaintainableObject.setBoClass(boClazz);
            newMaintainableObject.setBusinessObject((PersistableBusinessObject) boClazz.newInstance());
            newMaintainableObject.setBoClass(boClazz);
        }
        catch (InstantiationException e) {
            LOG.error("Unable to initialize maintainables of type " + clazz.getName());
            throw new RuntimeException("Unable to initialize maintainables of type " + clazz.getName());
        }
        catch (IllegalAccessException e) {
            LOG.error("Unable to initialize maintainables of type " + clazz.getName());
            throw new RuntimeException("Unable to initialize maintainables of type " + clazz.getName());
        }
    }

    /**
     * Builds out the document title for maintenance documents - this will get loaded into the flex doc and passed into workflow. It
     * will be searchable.
     */
    @Override
    public String getDocumentTitle() {
        String documentTitle = "";
        
        documentTitle = newMaintainableObject.getDocumentTitle(this);
        if (StringUtils.isNotBlank(documentTitle)) {
            // if doc title has been overridden by maintainable, use it
            return documentTitle;
        }
        
        // TODO - build out with bo label once we get the data dictionary stuff in place
        // build out the right classname
        String className = newMaintainableObject.getBusinessObject().getClass().getName();
        String truncatedClassName = className.substring(className.lastIndexOf('.') + 1);
        if (isOldBusinessObjectInDocument()) {
            documentTitle = "Edit ";
        }
        else {
            documentTitle = "New ";
        }
        documentTitle += truncatedClassName + " - ";
        documentTitle += this.getDocumentHeader().getFinancialDocumentDescription() + " ";
        // TODO: talk with Aaron about the getKeysName replacement
        // HashMap keyVals = (HashMap) newMaintainableObject.getKeysNameAndValuePairs();
        // Set list = keyVals.keySet();
        // Iterator i = list.iterator();
        // int idx = 0;
        // while(i.hasNext()) {
        // String key = (String) i.next();
        // String value = (String) keyVals.get(key);
        // if(idx != 0) {
        // documentTitle += ", ";
        // }
        // documentTitle += key;
        // documentTitle += " = ";
        // documentTitle += value;
        // idx++;
        // }
        // documentTitle += " - ";
        // documentTitle += this.getDocumentHeader().getDocumentDescription();
        return documentTitle;
    }

    /**
     * @param xmlDocument
     * @return
     */
    private boolean isOldMaintainableInDocument(Document xmlDocument) {
        boolean isOldMaintainableInExistence = false;
        if (xmlDocument.getElementsByTagName(OLD_MAINTAINABLE_TAG_NAME).getLength() > 0) {
            isOldMaintainableInExistence = true;
        }
        return isOldMaintainableInExistence;
    }


    /**
     * Checks old maintainable bo has key values
     */
    public boolean isOldBusinessObjectInDocument() {
        boolean isOldBusinessObjectInExistence = false;
        if (oldMaintainableObject == null || oldMaintainableObject.getBusinessObject() == null) {
            isOldBusinessObjectInExistence = false;
        }
        else {
            isOldBusinessObjectInExistence = KNSServiceLocator.getPersistenceStructureService().hasPrimaryKeyFieldValues(oldMaintainableObject.getBusinessObject());
        }
        return isOldBusinessObjectInExistence;
    }


    /**
     * This method is a simplified-naming wrapper around isOldBusinessObjectInDocument(), so that the method name matches the
     * functionality.
     */
    public boolean isNew() {
        if (RiceConstants.MAINTENANCE_EDIT_ACTION.equalsIgnoreCase(newMaintainableObject.getMaintenanceAction())) {
            return false;
        }
        else if (RiceConstants.MAINTENANCE_NEWWITHEXISTING_ACTION.equalsIgnoreCase(newMaintainableObject.getMaintenanceAction())) {
            return false;
        }
        else if (RiceConstants.MAINTENANCE_NEW_ACTION.equalsIgnoreCase(newMaintainableObject.getMaintenanceAction())) {
            return true;
        }
        else if (RiceConstants.MAINTENANCE_COPY_ACTION.equalsIgnoreCase(newMaintainableObject.getMaintenanceAction())) {
            return true;
        }
        else {
            return true;
        }
        // return !isOldBusinessObjectInDocument();
    }

    /**
     * This method is a simplified-naming wrapper around isOldBusinessObjectInDocument(), so that the method name matches the
     * functionality.
     */
    public boolean isEdit() {
        if (RiceConstants.MAINTENANCE_EDIT_ACTION.equalsIgnoreCase(newMaintainableObject.getMaintenanceAction())) {
            return true;
        }
        else {
            return false;
        }
        // return isOldBusinessObjectInDocument();
    }

    public boolean isNewWithExisting() {
        if (RiceConstants.MAINTENANCE_NEWWITHEXISTING_ACTION.equalsIgnoreCase(newMaintainableObject.getMaintenanceAction())) {
            return true;
        }
        else {
            return false;
        }
    }

    public void populateMaintainablesFromXmlDocumentContents() {
        // get a hold of the parsed xml document, then read the classname,
        // then instantiate one to two instances depending on content
        // then populate those instances
        if (!StringUtils.isEmpty(xmlDocumentContents)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document xmlDocument = builder.parse(new InputSource(new StringReader(xmlDocumentContents)));
                String clazz = xmlDocument.getDocumentElement().getAttribute(MAINTAINABLE_IMPL_CLASS);
                if (isOldMaintainableInDocument(xmlDocument)) {
                    oldMaintainableObject = (Maintainable) Class.forName(clazz).newInstance();
                    PersistableBusinessObject bo = getBusinessObjectFromXML(OLD_MAINTAINABLE_TAG_NAME);

                    String oldMaintenanceAction = getMaintenanceAction(xmlDocument, OLD_MAINTAINABLE_TAG_NAME);
                    oldMaintainableObject.setMaintenanceAction(oldMaintenanceAction);

                    oldMaintainableObject.setBusinessObject(bo);
                    oldMaintainableObject.setBoClass(bo.getClass());
                }
                newMaintainableObject = (Maintainable) Class.forName(clazz).newInstance();
                PersistableBusinessObject bo = getBusinessObjectFromXML(NEW_MAINTAINABLE_TAG_NAME);
                newMaintainableObject.setBusinessObject(bo);
                newMaintainableObject.setBoClass(bo.getClass());

                String newMaintenanceAction = getMaintenanceAction(xmlDocument, NEW_MAINTAINABLE_TAG_NAME);
                newMaintainableObject.setMaintenanceAction(newMaintenanceAction);

            }
            catch (ParserConfigurationException e) {
                LOG.error("Error while parsing document contents", e);
                throw new RuntimeException("Could not load document contents from xml", e);
            }
            catch (SAXException e) {
                LOG.error("Error while parsing document contents", e);
                throw new RuntimeException("Could not load document contents from xml", e);
            }
            catch (IOException e) {
                LOG.error("Error while parsing document contents", e);
                throw new RuntimeException("Could not load document contents from xml", e);
            }
            catch (InstantiationException e) {
                LOG.error("Error while parsing document contents", e);
                throw new RuntimeException("Could not load document contents from xml", e);
            }
            catch (IllegalAccessException e) {
                LOG.error("Error while parsing document contents", e);
                throw new RuntimeException("Could not load document contents from xml", e);
            }
            catch (ClassNotFoundException e) {
                LOG.error("Error while parsing document contents", e);
                throw new RuntimeException("Could not load document contents from xml", e);
            }

        }
    }

    /**
     * This method is a lame containment of ugly DOM walking code. This is ONLY necessary because of the version conflicts between
     * Xalan.jar in 2.6.x and 2.7. As soon as we can upgrade to 2.7, this will be switched to using XPath, which is faster and much
     * easier on the eyes.
     *
     * @param xmlDocument
     * @param oldOrNewElementName - String oldMaintainableObject or newMaintainableObject
     * @return the value of the element, or null if none was there
     */
    private String getMaintenanceAction(Document xmlDocument, String oldOrNewElementName) {

        if (StringUtils.isBlank(oldOrNewElementName)) {
            throw new IllegalArgumentException("oldOrNewElementName may not be blank, null, or empty-string.");
        }

        String maintenanceAction = null;
        NodeList rootChildren = xmlDocument.getDocumentElement().getChildNodes();
        for (int i = 0; i < rootChildren.getLength(); i++) {
            Node rootChild = rootChildren.item(i);
            if (oldOrNewElementName.equalsIgnoreCase(rootChild.getNodeName())) {
                NodeList maintChildren = rootChild.getChildNodes();
                for (int j = 0; j < maintChildren.getLength(); j++) {
                    Node maintChild = maintChildren.item(j);
                    if (MAINTENANCE_ACTION_TAG_NAME.equalsIgnoreCase(maintChild.getNodeName())) {
                        maintenanceAction = maintChild.getChildNodes().item(0).getNodeValue();
                    }
                }
            }
        }
        return maintenanceAction;
    }

    /**
     * Convenience method to return a SINGLE node from the current document's XML contents, based on the XPath string indicated.
     *
     * @param expression - valid XPath expression
     * @return a populated node if possible, null otherwise
     */
    private Node getXPathNode(String expression) {

        XPath xpath = XPathFactory.newInstance().newXPath();
        InputSource inputSource = new InputSource(new StringReader(xmlDocumentContents));
        Node node;
        try {
            node = (Node) xpath.evaluate(expression, inputSource, XPathConstants.NODE);
        }
        catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
        return node;
    }

    /**
     * Retrieves substring of document contents from maintainable tag name. Then use xml service to translate xml into a business
     * object.
     */
    private PersistableBusinessObject getBusinessObjectFromXML(String maintainableTagName) {
        String maintXml = StringUtils.substringBetween(xmlDocumentContents, "<" + maintainableTagName + ">", "</" + maintainableTagName + ">");
        PersistableBusinessObject businessObject = (PersistableBusinessObject) KNSServiceLocator.getXmlObjectSerializerService().fromXml(maintXml);
        return businessObject;
    }

    /**
     * Populates the xml document contents from the maintainables.
     *
     * @see org.kuali.core.document.MaintenanceDocument#populateXmlDocumentContentsFromMaintainables()
     */
    public void populateXmlDocumentContentsFromMaintainables() {
        StringBuffer docContentBuffer = new StringBuffer();
        docContentBuffer.append("<maintainableDocumentContents maintainableImplClass=\"").append(newMaintainableObject.getClass().getName()).append("\">");
        if (oldMaintainableObject != null && oldMaintainableObject.getBusinessObject() != null) {
            // TODO: refactor this out into a method
            docContentBuffer.append("<" + OLD_MAINTAINABLE_TAG_NAME + ">");

            PersistableBusinessObject oldBo = oldMaintainableObject.getBusinessObject();
            ObjectUtils.materializeAllSubObjects(oldBo); // hack to resolve XStream not dealing well with Proxies
            docContentBuffer.append(KNSServiceLocator.getXmlObjectSerializerService().toXml(oldBo));

            // add the maintainable's maintenanceAction
            docContentBuffer.append("<" + MAINTENANCE_ACTION_TAG_NAME + ">");
            docContentBuffer.append(oldMaintainableObject.getMaintenanceAction());
            docContentBuffer.append("</" + MAINTENANCE_ACTION_TAG_NAME + ">\n");

            docContentBuffer.append("</" + OLD_MAINTAINABLE_TAG_NAME + ">");
        }
        docContentBuffer.append("<" + NEW_MAINTAINABLE_TAG_NAME + ">");

        PersistableBusinessObject newBo = newMaintainableObject.getBusinessObject();
        ObjectUtils.materializeAllSubObjects(newBo); // hack to resolve XStream not dealing well with Proxies
        docContentBuffer.append(KNSServiceLocator.getXmlObjectSerializerService().toXml(newBo));

        // add the maintainable's maintenanceAction
        docContentBuffer.append("<" + MAINTENANCE_ACTION_TAG_NAME + ">");
        docContentBuffer.append(newMaintainableObject.getMaintenanceAction());
        docContentBuffer.append("</" + MAINTENANCE_ACTION_TAG_NAME + ">\n");

        docContentBuffer.append("</" + NEW_MAINTAINABLE_TAG_NAME + ">");
        docContentBuffer.append("</maintainableDocumentContents>");
        xmlDocumentContents = docContentBuffer.toString();
    }

    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();

        KualiWorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();
        getNewMaintainableObject().handleRouteStatusChange(getDocumentHeader());
        // commit the changes to the Maintainable BusinessObject when it goes to Processed (ie, fully approved),
        // and also unlock it
        if (workflowDocument.stateIsProcessed()) {
            String documentNumber = getDocumentHeader().getDocumentNumber();
            newMaintainableObject.setDocumentNumber(documentNumber);
            newMaintainableObject.saveBusinessObject();
            KNSServiceLocator.getMaintenanceDocumentService().deleteLocks(documentNumber);
        }

        // unlock the document when its cancelled or disapproved
        if (workflowDocument.stateIsCanceled() || workflowDocument.stateIsDisapproved()) {
            String documentNumber = getDocumentHeader().getDocumentNumber();
            KNSServiceLocator.getMaintenanceDocumentService().deleteLocks(documentNumber);
        }
    }

    /**
     * Pre-Save hook.
     *
     * @see org.kuali.core.document.Document#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        if (newMaintainableObject != null) {
            newMaintainableObject.prepareForSave();
        }
    }

    /**
     * @see org.kuali.core.document.DocumentBase#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        populateMaintainablesFromXmlDocumentContents();
        if (newMaintainableObject != null) {
            newMaintainableObject.processAfterRetrieve();
        }
    }

    /**
     * @return Returns the newMaintainableObject.
     */
    public Maintainable getNewMaintainableObject() {
        return newMaintainableObject;
    }

    /**
     * @param newMaintainableObject The newMaintainableObject to set.
     */
    public void setNewMaintainableObject(Maintainable newMaintainableObject) {
        this.newMaintainableObject = newMaintainableObject;
    }

    /**
     * @return Returns the oldMaintainableObject.
     */
    public Maintainable getOldMaintainableObject() {
        return oldMaintainableObject;
    }

    /**
     * @param oldMaintainableObject The oldMaintainableObject to set.
     */
    public void setOldMaintainableObject(Maintainable oldMaintainableObject) {
        this.oldMaintainableObject = oldMaintainableObject;
    }

    @Override
    public void setDocumentNumber(String documentNumber) {
        super.setDocumentNumber(documentNumber);

        // set the finDocNumber on the Maintainable
        oldMaintainableObject.setDocumentNumber(documentNumber);
        newMaintainableObject.setDocumentNumber(documentNumber);

    }

    /**
     * Gets the fieldsClearedOnCopy attribute.
     *
     * @return Returns the fieldsClearedOnCopy.
     */
    public final boolean isFieldsClearedOnCopy() {
        return fieldsClearedOnCopy;
    }

    /**
     * Sets the fieldsClearedOnCopy attribute value.
     *
     * @param fieldsClearedOnCopy The fieldsClearedOnCopy to set.
     */
    public final void setFieldsClearedOnCopy(boolean fieldsClearedOnCopy) {
        this.fieldsClearedOnCopy = fieldsClearedOnCopy;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("versionNumber", getVersionNumber());
        m.put("comp", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isCompletionRequested()));
        m.put("app", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isApprovalRequested()));
        m.put("ack", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isAcknowledgeRequested()));
        m.put("fyi", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isFYIRequested()));

        return m;
    }

    /**
     * Gets the xmlDocumentContents attribute.
     *
     * @return Returns the xmlDocumentContents.
     */
    public String getXmlDocumentContents() {
        return xmlDocumentContents;
    }

    /**
     * Sets the xmlDocumentContents attribute value.
     *
     * @param xmlDocumentContents The xmlDocumentContents to set.
     */
    public void setXmlDocumentContents(String xmlDocumentContents) {
        this.xmlDocumentContents = xmlDocumentContents;
    }

    /**
     * @see org.kuali.core.document.Document#getAllowsCopy()
     */
    public boolean getAllowsCopy() {
        Boolean allowsCopy = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getAllowsCopy(this);
        if ( allowsCopy != null ) {
            return allowsCopy.booleanValue();
        }
        else {
            return false;
        }
    }

    /**
     * @see org.kuali.core.document.MaintenanceDocument#getDisplayTopicFieldInNotes()
     */
    public boolean getDisplayTopicFieldInNotes() {
        return displayTopicFieldInNotes;
    }

    /**
     * @see org.kuali.core.document.MaintenanceDocument#setDisplayTopicFieldInNotes(boolean)
     */
    public void setDisplayTopicFieldInNotes(boolean displayTopicFieldInNotes) {
        this.displayTopicFieldInNotes = displayTopicFieldInNotes;
    }

    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        super.prepareForSave(event);
        populateXmlDocumentContentsFromMaintainables();
}
/**
     * Explicitly NOT calling super here.  This is a complete override of the validation 
     * rules behavior.
     * 
     * @see org.kuali.core.document.DocumentBase#validateBusinessRules(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    public void validateBusinessRules(KualiDocumentEvent event) {
        if (!GlobalVariables.getErrorMap().isEmpty()) {
            logErrors();
            throw new ValidationException("errors occured before business rule");
        }

        // check for locking documents for MaintenanceDocuments
        if (this instanceof MaintenanceDocument) {
            checkForLockingDocument();
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
            if (event instanceof SaveDocumentEvent) {
                // for maintenance documents, we want to always actually do a save if the
                // user requests a save, even if there are validation or business rules
                // failures. this empty if does this, and allows the document to be saved,
                // even if there are failures.
                // BR or validation failures on a ROUTE even should always stop the route,
                // that has not changed
            }
            else {
                throw new ValidationException("Unreported errors occured during business rule evaluation (rule developer needs to put meaningful error messages into global ErrorMap)");
            }
        }
        LOG.debug("validation completed");

    }


    private void checkForLockingDocument() {

        LOG.info("starting checkForLockingDocument");

        // get the docHeaderId of the blocking docs, if any are locked and blocking
        String blockingDocId = KNSServiceLocator.getMaintenanceDocumentService().getLockingDocumentId(this);

        // if we got nothing, then no docs are blocking, and we're done
        if (StringUtils.isBlank(blockingDocId)) {
            return;
        }

        LOG.info("Locking document found:  docId = " + blockingDocId + ".");

        // load the blocking locked document
        org.kuali.core.document.Document lockedDocument;
        try {
            lockedDocument = KNSServiceLocator.getDocumentService().getByDocumentHeaderId(blockingDocId);
        }
        catch (WorkflowException e) {
            throw new ValidationException("Could not load the locking document.", e);
        }

        // if we can ignore the lock (see method notes), then exit cause we're done
        if (lockCanBeIgnored(lockedDocument)) {
            return;
        }

        // build the link URL for the blocking document
        Properties parameters = new Properties();
        parameters.put(RiceConstants.DISPATCH_REQUEST_PARAMETER, RiceConstants.DOC_HANDLER_METHOD);
        parameters.put(RiceConstants.PARAMETER_DOC_ID, blockingDocId);
        parameters.put(RiceConstants.PARAMETER_COMMAND, RiceConstants.METHOD_DISPLAY_DOC_SEARCH_VIEW);
        String blockingUrl = UrlFactory.parameterizeUrl(RiceConstants.MAINTENANCE_ACTION, parameters);
        LOG.debug("blockingUrl = '" + blockingUrl + "'");

        // post an error about the locked document
        LOG.debug("Maintenance record: " + lockedDocument.getDocumentHeader().getDocumentNumber() + "is locked.");
        String[] errorParameters = { blockingUrl, blockingDocId };
        GlobalVariables.getErrorMap().putError(RiceConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_MAINTENANCE_LOCKED, errorParameters);

        throw new ValidationException("Maintenance Record is locked by another document.");
    }

    /**
     * This method guesses whether the current user should be allowed to change a document even though it is locked. It probably
     * should use Authorization instead? See KULNRVSYS-948
     * 
     * @param lockedDocument
     * @return
     * @throws WorkflowException
     */
    private boolean lockCanBeIgnored(org.kuali.core.document.Document lockedDocument) {
        // TODO: implement real authorization for Maintenance Document Save/Route - KULNRVSYS-948

        DocumentHeader documentHeader = lockedDocument.getDocumentHeader();

        // get the user-id. if no user-id, then we can do this test, so exit
        String userId = GlobalVariables.getUserSession().getNetworkId().trim();
        if (StringUtils.isBlank(userId)) {
            return false; // dont bypass locking
        }

        // if the current user is not the initiator of the blocking document
        if (!userId.equalsIgnoreCase(documentHeader.getWorkflowDocument().getInitiatorNetworkId().trim())) {
            return false;
        }

        // if the blocking document hasn't been routed, we can ignore it
        return RiceConstants.DocumentStatusCodes.INITIATED.equals(documentHeader.getFinancialDocumentStatusCode());
    }

    /**
     * this needs to happen after the document itself is saved, to preserve consistency of the ver_nbr and in the case of initial
     * save, because this can't be saved until the document is saved initially
     * 
     * @see org.kuali.core.document.DocumentBase#postProcessSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        PersistableBusinessObject bo = getNewMaintainableObject().getBusinessObject();
        if (bo instanceof GlobalBusinessObject) {
            KNSServiceLocator.getBusinessObjectService().save(bo);
        }
        //currently only global documents could change the list of what they're affecting during routing,
        //so could restrict this to only happening with them, but who knows if that will change, so safest
        //to always do the delete and re-add...seems a bit inefficient though if nothing has changed, which is
        //most of the time...could also try to only add/update/delete what's changed, but this is easier
        if (!(event instanceof SaveDocumentEvent)) { //don't lock until they route
            KNSServiceLocator.getMaintenanceDocumentService().deleteLocks(this.getDocumentNumber());
            KNSServiceLocator.getMaintenanceDocumentService().storeLocks(this.getNewMaintainableObject().generateMaintenanceLocks());
        }
    }
    
    /**
     * @see org.kuali.core.document.DocumentBase#getDocumentBusinessObject()
     */
    @Override
    public PersistableBusinessObject getDocumentBusinessObject() {
        if(documentBusinessObject==null) {
            documentBusinessObject=this.newMaintainableObject.getBusinessObject();
        }
        return documentBusinessObject;
    }
}