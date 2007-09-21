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

import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupAttribute;
import org.kuali.rice.kim.bo.Permission;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.RoleAttribute;

/**
 * This is the service API for accessing KIM services.  This contract should be used by all Kuali software that 
 * needs to leverage identity management features. 
 * 
 * @author Aaron Godert (agodert at cornell dot edu)
 */
public interface KIMService {
    public List<Person> getPersons(Role role);
    public List<Role> getRoles();
    public List<RoleAttribute> getQualifiedRoles(Person person, Role role);
    public List<Permission> getPermissions(Role role);
    public List<Group> getGroups(Role role);
    public List<GroupAttribute> getQualifiedGroups(Person person, Group role);
    
    
}
