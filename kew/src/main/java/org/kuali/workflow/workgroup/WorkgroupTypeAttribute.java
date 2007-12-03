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

import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routetemplate.RuleAttribute;

/**
 * Associates WorkgroupTypes to Attributes.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupTypeAttribute {

	private Long workgroupTypeAttributeId;
	private WorkgroupType workgroupType;
	private RuleAttribute attribute;
	private Boolean active = true;
	private int orderIndex;
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
