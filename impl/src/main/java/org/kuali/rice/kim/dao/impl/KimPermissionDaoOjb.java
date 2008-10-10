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
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.role.impl.RolePermissionImpl;
import org.kuali.rice.kim.dao.KimPermissionDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimPermissionDaoOjb extends PlatformAwareDaoBaseOjb implements KimPermissionDao {

	/**
	 * @see org.kuali.rice.kim.dao.KimPermissionDao#getRoleIdsForPermissions(java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRoleIdsForPermissions(Collection<KimPermissionImpl> permissions) {
		List<String> permissionIds = new ArrayList<String>( permissions.size() );
		for ( KimPermissionImpl kp : permissions ) {
			permissionIds.add( kp.getPermissionId() );
		}
		Criteria c = new Criteria();
		c.addIn( "permissionId", permissionIds );
		// TODO: add once effective dating in place
		//c.addColumnEqualTo( "active", true );
		
		Query query = QueryFactory.newQuery( RolePermissionImpl.class, c, true );
		Collection<RolePermissionImpl> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
		List<String> roleIds = new ArrayList<String>( coll.size() );
		for ( RolePermissionImpl rp : coll ) {
			roleIds.add( rp.getRoleId() );
		}
		return roleIds;
	}

}
