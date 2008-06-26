/* Copyright 2008 The Kuali Foundation
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

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * A NamespaceDefaultAttribute represents a single meta-data attribute in the system that shows up on
 * the Entity maintenance screen when that Entity is given permissions to use functionality
 * within a certain Namespace in the system.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@javax.persistence.Entity
@Table(name="KIM_GRP_TYP_DFLT_ATTRIBS_T")
public class GroupTypeDefaultAttribute extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = -8332284694172302250L;
	@Id
	@Column(name="ID")
	private Long id;
	@Column(name="GROUP_TYPE_ID")
	private Long groupTypeId;
	@Column(name="ATTRIBUTE_TYPE_ID")
	private Long attributeTypeId;
	@Column(name="ATTRIBUTE_NAME")
	private String attributeName;
	@Column(name="DESCRIPTION")
	private String description;
	@Column(name="REQUIRED")
	private boolean required;
	@Column(name="ACTIVE")
	private boolean active;

	//@OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	//@JoinColumn(name="ATTRIBUTE_TYPE_ID", insertable=false, updatable=false)
	@Transient
	private AttributeType attributeType;
	//@ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	//@JoinColumn(name="GROUP_TYPE_ID", insertable=false, updatable=false)
	@Transient
	private GroupType groupType;

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
     * @return the groupTypeId
     */
    public Long getGroupTypeId() {
        return this.groupTypeId;
    }

    /**
     * @param groupTypeId the groupTypeId to set
     */
    public void setGroupTypeId(Long groupTypeId) {
        this.groupTypeId = groupTypeId;
    }

    /**
     * @return the attributeTypeId
     */
    public Long getAttributeTypeId() {
        return this.attributeTypeId;
    }

    /**
     * @param attributeTypeId the attributeTypeId to set
     */
    public void setAttributeTypeId(Long attributeTypeId) {
        this.attributeTypeId = attributeTypeId;
    }

    /**
     * @return the attributeName
     */
    public String getAttributeName() {
        return this.attributeName;
    }

    /**
     * @param attributeName the attributeName to set
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the required
     */
    public boolean isRequired() {
        return this.required;
    }

    /**
     * @param required the required to set
     */
    public void setRequired(boolean required) {
        this.required = required;
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
     * @return the attributeType
     */
    public AttributeType getAttributeType() {
        return this.attributeType;
    }

    /**
     * @param attributeType the attributeType to set
     */
    public void setAttributeType(AttributeType attributeType) {
        this.attributeType = attributeType;
    }

    /**
     * @return the groupType
     */
    public GroupType getGroupType() {
        return this.groupType;
    }

    /**
     * @param groupType the groupType to set
     */
    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }

    /**
	 * This method returns a string representation of a default group type attribute.
	 *
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("groupTypeId", getGroupTypeId());
        propMap.put("attributeTypeId", getAttributeTypeId());
        propMap.put("attributeName", getAttributeName());
        propMap.put("value", getDescription());
        propMap.put("required", isRequired());
        propMap.put("active", isActive());
        return propMap;
	}

}

