/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@MappedSuperclass
public abstract class KimDelegationMemberImpl extends PersistableBusinessObjectBase {

	@Id
	@Column(name="DELE_MBR_ID")
	protected String delegationMemberId;
	@Column(name="DELE_ID")
	protected String delegationId;
	
	@OneToMany(targetEntity=KimDelegationMemberAttributeDataImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="DELE_MBR_ID", referencedColumnName="TARGET_PRIMARY_KEY", insertable=false, updatable=false )
	protected List<KimDelegationMemberAttributeDataImpl> attributes;
	
	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap lhm = new LinkedHashMap();
		lhm.put( "delegationMemberId", delegationMemberId );
		lhm.put( "delegationId", delegationId );
		return lhm;
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
	
}
