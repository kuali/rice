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
import java.util.HashMap;
import java.util.LinkedHashMap;


import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.TypedArrayList;
import org.kuali.rice.kim.dto.GroupAttributeDTO;
import org.kuali.rice.kim.dto.GroupDTO;
import org.kuali.rice.kim.dto.RoleDTO;

@javax.persistence.Entity
@Table(name="KIM_GROUPS_T")
public class Group extends PersistableBusinessObjectBase {

	private static final long serialVersionUID = 4974576362491778342L;

	@Id
	@Column(name="ID")
	private Long id;
	@Column(name="NAME")
	private String name;
	@Column(name="DESCRIPTION")
	private String description;
	@Column(name="GROUP_TYPE_ID")
	private Long groupTypeId;
	//@ManyToMany(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})@JoinTable(name="KIM_GROUPS_GROUPS_T",
	//           joinColumns=@JoinColumn(name="PARENT_GROUP_ID"),
	//           inverseJoinColumns=@JoinColumn(name="MEMBER_GROUP_ID"))
	@Transient
	private ArrayList<Group> memberGroups;
	//@ManyToMany(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})@JoinTable(name="KIM_GROUPS_GROUPS_T",
	//           joinColumns=@JoinColumn(name="MEMBER_GROUP_ID"),
	//           inverseJoinColumns=@JoinColumn(name="PARENT_GROUP_ID"))
	@Transient
	private ArrayList<Group> parentGroups;
	//@ManyToMany(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})@JoinTable(name="KIM_GROUPS_PRINCIPALS_T",
	//           joinColumns=@JoinColumn(name="GROUP_ID"),
	//           inverseJoinColumns=@JoinColumn(name="PRINCIPAL_ID"))
	@Transient
	private ArrayList<Principal> memberPrincipals;
    //@ManyToMany(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})@JoinTable(name="KIM_ROLES_GROUPS_T",
	//           joinColumns=@JoinColumn(name="GROUP_ID"),
	//           inverseJoinColumns=@JoinColumn(name="ROLE_ID"))
	@Transient
	private ArrayList<Role> roles;
    //@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
    //       targetEntity=org.kuali.rice.kim.bo.GroupAttribute.class, mappedBy="ERROR: See log")
	@Transient
	private ArrayList<GroupAttribute> groupAttributes;
    //@ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	//@JoinColumn(name="GROUP_TYPE_ID", insertable=false, updatable=false)
	@Transient
	private GroupType groupType;
	//@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
    //       targetEntity=org.kuali.rice.kim.bo.GroupQualifiedRoleAttribute.class, mappedBy="ERROR: See log")
	@Transient
	private ArrayList<GroupQualifiedRoleAttribute> qualifiedRoleAttributes;

    //these are essentially form objects only used for rendering
	@Transient
	private ArrayList<GroupAttribute> groupTypeAttributes;
	@Transient
	private ArrayList<GroupAttribute> nonGroupTypeAttributes;
	@Transient
	private ArrayList<RoleQualificationForGroup> roleQualificationsForGroup;

	/**
     * Instantiates a new Group.
     */
	public Group() {
		memberGroups = new TypedArrayList(Group.class);
		memberPrincipals = new TypedArrayList(Principal.class);
		parentGroups = new TypedArrayList(Group.class);
		roles = new TypedArrayList(Role.class);
		groupAttributes = new TypedArrayList(GroupAttribute.class);
		qualifiedRoleAttributes = new TypedArrayList(GroupQualifiedRoleAttribute.class);
        groupTypeAttributes = new TypedArrayList(GroupAttribute.class);
        nonGroupTypeAttributes = new TypedArrayList(GroupAttribute.class);
        roleQualificationsForGroup = new TypedArrayList(RoleQualificationForGroup.class);
	}

	/**
	 * @return the memberGroups
	 */
	public ArrayList<Group> getMemberGroups() {
		return this.memberGroups;
	}

	/**
	 * @param memberGroups
	 *            the memberGroups to set
	 */
	public void setMemberGroups(ArrayList<Group> memberGroups) {
		this.memberGroups = memberGroups;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
		propMap.put("id", getId());
		propMap.put("name", getName());
		propMap.put("description", getDescription());
		propMap.put("groupType name", getGroupType().getName());
		return propMap;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.core.bo.PersistableBusinessObjectBase#refresh()
	 */
	public void refresh() {
		// not implemented unless needed
	}

	/**
	 * @return the groupRoles
	 */
	public ArrayList<Role> getRoles() {
		return this.roles;
	}

	/**
	 * @param groupRoles
	 *            the groupRoles to set
	 */
	public void setRoles(ArrayList<Role> groupRoles) {
		this.roles = groupRoles;
	}

	/**
	 * @return the parentGroups
	 */
	public ArrayList<Group> getParentGroups() {
		return this.parentGroups;
	}

	/**
	 * @param parentGroups
	 *            the parentGroups to set
	 */
	public void setParentGroups(ArrayList<Group> parentGroups) {
		this.parentGroups = parentGroups;
	}

	/**
	 * @return the groupAttributes
	 */
	public ArrayList<GroupAttribute> getGroupAttributes() {
		return this.groupAttributes;
	}

	/**
	 * @param groupAttributes
	 *            the groupAttributes to set
	 */
	public void setGroupAttributes(ArrayList<GroupAttribute> groupAttributes) {
		this.groupAttributes = groupAttributes;
	}

	/**
	 * @return the groupType
	 */
	public GroupType getGroupType() {
		return this.groupType;
	}

	/**
	 * @param groupType
	 *            the groupType to set
	 */
	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
	}

