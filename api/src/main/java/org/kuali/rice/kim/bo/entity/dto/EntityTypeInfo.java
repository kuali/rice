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
package org.kuali.rice.kim.bo.entity.dto;

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

import org.kuali.rice.kim.bo.reference.EntityType;

/**
 * Contains entity type information.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EntityTypeInfo extends KimInactivatableInfo implements EntityType {

	private static final long serialVersionUID = 1L;
	
	private String entityTypeCode;
	private String entityTypeName;

	/**
	 * Gets the entity type code.
	 * 
	 * @return type code
	 * @see org.kuali.rice.kim.bo.reference.EntityType#getEntityTypeCode()
	 */
	public String getEntityTypeCode() {
		return unNullify(this.entityTypeCode);
	}
	
	/**
	 * Sets the entity type code.
	 * 
	 * @param entityTypeCode type code
	 */
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}

	/**
	 * Gets the entity type name.
	 * 
	 * @return type name
	 * @see org.kuali.rice.kim.bo.reference.EntityType#getEntityTypeName()
	 */
	public String getEntityTypeName() {
		return unNullify(this.entityTypeName);
	}
	
	/**
	 * Sets the entity type name.
	 * 
	 * @param entityTypeName type name
	 */
	public void setEntityTypeName(String entityTypeName) {
		this.entityTypeName = entityTypeName;
	}

	/** {@inheritDoc} */
	public String getCode() {
		return this.getEntityTypeCode();
	}
	
	/** {@inheritDoc} */
	public void setCode(String code) {
		this.setEntityTypeCode(code);
	}

	/** {@inheritDoc} */
	public String getName() {
		return this.getEntityTypeName();
	}
	
	/** {@inheritDoc} */
	public void setName(String name) {
		this.setEntityTypeName(name);
	}
}
