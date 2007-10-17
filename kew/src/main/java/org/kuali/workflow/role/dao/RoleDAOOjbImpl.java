/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package org.kuali.workflow.role.dao;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.workflow.role.QualifiedRole;
import org.kuali.workflow.role.Role;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleDAOOjbImpl extends PersistenceBrokerDaoSupport implements RoleDAO {

	public Role findRoleById(Long roleId) {
		Criteria criteria = new Criteria();
		criteria.addEqualTo("roleId", roleId);
		return (Role)getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(Role.class, criteria));
	}

	public Role findRoleByName(String roleName) {
		Criteria criteria = new Criteria();
		criteria.addEqualTo("name", roleName);
		return (Role)getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(Role.class, criteria));
	}

	public QualifiedRole findQualifiedRoleById(Long qualifiedRoleId) {
		Criteria criteria = new Criteria();
		criteria.addEqualTo("qualifiedRoleId", qualifiedRoleId);
		return (QualifiedRole)getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(QualifiedRole.class, criteria));
	}

	public Collection findQualifiedRolesForRole(String roleName) {
		Criteria criteria = new Criteria();
		criteria.addEqualTo("role.name", roleName);
		return getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(QualifiedRole.class, criteria));
	}

	public void save(Role role) {
		getPersistenceBrokerTemplate().store(role);
	}

	public void save(QualifiedRole qualifiedRole) {
		getPersistenceBrokerTemplate().store(qualifiedRole);
	}


}
