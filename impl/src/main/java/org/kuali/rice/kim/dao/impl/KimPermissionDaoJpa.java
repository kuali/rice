/*
 * Copyright 2008 The Kuali Foundation
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
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.role.impl.RolePermissionImpl;
import org.kuali.rice.kim.dao.KimPermissionDao;
import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria;;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimPermissionDaoJpa implements KimPermissionDao {

    @PersistenceContext(unitName="kim-unit")
    private EntityManager entityManager;
    
	/**
	 * @see org.kuali.rice.kim.dao.KimPermissionDao#getRoleIdsForPermissions(java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRoleIdsForPermissions(Collection<? extends KimPermission> permissions) {
		if ( permissions.isEmpty() ) {
			return new ArrayList<String>(0);
		}
		List<String> permissionIds = new ArrayList<String>( permissions.size() );
		for ( KimPermission kp : permissions ) {
			permissionIds.add( kp.getPermissionId() );
		}
		Criteria c = new Criteria(RolePermissionImpl.class.getName());
		c.in( "permissionId", permissionIds );
		c.eq( "active", true );
		
		ArrayList<RolePermissionImpl> coll = (ArrayList<RolePermissionImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		List<String> roleIds = new ArrayList<String>( coll.size() );
		for ( RolePermissionImpl rp : coll ) {
			roleIds.add( rp.getRoleId() );
		}
		return roleIds;
	}

	public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
