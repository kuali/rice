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
package org.kuali.rice.krad.service.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.Maintainable;
import org.kuali.rice.krad.rule.BusinessRule;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

/**
 * Implementation of <code>DocumentDictionaryService</code> which reads configuration
 * from the data dictionary
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentDictionaryServiceImpl implements DocumentDictionaryService {
    private DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getLabel
     */
    @Override
    public String getLabel(String documentTypeName) {
        String label = null;

        DocumentType docType = getDocumentType(documentTypeName);
        if (docType != null) {
            label = docType.getLabel();
        }

        return label;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getMaintenanceDocumentTypeName
     */
    @Override
    public String getMaintenanceDocumentTypeName(Class dataObjectClass) {
        String documentTypeName = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(dataObjectClass);
        if (entry != null) {
            documentTypeName = entry.getDocumentTypeName();
        }

        return documentTypeName;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getDescription
     */
    @Override
    public String getDescription(String documentTypeName) {
        String description = null;

        DocumentType docType = getDocumentType(documentTypeName);
        if (docType != null) {
            description = docType.getDescription();
        }

        return description;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getDefaultExistenceChecks
     */
    @Override
    public Collection getDefaultExistenceChecks(Class dataObjectClass) {
        return getDefaultExistenceChecks(getDocumentTypeName(dataObjectClass));
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getDefaultExistenceChecks
     */
    @Override
    public Collection getDefaultExistenceChecks(Document document) {
        return getDefaultExistenceChecks(getDocumentEntry(document).getDocumentTypeName());
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getDefaultExistenceChecks
     */
    @Override
    public Collection getDefaultExistenceChecks(String docTypeName) {
        Collection defaultExistenceChecks = null;

        DocumentEntry entry = getDocumentEntry(docTypeName);
        if (entry != null) {
            defaultExistenceChecks = entry.getDefaultExistenceChecks();
        }

        return defaultExistenceChecks;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getMaintenanceDataObjectClass
     */
    @Override
    public Class<?> getMaintenanceDataObjectClass(String docTypeName) {
        Class dataObjectClass = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            dataObjectClass = entry.getDataObjectClass();
        }

        return dataObjectClass;
    }

    /**
     * @see org.kuali.rice.krad.service.impl.DocumentDictionaryService#getMaintainableClass
     */
    @Override
    public Class<? extends Maintainable> getMaintainableClass(String docTypeName) {
        Class maintainableClass = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            maintainableClass = entry.getMaintainableClass();
        }

        return maintainableClass;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getBusinessRulesClass
     */
    @Override
    public Class<? extends BusinessRule> getBusinessRulesClass(Document document) {
        Class<? extends BusinessRule> businessRulesClass = null;

        String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
        DocumentEntry entry = getDocumentEntry(docTypeName);
        if (entry != null) {
            businessRulesClass = entry.getBusinessRulesClass();
        }

        return businessRulesClass;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getAllowsCopy
     */
    @Override
    public Boolean getAllowsCopy(Document document) {
        Boolean allowsCopy = Boolean.FALSE;

        if (document == null) {
            return allowsCopy;
        }

        DocumentEntry entry = null;
        if (document instanceof MaintenanceDocument) {
            MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
            if (maintenanceDocument.getNewMaintainableObject() != null) {
                entry = getMaintenanceDocumentEntry(
                        maintenanceDocument.getNewMaintainableObject().getDataObjectClass());
            }
        } else {
            entry = getDocumentEntry(document);
        }

        if (entry != null) {
            allowsCopy = Boolean.valueOf(entry.getAllowsCopy());
        }

        return allowsCopy;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getAllowsNewOrCopy
     */
    @Override
    public Boolean getAllowsNewOrCopy(String docTypeName) {
        Boolean allowsNewOrCopy = Boolean.FALSE;

        if (docTypeName != null) {
            MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
            if (entry != null) {
                allowsNewOrCopy = Boolean.valueOf(entry.getAllowsNewOrCopy());
            }
        }

        return allowsNewOrCopy;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getMaintenanceDocumentEntry
     */
    @Override
    public MaintenanceDocumentEntry getMaintenanceDocumentEntry(String docTypeName) {
        if (StringUtils.isBlank(docTypeName)) {
            throw new IllegalArgumentException("invalid (blank) docTypeName");
        }

        MaintenanceDocumentEntry entry = (MaintenanceDocumentEntry) getDataDictionary().getDocumentEntry(docTypeName);
        return entry;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getDocumentClassByName
     */
    @Override
    public Class<?> getDocumentClassByName(String documentTypeName) {
        Class documentClass = null;

        DocumentEntry entry = getDocumentEntry(documentTypeName);
        if (entry != null) {
            documentClass = entry.getDocumentClass();
        }

        return documentClass;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getAllowsRecordDeletion
     */
    @Override
    public Boolean getAllowsRecordDeletion(Class dataObjectClass) {
        Boolean allowsRecordDeletion = Boolean.FALSE;

        MaintenanceDocumentEntry docEntry = getMaintenanceDocumentEntry(dataObjectClass);

        if (docEntry != null) {
            allowsRecordDeletion = Boolean.valueOf(docEntry.getAllowsRecordDeletion());
        }

        return allowsRecordDeletion;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getAllowsRecordDeletion
     */
    @Override
    public Boolean getAllowsRecordDeletion(MaintenanceDocument document) {
        return document != null ?
                this.getAllowsRecordDeletion(document.getNewMaintainableObject().getDataObjectClass()) : Boolean.FALSE;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getLockingKeys
     */
    @Override
    public List<String> getLockingKeys(String docTypeName) {
        List lockingKeys = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            lockingKeys = entry.getLockingKeyFieldNames();
        }

        return lockingKeys;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getPreserveLockingKeysOnCopy
     */
    @Override
    public boolean getPreserveLockingKeysOnCopy(Class dataObjectClass) {
        boolean preserveLockingKeysOnCopy = false;

        MaintenanceDocumentEntry docEntry = getMaintenanceDocumentEntry(dataObjectClass);

        if (docEntry != null) {
            preserveLockingKeysOnCopy = docEntry.getPreserveLockingKeysOnCopy();
        }

        return preserveLockingKeysOnCopy;
    }

    /**
     * Retrieves the maintenance document entry associated with the given data object class
     *
     * @param dataObjectClass - data object class to retrieve maintenance document entry for
     * @return MaintenanceDocumentEntry for associated data object class
     */
    protected MaintenanceDocumentEntry getMaintenanceDocumentEntry(Class dataObjectClass) {
        if (dataObjectClass == null) {
            throw new IllegalArgumentException("invalid (blank) dataObjectClass");
        }

        MaintenanceDocumentEntry entry =
                getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(dataObjectClass);
        return entry;
    }

    /**
     * Retrieves the document entry for the document type of the given document instance
     *
     * @param document - document instance to retrieve document entry for
     * @return DocumentEntry instance found for document type
     */
    protected DocumentEntry getDocumentEntry(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }

        DocumentEntry entry = getDataDictionary().getDocumentEntry(document.getClass().getName());

        return entry;
    }

    /**
     * Retrieves the document entry for the given document type
     *
     * @param documentTypeName - document type name to retrieve document entry for
     * @return DocumentEntry instance found for document type
     */
    protected DocumentEntry getDocumentEntry(String documentTypeName) {
        if (documentTypeName == null) {
            throw new IllegalArgumentException("invalid (null) document type name");
        }

        DocumentEntry entry = getDataDictionary().getDocumentEntry(documentTypeName);

        return entry;
    }

    /**
     * Gets the workflow document type dto for the given documentTypeName
     *
     * @param documentTypeName - document type name to retrieve document type dto
     * @return DocumentType for given document type name
     */
    protected DocumentType getDocumentType(String documentTypeName) {
        return KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(documentTypeName);
    }

    /**
     * Retrieves the document type name for the maintenance document that is associated with
     * the given data object class
     *
     * @param dataObjectClass - data object class to retrieve document type name for
     * @return String document type name
     */
    protected String getDocumentTypeName(Class dataObjectClass) {
        String documentTypeName = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(dataObjectClass);
        if (entry != null) {
            documentTypeName = entry.getDocumentTypeName();
        }

        return documentTypeName;
    }

    protected DataDictionary getDataDictionary() {
        return getDataDictionaryService().getDataDictionary();
    }

    protected DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            this.dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}
