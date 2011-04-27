/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.framework.persistence.jpa.criteria.Criteria;
import org.kuali.rice.core.framework.persistence.jpa.criteria.QueryByCriteria;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.dao.KimRoleDao;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimConstants;

/**
 * This is a description of what this class does - jonathan don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimRoleDaoJpa implements KimRoleDao {

    @PersistenceContext(unitName="kim-unit")
    private EntityManager entityManager;
    
	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRolePrincipalsForPrincipalIdAndRoleIds(java.util.Collection,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRolePrincipalsForPrincipalIdAndRoleIds( Collection<String> roleIds, String principalId) {

		Criteria c = new Criteria(RoleMemberImpl.class.getName());

		if ( roleIds != null ) {
			c.in(KIMPropertyConstants.RoleMember.ROLE_ID, new ArrayList(roleIds));
		}
		if(principalId!=null){
			c.eq(KIMPropertyConstants.RoleMember.MEMBER_ID, principalId);
		}
		c.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE );
		ArrayList<RoleMemberImpl> coll = (ArrayList<RoleMemberImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRolePrincipalsForPrincipalIdAndRoleIds(java.util.Collection,
	 *      java.lang.String)
	 */
	public List<GroupMember> getGroupPrincipalsForPrincipalIdAndGroupIds( Collection<String> groupIds, String principalId) {
	    List<String> groupIdValues = new ArrayList<String>();
	    List<GroupMember> groupPrincipals = new ArrayList<GroupMember>();
	    if (groupIds != null
	            && principalId == null) {
	        groupIdValues = new ArrayList<String>(groupIds);
	    } else if (principalId != null) {
	        groupIdValues = KIMServiceLocator.getGroupService().getGroupIdsForPrincipal(principalId);
	    }
	    if (groupIdValues != null
	            && groupIdValues.size() > 0) {
    	    Collection<GroupMember> groupMembershipInfos = KIMServiceLocator.getGroupService().getMembers(groupIdValues);
            for (GroupMember groupMembershipInfo : groupMembershipInfos) {
                if (principalId != null) {
                    if (StringUtils.equals(groupMembershipInfo.getTypeCode(), Role.PRINCIPAL_MEMBER_TYPE)
                            && StringUtils.equals(principalId, groupMembershipInfo.getMemberId())
                            && groupMembershipInfo.isActive()) {
                        groupPrincipals.add(groupMembershipInfo);
                    }
                } else {
                    groupPrincipals.add(groupMembershipInfo);
                }
            }
	    }
	    return groupPrincipals;
	}

	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRolePrincipalsForPrincipalIdAndRoleIds(java.util.Collection,
	 *      java.lang.String)
	 */
	public List<GroupMember> getGroupMembers(Collection<String> groupIds) {
	    List<String> groupIdValues = new ArrayList<String>();
	    List<GroupMember> groupMembers = new ArrayList<GroupMember>();
	    if (groupIds != null) {
	        groupIdValues = new ArrayList<String>(groupIds);

	        if (groupIdValues != null
	                && groupIdValues.size() > 0) {
                Collection<GroupMember> groupMembershipInfos = KIMServiceLocator.getGroupService().getMembers(groupIdValues);
	            //Collection<GroupMember> groupMembershipInfos = KIMServiceLocator.getGroupService().g.getGroupMembers(groupIdValues);

                if (!CollectionUtils.isEmpty(groupMembershipInfos)) {
                    for (GroupMember groupMembershipInfo : groupMembershipInfos) {
                        if (StringUtils.equals(groupMembershipInfo.getTypeCode(), Role.GROUP_MEMBER_TYPE)
                                && groupMembershipInfo.isActive()) {
                            groupMembers.add(groupMembershipInfo);
                        }
                    }
	            }
	        }
	    }
	    return groupMembers;
	}

	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleGroupsForGroupIdsAndRoleIds(java.util.Collection,
	 *      java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRoleGroupsForGroupIdsAndRoleIds( Collection<String> roleIds, Collection<String> groupIds ) {
		Criteria c = new Criteria(RoleMemberImpl.class.getName());
		if(roleIds!=null && !roleIds.isEmpty())
			c.in(KIMPropertyConstants.RoleMember.ROLE_ID, new ArrayList(roleIds));
		if(groupIds!=null && !groupIds.isEmpty())
			c.in(KIMPropertyConstants.RoleMember.MEMBER_ID, new ArrayList(groupIds));
		c.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.GROUP_MEMBER_TYPE );

		ArrayList<RoleMemberImpl> coll = (ArrayList<RoleMemberImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public Map<String,KimDelegationImpl> getDelegationImplMapFromRoleIds(Collection<String> roleIds) {
		HashMap<String,KimDelegationImpl> results = new HashMap<String, KimDelegationImpl>();
		if ( roleIds != null && !roleIds.isEmpty() ) {
			Criteria c = new Criteria(KimDelegationImpl.class.getName());
			c.in(KIMPropertyConstants.Delegation.ROLE_ID, new ArrayList(roleIds));
			c.eq(KIMPropertyConstants.Delegation.ACTIVE, Boolean.TRUE);

			ArrayList<KimDelegationImpl> coll = (ArrayList<KimDelegationImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
			for ( KimDelegationImpl dele : coll ) {
				results.put( dele.getDelegationId(), dele);
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<KimDelegationImpl> getDelegationImplsForRoleIds(Collection<String> roleIds) {
		List<KimDelegationImpl> results = new ArrayList<KimDelegationImpl>();
		if ( roleIds != null && !roleIds.isEmpty() ) {
			Criteria c = new Criteria(KimDelegationImpl.class.getName());
			c.in(KIMPropertyConstants.Delegation.ROLE_ID, new ArrayList(roleIds));
			c.eq(KIMPropertyConstants.Delegation.ACTIVE, Boolean.TRUE);
			
			ArrayList<KimDelegationImpl> coll = (ArrayList<KimDelegationImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
			for ( KimDelegationImpl dele : coll ) {
				results.add(dele);
			}
		}
		return results;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getDelegationPrincipalsForPrincipalIdAndDelegationIds(java.util.Collection,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<KimDelegationMemberImpl> getDelegationPrincipalsForPrincipalIdAndDelegationIds(
			Collection<String> delegationIds, String principalId) {
		Criteria c = new Criteria(KimDelegationMemberImpl.class.getName());

		if(principalId!=null) {
			c.eq(KIMPropertyConstants.DelegationMember.MEMBER_ID, principalId);
		}
		c.eq( KIMPropertyConstants.DelegationMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE );
		if(delegationIds!=null && !delegationIds.isEmpty()) {
			c.in(KIMPropertyConstants.DelegationMember.DELEGATION_ID, new ArrayList(delegationIds));
		}
		ArrayList<KimDelegationMemberImpl> coll = (ArrayList<KimDelegationMemberImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		ArrayList<KimDelegationMemberImpl> results = new ArrayList<KimDelegationMemberImpl>( coll.size() );
		for ( KimDelegationMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getDelegationGroupsForGroupIdsAndDelegationIds(java.util.Collection,
	 *      java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public List<KimDelegationMemberImpl> getDelegationGroupsForGroupIdsAndDelegationIds(
			Collection<String> delegationIds, List<String> groupIds) {
		Criteria c = new Criteria(KimDelegationMemberImpl.class.getName());
		if(delegationIds!=null && !delegationIds.isEmpty()) {
			c.in(KIMPropertyConstants.DelegationMember.DELEGATION_ID, new ArrayList(delegationIds));
		}
		if(groupIds!=null && !groupIds.isEmpty()) {
			c.in(KIMPropertyConstants.DelegationMember.MEMBER_ID, groupIds);
		}
		c.eq( KIMPropertyConstants.DelegationMember.MEMBER_TYPE_CODE, Role.GROUP_MEMBER_TYPE );
		
		ArrayList<KimDelegationMemberImpl> coll = (ArrayList<KimDelegationMemberImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		ArrayList<KimDelegationMemberImpl> results = new ArrayList<KimDelegationMemberImpl>( coll.size() );
		for ( KimDelegationMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleMembersForRoleIds(Collection, String)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRoleMembersForRoleIds( Collection<String> roleIds, String memberTypeCode ) {
		Criteria c = new Criteria(RoleMemberImpl.class.getName());

		if(roleIds!=null && !roleIds.isEmpty()) {
			c.in(KIMPropertyConstants.RoleMember.ROLE_ID, new ArrayList(roleIds));
		}
		if ( memberTypeCode != null ) {
			c.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, memberTypeCode );
		}
		ArrayList<RoleMemberImpl> coll = (ArrayList<RoleMemberImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleMembersForRoleIds(Collection, String)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRoleMembershipsForRoleIdsAsMembers( Collection<String> roleIds) {
		Criteria c = new Criteria(RoleMemberImpl.class.getName());

		if(roleIds!=null && !roleIds.isEmpty()) {
			c.in(KIMPropertyConstants.RoleMember.MEMBER_ID, new ArrayList(roleIds));
		}
		c.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);

		ArrayList<RoleMemberImpl> coll = (ArrayList<RoleMemberImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRoleMembersForRoleIdsWithFilters( Collection<String> roleIds, String principalId, List<String> groupIds ) {
		Criteria c = new Criteria(RoleMemberImpl.class.getName());

		if(roleIds!=null && !roleIds.isEmpty()) {
			c.in(KIMPropertyConstants.RoleMember.ROLE_ID, new ArrayList(roleIds));
		}
		Criteria orSet = new Criteria(RoleMemberImpl.class.getName(), c.getAlias());
		orSet.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.ROLE_MEMBER_TYPE );
		Criteria principalCheck = new Criteria(RoleMemberImpl.class.getName(), c.getAlias());
		if(principalId!=null) {
			principalCheck.eq(KIMPropertyConstants.RoleMember.MEMBER_ID, principalId);
		}
		principalCheck.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE );
		orSet.or( principalCheck );
		Criteria groupCheck = new Criteria(RoleMemberImpl.class.getName(), c.getAlias());
		if(groupIds!=null && !groupIds.isEmpty()) {
			groupCheck.in(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds);
		}
		groupCheck.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.GROUP_MEMBER_TYPE );
		orSet.or( groupCheck );
		c.and( orSet );

		ArrayList<RoleMemberImpl> coll = (ArrayList<RoleMemberImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getDelegationMembersForDelegationIds(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public Map<String,List<KimDelegationMemberImpl>> getDelegationMembersForDelegationIds(
			List<String> delegationIds) {
		Criteria c = new Criteria(KimDelegationMemberImpl.class.getName());
		if(delegationIds!=null && !delegationIds.isEmpty()) {
			c.in(KIMPropertyConstants.DelegationMember.DELEGATION_ID, delegationIds);
		}
		
		ArrayList<KimDelegationMemberImpl> coll = (ArrayList<KimDelegationMemberImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		HashMap<String,List<KimDelegationMemberImpl>> result = new HashMap<String,List<KimDelegationMemberImpl>>();
		for ( KimDelegationMemberImpl dp : coll ) {
			if ( dp.isActive() ) {
				if ( !result.containsKey( dp.getDelegationId() ) ) {
					result.put( dp.getDelegationId(), new ArrayList<KimDelegationMemberImpl>() );
				}
				result.get( dp.getDelegationId() ).add( dp );
			}
		}
		return result;
	}

    public List<RoleImpl> getRoles(Map<String,String> fieldValues) {
        Criteria crit = new Criteria(RoleImpl.class.getName());
        Map<String,Map<String,String>> critMap = setupCritMaps(fieldValues);

//        BusinessObjectEntry boEntry = KNSServiceLocatorInternal.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry("org.kuali.rice.kim.bo.impl.RoleImpl");
//      List lookupNames = boEntry.getLookupDefinition().getLookupFieldNames();
        Map<String,String> lookupNames = critMap.get("lookupNames");
        for (Entry<String, String> entry : lookupNames.entrySet()) {
        	if (StringUtils.isNotBlank(entry.getValue())) {
        			if (!entry.getKey().equals(KIMPropertyConstants.Principal.PRINCIPAL_NAME)) {
        				addLikeToCriteria(crit, entry.getKey(), entry.getValue());
        			} else {
        					List roleIds = getRoleIdsForPrincipalName(entry.getValue());
                			if (roleIds!=null && !roleIds.isEmpty()) {
                				crit.in(KIMPropertyConstants.Role.ROLE_ID, roleIds);
                			} else {
                			// TODO : if no role id found that means principalname not matched, need to do something to force to return empty list
                				roleIds.add("NOTFOUND");
                				crit.in(KIMPropertyConstants.Role.ROLE_ID, roleIds);
                			}
        		}
        	}
        }
		if (!critMap.get("attr").isEmpty()) {
	    	String kimTypeId=null;
	        for (Map.Entry<String,String> entry : fieldValues.entrySet()) {
	        	if (entry.getKey().equals("kimTypeId")) {
	        		kimTypeId=entry.getValue();
	        		break;
	        	}
	        }
			setupAttrCriteria(crit, critMap.get("attr"),kimTypeId);
		}
		if (!critMap.get("perm").isEmpty()) {
			crit.exists(setupPermCriteria(critMap.get("perm"), crit));
		}
		if (!critMap.get("resp").isEmpty()) {
			crit.exists(setupRespCriteria(critMap.get("resp"), crit));
		}
		if (!critMap.get("group").isEmpty()) {
			crit.exists(setupGroupCriteria(critMap.get("group"), crit));
		}

        return (ArrayList<RoleImpl>) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    }

    private List<String> getPrincipalIdsForPrincipalName(String principalName){
    	Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("principals.principalName", principalName);
        List<? extends KimEntityDefaultInfo> entities = KIMServiceLocator.getIdentityService().lookupEntityDefaultInfo(criteria, false);

        List<String> principalIds = new ArrayList<String>();
        for (KimEntityDefaultInfo entity : entities) {
            for (KimPrincipal principal : entity.getPrincipals()) {
                principalIds.add(principal.getPrincipalId());
            }
        }

        return principalIds;

    }

    private List<String> getRoleIdsForPrincipalName(String value) {
		String principalName = value.replace('*', '%');
		List<String> roleIds = new ArrayList<String>();
		Criteria memberSubCrit = new Criteria(RoleMemberImpl.class.getName());
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("principals.principalName", principalName);
        List<? extends KimEntityDefaultInfo> entities = KIMServiceLocator.getIdentityService().lookupEntityDefaultInfo(criteria, false);
        if (entities == null
                || entities.size() == 0) {
            return roleIds;
        }

        List<String> principalIds = new ArrayList<String>();
        for (KimEntityDefaultInfo entity : entities) {
            for (KimPrincipal principal : entity.getPrincipals()) {
                principalIds.add(principal.getPrincipalId());
            }
        }
        if(principalIds!=null && !principalIds.isEmpty()){
        	memberSubCrit.eq(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE);
        	memberSubCrit.in(KIMPropertyConstants.RoleMember.MEMBER_ID, principalIds);

			ArrayList<RoleMemberImpl> coll = (ArrayList<RoleMemberImpl>) new QueryByCriteria(entityManager, memberSubCrit).toQuery().getResultList();
			for (RoleMemberImpl roleMbr : coll) {
				if (!roleIds.contains(roleMbr.getRoleId())) {
					roleIds.add(roleMbr.getRoleId());
				}
			}
        }

		List<String> groupIds = new ArrayList<String>();
		for (String principalId : principalIds) {
		    List<String> principalGroupIds = KIMServiceLocator.getGroupService().getGroupIdsForPrincipal(principalId);
		    for (String groupId : principalGroupIds) {
		        if (!groupIds.contains(groupId)) {
		            groupIds.add(groupId);
		        }
		    }
		}

        if(groupIds!=null && !groupIds.isEmpty()){
        	Criteria grpRoleCrit = new Criteria(RoleMemberImpl.class.getName());
        	grpRoleCrit.eq(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.GROUP_MEMBER_TYPE);
        	grpRoleCrit.in(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds);

        	ArrayList<RoleMemberImpl> roleMbrs = (ArrayList<RoleMemberImpl>) new QueryByCriteria(entityManager, grpRoleCrit).toQuery().getResultList();
			for (RoleMemberImpl roleMbr : roleMbrs) {
				if (!roleIds.contains(roleMbr.getRoleId())) {
					roleIds.add(roleMbr.getRoleId());
				}
			}
        }

    	return roleIds;
    }

    private Map setupCritMaps(Map<String,String> fieldValues) {
    	Map <String, Map> critMap = new HashMap();
        List permFieldName = new ArrayList();
        permFieldName.add("permName");
        permFieldName.add("permNamespaceCode");
        permFieldName.add("permTmplName");
        permFieldName.add("permTmplNamespaceCode");
        List respFieldName = new ArrayList();
        respFieldName.add("respName");
        respFieldName.add("respNamespaceCode");
        respFieldName.add("respTmplName");
        respFieldName.add("respTmplNamespaceCode");
        Map<String,String> permFieldMap = new HashMap<String,String>();
        Map<String,String> respFieldMap = new HashMap<String,String>();
        Map<String,String> attrFieldMap = new HashMap<String,String>();
        Map<String,String> groupFieldMap = new HashMap<String,String>();
        Map<String,String> lookupNamesMap = new HashMap<String,String>();

        for (Entry<String, String> entry : fieldValues.entrySet()) {
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


    private void setupAttrCriteria(Criteria crit, Map<String,String> attrCrit, String kimTypeId) {
        for (Entry<String, String> entry : attrCrit.entrySet()) {
			Criteria subCrit = new Criteria(RoleMemberImpl.class.getName());
			addLikeToCriteria(subCrit, "attributes.attributeValue",entry.getValue());
			addEqualToCriteria(subCrit, "attributes.kimAttributeId",entry.getKey().substring(entry.getKey().indexOf(".")+1, entry.getKey().length()));
			addEqualToCriteria(subCrit, "attributes.kimTypeId", kimTypeId);
			subCrit.eq("roleId", crit.getAlias() + "roleId");
			crit.exists(subCrit);
        }
    }

    private Criteria setupPermCriteria(Map<String,String> permCrit, Criteria parentCriteria) {

    	Map<String,String> searchCrit = new HashMap<String, String>();

        for (Entry<String, String> entry : permCrit.entrySet()) {
        	if (entry.getKey().equals("permTmplName") || entry.getKey().equals("permTmplNamespaceCode")) {
        		if (entry.getKey().equals("permTmplName")) {
        			searchCrit.put("template." + KimConstants.UniqueKeyConstants.PERMISSION_TEMPLATE_NAME,entry.getValue());

        		} else {
        			searchCrit.put("template." + KimConstants.UniqueKeyConstants.NAMESPACE_CODE,entry.getValue());
        		}
        	}

        	if (entry.getKey().equals("permName") || entry.getKey().equals("permNamespaceCode")) {
        		if (entry.getKey().equals("permName")) {
        			searchCrit.put(KimConstants.UniqueKeyConstants.PERMISSION_NAME, entry.getValue());
        		} else {
        			searchCrit.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE,entry.getValue());

        		}
        	}
        }

        List<KimPermissionInfo> permList = KIMServiceLocator.getPermissionService().lookupPermissions(searchCrit, true);
        List<String> roleIds = null;

        if(permList != null && !permList.isEmpty()){
        	roleIds = KIMServiceLocator.getPermissionService().getRoleIdsForPermissions(permList);
        }

        if(roleIds == null || roleIds.isEmpty()){
        	roleIds = new ArrayList<String>();
        	roleIds.add("-1"); // this forces a blank return.
        }

        Criteria memberSubCrit = new Criteria(RoleImpl.class.getName());
        memberSubCrit.in("roleId", roleIds);
        memberSubCrit.eq("roleId", parentCriteria.getAlias() + "roleId");
		return memberSubCrit;

    }

    private Criteria setupRespCriteria(Map<String,String> respCrit, Criteria parentCriteria) {

    	try{
    	//this.loadTestData();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	Map<String,String> searchCrit = new HashMap<String, String>();

        for (Entry<String, String> entry : respCrit.entrySet()) {
        	if (entry.getKey().equals("respTmplName") || entry.getKey().equals("respTmplNamespaceCode")) {
        		if (entry.getKey().equals("respTmplName")) {
        			searchCrit.put("template." + KimConstants.UniqueKeyConstants.RESPONSIBILITY_TEMPLATE_NAME,entry.getValue());

        		} else {
        			searchCrit.put("template." + KimConstants.UniqueKeyConstants.NAMESPACE_CODE,entry.getValue());
        		}
        	}

        	if (entry.getKey().equals("respName") || entry.getKey().equals("respNamespaceCode")) {
        		if (entry.getKey().equals("respName")) {
        			searchCrit.put(KimConstants.UniqueKeyConstants.RESPONSIBILITY_NAME, entry.getValue());
        		} else {
        			searchCrit.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE,entry.getValue());

        		}
        	}
        }

        List<? extends KimResponsibilityInfo> kriList = KIMServiceLocator.getResponsibilityService().lookupResponsibilityInfo(searchCrit, true);
        List<String> roleIds = new ArrayList<String>();

        for(KimResponsibilityInfo kri : kriList){
        	roleIds.addAll(KIMServiceLocator.getResponsibilityService().getRoleIdsForResponsibility(kri, null));
        }

        if(roleIds == null || roleIds.isEmpty()){
        	roleIds = new ArrayList<String>();
        	roleIds.add("-1"); // this forces a blank return.
        }

        Criteria memberSubCrit = new Criteria(RoleImpl.class.getName());
        memberSubCrit.in("roleId", roleIds);
        memberSubCrit.eq("roleId", parentCriteria.getAlias() + "roleId");
		return memberSubCrit;

    }

    private Criteria setupGroupCriteria(Map<String,String> groupCrit, Criteria parentCriteria) {

    	Map<String,String> searchCrit = new HashMap<String, String>();
        for (Entry<String, String> entry : groupCrit.entrySet()) {
        		if (entry.getKey().equals(KimConstants.AttributeConstants.GROUP_NAME)) {
        			searchCrit.put(entry.getKey(), entry.getValue());
        		} else { // the namespace code for the group field is named something besides the default. Set it to the default.
        			searchCrit.put(KimConstants.AttributeConstants.NAMESPACE_CODE, entry.getValue());
        		}
        }

        Criteria crit = new Criteria(RoleMemberImpl.class.getName());

        List<String> groupIds = KIMServiceLocator.getGroupService().lookupGroupIds(searchCrit);

        if(groupIds == null || groupIds.isEmpty()){
        	groupIds = new ArrayList<String>();
        	groupIds.add("-1");  // this forces a blank return.
        }
        crit.in("memberId", groupIds);
        crit.eq("roleId", parentCriteria.getAlias() + "roleId");

		return crit;

    }

    private void addLikeToCriteria(Criteria criteria, String propertyName, String propertyValue){
    	String[] keyValues = getCaseInsensitiveValues(propertyName, propertyValue);
   		criteria.like(keyValues[0], keyValues[1]);
    }

    private void addEqualToCriteria(Criteria criteria, String propertyName, String propertyValue){
    	String[] keyValues = getCaseInsensitiveValues(propertyName, propertyValue);
   		criteria.eq(keyValues[0], keyValues[1]);
    }

    private String[] getCaseInsensitiveValues(String propertyName, String propertyValue){
    	String[] keyValues = new String[2];
    	keyValues[0] = propertyName==null?"":"UPPER(__JPA_ALIAS[[0]]__." + propertyName + ")";
    	keyValues[1] = propertyValue==null?"":propertyValue.toUpperCase();
    	return keyValues;
    }

    @SuppressWarnings("unchecked")
	protected List<RoleMemberImpl> getRoleMemberImpls(Map<String, String> fieldValues){
		Criteria queryCriteria = new Criteria(RoleMemberImpl.class.getName());
		List<String> memberIds = new ArrayList<String>();
		if(hasExtraRoleMemberCriteria(fieldValues)){
			String memberName = fieldValues.get(KimConstants.KimUIConstants.MEMBER_NAME);
			String memberNamespaceCode = fieldValues.get(KimConstants.KimUIConstants.MEMBER_NAMESPACE_CODE);
			//Only name or namespace fields are provided in search
			//member type code is not provided
			List<RoleImpl> roles = getRoleMembersRoles(memberNamespaceCode, memberName);
			if(roles!=null){
				for(RoleImpl role: roles)
					memberIds.add(role.getRoleId());
			}

			memberIds.addAll(this.getPrincipalIdsForPrincipalName(memberName));

			memberIds.addAll(getRoleMembersGroupIds(memberNamespaceCode, memberName));

	        if(memberIds!=null && !memberIds.isEmpty())
	        	queryCriteria.in(KIMPropertyConstants.RoleMember.MEMBER_ID, memberIds);
		}
		if(hasCoreRoleMemberCriteria(fieldValues)){
	    	String roleMemberId = fieldValues.get(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID);
			String roleId = fieldValues.get(KimConstants.PrimaryKeyConstants.ROLE_ID);
			String memberId = fieldValues.get(KimConstants.PrimaryKeyConstants.MEMBER_ID);
			String memberTypeCode = fieldValues.get(KIMPropertyConstants.KimMember.MEMBER_TYPE_CODE);
			String activeFromDate = fieldValues.get(KIMPropertyConstants.KimMember.ACTIVE_FROM_DATE);
			String activeToDate = fieldValues.get(KIMPropertyConstants.KimMember.ACTIVE_TO_DATE);
			//role member id, role id, member id, member type code, active dates are provided in search
			if(StringUtils.isNotEmpty(roleMemberId))
				addEqualToCriteria(queryCriteria, KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
			if(StringUtils.isNotEmpty(roleId))
				addEqualToCriteria(queryCriteria, KimConstants.PrimaryKeyConstants.ROLE_ID, roleId);
			if(StringUtils.isNotEmpty(memberId))
				addEqualToCriteria(queryCriteria, KimConstants.PrimaryKeyConstants.MEMBER_ID, memberId);
			if(StringUtils.isNotEmpty(memberTypeCode))
				addEqualToCriteria(queryCriteria, KIMPropertyConstants.KimMember.MEMBER_TYPE_CODE, memberTypeCode);
			if(StringUtils.isNotEmpty(activeFromDate))
				queryCriteria.gte(KIMPropertyConstants.KimMember.ACTIVE_FROM_DATE, activeFromDate);
			if(StringUtils.isNotEmpty(activeToDate))
				queryCriteria.lte(KIMPropertyConstants.KimMember.ACTIVE_TO_DATE, activeToDate);
		}

        return (List<RoleMemberImpl>)new QueryByCriteria(entityManager, queryCriteria).toQuery().getResultList();
    }

    public List<RoleMembershipInfo> getRoleMembers(Map<String,String> fieldValues) {
    	List<RoleMemberImpl> roleMembers = getRoleMemberImpls(fieldValues);
        List<RoleMembershipInfo> roleMemberships = new ArrayList<RoleMembershipInfo>();
        RoleMembershipInfo roleMembership;
        for(RoleMemberImpl roleMember: roleMembers){
        	roleMembership = new RoleMembershipInfo(roleMember.getRoleId(), roleMember.getRoleMemberId(), roleMember.getMemberId(), roleMember.getMemberTypeCode(), roleMember.getQualifier() );
        	roleMemberships.add(roleMembership);
        }
        return roleMemberships;
    }

    public List<RoleMemberCompleteInfo> getRoleMembersCompleteInfo(Map<String,String> fieldValues) {
    	List<RoleMemberImpl> roleMembers = getRoleMemberImpls(fieldValues);
        List<RoleMemberCompleteInfo> roleMembersCompleteInfo = new ArrayList<RoleMemberCompleteInfo>();
        RoleMemberCompleteInfo roleMemberCompleteInfo;
        for(RoleMemberImpl roleMember: roleMembers){
        	roleMemberCompleteInfo = new RoleMemberCompleteInfo(
        			roleMember.getRoleId(), roleMember.getRoleMemberId(), roleMember.getMemberId(),
        			roleMember.getMemberTypeCode(), roleMember.getActiveFromDate(), roleMember.getActiveToDate(),
        			roleMember.getQualifier() );
        	roleMembersCompleteInfo.add(roleMemberCompleteInfo);
        }
        return roleMembersCompleteInfo;
    }

    private boolean hasCoreRoleMemberCriteria(Map<String, String> fieldValues){
		return StringUtils.isNotEmpty(fieldValues.get(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID)) ||
				StringUtils.isNotEmpty(fieldValues.get(KimConstants.PrimaryKeyConstants.ROLE_ID)) ||
				StringUtils.isNotEmpty(fieldValues.get(KimConstants.PrimaryKeyConstants.MEMBER_ID)) ||
				StringUtils.isNotEmpty(fieldValues.get(KIMPropertyConstants.KimMember.MEMBER_TYPE_CODE)) ||
				StringUtils.isNotEmpty(fieldValues.get(KIMPropertyConstants.KimMember.ACTIVE_FROM_DATE)) ||
				StringUtils.isNotEmpty(fieldValues.get(KIMPropertyConstants.KimMember.ACTIVE_TO_DATE));
    }

    private boolean hasExtraRoleMemberCriteria(Map<String, String> fieldValues){
    	return StringUtils.isNotEmpty(fieldValues.get(KimConstants.KimUIConstants.MEMBER_NAME)) ||
    			StringUtils.isNotEmpty(fieldValues.get(KimConstants.KimUIConstants.MEMBER_NAMESPACE_CODE));
    }

    @SuppressWarnings("unchecked")
	private List<RoleImpl> getRoleMembersRoles(String memberNamespaceCode, String memberName){
		Criteria queryCriteria = new Criteria(RoleImpl.class.getName());
		addEqualToCriteria(queryCriteria, KimConstants.UniqueKeyConstants.NAMESPACE_CODE, memberNamespaceCode);
		addEqualToCriteria(queryCriteria, KimConstants.UniqueKeyConstants.ROLE_NAME, memberName);

		return (List<RoleImpl>)new QueryByCriteria(entityManager, queryCriteria).toQuery().getResultList();
    }

    private List<String> getRoleMembersGroupIds(String memberNamespaceCode, String memberName){

    	Map<String,String> searchCrit = new HashMap<String, String>();
    	searchCrit.put(KimConstants.AttributeConstants.GROUP_NAME, memberName);
    	searchCrit.put(KimConstants.AttributeConstants.NAMESPACE_CODE, memberNamespaceCode);

    	return KIMServiceLocator.getGroupService().lookupGroupIds(searchCrit);
    }
    
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleGroupsForGroupIdsAndRoleIds(java.util.Collection, java.util.Collection, org.kuali.rice.core.util.AttributeSet)
	 */
	public List<RoleMemberImpl> getRoleGroupsForGroupIdsAndRoleIds(
			Collection<String> roleIds, Collection<String> groupIds,
			AttributeSet qualification) {
		Criteria c = new Criteria(RoleMemberImpl.class.getName());
		if(roleIds!=null && !roleIds.isEmpty())
			c.in(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
		if(groupIds!=null && !groupIds.isEmpty())
			c.in(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds);
		c.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.GROUP_MEMBER_TYPE );
		addSubCriteriaBasedOnRoleQualification(c, qualification);
		
		Collection<RoleMemberImpl> coll = (Collection<RoleMemberImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleMembersForRoleIds(java.util.Collection, java.lang.String, org.kuali.rice.core.util.AttributeSet)
	 */
	public List<RoleMemberImpl> getRoleMembersForRoleIds(
			Collection<String> roleIds, String memberTypeCode,
			AttributeSet qualification) {
		Criteria c = new Criteria(RoleMemberImpl.class.getName());

		if(roleIds!=null && !roleIds.isEmpty())
			c.in(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
		if ( memberTypeCode != null ) {
			c.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, memberTypeCode );
		}
		addSubCriteriaBasedOnRoleQualification(c, qualification);
		
		Collection<RoleMemberImpl> coll = (Collection<RoleMemberImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleMembersForRoleIdsWithFilters(java.util.Collection, java.lang.String, java.util.List, org.kuali.rice.core.util.AttributeSet)
	 */
	public List<RoleMemberImpl> getRoleMembersForRoleIdsWithFilters(
			Collection<String> roleIds, String principalId,
			List<String> groupIds, AttributeSet qualification) {
		Criteria c = new Criteria(RoleMemberImpl.class.getName());

		if(roleIds!=null && !roleIds.isEmpty())
			c.in(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
		Criteria orSet = new Criteria(RoleMemberImpl.class.getName());
		orSet.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.ROLE_MEMBER_TYPE );
		Criteria principalCheck = new Criteria(RoleMemberImpl.class.getName());
		if(principalId!=null)
			principalCheck.eq(KIMPropertyConstants.RoleMember.MEMBER_ID, principalId);
		principalCheck.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE );
		orSet.or( principalCheck );
		Criteria groupCheck = new Criteria(RoleMemberImpl.class.getName());
		if(groupIds!=null && !groupIds.isEmpty())
			groupCheck.in(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds);
		groupCheck.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.GROUP_MEMBER_TYPE );
		orSet.or( groupCheck );
		c.and( orSet );
		addSubCriteriaBasedOnRoleQualification(c, qualification);
		
		Collection<RoleMemberImpl> coll = (Collection<RoleMemberImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	/**

	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleMembershipsForRoleIdsAsMembers(java.util.Collection, org.kuali.rice.core.util.AttributeSet)
	 */
	public List<RoleMemberImpl> getRoleMembershipsForRoleIdsAsMembers(
			Collection<String> roleIds, AttributeSet qualification) {
		Criteria c = new Criteria(RoleMemberImpl.class.getName());

		if(roleIds!=null && !roleIds.isEmpty())
			c.in(KIMPropertyConstants.RoleMember.MEMBER_ID, roleIds);
		c.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);
		addSubCriteriaBasedOnRoleQualification(c, qualification);
		
		Collection<RoleMemberImpl> coll = (Collection<RoleMemberImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRolePrincipalsForPrincipalIdAndRoleIds(java.util.Collection, java.lang.String, org.kuali.rice.core.util.AttributeSet)
	 */
	public List<RoleMemberImpl> getRolePrincipalsForPrincipalIdAndRoleIds(
			Collection<String> roleIds, String principalId,
			AttributeSet qualification) {
		Criteria c = new Criteria(RoleMemberImpl.class.getName());

		if ( roleIds != null ) {
			c.in(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
		}
		if(principalId!=null)
			c.eq(KIMPropertyConstants.RoleMember.MEMBER_ID, principalId);
		c.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE );
		addSubCriteriaBasedOnRoleQualification(c, qualification);
		
		Collection<RoleMemberImpl> coll = (Collection<RoleMemberImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}
	
	/**
	 * Adds SubCriteria to the Query Criteria using the role qualification passed in 
	 * 
	 * @param c The Query Criteria object to be used 
	 * @param qualification The role qualification
	 */
	private void addSubCriteriaBasedOnRoleQualification(Criteria c, AttributeSet qualification) {
		if(qualification != null && CollectionUtils.isNotEmpty(qualification.keySet())) {
			for(Map.Entry<String, String> qualifier : qualification.entrySet()) {
		        Criteria subCrit = new Criteria(RoleMemberAttributeDataImpl.class.getName());
		        if(StringUtils.isNotEmpty(qualifier.getValue())) {
					String value = (qualifier.getValue()).replace('*', '%');
					subCrit.like("attributeValue", value);
					subCrit.eq("kimAttributeId", qualifier.getKey());
					//ArrayList<RoleMemberImpl> roleMbrs = (ArrayList<RoleMemberImpl>) new QueryByCriteria(entityManager, grpRoleCrit).toQuery().getResultList();
					//ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(RoleMemberAttributeDataImpl.class, subCrit);
					c.exists(subCrit);
		        }
			}
		}
	}

	/**
	 * 
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleMembershipsForMemberId(java.lang.String, java.lang.String, org.kuali.rice.core.util.AttributeSet)
	 */
	public List<RoleMemberImpl> getRoleMembershipsForMemberId(
			String memberType, String memberId, AttributeSet qualification) {
		Criteria c = new Criteria(RoleMemberImpl.class.getName());
		List<RoleMemberImpl> parentRoleMembers = new ArrayList<RoleMemberImpl>();

		if(StringUtils.isEmpty(memberId)
				|| StringUtils.isEmpty(memberType)) {
			return parentRoleMembers;
		}
		
		c.eq(KIMPropertyConstants.RoleMember.MEMBER_ID, memberId);
		c.eq( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, memberType);
		addSubCriteriaBasedOnRoleQualification(c, qualification);
		
		Collection<RoleMemberImpl> coll = (Collection<RoleMemberImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}
}
