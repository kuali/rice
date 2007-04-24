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
package org.kuali.core.service.impl;

import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.TransactionalDocumentEntry;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.TransactionalDocumentDictionaryService;

/**
 * This class is the service implementation for the TransactionalDocumentDictionary structure. Defines the API for the interacting
 * with Document-related entries in the data dictionary. This is the default implementation that gets delivered with Kuali.
 */
public class TransactionalDocumentDictionaryServiceImpl implements TransactionalDocumentDictionaryService {
    private DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.core.service.TransactionalDocumentDictionaryService#getAllowsCopy(org.kuali.bo.TransactionalDocument)
     */
    public Boolean getAllowsCopy(TransactionalDocument document) {
        Boolean allowsCopy = null;

        TransactionalDocumentEntry entry = getTransactionalDocumentEntry(document);
        if (entry != null) {
            allowsCopy = Boolean.valueOf(entry.getAllowsCopy());
        }

        return allowsCopy;
    }

    /**
     * @see org.kuali.core.service.TransactionalDocumentDictionaryService#getDocumentClassByName(java.lang.String)
     */
    public Class getDocumentClassByName(String documentTypeName) {
        Class documentClass = null;

        TransactionalDocumentEntry entry = getTransactionalDocumentEntryBydocumentTypeName(documentTypeName);
        if (entry != null) {
            documentClass = entry.getDocumentClass();
        }

        return documentClass;
    }

    /**
     * @see org.kuali.core.service.TransactionalDocumentDictionaryService#getAllowsErrorCorrection(org.kuali.bo.TransactionalDocument)
     */
    public Boolean getAllowsErrorCorrection(TransactionalDocument document) {
        Boolean allowsErrorCorrections = null;

        TransactionalDocumentEntry entry = getTransactionalDocumentEntry(document);
        if (entry != null) {
            allowsErrorCorrections = Boolean.valueOf(entry.getAllowsErrorCorrection());
        }

        return allowsErrorCorrections;
    }

    /**
     * @see org.kuali.core.service.TransactionalDocumentDictionaryService#getSummary(org.kuali.bo.TransactionalDocument)
     */
    public String getSummary(String transactionalDocumentTypeName) {
        String summary = null;

        TransactionalDocumentEntry entry = getTransactionalDocumentEntryBydocumentTypeName(transactionalDocumentTypeName);
        if (entry != null) {
            summary = String.valueOf(entry.getSummary());
        }

        return summary;
    }

    /**
     * @see org.kuali.core.service.TransactionalDocumentDictionaryService#getDescription(org.kuali.bo.TransactionalDocument)
     */
    public String getDescription(String transactionalDocumentTypeName) {
        String description = null;

        TransactionalDocumentEntry entry = getTransactionalDocumentEntryBydocumentTypeName(transactionalDocumentTypeName);
        if (entry != null) {
            description = String.valueOf(entry.getDescription());
        }

        return description;
    }

    /**
     * @see org.kuali.core.service.TransactionalDocumentDictionaryService#getDescription(org.kuali.bo.TransactionalDocument)
     */
    public String getLabel(String transactionalDocumentTypeName) {
        String label = null;

        TransactionalDocumentEntry entry = getTransactionalDocumentEntryBydocumentTypeName(transactionalDocumentTypeName);
        if (entry != null) {
            label = String.valueOf(entry.getLabel());
        }

        return label;
    }

    /**
     * @see org.kuali.core.service.TransactionalDocumentDictionaryService#getBusinessRulesClass(org.kuali.bo.TransactionalDocument)
     */
    public Class getBusinessRulesClass(TransactionalDocument document) {
        Class businessRulesClass = null;

        //TransactionalDocumentEntry entry = getTransactionalDocumentEntry(document);
        String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
        TransactionalDocumentEntry entry = getTransactionalDocumentEntryBydocumentTypeName(docTypeName);
        if (entry != null) {
            businessRulesClass = getDataDictionary().getBusinessRulesClass(entry.getDocumentTypeName());
        }

        return businessRulesClass;
    }

    /**
     * Sets the data dictionary instance.
     * 
     * @param dataDictionaryService
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Retrieves the data dictionary instance.
     * 
     * @return
     */
    public DataDictionary getDataDictionary() {
        return this.dataDictionaryService.getDataDictionary();
    }

    /**
     * Retrieves the document entry by transactional document class instance.
     * 
     * @param document
     * @return TransactionalDocumentEntry
     */
    private TransactionalDocumentEntry getTransactionalDocumentEntry(TransactionalDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }

        TransactionalDocumentEntry entry = getDataDictionary().getTransactionalDocumentEntry(document.getClass());

        return entry;
    }

    /**
     * Retrieves the document entry by transactional document type name.
     * 
     * @param documentTypeName
     * @return
     */
    private TransactionalDocumentEntry getTransactionalDocumentEntryBydocumentTypeName(String documentTypeName) {
        if (documentTypeName == null) {
            throw new IllegalArgumentException("invalid (null) document type name");
        }

        TransactionalDocumentEntry entry = getDataDictionary().getTransactionalDocumentEntry(documentTypeName);

        return entry;
    }
}