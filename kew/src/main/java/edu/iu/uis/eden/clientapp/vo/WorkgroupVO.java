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
package edu.iu.uis.eden.clientapp.vo;

import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Transport object representing a {@link Workgroup}.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * @workflow.webservice-object 
 */
public class WorkgroupVO implements java.io.Serializable {

    static final long serialVersionUID = 5233450403505886792L;

    private static final String ACTIVE_LABEL = "ACTIVE";
    private static final String INACTIVE_LABEL = "INACTIVE";

    private Long workgroupId;
    private String description;
    private String workgroupName;
    private boolean activeInd;
    private String workgroupType;
    private UserVO[] members = new UserVO[0];

    public WorkgroupVO() {}

    public boolean isActiveInd() {
        return activeInd;
    }

    public void setActiveInd(boolean activeInd) {
        this.activeInd = activeInd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserVO[] getMembers() {
        return members;
    }

    public void setMembers(UserVO[] members) {
        this.members = members;
    }

    public Long getWorkgroupId() {
        return workgroupId;
    }

    public void setWorkgroupId(Long workgroupId) {
        this.workgroupId = workgroupId;
    }

    public String getWorkgroupName() {
        return workgroupName;
    }

    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
    }

    public String getWorkgroupType() {
        return workgroupType;
    }
    
    public void setWorkgroupType(String workgroupType) {
        this.workgroupType = workgroupType;
    }
    
    public String getActiveLabel() {
        if (this.activeInd) {
            return ACTIVE_LABEL;
        } else {
            return INACTIVE_LABEL;
        }
    }

}
