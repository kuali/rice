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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.exception.AttributeValidationException;
import org.kuali.core.datadictionary.exception.ClassValidationException;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;
import org.kuali.core.document.MaintenanceDocumentBase;
import org.kuali.core.document.authorization.DocumentAuthorizerBase;
import org.kuali.core.maintenance.Maintainable;

/**
 * MaintenanceDocumentEntry
 * 
 * 
 */
public class MaintenanceDocumentEntry extends DocumentEntry {
    // logger
    //private static Log LOG = LogFactory.getLog(MaintenanceDocumentEntry.class);

    private Class<? extends BusinessObject> businessObjectClass;
    private Class<? extends Maintainable> maintainableClass;

    private List<MaintainableSectionDefinition> maintainableSections = new ArrayList<MaintainableSectionDefinition>();
    private List<String> lockingKeys = new ArrayList<String>();
    private List<ReferenceDefinition> defaultExistenceChecks = new ArrayList<ReferenceDefinition>();
    private List<ApcRuleDefinition> apcRules = new ArrayList<ApcRuleDefinition>();
    private Map<String,MaintainableSectionDefinition> maintainableSectionMap = new LinkedHashMap<String, MaintainableSectionDefinition>();
    private Map<String,ReferenceDefinition> defaultExistenceCheckMap = new LinkedHashMap<String, ReferenceDefinition>();
    private Map<String,ApcRuleDefinition> apcRuleMap = new LinkedHashMap<String, ApcRuleDefinition>();
    
    private boolean allowsNewOrCopy = true;
    private String additionalSectionsFile;

    public MaintenanceDocumentEntry() {
        super();
        setDocumentClass(MaintenanceDocumentBase.class);
    }


    public void setBusinessObjectClass(Class<? extends BusinessObject> businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) businessObjectClass");
        }

        this.businessObjectClass = businessObjectClass;
    }

    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return businessObjectClass;
    }


    public void setMaintainableClass(Class<? extends Maintainable> maintainableClass) {
        if (maintainableClass == null) {
            throw new IllegalArgumentException("invalid (null) maintainableClass");
        }
        this.maintainableClass = maintainableClass;
    }   

    public Class<? extends Maintainable> getMaintainableClass() {
        return maintainableClass;
    }

    /**
     * @return List of MaintainableSectionDefinition objects contained in this document
     */
    public List<MaintainableSectionDefinition> getMaintainableSections() {
        return maintainableSections;
    }

    /**
     * @return List of all lockingKey fieldNames associated with this LookupDefinition, in the order in which they were added
     */
    public List<String> getLockingKeyFieldNames() {
        return lockingKeys;
    }

    /**
     * 
     * @return List of all defaultExistenceCheck ReferenceDefinitions associated with this MaintenanceDocument, in the order in
     *         which they were added
     * 
     */
    public List<ReferenceDefinition> getDefaultExistenceChecks() {
        return defaultExistenceChecks;
    }

    /**
     * 
     * @return List of all defaultExistenceCheck reference fieldNames associated with this MaintenanceDocument, in the order in
     *         which they were added
     * 
     */
    public List<String> getDefaultExistenceCheckFieldNames() {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.addAll(this.defaultExistenceCheckMap.keySet());

        return fieldNames;
    }

    /**
     * 
     * @return List of all apcRule ApcRuleDefinitions associated with this MaintenanceDocument, in the order in which they were
     *         added
     * 
     */
    public List<ApcRuleDefinition> getApcRules() {
        return apcRules;
    }

    /**
     * 
     * @return List of all apcRule rule's fieldNames associated with this MaintenanceDocument, in the order in which they were added
     * 
     */
    public List<String> getApcRuleFieldNames() {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.addAll(this.apcRuleMap.keySet());

        return fieldNames;
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
    public void completeValidation() {
        super.completeValidation();

        for ( MaintainableSectionDefinition maintainableSectionDefinition : maintainableSections ) {
            maintainableSectionDefinition.completeValidation(businessObjectClass, null);
        }

        for ( String lockingKey : lockingKeys ) {
            if (!DataDictionary.isPropertyOf(businessObjectClass, lockingKey)) {
                throw new AttributeValidationException("unable to find attribute '" + lockingKey + "' for lockingKey in businessObjectClass '" + businessObjectClass.getName() );
            }
        }

        for ( ReferenceDefinition reference : defaultExistenceChecks ) {
            reference.completeValidation(businessObjectClass, null);
        }

        for ( ApcRuleDefinition apcRule : apcRules ) {
            apcRule.completeValidation(businessObjectClass, null);
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


    public String getAdditionalSectionsFile() {
        return additionalSectionsFile;
    }


    public void setAdditionalSectionsFile(String additionalSectionsFile) {
        this.additionalSectionsFile = additionalSectionsFile;
    }


    public List<String> getLockingKeys() {
        return lockingKeys;
    }


    public void setLockingKeys(List<String> lockingKeys) {
        for ( String lockingKey : lockingKeys ) {
            if (lockingKey == null) {
                throw new IllegalArgumentException("invalid (null) lockingKey");
            }
        }
        this.lockingKeys = lockingKeys;
    }


    public void setMaintainableSections(List<MaintainableSectionDefinition> maintainableSections) {
        maintainableSectionMap.clear();
        for ( MaintainableSectionDefinition maintainableSectionDefinition : maintainableSections ) {
            if (maintainableSectionDefinition == null) {
                throw new IllegalArgumentException("invalid (null) maintainableSectionDefinition");
            }
    
            String sectionTitle = maintainableSectionDefinition.getTitle();
            if (maintainableSectionMap.containsKey(sectionTitle)) {
                throw new DuplicateEntryException("section '" + sectionTitle + "' already defined for maintenanceDocument '" + getDocumentTypeName() + "'");
            }
    
            maintainableSectionMap.put(sectionTitle, maintainableSectionDefinition);
        }
        this.maintainableSections = maintainableSections;
    }


    public void setDefaultExistenceChecks(List<ReferenceDefinition> defaultExistenceChecks) {
        defaultExistenceCheckMap.clear();
        for ( ReferenceDefinition reference : defaultExistenceChecks  ) {
            if (reference == null) {
                throw new IllegalArgumentException("invalid (null) defaultExistenceCheck");
            }
    
            String keyName = reference.isCollectionReference()? (reference.getCollection()+"."+reference.getAttributeName()):reference.getAttributeName();
            if (defaultExistenceCheckMap.containsKey(keyName)) {
                throw new DuplicateEntryException("duplicate defaultExistenceCheck entry for attribute '" + keyName + "'");
            }
    
            defaultExistenceCheckMap.put(keyName, reference);
        }
        this.defaultExistenceChecks = defaultExistenceChecks;
    }


    public void setApcRules(List<ApcRuleDefinition> apcRules) {
        apcRuleMap.clear();
        for ( ApcRuleDefinition apcRule : apcRules ) {
            if (apcRule == null) {
                throw new IllegalArgumentException("invalid (null) apcRule");
            }
    
            String keyName = apcRule.getAttributeName();
            if (apcRuleMap.containsKey(keyName)) {
                throw new DuplicateEntryException("duplicate apcRule entry for attribute '" + keyName + "'");
            }
    
            apcRuleMap.put(keyName, apcRule);
        }
        this.apcRules = apcRules;
    }

}