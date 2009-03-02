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
package org.kuali.rice.kim.bo.ui;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@MappedSuperclass
public class KimDocumentBoBase  extends PersistableBusinessObjectBase implements Inactivateable {
    @Column(name="FDOC_NBR")
    protected String documentNumber;
	@Type(type="yes_no")
	@Column(name="ACTV_IND")
    protected boolean active = true;
	@Type(type="yes_no")
	@Column(name="EDIT_FLAG")
    protected boolean edit;

	
	@Column(name="ACTV_FRM_DT")
	protected Timestamp activeFromDate;
	@Column(name="ACTV_TO_DT")
	protected Timestamp activeToDate;

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "documentNumber", documentNumber );
		return m;
	}

	public String getDocumentNumber() {
		return this.documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public boolean isActive() {
		long now = System.currentTimeMillis();		
		return (activeFromDate == null || now > activeFromDate.getTime()) && (activeToDate == null || now < activeToDate.getTime());
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isEdit() {
		return this.edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public Timestamp getActiveFromDate() {
		return this.activeFromDate;
	}

	public void setActiveFromDate(Timestamp activeFromDate) {
		this.activeFromDate = activeFromDate;
	}

	public Timestamp getActiveToDate() {
		return this.activeToDate;
	}

	public void setActiveToDate(Timestamp activeToDate) {
		this.activeToDate = activeToDate;
	}


}
