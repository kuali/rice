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
package org.kuali.rice.kim.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * This is a base helper class that encapsulates common fields needed by several "Entity" classes.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@MappedSuperclass
public abstract class AbstractEntityBase extends PersistableBusinessObjectBase implements Serializable {

	@Id
	@Column(name="ID")
	private Long id;
	@Column(name="ENTITY_TYPE_ID")
    private Long entityTypeId;
	@Transient
    private EntityType entityType;

    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
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
     * @return the entityType
     */
    public EntityType getEntityType() {
        return this.entityType;
    }

    /**
     * @param entityType the entityType to set
     */
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
}
