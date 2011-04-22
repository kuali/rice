/*
 * Copyright 2007-2009 The Kuali Foundation
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

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.dto.GroupMembershipInfo;
import org.kuali.rice.kim.bo.group.impl.GroupAttributeDataImpl;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.dao.KimGroupDao;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KIMWebServiceConstants;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.kns.util.KNSPropertyConstants;

@WebService(endpointInterface = KIMWebServiceConstants.GroupService.INTERFACE_CLASS, serviceName = KIMWebServiceConstants.GroupService.WEB_SERVICE_NAME, portName = KIMWebServiceConstants.GroupService.WEB_SERVICE_PORT, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
public class GroupServiceImpl extends GroupServiceBase implements GroupService {

    private KimGroupDao kimGroupDao;
	   
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
        List<? extends Group> groupList = this.lookupGroups(searchCriteria);
        List<String> result = new ArrayList<String>();

        for (Group group : groupList) {
            result.add(group.getGroupId());
        }

        return result;
    }

    /**
	 * @see org.kuali.rice.kim.service.GroupService#lookupGroups(java.util.Map)
	 */
    @SuppressWarnings("unchecked")
	public List<? extends Group> lookupGroups(Map<String, String> searchCriteria) {
    	return this.toGroupInfo( kimGroupDao.getGroups(searchCriteria));
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
		Set<String> visitedGroupIds = new HashSet<String>();
		return getMemberPrincipalIdsInternal(groupId, visitedGroupIds);
		}

	/**
	 * @see org.kuali.rice.kim.service.GroupService#isMemberOfGroup(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean isMemberOfGroup(String principalId, String groupId) {
		if ( principalId == null || groupId == null ) {
			return false;
		}
		Set<String> visitedGroupIds = new HashSet<String>();
		return isMemberOfGroupInternal(principalId, groupId, visitedGroupIds);
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

    

    

    

    public Collection<GroupMembershipInfo> getGroupMembers( List<String> groupIds ) {
		if ( groupIds == null ) {
			return Collections.emptyList();
		}
    	List<GroupMembershipInfo> groupMembers = new ArrayList<GroupMembershipInfo>();
    	for (String groupId : groupIds) {
    		groupMembers.addAll( getGroupMembersOfGroup(groupId) );
    	}
    	return groupMembers;
    }


	@SuppressWarnings("unchecked")
	public Collection<GroupMembershipInfo> getGroupMembersOfGroup( String groupId ) {
		if ( groupId == null ) {
			return Collections.emptyList();
		}
		Map<String,String> criteria = new HashMap<String,String>( 1 );
		criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, groupId);
    	List<GroupMemberImpl> groupMemberImpls = (List<GroupMemberImpl>)getBusinessObjectService().findMatching(GroupMemberImpl.class, criteria);
    	List<GroupMembershipInfo> groupMembers = new ArrayList<GroupMembershipInfo>( groupMemberImpls.size() );
		for (GroupMemberImpl groupMember : groupMemberImpls) {
			if (groupMember != null && groupMember.isActive()) {
				groupMembers.add(toGroupMemberInfo(groupMember));
			}
		}
		return groupMembers;
	}

    protected GroupMembershipInfo toGroupMemberInfo(GroupMemberImpl kimGroupMember) {
    	GroupMembershipInfo groupMemberinfo = null;

        if (kimGroupMember != null) {
        	groupMemberinfo = new GroupMembershipInfo(kimGroupMember.getGroupId(), kimGroupMember.getGroupMemberId(),kimGroupMember.getMemberId(),kimGroupMember.getMemberTypeCode(), kimGroupMember.getActiveFromDate(), kimGroupMember.getActiveToDate());
        	groupMemberinfo.setVersionNumber(kimGroupMember.getVersionNumber());
        }

        return groupMemberinfo;
    }

    public KimGroupDao getKimGroupDao() {
        return kimGroupDao;
    }
    
    public void setKimGroupDao(KimGroupDao kimGroupDao) {
        this.kimGroupDao = kimGroupDao;
    }
}
