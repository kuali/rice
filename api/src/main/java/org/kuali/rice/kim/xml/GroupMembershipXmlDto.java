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
package org.kuali.rice.kim.xml;

import java.io.Serializable;
import java.sql.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.jaxb.SqlDateAdapter;


/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Member", namespace = "http://rice.kuali.org/xsd/kim/group", propOrder = {
    "memberId", "memberTypeCode", "activeFromDate", "activeToDate"
})
public class GroupMembershipXmlDto implements Serializable {
	@XmlTransient
	private static final long serialVersionUID = 8480290118998280178L;
	
	@XmlTransient
	protected String groupId;
	
	@XmlTransient
	protected String groupMemberId;
	
	@XmlElement
	protected String memberId;
	
	@XmlElement
	protected String memberTypeCode;
	
	@XmlTransient
	protected Long versionNumber;
	
	@XmlElement
	@XmlJavaTypeAdapter(value = SqlDateAdapter.class)
	protected Date activeFromDate;
	
	@XmlElement
	@XmlJavaTypeAdapter(value = SqlDateAdapter.class)
	protected Date activeToDate;

	public GroupMembershipXmlDto(String groupId, String groupMemberId, String memberId, String memberTypeCode, Date activeFromDate, Date activeToDate) {
		super();
		this.groupId = groupId;
		this.memberId = memberId;
		this.memberTypeCode = memberTypeCode;
		this.groupMemberId = groupMemberId;
		this.activeFromDate = activeFromDate;
		this.activeToDate = activeToDate;
	}
	
	// for jax-ws service construction
	@SuppressWarnings("unused")
	private GroupMembershipXmlDto() {}
	
	public String getMemberId() {
		return this.memberId;
	}


	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}


	public String getMemberTypeCode() {
		return this.memberTypeCode;
	}


	public void setMemberTypeCode(String memberTypeCode) {
		this.memberTypeCode = memberTypeCode;
	}


	public String getGroupId() {
		return this.groupId;
	}


	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}


	public String getGroupMemberId() {
		return this.groupMemberId;
	}


	public void setGroupMemberId(String groupMemberId) {
		this.groupMemberId = groupMemberId;
	}


	public Long getVersionNumber() {
		return this.versionNumber;
	}


	public void setVersionNumber(Long versionNumber) {
		this.versionNumber = versionNumber;
	}


//	@XmlJavaTypeAdapter(value = SqlDateAdapter.class) 
	public Date getActiveFromDate() {
		return this.activeFromDate;
	}


	public void setActiveFromDate(Date activeFromDate) {
		this.activeFromDate = activeFromDate;
	}


//	@XmlJavaTypeAdapter(value = SqlDateAdapter.class) 
	public Date getActiveToDate() {
		return this.activeToDate;
	}

	public void setActiveToDate(Date activeToDate) {
		this.activeToDate = activeToDate;
	}

	public boolean isActive() {
		long now = System.currentTimeMillis();		
		return (activeFromDate == null || now > activeFromDate.getTime()) && (activeToDate == null || now < activeToDate.getTime());
	}

}
