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

import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.util.KimConstants;
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
	protected String memberTypeCode = KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE;
	
	protected String memberNamespaceCode;
	
	@Column(name="MBR_NM")
	protected String memberName;
	protected List <RoleDocumentDelegationMemberQualifier> qualifiers = new TypedArrayList(RoleDocumentDelegationMemberQualifier.class);
	
	protected String delegationTypeCode;
	
	/**
	 * @return the delegationTypeCode
	 */
	public String getDelegationTypeCode() {
		return this.delegationTypeCode;
	}

	/**
	 * @param delegationTypeCode the delegationTypeCode to set
	 */
	public void setDelegationTypeCode(String delegationTypeCode) {
		this.delegationTypeCode = delegationTypeCode;
	}

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

	public RoleDocumentDelegationMemberQualifier getQualifier(String kimAttributeDefnId) {
		for(RoleDocumentDelegationMemberQualifier qualifier:qualifiers){
			if(qualifier.getKimAttrDefnId().equals(kimAttributeDefnId))
				return qualifier;
		}
		return null;
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


	public boolean isDelegationPrimary(){
		return KEWConstants.DELEGATION_PRIMARY.equals(getDelegationTypeCode());
	}

	public boolean isDelegationSecondary(){
		return KEWConstants.DELEGATION_SECONDARY.equals(getDelegationTypeCode());
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

}