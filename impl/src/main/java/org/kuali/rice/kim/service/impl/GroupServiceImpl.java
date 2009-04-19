package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collection;
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
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.GroupUpdateService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;

public class GroupServiceImpl implements GroupService, GroupUpdateService {

	protected BusinessObjectService businessObjectService;

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
    public Map<String, GroupInfo> getGroupInfos(List<String> groupIds) {
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
		criteria.put("groupId", groupId);
		return (GroupImpl) getBusinessObjectService().findByPrimaryKey(GroupImpl.class, criteria);
	}

	@SuppressWarnings("unchecked")
	protected GroupImpl getGroupByName(String namespaceCode, String groupName) {
		if ( namespaceCode == null || groupName == null ) {
			return null;
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put("namespaceCode", namespaceCode);
		criteria.put("groupName", groupName);
		//criteria.put("active", "Y");
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
     // TODO - THIS can be optimized

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
            List<GroupImpl> groupList = getDirectGroupsForPrincipal(principalId);

            for (GroupImpl g : groupList) {
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
	@SuppressWarnings("unchecked")
	public List<GroupInfo> getGroupsForPrincipalByNamespace(String principalId, String namespaceCode) {
		List<GroupImpl> directGroups = getDirectGroupsForPrincipal( principalId, namespaceCode );
		Set<GroupInfo> groups = new HashSet<GroupInfo>();
		for ( GroupImpl group : directGroups ) {
			groups.add( toGroupInfo( group ) );
			for ( GroupImpl parentGroup : getParentGroups( group.getGroupId() ) ) {
				groups.add( toGroupInfo( parentGroup ) );
			}
		}
		return new ArrayList<GroupInfo>( groups );
	}

	protected List<GroupImpl> getDirectGroupsForPrincipal( String principalId ) {
		return getDirectGroupsForPrincipal( principalId, null );
	}

	@SuppressWarnings("unchecked")
	protected List<GroupImpl> getDirectGroupsForPrincipal( String principalId, String namespaceCode ) {
		if ( principalId == null ) {
			return new ArrayList<GroupImpl>(0);
		}
		Map<String,String> criteria = new HashMap<String,String>( 3 );
		criteria.put("members.memberId", principalId);
		criteria.put("members.memberTypeCode", KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
		//criteria.put("active", "Y");
		if ( StringUtils.isNotEmpty( namespaceCode ) ) {
			criteria.put("namespaceCode", namespaceCode);
		}
		return (List<GroupImpl>)getBusinessObjectService().findMatching(GroupImpl.class, criteria);
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#getMemberGroups(java.lang.String)
	 */
	protected List<GroupImpl> getMemberGroups(String groupId) {
		if ( groupId == null ) {
			return new ArrayList<GroupImpl>(0);
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
			// if we've already seen that role, don't recurse into it
			if ( !groups.contains( memberGroup ) ) {
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
			return new ArrayList<String>(0);
		}
		GroupImpl group = getGroupImpl(groupId);
		if ( group == null ) {
			return new ArrayList<String>(0);
		}

		return group.getMemberPrincipalIds();
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#getMemberPrincipalIds(java.lang.String)
	 */
	public List<String> getMemberPrincipalIds(String groupId) {
		if ( groupId == null ) {
			return new ArrayList<String>(0);
		}
		Set<String> ids = new HashSet<String>();

		GroupImpl group = getGroupImpl(groupId);
		if ( group == null ) {
			return new ArrayList<String>(0);
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
	protected List<GroupImpl> getDirectParentGroups(String groupId) {
		if ( groupId == null ) {
			return new ArrayList<GroupImpl>(0);
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put("members.memberId", groupId);
		criteria.put("members.memberTypeCode", KimGroupMemberTypes.GROUP_MEMBER_TYPE);
		criteria.put("active", "Y");
		return (List<GroupImpl>)getBusinessObjectService().findMatching(GroupImpl.class, criteria);
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#getParentGroups(java.lang.String)
	 */
	protected List<GroupImpl> getParentGroups(String groupId) {
		if ( groupId == null ) {
			return new ArrayList<GroupImpl>(0);
		}
		Set<GroupImpl> groups = new HashSet<GroupImpl>();
		getParentGroupsInternal( groupId, groups );
		return new ArrayList<GroupImpl>( groups );
	}

	protected void getParentGroupsInternal( String groupId, Set<GroupImpl> groups ) {
		List<GroupImpl> parentGroups = getDirectParentGroups( groupId );
		for ( GroupImpl group : parentGroups ) {
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
            List<GroupImpl> groupList = getDirectParentGroups(groupId);

            for (GroupImpl group : groupList) {
                result.add(group.getGroupId());
            }
        }

        return result;
    }

    /**
     * @see org.kuali.rice.kim.service.GroupService#getParentGroupIds(java.lang.String)
     */
	public List<String> getParentGroupIds(String groupId) {
        List<String> result = new ArrayList<String>();
        if (groupId != null) {
            List<GroupImpl> groupList = getParentGroups(groupId);

            for (GroupImpl group : groupList) {
                result.add(group.getGroupId());
            }
        }

        return result;
	}


	/**
	 * @see org.kuali.rice.kim.service.GroupService#getDirectMemberGroupIds(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<String> getDirectMemberGroupIds(String groupId) {
		if ( groupId == null ) {
			return new ArrayList<String>(0);
		}
		GroupImpl group = getGroupImpl( groupId );
		return group.getMemberGroupIds();
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#isGroupActive(java.lang.String)
	 */
	public boolean isGroupActive( String groupId ) {
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put("groupId", groupId);
		criteria.put("active", "Y");
		return getBusinessObjectService().countMatching(GroupImpl.class, criteria) > 0;
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#getMemberGroupIds(java.lang.String)
	 */
	public List<String> getMemberGroupIds(String groupId) {
		if ( groupId == null ) {
			return new ArrayList<String>(0);
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
	public boolean isDirectMemberOfGroup(String principalId, String groupId) {
		if ( principalId == null || groupId == null ) {
			return false;
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put("members.memberId", principalId);
		criteria.put("members.memberTypeCode", KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
		criteria.put("groupId", groupId);
		criteria.put("active", "Y");
		return getBusinessObjectService().countMatching(GroupImpl.class, criteria) != 0;
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
	public Map<String, String> getGroupAttributes(String groupId) {
		if ( groupId == null ) {
			return new HashMap<String, String>(0);
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put("groupId", groupId);
		List<GroupAttributeDataImpl> groupAttributes = (List<GroupAttributeDataImpl>)getBusinessObjectService().findMatching(GroupAttributeDataImpl.class, criteria);
		Map<String, String> attributes = new HashMap<String, String>( groupAttributes.size() );
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
        criteria.put("groupId", group.getGroupId());
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

        this.getBusinessObjectService().save(groupMember);
        KIMServiceLocator.getGroupInternalService().updateForUserAddedToGroup(groupMember.getMemberId(), groupMember.getGroupId());

        return true;
    }

    /**
     * @see org.kuali.rice.kim.service.GroupService#removePrincipalFromGroup(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public boolean removePrincipalFromGroup(String principalId, String groupId) {
        Map<String,String> criteria = new HashMap<String,String>();
        criteria.put("groupId", groupId);
        criteria.put("memberId", principalId);
        criteria.put("memberTypeCode", KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
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
    @SuppressWarnings("unchecked")
    public boolean removeGroupFromGroup(String childId, String parentId) {
        Map<String,String> criteria = new HashMap<String,String>(3);
        criteria.put("groupId", parentId);
        criteria.put("memberId", childId);
        criteria.put("memberTypeCode", KimGroupMemberTypes.GROUP_MEMBER_TYPE);
        Collection<GroupMemberImpl> groupGroupList = getBusinessObjectService().findMatching(GroupMemberImpl.class, criteria);

        if(groupGroupList.size() == 1) {
            getBusinessObjectService().delete(groupGroupList.iterator().next());

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
    	// TODO jonathan - THIS METHOD NEEDS JAVADOCS
    	
    	// TODO tbradfor - Call updateForUserRemovedFromGroup for each
        Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put("groupId", groupId);
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

    @SuppressWarnings("unchecked")
    protected List<GroupAttributeDataImpl> copyInfoAttributesToGroupAttributes(Map<String,String> infoMap, String groupId, String kimTypeId) {
        List<GroupAttributeDataImpl> attrList = new ArrayList<GroupAttributeDataImpl>(infoMap.size());

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
    			if (groupMember != null) {
    				groupMembers.add(toGroupMemberInfo(groupMember));
    			}
    		}
    	}
    	return groupMembers;
    }


	protected List<GroupMemberImpl> getGroupMembers( String groupId) {
		if ( groupId == null ) {
			return new ArrayList<GroupMemberImpl>(0);
		}
		Map<String,String> criteria = new HashMap<String,String>( 3 );
		criteria.put("groupId", groupId);
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
