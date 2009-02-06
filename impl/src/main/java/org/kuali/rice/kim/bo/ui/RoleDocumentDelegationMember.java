/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.bo.ui;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googleroles.com)
 *
 */
@Entity
@Table(name="KRIM_PND_DLGN_MBR_T")
public class RoleDocumentDelegationMember extends KimDocumentBoBase {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="DLGN_MBR_ID")
	protected String delegationMemberId;
	
	@Id
	@Column(name="DLGN_ID")
	protected String delegationId;
	
	@Column(name="MBR_ID")
	protected String memberId;

	@Column(name="MBR_TYP_CD")
	protected String memberTypeCode;
	
	@Column(name="MBR_NM")
	protected String memberName;
	protected List <RoleDocumentDelegationMemberQualifier> qualifiers = new TypedArrayList(RoleDocumentDelegationMemberQualifier.class);
	
	@Column(name="ACTV_FRM_DT")
	protected Timestamp activeFromDate;
	@Column(name="ACTV_TO_DT")
	protected Timestamp activeToDate;
	

	public String getDelegationId() {
		return this.delegationId;
	}

	public void setDelegationId(String delegationId) {
		this.delegationId = delegationId;
	}

	/**
	 * @return the qualifiers
	 */
	public List<RoleDocumentDelegationMemberQualifier> getQualifiers() {
		return this.qualifiers;
	}

	/**
	 * @param qualifiers the qualifiers to set
	 */
	public void setQualifiers(List<RoleDocumentDelegationMemberQualifier> qualifiers) {
		this.qualifiers = qualifiers;
	}

	public int getNumberOfQualifiers(){
		return qualifiers==null?0:qualifiers.size();
	}

	/**
	 * @return the activeFromDate
	 */
	public Timestamp getActiveFromDate() {
		return this.activeFromDate;
	}

	/**
	 * @param activeFromDate the activeFromDate to set
	 */
	public void setActiveFromDate(Timestamp activeFromDate) {
		this.activeFromDate = activeFromDate;
	}

	/**
	 * @return the activeToDate
	 */
	public Timestamp getActiveToDate() {
		return this.activeToDate;
	}

	/**
	 * @param activeToDate the activeToDate to set
	 */
	public void setActiveToDate(Timestamp activeToDate) {
		this.activeToDate = activeToDate;
	}

	/**
	 * @return the memberId
	 */
	public String getMemberId() {
		return this.memberId;
	}

	/**
	 * @param memberId the memberId to set
	 */
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	/**
	 * @return the memberName
	 */
	public String getMemberName() {
		return this.memberName;
	}

	/**
	 * @param memberName the memberName to set
	 */
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	/**
	 * @return the delegationMemberId
	 */
	public String getDelegationMemberId() {
		return this.delegationMemberId;
	}

	/**
	 * @param delegationMemberId the delegationMemberId to set
	 */
	public void setDelegationMemberId(String delegationMemberId) {
		this.delegationMemberId = delegationMemberId;
	}

	/**
	 * @return the memberTypeCode
	 */
	public String getMemberTypeCode() {
		return this.memberTypeCode;
	}

	/**
	 * @param memberTypeCode the memberTypeCode to set
	 */
	public void setMemberTypeCode(String memberTypeCode) {
		this.memberTypeCode = memberTypeCode;
	}

}