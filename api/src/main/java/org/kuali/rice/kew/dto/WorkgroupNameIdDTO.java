/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.rice.kew.dto;

import org.kuali.rice.kim.util.KimConstants;

/**
 * Transport object for group namespace and name
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupNameIdDTO extends WorkgroupIdDTO {

	private static final long serialVersionUID = 1935321896253453916L;
	
	private String namespace = KimConstants.KIM_GROUP_DEFAULT_NAMESPACE_CODE;
	private String workgroupName;
    
    public WorkgroupNameIdDTO() {
        
    }
    
    public WorkgroupNameIdDTO(String workgroupName) {
        this.workgroupName = workgroupName;
    }
    
    public WorkgroupNameIdDTO(String namespace, String workgroupName) {
    	this.namespace = namespace;
    	this.workgroupName = workgroupName;
    }
    
    public String getWorkgroupName() {
        return workgroupName;
    }
    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
    }
    public String getNamespace() {
		return this.namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public boolean equals(Object object) {
        if (object instanceof WorkgroupNameIdDTO) {
            String objectId = ((WorkgroupNameIdDTO)object).getWorkgroupName();
            return ((workgroupName == null && objectId == null) || (workgroupName != null && workgroupName.equals(objectId)));
        }
        return false;
    }
    
    public int hashCode() {
        return (workgroupName == null ? 0 : workgroupName.hashCode());
    }
    
    public String toString() {
        return (workgroupName == null ? "null" : workgroupName);
    }
}
