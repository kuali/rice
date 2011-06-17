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

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.framework.type.KimRoleTypeService;

import java.util.ArrayList;
import java.util.List;

public abstract class PassThruRoleTypeServiceBase implements KimRoleTypeService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PassThruRoleTypeServiceBase.class);
	
	public static final String UNMATCHABLE_QUALIFICATION = "!~!~!~!~!~";

    @Override
	public abstract AttributeSet convertQualificationForMemberRoles(String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, AttributeSet qualification);
    
    @Override
	public List<RoleMembershipInfo> doRoleQualifiersMatchQualification(AttributeSet qualification, List<RoleMembershipInfo> roleMemberList) {
        return roleMemberList;
    }

    @Override
	public boolean doesRoleQualifierMatchQualification(AttributeSet qualification, AttributeSet roleQualifier) {
        return true;
    }

    @Override
	public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        return new ArrayList<RoleMembershipInfo>(0);
    }
    
    @Override
	public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification) {
        return false;
    }

    @Override
	public boolean isApplicationRoleType() {
        return false;
    }

    public List<String> getAcceptedAttributeNames() {
        return new ArrayList<String>(0);
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
    	return new ArrayList<String>(0);
    }

    public boolean supportsAttributes(List<String> attributeNames) {
        return true;
    }

    public AttributeSet translateInputAttributeSet(AttributeSet inputAttributeSet) {
        return inputAttributeSet;
    }

    @Override
	public AttributeSet validateAttributes(String kimTypeId, AttributeSet attributes) {
        return null;
    }
    
    @Override
	public List<RoleMembershipInfo> sortRoleMembers(List<RoleMembershipInfo> roleMembers) {
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
	public boolean validateUniqueAttributes(String kimTypeId, AttributeSet newAttributes, AttributeSet oldAttributes){
        return true;
    }

    @Override
	public AttributeSet validateUnmodifiableAttributes(String kimTypeId, AttributeSet mainAttributes, AttributeSet delegationAttributes){
        return new AttributeSet();
    }
    
    @Override
	public List<String> getUniqueAttributes(String kimTypeId){
        return new ArrayList<String>();
    }
    
	@Override
	public AttributeSet validateAttributesAgainstExisting(String kimTypeId, AttributeSet newAttributes, AttributeSet oldAttributes){
		return new AttributeSet();
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
