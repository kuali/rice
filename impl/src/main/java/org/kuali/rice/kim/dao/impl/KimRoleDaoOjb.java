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

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.dao.KimRoleDao;
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
		
		c.addEqualTo("memberId", principalId);
		c.addIn("roleId", roleIds);
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
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleMembersForRoleIds(java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleMemberImpl> getRoleMembersForRoleIds( Collection<String> roleIds ) {	
		Criteria c = new Criteria();
		
		c.addIn("roleId", roleIds);
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
}
