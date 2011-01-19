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
package org.kuali.rice.kim.bo.group.impl;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.kim.bo.impl.KimAbstractMemberImpl;

import javax.persistence.*;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name="KRIM_GRP_MBR_T")
public class GroupMemberImpl extends KimAbstractMemberImpl {

	@Id
	@GeneratedValue(generator="KRIM_GRP_MBR_ID_S")
	@GenericGenerator(name="KRIM_GRP_MBR_ID_S",strategy="org.kuali.rice.core.jpa.spring.RiceNumericStringSequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KRIM_GRP_MBR_ID_S"),
			@Parameter(name="value_column",value="id")
		})	
	@Column(name="GRP_MBR_ID")
	protected String groupMemberId;
		
	@Column(name="GRP_ID")
	protected String groupId;
	

	public String getGroupMemberId() {
		return this.groupMemberId;
	}

	public void setGroupMemberId(String groupMemberId) {
		this.groupMemberId = groupMemberId;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
