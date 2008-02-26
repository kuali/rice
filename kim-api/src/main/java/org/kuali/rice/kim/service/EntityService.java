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

import org.kuali.rice.kim.dto.EntityAttributeDTO;
import org.kuali.rice.kim.dto.EntityDTO;

/**
 * Service API for accessing KIM Entity services.  This contract should be used by all 
 * Kuali software which needs to leverage identity management features that require fine-grained
 * Entity attributes. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface EntityService {
    /**
     * KIM service API method that returns a complete collection of Entity objects for the application.
     * 
     * @return         List of Entity objects for the application
     * 
     */
    public List<EntityDTO> getAllEntitys();
    
    /**
     * KIM service API method that returns a complete collection of Entity ids for the 
     * application.
     * 
     * @return         List of Entity ids for the application
     * 
     */
    public List<Long> getAllEntityIds();
    
    /**
     * KIM Entity service API method that determines if a given user is member of a given
     * group.
     * 
     * @param   entityId             entityId uniquely identifying a KIM Entity
     * @param   groupName            name identifying a unique Group
     * @return                       boolean indicating if Entity is member of Group
     * 
     */
    public boolean isMemberOfGroup(Long entityId, String groupName);
    
    /**
     * KIM Entity service API method that retrieves all Entity Attribute DTOs for a given 
     * entity, and for a given Namespace.
     * 
     * @param entityId               entityId uniquely identifying a KIM Entity
     * @param namespaceName          the associated namespace to scope the attributes to
     * @return                       A HashMap - the key being the name of the attribute, the 
     *                               value being the actual EntityAttributeDTO object
     */
    public HashMap<String, EntityAttributeDTO> getEntityAttributesForNamespace(Long entityId, String namespaceName);
    
    /**
     * KIM Entity service API method that retrieves all Entity Attribute DTOs for a given 
     * entity, grouping them by Namespace.
     * 
     * @param entityId               entityId uniquely identifying a KIM Entity
     * @return                       A HashMap - the key being the name of the Namespace, the 
     *                               value being a List of the actual EntityAttributeDTO objects
     */
    public HashMap<String, List<EntityAttributeDTO>> getEntityAttributesByNamespace(Long entityId);
    
    /**
     * KIM Entity service API method that determines if a given user possesses all given Entity
     * attributes.
     * 
     * @param   entityId             entityId uniquely identifying a KIM Entity
     * @param   entityAttributes     Map<String, String> of role attribute name/value pairs
     *                               to match a Entity
     * @param   namespaceName        the associated namespace to scope the attributes to
     * @return                       boolean indicating if Entity possesses all given attributes
     * 
     */
    public boolean hasAttributes(Long entityId, Map<String, String> entityAttributes, String namespaceName);
    
    /**
     * KIM Entity service API method that retrieves the value for a given entity attribute.
     * 
     * @param   entityId             Entity id uniquely identifying a KIM Entity
     * @param   attributeName        Name of attribute
     * @param   namespaceName        The associated namespace to scope the attribute to 
     * @return                       String value associated with attribute
     * 
     */
    public String getAttributeValue(Long entityId, String attributeName, String namespaceName);
    
    /**
     * KIM Entity service API method that returns all Entity objects matching all given Entity
     * attributes.
     * 
     * @param   entityAttributes     Map<String, String> of role attribute name/value pairs
     *                               to qualify a Entity
     * @param   namespaceName        The associated namespace to scope the attributes to
     * @return                       boolean indicating if Entity possesses all given Role attributes
     * 
     */
    public List<EntityDTO> getEntitysWithAttributes(Map<String, String> entityAttributes, String namespaceName);
    
    /**
     * KIM Entity service API method that returns associated List of usernames for all Entity objects
     * matching all given Entity attributes.
     * 
     * @param   entityAttributes     Map<String, String> of role attribute name/value pairs
     *                               to qualify a Entity
     * @param   namespaceName        The associated namespace to scope the attribute to
     * @return                       boolean indicating if Entity possesses all given Role attributes
     * 
     */
    public List<Long> getEntityIdsWithAttributes(Map<String, String> entityAttributes, String namespaceName);
    
}
