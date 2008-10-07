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
import org.kuali.rice.kim.bo.role.impl.KimDelegationGroupImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationPrincipalImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleGroupImpl;
import org.kuali.rice.kim.bo.role.impl.RolePrincipalImpl;
import org.kuali.rice.kim.dao.KimRoleDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimRoleDaoOjb extends PlatformAwareDaoBaseOjb implements KimRoleDao {

	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getMatchingRolePrincipals(java.util.List, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<RolePrincipalImpl> getRolePrincipalsForPrincipalIdAndRoleIds( Collection<String> roleIds, String principalId) {
		
		Criteria c = new Criteria();
		
		c.addColumnEqualTo("principalId", principalId);
		c.addColumnIn("roleId", roleIds);
		Query query = QueryFactory.newQuery(RolePrincipalImpl.class, c);
		Collection<RolePrincipalImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		return new ArrayList<RolePrincipalImpl>( coll );
	}
	
	/**
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getRoleGroupsForGroupIdsAndRoleIds(java.util.List, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleGroupImpl> getRoleGroupsForGroupIdsAndRoleIds( Collection<String> roleIds, Collection<String> groupIds ) {
		Criteria c = new Criteria();
		c.addColumnIn("roleId", roleIds);
		c.addColumnIn("groupId", groupIds);
		Query query = QueryFactory.newQuery(RoleGroupImpl.class, c);
		Collection<RoleGroupImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		return new ArrayList<RoleGroupImpl>( coll );
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,KimRoleImpl> getRoleImplMap(Collection<String> roleIds) {
		HashMap<String,KimRoleImpl> results = new HashMap<String, KimRoleImpl>();
		Criteria c = new Criteria();
		c.addColumnIn("roleId", roleIds);
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
		c.addColumnIn("roleId", roleIds);
		c.addColumnEqualTo("active", Boolean.TRUE);
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
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getDelegationPrincipalsForPrincipalIdAndDelegationIds(java.util.Collection, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<KimDelegationPrincipalImpl> getDelegationPrincipalsForPrincipalIdAndDelegationIds(
			Collection<String> delegationIds, String principalId) {
		Criteria c = new Criteria();
		
		c.addColumnEqualTo("principalId", principalId);
		c.addColumnIn("delegationId", delegationIds);
		Query query = QueryFactory.newQuery(KimDelegationPrincipalImpl.class, c);
		Collection<KimDelegationPrincipalImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		return new ArrayList<KimDelegationPrincipalImpl>( coll );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.dao.KimRoleDao#getDelegationGroupsForGroupIdsAndDelegationIds(java.util.Collection, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public List<KimDelegationGroupImpl> getDelegationGroupsForGroupIdsAndDelegationIds(
			Collection<String> delegationIds, List<String> groupIds) {
		Criteria c = new Criteria();
		c.addColumnIn("delegationId", delegationIds);
		c.addColumnIn("groupId", groupIds);
		Query query = QueryFactory.newQuery(KimDelegationGroupImpl.class, c);
		Collection<KimDelegationGroupImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		return new ArrayList<KimDelegationGroupImpl>( coll );
	}
}
