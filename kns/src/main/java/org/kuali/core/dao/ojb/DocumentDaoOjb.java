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
package org.kuali.core.dao.ojb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteWorkgroup;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.dao.BusinessObjectDao;
import org.kuali.core.dao.DocumentDao;
import org.kuali.core.document.Document;
import org.kuali.core.util.OjbCollectionAware;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.dao.DataAccessException;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * This class is the OJB implementation of the DocumentDao interface.
 */
public class DocumentDaoOjb extends PlatformAwareDaoBaseOjb implements DocumentDao, OjbCollectionAware{
    private static Logger LOG = Logger.getLogger(DocumentDaoOjb.class);
    private BusinessObjectDao businessObjectDao;
    

    public DocumentDaoOjb() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.dao.DocumentDao#save(null)
     */
    public void save(Document document) throws DataAccessException {
        Document retrievedDocument = findByDocumentHeaderId(document.getClass(),document.getDocumentNumber());
        KNSServiceLocator.getOjbCollectionHelper().processCollections(this, document, retrievedDocument);
        this.getPersistenceBrokerTemplate().store(document);
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
        Criteria criteria = new Criteria();
        criteria.addIn(PropertyConstants.DOCUMENT_NUMBER, idList);

        QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);
        ArrayList <Document> tempList = new ArrayList(this.getPersistenceBrokerTemplate().getCollectionByQuery(query));
        for (Document doc : tempList) addAdHocs(doc);
        return tempList;
    }

    /**
     * Retrieves a collection of documents with type of given Class, and with the passed status code.
     * 
     * @see org.kuali.core.dao.DocumentDao#findByDocumentHeaderStatusCode(java.lang.Class, java.lang.String)
     */
    public Collection findByDocumentHeaderStatusCode(Class clazz, String statusCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.DOCUMENT_HEADER + "." + PropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, statusCode);

        QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);

        ArrayList <Document> tempList =  new ArrayList(this.getPersistenceBrokerTemplate().getCollectionByQuery(query));
        for (Document doc : tempList) addAdHocs(doc);
        return tempList;
    }

    /**
     * 
     * Deprecated method. Should use BusinessObjectService.linkAndSave() instead.
     * 
     */
    @Deprecated
    public void saveMaintainableBusinessObject(PersistableBusinessObject businessObject) {
        /*
         * this call is to assure all the object fk values are in sync and the fk fields is set in the main object
         */
        KNSServiceLocator.getPersistenceService().linkObjects(businessObject);
        this.getPersistenceBrokerTemplate().store(businessObject);
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