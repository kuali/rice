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
package org.kuali.rice.kim.v2.bo.entity;

import java.util.LinkedHashMap;
import java.util.List;

//import javax.persistence.Column;
//import javax.persistence.Id;
//import javax.persistence.Table;

import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
//@javax.persistence.Entity
//@Table(name="KR_KIM_ENTITY_T")
public class EntityImpl extends PersistableBusinessObjectBase implements Entity, Inactivateable {

    private static final long serialVersionUID = 1L;

//	@Id
//	@Column(name="ENTITY_ID")
	protected String entityId;

    protected boolean active;
    
    protected List<EntityEntityType> entityTypes;
	
	/**
	 * @return the entityId
	 */
	public String getEntityId() {
		return this.entityId;
	}
	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	/**
	 * @return the entityTypes
	 */
	public List<EntityEntityType> getEntityTypes() {
		return this.entityTypes;
	}
	/**
	 * @param entityTypes the entityTypes to set
	 */
	public void setEntityTypes(List<EntityEntityType> entityTypes) {
		this.entityTypes = entityTypes;
	}
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return this.active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap lhm = new  LinkedHashMap();
		lhm.put("entityId", entityId);
		return lhm;
	}
}
