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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.dto.PersonAttributeDTO;
import org.kuali.rice.kim.dto.PersonDTO;

/**
 * Service API for accessing KIM Person services.  This contract should be used by all 
 * Kuali software which needs to leverage identity management features that require fine-grained
 * Person attributes. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface PersonService {
    /**
     * KIM service API method that returns a complete collection of Person objects for the application.
     * 
     * @return         List of Person objects for the application
     * 
     */
    public List<PersonDTO> getAllPersons();
    
    /**
     * KIM service API method that returns a complete collection of Person ids for the 
     * application.
     * 
     * @return         List of Person ids for the application
     * 
     */
    public List<Long> getAllPersonIds();
    
    /**
     * KIM Person service API method that determines if a given user is member of a given
     * group.
     * 
     * @param   personId             personId uniquely identifying a KIM Person
     * @param   groupName            name identifying a unique Group
     * @return                       boolean indicating if Person is member of Group
     * 
     */
    public boolean isMemberOfGroup(Long personId, String groupName);
    
    /**
     * KIM Person service API method that retrieves all Person Attribute DTOs for a given 
     * person, and for a given Namespace.
     * 
     * @param personId               personId uniquely identifying a KIM Person
     * @param namespaceName          the associated namespace to scope the attributes to
     * @return                       A HashMap - the key being the name of the attribute, the 
     *                               value being the actual PersonAttributeDTO object
     */
    public HashMap<String, PersonAttributeDTO> getPersonAttributesForNamespace(Long personId, String namespaceName);
    
    /**
     * KIM Person service API method that retrieves all Person Attribute DTOs for a given 
     * person, grouping them by Namespace.
     * 
     * @param personId               personId uniquely identifying a KIM Person
     * @return                       A HashMap - the key being the name of the Namespace, the 
     *                               value being a List of the actual PersonAttributeDTO objects
     */
    public HashMap<String, List<PersonAttributeDTO>> getPersonAttributesByNamespace(Long personId);
    
    /**
     * KIM Person service API method that determines if a given user possesses all given Person
     * attributes.
     * 
     * @param   personId             personId uniquely identifying a KIM Person
     * @param   personAttributes     Map<String, String> of role attribute name/value pairs
     *                               to match a Person
     * @param   namespaceName        the associated namespace to scope the attributes to
     * @return                       boolean indicating if Person possesses all given attributes
     * 
     */
    public boolean hasAttributes(Long personId, Map<String, String> personAttributes, String namespaceName);
    
    /**
     * KIM Person service API method that retrieves the value for a given person attribute.
     * 
     * @param   personId             Person id uniquely identifying a KIM Person
     * @param   attributeName        Name of attribute
     * @param   namespaceName        The associated namespace to scope the attribute to 
     * @return                       String value associated with attribute
     * 
     */
    public String getAttributeValue(Long personId, String attributeName, String namespaceName);
    
    /**
     * KIM Person service API method that returns all Person objects matching all given Person
     * attributes.
     * 
     * @param   personAttributes     Map<String, String> of role attribute name/value pairs
     *                               to qualify a Person
     * @param   namespaceName        The associated namespace to scope the attributes to
     * @return                       boolean indicating if Person possesses all given Role attributes
     * 
     */
    public List<PersonDTO> getPersonsWithAttributes(Map<String, String> personAttributes, String namespaceName);
    
    /**
     * KIM Person service API method that returns associated List of usernames for all Person objects
     * matching all given Person attributes.
     * 
     * @param   personAttributes     Map<String, String> of role attribute name/value pairs
     *                               to qualify a Person
     * @param   namespaceName        The associated namespace to scope the attribute to
     * @return                       boolean indicating if Person possesses all given Role attributes
     * 
     */
    public List<Long> getPersonIdsWithAttributes(Map<String, String> personAttributes, String namespaceName);
    
}
