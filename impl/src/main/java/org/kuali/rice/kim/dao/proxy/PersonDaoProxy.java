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
package org.kuali.rice.kim.dao.proxy;

import java.util.List;
import java.util.Map;

import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kim.dao.PersonDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PersonDaoProxy<T extends PersonImpl> extends PlatformAwareDaoBaseOjb implements PersonDao<T> {

	private PersonDao<T> daoOjb;
	private PersonDao<T> daoJpa;
	
	private PersonDao<T> actualDao;
	
	/**
	 * @see org.springframework.dao.support.DaoSupport#initDao()
	 */
	@Override
	protected void initDao() throws Exception {
		super.initDao();
		actualDao = (OrmUtils.isJpaAnnotated(PersonImpl.class) && OrmUtils.isJpaEnabled()) ? daoJpa : daoOjb;
	}
		
	public List<T> findPeople(Map<String,String> criteria) {
		return actualDao.findPeople( criteria );
	}
	
	

	public void setDaoOjb(PersonDao<T> daoOjb) {
		this.daoOjb = daoOjb;
	}

	public void setDaoJpa(PersonDao<T> daoJpa) {
		this.daoJpa = daoJpa;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.v2.dao.PersonDao#convertEntityToPerson(org.kuali.rice.kim.bo.entity.KimEntity, org.kuali.rice.kim.bo.entity.KimPrincipal)
	 */
	public T convertEntityToPerson(KimEntity entity, KimPrincipal principal) {
		return actualDao.convertEntityToPerson(entity, principal);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.dao.PersonDao#convertPersonPropertiesToEntityProperties(java.util.Map)
	 */
	public Map<String, String> convertPersonPropertiesToEntityProperties(
			Map<String, String> criteria) {
		return actualDao.convertPersonPropertiesToEntityProperties(criteria);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.dao.PersonDao#findPeople(java.util.Map, boolean)
	 */
	public List<T> findPeople(Map<String, String> criteria, boolean unbounded) {
		return actualDao.findPeople(criteria, unbounded);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.dao.PersonDao#getPersonEntityTypeCode()
	 */
	public String getPersonEntityTypeCode() {
		return actualDao.getPersonEntityTypeCode();
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.dao.PersonDao#getPersonImplementationClass()
	 */
	public Class<? extends Person> getPersonImplementationClass() {
		return actualDao.getPersonImplementationClass();
	}
}
