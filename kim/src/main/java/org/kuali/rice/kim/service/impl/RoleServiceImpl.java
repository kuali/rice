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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupQualifiedRole;
import org.kuali.rice.kim.bo.Permission;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.PersonQualifiedRole;
import org.kuali.rice.kim.service.RoleService;

/**
 * This is the default KIM RoleService implementation that is provided by Rice.  This will mature over time as the KIM 
 * component is developed.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleServiceImpl implements RoleService {

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getGroupNames(java.lang.String)
     */
    public List<String> getGroupNames(String roleName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<String>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getGroupQualifiedRoles(java.lang.String)
     */
    public List<GroupQualifiedRole> getGroupQualifiedRoles(String roleName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<GroupQualifiedRole>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getGroupQualifiedRoles(java.lang.String, java.util.Map)
     */
    public List<GroupQualifiedRole> getGroupQualifiedRoles(String roleName, Map<String, String> qualifiedRoleAttributes) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<GroupQualifiedRole>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getGroups(java.lang.String)
     */
    public List<Group> getGroups(String roleName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<Group>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPermissionNames(java.lang.String)
     */
    public List<String> getPermissionNames(String roleName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<String>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPermissions(java.lang.String)
     */
    public List<Permission> getPermissions(String roleName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<Permission>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPersonQualifiedRoles(java.lang.String)
     */
    public List<PersonQualifiedRole> getPersonQualifiedRoles(String roleName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<PersonQualifiedRole>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPersonQualifiedRoles(java.lang.String, java.util.Map)
     */
    public List<PersonQualifiedRole> getPersonQualifiedRoles(String roleName, Map<String, String> qualifiedRoleAttributes) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<PersonQualifiedRole>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPersonUserNames(java.lang.String)
     */
    public List<String> getPersonUsernames(String roleName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<String>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPersons(java.lang.String)
     */
    public List<Person> getPersons(String roleName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<Person>();
    }
}
