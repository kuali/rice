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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.UnknownDocumentTypeException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentTypeService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for the DocumentType structure. This is the default implementation, delivered with Kuali
 * which makes use of the DataDictionary related services.
 */
@Transactional
public class DocumentTypeServiceImpl implements DocumentTypeService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentTypeServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.core.service.DocumentTypeService#getClassByName(java.lang.String)
     */
    public Class getClassByName(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }

        Class clazz = dataDictionaryService.getDocumentClassByTypeName(documentTypeName);
        if (clazz == null) {
            throw new UnknownDocumentTypeException("unable to get class for unknown documentTypeName '" + documentTypeName + "'");
        }
        return clazz;
    }

    /**
     * @see org.kuali.core.service.DocumentTypeService#getDocumentTypeNameByClass(java.lang.Class)
     */
    public String getDocumentTypeNameByClass(Class documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("invalid (null) documentClass");
        }
        if (!Document.class.isAssignableFrom(documentClass)) {
            throw new IllegalArgumentException("invalid (non-Document) documentClass");
        }

        String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(documentClass);
        if (StringUtils.isBlank(documentTypeName)) {
            throw new UnknownDocumentTypeException("unable to get documentTypeName for unknown documentClass '" + documentClass.getName() + "'");
        }
        return documentTypeName;
    }

    public String getDocumentTypeCodeByClass(Class documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("invalid (null) documentClass");
        }
        if (!Document.class.isAssignableFrom(documentClass)) {
            throw new IllegalArgumentException("invalid (non-Document) documentClass");
        }

        DocumentEntry documentEntry = dataDictionaryService.getDataDictionary().getDocumentEntry(documentClass.getName());
        if (null == documentEntry) {
            throw new UnknownDocumentTypeException("unable to get documentTypeCode for unknown documentClass '" + documentClass.getName() + "'");
        }

        String documentTypeCode = documentEntry.getDocumentTypeCode();
        if (StringUtils.isBlank(documentTypeCode)) {
            throw new UnknownDocumentTypeException("unable to get documentTypeCode for unknown documentClass '" + documentClass.getName() + "'");
        }
        return documentTypeCode;
    }

    /**
     * @see org.kuali.core.service.DocumentTypeService#getDocumentTypeByCode(java.lang.String)
     */
    public DocumentType getDocumentTypeByCode(String documentTypeCode) {
        DocumentType documentType = new DocumentType();
        documentType.setFinancialDocumentTypeCode(documentTypeCode);
        documentType = (DocumentType) businessObjectService.retrieve(documentType);
        if (documentType == null) {
            LOG.error("Document type code " + documentTypeCode + " is invalid");
            throw new UnknownDocumentTypeException("Document type code " + documentTypeCode + " is invalid");
        }
        return documentType;
    }

    /**
     * @see org.kuali.core.service.DocumentTypeService#getClassByName(java.lang.String)
     */
    public DocumentType getDocumentTypeByName(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }
        String typeCode = dataDictionaryService.getDocumentTypeCodeByTypeName(documentTypeName);
        if (typeCode == null) {
            throw new UnknownDocumentTypeException("unable to get documentTypeCode for unknown documentTypeName '" + documentTypeName + "'");
        }
        if (StringUtils.isBlank(typeCode)) {
            throw new UnknownDocumentTypeException("blank documentTypeCode for documentTypeName '" + documentTypeName + "'");
        }
        return getDocumentTypeByCode(typeCode);
    }

    /**
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @return dataDictionaryService
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * @param dataDictionaryService
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}