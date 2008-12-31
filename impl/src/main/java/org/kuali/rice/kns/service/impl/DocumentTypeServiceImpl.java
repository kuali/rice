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
package org.kuali.rice.kns.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.DocumentType;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.datadictionary.TransactionalDocumentEntry;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.DocumentPresentationController;
import org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.exception.UnknownDocumentTypeException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentTypeService;

/**
 * This class is the service implementation for the DocumentType structure. This is the default implementation, delivered with Kuali
 * which makes use of the DataDictionary related services.
 */
//@Transactional
public class DocumentTypeServiceImpl implements DocumentTypeService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentTypeServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.rice.kns.service.DocumentTypeService#getClassByName(java.lang.String)
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
     * @see org.kuali.rice.kns.service.DocumentTypeService#getDocumentTypeNameByClass(java.lang.Class)
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

    /**
     * This method will throw an {@link UnknownDocumentTypeException} if the document has no document
     * type code.
     * 
     * @see org.kuali.rice.kns.service.DocumentTypeService#getDocumentTypeCodeByClass(java.lang.Class)
     */
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
     * @see org.kuali.rice.kns.service.DocumentTypeService#getDocumentTypeByCode(java.lang.String)
     */
    public DocumentType getDocumentTypeByCode(String documentTypeCode) {
        DocumentType documentType = getPotentialDocumentTypeByCode(documentTypeCode);
        if (documentType == null) {
            LOG.error("Document type code " + documentTypeCode + " is invalid");
            throw new UnknownDocumentTypeException("Document type code " + documentTypeCode + " is invalid");
        }
        return documentType;
    }

    /**
     * @see org.kuali.rice.kns.service.DocumentTypeService#getClassByName(java.lang.String)
     */
    public DocumentType getDocumentTypeByName(String documentTypeName) {
    	String typeCode = getPotentialDocumentTypeCode(documentTypeName);
        if (typeCode == null) {
            throw new UnknownDocumentTypeException("unable to get documentTypeCode for unknown documentTypeName '" + documentTypeName + "'");
        }
        if (StringUtils.isBlank(typeCode)) {
            throw new UnknownDocumentTypeException("blank documentTypeCode for documentTypeName '" + documentTypeName + "'");
        }
        return getDocumentTypeByCode(typeCode);
    }

    public DocumentType getPotentialDocumentTypeByCode(String documentTypeCode) {
        DocumentType documentType = new DocumentType();
        documentType.setDocumentTypeCode(documentTypeCode);
        return (DocumentType) businessObjectService.retrieve(documentType);
    }

    public DocumentType getPotentialDocumentTypeByName(String documentTypeName) {
        return getPotentialDocumentTypeByCode(getPotentialDocumentTypeCode(documentTypeName));
    }
    
    private String getPotentialDocumentTypeCode(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }
        return dataDictionaryService.getDocumentTypeCodeByTypeName(documentTypeName);
    }
    
    /**
     * 
     * @see org.kuali.rice.kns.service.DocumentPresentationControllerService#getDocumentPresentationController(java.lang.String)
     */
    public DocumentPresentationController getDocumentPresentationController(String documentType) {
        DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();
        DocumentPresentationController documentPresentationController = null;
        
        if (StringUtils.isBlank(documentType)) {
            throw new IllegalArgumentException("invalid (blank) documentType");
        }

        DocumentEntry documentEntry = dataDictionary.getDocumentEntry(documentType);
        if (documentEntry == null) {
            throw new IllegalArgumentException("unknown documentType '" + documentType + "'");
        }
        try{
        	Class documentPresentationControllerClass = documentEntry.getDocumentPresentationControllerClass();
        	if(documentPresentationControllerClass != null){
        		documentPresentationController = (DocumentPresentationController) documentPresentationControllerClass.newInstance();
        	}else{
        		DocumentEntry doc = dataDictionary.getDocumentEntry(documentType);
                if ( doc instanceof TransactionalDocumentEntry ) {
                	documentPresentationController = (DocumentPresentationController) (new TransactionalDocumentPresentationControllerBase());
                }else if(doc instanceof MaintenanceDocumentEntry){
                	documentPresentationController = (DocumentPresentationController)  (new MaintenanceDocumentPresentationControllerBase());
                }else{
                	documentPresentationController = new DocumentPresentationControllerBase();
                }
        	}
        }
        catch (Exception e) {
            //throw new RuntimeException("unable to instantiate documentAuthorizer '" + documentPresentationControllerClass.getName() + "' for doctype '" + documentType + "'", e);
        	//use default controller
        	documentPresentationController = new DocumentPresentationControllerBase();
        }
        

        return documentPresentationController;
    }


    /**
     * @see org.kuali.rice.kns.service.DocumentAuthorizationService#getDocumentAuthorizer(org.kuali.rice.kns.document.Document)
     */
    public DocumentPresentationController getDocumentPresentationController(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        else if (document.getDocumentHeader() == null) {
            throw new IllegalArgumentException("invalid (null) document.documentHeader");
        }
        else if (!document.getDocumentHeader().hasWorkflowDocument()) {
            throw new IllegalArgumentException("invalid (null) document.documentHeader.workflowDocument");
        }

        String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentType();

        DocumentPresentationController documentPresentationController = getDocumentPresentationController(documentType);
        return documentPresentationController;
    }

    /**
     * @see org.kuali.rice.kns.service.DocumentAuthorizationService#getDocumentAuthorizer(java.lang.String)
     */
    public DocumentAuthorizer getDocumentAuthorizer(String documentType) {
        DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();

        if (StringUtils.isBlank(documentType)) {
            throw new IllegalArgumentException("invalid (blank) documentType");
        }

        DocumentEntry documentEntry = dataDictionary.getDocumentEntry(documentType);
        if (documentEntry == null) {
            throw new IllegalArgumentException("unknown documentType '" + documentType + "'");
        }

        Class documentAuthorizerClass = documentEntry.getDocumentAuthorizerClass();

        DocumentAuthorizer documentAuthorizer = null;
        try {
            documentAuthorizer = (DocumentAuthorizer) documentAuthorizerClass.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException("unable to instantiate documentAuthorizer '" + documentAuthorizerClass.getName() + "' for doctype '" + documentType + "'", e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("unable to instantiate documentAuthorizer '" + documentAuthorizerClass.getName() + "' for doctype '" + documentType + "'", e);
        }

        return documentAuthorizer;
    }


    /**
     * @see org.kuali.rice.kns.service.DocumentAuthorizationService#getDocumentAuthorizer(org.kuali.rice.kns.document.Document)
     */
    public DocumentAuthorizer getDocumentAuthorizer(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        else if (document.getDocumentHeader() == null) {
            throw new IllegalArgumentException("invalid (null) document.documentHeader");
        }
        else if (!document.getDocumentHeader().hasWorkflowDocument()) {
            throw new IllegalArgumentException("invalid (null) document.documentHeader.workflowDocument");
        }

        String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentType();

        DocumentAuthorizer documentAuthorizer = getDocumentAuthorizer(documentType);
        return documentAuthorizer;
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