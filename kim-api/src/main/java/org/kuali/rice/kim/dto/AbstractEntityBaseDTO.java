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

import java.io.Serializable;

/**
 * This base abstract class contains the common elements between an Entity and Person DTO.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class AbstractEntityBaseDTO implements Serializable {
    private Long id;
    private Long entityTypeId;
    private EntityTypeDTO entityTypeDto;

    public Long getId() {
        return id; 
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the entityTypeId
     */
    public Long getEntityTypeId() {
        return this.entityTypeId;
    }

    /**
     * @param entityTypeId the entityTypeId to set
     */
    public void setEntityTypeId(Long entityTypeId) {
        this.entityTypeId = entityTypeId;
    }

    /**
     * @return the entityTypeDto
     */
    public EntityTypeDTO getEntityTypeDto() {
        return this.entityTypeDto;
    }

    /**
     * @param entityTypeDto the entityTypeDto to set
     */
    public void setEntityType(EntityTypeDTO entityTypeDto) {
        this.entityTypeDto = entityTypeDto;
    }
}