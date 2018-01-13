/**
 * Copyright 2005-2018 The Kuali Foundation
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

import static org.kuali.rice.core.api.criteria.PredicateFactory.and;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.in;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebParam;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.history.HistoryQueryUtils;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.api.group.GroupMemberQueryResults;
import org.kuali.rice.kim.api.group.GroupQueryResults;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kim.impl.common.attribute.AttributeTransform;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.impl.services.KimImplServiceLocator;
import org.kuali.rice.krad.data.DataObjectService;

public class GroupServiceImpl extends GroupServiceBase implements GroupService {
    private static final Logger LOG = Logger.getLogger(GroupServiceImpl.class);

    private DataObjectService dataObjectService;

    @Override
    public Group getGroup(String groupId) throws RiceIllegalArgumentException {
        incomingParamCheck(groupId, "groupId");
		return GroupBo.to(getGroupBo(groupId));
    }

    @Override
    public List<Group> getGroupsByPrincipalId(String principalId) throws RiceIllegalArgumentException {
        incomingParamCheck(principalId,  "principalId");
        return getGroupsByPrincipalIdAndNamespaceCodeInternal(principalId, null);
    }

    @Override
    public List<Group> getGroupsByPrincipalIdAndNamespaceCode(String principalId, String namespaceCode) throws RiceIllegalArgumentException {
        incomingParamCheck(principalId, "principalId");
        incomingParamCheck(namespaceCode, "namespaceCode");
           Collections.singleton("name");
		return getGroupsByPrincipalIdAndNamespaceCodeInternal(principalId, namespaceCode);
    }

    protected List<Group> getGroupsByPrincipalIdAndNamespaceCodeInternal(String principalId, String namespaceCode) throws RiceIllegalArgumentException {

        Collection<Group> directGroups = getDirectGroupsForPrincipal( principalId, namespaceCode, new DateTime(System.currentTimeMillis()) );
		Set<Group> groups = new HashSet<Group>();
        groups.addAll(directGroups);
		for ( Group group : directGroups ) {
			groups.add( group );
			groups.addAll( getParentGroups( group.getId() ) );
		}
		return Collections.unmodifiableList(new ArrayList<Group>( groups ));
    }

    @Override
    public List<String> findGroupIds(final QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException {
        incomingParamCheck(queryByCriteria, "queryByCriteria");

        GroupQueryResults results = this.findGroups(queryByCriteria);
        List<String> result = new ArrayList<String>();

        for (Group group : results.getResults()) {
            result.add(group.getId());
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public boolean isDirectMemberOfGroup(String principalId, String groupId) throws RiceIllegalArgumentException {
        incomingParamCheck(principalId, "principalId");
        incomingParamCheck(groupId, "groupId");

        final QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(
                and(
                        equal(KIMPropertyConstants.GroupMember.MEMBER_ID, principalId),
                        equal(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE.getCode()),
                        equal(KIMPropertyConstants.GroupMember.GROUP_ID, groupId),
                        HistoryQueryUtils.between(KIMPropertyConstants.KimMember.ACTIVE_FROM_DATE_VALUE, KIMPropertyConstants.KimMember.ACTIVE_TO_DATE_VALUE, DateTime.now()))
        );
		QueryResults<GroupMemberBo> groupMembers = dataObjectService.findMatching(GroupMemberBo.class, builder.build());
		return (groupMembers.getResults().size() > 0);
    }

    @Override
    public List<String> getGroupIdsByPrincipalId(String principalId) throws RiceIllegalArgumentException {
        incomingParamCheck(principalId, "principalId");
        return getGroupIdsByPrincipalIdAndNamespaceCodeInternal(principalId, null);
    }

    @Override
    public List<String> getGroupIdsByPrincipalIdAndNamespaceCode(String principalId, String namespaceCode) throws RiceIllegalArgumentException {
        incomingParamCheck(principalId, "principalId");
        incomingParamCheck(namespaceCode, "namespaceCode");

        List<String> result = new ArrayList<String>();

        if (principalId != null) {
            List<Group> groupList = getGroupsByPrincipalIdAndNamespaceCode(principalId, namespaceCode);

            for (Group group : groupList) {
                result.add(group.getId());
            }
        }

        return Collections.unmodifiableList(result);
    }

    protected List<String> getGroupIdsByPrincipalIdAndNamespaceCodeInternal(String principalId, String namespaceCode) throws RiceIllegalArgumentException {

        List<String> result = new ArrayList<String>();

        if (principalId != null) {
            List<Group> groupList = getGroupsByPrincipalIdAndNamespaceCodeInternal(principalId, namespaceCode);

            for (Group group : groupList) {
                result.add(group.getId());
            }
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public List<String> getDirectGroupIdsByPrincipalId(String principalId) throws RiceIllegalArgumentException {
        incomingParamCheck(principalId, "principalId");

        List<String> result = new ArrayList<String>();

        if (principalId != null) {
        	Collection<Group> groupList = getDirectGroupsForPrincipal(principalId);

            for (Group g : groupList) {
                result.add(g.getId());
            }
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public List<String> getMemberPrincipalIds(String groupId) throws RiceIllegalArgumentException {
        incomingParamCheck(groupId, "groupId");

		return getMemberPrincipalIdsInternal(groupId, new HashSet<String>());
    }

    @Override
    public List<String> getDirectMemberPrincipalIds(String groupId) throws RiceIllegalArgumentException {
        incomingParamCheck(groupId, "groupId");

        return this.getMemberIdsByType(getMembersOfGroup(groupId), KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
    }

    @Override
    public List<String> getMemberGroupIds(String groupId) throws RiceIllegalArgumentException {
        incomingParamCheck(groupId, "groupId");

		List<GroupBo> groups = getMemberGroupBos( groupId );
		ArrayList<String> groupIds = new ArrayList<String>( groups.size() );
		for ( GroupBo group : groups ) {
			if ( group.isActive() ) {
				groupIds.add( group.getId() );
			}
		}
		return Collections.unmodifiableList(groupIds);
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
	public boolean isGroupMemberOfGroup(String groupMemberId, String groupId) throws RiceIllegalArgumentException {
		return isGroupMemberOfGroupWithDate(groupMemberId, groupId, new DateTime(System.currentTimeMillis()));
	}

    @Override
    public boolean isGroupMemberOfGroupWithDate(String groupMemberId, String groupId, DateTime asOfDate) throws RiceIllegalArgumentException {
        incomingParamCheck(groupMemberId, "groupMemberId");
        incomingParamCheck(groupId, "groupId");
        incomingParamCheck(asOfDate, "asOfDate");

        return isMemberOfGroupInternal(groupMemberId, groupId, new HashSet<String>(), KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE, asOfDate);
    }

    @Override
    public boolean isMemberOfGroup(String principalId, String groupId) throws RiceIllegalArgumentException{
        return isMemberOfGroupWithDate(principalId, groupId, new DateTime(System.currentTimeMillis()));
    }

    @Override
    public boolean isMemberOfGroupWithDate(String principalId, String groupId, DateTime asOfDate) throws RiceIllegalArgumentException{
        incomingParamCheck(principalId, "principalId");
        incomingParamCheck(groupId, "groupId");
        incomingParamCheck(asOfDate, "asOfDate");

        Set<String> visitedGroupIds = new HashSet<String>();
        return isMemberOfGroupInternal(principalId, groupId, visitedGroupIds, KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE, asOfDate);
    }

    @Override
    public List<String> getDirectMemberGroupIds(String groupId) throws RiceIllegalArgumentException{
        incomingParamCheck(groupId, "groupId");

        return this.getMemberIdsByType(getMembersOfGroup(groupId), KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE);
    }

    @Override
    public List<String> getDirectMemberGroupIdsWithDate(String groupId) throws RiceIllegalArgumentException{
        incomingParamCheck(groupId, "groupId");

        return this.getMemberIdsByType(getMembersOfGroup(groupId), KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE);
    }

    @Override
    public List<String> getParentGroupIds(String groupId) throws RiceIllegalArgumentException {
        incomingParamCheck(groupId, "groupId");

        List<String> result = new ArrayList<String>();
        if (groupId != null) {
            List<Group> groupList = getParentGroups(groupId);

            for (Group group : groupList) {
                result.add(group.getId());
            }
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public List<String> getDirectParentGroupIds(String groupId) throws RiceIllegalArgumentException {
        return getDirectParentGroupIdsWithDate(groupId, new DateTime(System.currentTimeMillis()));
    }

    @Override
    public List<String> getDirectParentGroupIdsWithDate(String groupId, DateTime asOfDate) throws RiceIllegalArgumentException {
        incomingParamCheck(groupId, "groupId");
        incomingParamCheck(asOfDate, "asOfDate");

        List<String> result = new ArrayList<String>();
        if (groupId != null) {
            List<Group> groupList = getDirectParentGroups(groupId, asOfDate);
            for (Group group : groupList) {
                result.add(group.getId());
            }
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public Map<String, String> getAttributes(String groupId) throws RiceIllegalArgumentException {
        incomingParamCheck(groupId, "groupId");

        Group group = getGroup(groupId);
        if (group != null) {
            return group.getAttributes();
        }
        return Collections.emptyMap();
    }

    @Override
    public List<GroupMember> getMembers(List<String> groupIds) throws RiceIllegalArgumentException{
        return getMembersWithDate(groupIds, new DateTime(System.currentTimeMillis()));
    }

    @Override
    public List<GroupMember> getMembersWithDate(List<String> groupIds, DateTime asOfDate) throws RiceIllegalArgumentException{
        incomingParamCheck(groupIds, "groupIds");
        incomingParamCheck(asOfDate, "asOfDate");

        final QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(
                and(
                    in(KIMPropertyConstants.GroupMember.GROUP_ID, groupIds.toArray(new String[groupIds.size()])),
                    HistoryQueryUtils.between(KIMPropertyConstants.KimMember.ACTIVE_FROM_DATE_VALUE, KIMPropertyConstants.KimMember.ACTIVE_TO_DATE_VALUE, asOfDate))
                );
        return findGroupMembers(builder.build()).getResults();
    }

    @Override
    public List<Group> getGroups(Collection<String> groupIds) throws RiceIllegalArgumentException {
        incomingParamCheck(groupIds, "groupIds");
        if (groupIds.isEmpty()) {
            return Collections.emptyList();
        }
        final QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(and(in("id", groupIds.toArray()), equal("active", Boolean.TRUE)));
        GroupQueryResults qr = findGroups(builder.build());

        return qr.getResults();
    }

    @Override
    public Group getGroupByNamespaceCodeAndName(String namespaceCode, String groupName) throws RiceIllegalArgumentException{
        incomingParamCheck(namespaceCode, "namespaceCode");
        incomingParamCheck(groupName, "groupName");

        final QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(
                    and(
                        equal(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode),
                        equal(KimConstants.UniqueKeyConstants.GROUP_NAME, groupName)));
		QueryResults<GroupBo> groups = dataObjectService.findMatching(GroupBo.class, builder.build());
		if ( !groups.getResults().isEmpty() ) {
			return GroupBo.to(groups.getResults().iterator().next());
		}
		return null;
    }

    @Override
    public GroupQueryResults findGroups(final QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException {
        incomingParamCheck(queryByCriteria, "queryByCriteria");

        QueryResults<GroupBo> results = dataObjectService.findMatching(GroupBo.class,
                AttributeTransform.getInstance().apply(queryByCriteria));

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

    @Override
    public GroupMemberQueryResults findGroupMembers(final QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException {
        incomingParamCheck(queryByCriteria, "queryByCriteria");

        QueryResults<GroupMemberBo> results = dataObjectService.findMatching(GroupMemberBo.class, queryByCriteria);

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


    protected boolean isMemberOfGroupInternal(String memberId,
            String groupId,
            Set<String> visitedGroupIds,
            MemberType memberType,
            DateTime asOfDate) {

        if ( memberId == null || groupId == null ) {
			return false;
		}

		// when group traversal is not needed
		Group group = getGroup(groupId);
		if ( group == null || !group.isActive() ) {
			return false;
		}

        List<GroupMember> members = getMembersOfGroupWithDate(group.getId(), asOfDate);
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
				if ( isMemberOfGroupInternal( memberId, memberGroupId, visitedGroupIds, memberType, asOfDate ) ) {
					return true;
				}
			}
		}

		// no match found, return false
		return false;
	}

    protected void getParentGroupsInternal( String groupId, Set<Group> groups ) {
		List<Group> parentGroups = getDirectParentGroups( groupId, new DateTime(System.currentTimeMillis()) );
		for ( Group group : parentGroups ) {
			if ( !groups.contains( group ) ) {
				groups.add( group );
				getParentGroupsInternal( group.getId(), groups );
			}
		}
	}

    protected List<Group> getDirectParentGroups(String groupId, DateTime asOfDate) {
        incomingParamCheck(groupId, "groupId");

        final QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(
                and(
                    equal(KIMPropertyConstants.GroupMember.MEMBER_ID, groupId),
                    equal(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE.getCode()),
                    HistoryQueryUtils.between(KIMPropertyConstants.KimMember.ACTIVE_FROM_DATE_VALUE,
                            KIMPropertyConstants.KimMember.ACTIVE_TO_DATE_VALUE, asOfDate)));

        List<GroupMember> groupMembers = findGroupMembers(builder.build()).getResults();
		Set<String> matchingGroupIds = new HashSet<String>();
		// filter to active groups
		for ( GroupMember gm : groupMembers ) {
		    matchingGroupIds.add(gm.getGroupId());
		}
		if (CollectionUtils.isNotEmpty(matchingGroupIds)) {
            return getGroups(matchingGroupIds);
        }
        return Collections.emptyList();
	}

    @Override
    public List<GroupMember> getMembersOfGroup(String groupId) throws RiceIllegalArgumentException {
        incomingParamCheck(groupId, "groupId");
        return getMembersOfGroupWithDate(groupId, DateTime.now());
    }

    @Override
    public List<GroupMember> getMembersOfGroupWithDate(String groupId, DateTime asOfDate) throws RiceIllegalArgumentException {
        incomingParamCheck(groupId, "groupId");

        final QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(
                and(
                    equal(KIMPropertyConstants.GroupMember.GROUP_ID, groupId),
                    HistoryQueryUtils.between(KIMPropertyConstants.KimMember.ACTIVE_FROM_DATE_VALUE, KIMPropertyConstants.KimMember.ACTIVE_TO_DATE_VALUE, asOfDate)));

        return findGroupMembers(builder.build()).getResults();
    }

    protected List<String> getMemberIdsByType(Collection<GroupMember> members, MemberType memberType) {
        List<String> membersIds = new ArrayList<String>();
        if (members != null) {
            for (GroupMember member : members) {
                if (member.getType().equals(memberType)) {
                    membersIds.add(member.getMemberId());
                }
            }
        }
        return Collections.unmodifiableList(membersIds);
    }

    protected GroupBo getGroupBo(String groupId) {
        incomingParamCheck(groupId, "groupId");
        return dataObjectService.find(GroupBo.class, groupId);
    }

    protected GroupMemberBo getGroupMemberBo(String id) {
        incomingParamCheck(id, "id");
        return dataObjectService.find(GroupMemberBo.class, id);
    }

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

		return Collections.unmodifiableList(new ArrayList<String>(ids));
	}

    protected Collection<Group> getDirectGroupsForPrincipal( String principalId ) {
		return getDirectGroupsForPrincipal( principalId, null, new DateTime(System.currentTimeMillis()) );
	}

	protected Collection<Group> getDirectGroupsForPrincipal( String principalId, String namespaceCode, DateTime asOfDate ) {
		if ( principalId == null ) {
			return Collections.emptyList();
		}

		// only return the active members
        final QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(
                and(
                    equal(KIMPropertyConstants.GroupMember.MEMBER_ID, principalId),
                    equal(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, MemberType.PRINCIPAL.getCode()),
                    HistoryQueryUtils.between(KIMPropertyConstants.KimMember.ACTIVE_FROM_DATE_VALUE, KIMPropertyConstants.KimMember.ACTIVE_TO_DATE_VALUE, asOfDate)));
		List<GroupMember> groupMembers = findGroupMembers(builder.build()).getResults();
        Set<String> groupIds = new HashSet<String>( groupMembers.size());
        for (GroupMember gm : groupMembers) {
            groupIds.add(gm.getGroupId());
        }

		// pull all the group information for the matching members
		List<Group> groups = CollectionUtils.isEmpty(groupIds) ? Collections.<Group>emptyList() : getGroups(groupIds);
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

    @Override
    public boolean addGroupToGroup(String childId, String parentId)  throws RiceIllegalArgumentException {
        incomingParamCheck(childId, "childId");
        incomingParamCheck(parentId, "parentId");

        if(childId.equals(parentId)) {
            throw new RiceIllegalArgumentException("Can't add group to itself.");
        }
        if(isGroupMemberOfGroup(parentId, childId)) {
            throw new RiceIllegalArgumentException("Circular group reference.");
        }

        GroupMemberBo groupMember = new GroupMemberBo();
        groupMember.setGroupId(parentId);
        groupMember.setType(KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE);
        groupMember.setMemberId(childId);

        this.dataObjectService.save(groupMember);
        return true;
    }

    @Override
    public boolean addPrincipalToGroup(String principalId, String groupId) throws RiceIllegalArgumentException {
        incomingParamCheck(principalId, "principalId");
        incomingParamCheck(groupId, "groupId");

        GroupMemberBo groupMember = new GroupMemberBo();
        groupMember.setGroupId(groupId);
        groupMember.setType(KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
        groupMember.setMemberId(principalId);

        groupMember = this.dataObjectService.save(groupMember);
        KimImplServiceLocator.getGroupInternalService().updateForUserAddedToGroup(groupMember.getMemberId(),
                groupMember.getGroupId());
        return true;
    }

    @Override
    public Group createGroup(Group group) throws RiceIllegalArgumentException {
        incomingParamCheck(group, "group");
        if (StringUtils.isNotBlank(group.getId()) && getGroup(group.getId()) != null) {
            throw new RiceIllegalArgumentException("the group to create already exists: " + group);
        }
        List<GroupAttributeBo> attrBos = KimAttributeDataBo
                .createFrom(GroupAttributeBo.class, group.getAttributes(), group.getKimTypeId());
        if (StringUtils.isNotEmpty(group.getId())) {
            for (GroupAttributeBo attr : attrBos) {
                attr.setAssignedToId(group.getId());
            }
        }
        GroupBo bo = GroupBo.from(group);
        bo.setAttributeDetails(attrBos);

        bo = saveGroup(bo);

        return GroupBo.to(bo);
    }

    @Override
    public Group updateGroup(Group group) throws RiceIllegalArgumentException{
        incomingParamCheck(group, "group");
        GroupBo origGroup = getGroupBo(group.getId());
        if (StringUtils.isBlank(group.getId()) || origGroup == null) {
            throw new RiceIllegalArgumentException("the group does not exist: " + group);
        }
        List<GroupAttributeBo> attrBos = KimAttributeDataBo.createFrom(GroupAttributeBo.class, group.getAttributes(), group.getKimTypeId());
        GroupBo bo = GroupBo.from(group);
        bo.setMembers(origGroup.getMembers());
        bo.setAttributeDetails(attrBos);

        bo = saveGroup(bo);
        if (origGroup.isActive()
                && !bo.isActive()) {
            KimImplServiceLocator.getRoleInternalService().groupInactivated(bo.getId());
        }

        return GroupBo.to(bo);
    }

    @Override
	public Group updateGroup(String groupId, Group group) throws RiceIllegalArgumentException{
        incomingParamCheck(group, "group");
        incomingParamCheck(groupId, "groupId");

        if (StringUtils.equals(groupId, group.getId())) {
            return updateGroup(group);
        }

        //if group Ids are different, inactivate old group, and create new with new id based off old
        GroupBo groupBo = getGroupBo(groupId);

        if (StringUtils.isBlank(group.getId()) || groupBo == null) {
            throw new RiceIllegalArgumentException("the group does not exist: " + group);
        }

        //create and save new group
        GroupBo newGroup = GroupBo.from(group);
        newGroup.setMembers(groupBo.getMembers());
        List<GroupAttributeBo> attrBos = KimAttributeDataBo.createFrom(GroupAttributeBo.class, group.getAttributes(), group.getKimTypeId());
        newGroup.setAttributeDetails(attrBos);
        newGroup = saveGroup(newGroup);

        //inactivate and save old group
        groupBo.setActive(false);
        saveGroup(groupBo);

        return GroupBo.to(newGroup);
    }

    @Override
    public GroupMember createGroupMember(GroupMember groupMember) throws RiceIllegalArgumentException {
        incomingParamCheck(groupMember, "groupMember");
        if (StringUtils.isNotBlank(groupMember.getId()) && getGroupMemberBo(groupMember.getId()) != null) {
            throw new RiceIllegalArgumentException("the groupMember to create already exists: " + groupMember);
        }

        GroupMemberBo bo = GroupMemberBo.from(groupMember);
        GroupBo groupBo = getGroupBo(groupMember.getGroupId());
        groupBo.getMembers().add(bo);
        groupBo = saveGroup(groupBo);

        //get new groupMember from saved group
        for (GroupMemberBo member : groupBo.getMembers()) {
            if (member.getMemberId().equals(groupMember.getMemberId())
                    && member.getType().equals(groupMember.getType())
                    && member.getActiveFromDate().equals(groupMember.getActiveFromDate())
                    && member.getActiveToDate().equals(groupMember.getActiveToDate())) {
                return GroupMemberBo.to(member);
            }
        }
        return GroupMemberBo.to(bo);
    }

    @Override
    public GroupMember updateGroupMember(
            @WebParam(name = "groupMember") GroupMember groupMember) throws RiceIllegalArgumentException {
        incomingParamCheck(groupMember, "groupMember");
        if (StringUtils.isBlank(groupMember.getId()) || getGroupMemberBo(groupMember.getId()) == null) {
            throw new RiceIllegalArgumentException("the groupMember to update does not exist: " + groupMember);
        }

        GroupMemberBo bo = GroupMemberBo.from(groupMember);
        GroupBo groupBo = getGroupBo(groupMember.getGroupId());
        //find and replace the existing member

        List<GroupMemberBo> memberList = new ArrayList<GroupMemberBo>();
        for (GroupMemberBo member : groupBo.getMembers()) {
            if (member.getId().equals(bo.getId())) {
                memberList.add(bo);
            } else {
                memberList.add(member);
            }

        }
        groupBo.setMembers(memberList);
        groupBo = saveGroup(groupBo);

        //get new groupMember from saved group
        for (GroupMemberBo member : groupBo.getMembers()) {
            if (member.getId().equals(groupMember.getId())) {
                return GroupMemberBo.to(member);
            }
        }
        return GroupMemberBo.to(bo);
    }

    @Override
    public void removeAllMembers(String groupId) throws RiceIllegalArgumentException{
        incomingParamCheck(groupId, "groupId");


        GroupService groupService = KimApiServiceLocator.getGroupService();
        List<String> memberPrincipalsBefore = groupService.getMemberPrincipalIds(groupId);

        Collection<GroupMemberBo> toDeactivate = getActiveGroupMembers(groupId, null, null);
        java.sql.Timestamp today = new java.sql.Timestamp(System.currentTimeMillis());

        // Set principals as inactive
        for (GroupMemberBo aToDeactivate : toDeactivate) {
            aToDeactivate.setActiveToDateValue(today);
        }

        // Save
        for (GroupMemberBo bo : toDeactivate) {
            this.dataObjectService.save(bo);
        }
        List<String> memberPrincipalsAfter = groupService.getMemberPrincipalIds(groupId);

        if (!CollectionUtils.isEmpty(memberPrincipalsAfter)) {
    	    // should never happen!
    	    LOG.warn("after attempting removal of all members, group with id '" + groupId + "' still has principal members");
        }

        // do updates
        KimImplServiceLocator.getGroupInternalService().updateForWorkgroupChange(groupId, memberPrincipalsBefore,
               memberPrincipalsAfter);
    }

    @Override
    public boolean removeGroupFromGroup(String childId, String parentId) throws RiceIllegalArgumentException {
    	incomingParamCheck(childId, "childId");
        incomingParamCheck(parentId, "parentId");

        java.sql.Timestamp today = new java.sql.Timestamp(System.currentTimeMillis());

    	List<GroupMemberBo> groupMembers =
    		getActiveGroupMembers(parentId, childId, KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE);

        if(groupMembers.size() == 1) {
        	GroupMemberBo groupMember = groupMembers.get(0);
        	groupMember.setActiveToDateValue(today);
            this.dataObjectService.save(groupMember);
            return true;
        }

        return false;
    }

    @Override
    public boolean removePrincipalFromGroup(String principalId, String groupId) throws RiceIllegalArgumentException {
    	incomingParamCheck(principalId, "principalId");
        incomingParamCheck(groupId, "groupId");

        List<GroupMemberBo> groupMembers =
    		getActiveGroupMembers(groupId, principalId, KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);

        if(groupMembers.size() == 1) {
        	GroupMemberBo member = groupMembers.iterator().next();
        	member.setActiveToDateValue(new java.sql.Timestamp(DateTime.now().getMillis()));
        	this.dataObjectService.save(member);
            KimImplServiceLocator.getGroupInternalService().updateForUserRemovedFromGroup(member.getMemberId(),
                    member.getGroupId());
            return true;
        }

        return false;
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
                            member.setActiveToDateValue(activeTo);
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

		return KimImplServiceLocator.getGroupInternalService().saveWorkgroup(group);
	}


	/**
	 * This helper method gets the active group members of the specified type (see {@link org.kuali.rice.kim.api.KimConstants.KimGroupMemberTypes}).
	 * If the optional params are null, it will return all active members for the specified group regardless
	 * of type.
	 *
	 * @param parentId
	 * @param childId optional, but if provided then memberType must be too
	 * @param memberType optional, but must be provided if childId is
     * @return a list of group members
	 */
	private List<GroupMemberBo> getActiveGroupMembers(String parentId, String childId, MemberType memberType) {
    	final java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

    	if (childId != null && memberType == null) {
            throw new RiceRuntimeException("memberType must be non-null if childId is non-null");
        }

        final QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();

        builder.setPredicates(
                and(
                        equal(KIMPropertyConstants.GroupMember.GROUP_ID, parentId),
                        HistoryQueryUtils.between(KIMPropertyConstants.KimMember.ACTIVE_FROM_DATE_VALUE,
                                KIMPropertyConstants.KimMember.ACTIVE_TO_DATE_VALUE, DateTime.now())));

        List<Predicate> optionalPredicates = new ArrayList<Predicate>();
        if (StringUtils.isNotEmpty(childId)) {
            optionalPredicates.add(equal(KIMPropertyConstants.GroupMember.MEMBER_ID, childId));
        }
        if (memberType != null) {
            optionalPredicates.add(equal(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, memberType.getCode()));
        }
        if (CollectionUtils.isNotEmpty(optionalPredicates)) {
            optionalPredicates.addAll(Arrays.asList(builder.getPredicates()));
            builder.setPredicates(
                    and(optionalPredicates.toArray(new Predicate[optionalPredicates.size()])));
        }

        QueryResults<GroupMemberBo> groupMembers = this.dataObjectService.findMatching(GroupMemberBo.class, builder.build());

        /*CollectionUtils.filter(groupMembers, new Predicate() {
			@Override public boolean evaluate(Object object) {
				GroupMemberBo member = (GroupMemberBo) object;
				// keep in the collection (return true) if the activeToDate is null, or if it is set to a future date
				return member.getActiveToDate() == null || today.before(member.getActiveToDate().toDate());
			}
		});*/

        return new ArrayList<GroupMemberBo>(groupMembers.getResults());
	}


    public void setDataObjectService(final DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    private void incomingParamCheck(Object object, String name) {
        if (object == null) {
            throw new RiceIllegalArgumentException(name + " was null");
        } else if (object instanceof String
                && StringUtils.isBlank((String) object)) {
            throw new RiceIllegalArgumentException(name + " was blank");
        }
    }

    /**
     * Returns the list of group members who are currently active and futureActive .
     * @param groupId
     * @return
     */
    public List<GroupMember> getCurrentAndFutureMembers(String groupId){
        List<GroupMemberBo> groupMembersBos = getActiveGroupMembers(groupId, null, null);
        List<GroupMember> groupMembers = new ArrayList<GroupMember>();
        for (GroupMemberBo groupBo : groupMembersBos) {
            groupMembers.add(GroupMemberBo.to(groupBo));
        }
        return Collections.unmodifiableList(groupMembers);
    }
}
