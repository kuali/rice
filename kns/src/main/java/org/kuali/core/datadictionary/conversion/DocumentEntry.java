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

package org.kuali.core.datadictionary.conversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.exception.ClassValidationException;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.lookup.keyvalues.KeyValuesFinder;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.PreRulesCheck;

/**
 * A single Document entry in the DataDictionary, which contains information relating to the display, validation, and general
 * maintenance of a Document (transactional or maintenance) and its attributes.
 * 
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 * 
 * 
 */
abstract public class DocumentEntry extends DataDictionaryEntryBase {

    private Class<? extends Document> documentClass;
    private Class<? extends BusinessRule> businessRulesClass;
    private Class<? extends PreRulesCheck> preRulesCheckClass;

    private String documentTypeName;
    private String documentTypeCode;

    private String label;
    private String shortLabel;

    private HelpDefinition helpDefinition;

    private boolean allowsNoteDelete = false;
    private boolean allowsNoteAttachments = true;
    private Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass;
    private boolean displayTopicFieldInNotes = false;
    
    private String summary;
    private String description;
    private List<String> webScriptFiles = new ArrayList<String>( 3 );

    protected Class<? extends DocumentAuthorizer> documentAuthorizerClass;
    private List<AuthorizationDefinition> authorizations = new ArrayList<AuthorizationDefinition>();
    private Map<String,AuthorizationDefinition> authorizationMap = new HashMap<String,AuthorizationDefinition>();
    private List<HeaderNavigation> headerNavigationList = new ArrayList<HeaderNavigation>();

    private boolean allowsCopy = false;
    private WorkflowProperties workflowProperties;

    /**
     * @see org.kuali.core.datadictionary.DataDictionaryEntry#getJstlKey()
     */
    public String getJstlKey() {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalStateException("unable to generate JSTL key: documentTypeName is blank");
        }

