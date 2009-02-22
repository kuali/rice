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
package org.kuali.rice.kim.service.support.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.translators.PrincipalNameToPrincipalIdTranslator;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimConstants;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PrincipalDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {
	
	private static IdentityManagementService identityManagementService;
	
	/**
	 * 
	 */
	public PrincipalDerivedRoleTypeServiceImpl() {
		addAcceptedAttributeName( KIMPropertyConstants.Person.PRINCIPAL_ID );
		addAcceptedAttributeName( KIMPropertyConstants.Person.PRINCIPAL_NAME );
		addAttributeTranslator( new PrincipalNameToPrincipalIdTranslator() );
	}
	
	
	/**
	 * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	public boolean performMatch(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet) {
		return true;
	}

	/**
	 * Since this is potentially the entire set of users, just check the qualification for the user we are interested in and return it.
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getRoleMembersFromApplicationRole(String, String, AttributeSet)
	 */
	@Override
    public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
		ArrayList<RoleMembershipInfo> tempIdList = new ArrayList<RoleMembershipInfo>();
		if ( qualification == null ) {
			return tempIdList;
		}
		qualification = translateInputAttributeSet(qualification);
		// check that the principal ID is not null
		String principalId = qualification.get( KimAttributes.PRINCIPAL_ID );
		if ( hasApplicationRole(principalId, null, namespaceCode, roleName, qualification)) {
	        tempIdList.add( new RoleMembershipInfo(null/*roleId*/, null, principalId, KimRole.PRINCIPAL_MEMBER_TYPE, null) );
		}
		return tempIdList;
	}
	
	@Override
	public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification) {
        // check that the principal exists and is active
        KimPrincipal principal = getIdentityManagementService().getPrincipal( principalId );
        if ( principal == null || !principal.isActive() ) {
            return false;
        }
        // check that the entity is active
        KimEntityDefaultInfo entity = getIdentityManagementService().getEntityDefaultInfo( principal.getEntityId() );
        if ( entity == null || !entity.isActive() ) {
            return false;
        }
        return true;
	}
	
	protected IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KIMServiceLocator.getIdentityManagementService();
		}
		return identityManagementService;
	}
}
