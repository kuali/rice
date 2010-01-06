/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.types.dto;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimTypeAttributeInfo implements Serializable {
	private static final long serialVersionUID = 5069578471162433850L;
	protected String sortCode;
	protected String attributeName;
	protected String namespaceCode;
	protected String attributeLabel;
	protected String componentName;
	protected String kimAttributeId;

	public String getSortCode() {
		return this.sortCode;
	}
	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
	}
	public String getAttributeName() {
		return this.attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public String getNamespaceCode() {
		return this.namespaceCode;
	}
	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}
	public String getAttributeLabel() {
		return this.attributeLabel;
	}
	public void setAttributeLabel(String attributeLabel) {
		this.attributeLabel = attributeLabel;
	}
	public String getComponentName() {
		return this.componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public String getKimAttributeId() {
		return this.kimAttributeId;
	}
	public void setKimAttributeId(String kimAttributeId) {
		this.kimAttributeId = kimAttributeId;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.append("attributeName", this.attributeName)
				.append("componentName",this.componentName)
				.append("kimAttributeId",this.kimAttributeId)
				.append("namespaceCode", this.namespaceCode)
				.append("sortCode",this.sortCode)
				.append("attributeLabel",this.attributeLabel)
				.toString();
	}
	
}
