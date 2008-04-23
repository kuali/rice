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
package org.kuali.rice.kim.dto;

import java.util.HashMap;

/**
 * This is a Data Transfer Object (DTO) that is used by the service layer.
 *
 * An Entity represents a specific instance of a person, process, company, system, etc in the system.  An Entity
 * has meta-data that hangs off of it.  User XYZ would be represented in the system as an Entity of type Person.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EntityDTO extends AbstractEntityBaseDTO {
    private static final long serialVersionUID = -3136254886317498473L;

    private HashMap<String,EntityAttributeDTO> entityAttributeDtos;

    private HashMap<String,PrincipalDTO> principalDtos;

    /**
     * @return entityAttributeDtos - the key to the HashMap is the name of the attribute, the value
     *                            is the actual DTO object
     */
    public HashMap<String, EntityAttributeDTO> getEntityAttributeDtos() {
        return this.entityAttributeDtos;
    }

    /**
     * @param entityAttributeDtos - the key to the HashMap is the name of the attribute, the value
     *                           is the actual DTO object
     */
    public void setEntityAttributesDtos(HashMap<String, EntityAttributeDTO> entityAttributeDtos) {
        this.entityAttributeDtos = entityAttributeDtos;
    }

    /**
     * @return PrincipalDTO - the key to the HashMap is the name of the principal, the value
     *                            is the actual DTO object
     */
    public HashMap<String, PrincipalDTO> getPrincipalDtos() {
        return this.principalDtos;
    }

    /**
     * @param PrincipalDTOs - the key to the HashMap is the name of the principal, the value
     *                           is the actual DTO object
     */
    public void setPrincipalDtos(HashMap<String, PrincipalDTO> principalDtos) {
        this.principalDtos = principalDtos;
    }
}
