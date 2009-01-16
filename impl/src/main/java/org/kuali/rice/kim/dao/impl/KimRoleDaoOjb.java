/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
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

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RolePermissionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.dao.KimRoleDao;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

/**
 * This is a description of what this class does - jonathan don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class KimRoleDaoOjb extends PlatformAwareDaoBaseOjb implements KimRoleDao {

	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRolePrincipalsForPrincipalIdAndRoleIds(java.util.Collection,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRolePrincipalsForPrincipalIdAndRoleIds( Collection<String> roleIds, String principalId) {
		
		Criteria c = new Criteria();
		
		c.addIn("roleId", roleIds);
		c.addEqualTo("memberId", principalId);
		c.addEqualTo( "memberTypeCode", KimRole.PRINCIPAL_MEMBER_TYPE );
		Query query = QueryFactory.newQuery(RoleMemberImpl.class, c);
		Collection<RoleMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		return new ArrayList<RoleMemberImpl>( coll );
	}
	
	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleGroupsForGroupIdsAndRoleIds(java.util.Collection,
	 *      java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRoleGroupsForGroupIdsAndRoleIds( Collection<String> roleIds, Collection<String> groupIds ) {
		Criteria c = new Criteria();
		c.addIn("roleId", roleIds);
		c.addIn("memberId", groupIds);
		c.addEqualTo( "memberTypeCode", KimRole.GROUP_MEMBER_TYPE );
		Query query = QueryFactory.newQuery(RoleMemberImpl.class, c);
		Collection<RoleMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		return new ArrayList<RoleMemberImpl>( coll );
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,KimRoleImpl> getRoleImplMap(Collection<String> roleIds) {
		HashMap<String,KimRoleImpl> results = new HashMap<String, KimRoleImpl>();
		Criteria c = new Criteria();
		c.addIn("roleId", roleIds);
		c.addEqualTo("active", "Y");
		Query query = QueryFactory.newQuery(KimRoleImpl.class, c);
		Collection<KimRoleImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		for ( KimRoleImpl role : coll ) {
			results.put( role.getRoleId(), role);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public Map<String,KimDelegationImpl> getDelegationImplMapFromRoleIds(Collection<String> roleIds) {
		HashMap<String,KimDelegationImpl> results = new HashMap<String, KimDelegationImpl>();
		Criteria c = new Criteria();
		c.addIn("roleId", roleIds);
		c.addEqualTo("active", Boolean.TRUE);
		Query query = QueryFactory.newQuery(KimDelegationImpl.class, c);
		Collection<KimDelegationImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		for ( KimDelegationImpl dele : coll ) {
			results.put( dele.getDelegationId(), dele);
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
		
		c.addEqualTo("memberId", principalId);
		c.addEqualTo( "memberTypeCode", KimRole.PRINCIPAL_MEMBER_TYPE );
		c.addIn("delegationId", delegationIds);
		Query query = QueryFactory.newQuery(KimDelegationMemberImpl.class, c);
		Collection<KimDelegationMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		return new ArrayList<KimDelegationMemberImpl>( coll );
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
		c.addIn("delegationId", delegationIds);
		c.addIn("memberId", groupIds);
		c.addEqualTo( "memberTypeCode", KimRole.GROUP_MEMBER_TYPE );
		Query query = QueryFactory.newQuery(KimDelegationMemberImpl.class, c);
		Collection<KimDelegationMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		return new ArrayList<KimDelegationMemberImpl>( coll );
	}
	
	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleMembersForRoleIds(Collection, String)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRoleMembersForRoleIds( Collection<String> roleIds, String memberTypeCode ) {	
		Criteria c = new Criteria();
		
		c.addIn("roleId", roleIds);
		if ( memberTypeCode != null ) {
			c.addEqualTo( "memberTypeCode", memberTypeCode );
		}
		Query query = QueryFactory.newQuery(RoleMemberImpl.class, c);
		Collection<RoleMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		return new ArrayList<RoleMemberImpl>( coll );
	}

	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRoleMembersForRoleIdsWithFilters( Collection<String> roleIds, String principalId, List<String> groupIds ) {	
		Criteria c = new Criteria();
		
		c.addIn("roleId", roleIds);
		Criteria orSet = new Criteria();
		orSet.addEqualTo( "memberTypeCode", KimRole.ROLE_MEMBER_TYPE );
		Criteria principalCheck = new Criteria();
		principalCheck.addEqualTo("memberId", principalId);
		principalCheck.addEqualTo( "memberTypeCode", KimRole.PRINCIPAL_MEMBER_TYPE );
		orSet.addOrCriteria( principalCheck );
		Criteria groupCheck = new Criteria();
		groupCheck.addIn("memberId", groupIds);
		groupCheck.addEqualTo( "memberTypeCode", KimRole.GROUP_MEMBER_TYPE );
		c.addAndCriteria( orSet );
		Query query = QueryFactory.newQuery(RoleMemberImpl.class, c);
		Collection<RoleMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		return new ArrayList<RoleMemberImpl>( coll );
	}
	
	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getDelegationMembersForDelegationIds(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public Map<String,List<KimDelegationMemberImpl>> getDelegationMembersForDelegationIds(
			List<String> delegationIds) {
		Criteria c = new Criteria();
		
		c.addIn("delegationId", delegationIds);
		Query query = QueryFactory.newQuery(KimDelegationMemberImpl.class, c);
		Collection<KimDelegationMemberImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		HashMap<String,List<KimDelegationMemberImpl>> result = new HashMap<String,List<KimDelegationMemberImpl>>();
		for ( KimDelegationMemberImpl dp : coll ) {
			if ( !result.containsKey( dp.getDelegationId() ) ) {
				result.put( dp.getDelegationId(), new ArrayList<KimDelegationMemberImpl>() );
			}
			result.get( dp.getDelegationId() ).add( dp );
		}
		return result;
	}
	
    public List<KimRoleImpl> getRoles(Map<String,String> fieldValues) {
        Criteria crit = new Criteria();
        Map<String,Map<String,String>> critMap = setupCritMaps(fieldValues);
        
//        BusinessObjectEntry boEntry = KNSServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry("org.kuali.rice.kim.bo.impl.RoleImpl");
//      List lookupNames = boEntry.getLookupDefinition().getLookupFieldNames();
        Map<String,String> lookupNames = critMap.get("lookupNames");
        for (Entry<String, String> entry : lookupNames.entrySet()) {
        	if (StringUtils.isNotBlank(entry.getValue())) {
        			if (!entry.getKey().equals(KIMPropertyConstants.Person.PRINCIPAL_NAME)) {
        				crit.addLike(entry.getKey(), entry.getValue());
        			} else {
        					List roleIds = getRoleIdsForPrincipalName(entry.getValue());
                			if (!roleIds.isEmpty()) {
                				crit.addIn("roleId", roleIds);
                			} else {
                			// TODO : if no role id found that means principalname not matched, need to do something to force to return empty list
                				roleIds.add("NOTFOUND");
                				crit.addIn("roleId", roleIds);
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

        Query q = QueryFactory.newQuery(KimRoleImpl.class, crit);
        
        return (List)getPersistenceBrokerTemplate().getCollectionByQuery(q);
    }


    private List<String> getRoleIdsForPrincipalName(String value) {
        Criteria subCrit = new Criteria();
		String principalName = value.replace('*', '%');
		subCrit.addLike(KIMPropertyConstants.Person.PRINCIPAL_NAME,principalName );
        subCrit.addEqualToField(KIMPropertyConstants.Person.PRINCIPAL_ID, Criteria.PARENT_QUERY_PREFIX + "memberId");
		ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(KimPrincipalImpl.class, subCrit);
        Criteria memberSubCrit = new Criteria();
        //memberSubCrit.addEqualToField("roleId", Criteria.PARENT_QUERY_PREFIX + "roleId");
        memberSubCrit.addEqualTo("memberTypeCode", "P");
        memberSubCrit.addExists(subQuery);
        List<String> roleIds = new ArrayList<String>();
		ReportQueryByCriteria memberSubQuery = QueryFactory.newReportQuery(RoleMemberImpl.class, memberSubCrit);
		for (RoleMemberImpl roleMbr : (List<RoleMemberImpl>)getPersistenceBrokerTemplate().getCollectionByQuery(memberSubQuery)) {
			if (!roleIds.contains(roleMbr.getRoleId())) {
				roleIds.add(roleMbr.getRoleId());
			}
		}
		Criteria groupMSubCrit = new Criteria();
	        groupMSubCrit.addEqualToField("groupId", Criteria.PARENT_QUERY_PREFIX + "memberId");
	       groupMSubCrit.addEqualTo("memberTypeCode", "P");
	        groupMSubCrit.addExists(subQuery);
		ReportQueryByCriteria groupMbrSubQuery = QueryFactory.newReportQuery(GroupMemberImpl.class, groupMSubCrit);
        Criteria grpRoleCrit = new Criteria();
        grpRoleCrit.addEqualTo("memberTypeCode", "G");
        grpRoleCrit.addExists(groupMbrSubQuery);
        memberSubQuery = QueryFactory.newReportQuery(RoleMemberImpl.class, grpRoleCrit);

		for (RoleMemberImpl roleMbr : (List<RoleMemberImpl>)getPersistenceBrokerTemplate().getCollectionByQuery(memberSubQuery)) {
			if (!roleIds.contains(roleMbr.getRoleId())) {
				roleIds.add(roleMbr.getRoleId());
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
        		} else if (entry.getKey().startsWith(KimAttributes.GROUP_NAME)) {
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
			subCrit.addLike("attributes.attributeValue",entry.getValue());
			subCrit.addEqualTo("attributes.kimAttributeId",entry.getKey().substring(entry.getKey().indexOf(".")+1, entry.getKey().length()));
			subCrit.addEqualTo("attributes.kimTypeId", kimTypeId);
			subCrit.addEqualToField("roleId", Criteria.PARENT_QUERY_PREFIX + "roleId");
			crit.addExists(QueryFactory.newReportQuery(RoleMemberImpl.class, subCrit));
        }
    }
    
    private ReportQueryByCriteria setupPermCriteria(Map<String,String> permCrit) {

    	//Criteria tmplSubCrit = new Criteria();
        Criteria memberSubCrit = new Criteria();
        for (Entry<String, String> entry : permCrit.entrySet()) {
        	if (entry.getKey().equals("permTmplName") || entry.getKey().equals("permTmplNamespaceCode")) {
        		if (entry.getKey().equals("permTmplName")) {
        			memberSubCrit.addLike("kimPermission.template.name",entry.getValue());
        			
        		} else {
        			memberSubCrit.addLike("kimPermission.template.namespaceCode",entry.getValue());        			
        		}
        	}
        	
        	if (entry.getKey().equals("permName") || entry.getKey().equals("permNamespaceCode")) {
        		if (entry.getKey().equals("permName")) {
        			memberSubCrit.addLike("kimPermission.name", entry.getValue());
        		} else {
        			memberSubCrit.addLike("kimPermission.namespaceCode",entry.getValue());
        			
        		}
        	}
        }

        memberSubCrit.addEqualToField("roleId", Criteria.PARENT_QUERY_PREFIX + "roleId");
		return QueryFactory.newReportQuery(RolePermissionImpl.class, memberSubCrit);

    }
    
    private ReportQueryByCriteria setupRespCriteria(Map<String,String> respCrit) {

        Criteria memberSubCrit = new Criteria();
        for (Entry<String, String> entry : respCrit.entrySet()) {
        	if (entry.getKey().equals("respTmplName") || entry.getKey().equals("respTmplNamespaceCode")) {
        		if (entry.getKey().equals("respTmplName")) {
        			memberSubCrit.addLike("kimResponsibility.template.name", entry.getValue());
        			
        		} else {
        			memberSubCrit.addLike("kimResponsibility.template.namespaceCode",entry.getValue());        			
        		}
        	}
        	
        	if (entry.getKey().equals("respName") || entry.getKey().equals("respNamespaceCode")) {
        		if (entry.getKey().equals("respName")) {
        			memberSubCrit.addLike("kimResponsibility.name", entry.getValue());
        		} else {
        			memberSubCrit.addLike("kimResponsibility.namespaceCode",entry.getValue());
        			
        		}
        	}
        }

        memberSubCrit.addEqualToField("roleId", Criteria.PARENT_QUERY_PREFIX + "roleId");
		return QueryFactory.newReportQuery(RoleResponsibilityImpl.class, memberSubCrit);

    }
 
    private ReportQueryByCriteria setupGroupCriteria(Map<String,String> groupCrit) {

    	//Criteria tmplSubCrit = new Criteria();
        Criteria memberSubCrit = new Criteria();
        for (Entry<String, String> entry : groupCrit.entrySet()) {
        		if (entry.getKey().equals(KimAttributes.GROUP_NAME)) {
        			memberSubCrit.addLike(KimAttributes.GROUP_NAME, entry.getValue());
        		} else {
        			memberSubCrit.addLike(KimAttributes.NAMESPACE_CODE,entry.getValue());        			
        		}
        }
        memberSubCrit.addEqualToField("groupId", Criteria.PARENT_QUERY_PREFIX + "memberId");
        Criteria crit = new Criteria();
        crit.addExists(QueryFactory.newReportQuery(KimGroupImpl.class, memberSubCrit));
        crit.addEqualTo("memberTypeCode", "G");
		return QueryFactory.newReportQuery(RoleMemberImpl.class, crit);

    }

    

}
