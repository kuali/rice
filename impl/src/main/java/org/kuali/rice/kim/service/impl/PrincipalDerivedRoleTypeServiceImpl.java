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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kim.service.translators.PrincipalNameToPrincipalIdTranslator;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PrincipalDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {
	
	private static IdentityManagementService identityManagementService;
	private List<String> roleGroupIds = Collections.unmodifiableList( new ArrayList<String>(0) );
	
	/**
	 * 
	 */
	public PrincipalDerivedRoleTypeServiceImpl() {
		addAcceptedAttributeName( "principalId" );
		addAcceptedAttributeName( "principalName" );
		addAttributeTranslator( new PrincipalNameToPrincipalIdTranslator() );
	}
	
	
	/**
	 * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	public boolean performMatch(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet) {
		return true;
//		// check that the principal ID is not null
//		String principalId = inputAttributeSet.get( "principalId" );
//		if ( StringUtils.isBlank( principalId )  ) {
//			return false;
//		}
//		// check that the principal exists and is active
//		KimPrincipal principal = getIdentityManagementService().getPrincipal( principalId );
//		if ( principal == null || !principal.isActive() ) {
//			return false;
//		}
//		// check that the entity is active
//		KimEntity entity = getIdentityManagementService().getEntity( principal.getEntityId() );
//		if ( entity == null || !entity.isActive() ) {
//			return false;
//		}
//		return true;
	}
	
	/**
	 * This is an application role type.  Always returns true.
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#isApplicationRoleType()
	 */
	@Override
	public boolean isApplicationRoleType() {
		return true;
	}

	/**
	 * Since this is potentially the entire set of users, just check the qualification for the user we are interested in and return it.
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	public List<String> getPrincipalIdsFromApplicationRole(String namespaceCode, String roleName,
			AttributeSet qualification) {
		ArrayList<String> tempIdList = new ArrayList<String>();
		if ( qualification == null ) {
			return tempIdList;
		}
		qualification = translateInputAttributeSet(qualification);
		// check that the principal ID is not null
		String principalId = qualification.get( "principalId" );
		if ( hasApplicationRole(principalId, null, namespaceCode, roleName, qualification)) {
	        tempIdList.add( principalId );
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
        KimEntity entity = getIdentityManagementService().getEntity( principal.getEntityId() );
        if ( entity == null || !entity.isActive() ) {
            return false;
        }
        return true;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getGroupIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	public List<String> getGroupIdsFromApplicationRole(String namespaceCode, String roleName,
			AttributeSet qualification) {
		return roleGroupIds;
	}
    
	protected IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KIMServiceLocator.getIdentityManagementService();
		}
		return identityManagementService;
	}
}
