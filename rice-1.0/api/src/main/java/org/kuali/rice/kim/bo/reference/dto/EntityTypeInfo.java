/*
 * Copyright 2007-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo.reference.dto;

import org.kuali.rice.kim.bo.reference.EntityType;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class EntityTypeInfo extends KimCodeInfoBase implements EntityType {

	private static final long serialVersionUID = 1L;

	public EntityTypeInfo() {
		super();
	}

	public EntityTypeInfo(EntityType entityType) {
		super(entityType);
	}

	/**
	 * @return the entityTypeCode
	 */
	public String getEntityTypeCode() {
		return getCode();
	}

	/**
	 * @param entityTypeCode the entityTypeCode to set
	 */
	public void setEntityTypeCode(String entityTypeCode) {
		setCode( entityTypeCode );
	}

	/**
	 * @return the entityTypeName
	 */
	public String getEntityTypeName() {
		return getName();
	}

	/**
	 * @param entityTypeName the entityTypeName to set
	 */
	public void setEntityTypeName(String entityTypeName) {
		setName(entityTypeName);
	}

}
