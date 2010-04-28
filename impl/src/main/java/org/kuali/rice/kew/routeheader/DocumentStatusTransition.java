/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.kew.routeheader;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.kuali.rice.kew.bo.KewPersistableBusinessObjectBase;


/**
 * Model bean representing the valid application document statuses for a document type
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KREW_APP_DOC_STAT_TRAN_T")
public class DocumentStatusTransition extends KewPersistableBusinessObjectBase {
	private static final long serialVersionUID = -2212481684546954746L;

	@Id
	@Column(name="APP_DOC_STAT_TRAN_ID")
	private Long statusTransitionId;
	
	@Column(name="DOC_HDR_ID")
	private Long routeHeaderId;
 	
	@Column(name="APP_DOC_STAT_FROM")
	private String oldAppDocStatus;
	
	@Column(name="APP_DOC_STAT_TO")
	private String newAppDocStatus;
	
	@Column(name="STAT_TRANS_DATE")
	private java.sql.Timestamp statusTransitionDate;
	
    public DocumentStatusTransition() {
    }

    public DocumentStatusTransition(Long routeHeaderId, String oldStatus, String newStatus) {
    	this.routeHeaderId = routeHeaderId;
    	this.oldAppDocStatus = oldStatus;
    	this.newAppDocStatus = newStatus;
    	this.statusTransitionDate = new Timestamp(System.currentTimeMillis());
    }
    /**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("routeHeaderId", this.routeHeaderId);
        m.put("oldAppDocStatus", this.oldAppDocStatus);
        m.put("newAppDocStatus", this.newAppDocStatus);
        m.put("statusTransitionDate", this.statusTransitionDate);
		return m;
	}

	public Long getStatusTransitionId() {
		return this.statusTransitionId;
	}

	public void setStatusTransitionId(Long statusTransitionId) {
		this.statusTransitionId = statusTransitionId;
	}

	public Long getRouteHeaderId() {
		return this.routeHeaderId;
	}

	public void setRouteHeaderId(Long headerId) {
		this.routeHeaderId = headerId;
	}

	public String getOldAppDocStatus() {
		return this.oldAppDocStatus;
	}

	public void setOldAppDocStatus(String oldAppDocStatus) {
		this.oldAppDocStatus = oldAppDocStatus;
	}

	public String getNewAppDocStatus() {
		return this.newAppDocStatus;
	}

	public void setNewAppDocStatus(String newAppDocStatus) {
		this.newAppDocStatus = newAppDocStatus;
	}

	public java.sql.Timestamp getStatusTransitionDate() {
		return this.statusTransitionDate;
	}

	public void setStatusTransitionDate(java.sql.Timestamp statusTransitionDate) {
		this.statusTransitionDate = statusTransitionDate;
	}
}
