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
import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.framework.type.KimDelegationTypeService;
import org.kuali.rice.kim.framework.type.KimRoleTypeService;
import org.kuali.rice.kim.impl.type.KimTypeServiceBase;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimRoleTypeServiceBase extends KimTypeServiceBase implements KimRoleTypeService, KimDelegationTypeService {

	private static final Logger LOG = Logger.getLogger(KimRoleTypeServiceBase.class);
	
	/**
	 * Performs a simple check that the qualifier on the role matches the qualification.
	 * Extra qualification attributes are ignored.
	 * 
	 * @see KimRoleTypeService#doesRoleQualifierMatchQualification(Attributes, Attributes)
	 */

	public boolean doesRoleQualifierMatchQualification(Attributes qualification, Attributes roleQualifier) {
		Attributes translatedQualification = translateInputAttributes(qualification);
		validateRequiredAttributesAgainstReceived(translatedQualification);
		return performMatch(translatedQualification, roleQualifier);
	}
	
	/**
	 * @see org.kuali.rice.kim.framework.type.KimRoleTypeService#doRoleQualifiersMatchQualification(Attributes, List)
	 */
	public List<RoleMembership> doRoleQualifiersMatchQualification(Attributes qualification, List<RoleMembership> roleMemberList) {
		Attributes translatedQualification = translateInputAttributes(qualification);
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
	 * @see org.kuali.rice.kim.framework.type.KimRoleTypeService#getRoleMembersFromApplicationRole(String, String, Attributes)
	 */
	public List<RoleMembership> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, Attributes qualification) {
		validateRequiredAttributesAgainstReceived(qualification);
		if ( !isApplicationRoleType() ) {
			throw new UnsupportedOperationException( this.getClass().getName() + " is not an application role." );
		} else {
			throw new UnsupportedOperationException( this.getClass().getName() + " is an application role type but has not overridden this method." );
		}
	}

	/**
	 * This simple initial implementation just calls  
	 * {@link #getRoleMembersFromApplicationRole(String, String, Attributes)} and checks the results.
	 *
	 */
	public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, Attributes qualification) {
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
	 * @see org.kuali.rice.kim.framework.type.KimRoleTypeService#isApplicationRoleType()
	 */
	public boolean isApplicationRoleType() {
		return false;
	}
		
	/**
	 * This base implementation simply returns the passed in Attributes.
	 * 
	 * @see org.kuali.rice.kim.framework.type.KimRoleTypeService#convertQualificationForMemberRoles(String, String, String, String, Attributes)
	 */
	public Attributes convertQualificationForMemberRoles(String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, Attributes qualification) {
		return qualification;
	}
	
	/**
	 * Base implementation: no sorting.  Just returns the input list.
	 * 
	 * @see org.kuali.rice.kim.framework.type.KimRoleTypeService#sortRoleMembers(java.util.List)
	 */
	public List<RoleMembership> sortRoleMembers(List<RoleMembership> roleMembers) {
		return roleMembers;
	}
	
	/**
	 * This base implementation does nothing but log that the method was called.
	 * 
	 * @see org.kuali.rice.kim.framework.type.KimRoleTypeService#principalInactivated(java.lang.String, java.lang.String, java.lang.String)
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
	public boolean doesDelegationQualifierMatchQualification(Attributes qualification, Attributes roleQualifier) {
		Attributes translatedQualification = translateInputAttributes(qualification);
		validateRequiredAttributesAgainstReceived(translatedQualification);
		return performMatch(translatedQualification, roleQualifier);
	}

	/**
	 * Returns true as a default
	 * 
	 * @see org.kuali.rice.kim.framework.type.KimRoleTypeService#shouldCacheRoleMembershipResults(java.lang.String, java.lang.String)
	 */
	public boolean shouldCacheRoleMembershipResults(String namespaceCode,
			String roleName) {
		return true;
	}

	public List<String> getQualifiersForExactMatch() {    
		return new ArrayList<String>(); 
	}
	
}
