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
package org.kuali.rice.kew.role.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class ActionRequestDerivedRoleTypeServiceImpl extends
		KimDerivedRoleTypeServiceBase {
	private static final String APPROVE_REQUEST_RECIPIENT_ROLE_NAME = "Approve Request Recipient";
	private static final String ACKNOWLEDGE_REQUEST_RECIPIENT_ROLE_NAME = "Acknowledge Request Recipient";
	private static final String FYI_REQUEST_RECIPIENT_ROLE_NAME = "FYI Request Recipient";
	protected WorkflowInfo workflowInfo = new WorkflowInfo();

	@Override
	public List<String> getPrincipalIdsFromApplicationRole(
			String namespaceCode, String roleName, AttributeSet qualification) {
		List<String> principalIds = new ArrayList<String>();
		if ( qualification != null ) {
			if (qualification.containsKey(KimAttributes.PRINCIPAL_ID)
					&& hasApplicationRole(qualification
							.get(KimAttributes.PRINCIPAL_ID), null, namespaceCode,
							roleName, qualification)) {
				principalIds.add(qualification.get(KimAttributes.PRINCIPAL_ID));
			}
		}
		return principalIds;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasApplicationRole(String principalId,
			List<String> groupIds, String namespaceCode, String roleName,
			AttributeSet qualification) {
		try {
			if ( qualification != null && StringUtils.isNumeric( qualification.get(KimAttributes.DOCUMENT_NUMBER) ) ) {
				ActionRequestDTO[] actionRequests = workflowInfo.getActionRequests(Long
									.parseLong(qualification
											.get(KimAttributes.DOCUMENT_NUMBER)), null, principalId);
				if (APPROVE_REQUEST_RECIPIENT_ROLE_NAME.equals(roleName)) {
					for ( ActionRequestDTO ar : actionRequests ) {
						if ( ar.getActionRequested().equals( KEWConstants.ACTION_REQUEST_APPROVE_REQ )
								&& ar.getStatus().equals( KEWConstants.ACTION_REQUEST_ACTIVATED ) ) {
							return true;
						}
					}
					return false;
				}
				if (ACKNOWLEDGE_REQUEST_RECIPIENT_ROLE_NAME.equals(roleName)) {
					for ( ActionRequestDTO ar : actionRequests ) {
						if ( ar.getActionRequested().equals( KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ ) 
							&& ar.getStatus().equals( KEWConstants.ACTION_REQUEST_ACTIVATED ) ) {
							return true;
						}
					}
					return false;
				}
				if (FYI_REQUEST_RECIPIENT_ROLE_NAME.equals(roleName)) {
					for ( ActionRequestDTO ar : actionRequests ) {
						if ( ar.getActionRequested().equals( KEWConstants.ACTION_REQUEST_FYI_REQ ) 
							&& ar.getStatus().equals( KEWConstants.ACTION_REQUEST_ACTIVATED ) ) {
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
}