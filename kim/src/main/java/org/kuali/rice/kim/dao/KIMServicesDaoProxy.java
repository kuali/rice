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
package org.kuali.rice.kim.dao;

import java.util.Collection;

import org.kuali.rice.kim.bo.EntityAttribute;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.web.form.GroupQualifiedRole;
import org.kuali.rice.kim.web.form.PrincipalQualifiedRole;
import org.kuali.rice.util.OrmUtils;

public class KIMServicesDaoProxy implements KIMServicesDao {

	private KIMServicesDao kimServicesDaoJpa;
	private KIMServicesDao kimServicesDaoOjb;

	private KIMServicesDao getDao(Class clazz) {
		return (OrmUtils.isJpaAnnotated(clazz) && OrmUtils.isJpaEnabled()) ? kimServicesDaoJpa
				: kimServicesDaoOjb;
	}

	public Collection<Principal> findAllPersons() {
		return getDao(Principal.class).findAllPersons();
	}

	public Principal findPerson(final Long personId) {
		return getDao(Principal.class).findPerson(personId);
	}

	public Collection<EntityAttribute> getPersonAttributesForNamespace(
			Long personId, String namespaceName) {
		return getDao(EntityAttribute.class).getPersonAttributesForNamespace(personId, namespaceName);
	}

	public String getPersonAttributeValue(Long personId, String attributeName,
			String namespaceName) {
		return getDao(EntityAttribute.class).getPersonAttributeValue(personId, attributeName, namespaceName);
	}

	public String getAttributeValueForNamespace(String attributeName, String namespaceName,
			final Long entityId) {
		return getDao(EntityAttribute.class).getAttributeValueForNamespace(attributeName, namespaceName, entityId);
	}

	public Collection<EntityAttribute> getAttributesForNamespace(
			String namespaceName, final Long entityId) {
		return getDao(EntityAttribute.class).getAttributesForNamespace(namespaceName, entityId);
	}

	public Collection<PrincipalQualifiedRole> findAllPersonsWithQualifiedRole(final String roleName) {
		return getDao(PrincipalQualifiedRole.class).findAllPersonsWithQualifiedRole(roleName);
	}

	public Collection<PrincipalQualifiedRole> findAllPrincipalsWithQualifiedRole(final String roleName) {
		return getDao(PrincipalQualifiedRole.class).findAllPrincipalsWithQualifiedRole(roleName);
	}

	public Collection<GroupQualifiedRole> findAllGroupsWithQualifiedRole(final String roleName) {
		return getDao(GroupQualifiedRole.class).findAllGroupsWithQualifiedRole(roleName);
	}



	/**
	 * @param servicesDaoJpa the kIMServicesDaoJpa to set
	 */
	public void setKimServicesDaoJpa(final KIMServicesDao kimServicesDaoJpa) {
		this.kimServicesDaoJpa = kimServicesDaoJpa;
	}

	/**
	 * @param servicesDaoOjb the kIMServicesDaoOjb to set
	 */
	public void setKimServicesDaoOjb(final KIMServicesDao kimServicesDaoOjb) {
		this.kimServicesDaoOjb = kimServicesDaoOjb;
	}

}
