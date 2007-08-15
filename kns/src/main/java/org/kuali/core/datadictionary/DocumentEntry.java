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

package org.kuali.core.datadictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.ClassValidationException;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.lookup.keyvalues.KeyValuesFinder;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiGroupService;

/**
 * A single Document entry in the DataDictionary, which contains information relating to the display, validation, and general
 * maintenance of a Document (transactional or maintenance) and its attributes.
 * 
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 * 
 * 
 */
abstract public class DocumentEntry extends DataDictionaryEntryBase {
    // logger
    private static Log LOG = LogFactory.getLog(DocumentEntry.class);

    private Class documentClass;
    private Class businessRulesClass;
    private Class preRulesCheckClass;

    private String documentTypeName;
    private String documentTypeCode;

    private String label;
    private String shortLabel;

    private HelpDefinition helpDefinition;

    private boolean allowsNoteDelete;
    private Class attachmentTypesValuesFinderClass;
    private boolean displayTopicFieldInNotes;
    
    private String summary;
    private String description;
    private List<String> webScriptFiles;

    protected Class documentAuthorizerClass;
    private Map authorizations;
    private List<HeaderNavigation> headerNavigationList;

    private boolean allowsCopy;

    /**
     * Default constructor
     */
    public DocumentEntry() {
        super();

        LOG.debug("creating new DocumentEntry");
        authorizations = new HashMap();
        headerNavigationList = new ArrayList<HeaderNavigation>();
        webScriptFiles = new ArrayList<String>( 3 );
    }

    /**
     * @see org.kuali.core.datadictionary.DataDictionaryEntry#getJstlKey()
     */
    public String getJstlKey() {
        if (StringUtils.isBlank(this.documentTypeName)) {
            throw new IllegalStateException("unable to generate JSTL key: documentTypeName is blank");
        }

        return this.documentTypeName;
    }

    public void setDocumentClass(Class documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("invalid (null) documentClass");
        }

