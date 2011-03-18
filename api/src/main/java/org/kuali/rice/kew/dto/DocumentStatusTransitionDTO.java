/*
 * Copyright 2007-2009 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.dto;

import java.io.Serializable;

/**
 * Transport object for DocumentStatusTransition
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentStatusTransitionDTO implements Serializable{
		
	private static final long serialVersionUID = -5694589806200056472L;
	
	private Long statusTransitionId;
	private Long routeHeaderId;
	private String oldAppDocStatus;
	private String newAppDocStatus;
	private java.sql.Timestamp statusTransitionDate;
	
	public Long getStatusTransitionId() {
		return this.statusTransitionId;
	}
	public void setStatusTransitionId(Long statusTransitionId) {
		this.statusTransitionId = statusTransitionId;
	}
	public Long getRouteHeaderId() {
		return this.routeHeaderId;
	}
	public void setRouteHeaderId(Long routeHeaderId) {
		this.routeHeaderId = routeHeaderId;
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
