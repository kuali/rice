/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.krad.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.exception.ClassValidationException;
import org.kuali.rice.krad.datadictionary.exception.DuplicateEntryException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.MaintenanceDocumentBase;
import org.kuali.rice.krad.maintenance.Maintainable;

/**
 * MaintenanceDocumentEntry
 * 
 * 
 */
public class MaintenanceDocumentEntry extends DocumentEntry {

    protected Class<? extends BusinessObject> businessObjectClass;
    protected Class<? extends Maintainable> maintainableClass;

    protected List<MaintainableSectionDefinition> maintainableSections = new ArrayList<MaintainableSectionDefinition>();
    protected List<String> lockingKeys = new ArrayList<String>();

    protected Map<String,MaintainableSectionDefinition> maintainableSectionMap = new LinkedHashMap<String, MaintainableSectionDefinition>();

    
    protected boolean allowsNewOrCopy = true;
    protected String additionalSectionsFile;
    
    //for issue KULRice3072, to enable PK field copy
    protected boolean preserveLockingKeysOnCopy = false;
    
    // for issue KULRice3070, to enable deleting a db record using maintenance doc
    protected boolean allowsRecordDeletion = false;
    
    protected boolean translateCodes = false;

	public MaintenanceDocumentEntry() {
        super();
        setDocumentClass(getStandardDocumentBaseClass());
    }

    public Class<? extends Document> getStandardDocumentBaseClass() {
        return MaintenanceDocumentBase.class;
    }

    /*
            This attribute is used in many contexts, for example, in maintenance docs, it's used to specify the classname
            of the BO being maintained.
     */
    public void setBusinessObjectClass(Class<? extends BusinessObject> businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) businessObjectClass");
        }

        this.businessObjectClass = businessObjectClass;
    }

    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return businessObjectClass;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DocumentEntry#getEntryClass()
     */
    @SuppressWarnings("unchecked")
	@Override
    public Class getEntryClass() {
    	return businessObjectClass;
    }

    /*
            The maintainableClass element specifies the name of the
            java class which is responsible for implementing the
            maintenance logic.
            The normal one is KualiMaintainableImpl.java.
     */
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
     * Gets the allowsNewOrCopy attribute. 
     * @return Returns the allowsNewOrCopy.
     */
    public boolean getAllowsNewOrCopy() {
        return allowsNewOrCopy;
    }


    /**
            The allowsNewOrCopy element contains a value of true or false.
            If true, this indicates the maintainable should allow the
            new and/or copy maintenance actions.
     */
    public void setAllowsNewOrCopy(boolean allowsNewOrCopy) {
        this.allowsNewOrCopy = allowsNewOrCopy;
    }


    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.rice.krad.datadictionary.DocumentEntry#completeValidation()
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

        if (documentAuthorizerClass != null && !MaintenanceDocumentAuthorizer.class.isAssignableFrom(documentAuthorizerClass)) {
            throw new ClassValidationException("This maintenance document for '" + businessObjectClass.getName() + "' has an invalid " + "documentAuthorizerClass ('" + documentAuthorizerClass.getName() + "').  " + "Maintenance Documents must use an implementation of MaintenanceDocumentAuthorizer.");
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "MaintenanceDocumentEntry for documentType " + getDocumentTypeName();
    }

    @Deprecated
    public String getAdditionalSectionsFile() {
        return additionalSectionsFile;
    }


    /*
            The additionalSectionsFile element specifies the name of the location
            of an additional JSP file to include in the maintenance document
            after the generation sections but before the notes.
            The location semantics are those of jsp:include.
     */
    @Deprecated
    public void setAdditionalSectionsFile(String additionalSectionsFile) {
        this.additionalSectionsFile = additionalSectionsFile;
    }


    public List<String> getLockingKeys() {
        return lockingKeys;
    }


    /*
            The lockingKeys element specifies a list of fields
            that comprise a unique key.  This is used for record locking
            during the file maintenance process.
     */
    public void setLockingKeys(List<String> lockingKeys) {
        for ( String lockingKey : lockingKeys ) {
            if (lockingKey == null) {
                throw new IllegalArgumentException("invalid (null) lockingKey");
            }
        }
        this.lockingKeys = lockingKeys;
    }


    /**
            The maintainableSections elements allows the maintenance document to
            be presented in sections.  Each section can have a different title.

            JSTL: maintainbleSections is a Map whichis accessed by a key
            of "maintainableSections".  This map contains entries with the
            following keys:
                * "0"   (for first section)
                * "1"   (for second section)
                etc.
            The corresponding value for each entry is a maintainableSection ExportMap.
            See MaintenanceDocumentEntryMapper.java.
     */
    @Deprecated
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


    
    /**
	 * @return the preserveLockingKeysOnCopy
	 */
	public boolean getPreserveLockingKeysOnCopy() {
		return this.preserveLockingKeysOnCopy;
	}

	/**
	 * @param preserveLockingKeysOnCopy the preserveLockingKeysOnCopy to set
	 */
	public void setPreserveLockingKeysOnCopy(boolean preserveLockingKeysOnCopy) {
		this.preserveLockingKeysOnCopy = preserveLockingKeysOnCopy;
	}
	
	/**
	 * @return the allowRecordDeletion
	 */
	public boolean getAllowsRecordDeletion() {
		return this.allowsRecordDeletion;
	}

	/**
	 * @param allowsRecordDeletion the allowRecordDeletion to set
	 */
	public void setAllowsRecordDeletion(boolean allowsRecordDeletion) {
		this.allowsRecordDeletion = allowsRecordDeletion;
	}

	@Deprecated
	public boolean isTranslateCodes() {
		return this.translateCodes;
	}

	@Deprecated
	public void setTranslateCodes(boolean translateCodes) {
		this.translateCodes = translateCodes;
	}

}
