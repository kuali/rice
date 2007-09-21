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

import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupAttribute;
import org.kuali.rice.kim.bo.Permission;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.RoleAttribute;
import org.kuali.rice.kim.service.KIMService;

/**
 * This is the default implementation that is provided by Rice.  This will mature over time as the KIM 
 * component is developed.
 * 
 * @author Aaron Godert (agodert at cornell dot edu)
 */
public class KIMServiceImpl implements KIMService {
    public List<Person> getPersons(Role role)
    {
	return null;
    }
    public List<Role> getRoles()
    {
	return null;
    }
    public List<RoleAttribute> getQualifiedRoles(Person person, Role role)
    {
	return null;
    }
    public List<Permission> getPermissions(Role role)
    {
	return null;
    }
    public List<Group> getGroups(Role role)
    {
	return null;
    }
    public List<GroupAttribute> getQualifiedGroups(Person person, Group role)
    {
	return null;
    }   
}
