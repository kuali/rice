/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.impl.GroupAttributeDataImpl;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.service.GroupUpdateService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KIMWebServiceConstants;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(endpointInterface = KIMWebServiceConstants.GroupUpdateService.INTERFACE_CLASS, serviceName = KIMWebServiceConstants.GroupUpdateService.WEB_SERVICE_NAME, portName = KIMWebServiceConstants.GroupUpdateService.WEB_SERVICE_PORT, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
public class GroupUpdateServiceImpl extends GroupServiceBase implements GroupUpdateService {

	/**
     * @see org.kuali.rice.kim.service.GroupService#addGroupToGroup(java.lang.String, java.lang.String)
     */
    public boolean addGroupToGroup(String childId, String parentId) {
        if(childId.equals(parentId)) {
            throw new IllegalArgumentException("Can't add group to itself.");
        }

        if(isGroupMemberOfGroup(parentId, childId)) {
            throw new IllegalArgumentException("Circular group reference.");
        }

        GroupMemberImpl groupMember = new GroupMemberImpl();
        groupMember.setGroupId(parentId);
        groupMember.setMemberTypeCode( KimGroupMemberTypes.GROUP_MEMBER_TYPE );
        groupMember.setMemberId(childId);

        getBusinessObjectService().save(groupMember);
        getIdentityManagementNotificationService().groupUpdated();

        return true;
    }

    /**
     * @see org.kuali.rice.kim.service.GroupService#addPrincipalToGroup(java.lang.String, java.lang.String)
     */
    public boolean addPrincipalToGroup(String principalId, String groupId) {
        GroupMemberImpl groupMember = new GroupMemberImpl();
        groupMember.setGroupId(groupId);
        groupMember.setMemberTypeCode( KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE );
        groupMember.setMemberId(principalId);

        getBusinessObjectService().save(groupMember);
        KIMServiceLocator.getGroupInternalService().updateForUserAddedToGroup(groupMember.getMemberId(), groupMember.getGroupId());
        getIdentityManagementNotificationService().groupUpdated();
        return true;
    }

    public GroupInfo createGroup(GroupInfo groupInfo) {
        GroupImpl group = new GroupImpl();

        group = KimCommonUtils.copyInfoToGroup(groupInfo, group);

        saveGroup(group);

        GroupInfo newGroupInfo = getGroupInfoByName(groupInfo.getNamespaceCode(), groupInfo.getGroupName());

        if(groupInfo.getAttributes() != null && groupInfo.getAttributes().size() > 0) {
            List<GroupAttributeDataImpl> groupAttributes = 
            		copyInfoAttributesToGroupAttributes(groupInfo.getAttributes(), newGroupInfo.getGroupId(), newGroupInfo.getKimTypeId());
            saveGroupAttributes(groupAttributes);
        }
        return getGroupInfo(newGroupInfo.getGroupId());
    }

    /**
    *
    * @see org.kuali.rice.kim.service.GroupUpdateService#removeAllGroupMembers(java.lang.String)
    */
   public void removeAllGroupMembers(String groupId) {
   	// TODO tbradfor - Call updateForUserRemovedFromGroup for each
       Map<String,String> criteria = new HashMap<String,String>(1);
       criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, groupId);
       getBusinessObjectService().deleteMatching(GroupMemberImpl.class, criteria);
       getIdentityManagementNotificationService().groupUpdated();
   }

	/**
     * @see org.kuali.rice.kim.service.GroupService#removeGroupFromGroup(java.lang.String, java.lang.String)
     */
    public boolean removeGroupFromGroup(String childId, String parentId) {
        Map<String,String> criteria = new HashMap<String,String>(3);
        criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, parentId);
        criteria.put(KIMPropertyConstants.GroupMember.MEMBER_ID, childId);
        criteria.put(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, KimGroupMemberTypes.GROUP_MEMBER_TYPE);

        if(getBusinessObjectService().countMatching(GroupMemberImpl.class, criteria) == 1) {
            getBusinessObjectService().deleteMatching(GroupMemberImpl.class, criteria);
            getIdentityManagementNotificationService().groupUpdated();
            return true;
        }

        return false;
    }

	/**
     * @see org.kuali.rice.kim.service.GroupService#removePrincipalFromGroup(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public boolean removePrincipalFromGroup(String principalId, String groupId) {
        Map<String,String> criteria = new HashMap<String,String>(3);
        criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, groupId);
        criteria.put(KIMPropertyConstants.GroupMember.MEMBER_ID, principalId);
        criteria.put(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
        Collection<GroupMemberImpl> groupMemberList = getBusinessObjectService().findMatching(GroupMemberImpl.class, criteria);

        if(groupMemberList.size() == 1) {
        	GroupMemberImpl member = groupMemberList.iterator().next();
            getBusinessObjectService().delete(member);
            KIMServiceLocator.getGroupInternalService().updateForUserRemovedFromGroup(member.getMemberId(), member.getGroupId());
            getIdentityManagementNotificationService().groupUpdated();
            return true;
        }

        return false;
    }

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.GroupUpdateService#updateGroup(java.lang.String, org.kuali.rice.kim.bo.group.dto.GroupInfo)
	 */
	public GroupInfo updateGroup(String groupId, GroupInfo groupInfo) {
        // TODO sgibson - can this be used to change id?
        GroupImpl group = getGroupImpl(groupId);

        if (group == null) {
            throw new IllegalArgumentException("Group not found for update.");
        }

        group = KimCommonUtils.copyInfoToGroup(groupInfo, group);

        //delete old group attributes
        Map<String,String> criteria = new HashMap<String,String>();
        criteria.put(KIMPropertyConstants.Group.GROUP_ID, group.getGroupId());
        getBusinessObjectService().deleteMatching(GroupAttributeDataImpl.class, criteria);


        saveGroup(group);
        getIdentityManagementNotificationService().groupUpdated();

        //create new group attributes
        if(groupInfo.getAttributes() != null && groupInfo.getAttributes().size() > 0) {
            List<GroupAttributeDataImpl> groupAttributes = 
            		copyInfoAttributesToGroupAttributes(groupInfo.getAttributes(), group.getGroupId(), group.getKimTypeId());
            saveGroupAttributes(groupAttributes);
        }

        return getGroupInfo(groupInfo.getGroupId());
    }

	protected void saveGroup(GroupImpl group) {
		if ( group == null ) {
			return;
		}

		KIMServiceLocator.getGroupInternalService().saveWorkgroup(group);
	}

	protected void saveGroupAttributes(List<GroupAttributeDataImpl> groupAttributes) {
        if ( groupAttributes == null ) {
            return;
        }
        getBusinessObjectService().save( groupAttributes );
    }

	protected void deleteGroupAttribute(GroupAttributeDataImpl groupAttribute) {
        if ( groupAttribute == null ) {
            return;
        }
        getBusinessObjectService().delete( groupAttribute );
    }
}
