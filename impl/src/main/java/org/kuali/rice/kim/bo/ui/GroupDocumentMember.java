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
package org.kuali.rice.kim.bo.ui;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.impl.KimAbstractMemberImpl;
import org.kuali.rice.kim.util.KimConstants;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name="KRIM_PND_GRP_MBR_T")
public class GroupDocumentMember extends KimDocumentBoBase {

	@Id
	@Column(name="GRP_MBR_ID")
	protected String groupMemberId;

	@Column(name="GRP_ID")
	protected String groupId;
	
	@Column(name="MBR_ID")
	protected String memberId;
	
	protected String memberName;
	
	protected String memberNamespaceCode;
	
	protected String memberFullName;

	//TODO: remove the default
	@Column(name="MBR_TYP_CD")
	protected String memberTypeCode = KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE;

	public String getGroupMemberId() {
		return this.groupMemberId;
	}

	public void setGroupMemberId(String groupMemberId) {
		this.groupMemberId = groupMemberId;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap<String,Object> m = new LinkedHashMap<String,Object>();
		m.put( "groupMemberId", groupMemberId );
		m.put( "groupId", groupId );
		m.put( "memberId", getMemberId() );
		m.put( "memberTypeCode", getMemberTypeCode() );
		return m;
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
	 * @return the memberNamespaceCode
	 */
	public String getMemberNamespaceCode() {
		return this.memberNamespaceCode;
	}

	/**
	 * @param memberNamespaceCode the memberNamespaceCode to set
	 */
	public void setMemberNamespaceCode(String memberNamespaceCode) {
		this.memberNamespaceCode = memberNamespaceCode;
	}
	
	/**
	 * @return the memberFullName
	 */
	public String getMemberFullName() {
		return this.memberFullName;
	}

	/**
	 * @param memberFullName the memberFullName to set
	 */
	public void setMemberFullName(String memberFullName) {
		this.memberFullName = memberFullName;
	}
}
