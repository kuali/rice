package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.dto.GroupMembershipInfo;
import org.kuali.rice.kim.bo.group.impl.GroupAttributeDataImpl;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.GroupUpdateService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSPropertyConstants;

public class GroupServiceImpl implements GroupService, GroupUpdateService {

	private BusinessObjectService businessObjectService;

	/**
     * @see org.kuali.rice.kim.service.GroupService#getGroupInfo(java.lang.String)
     */
    public GroupInfo getGroupInfo(String groupId) {
        GroupImpl group = getGroupImpl(groupId);
        return toGroupInfo(group);
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


	/**

     * @see org.kuali.rice.kim.service.GroupService#getGroupIdsForPrincipal(java.lang.String)
     */
    public List<String> getGroupIdsForPrincipal(String principalId) {
        return getGroupIdsForPrincipalByNamespace(principalId, null);
    }

    /**
     * @see org.kuali.rice.kim.service.GroupService#getGroupIdsForPrincipalByNamespace(java.lang.String, java.lang.String)
     */
    public List<String> getGroupIdsForPrincipalByNamespace(String principalId, String namespaceCode) {
        List<String> result = new ArrayList<String>();
        
        if (principalId != null) {
            List<GroupInfo> groupList = getGroupsForPrincipalByNamespace(principalId, namespaceCode);

            for (GroupInfo group : groupList) {
                result.add(group.getGroupId());
            }
        }

        return result;
    }

    /**
     * @see org.kuali.rice.kim.service.GroupService#getDirectGroupIdsForPrincipal(java.lang.String)
     */
    public List<String> getDirectGroupIdsForPrincipal(String principalId) {
        List<String> result = new ArrayList<String>();

        if (principalId != null) {
        	Collection<GroupInfo> groupList = getDirectGroupsForPrincipal(principalId);

            for (GroupInfo g : groupList) {
                result.add(g.getGroupId());
            }
        }

        return result;
    }

    /**
	 * @see org.kuali.rice.kim.service.GroupService#getGroupsForPrincipal(java.lang.String)
	 */
	public List<GroupInfo> getGroupsForPrincipal(String principalId) {
		return getGroupsForPrincipalByNamespace( principalId, null );
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#getGroupsForPrincipalByNamespace(java.lang.String, java.lang.String)
	 */
	public List<GroupInfo> getGroupsForPrincipalByNamespace(String principalId, String namespaceCode) {
		Collection<GroupInfo> directGroups = getDirectGroupsForPrincipal( principalId, namespaceCode );
		Set<GroupInfo> groups = new HashSet<GroupInfo>();
		for ( GroupInfo group : directGroups ) {
			groups.add( group );
			groups.addAll( getParentGroups( group.getGroupId() ) );
		}
		return new ArrayList<GroupInfo>( groups );
	}

	protected Collection<GroupInfo> getDirectGroupsForPrincipal( String principalId ) {
		return getDirectGroupsForPrincipal( principalId, null );
	}

	@SuppressWarnings("unchecked")
	protected Collection<GroupInfo> getDirectGroupsForPrincipal( String principalId, String namespaceCode ) {
		if ( principalId == null ) {
			return Collections.emptyList();
		}
		Map<String,Object> criteria = new HashMap<String,Object>();
		criteria.put(KIMPropertyConstants.GroupMember.MEMBER_ID, principalId);
		criteria.put(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
		Collection<GroupMemberImpl> groupMembers = getBusinessObjectService().findMatching(GroupMemberImpl.class, criteria);
		Set<String> groupIds = new HashSet<String>( groupMembers.size() );
		// only return the active members
		for ( GroupMemberImpl gm : groupMembers ) {
			if ( gm.isActive() ) {
				groupIds.add( gm.getGroupId() );
			}
		}
		// pull all the group information for the matching members
		Map<String,GroupInfo> groups = getGroupInfos(groupIds);
		List<GroupInfo> result = new ArrayList<GroupInfo>( groups.size() );
		// filter by namespace if necessary
		for ( GroupInfo gi : groups.values() ) {
			if ( gi.isActive() ) {
				if ( StringUtils.isBlank( namespaceCode ) || StringUtils.equals(namespaceCode, gi.getNamespaceCode() ) ) {
					result.add(gi);
				}
			}
		}
		return result;
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#getMemberGroups(java.lang.String)
	 */
	protected List<GroupImpl> getMemberGroups(String groupId) {
		if ( groupId == null ) {
			return Collections.emptyList();
		}
		Set<GroupImpl> groups = new HashSet<GroupImpl>();

		GroupImpl group = getGroupImpl(groupId);
		getMemberGroupsInternal(group, groups);

		return new ArrayList<GroupImpl>(groups);
	}

	protected void getMemberGroupsInternal( GroupImpl group, Set<GroupImpl> groups ) {
		if ( group == null ) {
			return;
		}
		List<String> memberGroupIds = group.getMemberGroupIds();

		for (String groupId : memberGroupIds) {
			GroupImpl memberGroup = getGroupImpl(groupId);
			// if we've already seen that group, don't recurse into it
			if ( memberGroup.isActive() && !groups.contains( memberGroup ) ) {
				groups.add(memberGroup);
				getMemberGroupsInternal(memberGroup,groups);
			}
		}

	}

	/**
     * @see org.kuali.rice.kim.service.GroupService#lookupGroupIds(java.util.Map)
     */
    public List<String> lookupGroupIds(Map<String, String> searchCriteria) {
        List<GroupImpl> groupList = lookupGroups(searchCriteria);
        List<String> result = new ArrayList<String>();

        for (GroupImpl group : groupList) {
            result.add(group.getGroupId());
        }

        return result;
    }

	@SuppressWarnings("unchecked")
	protected List<GroupImpl> lookupGroups(Map<String, String> searchCriteria) {
		return (List<GroupImpl>) getBusinessObjectService().findMatching(GroupImpl.class, searchCriteria);
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#getDirectMemberPrincipalIds(java.lang.String)
	 */
	public List<String> getDirectMemberPrincipalIds(String groupId) {
		if ( groupId == null ) {
			return Collections.emptyList();
		}
		GroupImpl group = getGroupImpl(groupId);
		if ( group == null ) {
			return Collections.emptyList();
		}

		return group.getMemberPrincipalIds();
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#getMemberPrincipalIds(java.lang.String)
	 */
	public List<String> getMemberPrincipalIds(String groupId) {
		if ( groupId == null ) {
			return Collections.emptyList();
		}
		Set<String> ids = new HashSet<String>();

		GroupImpl group = getGroupImpl(groupId);
		if ( group == null ) {
			return Collections.emptyList();
		}

		ids.addAll( group.getMemberPrincipalIds() );

		for (String memberGroupId : group.getMemberGroupIds()) {
			ids.addAll(getMemberPrincipalIds(memberGroupId));
		}

		return new ArrayList<String>(ids);
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#isMemberOfGroup(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean isMemberOfGroup(String principalId, String groupId) {
		if ( principalId == null || groupId == null ) {
			return false;
		}
		// we could call the getMemberPrincipalIds method, but this will be more efficient
		// when group traversal is not needed
		GroupImpl group = getGroupImpl(groupId);
		if ( group == null || !group.isActive() ) {
			return false;
		}
		// check the immediate group
		for (String groupPrincipalId : group.getMemberPrincipalIds() ) {
			if (groupPrincipalId.equals(principalId)) {
				return true;
			}
		}

		// check each contained group, returning as soon as a match is found
		for ( String memberGroupId : group.getMemberGroupIds() ) {
			if ( isMemberOfGroup( principalId, memberGroupId ) ) {
				return true;
			}
		}

		// no match found, return false
		return false;
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


	/**
     * @see org.kuali.rice.kim.service.GroupService#getDirectParentGroupIds(java.lang.String)
     */
    public List<String> getDirectParentGroupIds(String groupId) {
        // TODO - This could be optimized to get ids in one statement

        List<String> result = new ArrayList<String>();
        if (groupId != null) {
            Map<String,GroupInfo> groupList = getDirectParentGroups(groupId);
            result.addAll(groupList.keySet());
        }

        return result;
    }

    /**
     * @see org.kuali.rice.kim.service.GroupService#getParentGroupIds(java.lang.String)
     */
	public List<String> getParentGroupIds(String groupId) {
        List<String> result = new ArrayList<String>();
        if (groupId != null) {
            List<GroupInfo> groupList = getParentGroups(groupId);

            for (GroupInfo group : groupList) {
                result.add(group.getGroupId());
            }
        }

        return result;
	}


	/**
	 * @see org.kuali.rice.kim.service.GroupService#getDirectMemberGroupIds(java.lang.String)
	 */
	public List<String> getDirectMemberGroupIds(String groupId) {
		if ( groupId == null ) {
			return Collections.emptyList();
		}
		GroupImpl group = getGroupImpl( groupId );
		if ( group == null ) {
			return Collections.emptyList();
		}
		return group.getMemberGroupIds();
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#isGroupActive(java.lang.String)
	 */
	public boolean isGroupActive( String groupId ) {
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put(KIMPropertyConstants.Group.GROUP_ID, groupId);
		criteria.put(KNSPropertyConstants.ACTIVE, "Y");
		return getBusinessObjectService().countMatching(GroupImpl.class, criteria) > 0;
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#getMemberGroupIds(java.lang.String)
	 */
	public List<String> getMemberGroupIds(String groupId) {
		if ( groupId == null ) {
			return Collections.emptyList();
		}
		List<GroupImpl> groups = getMemberGroups( groupId );
		ArrayList<String> groupIds = new ArrayList<String>( groups.size() );
		for ( GroupImpl group : groups ) {
			if ( group.isActive() ) {
				groupIds.add( group.getGroupId() );
			}
		}
		return groupIds;
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#isDirectMemberOfGroup(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean isDirectMemberOfGroup(String principalId, String groupId) {
		if ( principalId == null || groupId == null ) {
			return false;
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put(KIMPropertyConstants.GroupMember.MEMBER_ID, principalId);
		criteria.put(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
		criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, groupId);

		Collection<GroupMemberImpl> groupMembers = getBusinessObjectService().findMatching(GroupMemberImpl.class, criteria);
		for ( GroupMemberImpl gm : groupMembers ) {
			if ( gm.isActive() ) {
				return true;
			}
		}
		return false;
	}

	public BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#getGroupAttributes(java.lang.String)
	 */
    @SuppressWarnings("unchecked")
	public AttributeSet getGroupAttributes(String groupId) {
		if ( groupId == null ) {
			return new AttributeSet(0);
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put(KIMPropertyConstants.Group.GROUP_ID, groupId);
		List<GroupAttributeDataImpl> groupAttributes = (List<GroupAttributeDataImpl>)getBusinessObjectService().findMatching(GroupAttributeDataImpl.class, criteria);
		AttributeSet attributes = new AttributeSet( groupAttributes.size() );
		for ( GroupAttributeDataImpl attr : groupAttributes ) {
			attributes.put(attr.getKimAttribute().getAttributeName(), attr.getAttributeValue());
		}
		return attributes;
	}

    public GroupInfo createGroup(GroupInfo groupInfo) {
        GroupImpl group = new GroupImpl();

        copyInfoToGroup(groupInfo, group);

        saveGroup(group);

        GroupInfo newGroupInfo = getGroupInfoByName(groupInfo.getNamespaceCode(), groupInfo.getGroupName());

        if(groupInfo.getAttributes() != null && groupInfo.getAttributes().size() > 0) {
            List<GroupAttributeDataImpl> groupAttributes = copyInfoAttributesToGroupAttributes(groupInfo.getAttributes(), newGroupInfo.getGroupId(), newGroupInfo.getKimTypeId());
            saveGroupAttributes(groupAttributes);
        }
        return getGroupInfo(newGroupInfo.getGroupId());
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

    public GroupInfo updateGroup(String groupId, GroupInfo groupInfo) {
        // TODO sgibson - can this be used to change id?
        GroupImpl group = getGroupImpl(groupId);

        if (group == null) {
            throw new IllegalArgumentException("Group not found for update.");
        }

        copyInfoToGroup(groupInfo, group);

        //delete old group attributes
        Map<String,String> criteria = new HashMap<String,String>();
        criteria.put(KIMPropertyConstants.Group.GROUP_ID, group.getGroupId());
        getBusinessObjectService().deleteMatching(GroupAttributeDataImpl.class, criteria);

        saveGroup(group);

        //create new group attributes
        if(groupInfo.getAttributes() != null && groupInfo.getAttributes().size() > 0) {
            List<GroupAttributeDataImpl> groupAttributes = copyInfoAttributesToGroupAttributes(groupInfo.getAttributes(), group.getGroupId(), group.getKimTypeId());
            saveGroupAttributes(groupAttributes);
        }

        return getGroupInfo(groupInfo.getGroupId());
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

        return true;
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

            return true;
        }

        return false;
    }

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

        return true;
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

            return true;
        }

        return false;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.service.GroupUpdateService#removeAllGroupMembers(java.lang.String)
     */
    public void removeAllGroupMembers(String groupId) {
    	// TODO tbradfor - Call updateForUserRemovedFromGroup for each
        Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, groupId);
        getBusinessObjectService().deleteMatching(GroupMemberImpl.class, criteria);
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

    public Collection<GroupMembershipInfo> getGroupMembers( List<String> groupIds ) {
    	List<GroupMembershipInfo> groupMembers = new ArrayList<GroupMembershipInfo>();
    	for (String groupId : groupIds) {
    		for (GroupMemberImpl groupMember : getGroupMembers(groupId)) {
    			if (groupMember != null && groupMember.isActive()) {
    				groupMembers.add(toGroupMemberInfo(groupMember));
    			}
    		}
    	}
    	return groupMembers;
    }


	@SuppressWarnings("unchecked")
	protected List<GroupMemberImpl> getGroupMembers( String groupId) {
		if ( groupId == null ) {
			return Collections.emptyList();
		}
		Map<String,String> criteria = new HashMap<String,String>( 1 );
		criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, groupId);
		return (List<GroupMemberImpl>)getBusinessObjectService().findMatching(GroupMemberImpl.class, criteria);
	}

    protected GroupMembershipInfo toGroupMemberInfo(GroupMemberImpl kimGroupMember) {
    	GroupMembershipInfo groupMemberinfo = null;

        if (kimGroupMember != null) {
        	groupMemberinfo = new GroupMembershipInfo(kimGroupMember.getGroupId(), kimGroupMember.getGroupMemberId(),kimGroupMember.getMemberId(),kimGroupMember.getMemberTypeCode(), kimGroupMember.getActiveFromDate(), kimGroupMember.getActiveToDate());
        	groupMemberinfo.setVersionNumber(kimGroupMember.getVersionNumber());
        }

        return groupMemberinfo;
    }


}
