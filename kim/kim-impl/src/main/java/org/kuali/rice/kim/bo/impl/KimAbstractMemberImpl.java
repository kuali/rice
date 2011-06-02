/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.impl;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.kuali.rice.kns.bo.InactivatableFromToImpl;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@MappedSuperclass
public abstract class KimAbstractMemberImpl extends InactivatableFromToImpl {

	@Column(name="MBR_ID")
	protected String memberId;
	
	@Column(name="MBR_TYP_CD")
	protected String memberTypeCode;

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
	
	
}
