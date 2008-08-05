/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package org.kuali.workflow.workgroup;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;

import edu.iu.uis.eden.routetemplate.RuleAttribute;

/**
 * Associates WorkgroupTypes to Attributes.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="EN_WRKGRP_TYP_ATTRIB_T")
public class WorkgroupTypeAttribute {

	@Id
	@Column(name="WRKGRP_TYP_ATTRIB_ID")
	private Long workgroupTypeAttributeId;
	@OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="WRKGRP_TYP_ID")
	private WorkgroupType workgroupType;
	@OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="ATTRIB_ID")
	private RuleAttribute attribute;
    @Transient
	private Boolean active = true;
	@Column(name="ORD_INDX")
	private int orderIndex;
	@Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;

	public Object loadAttribute() {
    	try {
    		ObjectDefinition objectDefinition = new ObjectDefinition(getAttribute().getClassName(), getAttribute().getMessageEntity());
    		Object attribute = GlobalResourceLoader.getObject(objectDefinition);
    		if (attribute == null) {
            	throw new WorkflowRuntimeException("Could not find attribute " + objectDefinition);
            }
    		return attribute;
    	} catch (Exception e) {
    		throw new RuntimeException("Caught error attempting to load attribute class: " + getAttribute().getClassName(), e);
    	}
    }

	public RuleAttribute getAttribute() {
		return attribute;
	}

	public void setAttribute(RuleAttribute attribute) {
		this.attribute = attribute;
	}

	public WorkgroupType getWorkgroupType() {
		return workgroupType;
	}

	public void setWorkgroupType(WorkgroupType workgroupType) {
		this.workgroupType = workgroupType;
	}

	public Long getWorkgroupTypeAttributeId() {
		return workgroupTypeAttributeId;
	}

	public void setWorkgroupTypeAttributeId(Long workgroupTypeId) {
		this.workgroupTypeAttributeId = workgroupTypeId;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public int getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}

	public Integer getLockVerNbr() {
		return lockVerNbr;
	}

	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}

}

