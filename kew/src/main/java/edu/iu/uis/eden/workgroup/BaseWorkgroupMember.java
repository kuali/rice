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
package edu.iu.uis.eden.workgroup;

import java.io.Serializable;

/**
 * A member of a {@link BaseWorkgroup}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BaseWorkgroupMember implements Serializable {

	private static final long serialVersionUID = -6343373525316948409L;

	private Long workgroupId;
    private String workflowId;
    private String memberType;
    private Integer workgroupVersionNumber = new Integer(0);
    private Integer lockVerNbr;

	private BaseWorkgroup workgroup;

    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    public Long getWorkgroupId() {
        return workgroupId;
    }

    public void setWorkgroupId(Long workgroupId) {
        this.workgroupId = workgroupId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

	public BaseWorkgroup getWorkgroup() {
        return workgroup;
    }

    public void setWorkgroup(BaseWorkgroup workgroup) {
        this.workgroup = workgroup;
    }

	public Integer getWorkgroupVersionNumber() {
		return workgroupVersionNumber;
	}

	public void setWorkgroupVersionNumber(Integer workgroupVerNbr) {
		this.workgroupVersionNumber = workgroupVerNbr;
	}

}
