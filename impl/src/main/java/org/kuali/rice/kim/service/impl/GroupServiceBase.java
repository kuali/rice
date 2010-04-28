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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.impl.GroupAttributeDataImpl;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.service.IdentityManagementNotificationService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.ksb.service.KSBServiceLocator;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public abstract class GroupServiceBase {
	private BusinessObjectService businessObjectService;
	private LookupService lookupService;
	
	/**
     * @see org.kuali.rice.kim.service.GroupService#getGroupInfo(java.lang.String)
     */
    public GroupInfo getGroupInfo(String groupId) {
        GroupImpl group = getGroupImpl(groupId);
        return toGroupInfo(group);
    }
    
    
	/**
     * @see org.kuali.rice.kim.service.GroupService#isGroupMemberOfGroup(java.lang.String,
     *      java.lang.String)
     */
	public boolean isGroupMemberOfGroup(String groupMemberId, String groupId) {
        if( groupId == null || groupMemberId == null) {
            return false;
        }

        // TODO: should it check for valid group ids here?

        return isGroupMemberOfGroupInternal(groupMemberId, groupId);
	}
	
	/**
     * @see org.kuali.rice.kim.service.GroupService#getGroupInfoByName(java.lang.String, java.lang.String)
     */
    public GroupInfo getGroupInfoByName(String namespaceCode, String groupName) {
        return toGroupInfo(getGroupByName(namespaceCode, groupName));
    }
    
	 /**
     * @see org.kuali.rice.kim.service.GroupService#getGroupInfos(java.util.List)
     */
    public Map<String, GroupInfo> getGroupInfos(Collection<String> groupIds) {
        Map<String, GroupInfo> result = new HashMap<String, GroupInfo>();

        // hopefully there is an efficient orm way to do this
        for (String s : groupIds) {
            GroupImpl group = getGroupImpl(s);
            if (group != null) {
                result.put(s, toGroupInfo(group));
            }
        }

        return result;
    }
    
    protected GroupImpl getGroupImpl(String groupId) {
		if ( groupId == null ) {
			return null;
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put(KIMPropertyConstants.Group.GROUP_ID, groupId);
		return (GroupImpl) getBusinessObjectService().findByPrimaryKey(GroupImpl.class, criteria);
	}

	@SuppressWarnings("unchecked")
	protected GroupImpl getGroupByName(String namespaceCode, String groupName) {
		if ( namespaceCode == null || groupName == null ) {
			return null;
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode);
		criteria.put(KimConstants.UniqueKeyConstants.GROUP_NAME, groupName);
		Collection<GroupImpl> groups = getBusinessObjectService().findMatching(GroupImpl.class, criteria);
		if ( groups.size() > 0 ) {
			return groups.iterator().next();
		}
		return null;
	}
	
	protected GroupInfo toGroupInfo(GroupImpl kimGroup) {
        GroupInfo info = null;

        if (kimGroup != null) {
            info = new GroupInfo();

            info.setActive(kimGroup.isActive());
            info.setGroupDescription(kimGroup.getGroupDescription());
            info.setGroupId(kimGroup.getGroupId());
            info.setGroupName(kimGroup.getGroupName());
            info.setKimTypeId(kimGroup.getKimTypeId());
            info.setNamespaceCode(kimGroup.getNamespaceCode());

            info.setAttributes(kimGroup.getAttributes());
        }

        return info;
    }

    protected List<GroupInfo> toGroupInfo(List<GroupImpl> kimGroups){
    	List<GroupInfo> lRet = null;

    	if(kimGroups != null){
    		lRet = new ArrayList<GroupInfo>();

    		for(GroupImpl gi: kimGroups){
    			lRet.add(this.toGroupInfo(gi));
    		}
    	}

    	return lRet;
    }

    protected GroupImpl copyInfoToGroup(GroupInfo info, GroupImpl group) {
        group.setActive(info.isActive());
        group.setGroupDescription(info.getGroupDescription());
        group.setGroupId(info.getGroupId());
        group.setGroupName(info.getGroupName());
        group.setKimTypeId(info.getKimTypeId());
        group.setNamespaceCode(info.getNamespaceCode());

        return group;
    }

    protected List<GroupAttributeDataImpl> copyInfoAttributesToGroupAttributes(Map<String,String> infoMap, String groupId, String kimTypeId) {
        List<GroupAttributeDataImpl> attrList = new ArrayList<GroupAttributeDataImpl>(infoMap.size());

        // TODO: fix this to use the KimTypeInfoService to get the attribute information rather than selecting from the database

        for(String key : infoMap.keySet()) {
            Map<String,String> criteria = new HashMap<String,String>();
            criteria.put("attributeName", key);
            KimAttributeImpl kimAttr = (KimAttributeImpl) getBusinessObjectService().findByPrimaryKey(KimAttributeImpl.class, criteria);

            if(kimAttr == null) {
            	throw new IllegalArgumentException("KimAttribute not found: " + key);
            }

            GroupAttributeDataImpl groupAttr = new GroupAttributeDataImpl();
            groupAttr.setKimAttributeId(kimAttr.getKimAttributeId());
            groupAttr.setAttributeValue(infoMap.get(key));
            groupAttr.setGroupId(groupId);
            groupAttr.setKimTypeId(kimTypeId);

            attrList.add(groupAttr);
        }

        return attrList;
    }
    
	protected boolean isGroupMemberOfGroupInternal(String groupMemberId, String groupId) {

	    GroupImpl group = getGroupImpl(groupId);
	    if( !group.isActive() ) {
	        return false;
	    }

	    for( String memberGroupId : group.getMemberGroupIds()) {
	        if(memberGroupId.equals(groupMemberId)) {
	            return true;
	        }
	        else if(isGroupMemberOfGroup(groupMemberId, memberGroupId)) {
	            return true;
	        }
	    }

	    return false;
	}

	@SuppressWarnings("unchecked")
	protected Map<String,GroupInfo> getDirectParentGroups(String groupId) {
		if ( groupId == null ) {
			return Collections.emptyMap();
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put(KIMPropertyConstants.GroupMember.MEMBER_ID, groupId);
		criteria.put(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, KimGroupMemberTypes.GROUP_MEMBER_TYPE);

		List<GroupMemberImpl> groupMembers = (List<GroupMemberImpl>)getBusinessObjectService().findMatching(GroupMemberImpl.class, criteria);
		Set<String> matchingGroupIds = new HashSet<String>();
		// filter to active groups
		for ( GroupMemberImpl gm : groupMembers ) {
			if ( gm.isActive() ) {
				matchingGroupIds.add(gm.getGroupId());
			}
		}
		return getGroupInfos(matchingGroupIds);
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#getParentGroups(java.lang.String)
	 */
	protected List<GroupInfo> getParentGroups(String groupId) {
		if ( groupId == null ) {
			return Collections.emptyList();
		}
		Set<GroupInfo> groups = new HashSet<GroupInfo>();
		getParentGroupsInternal( groupId, groups );
		return new ArrayList<GroupInfo>( groups );
	}

	protected void getParentGroupsInternal( String groupId, Set<GroupInfo> groups ) {
		Map<String,GroupInfo> parentGroups = getDirectParentGroups( groupId );
		for ( GroupInfo group : parentGroups.values() ) {
			if ( !groups.contains( group ) ) {
				groups.add( group );
				getParentGroupsInternal( group.getGroupId(), groups );
			}
		}
	}
	protected IdentityManagementNotificationService getIdentityManagementNotificationService() {
        return (IdentityManagementNotificationService)KSBServiceLocator.getMessageHelper().getServiceAsynchronously(new QName("KIM", "kimIdentityManagementNotificationService"));
    }

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
	
    /**
	 * @return the lookupService
	 */
    protected LookupService getLookupService() {
		if(lookupService == null) {
			lookupService = KNSServiceLocator.getLookupService();
		}
		return lookupService;
	}
    
    
}
