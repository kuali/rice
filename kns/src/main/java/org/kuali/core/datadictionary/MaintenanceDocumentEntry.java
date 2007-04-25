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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.ClassValidationException;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;
import org.kuali.core.document.MaintenanceDocumentBase;
import org.kuali.core.document.authorization.DocumentAuthorizerBase;

/**
 * MaintenanceDocumentEntry
 * 
 * 
 */
public class MaintenanceDocumentEntry extends DocumentEntry {
    // logger
    private static Log LOG = LogFactory.getLog(MaintenanceDocumentEntry.class);

    private Class businessObjectClass;
    private Class maintainableClass;

    private Map maintainableSections;
    private Map lockingKeys;
    private Map defaultExistenceChecks;
    private Map apcRules;
    
    private boolean allowsNewOrCopy;

    public MaintenanceDocumentEntry() {
        super();

        LOG.debug("creating new MaintenanceDocumentEntry");
        maintainableSections = new LinkedHashMap();
        lockingKeys = new LinkedHashMap();
        defaultExistenceChecks = new LinkedHashMap();
        apcRules = new LinkedHashMap();
        allowsNewOrCopy = true;
        super.setDocumentClass(MaintenanceDocumentBase.class);
    }


    public void setBusinessObjectClass(Class businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) businessObjectClass");
        }
        LOG.debug("calling setBusinessObjectClass '" + businessObjectClass.getName() + "'");

        this.businessObjectClass = businessObjectClass;
    }

    public Class getBusinessObjectClass() {
        return businessObjectClass;
    }


    public void setMaintainableClass(Class maintainableClass) {
        if (maintainableClass == null) {
            throw new IllegalArgumentException("invalid (null) maintainableClass");
        }
        LOG.debug("calling setMaintainableClass '" + maintainableClass.getName() + "'");

        this.maintainableClass = maintainableClass;
    }

    public Class getMaintainableClass() {
        return maintainableClass;
    }
    
    /**
     * Adds the given maintainableSection to the current collection of sections defined for this MaintenanceDocumentEntry
     * 
     * @param maintainableSectionDefinition
     */
    public void addMaintainableSection(MaintainableSectionDefinition maintainableSectionDefinition) {
        if (maintainableSectionDefinition == null) {
            throw new IllegalArgumentException("invalid (null) maintainableSectionDefinition");
        }

        String sectionTitle = maintainableSectionDefinition.getTitle();
        if (maintainableSections.containsKey(sectionTitle)) {
            throw new DuplicateEntryException("section '" + sectionTitle + "' already defined for maintenanceDocument '" + getDocumentTypeName() + "'");
        }

        maintainableSections.put(sectionTitle, maintainableSectionDefinition);
    }

    /**
     * @return List of MaintainableSectionDefinition objects contained in this document
     */
    public List getMaintainableSections() {
        List sectionList = new ArrayList();

        sectionList.addAll(this.maintainableSections.values());

        return Collections.unmodifiableList(sectionList);
    }

    /**
     * @param lockingKey
     * @throws IllegalArgumentException if the given lockingKey is null
     */
    public void addLockingKey(FieldDefinition lockingKey) {
        if (lockingKey == null) {
            throw new IllegalArgumentException("invalid (null) lockingKey");
        }
        LOG.debug("calling addLockingKey for field '" + lockingKey.getAttributeName() + "'");

        String keyName = lockingKey.getAttributeName();
        if (this.lockingKeys.containsKey(keyName)) {
            throw new DuplicateEntryException("duplicate returnKey entry for attribute '" + keyName + "'");
        }

        this.lockingKeys.put(keyName, lockingKey);
    }

    /**
     * @return List of all lockingKey fieldNames associated with this LookupDefinition, in the order in which they were added
     */
    public List getLockingKeyFieldnames() {
        List fieldNames = new ArrayList();
        fieldNames.addAll(this.lockingKeys.keySet());

        return Collections.unmodifiableList(fieldNames);
    }

    /**
     * @return Collection of all lockingKey FieldDefinitions associated with this LookupDefinition, in the order in which they were
     *         added
     */
    public List getLockingKeys() {
        List keyList = new ArrayList();

        keyList.addAll(this.lockingKeys.values());

        return Collections.unmodifiableList(keyList);
    }

    /**
     * 
     * Adds a new defaultExistenceCheck (ReferenceDefinition) to this MaintDoc
     * 
     * @param reference
     * @throws IllegalArgumentException if the given reference is null
     */
    public void addDefaultExistenceCheck(ReferenceDefinition reference) {

        if (reference == null) {
            throw new IllegalArgumentException("invalid (null) reference");
        }
        LOG.debug("calling addDefaultExistenceCheck for field '" + reference.getAttributeName() + "'");

        String keyName = reference.isCollectionReference()? (reference.getCollection()+"."+reference.getAttributeName()):reference.getAttributeName();
        if (this.defaultExistenceChecks.containsKey(keyName)) {
            throw new DuplicateEntryException("duplicate defaultExistenceCheck entry for attribute '" + keyName + "'");
        }

        this.defaultExistenceChecks.put(keyName, reference);
    }

    /**
     * 
     * @return List of all defaultExistenceCheck ReferenceDefinitions associated with this MaintenanceDocument, in the order in
     *         which they were added
     * 
     */
    public List getDefaultExistenceChecks() {
        List referenceList = new ArrayList();

        referenceList.addAll(this.defaultExistenceChecks.values());

        return Collections.unmodifiableList(referenceList);
    }

    /**
     * 
     * @return List of all defaultExistenceCheck reference fieldNames associated with this MaintenanceDocument, in the order in
     *         which they were added
     * 
     */
    public List getDefaultExistenceCheckFieldNames() {
        List fieldNames = new ArrayList();
        fieldNames.addAll(this.defaultExistenceChecks.keySet());

        return Collections.unmodifiableList(fieldNames);
    }

    /**
     * Adds a new apcRule (ApcRuleDefinition) to this class
     * 
     * @param apcRule
     */
    public void addApcRule(ApcRuleDefinition apcRule) {

        if (apcRule == null) {
            throw new IllegalArgumentException("invalid (null) apcRule");
        }
        LOG.debug("calling addApcRule for field '" + apcRule.getAttributeName() + "'");

        String keyName = apcRule.getAttributeName();
        if (this.apcRules.containsKey(keyName)) {
            throw new DuplicateEntryException("duplicate apcRule entry for attribute '" + keyName + "'");
        }

        this.apcRules.put(keyName, apcRule);
    }

    /**
     * 
     * @return List of all apcRule ApcRuleDefinitions associated with this MaintenanceDocument, in the order in which they were
     *         added
     * 
     */
    public List getApcRules() {
        List rulesList = new ArrayList();

        rulesList.addAll(this.apcRules.values());

        return Collections.unmodifiableList(rulesList);
    }

    /**
     * 
     * @return List of all apcRule rule's fieldNames associated with this MaintenanceDocument, in the order in which they were added
     * 
     */
    public List getApcRuleFieldNames() {
        List fieldNames = new ArrayList();
        fieldNames.addAll(this.apcRules.keySet());

        return Collections.unmodifiableList(fieldNames);
    }
    

    /**
     * Gets the allowsNewOrCopy attribute. 
     * @return Returns the allowsNewOrCopy.
     */
    public boolean getAllowsNewOrCopy() {
        return allowsNewOrCopy;
    }


    /**
     * Sets the allowsNewOrCopy attribute value.
     * @param allowsNewOrCopy The allowsNewOrCopy to set.
     */
    public void setAllowsNewOrCopy(boolean allowsNewOrCopy) {
        this.allowsNewOrCopy = allowsNewOrCopy;
    }


    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.core.datadictionary.DocumentEntry#completeValidation()
     */
    public void completeValidation(ValidationCompletionUtils validationCompletionUtils) {
        super.completeValidation(validationCompletionUtils);

        if (!validationCompletionUtils.isBusinessObjectClass(businessObjectClass)) {
            throw new ClassValidationException("businessObjectClass '" + businessObjectClass.getName() + "' is not a BusinessObject class");
        }

        if (!validationCompletionUtils.isMaintainableClass(maintainableClass)) {
            throw new ClassValidationException("maintainableClasss '" + maintainableClass.getName() + "' is not a Maintainable class");
        }

        for (Iterator i = maintainableSections.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            MaintainableSectionDefinition maintainableSectionDefinition = (MaintainableSectionDefinition) e.getValue();
            maintainableSectionDefinition.completeValidation(businessObjectClass, null, validationCompletionUtils);
        }

        for (Iterator i = lockingKeys.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            FieldDefinition returnKey = (FieldDefinition) e.getValue();
            returnKey.completeValidation(businessObjectClass, null, validationCompletionUtils);
        }

        for (Iterator i = defaultExistenceChecks.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            ReferenceDefinition reference = (ReferenceDefinition) e.getValue();
            reference.completeValidation(businessObjectClass, null, validationCompletionUtils);
        }

        for (Iterator i = apcRules.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            ApcRuleDefinition apcRule = (ApcRuleDefinition) e.getValue();
            apcRule.completeValidation(businessObjectClass, null, validationCompletionUtils);
        }

        // its never okay for a MaintenanceDocument.xml file to have the
        // DocumentAuthorizerBase class as its documentAuthorizerClass.
        if (documentAuthorizerClass.equals(DocumentAuthorizerBase.class)) {
            throw new ClassValidationException("This maintenance document for '" + businessObjectClass.getName() + "' has an invalid " + "documentAuthorizerClass ('" + documentAuthorizerClass.getName() + "').  " + "Maintenance Documents cannot use the " + "DocumentAuthorizerBase class for their documentAuthorizerClass property.  " + "They must use MaintenanceDocumentAuthorizerBase, or one of its subclasses.");
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "MaintenanceDocumentEntry for documentType " + getDocumentTypeName();
    }

}