/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.service.support.impl;


import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.util.KimConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PrincipalDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {
	
	private IdentityService identityService;
	
	{
		requiredAttributes.add( KimConstants.AttributeConstants.PRINCIPAL_ID );
		checkRequiredAttributes = false;
	}

	@Override
	public boolean performMatch(Map<String, String> inputAttributes, Map<String, String> storedAttributes) {
		return true;
	}

	/**
	 * Since this is potentially the entire set of users, just check the qualification for the user we are interested in and return it.
	 */
	@Override
    public List<RoleMembership> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, Map<String, String> qualification) {
		ArrayList<RoleMembership> tempIdList = new ArrayList<RoleMembership>();
		if ( qualification == null || qualification.isEmpty() ) {
			return tempIdList;
		}
		qualification = translateInputAttributes(qualification);
		// check that the principal ID is not null
		String principalId = qualification.get( KimConstants.AttributeConstants.PRINCIPAL_ID );
		if ( hasApplicationRole(principalId, null, namespaceCode, roleName, qualification)) {
	        tempIdList.add( RoleMembership.Builder.create(null/*roleId*/, null, principalId, Role.PRINCIPAL_MEMBER_TYPE, null).build());
		}
		return tempIdList;
	}
	
	@Override
	public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String, String> qualification) {
        // check that the principal exists and is active
        Principal principal = getIdentityService().getPrincipal( principalId );
        if ( principal == null || !principal.isActive() ) {
            return false;
        }
        // check that the identity is active
        EntityDefault entity = getIdentityService().getEntityDefault( principal.getEntityId() );
        return entity != null && entity.isActive();
	}
	
	protected IdentityService getIdentityService() {
		if ( identityService == null ) {
			identityService = KimApiServiceLocator.getIdentityService();
		}
		return identityService;
	}
}
