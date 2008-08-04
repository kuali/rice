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
package org.kuali.core.bo.user;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.core.bo.PersistableBusinessObjectBase;

@Entity
@Table(name="EN_USR_T")
public class RiceUser extends PersistableBusinessObjectBase {

	private static final long serialVersionUID = 2912023481237314564L;

	@Id
	@Column(name="PRSN_EN_ID")
	private String personUniversalIdentifier;
	@Column(name="PRSN_UNIV_ID")
	private String universityId;
	@Column(name="PRSN_NTWRK_ID")
	private String personUserIdentifier;
	@Column(name="PRSN_EMAIL_ADDR")
	private String emailAddress;
	@Column(name="PRSN_NM")
	private String personName;
	
    @Column(name="DTYPE", insertable=false, updatable=false, nullable=true)
    private String dtype;

	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap map = new LinkedHashMap();
		map.put("RiceUser", "RiceUser");
		return map;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String name) {
		this.personName = name;
	}

	public String getPersonUserIdentifier() {
		return personUserIdentifier;
	}

	public void setPersonUserIdentifier(String networkId) {
		this.personUserIdentifier = networkId;
	}

	public String getUniversityId() {
		return universityId;
	}

	public void setUniversityId(String universityId) {
		this.universityId = universityId;
	}

	public String getPersonUniversalIdentifier() {
		return personUniversalIdentifier;
	}

	public void setPersonUniversalIdentifier(String userId) {
		this.personUniversalIdentifier = userId;
	}

}

