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

import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.framework.type.KimRoleTypeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class PassThruRoleTypeServiceBase implements KimRoleTypeService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PassThruRoleTypeServiceBase.class);
	
	public static final String UNMATCHABLE_QUALIFICATION = "!~!~!~!~!~";

    @Override
	public abstract Map<String, String> convertQualificationForMemberRoles(String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, Map<String, String> qualification);
    
    @Override
	public List<RoleMembership> doRoleQualifiersMatchQualification(Map<String, String> qualification, List<RoleMembership> roleMemberList) {
        return roleMemberList;
    }

    @Override
	public boolean doesRoleQualifierMatchQualification(Map<String, String> qualification, Map<String, String> roleQualifier) {
        return true;
    }

    @Override
	public List<RoleMembership> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, Map<String, String> qualification) {
        return new ArrayList<RoleMembership>(0);
    }
    
    @Override
	public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String, String> qualification) {
        return false;
    }

    @Override
	public boolean isApplicationRoleType() {
        return false;
    }

    public List<String> getAcceptedAttributeNames() {
        return Collections.emptyList();
    }

    @Override
	public AttributeDefinitionMap getAttributeDefinitions(String kimTypeId) {
        return null;
    }

    @Override
	public String getWorkflowDocumentTypeName() {
        return null;
    }
    
    /**
     * @see org.kuali.rice.kim.api.type.KimTypeService#getWorkflowRoutingAttributes(java.lang.String)
     */
    @Override
	public List<String> getWorkflowRoutingAttributes(String routeLevel) {
    	return Collections.emptyList();
    }

    public boolean supportsAttributes(List<String> attributeNames) {
        return true;
    }

    public Map<String, String> translateInputAttributes(Map<String, String> inputAttributes) {
        return inputAttributes;
    }

    @Override
	public Map<String, String> validateAttributes(String kimTypeId, Map<String, String> attributes) {
        return null;
    }
    
    @Override
	public List<RoleMembership> sortRoleMembers(List<RoleMembership> roleMembers) {
        return roleMembers;
    }
    
    

	/**
	 * This base implementation does nothing but log that the method was called.
	 * 
	 * @see org.kuali.rice.kim.framework.type.KimRoleTypeService#principalInactivated(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void principalInactivated(String principalId, String namespaceCode,
			String roleName) {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "Principal Inactivated called: principalId="+principalId+" role=" + namespaceCode + "/" + roleName );
		}
		// base implementation - do nothing
	}

    @Override
	public boolean validateUniqueAttributes(String kimTypeId, Map<String, String> newAttributes, Map<String, String> oldAttributes){
        return true;
    }

    @Override
	public Map<String, String> validateUnmodifiableAttributes(String kimTypeId, Map<String, String> mainAttributes, Map<String, String> delegationAttributes){
        return Collections.emptyMap();
    }
    
    @Override
	public List<String> getUniqueAttributes(String kimTypeId){
        return Collections.emptyList();
    }
    
	@Override
	public Map<String, String> validateAttributesAgainstExisting(String kimTypeId, Map<String, String> newAttributes, Map<String, String> oldAttributes){
		return Collections.emptyMap();
	}

	/**
	 * Returns false by default.
	 * 
	 * @see org.kuali.rice.kim.framework.type.KimRoleTypeService#shouldCacheRoleMembershipResults(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean shouldCacheRoleMembershipResults(String namespaceCode,
			String roleName) {
		return false;
	}

}
