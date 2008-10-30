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
package org.kuali.rice.kew.workgroup;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * A member of a {@link BaseWorkgroup}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@IdClass(org.kuali.rice.kew.workgroup.BaseWorkgroupMemberId.class)
@Entity
@Table(name="EN_WRKGRP_MBR_T")
public class BaseWorkgroupMember implements Serializable {

	private static final long serialVersionUID = -6343373525316948409L;

	@Id
	@Column(name="GRP_ID")
	private Long workgroupId;
    @Id
	@Column(name="WRKGRP_MBR_PRSN_EN_ID")
	private String workflowId;
    @Column(name="WRKGRP_MBR_TYP")
	private String memberType;
    @Id
    @Column(name="WRKGRP_VER_NBR")
    private Integer workgroupVersionNumber = new Integer(0);
    @Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;

	@ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST}, optional=true)
	@JoinColumns({@JoinColumn(name="GRP_ID", insertable=false, updatable=false), @JoinColumn(name="WRKGRP_VER_NBR", insertable=false, updatable=false)})
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

