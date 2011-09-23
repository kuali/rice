package org.kuali.rice.kim.impl.role;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.responsibility.ResponsibilityQueryResults;
import org.kuali.rice.kim.api.responsibility.ResponsibilityService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kim.impl.common.delegate.DelegateTypeBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

public class RoleDaoOjb extends PlatformAwareDaoBaseOjb implements RoleDao {
    /**
     * Adds SubCriteria to the Query Criteria using the role qualification passed in
     *
     * @param c             The Query Criteria object to be used
     * @param qualification The role qualification
     */
    private void addSubCriteriaBasedOnRoleQualification(Criteria c, Map<String, String> qualification) {
        if (qualification != null && CollectionUtils.isNotEmpty(qualification.keySet())) {
            for (Map.Entry<String, String> qualifier : qualification.entrySet()) {
                Criteria subCrit = new Criteria();
                if (StringUtils.isNotEmpty(qualifier.getValue())) {
                    String value = (qualifier.getValue()).replace('*', '%');
                    subCrit.addLike("attributeValue", value);
                    subCrit.addEqualTo("kimAttributeId", qualifier.getKey());
					subCrit.addEqualToField("roleMemberId", Criteria.PARENT_QUERY_PREFIX + "roleMemberId"); 
                    ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(RoleMemberAttributeDataBo.class, subCrit);
                    c.addExists(subQuery);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<RoleMemberBo> getRolePrincipalsForPrincipalIdAndRoleIds(Collection<String> roleIds, String principalId, Map<String, String> qualification) {

        Criteria c = new Criteria();

        if (roleIds != null) {
            c.addIn(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
        }
        if (principalId != null) {
            c.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_ID, principalId);
        }
        c.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE);
        addSubCriteriaBasedOnRoleQualification(c, qualification);

        Query query = QueryFactory.newQuery(RoleMemberBo.class, c);
        Collection<RoleMemberBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        ArrayList<RoleMemberBo> results = new ArrayList<RoleMemberBo>(coll.size());
        for (RoleMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

    public List<GroupMember> getGroupPrincipalsForPrincipalIdAndGroupIds(Collection<String> groupIds, String principalId) {
        List<String> groupIdValues = new ArrayList<String>();
        List<GroupMember> groupPrincipals = new ArrayList<GroupMember>();
        if (groupIds != null
                && principalId == null) {
            groupIdValues = new ArrayList<String>(groupIds);
        } else if (principalId != null) {
            groupIdValues = KimApiServiceLocator.getGroupService().getGroupIdsByPrincipalId(principalId);
        }
        if (groupIdValues != null
                && groupIdValues.size() > 0) {
            Collection<GroupMember> groupMembers = KimApiServiceLocator.getGroupService().getMembers(groupIdValues);
            for (GroupMember groupMembershipInfo : groupMembers) {
                if (principalId != null) {
                    if (StringUtils.equals(groupMembershipInfo.getTypeCode(), Role.PRINCIPAL_MEMBER_TYPE)
                            && StringUtils.equals(principalId, groupMembershipInfo.getMemberId())
                            && groupMembershipInfo.isActive(new DateTime())) {
                        groupPrincipals.add(groupMembershipInfo);
                    }
                } else {
                    groupPrincipals.add(groupMembershipInfo);
                }
            }
        }
        return groupPrincipals;
    }

    public List<GroupMember> getGroupMembers(Collection<String> groupIds) {
        List<GroupMember> groupMembers = new ArrayList<GroupMember>();
        if (groupIds != null) {
            List<String> groupIdValues = new ArrayList<String>(groupIds);

            if (groupIdValues.size() > 0) {

                Collection<GroupMember> groupMemberships = KimApiServiceLocator.getGroupService().getMembers(groupIdValues);

                if (!CollectionUtils.isEmpty(groupMemberships)) {
                    for (GroupMember groupMembershipInfo : groupMemberships) {
                        if (StringUtils.equals(groupMembershipInfo.getTypeCode(), Role.GROUP_MEMBER_TYPE)
                                && groupMembershipInfo.isActive(new DateTime())) {
                            groupMembers.add(groupMembershipInfo);
                        }
                    }
                }
            }
        }
        return groupMembers;
    }

    @SuppressWarnings("unchecked")
    public List<RoleMemberBo> getRoleGroupsForGroupIdsAndRoleIds(Collection<String> roleIds, Collection<String> groupIds, Map<String, String> qualification) {
        Criteria c = new Criteria();
        if (roleIds != null && !roleIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
        }
        if (groupIds != null && !groupIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds);
        }
        c.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.GROUP_MEMBER_TYPE);
        addSubCriteriaBasedOnRoleQualification(c, qualification);

        Query query = QueryFactory.newQuery(RoleMemberBo.class, c);
        Collection<RoleMemberBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        ArrayList<RoleMemberBo> results = new ArrayList<RoleMemberBo>(coll.size());
        for (RoleMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public Map<String, DelegateTypeBo> getDelegationImplMapFromRoleIds(Collection<String> roleIds) {
        HashMap<String, DelegateTypeBo> results = new HashMap<String, DelegateTypeBo>();
        if (roleIds != null && !roleIds.isEmpty()) {
            Criteria c = new Criteria();
            c.addIn(KIMPropertyConstants.Delegation.ROLE_ID, roleIds);
            c.addEqualTo(KIMPropertyConstants.Delegation.ACTIVE, Boolean.TRUE);
            Query query = QueryFactory.newQuery(DelegateTypeBo.class, c);
            Collection<DelegateTypeBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            for (DelegateTypeBo delegateBo : coll) {
                results.put(delegateBo.getDelegationId(), delegateBo);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<DelegateTypeBo> getDelegationBosForRoleIds(Collection<String> roleIds) {
        List<DelegateTypeBo> results = new ArrayList<DelegateTypeBo>();
        if (roleIds != null && !roleIds.isEmpty()) {
            Criteria c = new Criteria();
            c.addIn(KIMPropertyConstants.Delegation.ROLE_ID, roleIds);
            c.addEqualTo(KIMPropertyConstants.Delegation.ACTIVE, Boolean.TRUE);
            Query query = QueryFactory.newQuery(DelegateTypeBo.class, c);
            Collection<DelegateTypeBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            for (DelegateTypeBo delegateBo : coll) {
                results.add(delegateBo);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<DelegateMemberBo> getDelegationPrincipalsForPrincipalIdAndDelegationIds(
            Collection<String> delegationIds, String principalId) {
        Criteria c = new Criteria();

        if (principalId != null) {
            c.addEqualTo(KIMPropertyConstants.DelegationMember.MEMBER_ID, principalId);
        }
        c.addEqualTo(KIMPropertyConstants.DelegationMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE);
        if (delegationIds != null && !delegationIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.DelegationMember.DELEGATION_ID, delegationIds);
        }
        Query query = QueryFactory.newQuery(DelegateMemberBo.class, c);
        Collection<DelegateMemberBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        ArrayList<DelegateMemberBo> results = new ArrayList<DelegateMemberBo>(coll.size());
        for (DelegateMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<DelegateMemberBo> getDelegationGroupsForGroupIdsAndDelegationIds(
            Collection<String> delegationIds, List<String> groupIds) {
        Criteria c = new Criteria();
        if (delegationIds != null && !delegationIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.DelegationMember.DELEGATION_ID, delegationIds);
        }
        if (groupIds != null && !groupIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.DelegationMember.MEMBER_ID, groupIds);
        }
        c.addEqualTo(KIMPropertyConstants.DelegationMember.MEMBER_TYPE_CODE, Role.GROUP_MEMBER_TYPE);
        Query query = QueryFactory.newQuery(DelegateMemberBo.class, c);
        Collection<DelegateMemberBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        ArrayList<DelegateMemberBo> results = new ArrayList<DelegateMemberBo>(coll.size());
        for (DelegateMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<RoleMemberBo> getRoleMembersForRoleIds(Collection<String> roleIds, String memberTypeCode, Map<String, String> qualification) {
        Criteria c = new Criteria();

        if (roleIds != null && !roleIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
        }
        if (memberTypeCode != null) {
            c.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, memberTypeCode);
        }
        addSubCriteriaBasedOnRoleQualification(c, qualification);

        Query query = QueryFactory.newQuery(RoleMemberBo.class, c);
        Collection<RoleMemberBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        ArrayList<RoleMemberBo> results = new ArrayList<RoleMemberBo>(coll.size());
        for (RoleMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<RoleMemberBo> getRoleMembershipsForRoleIdsAsMembers(Collection<String> roleIds, Map<String, String> qualification) {
        Criteria c = new Criteria();

        if (roleIds != null && !roleIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.RoleMember.MEMBER_ID, roleIds);
        }
        c.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);
        addSubCriteriaBasedOnRoleQualification(c, qualification);

        Query query = QueryFactory.newQuery(RoleMemberBo.class, c);
        Collection<RoleMemberBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        ArrayList<RoleMemberBo> results = new ArrayList<RoleMemberBo>(coll.size());
        for (RoleMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<RoleMemberBo> getRoleMembershipsForMemberId(String memberType, String memberId, Map<String, String> qualification) {
        Criteria c = new Criteria();
        List<RoleMemberBo> parentRoleMembers = new ArrayList<RoleMemberBo>();

        if (StringUtils.isEmpty(memberId)
                || StringUtils.isEmpty(memberType)) {
            return parentRoleMembers;
        }

        c.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_ID, memberId);
        c.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, memberType);
        addSubCriteriaBasedOnRoleQualification(c, qualification);

        Query query = QueryFactory.newQuery(RoleMemberBo.class, c);
        Collection<RoleMemberBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        ArrayList<RoleMemberBo> results = new ArrayList<RoleMemberBo>(coll.size());
        for (RoleMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<RoleMemberBo> getRoleMembersForRoleIdsWithFilters(Collection<String> roleIds, String principalId, Collection<String> groupIds, Map<String, String> qualification) {
        Criteria c = new Criteria();

        if (roleIds != null && !roleIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
        }
        Criteria orSet = new Criteria();
        orSet.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.ROLE_MEMBER_TYPE);
        Criteria principalCheck = new Criteria();
        if (principalId != null) {
            principalCheck.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_ID, principalId);
        }
        principalCheck.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE);
        orSet.addOrCriteria(principalCheck);
        Criteria groupCheck = new Criteria();
        if (groupIds != null && !groupIds.isEmpty()) {
            groupCheck.addIn(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds);
        }
        groupCheck.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.GROUP_MEMBER_TYPE);
        orSet.addOrCriteria(groupCheck);
        c.addAndCriteria(orSet);
        addSubCriteriaBasedOnRoleQualification(c, qualification);

        Query query = QueryFactory.newQuery(RoleMemberBo.class, c);
        Collection<RoleMemberBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        ArrayList<RoleMemberBo> results = new ArrayList<RoleMemberBo>(coll.size());
        for (RoleMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

    public List<RoleBo> getRoles(Map<String, String> fieldValues) {
        Criteria criteria = new Criteria();
        Map<String, Map<String, String>> criteriaMap = setupCritMaps(fieldValues);

//      List lookupNames = boEntry.getLookupDefinition().getLookupFieldNames();
        Map<String, String> lookupNames = criteriaMap.get("lookupNames");
        for (Map.Entry<String, String> entry : lookupNames.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue())) {
                if (!entry.getKey().equals(KIMPropertyConstants.Principal.PRINCIPAL_NAME)) {
                    addLikeToCriteria(criteria, entry.getKey(), entry.getValue());
                } else {
                    List<String> roleIds = getRoleIdsForPrincipalName(entry.getValue());
                    if (roleIds != null && !roleIds.isEmpty()) {
                        criteria.addIn(KimConstants.PrimaryKeyConstants.ID, roleIds);
                    } else {
                        // TODO : if no role id found that means principalname not matched, need to do something to force to return empty list
                        roleIds.add("NOTFOUND");
                        criteria.addIn(KimConstants.PrimaryKeyConstants.ID, roleIds);
                    }
                }
            }
        }
        if (!criteriaMap.get("attr").isEmpty()) {
            String kimTypeId = null;
            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                if (entry.getKey().equals("kimTypeId")) {
                    kimTypeId = entry.getValue();
                    break;
                }
            }
            setupAttrCriteria(criteria, criteriaMap.get("attr"), kimTypeId);
        }
        if (!criteriaMap.get("perm").isEmpty()) {
            criteria.addExists(setupPermCriteria(criteriaMap.get("perm")));
        }
        if (!criteriaMap.get("resp").isEmpty()) {
            criteria.addExists(setupRespCriteria(criteriaMap.get("resp")));
        }
        if (!criteriaMap.get("group").isEmpty()) {
            criteria.addExists(setupGroupCriteria(criteriaMap.get("group")));
        }

        Query q = QueryFactory.newQuery(RoleBo.class, criteria);

        return (List) getPersistenceBrokerTemplate().getCollectionByQuery(q);
    }


    private List<String> getPrincipalIdsForPrincipalName(String principalName) {
        QueryByCriteria.Builder qb = QueryByCriteria.Builder.create();
        qb.setPredicates(equal("principals.principalName", principalName));
        List<EntityDefault> entities = KimApiServiceLocator.getIdentityService().findEntityDefaults(qb.build()).getResults();

        List<String> principalIds = new ArrayList<String>();
        for (EntityDefault entity : entities) {
            for (Principal principal : entity.getPrincipals()) {
                principalIds.add(principal.getPrincipalId());
            }
        }

        return principalIds;

    }

    private List<String> getRoleIdsForPrincipalName(String value) {
        String principalName = value.replace('*', '%');
        List<String> roleIds = new ArrayList<String>();
        Criteria memberSubCrit = new Criteria();
        QueryByCriteria.Builder qb = QueryByCriteria.Builder.create();
        qb.setPredicates(equal("principals.principalName", principalName));
        List<EntityDefault> entities = KimApiServiceLocator.getIdentityService().findEntityDefaults(qb.build()).getResults();
        if (entities == null
                || entities.size() == 0) {
            return roleIds;
        }

        List<String> principalIds = new ArrayList<String>();
        for (EntityDefault entity : entities) {
            for (Principal principal : entity.getPrincipals()) {
                principalIds.add(principal.getPrincipalId());
            }
        }
        if (principalIds != null && !principalIds.isEmpty()) {
            memberSubCrit.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE);
            memberSubCrit.addIn(KIMPropertyConstants.RoleMember.MEMBER_ID, principalIds);

            ReportQueryByCriteria memberSubQuery = QueryFactory.newReportQuery(RoleMemberBo.class, memberSubCrit);
            for (RoleMemberBo roleMbr : (List<RoleMemberBo>) getPersistenceBrokerTemplate().getCollectionByQuery(memberSubQuery)) {
                if (roleMbr.isActive(new Timestamp(System.currentTimeMillis())) && !roleIds.contains(roleMbr.getRoleId())) {
                    roleIds.add(roleMbr.getRoleId());
                }
            }
        }

        List<String> groupIds = new ArrayList<String>();
        for (String principalId : principalIds) {
            List<String> principalGroupIds = KimApiServiceLocator.getGroupService().getGroupIdsByPrincipalId(
                    principalId);
            for (String groupId : principalGroupIds) {
                if (!groupIds.contains(groupId)) {
                    groupIds.add(groupId);
                }
            }
        }

        if (groupIds != null && !groupIds.isEmpty()) {
            Criteria grpRoleCrit = new Criteria();
            grpRoleCrit.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.GROUP_MEMBER_TYPE);
            grpRoleCrit.addIn(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds);

            ReportQueryByCriteria memberSubQuery = QueryFactory.newReportQuery(RoleMemberBo.class, grpRoleCrit);

            for (RoleMemberBo roleMbr : (List<RoleMemberBo>) getPersistenceBrokerTemplate().getCollectionByQuery(memberSubQuery)) {
                if (roleMbr.isActive(new Timestamp(System.currentTimeMillis())) && !roleIds.contains(roleMbr.getRoleId())) {
                    roleIds.add(roleMbr.getRoleId());
                }
            }
        }

        return roleIds;
    }

    private Map<String, Map<String, String>> setupCritMaps(Map<String, String> fieldValues) {

        Map<String, Map<String, String>> critMap = new HashMap<String, Map<String, String>>();
        List<String> permFieldName = new ArrayList<String>();
        permFieldName.add("permName");
        permFieldName.add("permNamespaceCode");
        permFieldName.add("permTmplName");
        permFieldName.add("permTmplNamespaceCode");
        List<String> respFieldName = new ArrayList<String>();
        respFieldName.add("respName");
        respFieldName.add("respNamespaceCode");
        respFieldName.add("respTmplName");
        respFieldName.add("respTmplNamespaceCode");
        Map<String, String> permFieldMap = new HashMap<String, String>();
        Map<String, String> respFieldMap = new HashMap<String, String>();
        Map<String, String> attrFieldMap = new HashMap<String, String>();
        Map<String, String> groupFieldMap = new HashMap<String, String>();
        Map<String, String> lookupNamesMap = new HashMap<String, String>();

        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue())) {
                String nameValue = entry.getValue().replace('*', '%');
                if (permFieldName.contains(entry.getKey())) {
                    permFieldMap.put(entry.getKey(), nameValue);
                } else if (respFieldName.contains(entry.getKey())) {
                    respFieldMap.put(entry.getKey(), nameValue);
                } else if (entry.getKey().startsWith(KimConstants.AttributeConstants.GROUP_NAME)) {
                    groupFieldMap.put(entry.getKey(), nameValue);
                } else if (entry.getKey().contains(".")) {
                    attrFieldMap.put(entry.getKey(), nameValue);
                } else {
                    lookupNamesMap.put(entry.getKey(), nameValue);
                }
            }
        }

        critMap.put("perm", permFieldMap);
        critMap.put("resp", respFieldMap);
        critMap.put("group", groupFieldMap);
        critMap.put("attr", attrFieldMap);
        critMap.put("lookupNames", lookupNamesMap);
        return critMap;
    }


    private void setupAttrCriteria(Criteria crit, Map<String, String> attrCrit, String kimTypeId) {
        for (Map.Entry<String, String> entry : attrCrit.entrySet()) {
            Criteria subCrit = new Criteria();
            addLikeToCriteria(subCrit, "attributes.attributeValue", entry.getValue());
            addEqualToCriteria(subCrit, "attributes.kimAttributeId", entry.getKey().substring(entry.getKey().indexOf(".") + 1, entry.getKey().length()));
            addEqualToCriteria(subCrit, "attributes.kimTypeId", kimTypeId);
            subCrit.addEqualToField("roleId", Criteria.PARENT_QUERY_PREFIX + "roleId");
            crit.addExists(QueryFactory.newReportQuery(RoleMemberBo.class, subCrit));
        }
    }

    private ReportQueryByCriteria setupPermCriteria(Map<String, String> permCrit) {

        List<Predicate> andConds = new ArrayList<Predicate>();

        for (Map.Entry<String, String> entry : permCrit.entrySet()) {
            if (entry.getKey().equals("permTmplName") || entry.getKey().equals("permTmplNamespaceCode")) {
                if (entry.getKey().equals("permTmplName")) {
                    andConds.add(equal("template." + KimConstants.UniqueKeyConstants.PERMISSION_TEMPLATE_NAME, entry.getValue()));

                } else {
                     andConds.add(equal("template." + KimConstants.UniqueKeyConstants.NAMESPACE_CODE, entry.getValue()));
                }
            }

            if (entry.getKey().equals("permName") || entry.getKey().equals("permNamespaceCode")) {
                if (entry.getKey().equals("permName")) {
                     andConds.add(equal(KimConstants.UniqueKeyConstants.PERMISSION_NAME, entry.getValue()));
                } else {
                     andConds.add(equal(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, entry.getValue()));

                }
            }
        }


        List<Permission> permList = KimApiServiceLocator.getPermissionService().findPermissions(QueryByCriteria.Builder.fromPredicates(andConds.toArray(new Predicate[]{}))).getResults();
        List<String> roleIds = null;

        if (permList != null && !permList.isEmpty()) {
            roleIds = getRoleIdsForPermissions(permList);
        }

        if (roleIds == null || roleIds.isEmpty()) {
            roleIds = new ArrayList<String>();
            roleIds.add("-1"); // this forces a blank return.
        }

        Criteria memberSubCrit = new Criteria();
        memberSubCrit.addIn("roleId", roleIds);
        memberSubCrit.addEqualToField("roleId", Criteria.PARENT_QUERY_PREFIX + "roleId");
        return QueryFactory.newReportQuery(RoleBo.class, memberSubCrit);

    }

    private List<String> getRoleIdsForPermissions(Collection<Permission> permissions) {
		if ( permissions.isEmpty() ) {
			return new ArrayList<String>(0);
		}
		List<String> permissionIds = new ArrayList<String>( permissions.size() );
		for ( Permission permission : permissions ) {
			permissionIds.add( permission.getId() );
		}
		Criteria c = new Criteria();
		c.addIn( "permissionId", permissionIds );
		c.addEqualTo( "active", true );

		Query query = QueryFactory.newQuery( RolePermissionBo.class, c, true );
		Collection<RolePermissionBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		List<String> roleIds = new ArrayList<String>( coll.size() );
		for ( RolePermissionBo rp : coll ) {
			roleIds.add( rp.getRoleId() );
		}
		return roleIds;
	}

    private ReportQueryByCriteria setupRespCriteria(Map<String, String> respCrit) {
        QueryByCriteria.Builder queryByCriteriaBuilder = QueryByCriteria.Builder.create();
        List<Predicate> predicates = new ArrayList<Predicate>();
        for (Map.Entry<String, String> entry : respCrit.entrySet()) {
            if (entry.getKey().equals("respTmplName") || entry.getKey().equals("respTmplNamespaceCode")) {
                if (entry.getKey().equals("respTmplName")) {
                    predicates.add(PredicateFactory.equal("template" + KimConstants.UniqueKeyConstants.RESPONSIBILITY_TEMPLATE_NAME, entry.getValue()));
                } else {
                    predicates.add(PredicateFactory.equal("template." + KimConstants.UniqueKeyConstants.NAMESPACE_CODE, entry.getValue()));
                }
            }
            if (entry.getKey().equals("respName") || entry.getKey().equals("respNamespaceCode")) {
                if (entry.getKey().equals("respName")) {
                    predicates.add(PredicateFactory.equal(KimConstants.UniqueKeyConstants.RESPONSIBILITY_NAME, entry.getValue()));
                } else {
                    predicates.add(PredicateFactory.equal(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, entry.getValue()));
                }
            }
        }
        queryByCriteriaBuilder.setPredicates(PredicateFactory.and(predicates.toArray(new Predicate[predicates.size()])));

        ResponsibilityService responsibilityService = KimApiServiceLocator.getResponsibilityService();
        ResponsibilityQueryResults results = responsibilityService.findResponsibilities(queryByCriteriaBuilder.build());
        List<Responsibility> responsibilities = results.getResults();


        List<String> roleIds = new ArrayList<String>();
        for (Responsibility responsibility : responsibilities) {
            roleIds.addAll(responsibilityService.getRoleIdsForResponsibility(responsibility.getId(), null));
        }

        if (roleIds.isEmpty()) {
            roleIds.add("-1"); // this forces a blank return.
        }

        Criteria memberSubCrit = new Criteria();
        memberSubCrit.addIn("roleId", roleIds);
        memberSubCrit.addEqualToField("roleId", Criteria.PARENT_QUERY_PREFIX + "roleId");
        return QueryFactory.newReportQuery(RoleBo.class, memberSubCrit);

    }

    private ReportQueryByCriteria setupGroupCriteria(Map<String,String> groupCrit) {

       //Map<String,String> searchCrit = new HashMap<String, String>();
       final QueryByCriteria.Builder searchCrit = QueryByCriteria.Builder.create();
       for (Entry<String, String> entry : groupCrit.entrySet()) {
                       if (entry.getKey().equals(KimConstants.AttributeConstants.GROUP_NAME)) {
                               //searchCrit.put(entry.getKey(), entry.getValue());
                   searchCrit.setPredicates(equal(entry.getKey(), entry.getValue()));
                       } else { // the namespace code for the group field is named something besides the default. Set it to the default.
                               //searchCrit.put(KimApiConstants.AttributeConstants.NAMESPACE_CODE, entry.getValue());
                   searchCrit.setPredicates(equal(KimConstants.AttributeConstants.NAMESPACE_CODE, entry.getValue()));

                       }
       }

       Criteria crit = new Criteria();

       List<String> groupIds = KimApiServiceLocator.getGroupService().findGroupIds(searchCrit.build());

       if(groupIds == null || groupIds.isEmpty()){
               groupIds = new ArrayList<String>();
               groupIds.add("-1");  // this forces a blank return.
       }
       crit.addIn("memberId", groupIds);
       crit.addEqualToField("roleId", Criteria.PARENT_QUERY_PREFIX + "roleId");

                return QueryFactory.newReportQuery(RoleMemberBo.class, crit);

   }

    private void addLikeToCriteria(Criteria criteria, String propertyName, String propertyValue) {
        String[] keyValues = getCaseInsensitiveValues(propertyName, propertyValue);
        criteria.addLike(keyValues[0], keyValues[1]);
    }

    private void addEqualToCriteria(Criteria criteria, String propertyName, String propertyValue) {
        String[] keyValues = getCaseInsensitiveValues(propertyName, propertyValue);
        criteria.addEqualTo(keyValues[0], keyValues[1]);
    }

    private String[] getCaseInsensitiveValues(String propertyName, String propertyValue) {
        String[] keyValues = new String[2];
        keyValues[0] = propertyName == null ? "" : getDbPlatform().getUpperCaseFunction() + "(" + propertyName + ")";
        keyValues[1] = propertyValue == null ? "" : propertyValue.toUpperCase();
        return keyValues;
    }

    private boolean hasCoreRoleMemberCriteria(Map<String, String> fieldValues) {
        return StringUtils.isNotEmpty(fieldValues.get(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID)) ||
                StringUtils.isNotEmpty(fieldValues.get(KimConstants.PrimaryKeyConstants.SUB_ROLE_ID)) ||
                StringUtils.isNotEmpty(fieldValues.get(KimConstants.PrimaryKeyConstants.MEMBER_ID)) ||
                StringUtils.isNotEmpty(fieldValues.get(KIMPropertyConstants.KimMember.MEMBER_TYPE_CODE)) ||
                StringUtils.isNotEmpty(fieldValues.get(KIMPropertyConstants.KimMember.ACTIVE_FROM_DATE)) ||
                StringUtils.isNotEmpty(fieldValues.get(KIMPropertyConstants.KimMember.ACTIVE_TO_DATE));
    }

    private boolean hasExtraRoleMemberCriteria(Map<String, String> fieldValues) {
        return StringUtils.isNotEmpty(fieldValues.get(KimConstants.KimUIConstants.MEMBER_NAME)) ||
                StringUtils.isNotEmpty(fieldValues.get(KimConstants.KimUIConstants.MEMBER_NAMESPACE_CODE));
    }

    @SuppressWarnings("unchecked")
    private List<RoleBo> getRoleMembersRoles(String memberNamespaceCode, String memberName) {
        Criteria queryCriteria = new Criteria();
        addEqualToCriteria(queryCriteria, KimConstants.UniqueKeyConstants.NAMESPACE_CODE, memberNamespaceCode);
        addEqualToCriteria(queryCriteria, KimConstants.UniqueKeyConstants.NAME, memberName);
        Query q = QueryFactory.newQuery(RoleBo.class, queryCriteria);
        return (List<RoleBo>) getPersistenceBrokerTemplate().getCollectionByQuery(q);
    }

    private List<String> getRoleMembersGroupIds(String memberNamespaceCode, String memberName){
       QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
       builder.setPredicates(and(
                   like(KimConstants.AttributeConstants.GROUP_NAME, memberName),
                   like(KimConstants.AttributeConstants.NAMESPACE_CODE, memberNamespaceCode)));
        return KimApiServiceLocator.getGroupService().findGroupIds(builder.build());
   }
}
