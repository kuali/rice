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
package org.kuali.rice.kew.role.service.impl;


import org.kuali.rice.kew.api.action.ActionRequestStatus;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.framework.type.KimDelegationTypeService;
import org.kuali.rice.kim.framework.type.KimRoleTypeService;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class ActionRequestDerivedRoleTypeServiceImpl extends
		KimDerivedRoleTypeServiceBase implements KimRoleTypeService, KimDelegationTypeService {
	private static final String NON_AD_HOC_APPROVE_REQUEST_RECIPIENT_ROLE_NAME = "Non-Ad Hoc Approve Request Recipient";
	private static final String APPROVE_REQUEST_RECIPIENT_ROLE_NAME = "Approve Request Recipient";
	private static final String ACKNOWLEDGE_REQUEST_RECIPIENT_ROLE_NAME = "Acknowledge Request Recipient";
	private static final String FYI_REQUEST_RECIPIENT_ROLE_NAME = "FYI Request Recipient";
	protected WorkflowInfo workflowInfo = new WorkflowInfo();

	{
		requiredAttributes.add( KimConstants.AttributeConstants.DOCUMENT_NUMBER );
		checkRequiredAttributes = true;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase#getRoleMembersFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.core.util.Map<String, String>)
	 */
	@Override
    public List<RoleMembership> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, Map<String, String> qualification) {
		List<RoleMembership> members = new ArrayList<RoleMembership>();
		if ( qualification != null && !qualification.isEmpty() ) {
		    String principalId = qualification.get(KimConstants.AttributeConstants.PRINCIPAL_ID);
			if (qualification.containsKey(KimConstants.AttributeConstants.PRINCIPAL_ID)
					&& hasApplicationRole(principalId, null, namespaceCode,
							roleName, qualification)) {
                members.add(RoleMembership.Builder.create(null/*roleId*/, null, principalId, Role.PRINCIPAL_MEMBER_TYPE, null).build());
			}
		}
		return members;
	}

	@Override
	public boolean hasApplicationRole(String principalId,
			List<String> groupIds, String namespaceCode, String roleName,
			Map<String, String> qualification) {
		validateRequiredAttributesAgainstReceived(qualification);
		try {
			if ( (qualification != null && !qualification.isEmpty()))  {
				ActionRequestDTO[] actionRequests = workflowInfo.getActionRequests(qualification.get(KimConstants.AttributeConstants.DOCUMENT_NUMBER), null, principalId);
				if (APPROVE_REQUEST_RECIPIENT_ROLE_NAME.equals(roleName) || NON_AD_HOC_APPROVE_REQUEST_RECIPIENT_ROLE_NAME.equals(roleName)) {
					for ( ActionRequestDTO ar : actionRequests ) {
						if ( ar.getActionRequested().equals( KEWConstants.ACTION_REQUEST_APPROVE_REQ )
								&& ar.getStatus().equals( ActionRequestStatus.ACTIVATED.getCode() ) ) {
							return APPROVE_REQUEST_RECIPIENT_ROLE_NAME.equals(roleName) || (NON_AD_HOC_APPROVE_REQUEST_RECIPIENT_ROLE_NAME.equals(roleName) && !ar.isAdHocRequest());
						}
					}
					return false;
				}
				if (ACKNOWLEDGE_REQUEST_RECIPIENT_ROLE_NAME.equals(roleName)) {
					for ( ActionRequestDTO ar : actionRequests ) {
						if ( ar.getActionRequested().equals( KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ ) 
							&& ar.getStatus().equals( ActionRequestStatus.ACTIVATED.getCode() ) ) {
							return true;
						}
					}
					return false;
				}
				if (FYI_REQUEST_RECIPIENT_ROLE_NAME.equals(roleName)) {
					for ( ActionRequestDTO ar : actionRequests ) {
						if ( ar.getActionRequested().equals( KEWConstants.ACTION_REQUEST_FYI_REQ ) 
							&& ar.getStatus().equals( ActionRequestStatus.ACTIVATED.getCode() ) ) {
							return true;
						}
					}
					return false;
				}
			}
			return false;
		} catch (WorkflowException e) {
			throw new RuntimeException("Unable to load route header", e);
		}
	}

	/**
	 * Returns false, as action requests change quite often so membership in this role is highly volatile
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#shouldCacheRoleMembershipResults(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean shouldCacheRoleMembershipResults(String namespaceCode,
			String roleName) {
		return false;
	}
	
}
