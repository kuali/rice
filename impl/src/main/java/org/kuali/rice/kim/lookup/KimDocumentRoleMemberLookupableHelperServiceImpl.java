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
package org.kuali.rice.kim.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimDocumentRoleMemberLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

	private static final long serialVersionUID = 1L;
	private UiDocumentService uiDocumentService;
	
	@SuppressWarnings("unchecked")
	protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
		List<KimDocumentRoleMember> searchResults = new ArrayList<KimDocumentRoleMember>();
		IdentityManagementRoleDocument roleDocument = (IdentityManagementRoleDocument)GlobalVariables.getUserSession().retrieveObject(KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_SHORT_KEY);
		String memberId = fieldValues.get(KimConstants.PrimaryKeyConstants.MEMBER_ID);
		String memberTypeCode = fieldValues.get(KIMPropertyConstants.KimMember.MEMBER_TYPE_CODE);
		String memberName = fieldValues.get(KimConstants.KimUIConstants.MEMBER_NAME);
		String memberNamespaceCode = fieldValues.get(KimConstants.KimUIConstants.MEMBER_NAMESPACE_CODE);
		String activeFromDate = fieldValues.get(KIMPropertyConstants.KimMember.ACTIVE_FROM_DATE);
		String activeToDate = fieldValues.get(KIMPropertyConstants.KimMember.ACTIVE_TO_DATE);
		if(roleDocument!=null){
			List<KimDocumentRoleMember> currentRoleMembers = roleDocument.getMembers();
			if(currentRoleMembers!=null && !currentRoleMembers.isEmpty()){
				for(KimDocumentRoleMember currentRoleMember: currentRoleMembers){
					if((StringUtils.isEmpty(memberId) || (StringUtils.isNotEmpty(memberId) && memberId.equals(currentRoleMember.getMemberId())))
							&& (StringUtils.isEmpty(memberTypeCode) || (StringUtils.isNotEmpty(memberTypeCode) && memberTypeCode.equals(currentRoleMember.getMemberTypeCode())))
							&& (StringUtils.isEmpty(memberName) || (StringUtils.isNotEmpty(memberName) && memberName.equals(currentRoleMember.getMemberName())))
							&& (StringUtils.isEmpty(memberNamespaceCode) || (StringUtils.isNotEmpty(memberNamespaceCode) && memberNamespaceCode.equals(currentRoleMember.getMemberNamespaceCode())))
							&& (StringUtils.isEmpty(activeFromDate) || (StringUtils.isNotEmpty(activeFromDate) && activeFromDate.equals(currentRoleMember.getActiveFromDate())))
							&& (StringUtils.isEmpty(activeToDate) || (StringUtils.isNotEmpty(activeToDate) && activeToDate.equals(currentRoleMember.getActiveToDate())))){
						searchResults.add(currentRoleMember);
					}
				}
			}
		} else{
			searchResults = getUiDocumentService().getRoleMembers(fieldValues);
		}
		return searchResults;
	}

	public UiDocumentService getUiDocumentService() {
		if ( uiDocumentService == null ) {
			uiDocumentService = KIMServiceLocator.getUiDocumentService();
		}
		return uiDocumentService;
	}
}