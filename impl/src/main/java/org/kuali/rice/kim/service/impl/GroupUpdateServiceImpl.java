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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kim.bo.entity.impl.KimEntityAffiliationImpl;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.impl.GroupAttributeDataImpl;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.api.group.GroupService;

import org.kuali.rice.kim.service.GroupUpdateService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KIMWebServiceConstants;
import org.kuali.rice.kim.util.KimCommonUtilsInternal;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the default implementation for the {@link GroupUpdateService}, where the write methods for KIM groups are located.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(endpointInterface = KIMWebServiceConstants.GroupUpdateService.INTERFACE_CLASS, serviceName = KIMWebServiceConstants.GroupUpdateService.WEB_SERVICE_NAME, portName = KIMWebServiceConstants.GroupUpdateService.WEB_SERVICE_PORT, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
public class GroupUpdateServiceImpl extends GroupServiceBase implements GroupUpdateService {
	
	private static final Logger LOG = Logger.getLogger(GroupUpdateServiceImpl.class);
	
	private SequenceAccessorService sequenceAccessorService;

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

        groupMember = (GroupMemberImpl)getBusinessObjectService().save(groupMember);
        KIMServiceLocatorInternal.getGroupInternalService().updateForUserAddedToGroup(groupMember.getMemberId(), groupMember.getGroupId());
        getIdentityManagementNotificationService().groupUpdated();
        return true;
    }

    public GroupInfo createGroup(GroupInfo groupInfo) {
        GroupImpl group = new GroupImpl();

        group = KimCommonUtilsInternal.copyInfoToGroup(groupInfo, group);

        saveGroup(group);

        GroupInfo newGroupInfo = getGroupInfoByName(groupInfo.getNamespaceCode(), groupInfo.getGroupName());

        if(groupInfo.getAttributes() != null && groupInfo.getAttributes().size() > 0) {
            List<GroupAttributeDataImpl> groupAttributes =
            		KimCommonUtilsInternal.copyInfoAttributesToGroupAttributes(groupInfo.getAttributes(), newGroupInfo.getGroupId(), newGroupInfo.getKimTypeId());
            saveGroupAttributes(groupAttributes);
        }
        return getGroupInfo(newGroupInfo.getGroupId());
    }

    /**
    *
    * @see org.kuali.rice.kim.service.GroupUpdateService#removeAllGroupMembers(java.lang.String)
    */
   public void removeAllGroupMembers(String groupId) {
	   GroupService groupService = KimApiServiceLocator.getGroupService();
       List<String> memberPrincipalsBefore = groupService.getMemberPrincipalIds(groupId);

       Collection<GroupMemberImpl> toDeactivate = getActiveGroupMembers(groupId, null, null);
       java.sql.Timestamp today = new java.sql.Timestamp(System.currentTimeMillis());

       // Set principals as inactive
        for (GroupMemberImpl aToDeactivate : toDeactivate) {
            aToDeactivate.setActiveToDate(today);
        }

       // Save
       getBusinessObjectService().save(new ArrayList<GroupMemberImpl>(toDeactivate));
       List<String> memberPrincipalsAfter = groupService.getMemberPrincipalIds(groupId);

       if (!CollectionUtils.isEmpty(memberPrincipalsAfter)) {
    	   // should never happen!
    	   LOG.warn("after attempting removal of all members, group with id '" + groupId + "' still has principal members");
       }

       // do updates
       KIMServiceLocatorInternal.getGroupInternalService().updateForWorkgroupChange(groupId, memberPrincipalsBefore, memberPrincipalsAfter);
       getIdentityManagementNotificationService().groupUpdated();
   }

	/**
     * @see org.kuali.rice.kim.service.GroupService#removeGroupFromGroup(java.lang.String, java.lang.String)
     */
    public boolean removeGroupFromGroup(String childId, String parentId) {
    	java.sql.Timestamp today = new java.sql.Timestamp(System.currentTimeMillis());

    	List<GroupMemberImpl> groupMembers =
    		getActiveGroupMembers(parentId, childId, KimGroupMemberTypes.GROUP_MEMBER_TYPE);

        if(groupMembers.size() == 1) {
        	GroupMemberImpl groupMember = groupMembers.get(0);
        	groupMember.setActiveToDate(today);
            getBusinessObjectService().save(groupMember);
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
    	List<GroupMemberImpl> groupMembers =
    		getActiveGroupMembers(groupId, principalId, KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);

        if(groupMembers.size() == 1) {
        	GroupMemberImpl member = groupMembers.iterator().next();
        	member.setActiveToDate(new java.sql.Timestamp(System.currentTimeMillis()));
        	getBusinessObjectService().save(member);
            KIMServiceLocatorInternal.getGroupInternalService().updateForUserRemovedFromGroup(member.getMemberId(), member.getGroupId());
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

        group = KimCommonUtilsInternal.copyInfoToGroup(groupInfo, group);

        //delete old group attributes
        Map<String,String> criteria = new HashMap<String,String>();
        criteria.put(KIMPropertyConstants.Group.GROUP_ID, group.getGroupId());
        getBusinessObjectService().deleteMatching(GroupAttributeDataImpl.class, criteria);


        group = saveGroup(group);

        //create new group attributes
        if(groupInfo.getAttributes() != null && groupInfo.getAttributes().size() > 0) {
            List<GroupAttributeDataImpl> groupAttributes =
            		KimCommonUtilsInternal.copyInfoAttributesToGroupAttributes(groupInfo.getAttributes(), group.getGroupId(), group.getKimTypeId());
            saveGroupAttributes(groupAttributes);
        }

        return getGroupInfo(groupInfo.getGroupId());
    }

	protected GroupImpl saveGroup(GroupImpl group) {
		if ( group == null ) {
			return null;
		} else if (group.getGroupId() != null) {
			// Get the version of the group that is in the DB
			GroupImpl oldGroup = getGroupImpl(group.getGroupId());

			if (oldGroup != null) {
				// Inactivate and re-add members no longer in the group (in order to preserve history).
				java.sql.Timestamp activeTo = new java.sql.Timestamp(System.currentTimeMillis());
				List<GroupMemberImpl> toReAdd = null;

				if (oldGroup.getMembers() != null) for (GroupMemberImpl member : oldGroup.getMembers()) {
					// if the old member isn't in the new group
					if (group.getMembers() == null || !group.getMembers().contains(member)) {
						// inactivate the member
						member.setActiveToDate(activeTo);
						if (toReAdd == null) { toReAdd = new ArrayList<GroupMemberImpl>(); }
						// queue it up for re-adding
						toReAdd.add(member);
					}
				}

				// do the re-adding
				if (toReAdd != null) {
					List<GroupMemberImpl> groupMembers = group.getMembers();
					if (groupMembers == null) { groupMembers = new ArrayList<GroupMemberImpl>(toReAdd.size()); }
					group.setMembers(groupMembers);
				}
			}
		}

		// GroupInternalService handles KEW update duties
		
		SequenceAccessorService sas = getSequenceAccessorService();
    	if (group.getGroupId() == null) {
    		group.setGroupId(sas.getNextAvailableSequenceNumber(
    				"KRIM_GRP_ID_S", GroupImpl.class).toString());
    	}
		GroupImpl savedGroup = KIMServiceLocatorInternal.getGroupInternalService().saveWorkgroup(group);
		getIdentityManagementNotificationService().groupUpdated();
		return savedGroup;
	}

	protected void saveGroupAttributes(List<GroupAttributeDataImpl> groupAttributes) {
        if ( groupAttributes == null ) {
            return;
        }
        SequenceAccessorService sas = getSequenceAccessorService();
        for (GroupAttributeDataImpl groupAttribute : groupAttributes) {
        	if (groupAttribute.getId() == null) {
        		groupAttribute.setId(sas.getNextAvailableSequenceNumber(
                        "KRIM_GRP_ATTR_DATA_ID_S", KimEntityAffiliationImpl.class).toString());
        	}
        }
        getBusinessObjectService().save( groupAttributes );
    }

	protected void deleteGroupAttribute(GroupAttributeDataImpl groupAttribute) {
        if ( groupAttribute == null ) {
            return;
        }
        getBusinessObjectService().delete( groupAttribute );
    }

	/**
	 * This helper method gets the active group members of the specified type (see {@link KimGroupMemberTypes}).
	 * If the optional params are null, it will return all active members for the specified group regardless
	 * of type.
	 *
	 * @param parentId
	 * @param childId optional, but if provided then memberType must be too
	 * @param memberType optional, but must be provided if childId is
     * @return a list of group members
	 */
	private List<GroupMemberImpl> getActiveGroupMembers(String parentId,
			String childId, String memberType) {
    	final java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

    	if (childId != null && memberType == null) throw new RiceRuntimeException("memberType must be non-null if childId is non-null");

		Map<String,Object> criteria = new HashMap<String,Object>(4);
        criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, parentId);

        if (childId != null) {
        	criteria.put(KIMPropertyConstants.GroupMember.MEMBER_ID, childId);
        	criteria.put(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, memberType);
        }

        Collection<GroupMemberImpl> groupMembers = getBusinessObjectService().findMatching(GroupMemberImpl.class, criteria);

        CollectionUtils.filter(groupMembers, new Predicate() {
			public boolean evaluate(Object object) {
				GroupMemberImpl member = (GroupMemberImpl) object;
				// keep in the collection (return true) if the activeToDate is null, or if it is set to a future date
				return member.getActiveToDate() == null || today.before(member.getActiveToDate());
			}
		});

        return new ArrayList<GroupMemberImpl>(groupMembers);
	}

	protected SequenceAccessorService getSequenceAccessorService() {
		if ( sequenceAccessorService == null ) {
			sequenceAccessorService = KNSServiceLocator.getSequenceAccessorService();
		}
		return sequenceAccessorService;
	}
	
}
