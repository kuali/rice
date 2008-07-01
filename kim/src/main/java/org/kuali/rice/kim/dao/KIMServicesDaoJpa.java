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
package org.kuali.rice.kim.dao;

import java.util.Collection;

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
public class KIMServicesDaoJpa implements KIMServicesDao {

	public Collection<Principal> findAllPersons() {
		return null;
	}

	public Principal findPerson(final Long personId) {
		return null;
	}

	public Collection<EntityAttribute> getPersonAttributesForNamespace(
			Long personId, String namespaceName) {
		return null;
	}

	public String getPersonAttributeValue(Long personId, String attributeName,
			String namespaceName) {
		return null;
	}

	public String getAttributeValueForNamespace(String attributeName, String namespaceName,
			final Long entityId) {
		return null;
	}

	public Collection<EntityAttribute> getAttributesForNamespace(
			String namespaceName, final Long entityId) {
		return null;
	}

	public Collection<PrincipalQualifiedRole> findAllPersonsWithQualifiedRole(final String roleName) {
		return null;
	}

	public Collection<PrincipalQualifiedRole> findAllPrincipalsWithQualifiedRole(final String roleName) {
		return null;
	}

	public Collection<GroupQualifiedRole> findAllGroupsWithQualifiedRole(final String roleName) {
		return null;
	}
	
	public Collection<Principal> findAllPersonsWithRole(final String roleName) {
		return null;
	}
}
