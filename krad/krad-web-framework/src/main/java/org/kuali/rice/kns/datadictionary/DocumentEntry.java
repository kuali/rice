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

package org.kuali.rice.kns.datadictionary;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.exception.ClassValidationException;
import org.kuali.rice.kns.datadictionary.exception.DuplicateEntryException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.DocumentPresentationController;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.rule.BusinessRule;
import org.kuali.rice.kns.rule.PromptBeforeValidation;
import org.kuali.rice.kns.web.derivedvaluesetter.DerivedValuesSetter;
import org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase;

/**
 * A single Document entry in the DataDictionary, which contains information relating to the display, validation, and general
 * maintenance of a Document (transactional or maintenance) and its attributes.
 * <p/>
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 */
abstract public class DocumentEntry extends DataDictionaryEntryBase {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentEntry.class);

    protected Class<? extends Document> documentClass;
    protected Class<? extends Document> baseDocumentClass;
    protected Class<? extends BusinessRule> businessRulesClass;
    protected Class<? extends PromptBeforeValidation> promptBeforeValidationClass;
    protected Class<? extends DerivedValuesSetter> derivedValuesSetterClass;
    protected String documentTypeName;

    protected boolean allowsNoteAttachments = true;
    protected boolean allowsNoteFYI = false;
    protected Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass;
    protected boolean displayTopicFieldInNotes = false;
    protected boolean usePessimisticLocking = false;
    protected boolean useWorkflowPessimisticLocking = false;
    protected boolean encryptDocumentDataInPersistentSessionStorage = false;

    protected List<String> webScriptFiles = new ArrayList<String>(3);

    protected Class<? extends DocumentAuthorizer> documentAuthorizerClass;
    protected List<HeaderNavigation> headerNavigationList = new ArrayList<HeaderNavigation>();

    protected boolean allowsCopy = false;
    protected WorkflowProperties workflowProperties;
    protected WorkflowAttributes workflowAttributes;

    protected List<ReferenceDefinition> defaultExistenceChecks = new ArrayList<ReferenceDefinition>();
    protected Map<String, ReferenceDefinition> defaultExistenceCheckMap = new LinkedHashMap<String, ReferenceDefinition>();

    protected boolean sessionDocument = false;
    protected Class<? extends DocumentPresentationController> documentPresentationControllerClass;


    /**
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryEntry#getJstlKey()
     */
    public String getJstlKey() {
        return documentTypeName;
    }

    /**
     * The documentClass element is the name of the java class
     * associated with the document.
     */
    public void setDocumentClass(Class<? extends Document> documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("invalid (null) documentClass");
        }

        this.documentClass = documentClass;
    }

    public Class<? extends Document> getDocumentClass() {
        return documentClass;
    }

    /**
     * The optional baseDocumentClass element is the name of the java superclass
     * associated with the document. This gives the data dictionary the ability
     * to index by the superclass in addition to the current class.
     */
    public void setBaseDocumentClass(Class<? extends Document> baseDocumentClass) {
        this.baseDocumentClass = baseDocumentClass;
    }

    public Class<? extends Document> getBaseDocumentClass() {
        return baseDocumentClass;
    }

    /**
     * The businessRulesClass element is the full class name of the java
     * class which contains the business rules for a document.
     */
    public void setBusinessRulesClass(Class<? extends BusinessRule> businessRulesClass) {
        this.businessRulesClass = businessRulesClass;
    }

    public Class<? extends BusinessRule> getBusinessRulesClass() {
        return businessRulesClass;
    }

    /**
     * The documentAuthorizerClass element is the full class name of the
     * java class which will determine what features are available to the
     * user based on the user role and document state.
     */
    public void setDocumentAuthorizerClass(Class<? extends DocumentAuthorizer> documentAuthorizerClass) {
        this.documentAuthorizerClass = documentAuthorizerClass;
    }

    /**
     * Returns the document authorizer class for the document.  Only framework code should be calling this method.
     * Client devs should use {@link DocumentTypeService#getDocumentAuthorizer(Document)} or
     * {@link DocumentTypeService#getDocumentAuthorizer(String)}
     *
     * @return a document authorizer class
     */
    public Class<? extends DocumentAuthorizer> getDocumentAuthorizerClass() {
        return documentAuthorizerClass;
    }

    /**
     * @return Returns the preRulesCheckClass.
     */
    public Class<? extends PromptBeforeValidation> getPromptBeforeValidationClass() {
        return promptBeforeValidationClass;
    }

    /**
     * The promptBeforeValidationClass element is the full class name of the java
     * class which determines whether the user should be asked any questions prior to running validation.
     *
     * @see KualiDocumentActionBase#promptBeforeValidation(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, String)
     */
    public void setPromptBeforeValidationClass(Class<? extends PromptBeforeValidation> preRulesCheckClass) {
        this.promptBeforeValidationClass = preRulesCheckClass;
    }

    /**
     * The documentTypeName element is the name of the document
     * as defined in the workflow system.
     * Example: "AddressTypeMaintenanceDocument"
     */
    public void setDocumentTypeName(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }
        this.documentTypeName = documentTypeName;
    }

    public String getDocumentTypeName() {
        return this.documentTypeName;
    }

    /**
     * Validate common fields for subclass' benefit.
     *
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryEntry#completeValidation()
     */
    public void completeValidation() {
        super.completeValidation();

        if (baseDocumentClass != null && !baseDocumentClass.isAssignableFrom(documentClass)) {
            throw new ClassValidationException("The baseDocumentClass " + baseDocumentClass.getName() +
                    " is not a superclass of the documentClass " + documentClass.getName());
        }

        if (workflowProperties != null && workflowAttributes != null) {
            throw new DataDictionaryException(documentTypeName + ": workflowProperties and workflowAttributes cannot both be defined for a document");
        }
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryEntry#getFullClassName()
     */
    public String getFullClassName() {
        if (getBaseDocumentClass() != null) {
            return getBaseDocumentClass().getName();
        }
        if (getDocumentClass() != null) {
            return getDocumentClass().getName();
        }
        return "";
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryEntryBase#getEntryClass()
     */
    public Class getEntryClass() {
        return getDocumentClass();
    }

    public String toString() {
        return "DocumentEntry for documentType " + documentTypeName;
    }

    /**
     * Accessor method for contained displayTopicFieldInNotes
     *
     * @return displayTopicFieldInNotes boolean
     */
    public boolean getDisplayTopicFieldInNotes() {
        return displayTopicFieldInNotes;
    }

    /**
     * This field contains a value of true or false.
     * If true, then the "Notes and Attachments" tab will render a column for a note topic.
     */
    public void setDisplayTopicFieldInNotes(boolean displayTopicFieldInNotes) {
        this.displayTopicFieldInNotes = displayTopicFieldInNotes;
    }

    /**
     * Accessor method for contained usePessimisticLocking
     *
     * @return usePessimisticLocking boolean
     */
    public boolean getUsePessimisticLocking() {
        return this.usePessimisticLocking;
    }

    /**
     * @param usePessimisticLocking
     */
    public void setUsePessimisticLocking(boolean usePessimisticLocking) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling setUsePessimisticLocking '" + usePessimisticLocking + "'");
        }

        this.usePessimisticLocking = usePessimisticLocking;
    }

    /**
     * Accessor method for contained useWorkflowPessimisticLocking
     *
     * @return useWorkflowPessimisticLocking boolean
     */
    public boolean getUseWorkflowPessimisticLocking() {
        return this.useWorkflowPessimisticLocking;
    }

    /**
     * @param useWorkflowPessimisticLocking
     */
    public void setUseWorkflowPessimisticLocking(boolean useWorkflowPessimisticLocking) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling setuseWorkflowPessimisticLocking '" + useWorkflowPessimisticLocking + "'");
        }

        this.useWorkflowPessimisticLocking = useWorkflowPessimisticLocking;
    }

    /**
     * The attachmentTypesValuesFinderClass specifies the name of a values finder
     * class. This is used to determine the set of file types that are allowed
     * to be attached to the document.
     */
    public void setAttachmentTypesValuesFinderClass(Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass) {
        if (attachmentTypesValuesFinderClass == null) {
            throw new IllegalArgumentException("invalid (null) attachmentTypesValuesFinderClass");
        }

        this.attachmentTypesValuesFinderClass = attachmentTypesValuesFinderClass;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#getKeyValuesFinder()
     */
    public Class<? extends KeyValuesFinder> getAttachmentTypesValuesFinderClass() {
        return attachmentTypesValuesFinderClass;
    }

    /**
     * The allowsCopy element contains a true or false value.
     * If true, then a user is allowed to make a copy of the
     * record using the maintenance screen.
     */
    public void setAllowsCopy(boolean allowsCopy) {
        this.allowsCopy = allowsCopy;
    }

    public boolean getAllowsCopy() {
        return allowsCopy;
    }

    public List<HeaderNavigation> getHeaderNavigationList() {
        return headerNavigationList;
    }

    public List<String> getWebScriptFiles() {
        return webScriptFiles;
    }

    /**
     * The webScriptFile element defines the name of javascript files
     * that are necessary for processing the document.  The specified
     * javascript files will be included in the generated html.
     */
    public void setWebScriptFiles(List<String> webScriptFiles) {
        this.webScriptFiles = webScriptFiles;
    }

    /**
     * @return the allowsNoteAttachments
     */
    public boolean getAllowsNoteAttachments() {
        return this.allowsNoteAttachments;
    }

    /**
     * The allowsNoteAttachments element contains a true or false value.
     * If true, then a document screen includes notes with attachments. Otherwise,
     * only notes is displayed.
     */
    public void setAllowsNoteAttachments(boolean allowsNoteAttachments) {
        this.allowsNoteAttachments = allowsNoteAttachments;
    }

    /**
     * @return the allowsNoteFYI
     */
    public boolean getAllowsNoteFYI() {
        return allowsNoteFYI;
    }

    /**
     * This is an indicator for determining whether to render the AdHoc FYI recipient box and Send FYI button.
     */
    public void setAllowsNoteFYI(boolean allowsNoteFYI) {
        this.allowsNoteFYI = allowsNoteFYI;
    }

    public WorkflowProperties getWorkflowProperties() {
        return this.workflowProperties;
    }

    /**
     * This element is used to define a set of workflowPropertyGroups, which are used to
     * specify which document properties should be serialized during the document serialization
     * process.
     */
    public void setWorkflowProperties(WorkflowProperties workflowProperties) {
        this.workflowProperties = workflowProperties;
    }

    public WorkflowAttributes getWorkflowAttributes() {
        return this.workflowAttributes;
    }

    public void setWorkflowAttributes(WorkflowAttributes workflowAttributes) {
        this.workflowAttributes = workflowAttributes;
    }

    /**
     * The headerNavigation element defines a set of additional
     * tabs which will appear on the document.
     */
    public void setHeaderNavigationList(List<HeaderNavigation> headerNavigationList) {
        this.headerNavigationList = headerNavigationList;
    }

    /**
     * @return List of all defaultExistenceCheck ReferenceDefinitions associated with this MaintenanceDocument, in the order in
     *         which they were added
     */
    public List<ReferenceDefinition> getDefaultExistenceChecks() {
        return defaultExistenceChecks;
    }


    /*
            The defaultExistenceChecks element contains a list of
            reference object names which are required to exist when maintaining a BO.
            Optionally, the reference objects can be required to be active.

            JSTL: defaultExistenceChecks is a Map of Reference elements,
            whose entries are keyed by attributeName
     */
    public void setDefaultExistenceChecks(List<ReferenceDefinition> defaultExistenceChecks) {
        this.defaultExistenceChecks = defaultExistenceChecks;
    }

    /**
     * @return List of all defaultExistenceCheck reference fieldNames associated with this MaintenanceDocument, in the order in
     *         which they were added
     */
    public List<String> getDefaultExistenceCheckFieldNames() {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.addAll(this.defaultExistenceCheckMap.keySet());

        return fieldNames;
    }


    public boolean isSessionDocument() {
        return this.sessionDocument;
    }

    public void setSessionDocument(boolean sessionDocument) {
        this.sessionDocument = sessionDocument;
    }

    /**
     * Returns the document presentation controller class for the document.  Only framework code should be calling this method.
     * Client devs should use {@link DocumentTypeService#getDocumentPresentationController(Document)} or
     * {@link DocumentTypeService#getDocumentPresentationController(String)}
     *
     * @return the documentPresentationControllerClass
     */
    public Class<? extends DocumentPresentationController> getDocumentPresentationControllerClass() {
        return this.documentPresentationControllerClass;
    }

    /**
     * @param documentPresentationControllerClass
     *         the documentPresentationControllerClass to set
     */
    public void setDocumentPresentationControllerClass(
            Class<? extends DocumentPresentationController> documentPresentationControllerClass) {
        this.documentPresentationControllerClass = documentPresentationControllerClass;
    }

    /**
     * @return the derivedValuesSetter
     */
    public Class<? extends DerivedValuesSetter> getDerivedValuesSetterClass() {
        return this.derivedValuesSetterClass;
    }

    /**
     * @param derivedValuesSetter the derivedValuesSetter to set
     */
    public void setDerivedValuesSetterClass(
            Class<? extends DerivedValuesSetter> derivedValuesSetter) {
        this.derivedValuesSetterClass = derivedValuesSetter;
    }

    public boolean isEncryptDocumentDataInPersistentSessionStorage() {
        return this.encryptDocumentDataInPersistentSessionStorage;
    }

    public void setEncryptDocumentDataInPersistentSessionStorage(
            boolean encryptDocumentDataInPersistentSessionStorage) {
        this.encryptDocumentDataInPersistentSessionStorage = encryptDocumentDataInPersistentSessionStorage;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryEntryBase#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (defaultExistenceChecks != null) {
            defaultExistenceCheckMap.clear();
            for (ReferenceDefinition reference : defaultExistenceChecks) {
                if (reference == null) {
                    throw new IllegalArgumentException("invalid (null) defaultExistenceCheck");
                }

                String keyName = reference.isCollectionReference() ? (reference.getCollection() + "." + reference.getAttributeName()) : reference.getAttributeName();
                if (defaultExistenceCheckMap.containsKey(keyName)) {
                    throw new DuplicateEntryException("duplicate defaultExistenceCheck entry for attribute '" + keyName + "'");
                }
                reference.setBusinessObjectClass(getEntryClass());
                defaultExistenceCheckMap.put(keyName, reference);
            }
        }
    }
}
