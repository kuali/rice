/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary;

import org.kuali.rice.kns.web.derivedvaluesetter.DerivedValuesSetter;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.ReferenceDefinition;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.exception.ClassValidationException;
import org.kuali.rice.krad.datadictionary.exception.DuplicateEntryException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.krad.document.authorization.DocumentAuthorizer;
import org.kuali.rice.krad.document.authorization.DocumentPresentationController;
import org.kuali.rice.krad.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.rice.kns.maintenance.Maintainable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public class MaintenanceDocumentEntry extends org.kuali.rice.krad.datadictionary.MaintenanceDocumentEntry {
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

    protected Class<? extends DerivedValuesSetter> derivedValuesSetterClass;
    protected List<String> webScriptFiles = new ArrayList<String>(3);
    protected List<HeaderNavigation> headerNavigationList = new ArrayList<HeaderNavigation>();

    protected Class<? extends DocumentAuthorizer> documentAuthorizerClass;
    protected Class<? extends DocumentPresentationController> documentPresentationControllerClass;

    protected boolean sessionDocument = false;

    public MaintenanceDocumentEntry() {
        super();
    }

    @Override
    public Class<? extends Document> getStandardDocumentBaseClass() {
        return MaintenanceDocumentBase.class;
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

    public Class<? extends Maintainable> getMaintainableClass() {
        return (Class<? extends Maintainable>) super.getMaintainableClass();
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
    public List<String> getLockingKeyFieldNames() {
        return lockingKeys;
    }

    /**
     * Gets the allowsNewOrCopy attribute.
     *
     * @return Returns the allowsNewOrCopy.
     */
    public boolean getAllowsNewOrCopy() {
        return allowsNewOrCopy;
    }

    /**
     * The allowsNewOrCopy element contains a value of true or false.
     * If true, this indicates the maintainable should allow the
     * new and/or copy maintenance actions.
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

        for (MaintainableSectionDefinition maintainableSectionDefinition : maintainableSections) {
            maintainableSectionDefinition.completeValidation(getDataObjectClass(), null);
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
     * Client devs should use {@link DocumentTypeService#getDocumentAuthorizer(org.kuali.rice.krad.document.Document)}
     * or
     * {@link DocumentTypeService#getDocumentAuthorizer(String)}
     *
     * @return a document authorizer class
     */
    public Class<? extends DocumentAuthorizer> getDocumentAuthorizerClass() {
        return documentAuthorizerClass;
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
     * The headerNavigation element defines a set of additional
     * tabs which will appear on the document.
     */
    public void setHeaderNavigationList(List<HeaderNavigation> headerNavigationList) {
        this.headerNavigationList = headerNavigationList;
    }

    public boolean isSessionDocument() {
        return this.sessionDocument;
    }

    public void setSessionDocument(boolean sessionDocument) {
        this.sessionDocument = sessionDocument;
    }

    /**
     * Returns the document presentation controller class for the document.  Only framework code should be calling this
     * method.
     * Client devs should use {@link DocumentTypeService#getDocumentPresentationController(org.kuali.rice.krad.document.Document)}
     * or
     * {@link DocumentTypeService#getDocumentPresentationController(String)}
     *
     * @return the documentPresentationControllerClass
     */
    public Class<? extends DocumentPresentationController> getDocumentPresentationControllerClass() {
        return this.documentPresentationControllerClass;
    }

    /**
     * @param documentPresentationControllerClass the documentPresentationControllerClass to set
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
    public void setDerivedValuesSetterClass(Class<? extends DerivedValuesSetter> derivedValuesSetter) {
        this.derivedValuesSetterClass = derivedValuesSetter;
    }
}
