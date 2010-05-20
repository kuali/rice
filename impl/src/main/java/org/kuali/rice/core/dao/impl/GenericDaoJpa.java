/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.core.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.core.util.RiceConstants;

import org.apache.log4j.Logger;
import org.kuali.rice.core.dao.GenericDao;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.bo.PersistableBusinessObject;

/**
 * JPA implementation of GenericDao 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GenericDaoJpa implements GenericDao{
	
	public static final Logger LOG = Logger.getLogger(GenericDaoJpa.class);

	@PersistenceContext(unitName="ken-unit")
	private EntityManager entityManager;
	
    private boolean useSelectForUpdate = true;

    /**
     * @param selectForUpdate whether to use select for update to implement pessimistic locking (testing/debugging purposes only)
     */
    public void setUseSelectForUpdate(boolean useSelectForUpdate) {
        this.useSelectForUpdate = useSelectForUpdate;
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
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#countMatching(java.lang.Class, java.util.Map)
	 */
	@Override
	public int countMatching(Class clazz, Map fieldValues) {
		return KNSServiceLocator.getBusinessObjectDao().countMatching(clazz, fieldValues);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#countMatching(java.lang.Class, java.util.Map, java.util.Map)
	 */
	@Override
	public int countMatching(Class clazz, Map positiveFieldValues,
			Map negativeFieldValues) {
		return KNSServiceLocator.getBusinessObjectDao().countMatching(clazz, positiveFieldValues, negativeFieldValues);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#delete(java.lang.Object)
	 */
	@Override
	public void delete(Object bo) {
		if(bo instanceof PersistableBusinessObject){
			KNSServiceLocator.getBusinessObjectService().delete((PersistableBusinessObject)bo);
		}
		else {
            LOG.error("Error: unable to process this bo");
        }
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#delete(java.util.List)
	 */
	@Override
	public void delete(List<Object> boList) {
	    for (Object bo : boList) {
	    	delete(bo);
        }
		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#deleteMatching(java.lang.Class, java.util.Map)
	 */
	@Override
	public void deleteMatching(Class clazz, Map fieldValues) {
		KNSServiceLocator.getBusinessObjectDao().deleteMatching(clazz, fieldValues);
		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#findAll(java.lang.Class)
	 */
	@Override
	public Collection findAll(Class clazz) {
		return KNSServiceLocator.getBusinessObjectDao().findAll(clazz);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#findAllOrderBy(java.lang.Class, java.lang.String, boolean)
	 */
	@Override
	public Collection findAllOrderBy(Class clazz, String sortField,
			boolean sortAscending) {
		return KNSServiceLocator.getBusinessObjectDao().findAllActiveOrderBy(clazz, sortField, sortAscending);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#findById(java.lang.Class, java.lang.Object)
	 */
	@Override
	public Object findById(Class clazz, Object keyValue) {
		return KNSServiceLocator.getBusinessObjectDao().findBySinglePrimaryKey(clazz, keyValue);

	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#findByPrimaryKey(java.lang.Class, java.util.Map)
	 */
	@Override
	public Object findByPrimaryKey(Class clazz, Map primaryKeys) {
		return KNSServiceLocator.getBusinessObjectDao().findByPrimaryKey(clazz, primaryKeys);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#findByUniqueKey(java.lang.Class, java.util.Map)
	 */
	//caller need casting return obj to bo 
	@Override
	public Object findByUniqueKey(Class clazz, Map uniqueKeys) {
		return KNSServiceLocator.getBusinessObjectDao().findByPrimaryKey(clazz, uniqueKeys);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#findMatching(java.lang.Class, java.util.Map)
	 */
	@Override
	public Collection findMatching(Class clazz, Map fieldValues) {
		return KNSServiceLocator.getBusinessObjectDao().findMatching(clazz, fieldValues);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#findMatching(java.lang.Class, org.apache.ojb.broker.query.Criteria)
	 */
	@Override
	public Collection findMatching(Class clazz, org.kuali.rice.core.jpa.criteria.Criteria criteria) {
		return findMatching(clazz, criteria, false, RiceConstants.NO_WAIT);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#findMatching(java.lang.Class, org.apache.ojb.broker.query.Criteria, boolean, long)
	 */
	@Override
	public Collection findMatching(Class clazz, org.kuali.rice.core.jpa.criteria.Criteria criteria,
			boolean selectForUpdate, long wait) {
		//need implementation
		LOG.info("*************calling JPA********************************");
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#findMatchingByExample(java.lang.Object)
	 */
	@Override
	public Collection findMatchingByExample(Object object) {
		//need implementation
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#findMatchingOrderBy(java.lang.Class, java.util.Map, java.lang.String, boolean)
	 */
	@Override
	public Collection findMatchingOrderBy(Class clazz, Map fieldValues,
			String sortField, boolean sortAscending) {
		return KNSServiceLocator.getBusinessObjectService().findMatchingOrderBy(clazz, fieldValues, sortField, sortAscending);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#retrieve(java.lang.Object)
	 */
	@Override
	//could also use KNS BusinessObjectDaoJpa.retrieve()
	public Object retrieve(Object bo) {
		
		if(bo instanceof PersistableBusinessObject)
			return KNSServiceLocator.getBusinessObjectDao().retrieve((PersistableBusinessObject) bo);
		else {
            LOG.error("Error: unable to process this bo");
            return null;
        }
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#save(java.lang.Object)
	 */
	@Override
	public void save(Object bo) {
		
		LOG.info("*************calling JPA********************************");
		
		if(bo instanceof PersistableBusinessObject)
			KNSServiceLocator.getBusinessObjectDao().save((PersistableBusinessObject) bo);
		else {
            LOG.error("Error: unable to process this bo");
		}
	}
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#save(java.util.List)
	 */
	@Override
	public void save(List businessObjects) {
		for(Object bo:  businessObjects)
			save(bo);
		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#findMatching(java.lang.Class, org.apache.ojb.broker.query.Criteria)
	 */
	@Override
	public Collection findMatching(Class clazz,
			org.apache.ojb.broker.query.Criteria criteria) {
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#findMatching(java.lang.Class, org.apache.ojb.broker.query.Criteria, boolean, long)
	 */
	@Override
	public Collection findMatching(Class clazz,
			org.apache.ojb.broker.query.Criteria criteria,
			boolean selectForUpdate, long wait) {
		// TODO g1zhang - THIS METHOD NEEDS JAVADOCS
		LOG.info("*************calling OJB********************************");
		return null;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.dao.GenericDao#findMatching(java.lang.Class, java.util.Map, boolean, long)
	 */
	@Override
	public Collection findMatching(Class clazz, Map criteria,
			boolean selectForUpdate, long wait) {
		// need implementation
		return null;
	}

}
