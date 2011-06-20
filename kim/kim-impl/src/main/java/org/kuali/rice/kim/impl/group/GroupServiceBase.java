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
import org.kuali.rice.core.api.criteria.CriteriaLookupService;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.LookupCustomizer;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.api.group.GroupMemberQueryResults;
import org.kuali.rice.kim.api.group.GroupQueryResults;
import org.kuali.rice.kim.impl.common.attribute.AttributeTransform;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

public abstract class GroupServiceBase {
    protected BusinessObjectService businessObjectService;
    private CriteriaLookupService criteriaLookupService;

    //@Override
    public Group getGroup(String groupId) {
		return GroupBo.to(getGroupBo(groupId));
    }

    protected GroupBo getGroupBo(String groupId) {
        if ( StringUtils.isEmpty(groupId) ) {
			 throw new RiceIllegalArgumentException("groupId is blank");
		}
        return (GroupBo)businessObjectService.findBySinglePrimaryKey(GroupBo.class, groupId);

    }

    //@Override
	public boolean isGroupMemberOfGroup(String groupMemberId, String groupId) {
        if ( StringUtils.isEmpty(groupId) || StringUtils.isEmpty(groupMemberId) ) {
			 throw new RiceIllegalArgumentException("groupMemberId or groupId is blank");
		}

        Set<String> visitedGroupIds = new HashSet<String>();
		return isMemberOfGroupInternal(groupMemberId, groupId, visitedGroupIds, KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE);
	}

