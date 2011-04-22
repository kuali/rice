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
package org.kuali.rice.kim.bo.role.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.util.AttributeSet;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RoleMembershipInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	protected String roleId;
	protected String roleMemberId;
	protected String embeddedRoleId; // ID of the role from which the group or principal was derived
	protected String memberId;
	protected String memberTypeCode;
	protected String roleSortingCode; // value which can be used to sort the role members into a meaningful order
	protected AttributeSet qualifier;
	protected List<DelegateInfo> delegates = new ArrayList<DelegateInfo>();
	
	// for jax-ws client proxy creation
	@SuppressWarnings("unused")
	private RoleMembershipInfo() {}
	
	public RoleMembershipInfo(String roleId, String roleMemberId, String memberId, String memberTypeCode,
			AttributeSet qualifier) {
		super();
		this.roleId = roleId;
		if ( memberId == null ) {
			throw new IllegalArgumentException( "memberId may not be null" );
		}
		this.memberId = memberId;
		if ( memberTypeCode == null ) {
			throw new IllegalArgumentException( "memberTypeCode may not be null" );
		}
		this.memberTypeCode = memberTypeCode;
		this.roleMemberId = roleMemberId;
		this.qualifier = qualifier;
	}
	
	public String getRoleId() {
		return this.roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public AttributeSet getQualifier() {
		return this.qualifier;
	}
	public void setQualifier(AttributeSet qualifier) {
		this.qualifier = qualifier;
	}
	public List<DelegateInfo> getDelegates() {
		return this.delegates;
	}
	public void setDelegates(List<DelegateInfo> delegates) {
		this.delegates = delegates;
	}

	public String getRoleMemberId() {
		return this.roleMemberId;
	}


	public void setRoleMemberId(String roleMemberId) {
		this.roleMemberId = roleMemberId;
	}


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

	public String getEmbeddedRoleId() {
		return this.embeddedRoleId;
	}

	public void setEmbeddedRoleId(String embeddedRoleId) {
		this.embeddedRoleId = embeddedRoleId;
	}

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	return new ToStringBuilder( this )
    	        .append( "memberTypeCode", this.memberTypeCode )
    			.append( "memberId", this.memberId )
    			.append( "roleId", this.roleId )
    			.append( "qualifier", this.qualifier ).toString();
    }

	/**
	 * @return the roleSortingCode
	 */
	public String getRoleSortingCode() {
		return this.roleSortingCode;
	}

	/**
	 * @param roleSortingCode the roleSortingCode to set
	 */
	public void setRoleSortingCode(String roleSortingCode) {
		this.roleSortingCode = roleSortingCode;
	}
}
