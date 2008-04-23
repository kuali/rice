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
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.TypedArrayList;

/**
 * This class represents the Principal data structure
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Principal extends PersistableBusinessObjectBase implements java.security.Principal {
    private static final long serialVersionUID = -5021417605671339853L;
    private Long id;
    private Long entityId;
    private String name;
    
    
    
   private Entity entity; 
   private EntityType entityType;
   private ArrayList<Group> groups;
   private ArrayList<Role> roles;
   

    /**
     * Constructs a NotificationSender.java instance.
     */
    public Principal() {
        groups = new TypedArrayList(Group.class);
        roles = new TypedArrayList(Role.class);
    }

    /**
     * Gets the id attribute. 
     * @return Returns the id.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the id attribute value.
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("name", getName());
        propMap.put("entityId", getEntityId());
        propMap.put("entity", getEntity());
        propMap.put("entityType", getEntityType());        
        return propMap;
    }

    /**
     * This overridden method ...
     * 
     * @see java.security.Principal#getName()
     */
    public String getName() {
        // TODO Chris - THIS METHOD NEEDS JAVADOCS
        return name;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the entityId
     */
    public Long getEntityId() {
        return this.entityId;
    }

    /**
     * @param entityId the entityId to set
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * @return the entity
     */
    public EntityType getEntityType() {
        return this.entityType;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    /**
     * @return the groups
     */
    public ArrayList<Group> getGroups() {
        return this.groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    /**
     * @return the roles
     */
    public ArrayList<Role> getRoles() {
        return this.roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(ArrayList<Role> roles) {
        this.roles = roles;
    }

    /**
     * @return the entity
     */
    public Entity getEntity() {
        return this.entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    
    
   
}