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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.dto.PersonAttributeDTO;
import org.kuali.rice.kim.dto.PersonDTO;
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
     * @see org.kuali.rice.kim.service.PersonService#getAllPersonIds()
     */
    public List<Long> getAllPersonIds() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#getAllPersons()
     */
    public List<PersonDTO> getAllPersons() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#getAttributeValue(java.lang.Long, java.lang.String, java.lang.String)
     */
    public String getAttributeValue(Long personId, String attributeName, String namespaceName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#getPersonIdsWithAttributes(java.util.Map, java.lang.String)
     */
    public List<Long> getPersonIdsWithAttributes(Map<String, String> personAttributes, String namespaceName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#getPersonsWithAttributes(java.util.Map, java.lang.String)
     */
    public List<PersonDTO> getPersonsWithAttributes(Map<String, String> personAttributes, String namespaceName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#hasAttributes(java.lang.Long, java.util.Map, java.lang.String)
     */
    public boolean hasAttributes(Long personId, Map<String, String> personAttributes, String namespaceName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return false;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#isMemberOfGroup(java.lang.Long, java.lang.String)
     */
    public boolean isMemberOfGroup(Long personId, String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return false;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#getPersonAttributesByNamespace(java.lang.Long)
     */
    public HashMap<String, List<PersonAttributeDTO>> getPersonAttributesByNamespace(Long personId) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.PersonService#getPersonAttributesForNamespace(java.lang.Long, java.lang.String)
     */
    public HashMap<String, PersonAttributeDTO> getPersonAttributesForNamespace(Long personId, String namespaceName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }
    
    
}
