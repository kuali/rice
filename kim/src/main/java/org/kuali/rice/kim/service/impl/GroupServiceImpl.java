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
import org.kuali.rice.kim.bo.Permission;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.service.GroupService;

/**
 * This is the default KIM GroupService implementation that is provided by Rice.  This will mature over time as the KIM 
 * component is developed.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class GroupServiceImpl implements GroupService {

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getGroupNames(java.lang.String)
     */
    public List<String> getGroupNames(String groupName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<String>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getGroupNames(java.util.Map)
     */
    public List<String> getGroupNames(Map<String, String> groupAttributes) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<String>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getGroups(java.lang.String)
     */
    public List<Group> getGroups(String groupName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<Group>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getGroups(java.util.Map)
     */
    public List<Group> getGroups(Map<String, String> groupAttributes) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<Group>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getPermissionNames(java.lang.String)
     */
    public List<String> getPermissionNames(String groupName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<String>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getPermissions(java.lang.String)
     */
    public List<Permission> getPermissions(String groupName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<Permission>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getPersonUserNames(java.lang.String)
     */
    public List<String> getPersonUsernames(String groupName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<String>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getPersons(java.lang.String)
     */
    public List<Person> getPersons(String groupName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<Person>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getRoleNames(java.lang.String)
     */
    public List<String> getRoleNames(String groupName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<String>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getRoles(java.lang.String)
     */
    public List<Role> getRoles(String groupName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<Role>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#hasAttributes(java.lang.String, java.util.Map)
     */
    public boolean hasAttributes(String groupName, Map<String, String> groupAttributes) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return false;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#hasQualifiedPermission(java.lang.String, java.lang.String, java.util.Map)
     */
    public boolean hasQualifiedPermission(String groupName, String permissionName,
	    Map<String, String> qualifiedRoleAttributes) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return false;
    }

}
