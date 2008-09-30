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
package org.kuali.rice.kim.bo.role;

import java.util.List;

import org.kuali.rice.kns.bo.Inactivateable;

/**
 * A role definition within KIM.  Roles are assigned to principals and/or groups.  
 * Roles have permissions.  Authorization checks should be done against the permissions
 * provided by membership in a role.
 * 
 * Roles may contain other roles.  Membership in the higher-level role implies membership
 * in all the contained roles (which may be nested to multiple levels) and all the permissions 
 * attached to any of those roles.
 * 
 * If a role is qualified, only the qualifier of the highest-level role are considered.
 * That is, if the person is directly assigned to a role and one of the roles it contains, the lower
 * level assignment is ignored in favor of the higher-level assignment.
 * 
 * @author Kuali Rice Team (kuali-rice@googleRoles.com)
 *
 */
public interface KimRole extends Inactivateable {

	/** Unique identifier for this role. */
	String getRoleId();
	
	/** Namespace for this role - identifies the system/module to which this role applies */
	String getNamespaceCode();
	
	/** Name for this role.  This value will be seen by the users. */
	String getRoleName();
	
	/** Verbose description of the role and functionally what permissions it implies. */
	String getRoleDescription();
	
	/** Type identifier for this role.  This will control what additional attributes are available */
	String getKimTypeId();
	
//	/** List of principals who have been assigned to this role. */
//	List<RolePrincipal> getMemberPrincipals();
//
//	/** List of groups who have been assigned to this role.  
//	 * All principals in these groups (and their contained groups) are implicitly assigned to this role. */
//	List<RoleGroup> getMemberGroups();
//
//	/** The list of all roles which are contained within this role.  This role implies all the permissions
//	 * of the contained roles but not all the members.  That is, membership in a contained role
//	 * does not imply membership in this role.
//	 */
//	List<RoleRelationship> getContainedRoles();
}
