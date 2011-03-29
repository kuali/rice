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

import org.kuali.rice.core.framework.persistence.jpa.criteria.Criteria;
import org.kuali.rice.core.framework.persistence.jpa.criteria.QueryByCriteria;
import org.kuali.rice.kim.bo.role.KimResponsibility;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.dao.KimResponsibilityDao;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimResponsibilityDaoJpa implements KimResponsibilityDao {

    @PersistenceContext(unitName="kim-unit")
    private EntityManager entityManager;
    
	/**
	 * @see org.kuali.rice.kim.dao.KimResponsibilityDao#getRoleIdsForResponsibilities(java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRoleIdsForResponsibilities(Collection<? extends KimResponsibility> responsibilities) {
		List<String> responsibilityIds = new ArrayList<String>( responsibilities.size() );
		for ( KimResponsibility kp : responsibilities ) {
			responsibilityIds.add( kp.getResponsibilityId() );
		}
		Criteria c = new Criteria(RoleResponsibilityImpl.class.getName());
		c.in( KimConstants.PrimaryKeyConstants.RESPONSIBILITY_ID, responsibilityIds );
		c.eq( KNSPropertyConstants.ACTIVE, true );
		
		//Query query = QueryFactory.newQuery( RoleResponsibilityImpl.class, c, true );
		
		ArrayList<RoleResponsibilityImpl> coll = (ArrayList<RoleResponsibilityImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		List<String> roleIds = new ArrayList<String>( coll.size() );
		for ( RoleResponsibilityImpl rp : coll ) {
			roleIds.add( rp.getRoleId() );
		}
		return roleIds;
	}
	
	/**
	 * @see org.kuali.rice.kim.dao.KimResponsibilityDao#getRoleIdsForResponsibility(org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl)
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRoleIdsForResponsibility( KimResponsibility responsibility) {
		Criteria c = new Criteria(RoleResponsibilityImpl.class.getName());
		c.eq( KimConstants.PrimaryKeyConstants.RESPONSIBILITY_ID, responsibility.getResponsibilityId() );
		c.eq( KNSPropertyConstants.ACTIVE, true );
		
		ArrayList<RoleResponsibilityImpl> coll = (ArrayList<RoleResponsibilityImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		List<String> roleIds = new ArrayList<String>( coll.size() );
		for ( RoleResponsibilityImpl rp : coll ) {
			roleIds.add( rp.getRoleId() );
		}
		return roleIds;
	}

	/**
	 * @see org.kuali.rice.kim.dao.KimResponsibilityDao#getResponsibilityAction(String, String)
	 */
	@SuppressWarnings("unchecked")
	public RoleResponsibilityActionImpl getResponsibilityAction(String roleId, String responsibilityId, String roleMemberId ) {
		if ( roleMemberId == null || responsibilityId == null ) {
			return null;
		}
		
		Criteria c = new Criteria(RoleResponsibilityActionImpl.class.getName());
		c.eq( "roleResponsibility.responsibilityId", responsibilityId );
		c.eq( "roleResponsibility.roleId", roleId );
		c.eq( "roleResponsibility.active", true );
		Criteria idCriteria = new Criteria(RoleResponsibilityActionImpl.class.getName(), c.getAlias());
		idCriteria.eq( KIMPropertyConstants.RoleMember.ROLE_MEMBER_ID, roleMemberId );
		// also handle when roleMemberId is "*" in table
		Criteria starCrit = new Criteria(RoleResponsibilityActionImpl.class.getName(), c.getAlias());
		starCrit.eq( KIMPropertyConstants.RoleMember.ROLE_MEMBER_ID, "*" );
		idCriteria.or( starCrit );
		c.and( idCriteria );
		
		ArrayList<RoleResponsibilityActionImpl> coll = (ArrayList<RoleResponsibilityActionImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
		RoleResponsibilityActionImpl result = null;
		if ( coll.size() == 0 ) {
			// ok, an exact match failed, attempt to pull where the role_rsp_id is "*"
			// but, at this point, we would require an exact match on the role member ID
			c = new Criteria(RoleResponsibilityActionImpl.class.getName());
			c.eq( "roleResponsibilityId", "*" );
			c.eq( KIMPropertyConstants.RoleMember.ROLE_MEMBER_ID, roleMemberId );
			coll = (ArrayList<RoleResponsibilityActionImpl>) new QueryByCriteria(entityManager, c).toQuery().getResultList();
			if ( coll.size() != 0 ) {
				result = coll.iterator().next();
			}
		} else {
			result = coll.iterator().next();
		}
		return result;
	}
	
	public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
