/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.types.impl;

import org.hibernate.annotations.Type;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRIM_ATTR_DEFN_T")
public class KimAttributeImpl extends PersistableBusinessObjectBase {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="KIM_ATTR_DEFN_ID")
	protected String kimAttributeId;
	@Column(name="NM")
	protected String attributeName;
	@Column(name="NMSPC_CD")
	protected String namespaceCode;
	@Column(name="LBL")
	protected String attributeLabel;
	@Type(type="yes_no")
	@Column(name="ACTV_IND")
	protected boolean active;
	@Column(name="CMPNT_NM")
	protected String componentName;
	
	public String getKimAttributeId() {
		return kimAttributeId;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getAttributeLabel() {
		return this.attributeLabel;
	}

	public void setAttributeLabel(String attributeLabel) {
		this.attributeLabel = attributeLabel;
	}

	public void setKimAttributeId(String kimAttributeId) {
		this.kimAttributeId = kimAttributeId;
	}

	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public String getComponentName() {
		return this.componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
}
