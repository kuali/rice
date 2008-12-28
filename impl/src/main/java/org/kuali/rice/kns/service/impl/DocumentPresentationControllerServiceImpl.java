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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.datadictionary.TransactionalDocumentEntry;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentPresentationController;
import org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.service.AuthorizationService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentPresentationControllerService;


/**
 * Most frequently, isAuthorized(group,action,targetType) will be called from isAuthorized(user,action,target) from inside the loop,
 * so it'd be a good idea to optimize getting an answer for a given group...
 */
public class DocumentPresentationControllerServiceImpl implements DocumentPresentationControllerService {
    private static Log LOG = LogFactory.getLog(DocumentPresentationControllerServiceImpl.class);
    private AuthorizationService authorizationService;
    private DataDictionaryService dataDictionaryService;

   
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


    /* spring-injected services */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }
}
