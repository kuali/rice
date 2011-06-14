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
package org.kuali.rice.krad.dto;

import java.util.Date;

public class InactiveableInfo {
	private static final long serialVersionUID = 1L;

	protected Date activeFromDate;
	protected Date activeToDate;
	protected Date activeAsOfDate;

	/**
	 * Returns active if the {@link #getActiveAsOfDate()} (current time used if not set) is between
	 * the from and to dates. Null dates are considered to indicate an open range.
	 */
	public boolean isActive() {
		long asOfDate = System.currentTimeMillis();
		if (activeAsOfDate != null) {
			asOfDate = activeAsOfDate.getTime();
		}

		return (activeFromDate == null || asOfDate > activeFromDate.getTime())
				&& (activeToDate == null || asOfDate < activeToDate.getTime());
	}

	public void setActive(boolean active) {
		// do nothing
	}

	public void setActiveFromDate(Date from) {
		this.activeFromDate = from;
	}

	public void setActiveToDate(Date to) {
		this.activeToDate = to;
	}

	public void setActiveAsOfDate(Date activeAsOfDate) {
		this.activeAsOfDate = activeAsOfDate;
	}

	public Date getActiveFromDate() {
		return this.activeFromDate;
	}

	public Date getActiveToDate() {
		return this.activeToDate;
	}
	
	public Date getActiveAsOfDate() {
		return this.activeAsOfDate;
	}

}
