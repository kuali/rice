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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.dao.BusinessObjectDao;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.OjbCollectionAware;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.dao.DataAccessException;

/**
 * This class is the OJB implementation of the BusinessObjectDao interface and should be used for generic business object unit
 * tests.
 */
public class BusinessObjectDaoOjb extends PlatformAwareDaoBaseOjb implements BusinessObjectDao, OjbCollectionAware {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BusinessObjectDaoOjb.class);

    private PersistenceStructureService persistenceStructureService;
    /**
     * @see org.kuali.core.dao.BusinessObjectDao#findByPrimaryKey(java.lang.Class, java.util.Map)
     */
    public PersistableBusinessObject findByPrimaryKey(Class clazz, Map primaryKeys) {
        Criteria criteria = buildCriteria(primaryKeys);

        return (PersistableBusinessObject) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(clazz, criteria));
    }

    /**
     * Retrieves all of the records for a given class name.
     * 
     * @param clazz - the name of the object being used, either KualiCodeBase or a subclass
     * @return Collection
     * @see org.kuali.core.dao.BusinessObjectDao#findAll(java.lang.Class)
     */
    public Collection findAll(Class clazz) {
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(clazz, (Criteria) null));
    }

    /**
     * @see org.kuali.core.dao.BusinessObjectDao#findAllOrderBy(java.lang.Class, java.lang.String, boolean)
     */
    public Collection findAllOrderBy(Class clazz, String sortField, boolean sortAscending) {
        QueryByCriteria queryByCriteria = new QueryByCriteria(clazz, (Criteria) null);

        if (sortAscending) {
            queryByCriteria.addOrderByAscending(sortField);
        }
        else {
            queryByCriteria.addOrderByDescending(sortField);
        }

        return getPersistenceBrokerTemplate().getCollectionByQuery(queryByCriteria);
    }

    /**
     * This is the default impl that comes with Kuali - uses OJB.
     * 
     * @see org.kuali.core.dao.BusinessObjectDao#findMatching(java.lang.Class, java.util.Map)
     */
    public Collection findMatching(Class clazz, Map fieldValues) {
        Criteria criteria = buildCriteria(fieldValues);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(clazz, criteria));
    }
    

    /**
     * @see org.kuali.core.dao.BusinessObjectDao#findAllActive(java.lang.Class)
     */
    public Collection findAllActive(Class clazz) {
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(clazz, buildActiveCriteria()));
    }

    /**
     * @see org.kuali.core.dao.BusinessObjectDao#findAllActiveOrderBy(java.lang.Class, java.lang.String, boolean)
     */
    public Collection findAllActiveOrderBy(Class clazz, String sortField, boolean sortAscending) {
        QueryByCriteria queryByCriteria = new QueryByCriteria(clazz, buildActiveCriteria());

        if (sortAscending) {
            queryByCriteria.addOrderByAscending(sortField);
        }
        else {
            queryByCriteria.addOrderByDescending(sortField);
        }

        return getPersistenceBrokerTemplate().getCollectionByQuery(queryByCriteria);
    }

    /**
     * @see org.kuali.core.dao.BusinessObjectDao#findMatchingActive(java.lang.Class, java.util.Map)
     */
    public Collection findMatchingActive(Class clazz, Map fieldValues) {
        Criteria criteria = buildCriteria(fieldValues);
        criteria.addAndCriteria(buildActiveCriteria());
        
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(clazz, criteria));
    }

    /**
     * This is the default impl that comes with Kuali - uses OJB.
     * 
     * @see org.kuali.core.dao.BusinessObjectDao#countMatching(java.lang.Class, java.util.Map)
     */
    public int countMatching(Class clazz, Map fieldValues) {
        Criteria criteria = buildCriteria(fieldValues);

        return getPersistenceBrokerTemplate().getCount(QueryFactory.newQuery(clazz, criteria));
    }

    /**
     * This is the default impl that comes with Kuali - uses OJB.
     * 
     * @see org.kuali.core.dao.BusinessObjectDao#countMatching(java.lang.Class, java.util.Map, java.util.Map)
     */
    public int countMatching(Class clazz, Map positiveFieldValues, Map negativeFieldValues) {
        Criteria criteria = buildCriteria(positiveFieldValues);
        Criteria negativeCriteria = buildNegativeCriteria(negativeFieldValues);
        criteria.addAndCriteria(negativeCriteria);
        return getPersistenceBrokerTemplate().getCount(QueryFactory.newQuery(clazz, criteria));
    }

    
    /**
     * This is the default impl that comes with Kuali - uses OJB.
     * 
     * @see org.kuali.core.dao.BusinessObjectDao#findMatching(java.lang.Class, java.util.Map)
     */
    public Collection findMatchingOrderBy(Class clazz, Map fieldValues, String sortField, boolean sortAscending) {
        Criteria criteria = buildCriteria(fieldValues);
        QueryByCriteria queryByCriteria = new QueryByCriteria(clazz, criteria);

        if (sortAscending) {
            queryByCriteria.addOrderByAscending(sortField);
        }
        else {
            queryByCriteria.addOrderByDescending(sortField);
        }

        return getPersistenceBrokerTemplate().getCollectionByQuery(queryByCriteria);
    }


    /**
     * Saves a business object.
     * 
     * @see org.kuali.core.dao.BusinessObjectDao#save(org.kuali.core.bo.PersistableBusinessObject)
     */
    public void save(PersistableBusinessObject bo) throws DataAccessException {
        // refresh bo to get db copy of collections
        PersistableBusinessObject savedBo = (PersistableBusinessObject) ObjectUtils.deepCopy(bo);
        
        Set<String> boCollections = getPersistenceStructureService().listCollectionObjectTypes(savedBo.getClass()).keySet();
        for (String boCollection : boCollections) {
            if (getPersistenceStructureService().isCollectionUpdatable(savedBo.getClass(), boCollection)) {
                savedBo.refreshReferenceObject(boCollection);
            }
        }
        KNSServiceLocator.getOjbCollectionHelper().processCollections(this, bo, savedBo);
        
        getPersistenceBrokerTemplate().store(bo);
    }
    
    /**
     * Saves a business object.
     * 
     * @see org.kuali.core.dao.BusinessObjectDao#save(org.kuali.core.bo.PersistableBusinessObject)
     */
    public void save(List businessObjects) throws DataAccessException {
        for (Iterator i = businessObjects.iterator(); i.hasNext();) {
            Object bo = i.next();
            getPersistenceBrokerTemplate().store(bo);
        }
    }


    /**
     * Deletes the business object passed in.
     * 
     * @param bo
     * @throws DataAccessException
     * @see org.kuali.core.dao.BusinessObjectDao#delete(org.kuali.core.bo.PersistableBusinessObject)
     */
    public void delete(PersistableBusinessObject bo) {
        getPersistenceBrokerTemplate().delete(bo);
    }

    /**
     * @see org.kuali.core.dao.BusinessObjectDao#delete(java.util.List)
     */
    public void delete(List<PersistableBusinessObject> boList) {
        for (PersistableBusinessObject bo : boList) {
            getPersistenceBrokerTemplate().delete(bo);
        }
    }


    /**
     * @see org.kuali.core.dao.BusinessObjectDao#deleteMatching(java.lang.Class, java.util.Map)
     */
    public void deleteMatching(Class clazz, Map fieldValues) {
        Criteria criteria = buildCriteria(fieldValues);

        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(clazz, criteria));

        // An ojb delete by query doesn't update the cache so we need to clear the cache for everything to work property.
        // don't believe me? Read the source code to OJB
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * @see org.kuali.core.dao.BusinessObjectDao#retrieve(org.kuali.core.bo.PersistableBusinessObject)
     */
    public PersistableBusinessObject retrieve(PersistableBusinessObject object) {
        return (PersistableBusinessObject) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQueryByIdentity(object));
    }

    /**
     * This method will build out criteria in the key-value paradigm (attribute-value).
     * 
     * @param fieldValues
     * @return
     */
    private Criteria buildCriteria(Map fieldValues) {
        Criteria criteria = new Criteria();
        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            String key = (String) e.getKey();
            Object value = e.getValue();
            if (value instanceof Collection) {
                criteria.addIn(key, (Collection) value);
            }
            else {
                criteria.addEqualTo(key, value);
            }
        }

        return criteria;
    }
    
    /**
     * Builds a Criteria object for active field set to true
     * @return Criteria
     */
    private Criteria buildActiveCriteria(){
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.ACTIVE, true);
        
        return criteria;
    }
    
    /**
     * This method will build out criteria in the key-value paradigm (attribute-value).
     * 
     * @param negativeFieldValues
     * @return
     */    
    private Criteria buildNegativeCriteria(Map negativeFieldValues) {
        Criteria criteria = new Criteria();
        for (Iterator i = negativeFieldValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            String key = (String) e.getKey();
            Object value = e.getValue();
            if (value instanceof Collection) {
                criteria.addNotIn(key, (Collection) value);
            }
            else {
                criteria.addNotEqualTo(key, value);
            }
        }

        return criteria;            
    }

    /**
     * Gets the persistenceStructureService attribute. 
     * @return Returns the persistenceStructureService.
     */
    protected PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService;
    }

    /**
     * Sets the persistenceStructureService attribute value.
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }
    
    
}