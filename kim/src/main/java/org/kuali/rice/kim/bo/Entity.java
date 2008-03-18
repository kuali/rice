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

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.TypedArrayList;

/**
 * An Entity represents a specific instance of a person, process, company, system, etc in the system.  An Entity 
 * has meta-data that hangs off of it.  User XYZ would be represented in the system as an Entity of type Person. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Entity extends PersistableBusinessObjectBase {
	private static final long serialVersionUID = -1207463934478758540L;
	private Long id;
	private Long entityTypeId;
	
	private EntityType entityType;
	private ArrayList<EntityAttribute> entityAttributes;
	private ArrayList<Principal> principals;
	
	
	public Entity() {
	    this.entityAttributes = new TypedArrayList(EntityAttribute.class);
	    this.principals = new TypedArrayList(Principal.class);
	}
	
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
    
    /**
     * @return the entityAttributes
     */
    public ArrayList<EntityAttribute> getEntityAttributes() {
        return this.entityAttributes;
    }

    /**
     * @param entityAttributes the entityAttributes to set
     */
    public void setEntityAttributes(ArrayList<EntityAttribute> entityAttributes) {
        this.entityAttributes = entityAttributes;
    }

    protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("entityType", getEntityType());
        return propMap;
	}

	public void refresh() {
		// not doing this unless we need it
	}

    /**
     * @return the principals
     */
    public ArrayList<Principal> getPrincipals() {
        return this.principals;
    }

    /**
     * @param principals the principals to set
     */
    public void setPrincipals(ArrayList<Principal> principals) {
        this.principals = principals;
    }
}
