/*
 * Copyright 2008 The Kuali Foundation
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
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.dao.KimResponsibilityDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimResponsibilityDaoOjb extends PlatformAwareDaoBaseOjb implements KimResponsibilityDao {

	/**
	 * @see org.kuali.rice.kim.dao.KimResponsibilityDao#getRoleIdsForResponsibilities(java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRoleIdsForResponsibilities(Collection<KimResponsibilityImpl> responsibilities) {
		List<String> responsibilityIds = new ArrayList<String>( responsibilities.size() );
		for ( KimResponsibilityImpl kp : responsibilities ) {
			responsibilityIds.add( kp.getResponsibilityId() );
		}
		Criteria c = new Criteria();
		c.addIn( "responsibilityId", responsibilityIds );
		c.addEqualTo( "active", true );
		
		Query query = QueryFactory.newQuery( RoleResponsibilityImpl.class, c, true );
		Collection<RoleResponsibilityImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		List<String> roleIds = new ArrayList<String>( coll.size() );
		for ( RoleResponsibilityImpl rp : coll ) {
			roleIds.add( rp.getRoleId() );
		}
		return roleIds;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.dao.KimResponsibilityDao#getRoleIdsForResponsibility(org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl)
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRoleIdsForResponsibility(
			KimResponsibilityImpl responsibility) {
		Criteria c = new Criteria();
		c.addEqualTo( "responsibilityId", responsibility.getResponsibilityId() );
		c.addEqualTo( "active", true );
		
		Query query = QueryFactory.newQuery( RoleResponsibilityImpl.class, c, true );
		Collection<RoleResponsibilityImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		List<String> roleIds = new ArrayList<String>( coll.size() );
		for ( RoleResponsibilityImpl rp : coll ) {
			roleIds.add( rp.getRoleId() );
		}
		return roleIds;
	}
	
	/**
	 * @see org.kuali.rice.kim.dao.KimResponsibilityDao#getResponsibilityAction(java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public RoleResponsibilityActionImpl getResponsibilityAction(String responsibilityId,
			String principalId, String groupId) {
		if ( principalId == null && groupId == null ) {
			return null;
		}
		
		Criteria c = new Criteria();
		c.addEqualTo( "responsibilityId", responsibilityId );
		// TODO: also handle when principalID is "*" in table
		if ( principalId != null ) {
			c.addEqualTo( "principalId", principalId );
		}
		if ( groupId != null ) {
			c.addEqualTo( "groupId", groupId );
		}
		c.addEqualTo( "active", true );
		
		Query query = QueryFactory.newQuery( RoleResponsibilityActionImpl.class, c );
		Collection<RoleResponsibilityActionImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		if ( coll.size() == 0 ) {
			return null;
		}
		return coll.iterator().next();
	}
}
