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
package org.kuali.core.dao.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteWorkgroup;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.dao.BusinessObjectDao;
import org.kuali.core.dao.DocumentDao;
import org.kuali.core.document.Document;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.springframework.dao.DataAccessException;

/**
 * This class is the OJB implementation of the DocumentDao interface.
 */
public class DocumentDaoJpa implements DocumentDao {
    private static Logger LOG = Logger.getLogger(DocumentDaoJpa.class);
    private BusinessObjectDao businessObjectDao;
    
	@PersistenceContext
	private EntityManager entityManager;
	
    public DocumentDaoJpa() {
        super();
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
        	addAdHocs(doc);
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


    public Document addAdHocs(Document document){
        /* Instead of reading the doc header to see if doc is in saved status
         * its probably easier and faster to just do this all the time and
         * store a null when appropriate.
         */
        List adHocRoutePersons;
        List adHocRouteWorkgroups;
        HashMap criteriaPerson = new HashMap();
        HashMap criteriaWorkgroup = new HashMap();
        
        criteriaPerson.put("documentNumber", document.getDocumentNumber());
        criteriaPerson.put("type", AdHocRoutePerson.PERSON_TYPE);
        adHocRoutePersons = (List) businessObjectDao.findMatching(AdHocRoutePerson.class, criteriaPerson);
        criteriaWorkgroup.put("documentNumber", document.getDocumentNumber());
        criteriaWorkgroup.put("type", AdHocRouteWorkgroup.WORKGROUP_TYPE);
        adHocRouteWorkgroups = (List) businessObjectDao.findMatching(AdHocRouteWorkgroup.class, criteriaWorkgroup);
        document.setAdHocRoutePersons(adHocRoutePersons);
        document.setAdHocRouteWorkgroups(adHocRouteWorkgroups);
        
        return document;
    }

    public BusinessObjectDao getBusinessObjectDao() {
        return businessObjectDao;
    }

    public void setBusinessObjectDao(BusinessObjectDao businessObjectDao) {
        this.businessObjectDao = businessObjectDao;
    }

}