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
package org.kuali.rice.kim.impl.permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kuali.rice.core.framework.persistence.jpa.criteria.Criteria;
import org.kuali.rice.core.framework.persistence.jpa.criteria.QueryByCriteria;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.impl.role.RolePermissionBo;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PermissionDaoJpa implements PermissionDao {

	@SuppressWarnings("unchecked")
	public List<String> getRoleIdsForPermissions(Collection<Permission> permissions) {
		if ( permissions.isEmpty() ) {
			return new ArrayList<String>(0);
		}
		List<String> permissionIds = new ArrayList<String>( permissions.size() );
		for ( Permission kp : permissions ) {
			permissionIds.add( kp.getId() );
		}
		Criteria c = new Criteria(RolePermissionBo.class.getName());
		c.in( "permissionId", permissionIds );
		c.eq( "active", true );
		
		ArrayList<RolePermissionBo> coll = (ArrayList<RolePermissionBo>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		List<String> roleIds = new ArrayList<String>( coll.size() );
		for ( RolePermissionBo rp : coll ) {
			roleIds.add( rp.getRoleId() );
		}
		return roleIds;
	}
	
    @PersistenceContext(unitName="kim-unit")
    private EntityManager entityManager;
    
	public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


}