    //@Override
    public boolean isMemberOfGroup(String principalId, String groupId) {
        if ( principalId == null || groupId == null ) {
			return false;
		}
		Set<String> visitedGroupIds = new HashSet<String>();
		return isMemberOfGroupInternal(principalId, groupId, visitedGroupIds, KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
    }

    /**
     * @see org.kuali.rice.kim.api.group.GroupService#getGroups(java.util.Collection)
     */
    //@Override
    public Map<String, Group> getGroups(Collection<String> groupIds) {
        Map<String, Group> result = new HashMap<String, Group>();
        if (CollectionUtils.isEmpty(groupIds)) {
            return result;
        }
        final QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(and(in("id", groupIds.toArray()), equal("active", "Y")));
        GroupQueryResults qr = findGroups(builder.build());
        for (Group group : qr.getResults()) {
            result.put(group.getId(), group);
        }
        return result;
    }

    //@Override
    public Group getGroupByName(String namespaceCode, String groupName) {
        if ( namespaceCode == null || groupName == null ) {
			return null;
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode);
		criteria.put(KimConstants.UniqueKeyConstants.GROUP_NAME, groupName);
		Collection<GroupBo> groups = businessObjectService.findMatching(GroupBo.class, criteria);
		if ( groups.size() > 0 ) {
			return GroupBo.to(groups.iterator().next());
		}
		return null;
    }

    public GroupQueryResults findGroups(final QueryByCriteria queryByCriteria) {
        if (queryByCriteria == null) {
            throw new RiceIllegalArgumentException("queryByCriteria is null");
        }

        LookupCustomizer.Builder<GroupBo> lc = LookupCustomizer.Builder.create();
        lc.setPredicateTransform(AttributeTransform.getInstance());

        GenericQueryResults<GroupBo> results = criteriaLookupService.lookup(GroupBo.class, queryByCriteria, lc.build());

        GroupQueryResults.Builder builder = GroupQueryResults.Builder.create();
        builder.setMoreResultsAvailable(results.isMoreResultsAvailable());
        builder.setTotalRowCount(results.getTotalRowCount());

        final List<Group.Builder> ims = new ArrayList<Group.Builder>();
        for (GroupBo bo : results.getResults()) {
            ims.add(Group.Builder.create(bo));
        }

        builder.setResults(ims);
        return builder.build();
    }

    public GroupMemberQueryResults findGroupMembers(final QueryByCriteria queryByCriteria) {
        if (queryByCriteria == null) {
            throw new RiceIllegalArgumentException("queryByCriteria is null");
        }

        GenericQueryResults<GroupMemberBo> results = criteriaLookupService.lookup(GroupMemberBo.class, queryByCriteria);

        GroupMemberQueryResults.Builder builder = GroupMemberQueryResults.Builder.create();
        builder.setMoreResultsAvailable(results.isMoreResultsAvailable());
        builder.setTotalRowCount(results.getTotalRowCount());

        final List<GroupMember.Builder> ims = new ArrayList<GroupMember.Builder>();
        for (GroupMemberBo bo : results.getResults()) {
            ims.add(GroupMember.Builder.create(bo));
        }

        builder.setResults(ims);
        return builder.build();
    }


    public boolean isMemberOfGroupInternal(String memberId, String groupId, Set<String> visitedGroupIds, String memberType) {
		if ( memberId == null || groupId == null ) {
			return false;
		}

		// when group traversal is not needed
		Group group = getGroup(groupId);
		if ( group == null || !group.isActive() ) {
			return false;
		}

        List<GroupMember> members = getMembersOfGroup(group.getId());
		// check the immediate group
		for (String groupMemberId : getMemberIdsByType(members, memberType)) {
			if (groupMemberId.equals(memberId)) {
				return true;
			}
		}

		// check each contained group, returning as soon as a match is found
		for ( String memberGroupId : getMemberIdsByType(members, KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE) ) {
			if (!visitedGroupIds.contains(memberGroupId)){
				visitedGroupIds.add(memberGroupId);
				if ( isMemberOfGroupInternal( memberId, memberGroupId, visitedGroupIds, memberType ) ) {
					return true;
				}
			}
		}

		// no match found, return false
		return false;
	}

    protected void getParentGroupsInternal( String groupId, Set<Group> groups ) {
		Map<String,Group> parentGroups = getDirectParentGroups( groupId );
		for ( Group group : parentGroups.values() ) {
			if ( !groups.contains( group ) ) {
				groups.add( group );
				getParentGroupsInternal( group.getId(), groups );
			}
		}
	}

    protected Map<String,Group> getDirectParentGroups(String groupId) {
		if ( groupId == null ) {
			return Collections.emptyMap();
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put(KIMPropertyConstants.GroupMember.MEMBER_ID, groupId);
		criteria.put(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE);

		List<GroupMemberBo> groupMembers = (List<GroupMemberBo>)businessObjectService.findMatching(GroupMemberBo.class, criteria);
		Set<String> matchingGroupIds = new HashSet<String>();
		// filter to active groups
		for ( GroupMemberBo gm : groupMembers ) {
			if ( gm.isActive(new Timestamp(System.currentTimeMillis())) ) {
				matchingGroupIds.add(gm.getGroupId());
			}
		}
		return getGroups(matchingGroupIds);
	}

    public List<GroupMember> getMembersOfGroup(String groupId) {
        if (groupId == null) {
            throw new RiceIllegalArgumentException("groupId is blank");
		}
        Map<String,String> criteria = new HashMap<String,String>();
		criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, groupId);

		Collection<GroupMemberBo> groupMembersBos = businessObjectService.findMatching(GroupMemberBo.class, criteria);
        List<GroupMember> groupMembers = new ArrayList<GroupMember>();
        for (GroupMemberBo groupBo : groupMembersBos) {
            if (groupBo.isActive(new Timestamp(System.currentTimeMillis()))){
                groupMembers.add(GroupMemberBo.to(groupBo));
            }
        }
        return groupMembers;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the criteriaLookupService attribute value.
     *
     * @param criteriaLookupService The criteriaLookupService to set.
     */
    public void setCriteriaLookupService(final CriteriaLookupService criteriaLookupService) {
        this.criteriaLookupService = criteriaLookupService;
    }

    protected List<Group> toGroupList(List<GroupBo> groupBos) {
        if (groupBos == null) {
            return null;
        }
        List<Group> groups = new ArrayList<Group>();
        for (GroupBo bo : groupBos) {
            groups.add(GroupBo.to(bo));
        }
        return groups;
    }

    /*protected List<String> getMemberIdsByType(Group group, String memberType) {
        List<String> principalIds = new ArrayList<String>();
        if (group != null) {
            for (GroupMember member : getMembersOfGroup(group.getId())) {
                if (member.getTypeCode().equals(memberType)) {
                    principalIds.add(member.getMemberId());
                }
            }
        }
        return principalIds;
    }*/

    protected List<GroupMember> getMembersByType(Collection<GroupMember> members, String memberType) {
        List<GroupMember> membersByType = new ArrayList<GroupMember>();
        if (members != null) {
            for (GroupMember member : members) {
                if (member.getTypeCode().equals(memberType)) {
                    membersByType.add(member);
                }
            }
        }
        return membersByType;
    }

    protected List<String> getMemberIdsByType(Collection<GroupMember> members, String memberType) {
        List<String> membersIds = new ArrayList<String>();
        if (members != null) {
            for (GroupMember member : members) {
                if (member.getTypeCode().equals(memberType)) {
                    membersIds.add(member.getMemberId());
                }
            }
        }
        return membersIds;
    }
}
