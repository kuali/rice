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

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;

/**
 * This is the default KIM PersonService implementation that is provided by Rice.  This will mature over time as the KIM 
 * component is developed.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PersonServiceImpl implements PersonService {

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#getAttributeValue(java.lang.String, java.lang.String)
     */
    public String getAttributeValue(String personUserName, String attributeName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return "";
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#getPersonUserNames(java.util.Map)
     */
    public List<String> getPersonUserNames(Map<String, String> personAttributes) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<String>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#getPersons(java.util.Map)
     */
    public List<Person> getPersons(Map<String, String> personAttributes) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return new ArrayList<Person>();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#hasAttributes(java.lang.String, java.util.Map)
     */
    public boolean hasAttributes(String personUserName, Map<String, String> personAttributes) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return false;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#hasPermission(java.lang.String, java.lang.String)
     */
    public boolean hasPermission(String personUserName, String permissionName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return false;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#hasQualifiedPermission(java.lang.String, java.lang.String, java.util.Map)
     */
    public boolean hasQualifiedPermission(String personUserName, String permissionName,
	    Map<String, String> qualifiedRoleAttributes) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return false;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#hasQualifiedRole(java.lang.String, java.lang.String, java.util.Map)
     */
    public boolean hasQualifiedRole(String personUserName, String roleName, Map<String, String> personAttributes) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return false;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#hasRole(java.lang.String, java.lang.String)
     */
    public boolean hasRole(String personUserName, String roleName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return false;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#isMemberOfGroup(java.lang.String, java.lang.String)
     */
    public boolean isMemberOfGroup(String personUserName, String groupName) {
	// TODO pberres - THIS METHOD NEEDS JAVADOCS
	return false;
    }

}
