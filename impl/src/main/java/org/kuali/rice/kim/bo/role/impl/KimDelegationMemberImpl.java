/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.role.impl;

import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.kim.bo.impl.KimAbstractMemberImpl;
import org.kuali.rice.kim.bo.role.KimDelegationMember;
import org.kuali.rice.kim.bo.role.dto.DelegateMemberCompleteInfo;
import org.springframework.util.AutoPopulatingList;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name="KRIM_DLGN_MBR_T")
public class KimDelegationMemberImpl extends KimAbstractMemberImpl implements KimDelegationMember {

	private static final long serialVersionUID = 6278392972043721961L;

	@Id
	@Column(name="DLGN_MBR_ID")
	protected String delegationMemberId;
	@Column(name="DLGN_ID")
	protected String delegationId;
	@Column(name="ROLE_MBR_ID")
	protected String roleMemberId;

	@OneToMany(targetEntity=KimDelegationMemberAttributeDataImpl.class,cascade={CascadeType.ALL},fetch=FetchType.EAGER)
	@JoinColumn(name="DLGN_MBR_ID", referencedColumnName="DLGN_MBR_ID", insertable=false, updatable=false )
	protected List<KimDelegationMemberAttributeDataImpl> attributes = new AutoPopulatingList(KimDelegationMemberAttributeDataImpl.class);

	public boolean isActive() {
		long now = System.currentTimeMillis();
		return (activeFromDate == null || activeFromDate.getTime() < now) && (activeToDate == null || activeToDate.getTime() > now);
	}

	public void setActiveFromDate(Timestamp from) {
		this.activeFromDate = from;
	}

	public void setActiveToDate(Timestamp to) {
		this.activeToDate = to;
	}

	public String getDelegationMemberId() {
		return this.delegationMemberId;
	}

	public void setDelegationMemberId(String delegationMemberId) {
		this.delegationMemberId = delegationMemberId;
	}

	public String getDelegationId() {
		return this.delegationId;
	}

	public void setDelegationId(String delegationId) {
		this.delegationId = delegationId;
	}

	public List<KimDelegationMemberAttributeDataImpl> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(List<KimDelegationMemberAttributeDataImpl> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimDelegationGroup#getQualifier()
	 */
	public AttributeSet getQualifier() {
		AttributeSet attribs = new AttributeSet();

		if ( attributes == null ) {
			return attribs;
		}
		for ( KimDelegationMemberAttributeDataImpl attr : attributes ) {
			attribs.put( attr.getKimAttribute().getAttributeName(), attr.getAttributeValue() );
		}
		return attribs;
	}

	/**
	 * @return the roleMemberId
	 */
	public String getRoleMemberId() {
		return this.roleMemberId;
	}

	/**
	 * @param roleMemberId the roleMemberId to set
	 */
	public void setRoleMemberId(String roleMemberId) {
		this.roleMemberId = roleMemberId;
	}

	public DelegateMemberCompleteInfo toSimpleInfo(){
		DelegateMemberCompleteInfo delegateMemberCompleteInfo = new DelegateMemberCompleteInfo();
		delegateMemberCompleteInfo.setDelegationMemberId(delegationMemberId);
		delegateMemberCompleteInfo.setActiveFromDate(activeFromDate);
		delegateMemberCompleteInfo.setActiveToDate(activeToDate);
		delegateMemberCompleteInfo.setDelegationId(delegationId);
		delegateMemberCompleteInfo.setMemberId(memberId);
		delegateMemberCompleteInfo.setMemberTypeCode(memberTypeCode);
		delegateMemberCompleteInfo.setQualifier(getQualifier());
		delegateMemberCompleteInfo.setRoleMemberId(roleMemberId);
		return delegateMemberCompleteInfo;
	}
}
