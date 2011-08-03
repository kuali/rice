/*
 * Copyright 2007-2008 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.framework.common.delegate.DelegationTypeService;
import org.kuali.rice.kim.framework.role.RoleTypeService;
import org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimRoleTypeServiceBase extends DataDictionaryTypeServiceBase implements RoleTypeService, DelegationTypeService {

	private static final Logger LOG = Logger.getLogger(KimRoleTypeServiceBase.class);
	
	/**
	 * Performs a simple check that the qualifier on the role matches the qualification.
	 * Extra qualification attributes are ignored.
	 * 
	 * @see org.kuali.rice.kim.framework.role.RoleTypeService#doesRoleQualifierMatchQualification(Map<String, String>, Map<String, String>)
	 */

	public boolean doesRoleQualifierMatchQualification(Map<String, String> qualification, Map<String, String> roleQualifier) {
		Map<String, String> translatedQualification = translateInputAttributes(qualification);
		validateRequiredAttributesAgainstReceived(translatedQualification);
		return performMatch(translatedQualification, roleQualifier);
	}
	
	/**
	 * @see org.kuali.rice.kim.framework.role.RoleTypeService#doRoleQualifiersMatchQualification(Map<String, String>, List)
	 */
	public List<RoleMembership> doRoleQualifiersMatchQualification(Map<String, String> qualification, List<RoleMembership> roleMemberList) {
		Map<String, String> translatedQualification = translateInputAttributes(qualification);
		validateRequiredAttributesAgainstReceived(translatedQualification);
		List<RoleMembership> matchingMemberships = new ArrayList<RoleMembership>();
		for ( RoleMembership roleMembership : roleMemberList ) {
			if ( performMatch( translatedQualification, roleMembership.getQualifier() ) ) {
				matchingMemberships.add( roleMembership );
			}
		}
		return matchingMemberships;
	}

	/**
	 * Return an empty list since this method should not be called by the role service for this service type.
	 * Subclasses which are application role types should override this method.
	 * 
	 * @see org.kuali.rice.kim.framework.role.RoleTypeService#getRoleMembersFromApplicationRole(String, String, Map<String, String>)
	 */
	public List<RoleMembership> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, Map<String, String> qualification) {
		validateRequiredAttributesAgainstReceived(qualification);
		if ( !isApplicationRoleType() ) {
			throw new UnsupportedOperationException( this.getClass().getName() + " is not an application role." );
		} else {
			throw new UnsupportedOperationException( this.getClass().getName() + " is an application role type but has not overridden this method." );
		}
	}

	/**
	 * This simple initial implementation just calls  
	 * {@link #getRoleMembersFromApplicationRole(String, String, Map<String, String>)} and checks the results.
	 *
	 */
	public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String, String> qualification) {
		if ( !isApplicationRoleType() ) {
			throw new UnsupportedOperationException( this.getClass().getName() + " is not an application role." );
		}
		// if principal ID given, check if it is in the list generated from the getPrincipalIdsFromApplicationRole method
		if ( StringUtils.isNotBlank( principalId ) ) {
		    List<RoleMembership> members = getRoleMembersFromApplicationRole(namespaceCode, roleName, qualification);
		    for ( RoleMembership rm : members ) {
		    	if ( StringUtils.isBlank( rm.getRoleMemberId() ) ) {
		    		continue;
		    	}
		        if ( rm.getMemberTypeCode().equals( Role.PRINCIPAL_MEMBER_TYPE ) ) {
		            if ( rm.getMemberId().equals( principalId ) ) {
		                return true;
		            }
		        } else { // groups
		            if ( groupIds != null 
		                    && groupIds.contains(rm.getMemberId())) {
		                return true;
		            }
		        }
			}
		}
		return false;
	}
	
	/**
	 * Default to not being an application role type.  Always returns false.
	 * 
	 * @see org.kuali.rice.kim.framework.role.RoleTypeService#isApplicationRoleType()
	 */
	public boolean isApplicationRoleType() {
		return false;
	}
		
	/**
	 * This base implementation simply returns the passed in Attributes.
	 * 
	 * @see org.kuali.rice.kim.framework.role.RoleTypeService#convertQualificationForMemberRoles(String, String, String, String, Map<String, String>)
	 */
	public Map<String, String> convertQualificationForMemberRoles(String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, Map<String, String> qualification) {
		return qualification;
	}
	
	/**
	 * Base implementation: no sorting.  Just returns the input list.
	 * 
	 * @see org.kuali.rice.kim.framework.role.RoleTypeService#sortRoleMembers(java.util.List)
	 */
	public List<RoleMembership> sortRoleMembers(List<RoleMembership> roleMembers) {
		return roleMembers;
	}
	
	/**
	 * This base implementation does nothing but log that the method was called.
	 * 
	 * @see org.kuali.rice.kim.framework.role.RoleTypeService#principalInactivated(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void principalInactivated(String principalId, String namespaceCode,
			String roleName) {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "Principal Inactivated called: principalId="+principalId+" role=" + namespaceCode + "/" + roleName );
		}
		// base implementation - do nothing
	}
	
	/**
	 * Performs a simple check that the qualifier on the delegation matches the qualification.
	 * Extra qualification attributes are ignored.
	 *
	 */
	public boolean doesDelegationQualifierMatchQualification(Map<String, String> qualification, Map<String, String> roleQualifier) {
		Map<String, String> translatedQualification = translateInputAttributes(qualification);
		validateRequiredAttributesAgainstReceived(translatedQualification);
		return performMatch(translatedQualification, roleQualifier);
	}

	/**
	 * Returns true as a default
	 * 
	 * @see org.kuali.rice.kim.framework.role.RoleTypeService#shouldCacheRoleMembershipResults(java.lang.String, java.lang.String)
	 */
	public boolean shouldCacheRoleMembershipResults(String namespaceCode,
			String roleName) {
		return true;
	}

	public List<String> getQualifiersForExactMatch() {    
		return new ArrayList<String>(); 
	}
	
}
