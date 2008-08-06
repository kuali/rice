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
package org.kuali.rice.kns.dao.proxy;

import java.util.Collection;
import java.util.Map;

import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kns.dao.LookupDao;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LookupDaoProxy implements LookupDao {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupDaoProxy.class);
    
	private LookupDao lookupDaoJpa;
	private LookupDao lookupDaoOjb;		
	
    public void setLookupDaoJpa(LookupDao lookupDaoJpa) {
		this.lookupDaoJpa = lookupDaoJpa;
	}

	public void setLookupDaoOjb(LookupDao lookupDaoOjb) {
		this.lookupDaoOjb = lookupDaoOjb;
	}
	
    private LookupDao getDao(Class clazz) {
    	return (OrmUtils.isJpaAnnotated(clazz) && OrmUtils.isJpaEnabled()) ? lookupDaoJpa : lookupDaoOjb; 
    }
    
	/**
	 * @see org.kuali.rice.kns.dao.LookupDao#createCriteria(java.lang.Object, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public boolean createCriteria(Object example, String searchValue, String propertyName, Object criteria) {
		return getDao(example.getClass()).createCriteria(example, searchValue, propertyName, criteria);
	}

	/**
	 * @see org.kuali.rice.kns.dao.LookupDao#createCriteria(java.lang.Object, java.lang.String, java.lang.String, boolean, java.lang.Object)
	 */
	public boolean createCriteria(Object example, String searchValue, String propertyName, boolean caseInsensitive, Object criteria) {
		return getDao(example.getClass()).createCriteria(example, searchValue, propertyName, caseInsensitive, criteria);
	}

	/**
	 * @see org.kuali.rice.kns.dao.LookupDao#findCollectionBySearchHelper(java.lang.Class, java.util.Map, boolean, boolean)
	 */
	public Collection findCollectionBySearchHelper(Class example, Map formProps, boolean unbounded, boolean usePrimaryKeyValuesOnly) {
		return getDao(example).findCollectionBySearchHelper(example, formProps, unbounded, usePrimaryKeyValuesOnly);
	}

	/**
	 * @see org.kuali.rice.kns.dao.LookupDao#findCollectionBySearchHelper(java.lang.Class, java.util.Map, boolean, boolean, java.lang.Object)
	 */
	public Collection findCollectionBySearchHelper(Class example, Map formProps, boolean unbounded, boolean usePrimaryKeyValuesOnly, Object additionalCriteria) {
		return getDao(example).findCollectionBySearchHelper(example, formProps, unbounded, usePrimaryKeyValuesOnly, additionalCriteria);
	}

	/**
	 * @see org.kuali.rice.kns.dao.LookupDao#findCollectionBySearchHelperWithUniversalUserJoin(java.lang.Class, java.util.Map, java.util.Map, boolean, boolean)
	 */
	public Collection findCollectionBySearchHelperWithUniversalUserJoin(Class example, Map nonUniversalUserSearchCriteria, Map universalUserSearchCriteria, boolean unbounded, boolean usePrimaryKeyValuesOnly) {
		return getDao(example).findCollectionBySearchHelperWithUniversalUserJoin(example, nonUniversalUserSearchCriteria, universalUserSearchCriteria, unbounded, usePrimaryKeyValuesOnly);
	}

	/**
	 * @see org.kuali.rice.kns.dao.LookupDao#findCountByMap(java.lang.Object, java.util.Map)
	 */
	public Long findCountByMap(Object example, Map formProps) {
		return getDao(example.getClass()).findCountByMap(example, formProps);
	}

	/**
	 * @see org.kuali.rice.kns.dao.LookupDao#findObjectByMap(java.lang.Object, java.util.Map)
	 */
	public Object findObjectByMap(Object example, Map formProps) {
		return getDao(example.getClass()).findObjectByMap(example, formProps);
	}

}