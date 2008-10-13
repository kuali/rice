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
package org.kuali.rice.kim.bo.group.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.group.GroupPrincipal;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KR_KIM_GROUP_PRINCIPAL_T")
public class GroupPrincipalImpl extends GroupMemberBase implements GroupPrincipal {

	@Column(name="PRNCPL_ID")
	protected String memberPrincipalId;

	public String getMemberPrincipalId() {
		return this.memberPrincipalId;
	}

	public void setMemberPrincipalId(String memberPrincipalId) {
		this.memberPrincipalId = memberPrincipalId;
	}
	
}
