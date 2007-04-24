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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.service.AuthorizationService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentAuthorizationService;
import org.springframework.transaction.annotation.Transactional;


/**
 * Most frequently, isAuthorized(group,action,targetType) will be called from isAuthorized(user,action,target) from inside the loop,
 * so it'd be a good idea to optimize getting an answer for a given group...
 */
@Transactional
public class DocumentAuthorizationServiceImpl implements DocumentAuthorizationService {
    private static Log LOG = LogFactory.getLog(DocumentAuthorizationServiceImpl.class);
    private AuthorizationService authorizationService;
    private DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.core.service.DocumentAuthorizationService#getDocumentAuthorizer(java.lang.String)
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
     * @see org.kuali.core.service.DocumentAuthorizationService#getDocumentAuthorizer(org.kuali.core.document.Document)
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


    /* spring-injected services */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }
}
