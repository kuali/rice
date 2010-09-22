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
package org.kuali.rice.kns.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.AdHocRouteWorkgroup;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.dao.BusinessObjectDao;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentAdHocService;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.springframework.dao.DataAccessException;

/**
 * This class is the OJB implementation of the DocumentDao interface.
 */
public class DocumentDaoJpa implements DocumentDao {
    private static Logger LOG = Logger.getLogger(DocumentDaoJpa.class);
    private BusinessObjectDao businessObjectDao;
    private DocumentAdHocService documentAdHocService;

	@PersistenceContext
	private EntityManager entityManager;

    public DocumentDaoJpa(EntityManager entityManager, BusinessObjectDao businessObjectDao, DocumentAdHocService documentAdHocService) {
        super();
        this.entityManager = entityManager;
        this.businessObjectDao = businessObjectDao;
        this.documentAdHocService = documentAdHocService;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kuali.dao.DocumentDao#save(null)
     */
    public void save(Document document) throws DataAccessException {
		Document attachedDoc = findByDocumentHeaderId(document.getClass(),document.getDocumentNumber());
		if (attachedDoc == null) {
			entityManager.persist(document);
		} else {
			OrmUtils.reattach(attachedDoc, document);
			entityManager.merge(attachedDoc);
		}
    }

	/**
     * Retrieve a Document of a specific type with a given document header ID.
     *
     * @param clazz
     * @param id
     * @return Document with given id
     */
    public Document findByDocumentHeaderId(Class clazz, String id) throws DataAccessException {
        List idList = new ArrayList();
        idList.add(id);

        List documentList = findByDocumentHeaderIds(clazz, idList);

        Document document = null;
        if ((null != documentList) && (documentList.size() > 0)) {
            document = (Document) documentList.get(0);
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
    public List findByDocumentHeaderIds(Class clazz, List idList) throws DataAccessException {
		org.kuali.rice.core.jpa.criteria.Criteria criteria = new org.kuali.rice.core.jpa.criteria.Criteria(clazz.getName());
		criteria.in(KNSPropertyConstants.DOCUMENT_NUMBER, (List) idList);
		List<Document> list = new ArrayList<Document>();
		try {
			list = new org.kuali.rice.core.jpa.criteria.QueryByCriteria(entityManager, criteria).toQuery().getResultList();
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
        for (Document doc : list) {
        	documentAdHocService.addAdHocs(doc);
			entityManager.refresh(doc);
        }
		return list;
    }

    /**
     *
     * Deprecated method. Should use BusinessObjectService.linkAndSave() instead.
     *
     */
    @Deprecated
    public void saveMaintainableBusinessObject(PersistableBusinessObject businessObject) {
    	throw new UnsupportedOperationException("Don't use this depricated method.");
    }

    public BusinessObjectDao getBusinessObjectDao() {
        return businessObjectDao;
    }

    public void setBusinessObjectDao(BusinessObjectDao businessObjectDao) {
        this.businessObjectDao = businessObjectDao;
    }

    /**
     * @return the entityManager
     */
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    /**
     * @param entityManager the entityManager to set
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
	 * @return the documentAdHocService
	 */
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
