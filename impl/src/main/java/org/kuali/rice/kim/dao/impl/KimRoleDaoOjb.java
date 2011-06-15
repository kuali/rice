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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.entity.principal.Principal;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.api.group.GroupQueryResults;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Role;

import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.dao.KimRoleDao;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimConstants;
import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is a description of what this class does - jonathan don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimRoleDaoOjb extends PlatformAwareDaoBaseOjb implements KimRoleDao {

	/**
	 * Adds SubCriteria to the Query Criteria using the role qualification passed in 
	 * 
	 * @param c The Query Criteria object to be used 
	 * @param qualification The role qualification
	 */
	private void addSubCriteriaBasedOnRoleQualification(Criteria c, AttributeSet qualification) {
		if(qualification != null && CollectionUtils.isNotEmpty(qualification.keySet())) {
			for(Map.Entry<String, String> qualifier : qualification.entrySet()) {
		        Criteria subCrit = new Criteria();
		        if(StringUtils.isNotEmpty(qualifier.getValue())) {
					String value = (qualifier.getValue()).replace('*', '%');
					subCrit.addLike("attributeValue", value);
					subCrit.addEqualTo("kimAttributeId", qualifier.getKey());
					ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(RoleMemberAttributeDataImpl.class, subCrit);
					c.addExists(subQuery);
		        }
			}
		}
	}
	
	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRolePrincipalsForPrincipalIdAndRoleIds(java.util.Collection,
	 *      java.lang.String, org.kuali.rice.core.util.AttributeSet)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRolePrincipalsForPrincipalIdAndRoleIds( Collection<String> roleIds, String principalId, AttributeSet qualification) {

		Criteria c = new Criteria();

		if ( roleIds != null ) {
			c.addIn(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
		}
		if(principalId!=null)
			c.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_ID, principalId);
		c.addEqualTo( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE );
		addSubCriteriaBasedOnRoleQualification(c, qualification);
		
		Query query = QueryFactory.newQuery(RoleMemberImpl.class, c);
		Collection<RoleMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	public List<GroupMember> getGroupPrincipalsForPrincipalIdAndGroupIds( Collection<String> groupIds, String principalId) {
	    List<String> groupIdValues = new ArrayList<String>();
	    List<GroupMember> groupPrincipals = new ArrayList<GroupMember>();
	    if (groupIds != null
	            && principalId == null) {
	        groupIdValues = new ArrayList<String>(groupIds);
	    } else if (principalId != null) {
	        groupIdValues = KimApiServiceLocator.getGroupService().getGroupIdsForPrincipal(principalId);
	    }
	    if (groupIdValues != null
	            && groupIdValues.size() > 0) {
    	    Collection<GroupMember> groupMembershipInfos = KimApiServiceLocator.getGroupService().getMembers(groupIdValues);
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

	public List<GroupMember> getGroupMembers(Collection<String> groupIds) {
	    List<String> groupIdValues = new ArrayList<String>();
	    List<GroupMember> groupMembers = new ArrayList<GroupMember>();
	    if (groupIds != null) {
	        groupIdValues = new ArrayList<String>(groupIds);

	        if (groupIdValues != null
	                && groupIdValues.size() > 0) {
                Collection<GroupMember> groupMembershipInfos = KimApiServiceLocator.getGroupService().getMembers(groupIdValues);
	            //Collection<GroupMember> groupMembershipInfos = KimApiServiceLocator.getGroupService().g.getGroupMembers(groupIdValues);

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
	 *      java.util.Collection, org.kuali.rice.core.util.AttributeSet)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRoleGroupsForGroupIdsAndRoleIds( Collection<String> roleIds, Collection<String> groupIds, AttributeSet qualification ) {
		Criteria c = new Criteria();
		if(roleIds!=null && !roleIds.isEmpty())
			c.addIn(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
		if(groupIds!=null && !groupIds.isEmpty())
			c.addIn(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds);
		c.addEqualTo( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.GROUP_MEMBER_TYPE );
		addSubCriteriaBasedOnRoleQualification(c, qualification);
		
		Query query = QueryFactory.newQuery(RoleMemberImpl.class, c);
		Collection<RoleMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
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
			Criteria c = new Criteria();
			c.addIn(KIMPropertyConstants.Delegation.ROLE_ID, roleIds);
			c.addEqualTo(KIMPropertyConstants.Delegation.ACTIVE, Boolean.TRUE);
			Query query = QueryFactory.newQuery(KimDelegationImpl.class, c);
			Collection<KimDelegationImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
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
			Criteria c = new Criteria();
			c.addIn(KIMPropertyConstants.Delegation.ROLE_ID, roleIds);
			c.addEqualTo(KIMPropertyConstants.Delegation.ACTIVE, Boolean.TRUE);
			Query query = QueryFactory.newQuery(KimDelegationImpl.class, c);
			Collection<KimDelegationImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
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
		Criteria c = new Criteria();

		if(principalId!=null)
			c.addEqualTo(KIMPropertyConstants.DelegationMember.MEMBER_ID, principalId);
		c.addEqualTo( KIMPropertyConstants.DelegationMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE );
		if(delegationIds!=null && !delegationIds.isEmpty())
			c.addIn(KIMPropertyConstants.DelegationMember.DELEGATION_ID, delegationIds);
		Query query = QueryFactory.newQuery(KimDelegationMemberImpl.class, c);
		Collection<KimDelegationMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
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
		Criteria c = new Criteria();
		if(delegationIds!=null && !delegationIds.isEmpty())
			c.addIn(KIMPropertyConstants.DelegationMember.DELEGATION_ID, delegationIds);
		if(groupIds!=null && !groupIds.isEmpty())
			c.addIn(KIMPropertyConstants.DelegationMember.MEMBER_ID, groupIds);
		c.addEqualTo( KIMPropertyConstants.DelegationMember.MEMBER_TYPE_CODE, Role.GROUP_MEMBER_TYPE );
		Query query = QueryFactory.newQuery(KimDelegationMemberImpl.class, c);
		Collection<KimDelegationMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		ArrayList<KimDelegationMemberImpl> results = new ArrayList<KimDelegationMemberImpl>( coll.size() );
		for ( KimDelegationMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleMembersForRoleIds(Collection, String, org.kuali.rice.core.util.AttributeSet)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRoleMembersForRoleIds( Collection<String> roleIds, String memberTypeCode, AttributeSet qualification ) {
		Criteria c = new Criteria();

		if(roleIds!=null && !roleIds.isEmpty())
			c.addIn(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
		if ( memberTypeCode != null ) {
			c.addEqualTo( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, memberTypeCode );
		}
		addSubCriteriaBasedOnRoleQualification(c, qualification);
		
		Query query = QueryFactory.newQuery(RoleMemberImpl.class, c);
		Collection<RoleMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleMembersForRoleIds(Collection, String, org.kuali.rice.core.util.AttributeSet)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRoleMembershipsForRoleIdsAsMembers( Collection<String> roleIds, AttributeSet qualification) {
		Criteria c = new Criteria();

		if(roleIds!=null && !roleIds.isEmpty())
			c.addIn(KIMPropertyConstants.RoleMember.MEMBER_ID, roleIds);
		c.addEqualTo( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);
		addSubCriteriaBasedOnRoleQualification(c, qualification);
		
		Query query = QueryFactory.newQuery(RoleMemberImpl.class, c);
		Collection<RoleMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}
	
	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleMembersForRoleIds(Collection, String, org.kuali.rice.core.util.AttributeSet)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRoleMembershipsForMemberId(String memberType, String memberId, AttributeSet qualification) {
		Criteria c = new Criteria();
		List<RoleMemberImpl> parentRoleMembers = new ArrayList<RoleMemberImpl>();

		if(StringUtils.isEmpty(memberId)
				|| StringUtils.isEmpty(memberType)) {
			return parentRoleMembers;
		}
		
		c.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_ID, memberId);
		c.addEqualTo( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, memberType);
		addSubCriteriaBasedOnRoleQualification(c, qualification);
		
		Query query = QueryFactory.newQuery(RoleMemberImpl.class, c);
		Collection<RoleMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		ArrayList<RoleMemberImpl> results = new ArrayList<RoleMemberImpl>( coll.size() );
		for ( RoleMemberImpl rm : coll ) {
			if ( rm.isActive() ) {
				results.add(rm);
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRoleMembersForRoleIdsWithFilters( Collection<String> roleIds, String principalId, List<String> groupIds, AttributeSet qualification) {
		Criteria c = new Criteria();

		if(roleIds!=null && !roleIds.isEmpty())
			c.addIn(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
		Criteria orSet = new Criteria();
		orSet.addEqualTo( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.ROLE_MEMBER_TYPE );
		Criteria principalCheck = new Criteria();
		if(principalId!=null)
			principalCheck.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_ID, principalId);
		principalCheck.addEqualTo( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE );
		orSet.addOrCriteria( principalCheck );
		Criteria groupCheck = new Criteria();
		if(groupIds!=null && !groupIds.isEmpty())
			groupCheck.addIn(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds);
		groupCheck.addEqualTo( KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.GROUP_MEMBER_TYPE );
		orSet.addOrCriteria( groupCheck );
		c.addAndCriteria( orSet );
		addSubCriteriaBasedOnRoleQualification(c, qualification);
		
		Query query = QueryFactory.newQuery(RoleMemberImpl.class, c);
		Collection<RoleMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
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
		Criteria c = new Criteria();
		if(delegationIds!=null && !delegationIds.isEmpty())
			c.addIn(KIMPropertyConstants.DelegationMember.DELEGATION_ID, delegationIds);
		Query query = QueryFactory.newQuery(KimDelegationMemberImpl.class, c);
		Collection<KimDelegationMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
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
        Criteria crit = new Criteria();
        Map<String,Map<String,String>> critMap = setupCritMaps(fieldValues);

//        BusinessObjectEntry boEntry = KRADServiceLocatorInternal.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry("org.kuali.rice.kim.bo.impl.RoleImpl");
//      List lookupNames = boEntry.getLookupDefinition().getLookupFieldNames();
        Map<String,String> lookupNames = critMap.get("lookupNames");
        for (Entry<String, String> entry : lookupNames.entrySet()) {
        	if (StringUtils.isNotBlank(entry.getValue())) {
        			if (!entry.getKey().equals(KIMPropertyConstants.Principal.PRINCIPAL_NAME)) {
        				addLikeToCriteria(crit, entry.getKey(), entry.getValue());
        			} else {
        					List roleIds = getRoleIdsForPrincipalName(entry.getValue());
                			if (roleIds!=null && !roleIds.isEmpty()) {
                				crit.addIn(KIMPropertyConstants.Role.ROLE_ID, roleIds);
                			} else {
                			// TODO : if no role id found that means principalname not matched, need to do something to force to return empty list
                				roleIds.add("NOTFOUND");
                				crit.addIn(KIMPropertyConstants.Role.ROLE_ID, roleIds);
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
			crit.addExists(setupPermCriteria(critMap.get("perm")));
		}
		if (!critMap.get("resp").isEmpty()) {
			crit.addExists(setupRespCriteria(critMap.get("resp")));
		}
		if (!critMap.get("group").isEmpty()) {
			crit.addExists(setupGroupCriteria(critMap.get("group")));
		}

        Query q = QueryFactory.newQuery(RoleImpl.class, crit);

        return (List)getPersistenceBrokerTemplate().getCollectionByQuery(q);
    }

    
    private List<String> getPrincipalIdsForPrincipalName(String principalName){
    	Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("principals.principalName", principalName);
        List<? extends KimEntityDefaultInfo> entities = KimApiServiceLocator.getIdentityService().lookupEntityDefaultInfo(criteria, false);

        List<String> principalIds = new ArrayList<String>();
        for (KimEntityDefaultInfo entity : entities) {
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
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("principals.principalName", principalName);
        List<? extends KimEntityDefaultInfo> entities = KimApiServiceLocator.getIdentityService().lookupEntityDefaultInfo(criteria, false);
        if (entities == null
                || entities.size() == 0) {
            return roleIds;
        }

        List<String> principalIds = new ArrayList<String>();
        for (KimEntityDefaultInfo entity : entities) {
            for (Principal principal : entity.getPrincipals()) {
                principalIds.add(principal.getPrincipalId());
            }
        }
        if(principalIds!=null && !principalIds.isEmpty()){
        	memberSubCrit.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE);
        	memberSubCrit.addIn(KIMPropertyConstants.RoleMember.MEMBER_ID, principalIds);

			ReportQueryByCriteria memberSubQuery = QueryFactory.newReportQuery(RoleMemberImpl.class, memberSubCrit);
			for (RoleMemberImpl roleMbr : (List<RoleMemberImpl>)getPersistenceBrokerTemplate().getCollectionByQuery(memberSubQuery)) {
				if (roleMbr.isActive() && !roleIds.contains(roleMbr.getRoleId())) {
					roleIds.add(roleMbr.getRoleId());
				}
			}
        }

		List<String> groupIds = new ArrayList<String>();
		for (String principalId : principalIds) {
		    List<String> principalGroupIds = KimApiServiceLocator.getGroupService().getGroupIdsForPrincipal(principalId);
		    for (String groupId : principalGroupIds) {
		        if (!groupIds.contains(groupId)) {
		            groupIds.add(groupId);
		        }
		    }
		}

        if(groupIds!=null && !groupIds.isEmpty()){
        	Criteria grpRoleCrit = new Criteria();
        	grpRoleCrit.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, Role.GROUP_MEMBER_TYPE);
        	grpRoleCrit.addIn(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds);

        	ReportQueryByCriteria memberSubQuery = QueryFactory.newReportQuery(RoleMemberImpl.class, grpRoleCrit);

			for (RoleMemberImpl roleMbr : (List<RoleMemberImpl>)getPersistenceBrokerTemplate().getCollectionByQuery(memberSubQuery)) {
				if (roleMbr.isActive() && !roleIds.contains(roleMbr.getRoleId()) ) {
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
			Criteria subCrit = new Criteria();
			addLikeToCriteria(subCrit, "attributes.attributeValue",entry.getValue());
			addEqualToCriteria(subCrit, "attributes.kimAttributeId",entry.getKey().substring(entry.getKey().indexOf(".")+1, entry.getKey().length()));
			addEqualToCriteria(subCrit, "attributes.kimTypeId", kimTypeId);
			subCrit.addEqualToField("roleId", Criteria.PARENT_QUERY_PREFIX + "roleId");
			crit.addExists(QueryFactory.newReportQuery(RoleMemberImpl.class, subCrit));
        }
    }

    private ReportQueryByCriteria setupPermCriteria(Map<String,String> permCrit) {

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

        List<KimPermissionInfo> permList = KimApiServiceLocator.getPermissionService().lookupPermissions(searchCrit, true);
        List<String> roleIds = null;

        if(permList != null && !permList.isEmpty()){
        	roleIds = KimApiServiceLocator.getPermissionService().getRoleIdsForPermissions(permList);
        }

        if(roleIds == null || roleIds.isEmpty()){
        	roleIds = new ArrayList<String>();
        	roleIds.add("-1"); // this forces a blank return.
        }

        Criteria memberSubCrit = new Criteria();
        memberSubCrit.addIn("roleId", roleIds);
        memberSubCrit.addEqualToField("roleId", Criteria.PARENT_QUERY_PREFIX + "roleId");
		return QueryFactory.newReportQuery(RoleImpl.class, memberSubCrit);

    }

    private ReportQueryByCriteria setupRespCriteria(Map<String,String> respCrit) {


        List<Predicate> inner = new ArrayList<Predicate>();
        for (Entry<String, String> entry : respCrit.entrySet()) {
            if (entry.getKey().equals("respTmplName") || entry.getKey().equals("respTmplNamespaceCode")) {
        		final Predicate tmpl;
                if (entry.getKey().equals("respTmplName")) {
        			tmpl = equal("template." + KimConstants.UniqueKeyConstants.RESPONSIBILITY_TEMPLATE_NAME, entry.getValue());
        		} else {
        			tmpl = equal("template." + KimConstants.UniqueKeyConstants.NAMESPACE_CODE, entry.getValue());
        		}
                inner.add(tmpl);
        	}

        	if (entry.getKey().equals("respName") || entry.getKey().equals("respNamespaceCode")) {
        		final Predicate other;
                if (entry.getKey().equals("respName")) {
        			other = equal(KimConstants.UniqueKeyConstants.RESPONSIBILITY_NAME, entry.getValue());
        		} else {
        			other = equal(KimConstants.UniqueKeyConstants.NAMESPACE_CODE,entry.getValue());
        		}
                inner.add(other);
        	}
        }
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(inner.toArray(new Predicate[] {}));

        List<Responsibility> kriList = KimApiServiceLocator.getResponsibilityService().findResponsibilities(builder.build()).getResults();
        List<String> roleIds = new ArrayList<String>();

        for(Responsibility kri : kriList){
        	roleIds.addAll(KimApiServiceLocator.getResponsibilityService().getRoleIdsForResponsibility(kri.getId(), null));
        }

        if(roleIds == null || roleIds.isEmpty()){
        	roleIds = new ArrayList<String>();
        	roleIds.add("-1"); // this forces a blank return.
        }

        Criteria memberSubCrit = new Criteria();
        memberSubCrit.addIn("roleId", roleIds);
        memberSubCrit.addEqualToField("roleId", Criteria.PARENT_QUERY_PREFIX + "roleId");
		return QueryFactory.newReportQuery(RoleImpl.class, memberSubCrit);

    }

    private ReportQueryByCriteria setupGroupCriteria(Map<String,String> groupCrit) {

    	//Map<String,String> searchCrit = new HashMap<String, String>();
        final QueryByCriteria.Builder searchCrit = QueryByCriteria.Builder.create();
        for (Entry<String, String> entry : groupCrit.entrySet()) {
        		if (entry.getKey().equals(KimConstants.AttributeConstants.GROUP_NAME)) {
        			//searchCrit.put(entry.getKey(), entry.getValue());
                    searchCrit.setPredicates(equal(entry.getKey(), entry.getValue()));
        		} else { // the namespace code for the group field is named something besides the default. Set it to the default.
        			//searchCrit.put(KimConstants.AttributeConstants.NAMESPACE_CODE, entry.getValue());
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

		return QueryFactory.newReportQuery(RoleMemberImpl.class, crit);

    }

    private void addLikeToCriteria(Criteria criteria, String propertyName, String propertyValue){
    	String[] keyValues = getCaseInsensitiveValues(propertyName, propertyValue);
   		criteria.addLike(keyValues[0], keyValues[1]);
    }

    private void addEqualToCriteria(Criteria criteria, String propertyName, String propertyValue){
    	String[] keyValues = getCaseInsensitiveValues(propertyName, propertyValue);
   		criteria.addEqualTo(keyValues[0], keyValues[1]);
    }

    private String[] getCaseInsensitiveValues(String propertyName, String propertyValue){
    	String[] keyValues = new String[2];
    	keyValues[0] = propertyName==null?"":getDbPlatform().getUpperCaseFunction() + "(" + propertyName + ")";
    	keyValues[1] = propertyValue==null?"":propertyValue.toUpperCase();
    	return keyValues;
    }

    @SuppressWarnings("unchecked")
	protected List<RoleMemberImpl> getRoleMemberImpls(Map<String, String> fieldValues){
		Criteria queryCriteria = new Criteria();
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
	        	queryCriteria.addIn(KIMPropertyConstants.RoleMember.MEMBER_ID, memberIds);
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
				queryCriteria.addGreaterOrEqualThan(KIMPropertyConstants.KimMember.ACTIVE_FROM_DATE, activeFromDate);
			if(StringUtils.isNotEmpty(activeToDate))
				queryCriteria.addLessOrEqualThan(KIMPropertyConstants.KimMember.ACTIVE_TO_DATE, activeToDate);
		}
        Query q = QueryFactory.newQuery(RoleMemberImpl.class, queryCriteria);
        return (List<RoleMemberImpl>)getPersistenceBrokerTemplate().getCollectionByQuery(q);
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
		Criteria queryCriteria = new Criteria();
		addEqualToCriteria(queryCriteria, KimConstants.UniqueKeyConstants.NAMESPACE_CODE, memberNamespaceCode);
		addEqualToCriteria(queryCriteria, KimConstants.UniqueKeyConstants.ROLE_NAME, memberName);
		Query q = QueryFactory.newQuery(RoleImpl.class, queryCriteria);
		return (List<RoleImpl>)getPersistenceBrokerTemplate().getCollectionByQuery(q);
    }

    private List<String> getRoleMembersGroupIds(String memberNamespaceCode, String memberName){

    	/*Map<String,String> searchCrit = new HashMap<String, String>();
    	searchCrit.put(KimConstants.AttributeConstants.GROUP_NAME, memberName);
    	searchCrit.put(KimConstants.AttributeConstants.NAMESPACE_CODE, memberNamespaceCode);*/
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(and(
                    like(KimConstants.AttributeConstants.GROUP_NAME, memberName),
                    like(KimConstants.AttributeConstants.NAMESPACE_CODE, memberNamespaceCode)));
         return KimApiServiceLocator.getGroupService().findGroupIds(builder.build());
    }
}
