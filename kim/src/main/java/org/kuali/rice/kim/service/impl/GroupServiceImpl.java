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

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.Principal;
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
     * @see org.kuali.rice.kim.service.GroupService#getAllGroupNames()
     */
    public List<String> getAllGroupNames() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getAllGroups()
     */
    public List<Group> getAllGroups() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getGroupMemberNames(java.lang.String)
     */
    public List<String> getGroupMemberNames(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getGroupMembers(java.lang.String)
     */
    public List<Group> getGroupMembers(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getGroupNamesWithAttributes(java.util.Map)
     */
    public List<String> getGroupNamesWithAttributes(Map<String, String> groupAttributes) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getGroupsWithAttributes(java.util.Map)
     */
    public List<Group> getGroupsWithAttributes(Map<String, String> groupAttributes) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getPersonMemberIds(java.lang.String)
     */
    public List<Long> getPersonMemberIds(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getPersonMembers(java.lang.String)
     */
    public List<Person> getPersonMembers(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getPrincipalMemberNames(java.lang.String)
     */
    public List<String> getPrincipalMemberNames(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getPrincipalMembers(java.lang.String)
     */
    public List<Principal> getPrincipalMembers(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getRoleNamesForGroup(java.lang.String)
     */
    public List<String> getRoleNamesForGroup(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getRolesForGroup(java.lang.String)
     */
    public List<Role> getRolesForGroup(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#hasAttributes(java.lang.String, java.util.Map)
     */
    public boolean hasAttributes(String groupName, Map<String, String> groupAttributes) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return false;
    }
}
