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

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.role.KimDelegationPrincipal;
import org.kuali.rice.kns.bo.InactivateableFromTo;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KRIM_DLGN_PRNCPL_T")
public class KimDelegationPrincipalImpl extends KimDelegationMemberImpl implements
		KimDelegationPrincipal, InactivateableFromTo {

	@Column(name="PRNCPL_ID")
	protected String principalId;
	@Column(name="ACTV_FRM_IND")
	protected Timestamp activeFromDate;
	@Column(name="ACTV_TO_IND")
	protected Timestamp activeToDate;
	
	/**
	 * @return the active
	 */
	public boolean isActive() {
		long now = System.currentTimeMillis();
		return now > activeFromDate.getTime() && now < activeToDate.getTime();
	}

	public void setActiveFromDate(Timestamp from) {
		this.activeFromDate = from;
	}

	public void setActiveToDate(Timestamp to) {
		this.activeToDate = to;
	}
	
	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap lhm = new LinkedHashMap();
		lhm.put( "principalId", principalId );
		return lhm;
	}

	public String getPrincipalId() {
		return this.principalId;
	}

	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}

}
