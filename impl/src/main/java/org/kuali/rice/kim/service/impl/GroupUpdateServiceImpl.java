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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kim.api.common.attribute.KimAttributeData;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.group.GroupUpdateService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.entity.impl.KimEntityAffiliationImpl;
import org.kuali.rice.kim.impl.group.GroupAttributeBo;
import org.kuali.rice.kim.impl.group.GroupBo;
import org.kuali.rice.kim.impl.group.GroupMemberBo;
import org.kuali.rice.kim.impl.group.GroupServiceBase;
import org.kuali.rice.kim.service.IdentityManagementNotificationService;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KIMWebServiceConstants;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.ksb.api.bus.services.KsbApiServiceLocator;

import javax.jws.WebService;
import javax.xml.namespace.QName;
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
     * @see org.kuali.rice.kim.api.group.GroupUpdateService#addGroupToGroup(java.lang.String, java.lang.String)
     */
    public boolean addGroupToGroup(String childId, String parentId) {
        if(childId.equals(parentId)) {
            throw new IllegalArgumentException("Can't add group to itself.");
        }

        if(isGroupMemberOfGroup(parentId, childId)) {
            throw new IllegalArgumentException("Circular group reference.");
        }

        GroupMemberBo groupMember = new GroupMemberBo();
        groupMember.setGroupId(parentId);
        groupMember.setTypeCode(KimGroupMemberTypes.GROUP_MEMBER_TYPE);
        groupMember.setMemberId(childId);

        this.businessObjectService.save(groupMember);
        getIdentityManagementNotificationService().groupUpdated();

        return true;
    }

    /**
     * @see org.kuali.rice.kim.api.group.GroupUpdateService#addPrincipalToGroup(java.lang.String, java.lang.String)
     */
    public boolean addPrincipalToGroup(String principalId, String groupId) {
        GroupMemberBo groupMember = new GroupMemberBo();
        groupMember.setGroupId(groupId);
        groupMember.setTypeCode(KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
        groupMember.setMemberId(principalId);

        groupMember = (GroupMemberBo)this.businessObjectService.save(groupMember);
        KIMServiceLocatorInternal.getGroupInternalService().updateForUserAddedToGroup(groupMember.getMemberId(), groupMember.getGroupId());
        getIdentityManagementNotificationService().groupUpdated();
        return true;
    }

    public Group createGroup(Group group) {
        if (group == null) {
            throw new RiceIllegalArgumentException(("group is null"));
        }
        GroupBo groupBo = new GroupBo();

        groupBo = GroupBo.from(group);

        saveGroup(groupBo);

        Group newGroupInfo = getGroupByName(group.getNamespaceCode(), group.getName());

        if(group.getAttributes() != null && group.getAttributes().size() > 0) {

            List<GroupAttributeBo> attributeBos = new ArrayList<GroupAttributeBo>();
            for (KimAttributeData attr : group.getAttributes()) {
                attributeBos.add(GroupAttributeBo.from(attr));
            }
            saveGroupAttributes(attributeBos);
        }
        return getGroup(newGroupInfo.getId());
    }

    /**
    *
    * @see org.kuali.rice.kim.api.group.GroupUpdateService#removeAllMembers(java.lang.String)
    */
   public void removeAllMembers(String groupId) {
	   GroupService groupService = KimApiServiceLocator.getGroupService();
       List<String> memberPrincipalsBefore = groupService.getMemberPrincipalIds(groupId);

       Collection<GroupMemberBo> toDeactivate = getActiveGroupMembers(groupId, null, null);
       java.sql.Timestamp today = new java.sql.Timestamp(System.currentTimeMillis());

       // Set principals as inactive
        for (GroupMemberBo aToDeactivate : toDeactivate) {
            aToDeactivate.setActiveToDate(today);
        }

       // Save
       this.businessObjectService.save(new ArrayList<GroupMemberBo>(toDeactivate));
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
     * @see org.kuali.rice.kim.api.group.GroupUpdateService#removeGroupFromGroup(java.lang.String, java.lang.String)
     */
    public boolean removeGroupFromGroup(String childId, String parentId) {
    	java.sql.Timestamp today = new java.sql.Timestamp(System.currentTimeMillis());

    	List<GroupMemberBo> groupMembers =
    		getActiveGroupMembers(parentId, childId, KimGroupMemberTypes.GROUP_MEMBER_TYPE);

        if(groupMembers.size() == 1) {
        	GroupMemberBo groupMember = groupMembers.get(0);
        	groupMember.setActiveToDate(today);
            this.businessObjectService.save(groupMember);
            getIdentityManagementNotificationService().groupUpdated();
            return true;
        }

        return false;
    }

	/**
     * @see org.kuali.rice.kim.api.group.GroupUpdateService#removePrincipalFromGroup(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public boolean removePrincipalFromGroup(String principalId, String groupId) {
    	List<GroupMemberBo> groupMembers =
    		getActiveGroupMembers(groupId, principalId, KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);

        if(groupMembers.size() == 1) {
        	GroupMemberBo member = groupMembers.iterator().next();
        	member.setActiveToDate(new java.sql.Timestamp(System.currentTimeMillis()));
        	this.businessObjectService.save(member);
            KIMServiceLocatorInternal.getGroupInternalService().updateForUserRemovedFromGroup(member.getMemberId(), member.getGroupId());
            getIdentityManagementNotificationService().groupUpdated();
            return true;
        }

        return false;
    }

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.api.group.GroupUpdateService#updateGroup(java.lang.String, org.kuali.rice.kim.api.group.Group)
	 */
	public Group updateGroup(String groupId, Group group) {
        if (group == null) {
            throw new RiceIllegalArgumentException(("group is null"));
        }
        if (StringUtils.isEmpty(groupId)) {
            throw new RiceIllegalArgumentException(("groupId is empty"));
        }
        // Note:  this cannot be used to change id
        GroupBo groupBo = getGroupBo(groupId);

        if (groupBo == null) {
            throw new IllegalArgumentException("Group not found for update.");
        }

        groupBo.setActive(group.isActive());
        groupBo.setName(group.getName());
        groupBo.setNamespaceCode(group.getNamespaceCode());
        groupBo.setDescription(group.getDescription());
        groupBo.setKimTypeId(group.getKimTypeId());


        //delete old group attributes
        Map<String,String> criteria = new HashMap<String,String>();
        criteria.put(KIMPropertyConstants.Group.GROUP_ID, groupBo.getId());
        this.businessObjectService.deleteMatching(GroupAttributeBo.class, criteria);


        groupBo = saveGroup(groupBo);

        //create new group attributes
        if(group.getAttributes() != null && group.getAttributes().size() > 0) {
            List<GroupAttributeBo> attributeBos = new ArrayList<GroupAttributeBo>();
            for (KimAttributeData attr : group.getAttributes()) {
                attributeBos.add(GroupAttributeBo.from(attr));
            }
            saveGroupAttributes(attributeBos);
        }

        return getGroup(group.getId());
    }

	protected GroupBo saveGroup(GroupBo group) {
		if ( group == null ) {
			return null;
		} else if (group.getId() != null) {
			// Get the version of the group that is in the DB
			GroupBo oldGroup = getGroupBo(group.getId());

			if (oldGroup != null) {
				// Inactivate and re-add members no longer in the group (in order to preserve history).
				java.sql.Timestamp activeTo = new java.sql.Timestamp(System.currentTimeMillis());
				List<GroupMemberBo> toReAdd = null;

				if (oldGroup.getMembers() != null) {
                    for (GroupMemberBo member : oldGroup.getMembers()) {
                        // if the old member isn't in the new group
                        if (group.getMembers() == null || !group.getMembers().contains(member)) {
                            // inactivate the member
                            member.setActiveToDate(activeTo);
                            if (toReAdd == null) {
                                toReAdd = new ArrayList<GroupMemberBo>();
                            }
                            // queue it up for re-adding
                            toReAdd.add(member);
                        }
                    }
				}

				// do the re-adding
				if (toReAdd != null) {
					List<GroupMemberBo> groupMembers = group.getMembers();
					if (groupMembers == null) {
                        groupMembers = new ArrayList<GroupMemberBo>(toReAdd.size());
                    }
					group.setMembers(groupMembers);
				}
			}
		}

		// GroupInternalService handles KEW update duties
		
		SequenceAccessorService sas = getSequenceAccessorService();
    	if (group.getId() == null) {
    		group.setId(sas.getNextAvailableSequenceNumber(
                    "KRIM_GRP_ID_S", GroupBo.class).toString());
    	}
		GroupBo savedGroup = KIMServiceLocatorInternal.getGroupInternalService().saveWorkgroup(group);
		getIdentityManagementNotificationService().groupUpdated();
		return savedGroup;
	}

	protected void saveGroupAttributes(List<GroupAttributeBo> groupAttributes) {
        if ( groupAttributes == null ) {
            return;
        }
        SequenceAccessorService sas = getSequenceAccessorService();
        for (GroupAttributeBo groupAttribute : groupAttributes) {
        	if (groupAttribute.getId() == null) {
        		groupAttribute.setId(sas.getNextAvailableSequenceNumber(
                        "KRIM_GRP_ATTR_DATA_ID_S", KimEntityAffiliationImpl.class).toString());
        	}
        }
        this.businessObjectService.save( groupAttributes );
    }

	protected void deleteGroupAttribute(GroupAttributeBo groupAttribute) {
        if ( groupAttribute == null ) {
            return;
        }
        this.businessObjectService.delete( groupAttribute );
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
	private List<GroupMemberBo> getActiveGroupMembers(String parentId,
			String childId, String memberType) {
    	final java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

    	if (childId != null && memberType == null) throw new RiceRuntimeException("memberType must be non-null if childId is non-null");

		Map<String,Object> criteria = new HashMap<String,Object>(4);
        criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, parentId);

        if (childId != null) {
        	criteria.put(KIMPropertyConstants.GroupMember.MEMBER_ID, childId);
        	criteria.put(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, memberType);
        }

        Collection<GroupMemberBo> groupMembers = this.businessObjectService.findMatching(GroupMemberBo.class, criteria);

        CollectionUtils.filter(groupMembers, new Predicate() {
			public boolean evaluate(Object object) {
				GroupMemberBo member = (GroupMemberBo) object;
				// keep in the collection (return true) if the activeToDate is null, or if it is set to a future date
				return member.getActiveToDate() == null || today.before(member.getActiveToDate());
			}
		});

        return new ArrayList<GroupMemberBo>(groupMembers);
	}

	protected SequenceAccessorService getSequenceAccessorService() {
		if ( sequenceAccessorService == null ) {
			sequenceAccessorService = KNSServiceLocator.getSequenceAccessorService();
		}
		return sequenceAccessorService;
	}

    protected IdentityManagementNotificationService getIdentityManagementNotificationService() {
        return (IdentityManagementNotificationService) KsbApiServiceLocator.getMessageHelper().getServiceAsynchronously(new QName("KIM", "kimIdentityManagementNotificationService"));
    }
	
}
