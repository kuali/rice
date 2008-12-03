package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.group.GroupGroup;
import org.kuali.rice.kim.bo.group.GroupPrincipal;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.impl.GroupAttributeDataImpl;
import org.kuali.rice.kim.bo.group.impl.GroupGroupImpl;
import org.kuali.rice.kim.bo.group.impl.GroupPrincipalImpl;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;

public class GroupServiceImpl implements GroupService {

	protected BusinessObjectService businessObjectService;
	
	/** 
     * @see org.kuali.rice.kim.service.GroupService#getGroupInfo(java.lang.String)
     */
    public GroupInfo getGroupInfo(String groupId) {
        KimGroupImpl group = getGroupImpl(groupId);
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
            KimGroupImpl group = getGroupImpl(s);
            if (group != null) {
                result.put(s, toGroupInfo(group));
            }
        }

        return result;
    }

	protected KimGroupImpl getGroupImpl(String groupId) {
		if ( groupId == null ) {
			return null;
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put("groupId", groupId);
		return (KimGroupImpl) getBusinessObjectService().findByPrimaryKey(KimGroupImpl.class, criteria);
	}

	@SuppressWarnings("unchecked")
	protected KimGroupImpl getGroupByName(String namespaceCode, String groupName) {
		if ( namespaceCode == null || groupName == null ) {
			return null;
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put("namespaceCode", namespaceCode);
		criteria.put("groupName", groupName);
		//criteria.put("active", "Y");
		Collection<KimGroupImpl> groups = getBusinessObjectService().findMatching(KimGroupImpl.class, criteria);
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
            List<KimGroupImpl> groupList = getDirectGroupsForPrincipal(principalId);

            for (KimGroupImpl g : groupList) {
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
		List<KimGroupImpl> directGroups = getDirectGroupsForPrincipal( principalId, namespaceCode );
		Set<GroupInfo> groups = new HashSet<GroupInfo>();
		for ( KimGroupImpl group : directGroups ) {
			groups.add( toGroupInfo( group ) );
			for ( KimGroupImpl parentGroup : getParentGroups( group.getGroupId() ) ) {
				groups.add( toGroupInfo( parentGroup ) );
			}
		}
		return new ArrayList<GroupInfo>( groups );
	}

	protected List<KimGroupImpl> getDirectGroupsForPrincipal( String principalId ) {
		return getDirectGroupsForPrincipal( principalId, null );
	}

	@SuppressWarnings("unchecked")
	protected List<KimGroupImpl> getDirectGroupsForPrincipal( String principalId, String namespaceCode ) {
		if ( principalId == null ) {
			return new ArrayList<KimGroupImpl>(0);
		}
		Map<String,String> criteria = new HashMap<String,String>( 3 );
		criteria.put("memberPrincipals.memberPrincipalId", principalId);
		//criteria.put("active", "Y");
		if ( StringUtils.isNotEmpty( namespaceCode ) ) {
			criteria.put("namespaceCode", namespaceCode);
		}
		return (List<KimGroupImpl>)getBusinessObjectService().findMatching(KimGroupImpl.class, criteria);
	}
		
	/**
	 * @see org.kuali.rice.kim.service.GroupService#getMemberGroups(java.lang.String)
	 */
	protected List<KimGroupImpl> getMemberGroups(String groupId) {
		if ( groupId == null ) {
			return new ArrayList<KimGroupImpl>(0);
		}
		Set<KimGroupImpl> groups = new HashSet<KimGroupImpl>();

		KimGroupImpl group = getGroupImpl(groupId);
		getMemberGroupsInternal(group, groups);

		return new ArrayList<KimGroupImpl>(groups);
	}
	
	protected void getMemberGroupsInternal( KimGroupImpl group, Set<KimGroupImpl> groups ) {
		if ( group == null ) {
			return;
		}
		List<GroupGroupImpl> memberGroups = group.getMemberGroups();

		for (GroupGroupImpl groupGroup : memberGroups) {
			if ( !groupGroup.isActive() ) {
				continue;
			}
			KimGroupImpl memberGroup = getGroupImpl(groupGroup.getMemberGroupId());
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
        List<KimGroupImpl> groupList = lookupGroups(searchCriteria);
        List<String> result = new ArrayList<String>();

        for (KimGroupImpl group : groupList) {
            result.add(group.getGroupId());
        }

        return result;
    }

	@SuppressWarnings("unchecked")
	protected List<KimGroupImpl> lookupGroups(Map<String, String> searchCriteria) {
		return (List<KimGroupImpl>) getBusinessObjectService().findMatching(KimGroupImpl.class, searchCriteria);
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#getDirectMemberPrincipalIds(java.lang.String)
	 */
	public List<String> getDirectMemberPrincipalIds(String groupId) {
		if ( groupId == null ) {
			return new ArrayList<String>(0);
		}
		KimGroupImpl group = getGroupImpl(groupId);
		if ( group == null ) {
			return new ArrayList<String>(0);
		}
		List<GroupPrincipalImpl> groupPrincipals = group.getMemberPrincipals();

		List<String> ids = new ArrayList<String>();
		for (GroupPrincipalImpl groupPrincipal : groupPrincipals) {
			ids.add(groupPrincipal.getMemberPrincipalId());
		}
		
		return ids;
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#getMemberPrincipalIds(java.lang.String)
	 */
	public List<String> getMemberPrincipalIds(String groupId) {
		if ( groupId == null ) {
			return new ArrayList<String>(0);
		}
		Set<String> ids = new HashSet<String>();

		KimGroupImpl group = getGroupImpl(groupId);
		if ( group == null ) {
			return new ArrayList<String>(0);
		}

		for (GroupPrincipal groupPrincipal : group.getMemberPrincipals()) {
			ids.add(groupPrincipal.getMemberPrincipalId());
		}
		
		for (GroupGroup groupGroup : group.getMemberGroups()) {
			ids.addAll(getMemberPrincipalIds(groupGroup.getMemberGroupId()));
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
		KimGroupImpl group = getGroupImpl(groupId);
		if ( group == null || !group.isActive() ) {
			return false;
		}
		// check the immediate group
		for (GroupPrincipal groupPrincipal : group.getMemberPrincipals() ) {
			if (groupPrincipal.getMemberPrincipalId().equals(principalId)) {
				return true;
			}
		}
		
		// check each contained group, returning as soon as a match is found
		for ( GroupGroup memberGroup : group.getMemberGroups() ) {
			if ( isMemberOfGroup( principalId, memberGroup.getMemberGroupId() ) ) {
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
	    
	    KimGroupImpl group = getGroupImpl(groupId);
	    if( !group.isActive() ) {
	        return false;
	    }
	    
	    for( GroupGroup memberGroup : group.getMemberGroups()) {
	        if(memberGroup.getMemberGroupId().equals(groupMemberId)) {
	            return true;
	        }
	        else if(isGroupMemberOfGroup(groupMemberId, memberGroup.getMemberGroupId())) {
	            return true;
	        }
	    }
	    
	    return false;
	}

	@SuppressWarnings("unchecked")
	protected List<KimGroupImpl> getDirectParentGroups(String groupId) {
		if ( groupId == null ) {
			return new ArrayList<KimGroupImpl>(0);
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put("memberGroups.memberGroupId", groupId);
		criteria.put("active", "Y");
		return (List<KimGroupImpl>)getBusinessObjectService().findMatching(KimGroupImpl.class, criteria);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.GroupService#getParentGroups(java.lang.String)
	 */
	protected List<KimGroupImpl> getParentGroups(String groupId) {
		if ( groupId == null ) {
			return new ArrayList<KimGroupImpl>(0);
		}
		Set<KimGroupImpl> groups = new HashSet<KimGroupImpl>();
		getParentGroupsInternal( groupId, groups );
		return new ArrayList<KimGroupImpl>( groups );
	}
	
	protected void getParentGroupsInternal( String groupId, Set<KimGroupImpl> groups ) {
		List<KimGroupImpl> parentGroups = getDirectParentGroups( groupId );
		for ( KimGroupImpl group : parentGroups ) {
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
            List<KimGroupImpl> groupList = getDirectParentGroups(groupId);

            for (KimGroupImpl group : groupList) {
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
            List<KimGroupImpl> groupList = getParentGroups(groupId);

            for (KimGroupImpl group : groupList) {
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
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put("groupId", groupId);
		List<GroupGroup> groups = (List<GroupGroup>)getBusinessObjectService().findMatching(GroupGroupImpl.class, criteria);
		ArrayList<String> groupIds = new ArrayList<String>( groups.size() );
		for ( GroupGroup group : groups ) {
			if ( isGroupActive( group.getMemberGroupId() ) ) {
				groupIds.add( group.getMemberGroupId() );
			}
		}
		return groupIds;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.GroupService#isGroupActive(java.lang.String)
	 */
	public boolean isGroupActive( String groupId ) {
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put("groupId", groupId);
		criteria.put("active", "Y");
		return getBusinessObjectService().countMatching(KimGroupImpl.class, criteria) > 0;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.GroupService#getMemberGroupIds(java.lang.String)
	 */
	public List<String> getMemberGroupIds(String groupId) {
		if ( groupId == null ) {
			return new ArrayList<String>(0);
		}
		List<KimGroupImpl> groups = getMemberGroups( groupId );
		ArrayList<String> groupIds = new ArrayList<String>( groups.size() );
		for ( KimGroupImpl group : groups ) {
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
		criteria.put("memberPrincipals.memberPrincipalId", principalId);
		criteria.put("groupId", groupId);
		criteria.put("active", "Y");
		return getBusinessObjectService().countMatching(KimGroupImpl.class, criteria) != 0;
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

    /**
     * @see org.kuali.rice.kim.service.GroupService#createGroup(org.kuali.rice.kim.bo.group.dto.GroupInfo)
     */
    public GroupInfo createGroup(GroupInfo groupInfo) {
        KimGroupImpl group = new KimGroupImpl();

        copyInfoToGroup(groupInfo, group);
        if(groupInfo.getAttributes() != null && groupInfo.getAttributes().size() > 0) {
        	group.getGroupAttributes().addAll(copyInfoAttributesToGroupAttributes(groupInfo.getAttributes(), groupInfo.getGroupId(), groupInfo.getKimTypeId()));
        }
        
        saveGroup(group);

        return getGroupInfoByName(groupInfo.getNamespaceCode(), groupInfo.getGroupName());
    }

	protected void saveGroup(KimGroupImpl group) {
		if ( group == null ) {
			return;
		}
		getBusinessObjectService().save( group );
	}
    
    /**
     * @see org.kuali.rice.kim.service.GroupService#updateGroup(java.lang.String, org.kuali.rice.kim.bo.group.dto.GroupInfo)
     */
    public GroupInfo updateGroup(String groupId, GroupInfo groupInfo) {
        // TODO sgibson - can this be used to change id?
        KimGroupImpl group = getGroupImpl(groupId);

        if (group == null) {
            throw new IllegalArgumentException("Group not found for update.");
        }

        copyInfoToGroup(groupInfo, group);
        saveGroup(group);

        return getGroupInfo(groupInfo.getGroupId());
    }

    /**
     * @see org.kuali.rice.kim.service.GroupService#addPrincipalToGroup(java.lang.String, java.lang.String)
     */
    public boolean addPrincipalToGroup(String principalId, String groupId) {
        GroupPrincipalImpl groupPrincipal = new GroupPrincipalImpl();
        groupPrincipal.setGroupId(groupId);
        groupPrincipal.setMemberPrincipalId(principalId);
        
        this.getBusinessObjectService().save(groupPrincipal);
        
        return true;
    }

    /** 
     * @see org.kuali.rice.kim.service.GroupService#removePrincipalFromGroup(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public boolean removePrincipalFromGroup(String principalId, String groupId) {
        Map<String,String> criteria = new HashMap<String,String>();
        criteria.put("groupId", groupId);
        criteria.put("memberPrincipalId", principalId);
        Collection<GroupPrincipalImpl> groupPrincipalList = getBusinessObjectService().findMatching(GroupPrincipalImpl.class, criteria);
        
        if(groupPrincipalList.size() == 1) {
            getBusinessObjectService().delete(groupPrincipalList.iterator().next());
            
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
        
        GroupGroupImpl groupGroup = new GroupGroupImpl();
        groupGroup.setGroupId(parentId);
        groupGroup.setMemberGroupId(childId);
        
        getBusinessObjectService().save(groupGroup);
        
        return true;
    }

    /** 
     * @see org.kuali.rice.kim.service.GroupService#removeGroupFromGroup(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public boolean removeGroupFromGroup(String childId, String parentId) {
        Map<String,String> criteria = new HashMap<String,String>();
        criteria.put("groupId", parentId);
        criteria.put("memberGroupId", childId);
        Collection<GroupGroupImpl> groupGroupList = getBusinessObjectService().findMatching(GroupGroupImpl.class, criteria);
        
        if(groupGroupList.size() == 1) {
            getBusinessObjectService().delete(groupGroupList.iterator().next());
            
            return true;
        }
        
        return false;
    }
    
    protected GroupInfo toGroupInfo(KimGroupImpl kimGroup) {
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

    protected KimGroupImpl copyInfoToGroup(GroupInfo info, KimGroupImpl group) {
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
            groupAttr.setTargetPrimaryKey(groupId);
            groupAttr.setKimTypeId(kimTypeId);
            
            attrList.add(groupAttr);
        }
        
        return attrList;
    }

}
