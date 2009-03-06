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
 * See the License for the specific language governing members and
 * limitations under the License.
 */
package org.kuali.rice.kim.rules.ui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.options.MemberTypeValuesFinder;
import org.kuali.rice.kim.bo.ui.GroupDocumentMember;
import org.kuali.rice.kim.document.IdentityManagementGroupDocument;
import org.kuali.rice.kim.rule.event.ui.AddGroupMemberEvent;
import org.kuali.rice.kim.rule.ui.AddGroupMemberRule;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupDocumentMemberRule extends DocumentRuleBase implements AddGroupMemberRule {

	private static final String ERROR_PATH = "document.member.memberId";

	public boolean processAddGroupMember(AddGroupMemberEvent addGroupMemberEvent){
		GroupDocumentMember newMember = addGroupMemberEvent.getMember();
		IdentityManagementGroupDocument document = (IdentityManagementGroupDocument)addGroupMemberEvent.getDocument();
	    boolean rulePassed = true;

        if (newMember == null || StringUtils.isBlank(newMember.getMemberId())){
            GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Member"});
            return false;
        }
	    if(MemberTypeValuesFinder.MEMBER_TYPE_ROLE_CODE.equals(newMember.getMemberTypeCode())){
	    	if(!validAssignGroup(newMember, document))
	    		return false;
	    }
	    int i = 0;
	    for (GroupDocumentMember member: document.getMembers()){
	    	if (member.getMemberId().equals(newMember.getMemberId())){
	            rulePassed = false;
	            GlobalVariables.getErrorMap().putError("document.members["+i+"].memberId", RiceKeyConstants.ERROR_DUPLICATE_ENTRY, new String[] {"Member"});
	    	}
	    	i++;
	    }
		return rulePassed;
	} 

	private boolean validAssignGroup(GroupDocumentMember groupMember, IdentityManagementGroupDocument document){
        boolean rulePassed = true;
		Map<String,String> groupDetails = new HashMap<String,String>();
    	Map<String, String> criteria = new HashMap<String, String>();
    	criteria.put("groupId", groupMember.getMemberId());
    	KimGroupImpl groupImpl = (KimGroupImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimGroupImpl.class, criteria);
		groupDetails.put(KimAttributes.NAMESPACE_CODE, groupImpl.getNamespaceCode());
		groupDetails.put(KimAttributes.ROLE_NAME, groupImpl.getGroupName());
		if (!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorizedByTemplate(
				document, 
				KimConstants.NAMESPACE_CODE, 
				KimConstants.PermissionTemplateNames.ASSIGN_GROUP, 
				GlobalVariables.getUserSession().getPerson().getPrincipalId(), 
				groupDetails, null)){
            GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_ASSIGN_RESPONSIBILITY, 
            		new String[] {groupImpl.getNamespaceCode(), groupImpl.getGroupName()});
            rulePassed = false;
		}
		return rulePassed;
	}

}