        this.documentClass = documentClass;
    }

    public Class getDocumentClass() {
        return this.documentClass;
    }

    public void setBusinessRulesClass(Class businessRulesClass) {
        this.businessRulesClass = businessRulesClass;
    }

    public Class getBusinessRulesClass() {
        return businessRulesClass;
    }

    public void setDocumentAuthorizerClass(Class documentAuthorizerClass) {
        this.documentAuthorizerClass = documentAuthorizerClass;
    }

    public Class getDocumentAuthorizerClass() {
        return documentAuthorizerClass;
    }

    /**
     * @return Returns the preRulesCheckClass.
     */
    public Class getPreRulesCheckClass() {
        return preRulesCheckClass;
    }

    /**
     * @param preRulesCheckClass The preRulesCheckClass to set.
     */
    public void setPreRulesCheckClass(Class preRulesCheckClass) {
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

        if (authorizations.containsKey(action)) {
            throw new DuplicateEntryException("an authorizationDefinition with action '" + action + "' already exists for this document type");
        }

        authorizations.put(action, authorizationDefinition);
    }

    /**
     * 
     * @return
     */
    public Map getAuthorizationDefinitions() {
        return Collections.unmodifiableMap(authorizations);
    }

    public void setDocumentTypeName(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }
        LOG.debug("calling setDocumentTypeName '" + documentTypeName + "'");

        this.documentTypeName = documentTypeName;
    }

    public String getDocumentTypeName() {
        return this.documentTypeName;
    }

    public void setDocumentTypeCode(String documentTypeCode) {
        if (StringUtils.isBlank(documentTypeCode)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeCode");
        }
        LOG.debug("calling setDocumentTypeCode '" + documentTypeCode + "'");

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
        LOG.debug("calling setLabel '" + label + "'");

        this.label = label;
    }

    /**
     * @return the shortLabel, or the label if no shortLabel has been set
     */
    public String getShortLabel() {
        return (shortLabel != null) ? shortLabel : getLabel();
    }

    public void setShortLabel(String shortLabel) {
        if (StringUtils.isBlank(shortLabel)) {
            throw new IllegalArgumentException("invalid (blank) shortLabel");
        }
        LOG.debug("calling setShortLabel '" + shortLabel + "'");

        this.shortLabel = shortLabel;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        if (StringUtils.isBlank(summary)) {
            throw new IllegalArgumentException("invalid (blank) summary");
        }
        LOG.debug("calling setSummary '" + summary + "'");

        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("invalid (blank) description");
        }
        LOG.debug("calling setDescription '" + description + "'");

        this.description = description;
    }

    /**
     * Validate common fields for subclass' benefit.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryEntry#completeValidation(java.lang.Object)
     */
    public void completeValidation(ValidationCompletionUtils validationCompletionUtils) {
        super.completeValidation(validationCompletionUtils);
        // TODO: validate documentTypeName against some external source
        // TODO: validate documentTypeCode against some external source

        if (businessRulesClass != null) {
            if (!validationCompletionUtils.isDescendentClass(businessRulesClass, BusinessRule.class)) {
                throw new ClassValidationException("businessRulesClass value '" + businessRulesClass.getName() + "' is not a BusinessRule class");
            }
        }

        if (!authorizations.isEmpty()) {
            for (Iterator i = authorizations.entrySet().iterator(); i.hasNext();) {
                Map.Entry e = (Map.Entry) i.next();

                String action = (String) e.getKey();
                AuthorizationDefinition auth = (AuthorizationDefinition) e.getValue();

                auth.completeValidation(null, null, validationCompletionUtils);
            }
        }

        if (!validationCompletionUtils.isDocumentClass(this.documentClass)) {
            throw new ClassValidationException("documentClass '" + documentClass.getName() + "' is not a Document class");
        }

        if (this.preRulesCheckClass != null && !validationCompletionUtils.isPreRulesCheckClass(this.preRulesCheckClass)) {
            throw new ClassValidationException("class '" + this.preRulesCheckClass + "' is not a PreRulesCheck class");
        }
    }

    /**
     * Perform authorization validation, which requires access to KualiGroupService which isn't available during earlier
     * validation-related methods
     * 
     * @param kualiGroupService
     */
    public void validateAuthorizations(KualiGroupService kualiGroupService) {
        if (!authorizations.isEmpty()) {
            for (Iterator i = authorizations.entrySet().iterator(); i.hasNext();) {
                Map.Entry e = (Map.Entry) i.next();

                String action = (String) e.getKey();
                AuthorizationDefinition auth = (AuthorizationDefinition) e.getValue();

                auth.validateWorkroups(kualiGroupService);
            }
        }
    }

    /**
     * Validate the required documentAuthorizerClass
     * 
     * @param kualiConfigurationService
     */
    public void validateAuthorizer(KualiConfigurationService kualiConfigurationService, ValidationCompletionUtils validationCompletionUtils) {
        if (documentAuthorizerClass == null) {
            throw new ClassValidationException("documentAuthorizerClass is required");
        }
        else {
            
            if (!validationCompletionUtils.isDescendentClass(documentAuthorizerClass, DocumentAuthorizer.class)) {
                throw new ClassValidationException("documentAuthorizerClass value '" + documentAuthorizerClass.getName() + "' is not a DocumentAuthorizer class");
            }
            
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
     * @return HelpDefinition
     */
    public HelpDefinition getHelpDefinition() {
        return helpDefinition;
    }

    /**
     * @param financialSystemParameterHelp
     */
    public void setHelpDefinition(HelpDefinition helpDefinition) {
        this.helpDefinition = helpDefinition;
    }

    /**
     * @param allowsNoteDelete
     */
    public void setAllowsNoteDelete(boolean allowsNoteDelete) {
        LOG.debug("calling setAllowsNoteDelete '" + allowsNoteDelete + "'");

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
        LOG.debug("calling setDisplayTopicFieldInNotes '" + displayTopicFieldInNotes + "'");

        this.displayTopicFieldInNotes = displayTopicFieldInNotes;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#setKeyValuesFinder(java.lang.String)
     */
    public void setAttachmentTypesValuesFinderClass(Class<KeyValuesFinder> attachmentTypesValuesFinderClass) {
        if (attachmentTypesValuesFinderClass == null) {
            throw new IllegalArgumentException("invalid (null) attachmentTypesValuesFinderClass");
        }

        LOG.debug("calling setAttachmentTypesValuesFinderClass '" + attachmentTypesValuesFinderClass.getName() + "'");

        this.attachmentTypesValuesFinderClass = attachmentTypesValuesFinderClass;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#getKeyValuesFinder()
     */
    public Class getAttachmentTypeValuesFinderClass() {
        return attachmentTypesValuesFinderClass;
    }

    public void setAllowsCopy(boolean allowsCopy) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling setAllowsCopy '" + allowsCopy + "'");
        }

        this.allowsCopy = allowsCopy;
    }

    public boolean getAllowsCopy() {
        return allowsCopy;
    }
    
    public HeaderNavigation[] getHeaderTabNavigation() {
        return headerNavigationList.toArray(new HeaderNavigation[headerNavigationList.size()]);
    }

    public void addHeaderNavigation(HeaderNavigation headerNavigation) {
        this.headerNavigationList.add(headerNavigation);
    }

    public void addWebScriptFile(String webScriptFile) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling addWebScriptFile '" + webScriptFile + "'");
        }
        this.webScriptFiles.add( webScriptFile );
    }

    public List<String> getWebScriptFiles() {
        return webScriptFiles;
    }

    public void setWebScriptFiles(List<String> webScriptFiles) {
        this.webScriptFiles = webScriptFiles;
    }

}