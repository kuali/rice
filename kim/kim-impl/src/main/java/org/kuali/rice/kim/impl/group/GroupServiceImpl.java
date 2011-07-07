/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kim.impl.group;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.api.group.GroupQueryResults;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimConstants;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GroupServiceImpl extends GroupServiceBase implements GroupService {

    @Override
    public List<Group> getGroupsForPrincipal(String principalId) throws RiceIllegalArgumentException {
        if ( StringUtils.isEmpty(principalId) ) {
			 throw new RiceIllegalArgumentException("principalId is blank");
		}
        return getGroupsForPrincipalByNamespace( principalId, null );
    }

    @Override
    public List<Group> getGroupsForPrincipalByNamespace(String principalId, String namespaceCode) throws RiceIllegalArgumentException {
        if ( StringUtils.isEmpty(principalId) ) {
			 throw new RiceIllegalArgumentException("principalId is blank");
		}
        Collection<Group> directGroups = getDirectGroupsForPrincipal( principalId, namespaceCode );
		Set<Group> groups = new HashSet<Group>();
        groups.addAll(directGroups);
		for ( Group group : directGroups ) {
			groups.add( group );
			groups.addAll( getParentGroups( group.getId() ) );
		}
		return new ArrayList<Group>( groups );
    }

    @Override
    public List<String> findGroupIds(final QueryByCriteria queryByCriteria) {
        GroupQueryResults results = this.findGroups(queryByCriteria);
        List<String> result = new ArrayList<String>();

        for (Group group : results.getResults()) {
            result.add(group.getId());
        }

        return result;
    }

    /*@Override
    public List<Group> lookupGroups(Map<String, String> searchCriteria) {
        List<GroupBo> groupBos = groupDao.getGroups(searchCriteria);
        List<Group> groups = toGroupList(groupBos);
        if (groups == null) {
            return Collections.emptyList();
        }
        return groups;
    }*/

    @Override
    public boolean isDirectMemberOfGroup(String principalId, String groupId) throws RiceIllegalArgumentException {
        if ( StringUtils.isEmpty(groupId) ) {
			throw new RiceIllegalArgumentException("groupId is blank");
		}
        if ( StringUtils.isEmpty(principalId) ) {
			throw new RiceIllegalArgumentException("principalId is blank");
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put(KIMPropertyConstants.GroupMember.MEMBER_ID, principalId);
		criteria.put(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
		criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, groupId);

		Collection<GroupMemberBo> groupMembers = businessObjectService.findMatching(GroupMemberBo.class, criteria);
		for ( GroupMemberBo gm : groupMembers ) {
			if ( gm.isActive(new Timestamp(System.currentTimeMillis())) ) {
				return true;
			}
		}
		return false;
    }

    @Override
    public List<String> getGroupIdsForPrincipal(String principalId) throws RiceIllegalArgumentException {
        if ( StringUtils.isEmpty(principalId) ) {
			throw new RiceIllegalArgumentException("principalId is blank");
		}
        return getGroupIdsForPrincipalByNamespace(principalId, null);
    }

    @Override
    public List<String> getGroupIdsForPrincipalByNamespace(String principalId, String namespaceCode) throws RiceIllegalArgumentException {
        if ( StringUtils.isEmpty(principalId) ) {
			 throw new RiceIllegalArgumentException("principalId is blank");
		}
        List<String> result = new ArrayList<String>();

        if (principalId != null) {
            List<Group> groupList = getGroupsForPrincipalByNamespace(principalId, namespaceCode);

            for (Group group : groupList) {
                result.add(group.getId());
            }
        }

        return result;
    }

    @Override
    public List<String> getDirectGroupIdsForPrincipal(String principalId) throws RiceIllegalArgumentException {
        if ( StringUtils.isEmpty(principalId) ) {
			throw new RiceIllegalArgumentException("principalId is blank");
		}
        List<String> result = new ArrayList<String>();

        if (principalId != null) {
        	Collection<Group> groupList = getDirectGroupsForPrincipal(principalId);

            for (Group g : groupList) {
                result.add(g.getId());
            }
        }

        return result;
    }

    @Override
    public List<String> getMemberPrincipalIds(String groupId) throws RiceIllegalArgumentException {
        if ( StringUtils.isEmpty(groupId) ) {
			throw new RiceIllegalArgumentException("groupId is blank");
		}
		Set<String> visitedGroupIds = new HashSet<String>();
		return getMemberPrincipalIdsInternal(groupId, visitedGroupIds);
    }

    @Override
    public List<String> getDirectMemberPrincipalIds(String groupId) throws RiceIllegalArgumentException {
        if ( StringUtils.isEmpty(groupId) ) {
			throw new RiceIllegalArgumentException("groupId is blank");
		}
        //Group group = getGroup(groupId);
        return this.getMemberIdsByType(getMembersOfGroup(groupId), KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
    }

    @Override
    public List<String> getMemberGroupIds(String groupId) throws RiceIllegalArgumentException {
        /*if ( StringUtils.isEmpty(groupId) ) {
			throw new RiceIllegalArgumentException("groupId is blank");
		}
		Set<String> visitedGroupIds = new HashSet<String>();
		return getMemberIdsInternalByType(groupId, visitedGroupIds, KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE);
*/

        if ( StringUtils.isEmpty(groupId) ) {
			throw new RiceIllegalArgumentException("groupId is blank");
		}
		List<GroupBo> groups = getMemberGroupBos( groupId );
		ArrayList<String> groupIds = new ArrayList<String>( groups.size() );
		for ( GroupBo group : groups ) {
			if ( group.isActive() ) {
				groupIds.add( group.getId() );
			}
		}
		return groupIds;
    }


	protected List<GroupBo> getMemberGroupBos(String groupId) {
		if ( groupId == null ) {
			return Collections.emptyList();
		}
		Set<GroupBo> groups = new HashSet<GroupBo>();

		GroupBo group = getGroupBo(groupId);
		getMemberGroupsInternal(group, groups);

		return new ArrayList<GroupBo>(groups);
	}

    protected void getMemberGroupsInternal( GroupBo group, Set<GroupBo> groups ) {
		if ( group == null ) {
			return;
		}
		List<String> groupIds = group.getMemberGroupIds();

		for (String id : groupIds) {
			GroupBo memberGroup = getGroupBo(id);
			// if we've already seen that group, don't recurse into it
			if ( memberGroup.isActive() && !groups.contains( memberGroup ) ) {
				groups.add(memberGroup);
				getMemberGroupsInternal(memberGroup,groups);
			}
		}

	}

    @Override
    public List<String> getDirectMemberGroupIds(String groupId) {
        if ( groupId == null ) {
			return Collections.emptyList();
		}
        //Group group = getGroup(groupId);
        return this.getMemberIdsByType(getMembersOfGroup(groupId), KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE);
    }

    @Override
    public List<String> getParentGroupIds(String groupId) throws RiceIllegalArgumentException {
        if ( StringUtils.isEmpty(groupId) ) {
			throw new RiceIllegalArgumentException("groupId is blank");
		}
        List<String> result = new ArrayList<String>();
        if (groupId != null) {
            List<Group> groupList = getParentGroups(groupId);

            for (Group group : groupList) {
                result.add(group.getId());
            }
        }

        return result;
    }

    @Override
    public List<String> getDirectParentGroupIds(String groupId) throws RiceIllegalArgumentException {
        if ( StringUtils.isEmpty(groupId) ) {
			throw new RiceIllegalArgumentException("groupId is blank");
		}
        List<String> result = new ArrayList<String>();
        if (groupId != null) {
            List<Group> groupList = getDirectParentGroups(groupId);
            for (Group group : groupList) {
                result.add(group.getId());
            }
        }

        return result;
    }

    @Override
    public Attributes getAttributes(String groupId) throws RiceIllegalArgumentException {
        if ( StringUtils.isEmpty(groupId) ) {
			throw new RiceIllegalArgumentException("groupId is blank");
		}

        if (groupId == null) {
            return Attributes.empty();
        }

        Group group = getGroup(groupId);
        if (group != null) {
            return group.getAttributes();
        }
        return null;
    }

    @Override
    public List<GroupMember> getMembers(List<String> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) {
            throw new RiceIllegalArgumentException("groupIds is empty");
		}

        //TODO: PRIME example of something for new Criteria API
        List<GroupMember> groupMembers = new ArrayList<GroupMember>();
        for (String groupId : groupIds) {
              groupMembers.addAll(getMembersOfGroup(groupId));
        }
        return groupMembers;
    }

   /* @Override
    public Collection<Person> getPersonMembersOfGroup(final String groupId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<Group> getGroupMembersOfGroup(@WebParam(name = "groupId") final String groupId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }*/


	protected List<Group> getParentGroups(String groupId) throws RiceIllegalArgumentException {
		if ( StringUtils.isEmpty(groupId) ) {
			throw new RiceIllegalArgumentException("groupId is blank");
		}
		Set<Group> groups = new HashSet<Group>();
		getParentGroupsInternal( groupId, groups );
		return new ArrayList<Group>( groups );
	}

    protected List<String> getMemberPrincipalIdsInternal(String groupId, Set<String> visitedGroupIds) {
		if ( groupId == null ) {
			return Collections.emptyList();
		}
		Set<String> ids = new HashSet<String>();
		GroupBo group = getGroupBo(groupId);
		if ( group == null || !group.isActive()) {
			return Collections.emptyList();
		}

        //List<String> memberIds = getMemberIdsByType(group, memberType);
        //List<GroupMember> members = new ArrayList<GroupMember>(getMembersOfGroup(group.getId()));
		ids.addAll( group.getMemberPrincipalIds());
		visitedGroupIds.add(group.getId());

		for (String memberGroupId : group.getMemberGroupIds()) {
			if (!visitedGroupIds.contains(memberGroupId)){
				ids.addAll(getMemberPrincipalIdsInternal(memberGroupId, visitedGroupIds));
			}
		}

		return new ArrayList<String>(ids);
	}

    protected Collection<Group> getDirectGroupsForPrincipal( String principalId ) {
		return getDirectGroupsForPrincipal( principalId, null );
	}

    @SuppressWarnings("unchecked")
	protected Collection<Group> getDirectGroupsForPrincipal( String principalId, String namespaceCode ) {
		if ( principalId == null ) {
			return Collections.emptyList();
		}
		Map<String,Object> criteria = new HashMap<String,Object>();
		criteria.put(KIMPropertyConstants.GroupMember.MEMBER_ID, principalId);
		criteria.put(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
		Collection<GroupMemberBo> groupMembers = businessObjectService.findMatching(GroupMemberBo.class, criteria);
		Set<String> groupIds = new HashSet<String>( groupMembers.size() );
		// only return the active members
		for ( GroupMemberBo gm : groupMembers ) {
			if ( gm.isActive(new Timestamp(System.currentTimeMillis())) ) {
				groupIds.add( gm.getGroupId() );
			}
		}
		// pull all the group information for the matching members
		List<Group> groups = getGroups(groupIds);
		List<Group> result = new ArrayList<Group>( groups.size() );
		// filter by namespace if necessary
		for ( Group group : groups ) {
			if ( group.isActive() ) {
				if ( StringUtils.isBlank(namespaceCode) || StringUtils.equals(namespaceCode, group.getNamespaceCode() ) ) {
					result.add(group);
				}
			}
		}
		return result;
	}
}
