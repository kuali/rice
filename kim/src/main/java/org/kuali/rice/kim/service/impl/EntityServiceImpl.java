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

import org.kuali.rice.kim.bo.Entity;
import org.kuali.rice.kim.bo.NamespaceDefaultAttribute;
import org.kuali.rice.kim.dto.EntityAttributeDTO;
import org.kuali.rice.kim.dto.EntityDTO;
import org.kuali.rice.kim.service.EntityService;
import org.kuali.rice.kim.service.NamespaceService;

/**
 * This is a description of what this class does - KFS_Developer_1 don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class EntityServiceImpl implements EntityService {

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.EntityService#getAllEntityIds()
     */
    public List<Long> getAllEntityIds() {
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.EntityService#getAllEntitys()
     */
    public List<EntityDTO> getAllEntitys() {
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.EntityService#getAttributeValue(java.lang.Long, java.lang.String, java.lang.String)
     */
    public String getAttributeValue(Long entityId, String attributeName, String namespaceName) {
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.EntityService#getEntityAttributesByNamespace(java.lang.Long)
     */
    public HashMap<String, List<EntityAttributeDTO>> getEntityAttributesByNamespace(Long entityId) {
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.EntityService#getEntityAttributesForNamespace(java.lang.Long, java.lang.String)
     */
    public HashMap<String, EntityAttributeDTO> getEntityAttributesForNamespace(Long entityId, String namespaceName) {
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.EntityService#getEntityIdsWithAttributes(java.util.Map, java.lang.String)
     */
    public List<Long> getEntityIdsWithAttributes(Map<String, String> entityAttributes, String namespaceName) {
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.EntityService#getEntitysWithAttributes(java.util.Map, java.lang.String)
     */
    public List<EntityDTO> getEntitysWithAttributes(Map<String, String> entityAttributes, String namespaceName) {
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.EntityService#hasAttributes(java.lang.Long, java.util.Map, java.lang.String)
     */
    public boolean hasAttributes(Long entityId, Map<String, String> entityAttributes, String namespaceName) {
        return false;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.EntityService#isMemberOfGroup(java.lang.Long, java.lang.String)
     */
    public boolean isMemberOfGroup(Long entityId, String groupName) {
        return false;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.EntityService#getNamespaceAttributes(org.kuali.rice.kim.bo.Entity)
     */
    public Map<String, NamespaceDefaultAttribute> getNamespaceAttributes(Entity entity) {
        //NamespaceService.getNamespaceAttribures(Entity entity);
        return null;
    }

}
