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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.impl.KimAbstractMemberImpl;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.RoleResponsibilityActionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.dto.KimTypeAttributeInfo;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.KimTypeInfoService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googleroles.com)
 *
 */
@Entity
@Table(name="KRIM_ROLE_MBR_T")
public class RoleMemberImpl extends KimAbstractMemberImpl {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ROLE_MBR_ID")
	protected String roleMemberId;
	
	@Column(name="ROLE_ID")
	protected String roleId;
	
	protected List<RoleMemberAttributeDataImpl> attributes = new TypedArrayList(RoleMemberAttributeDataImpl.class);
	
	protected List <RoleResponsibilityActionImpl> roleRspActions;
	
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
	
	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "roleMemberId", roleMemberId );
		m.put( "roleId", roleId );
		m.put( "memberId", getMemberId() );
		m.put( "memberTypeCode", getMemberTypeCode() );
		return m;
	}

	public List<RoleMemberAttributeDataImpl> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(List<RoleMemberAttributeDataImpl> attributes) {
		this.attributes = attributes;
	}

	protected transient AttributeSet qualifierAsAttributeSet = null;

	public AttributeSet getQualifier() {
		if ( qualifierAsAttributeSet == null ) {
			KimRoleInfo role = getRoleService().getRole(roleId);
			KimTypeInfo kimType = getTypeInfoService().getKimType( role.getKimTypeId() );
			AttributeSet m = new AttributeSet();
			for ( RoleMemberAttributeDataImpl data : getAttributes() ) {
				KimTypeAttributeInfo attribute = null;
				if ( kimType != null ) {
					attribute = kimType.getAttributeDefinition( data.getKimAttributeId() );
				}
				if ( attribute != null ) {
					m.put( attribute.getAttributeName(), data.getAttributeValue() );
				} else {
					m.put( data.getKimAttribute().getAttributeName(), data.getAttributeValue() );
				}
			}
			qualifierAsAttributeSet = m;
		}
		return qualifierAsAttributeSet;
	}
	
	public boolean hasQualifier() {
		return !getAttributes().isEmpty();
	}
	
	/**
	 * @return the roleRspActions
	 */
	public List<RoleResponsibilityActionImpl> getRoleRspActions() {
		return this.roleRspActions;
	}
	/**
	 * @param roleRspActions the roleRspActions to set
	 */
	public void setRoleRspActions(List<RoleResponsibilityActionImpl> roleRspActions) {
		this.roleRspActions = roleRspActions;
	}

	private transient static KimTypeInfoService kimTypeInfoService;
	protected KimTypeInfoService getTypeInfoService() {
		if(kimTypeInfoService == null){
			kimTypeInfoService = KIMServiceLocator.getTypeInfoService();
		}
		return kimTypeInfoService;
	}
	private transient static RoleService roleService;
	protected RoleService getRoleService() {
		if(roleService == null){
			roleService = KIMServiceLocator.getRoleManagementService();
		}
		return roleService;
	}

	public RoleMemberCompleteInfo toSimpleInfo(){
		RoleMemberCompleteInfo roleMemberCompleteInfo = new RoleMemberCompleteInfo();
		roleMemberCompleteInfo.setRoleId(roleId);
		roleMemberCompleteInfo.setRoleMemberId(roleMemberId);
		roleMemberCompleteInfo.setActiveFromDate(activeFromDate);
		roleMemberCompleteInfo.setActiveToDate(activeToDate);
		roleMemberCompleteInfo.setMemberId(memberId);
		roleMemberCompleteInfo.setMemberTypeCode(memberTypeCode);
		roleMemberCompleteInfo.setQualifier(getQualifier());
		roleMemberCompleteInfo.setRoleRspActions(new ArrayList<RoleResponsibilityActionInfo>());
		if(roleRspActions!=null){
			for(RoleResponsibilityActionImpl roleResponsibilityActionImpl: roleRspActions){
				roleMemberCompleteInfo.getRoleRspActions().add(roleResponsibilityActionImpl.toSimpleInfo());
			}
		}
		return roleMemberCompleteInfo;
	}

}
