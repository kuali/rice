/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.role.dto;

import java.io.Serializable;

import org.kuali.rice.kim.bo.role.KimPermission;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimPermissionInfo extends PermissionDetailsInfo implements KimPermission, Serializable {

	protected String namespaceCode;
	protected String name;
	protected String description;
	protected KimPermissionTemplateInfo template;
	
	protected boolean active;
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isActive() {
		return this.active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getNamespaceCode() {
		return this.namespaceCode;
	}
	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}
	public KimPermissionTemplateInfo getTemplate() {
		return this.template;
	}
	public void setTemplate(KimPermissionTemplateInfo template) {
		this.template = template;
	}
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return new ToStringBuilder(this).append("details", this.details).append("template", this.template).append("namespaceCode", this.namespaceCode).append("name", this.name).toString();
    }
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if ( getPermissionId() == null ) {
			return 0;
		}
		return getPermissionId().hashCode();
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ( obj == null || !(obj instanceof KimPermissionInfo) ) {
			return false;
		}
		return StringUtils.equals( getPermissionId(), ((KimPermissionInfo)obj).getPermissionId() );
	}
	
	public String getNameToDisplay(){
		return (StringUtils.isBlank(getName()) && getTemplate()!=null)?getTemplate().getName():getName();
	}

}