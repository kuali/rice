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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.dto.EntityDTO;
import org.kuali.rice.kim.dto.GroupDTO;
import org.kuali.rice.kim.dto.PersonDTO;
import org.kuali.rice.kim.dto.PrincipalDTO;
import org.kuali.rice.kim.dto.RoleDTO;
import org.kuali.rice.kim.service.GroupService;

/**
 * This is the default KIM GroupService implementation that is provided by Rice.  This will mature over time as the KIM
 * component is developed.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class GroupServiceImpl implements GroupService {

    /**
     * KIM service API method that returns a list of all Groups in the system
     *
     * @return         List of all Groups in the system
     *
     *
     * @see org.kuali.rice.kim.service.GroupService#getAllGroupNames()
     */
    public List<String> getAllGroupNames() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        final Collection<Group> groups = KNSServiceLocator.getBusinessObjectService().findAll(Group.class);
        final ArrayList<String> names = new ArrayList<String>(groups.size());
        for (Group g : groups) {
            names.add(g.getName());
        }
        return names;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.service.GroupService#getAllGroups()
     */
    public List<GroupDTO> getAllGroups() {
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
    public List<GroupDTO> getGroupMembers(String groupName) {
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
    public List<GroupDTO> getGroupsWithAttributes(Map<String, String> groupAttributes) {
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
    public List<PersonDTO> getPersonMembers(String groupName) {
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
    public List<PrincipalDTO> getPrincipalMembers(String groupName) {
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
    public List<RoleDTO> getRolesForGroup(String groupName) {
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

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getEntityMemberIds(java.lang.String)
     */
    public List<Long> getEntityMemberIds(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getEntityMembers(java.lang.String)
     */
    public List<EntityDTO> getEntityMembers(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }
    
    
}
