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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.collections.ListUtils;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.workgroup.WorkgroupMembershipChangeProcessor;
import org.kuali.rice.kim.service.GroupInternalService;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;
import org.kuali.rice.ksb.service.KSBServiceLocator;

/**
 * This is a description of what this class does 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupInternalServiceImpl implements GroupInternalService {
    public ActionRequestService getActionRequestService() {
        return (ActionRequestService) KEWServiceLocator.getActionRequestService();
    }
    
    public ActionListService getActionListService() {
    	return (ActionListService) KEWServiceLocator.getActionListService();
    }
	
    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.GroupInternalService#updateActionItemsForWorkgroupChange(java.lang.String, java.lang.String)
	 */
    public void updateActionItemsForWorkgroupChange(String oldKimGroupId, String newKimGroupId)	{
        IdentityManagementService ims = KIMServiceLocator.getIdentityManagementService();
        List<String> oldPrincipalIds = ims.getGroupMemberPrincipalIds(oldKimGroupId);
        List<String> newPrincipalIds = ims.getGroupMemberPrincipalIds(newKimGroupId);
        MembersDiff membersDiff = getMembersDiff(oldPrincipalIds, newPrincipalIds);
        for (String removedPrincipalId : membersDiff.getRemovedPrincipalIds()) {
            KSBXMLService workgroupMembershipChangeProcessor = (KSBXMLService) KSBServiceLocator.getMessageHelper()
            .getServiceAsynchronously(new QName(MessageServiceNames.WORKGROUP_MEMBERSHIP_CHANGE_SERVICE));
            try {
                workgroupMembershipChangeProcessor.invoke(WorkgroupMembershipChangeProcessor
                        .getMemberRemovedMessageContents(removedPrincipalId, newKimGroupId));
            } catch (Exception e) {
                throw new WorkflowRuntimeException(e);
            }
        }
        for (String addedPrincipalId : membersDiff.getAddedPrincipalIds()) {
            KSBXMLService workgroupMembershipChangeProcessor = (KSBXMLService) KSBServiceLocator.getMessageHelper()
            .getServiceAsynchronously(new QName(MessageServiceNames.WORKGROUP_MEMBERSHIP_CHANGE_SERVICE));
            try {
                workgroupMembershipChangeProcessor.invoke(WorkgroupMembershipChangeProcessor.getMemberAddedMessageContents(
                        addedPrincipalId, oldKimGroupId));
            } catch (Exception e) {
                throw new WorkflowRuntimeException(e);
            }

        }
    }


    /**
     * Update the user's Action List to reflect their addition to the given Workgroup.
     */
    public void updateActionListForUserAddedToGroup(String principalId, String groupId) {
        // first verify that the user is still a member of the workgroup
    	if(KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(principalId, groupId))
    	{
    		List<ActionRequestValue> actionRequests = new ArrayList<ActionRequestValue>();
    		List<String> allGroupsToCheck = KIMServiceLocator.getIdentityManagementService().getParentGroupIds(groupId);
            allGroupsToCheck.add(0, groupId);
            for (String groupToCheckId : allGroupsToCheck) {
                actionRequests.addAll(getActionRequestService().findActivatedByGroup(groupToCheckId));
            }
            for (Iterator requestIt = actionRequests.iterator(); requestIt.hasNext();) {
                ActionRequestValue request = (ActionRequestValue) requestIt.next();
                ActionItem item = getActionListService().createActionItemForActionRequest(request);
                item.setPrincipalId(principalId);
                getActionListService().saveActionItem(item);
            }
        }
    }
    
    /**
     * Update the user's Action List to reflect their removal from the given Workgroup.
     */
    public void updateActionListForUserRemovedFromGroup(String principalId, String groupId) {
        // first verify that the user is no longer a member of the workgroup
    	if(!KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(principalId, groupId))
    	{
    		List<String> allGroupsToCheck = KIMServiceLocator.getIdentityManagementService().getParentGroupIds(groupId);
            allGroupsToCheck.add(0, groupId);
            Collection<ActionItem> actionItems = getActionListService().findByPrincipalId(principalId);
    		for (Iterator<ActionItem> itemIt = actionItems.iterator(); itemIt.hasNext();) {
            	ActionItem item = itemIt.next();
            	if (item.isWorkgroupItem()) {
            		for (String groupIdToCheck : allGroupsToCheck) {
            			if (item.getGroupId().equals(groupIdToCheck)) {
            				getActionListService().deleteActionItem(item);
            			}
            		}
            	}
            }
    	}

    }

    private MembersDiff getMembersDiff(List<String> oldMemberPrincipalIds, List<String> newMemberPrincipalIds) {
        Set<String> addedPrincipalIds = new HashSet<String>(ListUtils.subtract(newMemberPrincipalIds, oldMemberPrincipalIds));
        Set<String> removedPrincipalIds = new HashSet<String>(ListUtils.subtract(oldMemberPrincipalIds, newMemberPrincipalIds));
        return new MembersDiff(addedPrincipalIds, removedPrincipalIds);
    }

    private class MembersDiff {
        private final Set<String> addedPrincipalIds;

        private final Set<String> removedPrincipalIds;

        public MembersDiff(Set<String> addedPrincipalIds, Set<String>removedPrincipalIds) {
            this.addedPrincipalIds = addedPrincipalIds;
            this.removedPrincipalIds = removedPrincipalIds;
        }

        public Set<String> getAddedPrincipalIds() {
            return addedPrincipalIds;
        }

        public Set<String> getRemovedPrincipalIds() {
            return removedPrincipalIds;
        }
    }

}
