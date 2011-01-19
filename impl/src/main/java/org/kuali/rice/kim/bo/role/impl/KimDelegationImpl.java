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
package org.kuali.rice.kim.bo.role.impl;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.role.KimDelegation;
import org.kuali.rice.kim.bo.role.dto.DelegateMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.DelegateTypeInfo;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.springframework.util.AutoPopulatingList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googleroles.com)
 *
 */
@Entity
@Table(name="KRIM_DLGN_T")
public class KimDelegationImpl extends PersistableBusinessObjectBase implements KimDelegation {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="DLGN_ID")
	protected String delegationId;
	
	@Column(name="ROLE_ID")
	protected String roleId;

	@Type(type="yes_no")
	@Column(name="ACTV_IND")
	protected boolean active = true;

	@Column(name="KIM_TYP_ID")
	protected String kimTypeId;

	@Column(name="DLGN_TYP_CD")
	protected String delegationTypeCode;
	
	@OneToMany(targetEntity=KimDelegationMemberImpl.class,cascade={CascadeType.ALL},fetch=FetchType.EAGER)
	@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name="DLGN_ID", insertable=false, updatable=false)
	protected List<KimDelegationMemberImpl> members = new AutoPopulatingList(KimDelegationMemberImpl.class);

	@Transient
	protected KimTypeInfo kimType;

	public String getRoleId() {
		return this.roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getKimTypeId() {
		return this.kimTypeId;
	}

	public void setKimTypeId(String typeId) {
		this.kimTypeId = typeId;
	}

	public KimTypeInfo getKimType() {
		return this.kimType;
	}

	public String getDelegationTypeCode() {
		return this.delegationTypeCode;
	}

	public void setDelegationTypeCode(String delegationTypeCode) {
		this.delegationTypeCode = delegationTypeCode;
	}

	public String getDelegationId() {
		return this.delegationId;
	}

	public void setDelegationId(String delegationId) {
		this.delegationId = delegationId;
	}

	public List<String> getMemberGroupIds() {
		return getMembersOfType( Role.GROUP_MEMBER_TYPE );
	}

	public List<String> getMemberPrincipalIds() {
		return getMembersOfType( Role.PRINCIPAL_MEMBER_TYPE );
	}

	public List<String> getMemberRoleIds() {
		return getMembersOfType( Role.ROLE_MEMBER_TYPE );
	}

	protected List<String> getMembersOfType( String memberTypeCode ) {
		List<String> roleMembers = new ArrayList<String>();
		for ( KimDelegationMemberImpl member : getMembers() ) {
			if ( member.getMemberTypeCode().equals ( memberTypeCode )
					&& member.isActive() ) {
				roleMembers.add( member.getMemberId() );
			}
		}
		return roleMembers;
	}

	public List<KimDelegationMemberImpl> getMembers() {
		return this.members;
	}

	public void setMembers(List<KimDelegationMemberImpl> members) {
		this.members = members;
	}
	
	public DelegateTypeInfo toSimpleInfo(){
		DelegateTypeInfo delegateTypeInfo = new DelegateTypeInfo();
		delegateTypeInfo.setActive(active);
		delegateTypeInfo.setDelegationId(delegationId);
		delegateTypeInfo.setDelegationTypeCode(delegationTypeCode);
		//delegateTypeInfo.setKimType(kimType);
		delegateTypeInfo.setKimTypeId(kimTypeId);
		delegateTypeInfo.setRoleId(roleId);
		delegateTypeInfo.setMembers(new ArrayList<DelegateMemberCompleteInfo>());
		if(members!=null){
			for(KimDelegationMemberImpl member: members)
				delegateTypeInfo.getMembers().add(member.toSimpleInfo());
		}
		return delegateTypeInfo;
	}

}
