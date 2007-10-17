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

import org.kuali.workflow.role.QualifiedRole;
import org.kuali.workflow.role.Role;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RoleDAO {

	public Role findRoleById(Long roleId);

	public Role findRoleByName(String roleName);

	public QualifiedRole findQualifiedRoleById(Long qualifiedRoleId);

	public Collection findQualifiedRolesForRole(String roleName);

	public void save(Role role);

	public void save(QualifiedRole qualifiedRole);


}
