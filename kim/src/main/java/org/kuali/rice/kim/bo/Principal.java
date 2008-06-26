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

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.core.util.TypedArrayList;
import org.kuali.rice.kim.dto.PersonDTO;
import org.kuali.rice.kim.dto.PrincipalDTO;

/**
 * This class represents the Principal data structure
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@javax.persistence.Entity
@Table(name="KIM_PRINCIPALS_T")
public class Principal extends AbstractEntityBase implements java.security.Principal {
    private static final long serialVersionUID = -5021417605671339853L;
    @Column(name="ENTITY_ID")
	private Long entityId;
    @Column(name="NAME")
	private String name;

    //@ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	//@JoinColumn(name="ENTITY_ID", insertable=false, updatable=false)
	@Transient
	private Entity entity;
    //@ManyToMany(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})@JoinTable(name="KIM_GROUPS_PRINCIPALS_T",
	//           joinColumns=@JoinColumn(name="PRINCIPAL_ID"),
	//           inverseJoinColumns=@JoinColumn(name="GROUP_ID"))
	@Transient
	private ArrayList<Group> groups;
    //@ManyToMany(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})@JoinTable(name="KIM_ROLES_PRINCIPALS_T",
	//           joinColumns=@JoinColumn(name="PRINCIPAL_ID"),
	//           inverseJoinColumns=@JoinColumn(name="ROLE_ID"))
	@Transient
	private ArrayList<Role> roles;

    //this list is used for rendering the UI appropriately using the maintenance document framework
    //this can be considered essentially a form object
    private ArrayList<RoleQualificationForPrincipal> roleQualificationsForPrincipal;

    //this list is what actually gets persisted for principal role qualifications
    //@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
    //       targetEntity=org.kuali.rice.kim.bo.PrincipalQualifiedRoleAttribute.class, mappedBy="ERROR: See log")
	@Transient
	private ArrayList<PrincipalQualifiedRoleAttribute> principalQualifiedRoleAttributes;

    /**
     * Constructs a Principal instance.
     */
    public Principal() {
        this.groups = new TypedArrayList(Group.class);
        this.roles = new TypedArrayList(Role.class);
        this.roleQualificationsForPrincipal = new TypedArrayList(RoleQualificationForPrincipal.class);
        this.principalQualifiedRoleAttributes = new TypedArrayList(PrincipalQualifiedRoleAttribute.class);
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

    /**
     * @return the roleQualificationsForPrincipal
     */
    public ArrayList<RoleQualificationForPrincipal> getRoleQualificationsForPrincipal() {
        return this.roleQualificationsForPrincipal;
    }

    /**
     * @param roleQualificationsForPrincipal the roleQualificationsForPrincipal to set
     */
    public void setRoleQualificationsForPrincipal(ArrayList<RoleQualificationForPrincipal> roleQualificationsForPrincipal) {
        this.roleQualificationsForPrincipal = roleQualificationsForPrincipal;
    }

    /**
     * @return the principalQualifiedRoleAttributes
     */
    public ArrayList<PrincipalQualifiedRoleAttribute> getPrincipalQualifiedRoleAttributes() {
        return this.principalQualifiedRoleAttributes;
    }

    /**
     * @param principalQualifiedRoleAttributes the principalQualifiedRoleAttributes to set
     */
    public void setPrincipalQualifiedRoleAttributes(ArrayList<PrincipalQualifiedRoleAttribute> principalQualifiedRoleAttributes) {
        this.principalQualifiedRoleAttributes = principalQualifiedRoleAttributes;
    }

    /**
     *
     * This method creates a DTO from a BO
     *
     * @param principal BO
     * @return PrincipalDTO
     */
    public static PrincipalDTO toDTO(final Principal principal) {
        final PrincipalDTO dto = new PrincipalDTO();
        return fillInDTO(principal, dto);
    }

    protected static PrincipalDTO fillInDTO(final Principal principal,
        final PrincipalDTO dto) {
      dto.setId(principal.getId());
      dto.setName(principal.getName());
      dto.setEntityTypeId(principal.getEntityId());
      // TODO lindholm dto.setEntityType(EntityType.toDTO(principal.getEntityType())); OJB mapping can't resolve entityTypeId ???

      return dto;
    }

    /**
     *
     * This method creates a Person DTO from a principal BO
     *
     * @param person BO
     * @return DTO
     */
    public static PersonDTO toPersonDTO(final Principal person) {
      final PersonDTO dto = new PersonDTO();
      dto.setId(person.getId());
      dto.setEntityTypeId(person.getEntityId());
      // TODO lindholm dto.setEntityType(EntityType.toDTO(person.getEntityType())); OJB mapping can't resolve entityTypeId ???
      dto.setPersonAttributesDtos(null); // TODO lindholm need filling in

      return dto;
    }
}

