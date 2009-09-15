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
package org.kuali.rice.kim.bo.role.dto;

import java.sql.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.jaxb.SqlDateAdapter;
import org.kuali.rice.kns.bo.InactivateableFromTo;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class InactiveableInfo implements InactivateableFromTo {

	private static final long serialVersionUID = 1L;
	
	protected Date activeFromDate;
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

	@XmlJavaTypeAdapter(SqlDateAdapter.class)
	public Date getActiveFromDate() {
		return this.activeFromDate;
	}

	@XmlJavaTypeAdapter(SqlDateAdapter.class)
	public Date getActiveToDate() {
		return this.activeToDate;
	}

}
