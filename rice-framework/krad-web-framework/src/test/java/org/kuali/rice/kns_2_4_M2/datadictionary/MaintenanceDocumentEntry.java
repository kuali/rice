/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns_2_4_M2.datadictionary;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.exception.DuplicateEntryException;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public class MaintenanceDocumentEntry extends org.kuali.rice.krad.datadictionary.MaintenanceDocumentEntry implements KNSDocumentEntry {
    protected List<MaintainableSectionDefinition> maintainableSections = new ArrayList<MaintainableSectionDefinition>();
    protected List<String> lockingKeys = new ArrayList<String>();

    protected Map<String, MaintainableSectionDefinition> maintainableSectionMap =
            new LinkedHashMap<String, MaintainableSectionDefinition>();

    protected boolean allowsNewOrCopy = true;
    protected String additionalSectionsFile;

    //for issue KULRice3072, to enable PK field copy
    protected boolean preserveLockingKeysOnCopy = false;

    // for issue KULRice3070, to enable deleting a db record using maintenance doc
    protected boolean allowsRecordDeletion = false;

    protected boolean translateCodes = false;

    protected List<String> webScriptFiles = new ArrayList<String>(3);
    protected List<HeaderNavigation> headerNavigationList = new ArrayList<HeaderNavigation>();

    protected boolean sessionDocument = false;

    public MaintenanceDocumentEntry() {
        super();

    }

    /*
           This attribute is used in many contexts, for example, in maintenance docs, it's used to specify the classname
           of the BO being maintained.
    */
    public void setBusinessObjectClass(Class<? extends BusinessObject> businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) dataObjectClass");
        }

        setDataObjectClass(businessObjectClass);
    }

    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return (Class<? extends BusinessObject>) getDataObjectClass();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DocumentEntry#getEntryClass()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class getEntryClass() {
        return getDataObjectClass();
    }

    /**
     * @return List of MaintainableSectionDefinition objects contained in this document
     */
    public List<MaintainableSectionDefinition> getMaintainableSections() {
        return maintainableSections;
    }

    /**
     * @return List of all lockingKey fieldNames associated with this LookupDefinition, in the order in which they were
     *         added
     */
    @Override
	public List<String> getLockingKeyFieldNames() {
        return lockingKeys;
    }

    /**
     * Gets the allowsNewOrCopy attribute.
     *
     * @return Returns the allowsNewOrCopy.
     */
    @Override
	public boolean getAllowsNewOrCopy() {
        return allowsNewOrCopy;
    }

    /**
     * The allowsNewOrCopy element contains a value of true or false.
     * If true, this indicates the maintainable should allow the
     * new and/or copy maintenance actions.
     */
    @Override
	public void setAllowsNewOrCopy(boolean allowsNewOrCopy) {
        this.allowsNewOrCopy = allowsNewOrCopy;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
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

    @Override
	public List<String> getLockingKeys() {
        return lockingKeys;
    }

    /*
           The lockingKeys element specifies a list of fields
           that comprise a unique key.  This is used for record locking
           during the file maintenance process.
    */
    @Override
	public void setLockingKeys(List<String> lockingKeys) {
        for (String lockingKey : lockingKeys) {
            if (lockingKey == null) {
                throw new IllegalArgumentException("invalid (null) lockingKey");
            }
        }
        this.lockingKeys = lockingKeys;
    }

    /**
     * The maintainableSections elements allows the maintenance document to
     * be presented in sections.  Each section can have a different title.
     *
     * JSTL: maintainbleSections is a Map whichis accessed by a key
     * of "maintainableSections".  This map contains entries with the
     * following keys:
     * "0"   (for first section)
     * "1"   (for second section)
     * etc.
     * The corresponding value for each entry is a maintainableSection ExportMap.
     * See MaintenanceDocumentEntryMapper.java.
     */
    @Deprecated
    public void setMaintainableSections(List<MaintainableSectionDefinition> maintainableSections) {
        maintainableSectionMap.clear();
        for (MaintainableSectionDefinition maintainableSectionDefinition : maintainableSections) {
            if (maintainableSectionDefinition == null) {
                throw new IllegalArgumentException("invalid (null) maintainableSectionDefinition");
            }

            String sectionTitle = maintainableSectionDefinition.getTitle();
            if (maintainableSectionMap.containsKey(sectionTitle)) {
                throw new DuplicateEntryException(
                        "section '" + sectionTitle + "' already defined for maintenanceDocument '" +
                                getDocumentTypeName() + "'");
            }

            maintainableSectionMap.put(sectionTitle, maintainableSectionDefinition);
        }
        this.maintainableSections = maintainableSections;
    }

    /**
     * @return the preserveLockingKeysOnCopy
     */
    @Override
	public boolean getPreserveLockingKeysOnCopy() {
        return this.preserveLockingKeysOnCopy;
    }

    /**
     * @param preserveLockingKeysOnCopy the preserveLockingKeysOnCopy to set
     */
    @Override
	public void setPreserveLockingKeysOnCopy(boolean preserveLockingKeysOnCopy) {
        this.preserveLockingKeysOnCopy = preserveLockingKeysOnCopy;
    }

    /**
     * @return the allowRecordDeletion
     */
    @Override
	public boolean getAllowsRecordDeletion() {
        return this.allowsRecordDeletion;
    }

    /**
     * @param allowsRecordDeletion the allowRecordDeletion to set
     */
    @Override
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

    @Override
	public List<HeaderNavigation> getHeaderNavigationList() {
        return headerNavigationList;
    }

    @Override
	public List<String> getWebScriptFiles() {
        return webScriptFiles;
    }

    /**
     * The webScriptFile element defines the name of javascript files
     * that are necessary for processing the document.  The specified
     * javascript files will be included in the generated html.
     */
    @Override
	public void setWebScriptFiles(List<String> webScriptFiles) {
        this.webScriptFiles = webScriptFiles;
    }

    /**
     * The headerNavigation element defines a set of additional
     * tabs which will appear on the document.
     */
    @Override
	public void setHeaderNavigationList(List<HeaderNavigation> headerNavigationList) {
        this.headerNavigationList = headerNavigationList;
    }

    @Override
	public boolean isSessionDocument() {
        return this.sessionDocument;
    }

    @Override
	public void setSessionDocument(boolean sessionDocument) {
        this.sessionDocument = sessionDocument;
    }

}
