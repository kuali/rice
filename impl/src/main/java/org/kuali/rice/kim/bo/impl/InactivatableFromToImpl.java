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
package org.kuali.rice.kim.bo.impl;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.kuali.rice.kns.bo.InactivateableFromTo;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@MappedSuperclass
public abstract class InactivatableFromToImpl extends PersistableBusinessObjectBase implements InactivateableFromTo {

	private static final long serialVersionUID = 1L;
	
	@Column(name="ACTV_FRM_DT")
	protected Date activeFromDate;
	@Column(name="ACTV_TO_DT")
	protected Date activeToDate;
	
	/**
	 * Returns active if the current time is between the from and to dates.  Null dates are 
	 * considered to indicate an open range.
	 */
	public boolean isActive() {
		long now = System.currentTimeMillis();		
		return (activeFromDate == null || now > activeFromDate.getTime()) && (activeToDate == null || now < activeToDate.getTime());
	}

	public void setActiveFromDate(Date from) {
		this.activeFromDate = from;
	}

	public void setActiveToDate(Date to) {
		this.activeToDate = to;
	}

	public Date getActiveFromDate() {
		return this.activeFromDate;
	}

	public Date getActiveToDate() {
		return this.activeToDate;
	}

}
