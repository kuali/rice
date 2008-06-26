/*
 * Copyright 2008 The Kuali Foundation
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

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kim.bo.EntityAttribute;
import org.kuali.rice.kim.bo.GroupQualifiedRole;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.PrincipalQualifiedRole;

/**
 * This is a description of what this class does - lindholm don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KIMServicesDaoOjb extends PlatformAwareDaoBaseOjb implements
		KIMServicesDao {
	private Long personEntityTypeId = new Long(1); // TODO should be defined somewhere. ID or String
	final Criteria personCriteria = new Criteria();
	final Criteria personQualifiedCriteria = new Criteria();

	public KIMServicesDaoOjb() {
		personCriteria.addEqualTo("entity.entityType.id", personEntityTypeId);
		personQualifiedCriteria.addEqualTo("principal.entity.entityType.id", personEntityTypeId);

	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findAllPersons()
	 */
	public Collection<Principal> findAllPersons() {
		final Query query = QueryFactory.newQuery(Principal.class,
				personCriteria);
		return (Collection<Principal>) getPersistenceBrokerTemplate()
				.getCollectionByQuery(query);
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findAllPersons()
	 * @deprecated Doesn't work yet
	 */
	public Collection<Principal> findAllPersonsWithRole(final String roleName) {
		final Criteria role = new Criteria();
		role.addEqualTo("role.name", roleName);  // TODO subquery??
		role.addAndCriteria(personCriteria);
		final Query query = QueryFactory.newQuery(Principal.class,
				role);
		return (Collection<Principal>) getPersistenceBrokerTemplate()
				.getCollectionByQuery(query);


	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findPerson(java.lang.Long)
	 */
	public Principal findPerson(final Long personId) {
		final Criteria principalCriteria = new Criteria();
		principalCriteria.addEqualTo("id", personId);
		principalCriteria.addAndCriteria(personCriteria);

		final Query query = QueryFactory.newQuery(Principal.class,
				principalCriteria);
		return (Principal) getPersistenceBrokerTemplate().getObjectByQuery(
				query);
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#getPersonAttributesForNamespace(java.lang.Long,
	 *      java.lang.String)
	 */
	public Collection<EntityAttribute> getPersonAttributesForNamespace(
			Long personId, String namespaceName) {

		final Principal person = findPerson(personId);
		if (person == null) {
			return null;
		}

		return getAttributesForNamespace(namespaceName, person.getEntityId());
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#getAttributesForNamespace(java.lang.String, java.lang.Long)
	 */
	public Collection<EntityAttribute> getAttributesForNamespace(
			String namespaceName, final Long entityId) {
		final Criteria principalCriteria = new Criteria();
		principalCriteria.addEqualTo("entity.id", entityId);

		final Criteria namespaceCriteria = new Criteria();
		namespaceCriteria.addEqualTo("namespace.name", namespaceName);
		principalCriteria.addAndCriteria(namespaceCriteria);

		final Query query = QueryFactory.newQuery(EntityAttribute.class,
				principalCriteria);
		Collection<EntityAttribute> attrs = (Collection<EntityAttribute>) getPersistenceBrokerTemplate()
				.getCollectionByQuery(query);
		if (attrs == null || attrs.size() == 0) {
			return null;
		}
		return attrs;
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#getPersonAttributeValue(java.lang.Long,
	 *      java.lang.String, java.lang.String)
	 */
	public String getPersonAttributeValue(Long personId, String attributeName,
			String namespaceName) {

		final Principal person = findPerson(personId);
		if (person == null) {
			return null;
		}

		return getAttributeValueForNamespace(attributeName, namespaceName,
				person.getEntityId());
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#getAttributeValueForNamespace(java.lang.String,
	 *      java.lang.String, java.lang.Long)
	 */
	public String getAttributeValueForNamespace(String attributeName,
			String namespaceName, final Long entityId) {
		final Criteria principalCriteria = new Criteria();
		principalCriteria.addEqualTo("entity.id", entityId);

		final Criteria namespaceCriteria = new Criteria();
		namespaceCriteria.addEqualTo("namespace.name", namespaceName);
		principalCriteria.addAndCriteria(namespaceCriteria);

		final Criteria valueCriteria = new Criteria();
		valueCriteria.addEqualTo("attributeName", attributeName);
		principalCriteria.addAndCriteria(valueCriteria);

		final Query query = QueryFactory.newQuery(EntityAttribute.class,
				principalCriteria);
		EntityAttribute value = (EntityAttribute) getPersistenceBrokerTemplate()
				.getObjectByQuery(query);
		if (value == null) {
			return null;
		}
		return value.getValue();
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findAllPersonsWithQualifiedRole(java.lang.String)
	 */
	public Collection<PrincipalQualifiedRole> findAllPersonsWithQualifiedRole(final String roleName) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("role.name", roleName);
		roleCriteria.addAndCriteria(personQualifiedCriteria);

		final Query query = QueryFactory.newQuery(PrincipalQualifiedRole.class,
				roleCriteria);
		return (Collection<PrincipalQualifiedRole>) getPersistenceBrokerTemplate()
				.getCollectionByQuery(query);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findAllPrincipalsWithQualifiedRole(java.lang.String)
	 */
	public Collection<PrincipalQualifiedRole> findAllPrincipalsWithQualifiedRole(final String roleName) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("role.name", roleName);

		final Query query = QueryFactory.newQuery(PrincipalQualifiedRole.class,
				roleCriteria);
		return (Collection<PrincipalQualifiedRole>) getPersistenceBrokerTemplate()
				.getCollectionByQuery(query);

	}



	/**
	 *
	 * This method ...
	 *
	 * @param roleName
	 * @return
	 */
	public Collection<GroupQualifiedRole> findAllGroupsWithQualifiedRole(final String roleName) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("role.name", roleName);

		final Query query = QueryFactory.newQuery(GroupQualifiedRole.class,
				roleCriteria);
		return (Collection<GroupQualifiedRole>) getPersistenceBrokerTemplate()
				.getCollectionByQuery(query);

	}
}
