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
package org.kuali.rice.kim.bo.role.impl;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.role.RoleMember;
import org.kuali.rice.kim.bo.role.RolePrincipal;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KR_KIM_ROLE_PRINCIPAL_T")
@AttributeOverrides({
	@AttributeOverride(name="memberId",column=@Column(name="PRNCPL_ID"))
})
public class RolePrincipalImpl extends RoleMemberBase implements RolePrincipal {

	public String getMemberPrincipalId() {
		return getMemberId();
	}
	
	public void setMemberPrincipalId( String principalId ) {
		setMemberId( principalId );
	}

	/**
	 * @see org.kuali.rice.kim.bo.group.RoleMember#getRoleMemberClass()
	 */
	public Class<? extends RoleMember> getRoleMemberClass() {
		return this.getClass();
	}

	/**
	 * @see org.kuali.rice.kim.bo.group.RoleMember#getRoleMemberTypeCode()
	 */
	public String getRoleMemberTypeCode() {
		return getClass().getSimpleName();
	}

}
