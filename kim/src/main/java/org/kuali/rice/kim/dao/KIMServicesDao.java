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
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.EntityAttribute;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.web.form.GroupQualifiedRole;
import org.kuali.rice.kim.web.form.PrincipalQualifiedRole;

/**
 * This is a description of what this class does - lindholm don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface KIMServicesDao {
	/**
	 *
	 * This method finds all Principals with the Person entity
	 *
	 * @return
	 */
	public Collection<Principal> findAllPersons();

	/**
	 *
	 * This method returns the Principal representing a person
	 *
	 * @param personId
	 * @return
	 */
	public Principal findPerson(final Long personId);

	/**
	 *
	 * This method returns the person Principals attributes for this namespace
	 *
	 * @param personId
	 * @param namespaceName
	 * @return
	 */
	public Collection<EntityAttribute> getPersonAttributesForNamespace(
			Long personId, String namespaceName);

	/**
	 *
	 *This method returns the attribute value of an attribute for a person in a
	 * namespace
	 *
	 * @param personId
	 * @param attributeName
	 * @param namespaceName
	 * @return
	 */
	public String getPersonAttributeValue(Long personId, String attributeName,
			String namespaceName);

	/**
	 *
	 * This method returns the attribute value for an entity in a namespace
	 *
	 * @param attributeName
	 * @param namespaceName
	 * @param entityId
	 * @return
	 */
	public String getAttributeValueForNamespace(String attributeName,
			String namespaceName, final Long entityId);

	/**
	 *
	 * This method returns the Entity Attributes for a namespace
	 *
	 * @param namespaceName
	 * @param entityId
	 * @return
	 */
	public Collection<EntityAttribute> getAttributesForNamespace(
			String namespaceName, final Long entityId);

	/**
	 *
	 * This method returns the Person Principals with Qualified Roles
	 *
	 * @param roleName
	 * @return
	 */
	public Collection<PrincipalQualifiedRole> findAllPersonsWithQualifiedRole(
			final String roleName);

	/**
	 *
	 * This method returns the Principals with Qualified Roles
	 *
	 * @param roleName
	 * @return
	 */
	public Collection<PrincipalQualifiedRole> findAllPrincipalsWithQualifiedRole(
			final String roleName);

	/**
	 *
	 * This method the Groups with Qualified Roles
	 *
	 * @param roleName
	 * @return
	 */
	public Collection<GroupQualifiedRole> findAllGroupsWithQualifiedRole(
			final String roleName);

	// public Collection<Principal> findAllPersonsWithRole(final String
	// roleName);
}
