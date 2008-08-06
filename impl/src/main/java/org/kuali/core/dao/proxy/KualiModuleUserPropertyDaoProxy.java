/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.dao.proxy;

import java.util.Collection;

import org.kuali.core.bo.user.KualiModuleUserProperty;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.KualiModuleUserPropertyDao;
import org.kuali.rice.core.util.OrmUtils;

public class KualiModuleUserPropertyDaoProxy implements KualiModuleUserPropertyDao {

    private KualiModuleUserPropertyDao moduleUserPropertyDaoJpa;
    private KualiModuleUserPropertyDao moduleUserPropertyDaoOjb;
	
    private KualiModuleUserPropertyDao getDao(Class clazz) {
    	return (OrmUtils.isJpaAnnotated(clazz) && OrmUtils.isJpaEnabled()) ? moduleUserPropertyDaoJpa : moduleUserPropertyDaoOjb; 
    }
    
    public void save(KualiModuleUserProperty property) {
		getDao(KualiModuleUserProperty.class).save(property);
	}

	public void save(Collection<KualiModuleUserProperty> properties) {
		getDao(KualiModuleUserProperty.class).save(properties);
	}

	public Collection<KualiModuleUserProperty> getPropertiesForUser(UniversalUser user) {
		return getDao(KualiModuleUserProperty.class).getPropertiesForUser(user);
	}

	public Collection<KualiModuleUserProperty> getPropertiesForUser(String personUniversalIdentifier) {
		return getDao(KualiModuleUserProperty.class).getPropertiesForUser(personUniversalIdentifier);
	}

	public void setModuleUserPropertyDaoJpa(KualiModuleUserPropertyDao moduleUserPropertyDaoJpa) {
		this.moduleUserPropertyDaoJpa = moduleUserPropertyDaoJpa;
	}

	public void setModuleUserPropertyDaoOjb(KualiModuleUserPropertyDao moduleUserPropertyDaoOjb) {
		this.moduleUserPropertyDaoOjb = moduleUserPropertyDaoOjb;
	}

}
