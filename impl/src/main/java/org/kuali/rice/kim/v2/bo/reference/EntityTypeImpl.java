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
package org.kuali.rice.kim.v2.bo.reference;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@javax.persistence.Entity
@Table(name="KR_KIM_ENT_TYPE_T")
public class EntityTypeImpl extends KimCodeBase implements EntityType {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ENT_TYP_CD")
	protected String entityTypeCode;
	@Column(name="ENT_TYP_NM")
	protected String entityTypeName;
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.bo.KualiCode#setCode(java.lang.String)
	 */
	public void setCode(String code) {
		setEntityTypeCode(code);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.bo.KualiCode#setName(java.lang.String)
	 */
	public void setName(String name) {
		setEntityTypeName(name);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.bo.Summarizable#getCode()
	 */
	public String getCode() {
		return getEntityTypeCode();
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.bo.Summarizable#getName()
	 */
	public String getName() {
		return getEntityTypeName();
	}

	/**
	 * @return the entityTypeCode
	 */
	public String getEntityTypeCode() {
		return this.entityTypeCode;
	}

	/**
	 * @param entityTypeCode the entityTypeCode to set
	 */
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}

	/**
	 * @return the entityTypeName
	 */
	public String getEntityTypeName() {
		return this.entityTypeName;
	}

	/**
	 * @param entityTypeName the entityTypeName to set
	 */
	public void setEntityTypeName(String entityTypeName) {
		this.entityTypeName = entityTypeName;
	}

}
