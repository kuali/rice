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

import java.sql.Timestamp;

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
	
	@Column(name="ACTV_FRM_IND")
	protected Timestamp activeFromDate;
	@Column(name="ACTV_TO_IND")
	protected Timestamp activeToDate;
	
	/**
	 * @return the active
	 */
	public boolean isActive() {
		// TODO: FIXME - once we start using from/to dates uncomment the logic. Until then return true for always active.
		//long now = System.currentTimeMillis();
		//return now > activeFromDate.getTime() && now < activeToDate.getTime();
		return true;
	}

	public void setActiveFromDate(Timestamp from) {
		this.activeFromDate = from;
	}

	public void setActiveToDate(Timestamp to) {
		this.activeToDate = to;
	}

}