        return documentTypeName;
    }

    public void setDocumentClass(Class<? extends Document> documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("invalid (null) documentClass");
        }

        this.documentClass = documentClass;
    }

    public Class<? extends Document> getDocumentClass() {
        return documentClass;
    }

    public void setBusinessRulesClass(Class<? extends BusinessRule> businessRulesClass) {
        this.businessRulesClass = businessRulesClass;
    }

    public Class<? extends BusinessRule> getBusinessRulesClass() {
        return businessRulesClass;
    }

    public void setDocumentAuthorizerClass(Class<? extends DocumentAuthorizer> documentAuthorizerClass) {
        this.documentAuthorizerClass = documentAuthorizerClass;
    }

    public Class<? extends DocumentAuthorizer> getDocumentAuthorizerClass() {
        return documentAuthorizerClass;
    }

    /**
     * @return Returns the preRulesCheckClass.
     */
    public Class<? extends PreRulesCheck> getPreRulesCheckClass() {
        return preRulesCheckClass;
    }

    /**
     * @param preRulesCheckClass The preRulesCheckClass to set.
     */
    public void setPreRulesCheckClass(Class<? extends PreRulesCheck> preRulesCheckClass) {
        this.preRulesCheckClass = preRulesCheckClass;
    }

    /**
     * Adds the given AuthorizationDefinition to the authorization map for this documentEntry
     * 
     * @param authorizationDefinition
     */
    public void addAuthorizationDefinition(AuthorizationDefinition authorizationDefinition) {
        String action = authorizationDefinition.getAction();
        if (StringUtils.isBlank(action)) {
            throw new IllegalArgumentException("invalid (blank) action name");
        }

        if (authorizationMap.containsKey(action)) {
            throw new DuplicateEntryException("an authorizationDefinition with action '" + action + "' already exists for this document type");
        }

        authorizations.add(authorizationDefinition);
        authorizationMap.put(action, authorizationDefinition);
    }

    /**
     * 
     * @return
     */
    public List<AuthorizationDefinition> getAuthorizationDefinitions() {
        return authorizations;
    }

    public void setDocumentTypeName(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }
        this.documentTypeName = documentTypeName;
    }

    public String getDocumentTypeName() {
        return this.documentTypeName;
    }

    public void setDocumentTypeCode(String documentTypeCode) {
        if (StringUtils.isBlank(documentTypeCode)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeCode");
        }
        this.documentTypeCode = documentTypeCode;
    }

    public String getDocumentTypeCode() {
        return documentTypeCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        if (StringUtils.isBlank(label)) {
            throw new IllegalArgumentException("invalid (blank) label");
        }
        this.label = label;
    }

    /**
     * @return the shortLabel, or the label if no shortLabel has been set
     */
    public String getShortLabel() {
        return (shortLabel != null) ? shortLabel : label;
    }

    public void setShortLabel(String shortLabel) {
        if (StringUtils.isBlank(shortLabel)) {
            throw new IllegalArgumentException("invalid (blank) shortLabel");
        }
        this.shortLabel = shortLabel;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Validate common fields for subclass' benefit.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryEntry#completeValidation()
     */
    public void completeValidation(ValidationCompletionUtils validationCompletionUtils) {
        super.completeValidation( validationCompletionUtils );
        // TODO: validate documentTypeCode against some external source

        for ( AuthorizationDefinition auth : authorizations ) {
            auth.completeValidation(null, null, validationCompletionUtils);
        }
    }

    /**
     * Validate the required documentAuthorizerClass
     */
    public void validateAuthorizer() {
        if (documentAuthorizerClass == null) {
            throw new ClassValidationException("documentAuthorizerClass is required");
        }
    }

    /**
     * @see org.kuali.core.datadictionary.DataDictionaryEntry#getFullClassName()
     */
    public String getFullClassName() {
    	if ( getDocumentClass() != null ) {
    		return getDocumentClass().getName();
    	}
    	return "";
    }

    /**
     * @see org.kuali.core.datadictionary.DataDictionaryEntryBase#getEntryClass()
     */
    public Class getEntryClass() {
        return getDocumentClass();
    }

    public String toString() {
        return "DocumentEntry for documentType " + documentTypeName;
    }

    /**
     * Accessor method for contained <code>{@link HelpDefinition}</code>
     * 
     * @return helpDefinition
     */
    public HelpDefinition getHelpDefinition() {
        return helpDefinition;
    }

    /**
     * @param helpDefinition
     */
    public void setHelpDefinition(HelpDefinition helpDefinition) {
        this.helpDefinition = helpDefinition;
    }

    /**
     * @param allowsNoteDelete
     */
    public void setAllowsNoteDelete(boolean allowsNoteDelete) {
        this.allowsNoteDelete = allowsNoteDelete;
    }

    /**
     * Accessor method for contained allowsNoteDelete
     * 
     * @return allowsNoteDelete
     */
    public boolean getAllowsNoteDelete() {
        return allowsNoteDelete;
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
     * 
     * This method...
     * @param displayTopicFieldInNotes
     */
    public void setDisplayTopicFieldInNotes(boolean displayTopicFieldInNotes) {
        this.displayTopicFieldInNotes = displayTopicFieldInNotes;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#setKeyValuesFinder(java.lang.String)
     */
    public void setAttachmentTypesValuesFinderClass(Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass) {
        if (attachmentTypesValuesFinderClass == null) {
            throw new IllegalArgumentException("invalid (null) attachmentTypesValuesFinderClass");
        }

        this.attachmentTypesValuesFinderClass = attachmentTypesValuesFinderClass;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#getKeyValuesFinder()
     */
    public Class<? extends KeyValuesFinder> getAttachmentTypesValuesFinderClass() {
        return attachmentTypesValuesFinderClass;
    }

    public void setAllowsCopy(boolean allowsCopy) {
        this.allowsCopy = allowsCopy;
    }

    public boolean getAllowsCopy() {
        return allowsCopy;
    }
    
    public List<HeaderNavigation> getHeaderNavigationList() {
        return headerNavigationList;
    }

    public void addHeaderNavigation(HeaderNavigation headerNavigation) {
        this.headerNavigationList.add(headerNavigation);
    }

    public void addWebScriptFile(String webScriptFile) {
        this.webScriptFiles.add( webScriptFile );
    }

    public List<String> getWebScriptFiles() {
        return webScriptFiles;
    }

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
     * @param allowsNoteAttachments the allowsNoteAttachments to set
     */
    public void setAllowsNoteAttachments(boolean allowsNoteAttachments) {
        this.allowsNoteAttachments = allowsNoteAttachments;
    }

    public WorkflowProperties getWorkflowProperties() {
        return this.workflowProperties;
    }

    public void setWorkflowProperties(WorkflowProperties workflowProperties) {
        this.workflowProperties = workflowProperties;
    }

    public void setHeaderNavigationList(List<HeaderNavigation> headerNavigationList) {
        this.headerNavigationList = headerNavigationList;
    }

    public List<AuthorizationDefinition> getAuthorizations() {
        return this.authorizations;
    }

    public void setAuthorizations(List<AuthorizationDefinition> authorizations) {
        this.authorizations = authorizations;
    }

}