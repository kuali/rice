/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.kns.dao.impl;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.rice.kns.dao.BusinessObjectDao;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentAdHocService;
import org.kuali.rice.kns.service.KNSServiceLocatorInternal;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.OjbCollectionAware;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the OJB implementation of the DocumentDao interface.
 */
public class DocumentDaoOjb extends PlatformAwareDaoBaseOjb implements DocumentDao, OjbCollectionAware{
    private static final Logger LOG = Logger.getLogger(DocumentDaoOjb.class);
    protected BusinessObjectDao businessObjectDao;
    protected DocumentAdHocService documentAdHocService;


    public DocumentDaoOjb(BusinessObjectDao businessObjectDao, DocumentAdHocService documentAdHocService) {
        super();
        this.businessObjectDao = businessObjectDao;
        this.documentAdHocService = documentAdHocService;
    }

    /**
     * @see org.kuali.dao.DocumentDao#save(null)
     */
    @Override
    public <T extends Document> T save(T document) throws DataAccessException {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug( "About to store document: " + document, new Throwable() );
    	}
        Document retrievedDocument = findByDocumentHeaderId(document.getClass(),document.getDocumentNumber());
        KNSServiceLocatorInternal.getOjbCollectionHelper().processCollections(this, document, retrievedDocument);
        this.getPersistenceBrokerTemplate().store(document);
        return document;
    }

    /**
     * Retrieve a Document of a specific type with a given document header ID.
     *
     * @param clazz
     * @param id
     * @return Document with given id
     */
    @Override
    public <T extends Document> T findByDocumentHeaderId(Class<T> clazz, String id) {
        List<String> idList = new ArrayList<String>();
        idList.add(id);

        List<T> documentList = findByDocumentHeaderIds(clazz, idList);

        T document = null;
        if ((null != documentList) && (documentList.size() > 0)) {
            document = documentList.get(0);
        }

        return document;
    }

    /**
     * Retrieve a List of Document instances with the given ids
     *
     * @param clazz
     * @param idList
     * @return List
     */
    @Override
    public <T extends Document> List<T> findByDocumentHeaderIds(Class<T> clazz, List<String> idList) {
        Criteria criteria = new Criteria();
        criteria.addIn(KNSPropertyConstants.DOCUMENT_NUMBER, idList);

        QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);
        
        // this cast is correct because OJB produces a collection which contains elements of the class defined on the query
        @SuppressWarnings("unchecked")
        List<T> tempList = new ArrayList<T>(this.getPersistenceBrokerTemplate().getCollectionByQuery(query));
        
        for (T doc : tempList) {
        	documentAdHocService.addAdHocs(doc);
        }
        return tempList;
    }

    /**
     * Returns the {@link BusinessObjectDao}
     * @see org.kuali.rice.kns.dao.DocumentDao#getBusinessObjectDao()
     * @return the {@link BusinessObjectDao}
     */
    @Override
    public BusinessObjectDao getBusinessObjectDao() {
        return businessObjectDao;
    }

    /**
     * Sets the {@link BusinessObjectDao}
     * @param businessObjectDao ths {@link BusinessObjectDao}
     */
    public void setBusinessObjectDao(BusinessObjectDao businessObjectDao) {
        this.businessObjectDao = businessObjectDao;
    }

    /**
	 * @return the documentAdHocService
	 */
    @Override
	public DocumentAdHocService getDocumentAdHocService() {
		return this.documentAdHocService;
	}

    /**
     * Setter for injecting the DocumentAdHocService
     * @param dahs
     */
    public void setDocumentAdHocService(DocumentAdHocService dahs) {
    	this.documentAdHocService = dahs;
    }
}
