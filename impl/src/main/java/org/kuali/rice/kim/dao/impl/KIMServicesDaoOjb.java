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
package org.kuali.rice.kim.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.rice.kim.bo.Entity;
import org.kuali.rice.kim.bo.EntityAttribute;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupQualifiedRoleAttribute;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.PrincipalQualifiedRoleAttribute;
import org.kuali.rice.kim.dao.KIMServicesDao;
import org.kuali.rice.kim.service.impl.ImplUtils;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

/**
 * Implements DAO queries for KIM objects for better performance
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
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findQualifiedGroupsWithRole(java.lang.String, java.util.Map)
	 */
	public Collection<Group> findQualifiedGroupsWithRole(String roleName, Map<String, String> qualifiedRoleAttributes) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("roles.name", roleName);

		if (qualifiedRoleAttributes != null && qualifiedRoleAttributes.size() > 0) {
			final Criteria attributeCriteria = new Criteria();
			for (String key : qualifiedRoleAttributes.keySet()) {
				final Criteria attribute = new Criteria();
				attribute.addEqualTo("groupQualifiedRoleAttributes.attributeName", key);
				attribute.addEqualTo("groupQualifiedRoleAttributes.attributeValue", qualifiedRoleAttributes.get(key));
				attributeCriteria.addOrCriteria(attribute);
			}
			roleCriteria.addAndCriteria(attributeCriteria);
		}

		final QueryByCriteria query = QueryFactory.newQuery(Group.class,
				roleCriteria, true);
		query.addOrderByAscending("id");
		Collection<Group> possibles = (Collection<Group>) getPersistenceBrokerTemplate()
				.getCollectionByQuery(query);
		if (qualifiedRoleAttributes.size() == 1) {
			return possibles;
		} else {
			Collection<Group> matches = new ArrayList<Group>(possibles.size());
			for (Group g : possibles) {
				if (ImplUtils.hasAllQualifiedAttributes(qualifiedRoleAttributes, g.getGroupQualifiedRoleAttributes())) {
					matches.add(g);
				}
			}
			return matches;
		}
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findQualifiedGroupsWithRole(java.lang.String, java.lang.String, java.util.Map)
	 */
	public Group findQualifiedGroupsWithRole(String groupName, String roleName, Map<String, String> qualifiedRoleAttributes) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("name", groupName);
		roleCriteria.addEqualTo("roles.name", roleName);

		if (qualifiedRoleAttributes != null && qualifiedRoleAttributes.size() > 0) {
			final Criteria attributeCriteria = new Criteria();
			for (String key : qualifiedRoleAttributes.keySet()) {
				final Criteria attribute = new Criteria();
				attribute.addEqualTo("groupQualifiedRoleAttributes.attributeName", key);
				attribute.addEqualTo("groupQualifiedRoleAttributes.attributeValue", qualifiedRoleAttributes.get(key));
				attributeCriteria.addOrCriteria(attribute);
			}
			roleCriteria.addAndCriteria(attributeCriteria);
		}

		final Query query = QueryFactory.newQuery(Group.class,
				roleCriteria);
		final Group group = (Group) getPersistenceBrokerTemplate()
				.getObjectByQuery(query);
		if (group != null) {
			if (qualifiedRoleAttributes.size() == 1) {
				return group;
			} else {
				if (ImplUtils.hasAllQualifiedAttributes(qualifiedRoleAttributes, group.getGroupQualifiedRoleAttributes())) {
					return group;
				}
			}
		}
		return null;
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findGroupQualifiedRole(java.lang.String, java.util.Map)
	 */
	public Collection<GroupQualifiedRoleAttribute> findGroupQualifiedRole(String roleName, Map<String, String> attributes) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("role.name", roleName);

		if (attributes != null && attributes.size() > 0) {
			final Criteria attributeCriteria = new Criteria();
			for (String key : attributes.keySet()) {
				final Criteria attribute = new Criteria();
				attribute.addEqualTo("attributeName", key);
				attribute.addEqualTo("attributeValue", attributes.get(key));
				attributeCriteria.addOrCriteria(attribute);
			}
			roleCriteria.addAndCriteria(attributeCriteria);
		}

		final QueryByCriteria query = QueryFactory.newQuery(GroupQualifiedRoleAttribute.class,
				roleCriteria);
		query.addOrderByAscending("id");
		return (Collection<GroupQualifiedRoleAttribute>) getPersistenceBrokerTemplate()
				.getCollectionByQuery(query);
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findPrincipalQualifiedRole(java.lang.String, java.util.Map)
	 */
	public Collection<PrincipalQualifiedRoleAttribute> findPrincipalQualifiedRole(final String roleName, final Map<String, String> attributes) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("role.name", roleName);

		if (attributes != null && attributes.size() > 0) {
			final Criteria attributeCriteria = new Criteria();
			for (String key : attributes.keySet()) {
				final Criteria attribute = new Criteria();
				attribute.addEqualTo("attributeName", key);
				attribute.addEqualTo("attributeValue", attributes.get(key));
				attributeCriteria.addOrCriteria(attribute);
			}
			roleCriteria.addAndCriteria(attributeCriteria);
		}

		final QueryByCriteria query = QueryFactory.newQuery(PrincipalQualifiedRoleAttribute.class,
				roleCriteria, true);
		query.addOrderByAscending("id");
		return (Collection<PrincipalQualifiedRoleAttribute>) getPersistenceBrokerTemplate()
				.getCollectionByQuery(query);
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findQualifiedPrincipalsWithRole(java.lang.String, java.util.Map)
	 */
	public Collection<Principal> findQualifiedPrincipalsWithRole(String roleName, Map<String, String> qualifiedRoleAttributes) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("roles.name", roleName);
		return findQualifiedPrincipalsWithRole(qualifiedRoleAttributes, roleCriteria);

	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findQualifiedPersonsWithRole(java.lang.String, java.util.Map)
	 */
	public Collection<Principal> findQualifiedPersonsWithRole(String roleName, Map<String, String> qualifiedRoleAttributes) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("roles.name", roleName);
		roleCriteria.addAndCriteria(personCriteria);
		return findQualifiedPrincipalsWithRole(qualifiedRoleAttributes, roleCriteria);
	}
	
	
	/**
	 * 
	 * 
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findQualifiedPersonWithRole(java.lang.Long, java.lang.String, java.util.Map)
	 */
	public Principal findQualifiedPersonWithRole(Long personId, String roleName, Map<String, String> qualifiedRoleAttributes) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("id", personId);
		roleCriteria.addEqualTo("roles.name", roleName);
		roleCriteria.addAndCriteria(personCriteria);
		final Collection<Principal> p = findQualifiedPrincipalsWithRole(qualifiedRoleAttributes, roleCriteria);
		if (p.iterator().hasNext()) {
			return p.iterator().next();
		} else {
			return null;
		}
	}

	public Principal findQualifiedPrincipalWithRole(String principalName, String roleName, Map<String, String> qualifiedRoleAttributes) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("name", principalName);
		roleCriteria.addEqualTo("roles.name", roleName);
		roleCriteria.addAndCriteria(personCriteria);
		final Collection<Principal> p = findQualifiedPrincipalsWithRole(qualifiedRoleAttributes, roleCriteria);
		if (p.iterator().hasNext()) {
			return p.iterator().next();
		} else {
			return null;
		}

	}
	
	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findQualifiedPersonWithPermission(java.lang.Long, java.lang.String, java.util.Map, java.lang.String)
	 */
	public Principal findQualifiedPersonWithPermission(Long personId, String permissionName, Map<String, String> qualifiedRoleAttributes, String namespaceName) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addAndCriteria(personCriteria);
		roleCriteria.addEqualTo("id", personId);
		return findQualifiedPrincipalWithPermissionNamespace(permissionName, qualifiedRoleAttributes, namespaceName,
				roleCriteria);
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findQualifiedPrincipalWithPermission(java.lang.Long, java.lang.String, java.util.Map, java.lang.String)
	 */
	public Principal findQualifiedPrincipalWithPermission(String principalName, String permissionName, Map<String, String> qualifiedRoleAttributes, String namespaceName) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("name", principalName);
		return findQualifiedPrincipalWithPermissionNamespace(permissionName, qualifiedRoleAttributes, namespaceName,
				roleCriteria);

	}

	/**
	 * This method finds a QualifiedPrincipalWithPermissionRole
	 *
	 * @param permissionName
	 * @param qualifiedRoleAttributes
	 * @param namespaceName
	 * @param roleCriteria
	 * @return
	 */
	private Principal findQualifiedPrincipalWithPermissionNamespace(String permissionName, Map<String, String> qualifiedRoleAttributes,
			String namespaceName, final Criteria roleCriteria) {
		roleCriteria.addEqualTo("roles.permissions.namespace.name", namespaceName);
		roleCriteria.addEqualTo("roles.permissions.name", permissionName);

		Collection<Principal> p = findQualifiedPrincipalsWithRole(qualifiedRoleAttributes, roleCriteria);
		if (p.iterator().hasNext()) {
			return p.iterator().next();
		} else {
			return null;
		}
	}

	/**
	 * This method finds Principals in a role with qualified attributes
	 *
	 * @param roleName
	 * @param qualifiedRoleAttributes
	 * @param roleCriteria query filter
	 * @return
	 */
	private Collection<Principal> findQualifiedPrincipalsWithRole(
			Map<String, String> qualifiedRoleAttributes,
			final Criteria roleCriteria) {

		if (qualifiedRoleAttributes != null && qualifiedRoleAttributes.size() > 0) {
			final Criteria attributeCriteria = new Criteria();
			for (String key : qualifiedRoleAttributes.keySet()) {
				final Criteria attribute = new Criteria();
				attribute.addEqualTo("principalQualifiedRoleAttributes.attributeName", key);
				attribute.addEqualTo("principalQualifiedRoleAttributes.attributeValue", qualifiedRoleAttributes.get(key));
				attributeCriteria.addOrCriteria(attribute);
			}
			roleCriteria.addAndCriteria(attributeCriteria);
		}

		final QueryByCriteria query = QueryFactory.newQuery(Principal.class,
				roleCriteria, true);
		query.addOrderByAscending("id");
		final Collection<Principal> possibles = (Collection<Principal>) getPersistenceBrokerTemplate()
		.getCollectionByQuery(query);
		if (qualifiedRoleAttributes.size() == 1) { // Don't need to filter
			return possibles;
		} else {
			final Collection<Principal> matches = new ArrayList<Principal>();
			for (Principal p : possibles) {
				if (ImplUtils.hasAllQualifiedAttributes(qualifiedRoleAttributes, p.getPrincipalQualifiedRoleAttributes())) {
					matches.add(p);
				}
			}
			return matches;
		}
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findQualifiedEntitysWithRole(java.lang.String, java.util.Map)
	 */
	public Collection<Entity> findQualifiedEntitysWithRole(String roleName, Map<String, String> qualifiedRoleAttributes) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("principals.roles.name", roleName);

		if (qualifiedRoleAttributes != null && qualifiedRoleAttributes.size() > 0) {
			final Criteria attributeCriteria = new Criteria();
			for (String key : qualifiedRoleAttributes.keySet()) {
				final Criteria attribute = new Criteria();
				attribute.addEqualTo("principals.principalQualifiedRoleAttributes.attributeName", key);
				attribute.addEqualTo("principals.principalQualifiedRoleAttributes.attributeValue", qualifiedRoleAttributes.get(key));
				attributeCriteria.addOrCriteria(attribute);
			}
			roleCriteria.addAndCriteria(attributeCriteria);
		}

		final QueryByCriteria query = QueryFactory.newQuery(Entity.class,
				roleCriteria, true);
		query.addOrderByAscending("id");
		final Collection<Entity> possibles = (Collection<Entity>) getPersistenceBrokerTemplate()
		.getCollectionByQuery(query);
		if (qualifiedRoleAttributes.size() == 1) { // Don't need to filter
			return possibles;
		} else {
			final Collection<Entity> matches = new ArrayList<Entity>();
			for (Entity e : possibles) {
				for (Principal p : e.getPrincipals()) {
					if (ImplUtils.hasAllQualifiedAttributes(qualifiedRoleAttributes, p.getPrincipalQualifiedRoleAttributes())) {
						matches.add(e);
						break;
					}
				}
			}
			return matches;
		}
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findQualifiedEntityWithRole(java.lang.Long, java.lang.String, java.util.Map)
	 */
	public Entity findQualifiedEntityWithRole(Long entityId, String roleName, Map<String, String> qualifiedRoleAttributes) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("id", entityId);
		roleCriteria.addEqualTo("principals.roles.name", roleName);

		return findQualifiedEntity(qualifiedRoleAttributes, roleCriteria);
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findQualifiedEntityWithPermissionRole(java.lang.Long, java.lang.String, java.util.Map, java.lang.String)
	 */
	public Entity findQualifiedEntityWithPermissionRole(Long entityId, String permissionName, Map<String, String> qualifiedRoleAttributes, String namespaceName) {
		final Criteria roleCriteria = new Criteria();
		roleCriteria.addEqualTo("id", entityId);
		roleCriteria.addEqualTo("principals.roles.permissions.name", permissionName);
		roleCriteria.addEqualTo("principals.roles.permissions.namespace.name", namespaceName);

		return findQualifiedEntity(qualifiedRoleAttributes, roleCriteria);

	}

	/**
	 * This method finds an entity with qualified attributes
	 *
	 * @param qualifiedRoleAttributes
	 * @param roleCriteria
	 * @return
	 */
	private Entity findQualifiedEntity(
			Map<String, String> qualifiedRoleAttributes,
			final Criteria roleCriteria) {
		if (qualifiedRoleAttributes != null && qualifiedRoleAttributes.size() > 0) {
			final Criteria attributeCriteria = new Criteria();
			for (String key : qualifiedRoleAttributes.keySet()) {
				final Criteria attribute = new Criteria();
				attribute.addEqualTo("principals.principalQualifiedRoleAttributes.attributeName", key);
				attribute.addEqualTo("principals.principalQualifiedRoleAttributes.attributeValue", qualifiedRoleAttributes.get(key));
				attributeCriteria.addOrCriteria(attribute);
			}
			roleCriteria.addAndCriteria(attributeCriteria);
		}

		final QueryByCriteria query = QueryFactory.newQuery(Entity.class,
				roleCriteria);
		Entity e = (Entity)getPersistenceBrokerTemplate().getObjectByQuery(query);
		if (e != null) {
			for (Principal p : e.getPrincipals()) {
				if (ImplUtils.hasAllQualifiedAttributes(qualifiedRoleAttributes, p.getPrincipalQualifiedRoleAttributes())) {
					return e;
				}
			}
		}
		return null;
	}


	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findPrincipalWithPermission(java.lang.String, java.lang.String, String)
	 */
	public Principal findPrincipalWithPermission(final String principalName, final String permissionName, final String namespaceName) {
		final Criteria c = new Criteria();
		c.addEqualTo("roles.permissions.name", permissionName);

		final Criteria c2 = new Criteria();
		c2.addEqualTo("roles.permissions.namespace.name", namespaceName);
		c.addAndCriteria(c2);

		final Criteria c3 = new Criteria();
		c3.addEqualTo("name", principalName);
		c.addAndCriteria(c3);

		final Query query = QueryFactory.newQuery(Principal.class,
				c);
		final Principal principal = (Principal) getPersistenceBrokerTemplate()
		.getObjectByQuery(query);

		return principal;
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findPersonWithPermission(java.lang.Long, java.lang.String, java.lang.String)
	 */
	public Principal findPersonWithPermission(Long personId, String permissionName,	String namespaceName) {
		final Criteria c = new Criteria();
		c.addEqualTo("roles.permissions.name", permissionName);

		final Criteria c2 = new Criteria();
		c2.addEqualTo("roles.permissions.namespace.name", namespaceName);
		c.addAndCriteria(c2);

		final Criteria c3 = new Criteria();
		c3.addEqualTo("id", personId);
		c.addAndCriteria(c3);
		c.addAndCriteria(personCriteria);

		final Query query = QueryFactory.newQuery(Principal.class,
				c);
		final Principal principal = (Principal) getPersistenceBrokerTemplate()
		.getObjectByQuery(query);

		return principal;
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findPersonWithRole(java.lang.Long, java.lang.String)
	 */
	public Principal findPersonWithRole(Long personId, String roleName) {
		final Criteria c = new Criteria();
		c.addEqualTo("id", personId);
		c.addAndCriteria(personCriteria);

		final Criteria c2 = new Criteria();
		c2.addEqualTo("roles.name", roleName);
		c.addAndCriteria(c2);

		final Query query = QueryFactory.newQuery(Principal.class,
				c);
		final Principal principal = (Principal) getPersistenceBrokerTemplate()
		.getObjectByQuery(query);

		return principal;
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findPrincipalWithRole(java.lang.String, java.lang.String)
	 */
	public Principal findPrincipalWithRole(String principalName, String roleName) {
		final Criteria c = new Criteria();
		c.addEqualTo("name", principalName);

		final Criteria c2 = new Criteria();
		c2.addEqualTo("roles.name", roleName);
		c.addAndCriteria(c2);

		final Query query = QueryFactory.newQuery(Principal.class,
				c);
		final Principal principal = (Principal) getPersistenceBrokerTemplate()
		.getObjectByQuery(query);

		return principal;
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findEntityWithRole(java.lang.Long, java.lang.String)
	 */
	public Entity findEntityWithRole(Long entityId, String roleName) {
		final Criteria c = new Criteria();
		c.addEqualTo("id", entityId);

		final Criteria c2 = new Criteria();
		c2.addEqualTo("principals.roles.name", roleName);
		c.addAndCriteria(c2);

		final Query query = QueryFactory.newQuery(Entity.class,
				c);
		final Entity entity = (Entity) getPersistenceBrokerTemplate()
		.getObjectByQuery(query);

		return entity;
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findEntityWithPermission(java.lang.Long, java.lang.String, java.lang.String)
	 */
	public Entity findEntityWithPermission(Long entityId, String permissionName, String namespaceName) {
		final Criteria c = new Criteria();
		c.addEqualTo("id", entityId);

		final Criteria c2 = new Criteria();
		c2.addEqualTo("principals.roles.permissions.namespace.name", namespaceName);
		c.addAndCriteria(c2);

		final Criteria c3 = new Criteria();
		c3.addEqualTo("principals.roles.permissions.name", permissionName);
		c.addAndCriteria(c3);

		final Query query = QueryFactory.newQuery(Entity.class,
				c);
		final Entity entity = (Entity) getPersistenceBrokerTemplate()
		.getObjectByQuery(query);

		return entity;
	}

	/**
	 *
	 *
	 * @see org.kuali.rice.kim.dao.KIMServicesDao#findGroupWithRole(java.lang.String, java.lang.String)
	 */
	public Group findGroupWithRole(String groupName, String roleName) {
		final Criteria c = new Criteria();
		c.addEqualTo("name", groupName);

		final Criteria c2 = new Criteria();
		c2.addEqualTo("roles.name", roleName);
		c.addAndCriteria(c2);

		final Query query = QueryFactory.newQuery(Group.class,
				c);
		final Group group = (Group) getPersistenceBrokerTemplate()
		.getObjectByQuery(query);

		return group;
	}
}