	/**
	 * @return the groupTypeId
	 */
	public Long getGroupTypeId() {
		return this.groupTypeId;
	}

	/**
	 * @param groupTypeId
	 *            the groupTypeId to set
	 */
	public void setGroupTypeId(Long groupTypeId) {
		this.groupTypeId = groupTypeId;
	}

	/**
	 * @return the memberPrincipals
	 */
	public ArrayList<Principal> getMemberPrincipals() {
		return this.memberPrincipals;
	}

	/**
	 * @param memberPrincipals
	 *            the memberPrincipals to set
	 */
	public void setMemberPrincipals(ArrayList<Principal> memberPrincipals) {
		this.memberPrincipals = memberPrincipals;
	}

	/**
	 * @param qualifiedRoleAttributes
	 *            the qualifiedRoleAttributes to set
	 */
	public void setQualifiedRoleAttributes(
			ArrayList<GroupQualifiedRoleAttribute> qualifiedRoleAttributes) {
		this.qualifiedRoleAttributes = qualifiedRoleAttributes;
	}

	/**
	 * @return the qualifiedRoleAttributes
	 */
	public ArrayList<GroupQualifiedRoleAttribute> getQualifiedRoleAttributes() {
		return qualifiedRoleAttributes;
	}
	
	/**
	 * @return the groupTypeAttributes
	 */
	public ArrayList<GroupAttribute> getGroupTypeAttributes() {
		return this.groupTypeAttributes;
	}

	/**
	 * @param groupTypeAttributes the groupTypeAttributes to set
	 */
	public void setGroupTypeAttributes(ArrayList<GroupAttribute> groupTypeAttributes) {
		this.groupTypeAttributes = groupTypeAttributes;
	}

	/**
	 * @return the nonGroupTypeAttributes
	 */
	public ArrayList<GroupAttribute> getNonGroupTypeAttributes() {
		return this.nonGroupTypeAttributes;
	}

	/**
	 * @param nonGroupTypeAttributes the nonGroupTypeAttributes to set
	 */
	public void setNonGroupTypeAttributes(
			ArrayList<GroupAttribute> nonGroupTypeAttributes) {
		this.nonGroupTypeAttributes = nonGroupTypeAttributes;
	}
	
	/**
	 * @return the roleQualificationsForGroup
	 */
	public ArrayList<RoleQualificationForGroup> getRoleQualificationsForGroup() {
		return this.roleQualificationsForGroup;
	}

	/**
	 * @param roleQualificationsForGroup the roleQualificationsForGroup to set
	 */
	public void setRoleQualificationsForGroup(
			ArrayList<RoleQualificationForGroup> roleQualificationsForGroup) {
		this.roleQualificationsForGroup = roleQualificationsForGroup;
	}

	/**
	 *
	 * This method creates a DTO from a BO
	 *
	 * @param group
	 * @return GroupDTO
	 */
	public static GroupDTO toDTO(final Group group) {
		final GroupDTO dto = new GroupDTO();
		fillInDTO(group, dto, false);
		return dto;
	}

	/**
	 *
	 * This method creates a DTO from a BO
	 *
	 * @param group
	 * @param shallowCopy
	 * @return
	 */
	public static GroupDTO toDTO(final Group group, final boolean shallowCopy) {
		final GroupDTO dto = new GroupDTO();
		fillInDTO(group, dto, shallowCopy);
		return dto;
	}

	protected static void fillInDTO(final Group group, final GroupDTO dto,
			final boolean shallowCopy) {
		dto.setDescription(group.getDescription());
		dto.setId(group.getId());
		dto.setName(group.getName());

		final HashMap<String, RoleDTO> roles = new HashMap<String, RoleDTO>();
		for (Role role : group.getRoles()) {
			roles.put(role.getName(), Role.toDTO(role, true));
		}
		dto.setGroupRoleDtos(roles);

		if (!shallowCopy) {
			final HashMap<String, GroupDTO> memberGroups = new HashMap<String, GroupDTO>();
			for (Group memberGroup : group.getMemberGroups()) {
				memberGroups.put(memberGroup.getName(), Group.toDTO(
						memberGroup, true));
			}
			dto.setMemberGroupDtos(memberGroups);

			final HashMap<String, GroupDTO> parentGroups = new HashMap<String, GroupDTO>();
			for (Group parentGroup : group.getMemberGroups()) {
				parentGroups.put(parentGroup.getName(), Group.toDTO(
						parentGroup, true));
			}
			dto.setParentGroupDtos(parentGroups);
		}
		final HashMap<String, GroupAttributeDTO> gas = new HashMap<String, GroupAttributeDTO>();
		for (GroupAttribute ga : group.getGroupAttributes()) {
			gas.put(ga.getAttributeName(), GroupAttribute.toDTO(ga));
		}

	}
}
