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
package org.kuali.core.dao.jpa;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kuali.core.bo.user.KualiModuleUserProperty;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.KualiModuleUserPropertyDao;
import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria;

public class KualiModuleUserPropertyDaoJpa implements KualiModuleUserPropertyDao {

	@PersistenceContext
	private EntityManager entityManager;

	// TODO: Sample app does not use this. If fails, JPA may require a retrieve before the save (or persist call if new)
	public void save(KualiModuleUserProperty property) {
		entityManager.merge(property);
	}

	// TODO: Sample app does not use this. If fails, JPA may require a retrieve before the save (or persist call if new)
	public void save(Collection<KualiModuleUserProperty> properties) {
		for (KualiModuleUserProperty prop : properties) {
			entityManager.merge(prop);
		}
	}

	public Collection<KualiModuleUserProperty> getPropertiesForUser(UniversalUser user) {
		if (user == null || user.getPersonUniversalIdentifier() == null) {
			return new ArrayList<KualiModuleUserProperty>();
		}
		return getPropertiesForUser(user.getPersonUniversalIdentifier());
	}

	public Collection<KualiModuleUserProperty> getPropertiesForUser(String personUniversalIdentifier) {
		if (personUniversalIdentifier == null) {
			return new ArrayList<KualiModuleUserProperty>();
		}
		Criteria criteria = new Criteria(KualiModuleUserProperty.class.getName());
		criteria.eq("personUniversalIdentifier", personUniversalIdentifier);
		return new QueryByCriteria(entityManager, criteria).toQuery().getResultList();
	}

}
