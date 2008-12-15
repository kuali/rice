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
package org.kuali.rice.kim.service.support.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.KimRoleTypeService;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimRoleTypeServiceBase extends KimTypeServiceBase implements KimRoleTypeService {

	/**
	 * Performs a simple check that the qualifier on the role matches the qualification.
	 * Extra qualification attributes are ignored.
	 * 
	 * @see KimRoleTypeService#doesRoleQualifierMatchQualification(AttributeSet, AttributeSet)
	 */
	public boolean doesRoleQualifierMatchQualification(AttributeSet qualification, AttributeSet roleQualifier) {
		return performMatch(translateInputAttributeSet(qualification), roleQualifier);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#doRoleQualifiersMatchQualification(AttributeSet, List)
	 */
	public List<RoleMembershipInfo> doRoleQualifiersMatchQualification(AttributeSet qualification, List<RoleMembershipInfo> roleMemberList) {
		AttributeSet translatedQualification = translateInputAttributeSet(qualification);
		List<RoleMembershipInfo> matchingMemberships = new ArrayList<RoleMembershipInfo>();
		for ( RoleMembershipInfo rmi : roleMemberList ) {
			if ( performMatch( translatedQualification, rmi.getQualifier() ) ) {
				matchingMemberships.add( rmi );
			}
		}
		return matchingMemberships;
	}

	/**
	 * Return an empty list since this method should not be called by the role service for this service type.
	 * Subclasses which are application role types should override this method.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#getPrincipalIdsFromApplicationRole(String, String, AttributeSet)
	 */
	public List<String> getPrincipalIdsFromApplicationRole(String namespaceCode, String roleName,
			AttributeSet qualification) {
		if ( !isApplicationRoleType() ) {
			throw new UnsupportedOperationException( this.getClass().getName() + " is not an application role." );
		} else {
			throw new UnsupportedOperationException( this.getClass().getName() + " is an application role type but has not overridden this method." );
		}
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#getGroupIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public List<String> getGroupIdsFromApplicationRole(String namespaceCode,
			String roleName, AttributeSet qualification) {
		if ( !isApplicationRoleType() ) {
			throw new UnsupportedOperationException( this.getClass().getName() + " is not an application role." );
		} else {
			throw new UnsupportedOperationException( this.getClass().getName() + " is an application role type but has not overridden this method." );
		}
	}
	
	/**
	 * This simple initial implementation just calls the {@link #getPrincipalIdsFromApplicationRole(String, String, AttributeSet)} and 
	 * {@link #getGroupIdsFromApplicationRole(String, String, AttributeSet)} methods and checks their results.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#hasApplicationRole(java.lang.String, java.util.List, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public boolean hasApplicationRole(String principalId, List<String> groupIds,
			String namespaceCode, String roleName, AttributeSet qualification) {
		if ( !isApplicationRoleType() ) {
			throw new UnsupportedOperationException( this.getClass().getName() + " is not an application role." );
		}
		// if principal ID given, check if it is in the list generated from the getPrincipalIdsFromApplicationRole method
		if ( StringUtils.isNotBlank( principalId ) ) {
			if ( getPrincipalIdsFromApplicationRole(namespaceCode, roleName, qualification).contains(principalId) ) {
				return true;
			}
		}
		// if any group IDs were given, check if any one of them is in the list returned from getGroupIdsFromApplicationRole
		if ( groupIds != null && !groupIds.isEmpty() ) {
			List<String> roleGroupIds = getGroupIdsFromApplicationRole(namespaceCode, roleName, qualification);
			for ( String groupId : groupIds ) {
				if ( roleGroupIds.contains(groupId) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Default to not being an application role type.  Always returns false.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#isApplicationRoleType()
	 */
	public boolean isApplicationRoleType() {
		return false;
	}
		
	/**
	 * Simple implementation, simply returns the passed in qualification in a single-element list.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#getAllImpliedQualifications(AttributeSet)
	 */
	public List<AttributeSet> getAllImpliedQualifications(
			AttributeSet qualification) {
		ArrayList<AttributeSet> impliedQualifications = new ArrayList<AttributeSet>( 1 );
		impliedQualifications.add(qualification);
		return impliedQualifications;
	}
	
	/**
	 * Simple implementation, simply returns the passed in qualification in a single-element list.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#getAllImplyingQualifications(AttributeSet)
	 */
	public List<AttributeSet> getAllImplyingQualifications(
			AttributeSet qualification) {
		ArrayList<AttributeSet> implyingQualifications = new ArrayList<AttributeSet>( 1 );
		implyingQualifications.add(qualification);
		return implyingQualifications;
	}
	/**
	 * No conversion performed.  Simply returns the passed in Map.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#convertQualificationAttributesToRequired(AttributeSet)
	 */
	public AttributeSet convertQualificationAttributesToRequired(
			AttributeSet qualificationAttributes) {
		return qualificationAttributes;
	}

	/**
	 * This base implementation simply returns the passed in AttributeSet.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#convertQualificationForMemberRoles(org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public AttributeSet convertQualificationForMemberRoles(String namespaceCode, String roleName, AttributeSet qualification) {
		return qualification;
	}
}
