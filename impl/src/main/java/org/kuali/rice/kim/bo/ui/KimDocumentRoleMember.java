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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name="KRIM_PND_ROLE_MBR_MT")
public class KimDocumentRoleMember  extends KimDocumentBoBase {
	private static final long serialVersionUID = -2463865643038170979L;

	@Column(name="ROLE_MBR_ID")
	protected String roleMemberId;
	
	@Column(name="ROLE_ID")
	protected String roleId;
	@Column(name="MBR_ID")
	protected String memberId;
	
	//TODO: remove the default
	@Column(name="MBR_TYP_CD")
	protected String memberTypeCode = KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE;
	@Column(name="MBR_NM")
	protected String memberName;
	@Column(name="MBR_NMSPC")
	protected String memberNamespaceCode;

	protected List <KimDocumentRoleQualifier> qualifiers = new TypedArrayList(KimDocumentRoleQualifier.class);
	protected String qualifiersToDisplay;
	
	@Transient
	private List<KimDocumentRoleResponsibilityAction> roleRspActions;

	public KimDocumentRoleMember() {
		qualifiers = new ArrayList <KimDocumentRoleQualifier>();
		roleRspActions = new ArrayList <KimDocumentRoleResponsibilityAction>();
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

	public String getRoleMemberId() {
		return this.roleMemberId;
	}

	public void setRoleMemberId(String roleMemberId) {
		this.roleMemberId = roleMemberId;
	}

	public String getRoleId() {
		return this.roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public KimDocumentRoleQualifier getQualifier(String kimAttributeDefnId) {
		for(KimDocumentRoleQualifier qualifier:qualifiers){
			if(qualifier.getKimAttrDefnId().equals(kimAttributeDefnId))
				return qualifier;
		}
		return null;
	}

	public List<KimDocumentRoleQualifier> getQualifiers() {
		return this.qualifiers;
	}

	public void setQualifiers(List<KimDocumentRoleQualifier> qualifiers) {
		this.qualifiers = qualifiers;
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
		if ( memberName == null ) {
			populateDerivedValues();
		}
		return memberName;
	}

	/**
	 * @param memberName the memberName to set
	 */
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public List<KimDocumentRoleResponsibilityAction> getRoleRspActions() {
		return this.roleRspActions;
	}

	public void setRoleRspActions(
			List<KimDocumentRoleResponsibilityAction> roleRspActions) {
		this.roleRspActions = roleRspActions;
	}

	/**
	 * @return the memberNamespaceCode
	 */
	public String getMemberNamespaceCode() {
		if ( memberNamespaceCode == null ) {
			populateDerivedValues();
		}
		return memberNamespaceCode;
	}

	/**
	 * @param memberNamespaceCode the memberNamespaceCode to set
	 */
	public void setMemberNamespaceCode(String memberNamespaceCode) {
		this.memberNamespaceCode = memberNamespaceCode;
	}

	protected void populateDerivedValues() {
    	BusinessObject member = KIMServiceLocator.getUiDocumentService().getMember(getMemberTypeCode(), getMemberId());
    	if ( member != null ) {
	        setMemberName(KIMServiceLocator.getUiDocumentService().getMemberName(getMemberTypeCode(), member));
	        setMemberNamespaceCode(KIMServiceLocator.getUiDocumentService().getMemberNamespaceCode(getMemberTypeCode(), member));
    	}
	}
	
	public boolean isRole(){
		return getMemberTypeCode()!=null && getMemberTypeCode().equals(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);
	}
	
	public boolean isGroup(){
		return getMemberTypeCode()!=null && getMemberTypeCode().equals(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE);
	}

	public boolean isPrincipal(){
		return getMemberTypeCode()!=null && getMemberTypeCode().equals(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE);
	}

	public AttributeSet getQualifierAsAttributeSet() {
		AttributeSet m = new AttributeSet();
		for ( KimDocumentRoleQualifier data : getQualifiers() ) {
			m.put( data.getKimAttribute().getAttributeName(), data.getAttrVal() );
		}
		return m;
	}

	/**
	 * @return the qualifiersToDisplay
	 */
	public String getQualifiersToDisplay() {
		return this.qualifiersToDisplay;
	}

	/**
	 * @param qualifiersToDisplay the qualifiersToDisplay to set
	 */
	public void setQualifiersToDisplay(String qualifiersToDisplay) {
		this.qualifiersToDisplay = qualifiersToDisplay;
	}

}
