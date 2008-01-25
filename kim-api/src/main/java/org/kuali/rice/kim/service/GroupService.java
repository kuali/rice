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
package org.kuali.rice.kim.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.Role;

/**
 * Service API for accessing KIM Group services.  This contract should be used by all 
 * Kuali software which needs to leverage identity management features that require fine-grained
 * Group attributes. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface GroupService {
    /**
     * KIM service API method that returns a list of all Groups in the system
     *  
     * @return         List of all Groups in the system
     * 
     */
    public List<Group> getAllGroups();
    
    /**
     * KIM service API method that returns a collection of Groups in the system
     *   
     * @return         List of all Group names in the system
     * 
     */
    public List<String> getAllGroupNames();
    
    /**
     * KIM Group service API method that returns all Principal objects within either a given Group, 
     * or within any nested groups within that Group.
     * 
     * @param   groupName            name identifying Group
     * @return                       unique (i.e. not replicated) List of Person objects
     * 
     */
    public List<Principal> getPrincipalMembers(String groupName);
    
    /**
     * KIM Group service API method that returns all Principal names within either a given Group, 
     * or within any nested groups within that Group.
     * 
     * @param   groupName            name identifying Group
     * @return                       unique (i.e. not replicated) List of Person objects
     * 
     */
    public List<String> getPrincipalMemberNames(String groupName);
    
    /**
     * KIM Group service API method that returns all Person objects within either a given Group, 
     * or within any nested groups within that Group.
     * 
     * @param   groupName            name identifying Group
     * @return                       unique (i.e. not replicated) List of Person objects
     * 
     */
    public List<Person> getPersonMembers(String groupName);
    
    /**
     * KIM Group service API method that returns all Person id within either a given Group, 
     * or within any nested groups within that Group.
     * 
     * @param   groupName            name identifying Group
     * @return                       unique (i.e. not replicated) List of Person user names
     * 
     */
    public List<Long> getPersonMemberIds(String groupName);
    
    /**
     * KIM Group service API method that returns all Group objects that are considered members 
     * of a given group or within any nested Groups of those Groups - this is recursive.
     * 
     * @param   groupName            name identifying Group
     * @return                       List of Group objects considered members of the group
     * 
     */
    public List<Group> getGroupMembers(String groupName);
    
    /**
     * KIM Group service API method that returns all Group names that are considered members 
     * of a given group or within any nested Groups of those Groups - this is recursive.
     * 
     * @param   groupName            name identifying Group
     * @return                       List of Group names considered members of the group
     * 
     */
    public List<String> getGroupMemberNames(String groupName);
    
    /**
     * KIM Group service API method that returns all Role objects associated 
     * with a given Group.
     * 
     * @param   groupName            name identifying Group
     * @return                       List of Role objects associated with the group
     * 
     */
    public List<Role> getRolesForGroup(String groupName);
    
    /**
     * KIM Group service API method that returns all Role names associated 
     * with a given Group.
     * 
     * @param   groupName            name identifying Group
     * @return                       List of Role names associated with the group
     * 
     */
    public List<String> getRoleNamesForGroup(String groupName);
    
    /**
     * KIM Group service API method that determines if a given group possesses all
     * group attributes 
     * 
     * @param   groupName            name identifying Group
     * @param   groupAttributes      Map<String, String> of group attribute name/value pairs
     * @return                       true if the Group possesses all group attributes 
     * 
     */
    public boolean hasAttributes(String groupName, Map<String, String> groupAttributes);
    
    /**
     * KIM Group service API method that returns all Group objects that possess
     * a given set of group attributes 
     * 
     * @param   groupAttributes      Map<String, String> of group attribute name/value pairs
     * @return                       all Group objects that possess set of group attributes 
     * 
     */
    public List<Group> getGroupsWithAttributes(Map<String, String> groupAttributes);
    
    /**
     * KIM Group service API method that returns associated List of names for all Group objects 
     * that possess a given set of group attributes 
     * 
     * @param   groupAttributes      Map<String, String> of group attribute name/value pairs
     * @return                       names of all Group objects that possess set of group attributes 
     * 
     */
    public List<String> getGroupNamesWithAttributes(Map<String, String> groupAttributes);
}
